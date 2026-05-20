package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.CardapioDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoAlimentoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.OrigemPedidoAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import org.junit.Test;
import org.mockito.Mock;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.PersistenceException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PedidoAlimentoServiceTest extends BaseUnitTest {

    private final PedidoAlimentoService service = new PedidoAlimentoService();

    @Mock
    private PedidoAlimentoDAO dao;

    @Mock
    private CardapioDAO cardapioDAO;

    @Mock
    private UnidadePrisionalService unidadePrisionalService;

    @Mock
    private AgendaPedidoRefeicaoService agendaPedidoRefeicaoService;

    @Mock
    private RolloutAlimentacaoService rolloutAlimentacaoService;

    @Mock
    private ContratoService contratoService;

    @Mock
    private TipoRefeicaoService tipoRefeicaoService;

    @Mock
    private EstruturaService estruturaService;

    @Test
    public void devePermitirQuandoReferenciaForNoInicioDaRefeicaoAnterior() throws Exception {
        HorarioRefeicao cafe = horario("0700", "Cafe");
        HorarioRefeicao almoco = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                cafe,
                LocalDate.of(2026, 4, 24),
                almoco,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 7, 0));
    }

    @Test
    public void devePermitirQuandoJanelaForCircularNoInicioDaRefeicaoAnterior() throws Exception {
        HorarioRefeicao refeicaoAnterior = horario("1200", "Cafe");
        HorarioRefeicao refeicaoAlvo = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                refeicaoAnterior,
                LocalDate.of(2026, 4, 24),
                refeicaoAlvo,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 12, 0));
    }

    @Test
    public void devePermitirQuandoJanelaForCircularUmMinutoAntesDoCorte() throws Exception {
        HorarioRefeicao refeicaoAnterior = horario("1200", "Cafe");
        HorarioRefeicao refeicaoAlvo = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                refeicaoAnterior,
                LocalDate.of(2026, 4, 24),
                refeicaoAlvo,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 9, 59));
    }

    @Test(expected = ValidacaoException.class)
    public void deveBloquearQuandoReferenciaForNoHorarioDeCorte() throws Exception {
        HorarioRefeicao cafe = horario("0700", "Cafe");
        HorarioRefeicao almoco = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                cafe,
                LocalDate.of(2026, 4, 24),
                almoco,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 10, 0));
    }

    @Test(expected = ValidacaoException.class)
    public void deveBloquearQuandoJanelaForCircularNoHorarioDeCorte() throws Exception {
        HorarioRefeicao refeicaoAnterior = horario("1200", "Cafe");
        HorarioRefeicao refeicaoAlvo = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                refeicaoAnterior,
                LocalDate.of(2026, 4, 24),
                refeicaoAlvo,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 10, 0));
    }

    @Test(expected = ValidacaoException.class)
    public void deveBloquearQuandoReferenciaForAntesDaRefeicaoAnterior() throws Exception {
        HorarioRefeicao cafe = horario("0700", "Cafe");
        HorarioRefeicao almoco = horario("1200", "Almoco");

        service.validarGeracaoDentroDaJanelaOperacional(
                cafe,
                LocalDate.of(2026, 4, 24),
                almoco,
                LocalDate.of(2026, 4, 24),
                2,
                LocalDateTime.of(2026, 4, 24, 6, 59));
    }

    @Test
    public void isViolacaoUnicidade_deveReconhecerOra00001NaCadeiaDaExcecao() {
        PersistenceException exception = new PersistenceException(
                new RuntimeException("ORA-00001: restricao exclusiva UK_SGP_PED_ALIM_ATIVO violada"));

        assertThat(service.isViolacaoUnicidade(exception)).isTrue();
    }

    @Test
    public void isViolacaoUnicidade_deveIgnorarConstraintNaoRelacionadaAoPedidoAtivo() {
        PersistenceException exception = new PersistenceException(
                new RuntimeException("ORA-00001: restricao exclusiva UK_OUTRA_TABELA violada"));

        assertThat(service.isViolacaoUnicidade(exception)).isFalse();
    }

    @Test
    public void isViolacaoUnicidade_deveIgnorarErroSemSinalDeConstraint() {
        RuntimeException exception = new RuntimeException("erro de regra");

        assertThat(service.isViolacaoUnicidade(exception)).isFalse();
    }

    @Test
    public void gerarPedidosAutomaticosDaUnidadeComCatchUp_deveRetornarZeroQuandoEntradaInvalida() {
        assertThat(service.gerarPedidosAutomaticosDaUnidadeComCatchUp(null, LocalDateTime.now(), 2)).isZero();
        assertThat(service.gerarPedidosAutomaticosDaUnidadeComCatchUp(10L, null, 2)).isZero();
    }

    @Test
    public void gerarPedidosAutomaticosDaUnidadeComCatchUp_deveTratarDiasNegativosComoZero() throws Exception {
        setField(service, "unidadePrisionalService", unidadePrisionalService);
        setField(service, "rolloutAlimentacaoService", rolloutAlimentacaoService);
        setField(service, "agendaPedidoRefeicaoService", agendaPedidoRefeicaoService);
        setField(service, "contratoService", contratoService);
        setField(service, "tipoRefeicaoService", tipoRefeicaoService);
        UnidadePrisional unidade = unidade(10L);
        LocalDateTime referencia = LocalDateTime.of(2026, 5, 8, 13, 0);

        when(unidadePrisionalService.buscaPorId(10L)).thenReturn(unidade);
        when(rolloutAlimentacaoService.unidadeHabilitada(10L)).thenReturn(true);
        when(agendaPedidoRefeicaoService.unidadeAptaParaAutomacao(10L, referencia)).thenReturn(true);
        when(contratoService.buscarContratoVigenteEAtivoPorUnidade(eq(10L), any(Timestamp.class)))
                .thenThrow(new RuntimeException("sem contrato"));

        int resultado = service.gerarPedidosAutomaticosDaUnidadeComCatchUp(10L, referencia, -5);

        assertThat(resultado).isZero();
        verify(contratoService).buscarContratoVigenteEAtivoPorUnidade(
                eq(10L),
                eq(Timestamp.valueOf(referencia.toLocalDate().atStartOfDay())));
    }

    @Test
    public void gerarPedidosAutomaticosDaUnidadeComCatchUp_deveRetornarZeroQuandoRolloutDesabilitado() throws Exception {
        setField(service, "unidadePrisionalService", unidadePrisionalService);
        setField(service, "rolloutAlimentacaoService", rolloutAlimentacaoService);
        UnidadePrisional unidade = unidade(10L);
        LocalDateTime referencia = LocalDateTime.of(2026, 5, 8, 13, 0);

        when(unidadePrisionalService.buscaPorId(10L)).thenReturn(unidade);
        when(rolloutAlimentacaoService.unidadeHabilitada(10L)).thenReturn(false);

        int resultado = service.gerarPedidosAutomaticosDaUnidadeComCatchUp(10L, referencia, 2);

        assertThat(resultado).isZero();
        verify(agendaPedidoRefeicaoService, never()).unidadeAptaParaAutomacao(anyLong(), any(LocalDateTime.class));
    }

    @Test
    public void gerarPedidosAutomaticosDaUnidadeComCatchUp_deveRetornarZeroQuandoUnidadeNaoEstaApta() throws Exception {
        setField(service, "unidadePrisionalService", unidadePrisionalService);
        setField(service, "rolloutAlimentacaoService", rolloutAlimentacaoService);
        setField(service, "agendaPedidoRefeicaoService", agendaPedidoRefeicaoService);
        UnidadePrisional unidade = unidade(10L);
        LocalDateTime referencia = LocalDateTime.of(2026, 5, 8, 13, 0);

        when(unidadePrisionalService.buscaPorId(10L)).thenReturn(unidade);
        when(rolloutAlimentacaoService.unidadeHabilitada(10L)).thenReturn(true);
        when(agendaPedidoRefeicaoService.unidadeAptaParaAutomacao(10L, referencia)).thenReturn(false);

        int resultado = service.gerarPedidosAutomaticosDaUnidadeComCatchUp(10L, referencia, 2);

        assertThat(resultado).isZero();
    }

    @Test
    public void gerarPedidosAutomaticosDaUnidade_deveExecutarEmNovaTransacao() throws Exception {
        TransactionAttribute transactionAttribute = PedidoAlimentoService.class
                .getMethod("gerarPedidosAutomaticosDaUnidade", Long.class, LocalDateTime.class)
                .getAnnotation(TransactionAttribute.class);

        assertThat(transactionAttribute).isNotNull();
        assertThat(transactionAttribute.value()).isEqualTo(TransactionAttributeType.REQUIRES_NEW);
    }

    @Test
    public void gerarPedidosAutomaticosDaUnidadeComCatchUp_deveDelegarReferenciasParaProxyTransacional()
            throws Exception {
        PedidoAlimentoService serviceSpy = spy(service);
        setField(serviceSpy, "unidadePrisionalService", unidadePrisionalService);
        setField(serviceSpy, "rolloutAlimentacaoService", rolloutAlimentacaoService);
        setField(serviceSpy, "agendaPedidoRefeicaoService", agendaPedidoRefeicaoService);
        setField(serviceSpy, "contratoService", contratoService);
        setField(serviceSpy, "tipoRefeicaoService", tipoRefeicaoService);
        PedidoAlimentoService executorTransacional = org.mockito.Mockito.mock(PedidoAlimentoService.class);
        UnidadePrisional unidade = unidade(10L);
        LocalDateTime referencia = LocalDateTime.of(2026, 5, 8, 13, 0);
        TipoRefeicao cafe = tipoRefeicao(1L, "Cafe");
        Contrato contrato = contrato(20L);

        when(unidadePrisionalService.buscaPorId(10L)).thenReturn(unidade);
        when(rolloutAlimentacaoService.unidadeHabilitada(10L)).thenReturn(true);
        when(agendaPedidoRefeicaoService.unidadeAptaParaAutomacao(10L, referencia)).thenReturn(true);
        doReturn(executorTransacional).when(serviceSpy).obterExecutorTransacional();
        when(tipoRefeicaoService.listarTodos()).thenReturn(Collections.singletonList(cafe));
        when(contratoService.buscarContratoVigenteEAtivoPorUnidade(eq(10L), any(Timestamp.class)))
                .thenReturn(contrato);
        when(contratoService.buscarHorarioEntregaVigentePorUnidade(eq(10L), eq(1L), any(Timestamp.class)))
                .thenReturn(horario("1200", cafe));
        when(executorTransacional.gerarPedidosAutomaticosDaUnidade(eq(10L), any(LocalDateTime.class)))
                .thenReturn(1);

        int resultado = serviceSpy.gerarPedidosAutomaticosDaUnidadeComCatchUp(10L, referencia, 0);

        assertThat(resultado).isEqualTo(1);
        verify(executorTransacional).gerarPedidosAutomaticosDaUnidade(10L, referencia);
    }

    @Test
    public void montarReferenciasCatchUp_deveIncluirHorariosVigentesEFimDaJanela() throws Exception {
        setField(service, "contratoService", contratoService);
        setField(service, "tipoRefeicaoService", tipoRefeicaoService);
        LocalDateTime inicio = LocalDateTime.of(2026, 5, 8, 6, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 5, 8, 13, 0);
        TipoRefeicao cafe = tipoRefeicao(1L, "Cafe");
        TipoRefeicao almoco = tipoRefeicao(2L, "Almoco");
        Contrato contrato = contrato(20L);

        when(tipoRefeicaoService.listarTodos()).thenReturn(Arrays.asList(cafe, almoco));
        when(contratoService.buscarContratoVigenteEAtivoPorUnidade(eq(10L), any(Timestamp.class)))
                .thenReturn(contrato);
        when(contratoService.buscarHorarioEntregaVigentePorUnidade(eq(10L), eq(1L), any(Timestamp.class)))
                .thenReturn(horario("0700", cafe));
        when(contratoService.buscarHorarioEntregaVigentePorUnidade(eq(10L), eq(2L), any(Timestamp.class)))
                .thenReturn(horario("1200", almoco));

        List<LocalDateTime> resultado = montarReferenciasCatchUp(10L, inicio, fim);

        assertThat(resultado).containsExactly(
                LocalDateTime.of(2026, 5, 8, 7, 0, 1),
                LocalDateTime.of(2026, 5, 8, 12, 0, 1),
                fim);
    }

    @Test(expected = ValidacaoException.class)
    public void gerarPedidoAutomatico_deveBloquearQuandoJaExisteAtivo() throws Exception {
        PedidoAlimentoService serviceSpy = serviceComPedidoMontado(pedidoAlimento());
        UnidadePrisional unidade = unidade(10L);
        Contrato contrato = contrato(20L);
        TipoRefeicao tipoRefeicao = tipoRefeicao(30L, "Almoco");

        when(dao.existePedidoAtivoPorTipoRefeicaoEUnidade("24/04/2026 12:00:00", 30L, 10L, 20L))
                .thenReturn(true);

        serviceSpy.gerarPedido(
                unidade,
                contrato,
                tipoRefeicao,
                "24/04/2026 12:00:00",
                0L,
                PedidoAlimentoService.ModoGeracaoPedido.AUTOMATICO_ATIVO);
    }

    @Test(expected = ValidacaoException.class)
    public void gerarPedidoAutomatico_deveBloquearQuandoJaExisteAtivoNoMesmoDiaComHorarioDiferente()
            throws Exception {
        PedidoAlimento pedido = PedidoAlimento.create("24/04/2026 11:10:00", 0L, 10L, 20L, 30L);
        PedidoAlimentoService serviceSpy = serviceComPedidoMontado(pedido, "24/04/2026 11:10:00");
        UnidadePrisional unidade = unidade(10L);
        Contrato contrato = contrato(20L);
        TipoRefeicao tipoRefeicao = tipoRefeicao(30L, "Almoco");

        when(dao.existePedidoAtivoPorTipoRefeicaoEUnidade("24/04/2026 11:10:00", 30L, 10L, 20L))
                .thenReturn(true);

        serviceSpy.gerarPedido(
                unidade,
                contrato,
                tipoRefeicao,
                "24/04/2026 11:10:00",
                0L,
                PedidoAlimentoService.ModoGeracaoPedido.AUTOMATICO_ATIVO);
    }

    @Test
    public void gerarPedidoManualAssinado_deveSubstituirAtivoNoMesmoDiaComHorarioDiferente()
            throws Exception {
        PedidoAlimento pedido = PedidoAlimento.create("24/04/2026 11:10:00", 0L, 10L, 20L, 30L);
        PedidoAlimentoService serviceSpy = serviceComPedidoMontado(pedido, "24/04/2026 11:10:00");
        UnidadePrisional unidade = unidade(10L);
        Contrato contrato = contrato(20L);
        TipoRefeicao tipoRefeicao = tipoRefeicao(30L, "Almoco");

        when(dao.existePedidoAtivoPorTipoRefeicaoEUnidade("24/04/2026 11:10:00", 30L, 10L, 20L))
                .thenReturn(true);
        when(dao.findPedidosEmAbertoPorTipoRefeicaoEUnidade("24/04/2026 11:10:00", 30L, 10L, 20L))
                .thenReturn(Arrays.asList(501L, 502L));

        PedidoAlimentoService.PedidoGerado resultado = serviceSpy.gerarPedido(
                unidade,
                contrato,
                tipoRefeicao,
                "24/04/2026 11:10:00",
                0L,
                PedidoAlimentoService.ModoGeracaoPedido.MANUAL_ASSINADO);

        assertThat(resultado.isSubstituiuPedidoAtivo()).isTrue();
        assertThat(pedido.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(pedido.getOrigem()).isEqualTo(OrigemPedidoAlimento.MANUAL);
        verify(dao).bulkDeactivateItemPedidoAlimentoByPedidoIds(Arrays.asList(501L, 502L));
        verify(dao).bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(Arrays.asList(501L, 502L));
        verify(dao).bulkDeactivatePedidoAlimentoByIds(Arrays.asList(501L, 502L));
        verify(dao).salvar(pedido);
    }

    @Test
    public void gerarPedidoManualPendente_devePersistirOrigemManual() throws Exception {
        PedidoAlimento pedido = pedidoAlimento();
        PedidoAlimentoService serviceSpy = serviceComPedidoMontado(pedido);
        UnidadePrisional unidade = unidade(10L);
        Contrato contrato = contrato(20L);
        TipoRefeicao tipoRefeicao = tipoRefeicao(30L, "Almoco");

        serviceSpy.gerarPedido(
                unidade,
                contrato,
                tipoRefeicao,
                "24/04/2026 12:00:00",
                99L,
                PedidoAlimentoService.ModoGeracaoPedido.MANUAL_PENDENTE);

        assertThat(pedido.getStatus()).isEqualTo(Status.PENDENTE);
        assertThat(pedido.getOrigem()).isEqualTo(OrigemPedidoAlimento.MANUAL);
        verify(dao).salvar(pedido);
    }

    @Test
    public void gerarPedidoAutomatico_deveInativarPendentesESalvarAtivoSemSubstituirAtivo() throws Exception {
        PedidoAlimento pedido = pedidoAlimento();
        PedidoAlimentoService serviceSpy = serviceComPedidoMontado(pedido);
        UnidadePrisional unidade = unidade(10L);
        Contrato contrato = contrato(20L);
        TipoRefeicao tipoRefeicao = tipoRefeicao(30L, "Almoco");

        when(dao.existePedidoAtivoPorTipoRefeicaoEUnidade("24/04/2026 12:00:00", 30L, 10L, 20L))
                .thenReturn(false);
        when(dao.findPedidosPendentesPorTipoRefeicaoEUnidade("24/04/2026 12:00:00", 30L, 10L, 20L))
                .thenReturn(Arrays.asList(101L, 102L));

        PedidoAlimentoService.PedidoGerado resultado = serviceSpy.gerarPedido(
                unidade,
                contrato,
                tipoRefeicao,
                "24/04/2026 12:00:00",
                0L,
                PedidoAlimentoService.ModoGeracaoPedido.AUTOMATICO_ATIVO);

        assertThat(resultado.isSubstituiuPedidoAtivo()).isFalse();
        assertThat(pedido.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(pedido.getOrigem()).isEqualTo(OrigemPedidoAlimento.AUTOMATICO);
        verify(dao).bulkDeactivateItemPedidoAlimentoByPedidoIds(Arrays.asList(101L, 102L));
        verify(dao).bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(Arrays.asList(101L, 102L));
        verify(dao).bulkDeactivatePedidoAlimentoByIds(Arrays.asList(101L, 102L));
        verify(dao).salvarComFlush(pedido);
    }

    @Test
    public void gerarPedidoAutomatico_deveSalvarAtivoSemInativarQuandoNaoExistemPendentes() throws Exception {
        PedidoAlimento pedido = pedidoAlimento();
        PedidoAlimentoService serviceSpy = serviceComPedidoMontado(pedido);
        UnidadePrisional unidade = unidade(10L);
        Contrato contrato = contrato(20L);
        TipoRefeicao tipoRefeicao = tipoRefeicao(30L, "Almoco");

        when(dao.existePedidoAtivoPorTipoRefeicaoEUnidade("24/04/2026 12:00:00", 30L, 10L, 20L))
                .thenReturn(false);
        when(dao.findPedidosPendentesPorTipoRefeicaoEUnidade("24/04/2026 12:00:00", 30L, 10L, 20L))
                .thenReturn(Collections.<Long>emptyList());

        serviceSpy.gerarPedido(
                unidade,
                contrato,
                tipoRefeicao,
                "24/04/2026 12:00:00",
                0L,
                PedidoAlimentoService.ModoGeracaoPedido.AUTOMATICO_ATIVO);

        assertThat(pedido.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(pedido.getOrigem()).isEqualTo(OrigemPedidoAlimento.AUTOMATICO);
        verify(dao, never()).bulkDeactivateItemPedidoAlimentoByPedidoIds(any());
        verify(dao, never()).bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(any());
        verify(dao, never()).bulkDeactivatePedidoAlimentoByIds(any());
        verify(dao).salvarComFlush(pedido);
    }

    @Test
    public void gerarPedidosAutomaticosDaUnidade_deveConsultarCardapioComUnidadeAntesDeGerar() throws Exception {
        setField(service, "unidadePrisionalService", unidadePrisionalService);
        setField(service, "rolloutAlimentacaoService", rolloutAlimentacaoService);
        setField(service, "agendaPedidoRefeicaoService", agendaPedidoRefeicaoService);
        setField(service, "contratoService", contratoService);
        setField(service, "tipoRefeicaoService", tipoRefeicaoService);
        setField(service, "dao", dao);
        setField(service, "cardapioDAO", cardapioDAO);
        UnidadePrisional unidade = unidade(10L);
        Contrato contrato = contrato(20L);
        TipoRefeicao cafe = tipoRefeicao(1L, "Cafe");
        TipoRefeicao almoco = tipoRefeicao(2L, "Almoco");
        LocalDateTime referencia = LocalDateTime.of(2026, 4, 24, 7, 1);

        when(unidadePrisionalService.buscaPorId(10L)).thenReturn(unidade);
        when(rolloutAlimentacaoService.unidadeHabilitada(10L)).thenReturn(true);
        when(agendaPedidoRefeicaoService.unidadeAptaParaAutomacao(10L, referencia)).thenReturn(true);
        when(tipoRefeicaoService.listarTodos()).thenReturn(Arrays.asList(cafe, almoco));
        when(contratoService.buscarContratoVigenteEAtivoPorUnidade(eq(10L), any(Timestamp.class)))
                .thenReturn(contrato);
        when(contratoService.buscarHorarioEntregaVigentePorUnidade(eq(10L), eq(1L), any(Timestamp.class)))
                .thenReturn(horario("0700", cafe));
        when(contratoService.buscarHorarioEntregaVigentePorUnidade(eq(10L), eq(2L), any(Timestamp.class)))
                .thenReturn(horario("1200", almoco));
        when(dao.existePedidoAtivoPorTipoRefeicaoEUnidade("24/04/2026 12:00:00", 2L, 10L, 20L))
                .thenReturn(false);
        when(cardapioDAO.existeCardapioPara("24/04/2026 12:00:00", 2L, 20L, 10L)).thenReturn(false);

        int resultado = service.gerarPedidosAutomaticosDaUnidade(10L, referencia);

        assertThat(resultado).isZero();
        verify(cardapioDAO).existeCardapioPara("24/04/2026 12:00:00", 2L, 20L, 10L);
        verify(dao, never()).salvarComFlush(any(PedidoAlimento.class));
    }

    @Test
    public void montarPedidoValidando_deveConsultarCardapioComUnidade() throws Exception {
        PedidoAlimentoService serviceSpy = spy(new PedidoAlimentoService());
        setField(serviceSpy, "cardapioDAO", cardapioDAO);
        UnidadePrisional unidade = unidade(10L);
        Contrato contrato = contrato(20L);
        TipoRefeicao tipoRefeicao = tipoRefeicao(30L, "Almoco");
        PedidoAlimento pedido = pedidoAlimento();

        when(cardapioDAO.existeCardapioPara("24/04/2026 12:00:00", 30L, 20L, 10L)).thenReturn(true);
        doReturn(new PedidoAlimentoService.PedidoMontado(
                pedido,
                Collections.singletonList(new br.gov.mt.sejudh.sigepen.model.dto.DetalheItemEstruturaCardapioDTO(
                        "Arroz", 1.0, 10, "Kg", "Ala", 10, 100L, 200L)),
                Collections.emptyList()))
                .when(serviceSpy).montarPedido(unidade, contrato, tipoRefeicao, "24/04/2026 12:00:00", 0L);

        PedidoAlimentoService.PedidoMontado resultado = serviceSpy.montarPedidoValidando(
                unidade, contrato, tipoRefeicao, "24/04/2026 12:00:00", 0L);

        assertThat(resultado.getPedidoAlimento()).isSameAs(pedido);
        verify(cardapioDAO).existeCardapioPara("24/04/2026 12:00:00", 30L, 20L, 10L);
    }

    @Test
    public void montarPedidoValidando_deveBloquearQuandoNaoExistirCardapioDaUnidade() throws Exception {
        setField(service, "cardapioDAO", cardapioDAO);
        UnidadePrisional unidade = unidade(10L);
        Contrato contrato = contrato(20L);
        TipoRefeicao tipoRefeicao = tipoRefeicao(30L, "Almoco");

        when(cardapioDAO.existeCardapioPara("24/04/2026 12:00:00", 30L, 20L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> service.montarPedidoValidando(
                unidade, contrato, tipoRefeicao, "24/04/2026 12:00:00", 0L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("cardapio");
        verify(cardapioDAO).existeCardapioPara("24/04/2026 12:00:00", 30L, 20L, 10L);
    }

    private PedidoAlimentoService serviceComPedidoMontado(PedidoAlimento pedido) throws Exception {
        return serviceComPedidoMontado(pedido, "24/04/2026 12:00:00");
    }

    private PedidoAlimentoService serviceComPedidoMontado(PedidoAlimento pedido, String dataPedido) throws Exception {
        PedidoAlimentoService serviceSpy = spy(new PedidoAlimentoService());
        setField(serviceSpy, "dao", dao);
        setField(serviceSpy, "cardapioDAO", cardapioDAO);
        doReturn(new PedidoAlimentoService.PedidoMontado(
                pedido,
                Collections.emptyList(),
                Collections.emptyList()))
                .when(serviceSpy).montarPedidoValidando(
                        any(UnidadePrisional.class),
                        any(Contrato.class),
                        any(TipoRefeicao.class),
                        eq(dataPedido),
                        anyLong());
        return serviceSpy;
    }

    private PedidoAlimento pedidoAlimento() {
        return PedidoAlimento.create("24/04/2026 12:00:00", 0L, 10L, 20L, 30L);
    }

    private UnidadePrisional unidade(Long id) {
        UnidadePrisional unidade = new UnidadePrisional();
        unidade.setId(id);
        Estrutura estrutura = new Estrutura();
        estrutura.setId(100L);
        unidade.setEstrutura(estrutura);
        return unidade;
    }

    private Contrato contrato(Long id) {
        Contrato contrato = new Contrato();
        contrato.setId(id);
        return contrato;
    }

    private HorarioRefeicao horario(String inicio, String descricao) {
        HorarioRefeicao horario = new HorarioRefeicao();
        horario.setHorarioInicio(inicio);
        TipoRefeicao tipo = new TipoRefeicao();
        tipo.setDescricao(descricao);
        horario.setTipoRefeicao(tipo);
        return horario;
    }

    private HorarioRefeicao horario(String inicio, TipoRefeicao tipo) {
        HorarioRefeicao horario = new HorarioRefeicao();
        horario.setHorarioInicio(inicio);
        horario.setTipoRefeicao(tipo);
        return horario;
    }

    private TipoRefeicao tipoRefeicao(Long id, String descricao) {
        TipoRefeicao tipo = new TipoRefeicao();
        tipo.setId(id);
        tipo.setDescricao(descricao);
        return tipo;
    }

    private void setField(Object target, String nomeCampo, Object valor) throws Exception {
        Field field = PedidoAlimentoService.class.getDeclaredField(nomeCampo);
        field.setAccessible(true);
        field.set(target, valor);
    }

    @SuppressWarnings("unchecked")
    private List<LocalDateTime> montarReferenciasCatchUp(Long unidadeId, LocalDateTime inicio, LocalDateTime fim)
            throws Exception {
        Method method = PedidoAlimentoService.class.getDeclaredMethod(
                "montarReferenciasCatchUp",
                Long.class,
                LocalDateTime.class,
                LocalDateTime.class);
        method.setAccessible(true);
        return (List<LocalDateTime>) method.invoke(service, unidadeId, inicio, fim);
    }
}
