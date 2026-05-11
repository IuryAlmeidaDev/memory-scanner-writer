package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AgendadorPedidoAlimentacaoDinamicoServiceTest extends BaseUnitTest {

    private final AgendadorPedidoAlimentacaoDinamicoService service = new AgendadorPedidoAlimentacaoDinamicoService();

    @Mock
    private Timer timer;

    @Mock
    private TimerService timerService;

    @Mock
    private UnidadePrisionalService unidadePrisionalService;

    @Mock
    private ProcessadorPedidoAlimentacaoAsync processadorPedidoAlimentacaoAsync;

    @Mock
    private TipoRefeicaoService tipoRefeicaoService;

    @Mock
    private RolloutAlimentacaoService rolloutAlimentacaoService;

    @Test
    public void deveConfigurarTimezoneDeCuiabaNoScheduleExpression() {
        ScheduleExpression expression = service.criarScheduleExpression(LocalDateTime.of(2026, 4, 24, 16, 59, 0));

        assertThat(expression.getTimezone()).isEqualTo("America/Cuiaba");
    }

    @Test
    public void deveConsiderarDisparoNoFuturoApenasQuandoForDepoisDaReferencia() {
        LocalDateTime referencia = LocalDateTime.of(2026, 4, 24, 16, 59, 0);

        assertThat(service.isDisparoFuturo(referencia.plusSeconds(1), referencia)).isTrue();
        assertThat(service.isDisparoFuturo(referencia, referencia)).isFalse();
        assertThat(service.isDisparoFuturo(referencia.minusSeconds(1), referencia)).isFalse();
    }

    @Test
    public void aoDisparar_devePermitirExecucaoParalelaComLockRead() throws Exception {
        Lock lock = AgendadorPedidoAlimentacaoDinamicoService.class
                .getMethod("aoDisparar", Timer.class)
                .getAnnotation(Lock.class);

        assertThat(lock).isNotNull();
        assertThat(lock.value()).isEqualTo(LockType.READ);
    }

    @Test
    public void rearmarAgendaCompleta_deveSerializarAlteracaoDeTimersComLockWrite() throws Exception {
        Lock lock = AgendadorPedidoAlimentacaoDinamicoService.class
                .getMethod("rearmarAgendaCompleta")
                .getAnnotation(Lock.class);

        assertThat(lock).isNotNull();
        assertThat(lock.value()).isEqualTo(LockType.WRITE);
    }

    @Test
    public void rearmarUnidade_deveSerializarAlteracaoDeTimersComLockWrite() throws Exception {
        Lock lock = AgendadorPedidoAlimentacaoDinamicoService.class
                .getMethod("rearmarUnidade", Long.class)
                .getAnnotation(Lock.class);

        assertThat(lock).isNotNull();
        assertThat(lock.value()).isEqualTo(LockType.WRITE);
    }

    @Test
    public void aoDisparar_deveIgnorarTimerComInfoInvalido() throws Exception {
        setField("processadorPedidoAlimentacaoAsync", processadorPedidoAlimentacaoAsync);
        when(timer.getInfo()).thenReturn("info invalida");

        service.aoDisparar(timer);

        verify(processadorPedidoAlimentacaoAsync, never()).processarUnidade(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class));
    }

    @Test
    public void aoDisparar_deveDelegarProcessamentoParaAsync() throws Exception {
        setField("processadorPedidoAlimentacaoAsync", processadorPedidoAlimentacaoAsync);
        setField("tipoRefeicaoService", tipoRefeicaoService);
        setField("rolloutAlimentacaoService", rolloutAlimentacaoService);
        when(timer.getInfo()).thenReturn(timerInfo(10L));
        when(rolloutAlimentacaoService.unidadeHabilitada(10L)).thenReturn(true);
        when(tipoRefeicaoService.listarTodos()).thenReturn(Collections.emptyList());

        service.aoDisparar(timer);

        ArgumentCaptor<LocalDateTime> referenciaCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(processadorPedidoAlimentacaoAsync).processarUnidade(
                org.mockito.ArgumentMatchers.eq(10L),
                referenciaCaptor.capture());
        assertThat(referenciaCaptor.getValue()).isNotNull();
    }

    @Test
    public void aoDisparar_deveIgnorarUnidadeComRolloutDesabilitado() throws Exception {
        setField("processadorPedidoAlimentacaoAsync", processadorPedidoAlimentacaoAsync);
        setField("timerService", timerService);
        setField("rolloutAlimentacaoService", rolloutAlimentacaoService);
        when(timer.getInfo()).thenReturn(timerInfo(10L));
        when(rolloutAlimentacaoService.unidadeHabilitada(10L)).thenReturn(false);

        service.aoDisparar(timer);

        verify(processadorPedidoAlimentacaoAsync, never()).processarUnidade(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class));
        verify(timerService, never()).createCalendarTimer(any(ScheduleExpression.class), any(javax.ejb.TimerConfig.class));
    }

    @Test
    public void rearmarUnidade_naoDeveAgendarTimerQuandoCatchUpFalharSincronamente() throws Exception {
        setField("timerService", timerService);
        setField("rolloutAlimentacaoService", rolloutAlimentacaoService);
        setField("processadorPedidoAlimentacaoAsync", processadorPedidoAlimentacaoAsync);
        when(timerService.getTimers()).thenReturn(Collections.emptyList());
        when(rolloutAlimentacaoService.unidadeHabilitada(10L)).thenReturn(true);
        doThrow(new RuntimeException("falha async")).when(processadorPedidoAlimentacaoAsync)
                .processarCatchUpUnidade(
                        org.mockito.ArgumentMatchers.eq(10L),
                        org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                        org.mockito.ArgumentMatchers.eq(2));

        service.rearmarUnidade(10L);

        verify(timerService, never()).createCalendarTimer(any(ScheduleExpression.class), any(javax.ejb.TimerConfig.class));
    }

    @Test
    public void rearmarUnidade_deveCancelarTimersQuandoRolloutEstiverDesabilitado() throws Exception {
        setField("timerService", timerService);
        setField("rolloutAlimentacaoService", rolloutAlimentacaoService);
        when(timerService.getTimers()).thenReturn(Arrays.asList(timer));
        when(timer.getInfo()).thenReturn(timerInfo(10L));
        when(rolloutAlimentacaoService.unidadeHabilitada(10L)).thenReturn(false);

        service.rearmarUnidade(10L);

        verify(timer).cancel();
    }

    @Test
    public void rearmarAgendaCompleta_deveContinuarQuandoUmaUnidadeFalhar() throws Exception {
        setField("unidadePrisionalService", unidadePrisionalService);
        setField("rolloutAlimentacaoService", rolloutAlimentacaoService);
        setField("processadorPedidoAlimentacaoAsync", processadorPedidoAlimentacaoAsync);
        setField("tipoRefeicaoService", tipoRefeicaoService);
        setField("timerService", timerService);
        UnidadePrisional unidadeComFalha = unidade(10L);
        UnidadePrisional unidadeValida = unidade(20L);
        when(unidadePrisionalService.listarUnidadesComContratosAtivo())
                .thenReturn(Arrays.asList(unidadeComFalha, unidadeValida));
        when(rolloutAlimentacaoService.unidadeHabilitada(10L))
                .thenThrow(new RuntimeException("falha rollout"));
        when(rolloutAlimentacaoService.unidadeHabilitada(20L)).thenReturn(true);
        when(timerService.getTimers()).thenReturn(Collections.emptyList());
        when(tipoRefeicaoService.listarTodos()).thenReturn(Collections.emptyList());

        service.rearmarAgendaCompleta();

        verify(processadorPedidoAlimentacaoAsync).processarCatchUpUnidade(
                org.mockito.ArgumentMatchers.eq(20L),
                org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                org.mockito.ArgumentMatchers.eq(2));
    }

    private Serializable timerInfo(Long unidadeId) throws Exception {
        Class<?> timerInfoClass = Class.forName(
                "br.gov.mt.sejudh.sigepen.service.AgendadorPedidoAlimentacaoDinamicoService$TimerInfo");
        Constructor<?> constructor = timerInfoClass.getDeclaredConstructor(Long.class);
        constructor.setAccessible(true);
        return (Serializable) constructor.newInstance(unidadeId);
    }

    private UnidadePrisional unidade(Long id) {
        UnidadePrisional unidade = new UnidadePrisional();
        unidade.setId(id);
        return unidade;
    }

    private void setField(String nomeCampo, Object valor) throws Exception {
        Field field = AgendadorPedidoAlimentacaoDinamicoService.class.getDeclaredField(nomeCampo);
        field.setAccessible(true);
        field.set(service, valor);
    }
}
