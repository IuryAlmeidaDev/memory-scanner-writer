package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

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
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoComboDTO;
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
