package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.OrigemPedidoAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.Fornecedor;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoComboDTO;
import br.gov.mt.sejudh.sigepen.model.dto.DetalheItemEstruturaCardapioDTO;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialEstruturaDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.service.HistoricoPedidoAlimentoService;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class HistoricoPedidoAlimentoBeanTest extends BaseUnitTest {

    @Mock
    private HistoricoPedidoAlimentoService historicoPedidoAlimentoService;
    @Mock
    private TipoRefeicaoService tipoRefeicaoService;
    @Mock
    private SecurityUtil security;
    @Mock
    private Logger log;
    @Mock
    private Subject subject;
    @Mock
    private Session session;

    @InjectMocks
    private HistoricoPedidoAlimentoBeanTestavel bean;

    @After
    public void limparContextoShiro() {
        ThreadContext.unbindSubject();
    }

    @Test
    public void init_semPermissaoCarregaContratosDaUnidadeAtual() {
        configurarUnidadeSelecionada(15L);
        TipoRefeicao tipoRefeicao = tipoRefeicao(1L, "Cafe");
        ContratoComboDTO contrato = new ContratoComboDTO(2L, "CT-002");
        when(security.possuiPermissao("ListarTodasUnidades")).thenReturn(false);
        when(tipoRefeicaoService.listarTodos()).thenReturn(Collections.singletonList(tipoRefeicao));
        when(historicoPedidoAlimentoService.listarContratosParaCombo(false, 15L))
                .thenReturn(Collections.singletonList(contrato));

        String retorno = bean.init();

        assertThat(retorno).isNull();
        verify(historicoPedidoAlimentoService).listarContratosParaCombo(false, 15L);
        assertThat(bean.getTiposRefeicao()).containsExactly(tipoRefeicao);
        assertThat(bean.getContratos()).containsExactly(contrato);
    }

    @Test
    public void selecionarRelatorioEmissao_pedidoAutomaticoAtivo_deveUsarRelatorioNaoAssinado() {
        PedidoAlimento pedido = pedido(OrigemPedidoAlimento.AUTOMATICO, Status.ATIVO);

        Relatorio resultado = bean.selecionarRelatorioEmissao(pedido);

        assertThat(resultado).isEqualTo(Relatorio.PedidoDetalhado);
    }

    @Test
    public void selecionarRelatorioEmissao_pedidoManualPendente_deveUsarRelatorioNaoAssinado() {
        PedidoAlimento pedido = pedido(OrigemPedidoAlimento.MANUAL, Status.PENDENTE);

        Relatorio resultado = bean.selecionarRelatorioEmissao(pedido);

        assertThat(resultado).isEqualTo(Relatorio.PedidoDetalhado);
    }

    @Test
    public void selecionarRelatorioEmissao_pedidoManualAtivo_deveUsarRelatorioAssinado() {
        PedidoAlimento pedido = pedido(OrigemPedidoAlimento.MANUAL, Status.ATIVO);

        Relatorio resultado = bean.selecionarRelatorioEmissao(pedido);

        assertThat(resultado).isEqualTo(Relatorio.PedidoDetalhadoAssinado);
    }

    @Test
    public void selecionarRelatorioEmissao_pedidoManualInativo_deveUsarRelatorioNaoAssinado() {
        PedidoAlimento pedido = pedido(OrigemPedidoAlimento.MANUAL, Status.INATIVO);

        Relatorio resultado = bean.selecionarRelatorioEmissao(pedido);

        assertThat(resultado).isEqualTo(Relatorio.PedidoDetalhado);
    }

    @Test
    public void montarParametros_pedidoAutomatico_deveMarcarEmissaoAutomatica() throws Exception {
        setDescricoesRefeicao();
        PedidoAlimento pedido = pedido(OrigemPedidoAlimento.AUTOMATICO, Status.ATIVO);

        Map<String, Object> resultado = montarParametros(pedido);

        assertThat(resultado).containsEntry("EMISSAO_AUTOMATICA", true);
        assertThat(resultado).containsEntry("EMISSOR", "");
    }

    @Test
    public void montarParametros_pedidoManual_deveUsarMatriculaDoUsuario() throws Exception {
        setDescricoesRefeicao();
        PedidoAlimento pedido = pedido(OrigemPedidoAlimento.MANUAL, Status.ATIVO);
        when(historicoPedidoAlimentoService.buscarMatriculaUsuarioPorId(10L)).thenReturn(12345L);

        Map<String, Object> resultado = montarParametros(pedido);

        assertThat(resultado).containsEntry("EMISSAO_AUTOMATICA", false);
        assertThat(resultado).containsEntry("EMISSOR", "12345");
    }

    private void configurarUnidadeSelecionada(Long unidadeId) {
        UnidadePrisional unidade = new UnidadePrisional(unidadeId);
        when(subject.getSession()).thenReturn(session);
        when(session.getAttribute("unidadeSelecionada")).thenReturn(unidade);
        ThreadContext.bind(subject);
    }

    private TipoRefeicao tipoRefeicao(Long id, String descricao) {
        TipoRefeicao tipoRefeicao = new TipoRefeicao();
        tipoRefeicao.setId(id);
        tipoRefeicao.setDescricao(descricao);
        return tipoRefeicao;
    }

    private PedidoAlimento pedido(OrigemPedidoAlimento origem, Status status) {
        PedidoAlimento pedido = PedidoAlimento.create("24/04/2026 12:00:00", 10L, 20L, 30L, 40L, origem);
        pedido.setStatus(status);
        return pedido;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> montarParametros(PedidoAlimento pedido) throws Exception {
        Method method = HistoricoPedidoAlimentoBean.class.getDeclaredMethod(
                "montarParametros",
                PedidoAlimento.class,
                java.util.List.class,
                java.util.List.class,
                Contrato.class,
                String.class);
        method.setAccessible(true);
        return (Map<String, Object>) method.invoke(
                bean,
                pedido,
                Collections.<DetalheItemEstruturaCardapioDTO>emptyList(),
                Collections.<DietaEspecialEstruturaDTO>emptyList(),
                contrato(),
                "Unidade A");
    }

    private void setDescricoesRefeicao() throws Exception {
        Field field = HistoricoPedidoAlimentoBean.class.getDeclaredField("descricoesRefeicao");
        field.setAccessible(true);
        field.set(bean, Collections.singletonMap(40L, "Almoco"));
    }

    private Contrato contrato() {
        Fornecedor fornecedor = new Fornecedor(1L, "Fornecedor", "Fornecedor", "12345678000199");
        Contrato contrato = new Contrato();
        contrato.setFornecedor(fornecedor);
        return contrato;
    }

    private static class HistoricoPedidoAlimentoBeanTestavel extends HistoricoPedidoAlimentoBean {

        @Override
        protected AcaoDeTela getAcaoDeTela() {
            return AcaoDeTela.LISTAR;
        }

        @Override
        protected void logDeAcesso(Logger log, Object obj) {
        }
    }
}
