package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

import javax.faces.application.FacesMessage;

import org.apache.logging.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.DietaRecuperando;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialDTO;
import br.gov.mt.sejudh.sigepen.service.DietaEspecialService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class DietaEspecialBeanTest extends BaseUnitTest {

    @Mock
    private DietaEspecialService dietaEspecialService;
    @Mock
    private FacesUtil facesUtil;
    @Mock
    private Logger log;
    @Mock
    private SecurityUtil security;
    @Mock
    private Subject subject;
    @Mock
    private Session session;

    @InjectMocks
    private DietaEspecialBean bean;

    @After
    public void limparContextoShiro() {
        ThreadContext.unbindSubject();
    }

    @Test
    public void salvar_dataFimAntesOuIgualInicio_naoSalvaDieta() throws Exception {
        DietaRecuperando dieta = new DietaRecuperando();
        dieta.setInicio(new Date(2_000L));
        dieta.setFim(new Date(1_000L));
        bean.setDietaEspecial(dieta);

        String retorno = bean.salvar();

        assertThat(retorno).isEqualTo("pretty:ListarDietaEspecial");
        verify(facesUtil).exibirMensagemErro(contains("data fim"), org.mockito.Mockito.eq(true));
        verify(dietaEspecialService, never()).salvar(any(DietaRecuperando.class));
    }

    @Test
    public void salvar_serviceLancaValidacao_exibeAvisoENaoRetornaListagem() throws Exception {
        DietaRecuperando dieta = new DietaRecuperando();
        dieta.setInicio(new Date(1_000L));
        bean.setDietaEspecial(dieta);
        when(dietaEspecialService.salvar(dieta))
                .thenThrow(new br.gov.mt.sejudh.sigepen.exception.ValidacaoException("dieta invalida"));

        String retorno = bean.salvar();

        assertThat(retorno).isNull();
        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "AVISO", "dieta invalida");
    }

    @Test
    public void carregarNomesRecuperandosFiltro_semPermissaoFiltraUnidadeERemoveDuplicados() throws Exception {
        configurarUnidadeSelecionada(9L);
        when(security.possuiPermissao("ListarTodasUnidades")).thenReturn(false);
        when(dietaEspecialService.listarNomesRecuperandosParaFiltro(false, 9L))
                .thenReturn(Arrays.asList("Maria", null, "Ana", "Maria"));

        invocarCarregarNomesRecuperandosFiltro();

        verify(dietaEspecialService).listarNomesRecuperandosParaFiltro(false, 9L);
        assertThat(bean.getNomesRecuperandosFiltro()).containsExactly("Ana", "Maria");
    }

    @Test
    public void completarNomesRecuperandosFiltro_consultaCurtaNaoBuscaBanco() {
        java.util.List<String> resultado = bean.completarNomesRecuperandosFiltro("Ma");

        assertThat(resultado).isEmpty();
        verify(dietaEspecialService, never()).listarNomesRecuperandosParaFiltro(
                org.mockito.Mockito.anyBoolean(),
                org.mockito.Mockito.any(),
                org.mockito.Mockito.anyString(),
                org.mockito.Mockito.anyInt());
    }

    @Test
    public void possuiNomeSocial_deveRetornarTrueQuandoAlgumRegistroPossuiNomeSocial() {
        DietaEspecialDTO semNomeSocial = new DietaEspecialDTO();
        semNomeSocial.setNomeSocialRecuperando(" ");
        DietaEspecialDTO comNomeSocial = new DietaEspecialDTO();
        comNomeSocial.setNomeSocialRecuperando("Maria Social");

        boolean resultado = bean.possuiNomeSocial(Arrays.asList(semNomeSocial, comNomeSocial));

        assertThat(resultado).isTrue();
    }

    @Test
    public void possuiNomeSocial_deveRetornarFalseQuandoListaNaoPossuiNomeSocial() {
        DietaEspecialDTO semNomeSocial = new DietaEspecialDTO();
        semNomeSocial.setNomeSocialRecuperando(" ");

        boolean resultado = bean.possuiNomeSocial(Arrays.asList(semNomeSocial, new DietaEspecialDTO()));

        assertThat(resultado).isFalse();
    }

    private void configurarUnidadeSelecionada(Long unidadeId) {
        UnidadePrisional unidade = new UnidadePrisional(unidadeId);
        when(subject.getSession()).thenReturn(session);
        when(session.getAttribute("unidadeSelecionada")).thenReturn(unidade);
        ThreadContext.bind(subject);
    }

    private void invocarCarregarNomesRecuperandosFiltro() throws Exception {
        Method method = DietaEspecialBean.class.getDeclaredMethod("carregarNomesRecuperandosFiltro");
        method.setAccessible(true);
        method.invoke(bean);
    }
}
