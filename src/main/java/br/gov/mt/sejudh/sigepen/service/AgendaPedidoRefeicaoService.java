package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.AgendaPedidoRefeicaoDTO;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Stateless
public class AgendaPedidoRefeicaoService {

    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HHmm");
    private static final DateTimeFormatter DATA_HORA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Inject
    private ContratoService contratoService;

    @Inject
    private TipoRefeicaoService tipoRefeicaoService;

    public HorarioRefeicao resolverHorarioVigente(HorarioRefeicao horarioUnidade, HorarioRefeicao horarioContrato) {
        if (horarioUnidade != null) {
            return horarioUnidade;
        }
        return horarioContrato;
    }

    public LocalDateTime calcularCorte(LocalDate dataBase, HorarioRefeicao proximaRefeicao) {
        LocalTime horario = LocalTime.parse(proximaRefeicao.getHorarioInicio(), HORA_FORMATTER);
        return LocalDateTime.of(dataBase, horario).minusHours(2);
    }

    public boolean unidadeAptaParaAutomacao(Long unidadeId, LocalDateTime referencia) {
        Timestamp dataReferencia = Timestamp.valueOf(referencia);
        List<TipoRefeicao> tipos = tipoRefeicaoService.listarTodos();
        for (TipoRefeicao tipo : tipos) {
            HorarioRefeicao horario = contratoService.buscarHorarioEntregaVigentePorUnidade(unidadeId, tipo.getId(), dataReferencia);
            if (horario == null) {
                return false;
            }
        }
        return true;
    }

    public List<AgendaPedidoRefeicaoDTO> montarAgendaOperacional(List<UnidadePrisional> unidades, LocalDateTime referencia) {
        List<AgendaPedidoRefeicaoDTO> agenda = new ArrayList<>();
        for (UnidadePrisional unidade : unidades) {
            agenda.add(montarLinha(unidade, referencia));
        }
        return agenda;
    }

    private AgendaPedidoRefeicaoDTO montarLinha(UnidadePrisional unidade, LocalDateTime referencia) {
        AgendaPedidoRefeicaoDTO dto = new AgendaPedidoRefeicaoDTO();
        dto.setUnidadeId(unidade.getId());
        dto.setNomeUnidade(unidade.getNome());
        dto.setUltimaAvaliacao(referencia.format(DATA_HORA_FORMATTER));
        dto.setTempoRestanteCorte("--");

        Timestamp dataReferencia = Timestamp.valueOf(referencia);
        Contrato contrato;
        try {
            contrato = contratoService.buscarContratoVigenteEAtivoPorUnidade(unidade.getId(), dataReferencia);
            dto.setContrato(contrato.getNumContrato());
        } catch (Exception e) {
            dto.setStatus("BLOQUEADA_AUTOMACAO");
            dto.setMotivo("Sem contrato vigente para a unidade");
            return dto;
        }

        List<HorarioRefeicao> horarios = new ArrayList<>();
        for (TipoRefeicao tipo : tipoRefeicaoService.listarTodos()) {
            HorarioRefeicao horario = contratoService.buscarHorarioEntregaVigentePorUnidade(unidade.getId(), tipo.getId(), dataReferencia);
            if (horario == null || horario.getHorarioInicio() == null) {
                dto.setStatus("BLOQUEADA_AUTOMACAO");
                dto.setMotivo("Sem horario vigente para todas as refeicoes");
                return dto;
            }
            horarios.add(horario);
        }

        horarios.sort(Comparator.comparing(h -> LocalTime.parse(h.getHorarioInicio(), HORA_FORMATTER)));

        int idxAtual = -1;
        LocalTime agora = referencia.toLocalTime();
        for (int i = 0; i < horarios.size(); i++) {
            LocalTime inicio = LocalTime.parse(horarios.get(i).getHorarioInicio(), HORA_FORMATTER);
            if (!inicio.isAfter(agora)) {
                idxAtual = i;
            }
        }

        LocalDate dataAtual = referencia.toLocalDate();
        LocalDate dataProxima = referencia.toLocalDate();
        if (idxAtual < 0) {
            idxAtual = horarios.size() - 1;
            dataAtual = dataAtual.minusDays(1);
        }

        int idxProxima = (idxAtual + 1) % horarios.size();
        if (idxProxima == 0) {
            dataProxima = dataProxima.plusDays(1);
        }

        HorarioRefeicao atual = horarios.get(idxAtual);
        HorarioRefeicao proxima = horarios.get(idxProxima);

        LocalDateTime disparo = LocalDateTime.of(dataAtual, LocalTime.parse(atual.getHorarioInicio(), HORA_FORMATTER));
        LocalDateTime corte = calcularCorte(dataProxima, proxima);

        dto.setRefeicaoAtual(atual.getTipoRefeicao().getDescricao());
        dto.setProximaRefeicao(proxima.getTipoRefeicao().getDescricao());
        dto.setDisparo(disparo.format(DATA_HORA_FORMATTER));
        dto.setCorte(corte.format(DATA_HORA_FORMATTER));
        dto.setTempoRestanteCorte(calcularTempoRestanteAteCorte(corte, referencia));
        dto.setStatus("APTA");
        dto.setMotivo("Horario vigente encontrado para todas as refeicoes");
        return dto;
    }

    String calcularTempoRestanteAteCorte(LocalDateTime corte, LocalDateTime referencia) {
        if (corte == null || referencia == null) {
            return "--";
        }

        long minutosRestantes = ChronoUnit.MINUTES.between(referencia, corte);
        if (minutosRestantes <= 0) {
            return "Encerrado";
        }

        long horas = minutosRestantes / 60;
        long minutos = minutosRestantes % 60;
        return horas + "h " + minutos + "m";
    }
}
