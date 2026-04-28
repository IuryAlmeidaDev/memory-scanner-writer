package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;

import javax.annotation.PostConstruct;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.annotation.Resource;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Singleton
@Startup
public class AgendadorPedidoAlimentacaoDinamicoService {

    private static final Logger LOGGER = Logger.getLogger(AgendadorPedidoAlimentacaoDinamicoService.class.getName());
    private static final DateTimeFormatter HORARIO_REFEICAO_FORMATTER = DateTimeFormatter.ofPattern("HHmm");
    private static final ZoneId APP_ZONE = ZoneId.of("America/Cuiaba");

    private static class TimerInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Long unidadeId;

        private TimerInfo(Long unidadeId) {
            this.unidadeId = unidadeId;
        }
    }

    private static class TriggerPlanejado {
        private final Long unidadeId;
        private final LocalDateTime dataHoraDisparo;

        private TriggerPlanejado(Long unidadeId, LocalDateTime dataHoraDisparo) {
            this.unidadeId = unidadeId;
            this.dataHoraDisparo = dataHoraDisparo;
        }
    }

    @Resource
    private TimerService timerService;

    @Inject
    private UnidadePrisionalService unidadePrisionalService;

    @Inject
    private PedidoAlimentoService pedidoAlimentoService;

    @Inject
    private ContratoService contratoService;

    @Inject
    private TipoRefeicaoService tipoRefeicaoService;

    @Inject
    private RolloutAlimentacaoService rolloutAlimentacaoService;

    @PostConstruct
    public void inicializar() {
        rearmarAgendaCompleta();
    }

    public void rearmarAgendaCompleta() {
        List<UnidadePrisional> unidades = unidadePrisionalService.listarUnidadesComContratosAtivo();
        LocalDateTime referencia = LocalDateTime.now(APP_ZONE);

        for (UnidadePrisional unidade : unidades) {
            if (!rolloutAlimentacaoService.unidadeHabilitada(unidade.getId())) {
                continue;
            }

            cancelarTimersDaUnidade(unidade.getId());
            pedidoAlimentoService.gerarPedidosAutomaticosDaUnidade(unidade.getId(), referencia);
            agendarProximoDisparo(unidade.getId(), referencia);
        }
    }

    public void rearmarUnidade(Long unidadeId) {
        if (unidadeId == null || !rolloutAlimentacaoService.unidadeHabilitada(unidadeId)) {
            return;
        }

        cancelarTimersDaUnidade(unidadeId);
        LocalDateTime referencia = LocalDateTime.now(APP_ZONE);
        pedidoAlimentoService.gerarPedidosAutomaticosDaUnidade(unidadeId, referencia);
        agendarProximoDisparo(unidadeId, referencia);
    }

    @Timeout
    public void aoDisparar(Timer timer) {
        Object info = timer.getInfo();
        if (!(info instanceof TimerInfo)) {
            return;
        }

        TimerInfo timerInfo = (TimerInfo) info;
        LocalDateTime referencia = LocalDateTime.now(APP_ZONE);

        try {
            pedidoAlimentoService.gerarPedidosAutomaticosDaUnidade(timerInfo.unidadeId, referencia);
        } catch (Exception e) {
            LOGGER.warning("Erro ao processar disparo dinamico da unidade " + timerInfo.unidadeId + ": " + e.getMessage());
        }

        agendarProximoDisparo(timerInfo.unidadeId, referencia.plusSeconds(1));
    }

    private void cancelarTimersDaUnidade(Long unidadeId) {
        for (Timer timer : timerService.getTimers()) {
            Object info = timer.getInfo();
            if (info instanceof TimerInfo) {
                TimerInfo timerInfo = (TimerInfo) info;
                if (Objects.equals(timerInfo.unidadeId, unidadeId)) {
                    timer.cancel();
                }
            }
        }
    }

    private void agendarProximoDisparo(Long unidadeId, LocalDateTime referencia) {
        Optional<TriggerPlanejado> trigger = calcularProximoDisparo(unidadeId, referencia);
        if (!trigger.isPresent()) {
            LOGGER.info("Sem disparo futuro para unidade " + unidadeId + " na referencia " + referencia);
            return;
        }

        TriggerPlanejado proximo = trigger.get();
        LocalDateTime agora = LocalDateTime.now(APP_ZONE);
        if (!isDisparoFuturo(proximo.dataHoraDisparo, agora)) {
            LOGGER.warning("Disparo calculado fora do futuro. unidade=" + unidadeId
                    + ", referencia=" + referencia
                    + ", agora=" + agora
                    + ", disparo=" + proximo.dataHoraDisparo
                    + ", timezone=" + APP_ZONE.getId()
                    + ". Sera recalculado.");

            Optional<TriggerPlanejado> recalculado = calcularProximoDisparo(unidadeId, agora.plusSeconds(1));
            if (!recalculado.isPresent()) {
                LOGGER.info("Sem disparo futuro para unidade " + unidadeId + " apos recalc na referencia " + agora);
                return;
            }
            proximo = recalculado.get();
        }

        if (!isDisparoFuturo(proximo.dataHoraDisparo, LocalDateTime.now(APP_ZONE))) {
            LOGGER.warning("Disparo da unidade " + unidadeId + " descartado por nao estar no futuro. disparo="
                    + proximo.dataHoraDisparo + ", timezone=" + APP_ZONE.getId());
            return;
        }

        ScheduleExpression expression = criarScheduleExpression(proximo.dataHoraDisparo);

        TimerConfig timerConfig = new TimerConfig(new TimerInfo(proximo.unidadeId), false);
        timerService.createCalendarTimer(expression, timerConfig);

        LOGGER.info("Disparo dinamico agendado. unidade=" + unidadeId
                + ", horario=" + proximo.dataHoraDisparo
                + ", timezone=" + APP_ZONE.getId());
    }

    ScheduleExpression criarScheduleExpression(LocalDateTime dataHoraDisparo) {
        return new ScheduleExpression()
                .timezone(APP_ZONE.getId())
                .year(String.valueOf(dataHoraDisparo.getYear()))
                .month(String.valueOf(dataHoraDisparo.getMonthValue()))
                .dayOfMonth(String.valueOf(dataHoraDisparo.getDayOfMonth()))
                .hour(String.valueOf(dataHoraDisparo.getHour()))
                .minute(String.valueOf(dataHoraDisparo.getMinute()))
                .second(String.valueOf(dataHoraDisparo.getSecond()));
    }

    boolean isDisparoFuturo(LocalDateTime disparo, LocalDateTime referencia) {
        return disparo != null && referencia != null && disparo.isAfter(referencia);
    }

    private Optional<TriggerPlanejado> calcularProximoDisparo(Long unidadeId, LocalDateTime referencia) {
        List<HorarioRefeicao> horarios = buscarHorariosVigentesOrdenados(unidadeId, referencia);
        if (horarios.isEmpty()) {
            return Optional.empty();
        }

        LocalDate hoje = referencia.toLocalDate();
        LocalDate amanha = hoje.plusDays(1);

        LocalDateTime proximo = null;
        for (HorarioRefeicao horarioAtual : horarios) {
            LocalTime horaAtual = LocalTime.parse(horarioAtual.getHorarioInicio(), HORARIO_REFEICAO_FORMATTER);

            LocalDateTime candidatoHoje = LocalDateTime.of(hoje, horaAtual);
            if (candidatoHoje.isAfter(referencia) && (proximo == null || candidatoHoje.isBefore(proximo))) {
                proximo = candidatoHoje;
            }

            LocalDateTime candidatoAmanha = LocalDateTime.of(amanha, horaAtual);
            if (proximo == null || candidatoAmanha.isBefore(proximo)) {
                proximo = candidatoAmanha;
            }
        }

        return proximo == null ? Optional.empty() : Optional.of(new TriggerPlanejado(unidadeId, proximo));
    }

    private List<HorarioRefeicao> buscarHorariosVigentesOrdenados(Long unidadeId, LocalDateTime referencia) {
        List<HorarioRefeicao> horarios = new ArrayList<>();
        Timestamp dataReferencia = Timestamp.valueOf(referencia);

        for (TipoRefeicao tipoRefeicao : tipoRefeicaoService.listarTodos()) {
            HorarioRefeicao horario = contratoService.buscarHorarioEntregaVigentePorUnidade(
                    unidadeId,
                    tipoRefeicao.getId(),
                    dataReferencia);
            if (horario != null && horario.getHorarioInicio() != null) {
                horarios.add(horario);
            }
        }

        horarios.sort(Comparator.comparing(h -> LocalTime.parse(h.getHorarioInicio(), HORARIO_REFEICAO_FORMATTER)));
        return horarios;
    }
}
