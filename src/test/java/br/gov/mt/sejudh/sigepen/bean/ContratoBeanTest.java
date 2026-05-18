package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroContrato;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.service.ContratoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class ContratoBeanTest extends BaseUnitTest {

    @Mock
    private ContratoService contratoService;
    @Mock
    private UnidadePrisionalService unidadeService;
    @Mock
    private SecurityUtil security;
    @Mock
    private Subject subject;
    @Mock
    private Session session;

    @InjectMocks
    private ContratoBean bean;

    @After
    public void limparContextoShiro() {
        ThreadContext.unbindSubject();
    }

    @Test
    public void completarFornecedoresFiltro_semPermissaoFiltraUnidadeELimita() {
        configurarUnidadeSelecionada(7L);
        when(security.possuiPermissao("ListarTodasUnidades")).thenReturn(false);
        when(contratoService.listarFornecedoresParaFiltro(false, 7L, "For", 20))
                .thenReturn(Arrays.asList("Fornecedor A", "Fornecedor B"));

        java.util.List<String> resultado = bean.completarFornecedoresFiltro(" For ");

        assertThat(resultado).containsExactly("Fornecedor A", "Fornecedor B");
        verify(contratoService).listarFornecedoresParaFiltro(false, 7L, "For", 20);
    }

    @Test
    public void completarNumerosContratosFiltro_semPermissaoFiltraUnidadeELimita() {
        configurarUnidadeSelecionada(7L);
        when(security.possuiPermissao("ListarTodasUnidades")).thenReturn(false);
        when(contratoService.listarNumerosParaFiltro(false, 7L, "CT", 20))
                .thenReturn(Arrays.asList("CT-001", "CT-002"));

        java.util.List<String> resultado = bean.completarNumerosContratosFiltro(" CT ");

        assertThat(resultado).containsExactly("CT-001", "CT-002");
        verify(contratoService).listarNumerosParaFiltro(false, 7L, "CT", 20);
    }

    @Test
    public void completarContratoFiltro_consultaCurtaNaoBuscaBanco() {
        assertThat(bean.completarFornecedoresFiltro("F")).isEmpty();
        assertThat(bean.completarNumerosContratosFiltro("C")).isEmpty();
        org.mockito.Mockito.verify(contratoService, org.mockito.Mockito.never())
                .listarFornecedoresParaFiltro(org.mockito.Mockito.anyBoolean(), org.mockito.Mockito.any(),
                        org.mockito.Mockito.anyString(), org.mockito.Mockito.anyInt());
        org.mockito.Mockito.verify(contratoService, org.mockito.Mockito.never())
                .listarNumerosParaFiltro(org.mockito.Mockito.anyBoolean(), org.mockito.Mockito.any(),
                        org.mockito.Mockito.anyString(), org.mockito.Mockito.anyInt());
    }

    @Test
    public void atualizarUnidadesPorContrato_comContratoSelecionadoListaUnidadesDoContrato() {
        FiltroContrato filtro = new FiltroContrato();
        filtro.setNumContrato(" CT-001 ");
        filtro.setUnidadePrisional(new UnidadePrisional(99L));
        bean.setFiltro(filtro);
        when(security.possuiPermissao("ListarTodasUnidades")).thenReturn(true);
        UnidadePrisional unidade = unidade(7L, "Unidade A");
        when(contratoService.listarUnidadesPorNumeroContrato("CT-001"))
                .thenReturn(Collections.singletonList(unidade));

        bean.atualizarUnidadesPorContrato();

        assertThat(bean.getUnidadesPrisionais()).containsExactly(unidade);
        assertThat(bean.getFiltro().getUnidadePrisional()).isNull();
        verify(contratoService).listarUnidadesPorNumeroContrato("CT-001");
    }

    @Test
    public void atualizarUnidadesPorContrato_semContratoRestauraTodasUnidades() {
        FiltroContrato filtro = new FiltroContrato();
        filtro.setUnidadePrisional(new UnidadePrisional(99L));
        bean.setFiltro(filtro);
        when(security.possuiPermissao("ListarTodasUnidades")).thenReturn(true);
        UnidadePrisional unidade = unidade(8L, "Unidade B");
        when(unidadeService.listarUnidadesNaoInativas()).thenReturn(Collections.singletonList(unidade));

        bean.atualizarUnidadesPorContrato();

        assertThat(bean.getUnidadesPrisionais()).containsExactly(unidade);
        assertThat(bean.getFiltro().getUnidadePrisional()).isNull();
        verify(unidadeService).listarUnidadesNaoInativas();
    }

    private void configurarUnidadeSelecionada(Long unidadeId) {
        UnidadePrisional unidade = new UnidadePrisional(unidadeId);
        when(subject.getSession()).thenReturn(session);
        when(session.getAttribute("unidadeSelecionada")).thenReturn(unidade);
        ThreadContext.bind(subject);
    }

    private UnidadePrisional unidade(Long id, String nome) {
        UnidadePrisional unidade = new UnidadePrisional(id);
        unidade.setNome(nome);
        return unidade;
    }
}
