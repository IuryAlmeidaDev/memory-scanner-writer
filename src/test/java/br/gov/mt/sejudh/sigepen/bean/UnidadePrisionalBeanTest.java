package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.ParametroUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.service.ParametroUnidadeService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.faces.application.FacesMessage;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class UnidadePrisionalBeanTest extends BaseUnitTest {

    @Mock
    private UnidadePrisionalService unidadeService;

    @Mock
    private ParametroUnidadeService parametroUnidadeService;

    @Mock
    private FacesUtil facesUtil;

    @Spy
    @InjectMocks
    private UnidadePrisionalBean bean;

    @Before
    public void isolarDependenciasEstaticas() {
        doReturn(new UnidadePrisional(99L)).when(bean).getUnidadeSelecionadaAtual();
        doNothing().when(bean).setUnidadeSelecionadaAtual(org.mockito.ArgumentMatchers.any(UnidadePrisional.class));
        doNothing().when(bean).registrarSucessoSalvar();
    }

    @Test
    public void salvar_quandoLimiteValido_deveSalvarParametrosUnidadeEUnidade() throws Exception {
        UnidadePrisional unidade = unidade(1L, 2);
        List<ParametroUnidade> parametros = Collections.emptyList();
        bean.setUnidade(unidade);
        setField("parametrosVisitante", parametros);

        String retorno = bean.salvar();

        assertThat(retorno).isEqualTo("pretty:ListarUnidade");
        verify(parametroUnidadeService).salvarParametrosVisitanteUnidade(parametros, unidade);
        verify(unidadeService).salvar(unidade);
        verify(facesUtil).exibirMensagemInfo("Unidade Prisional UNIDADE salva com Sucesso!", true);
    }

    @Test
    public void salvar_quandoServiceLancarValidacaoException_deveExibirMensagemWarnERetornarNulo() throws Exception {
        UnidadePrisional unidade = unidade(1L, -1);
        bean.setUnidade(unidade);
        setField("parametrosVisitante", Collections.<ParametroUnidade>emptyList());
        doThrow(new ValidacaoException("Quantidade de visita por recuperando não pode ser negativa"))
                .when(unidadeService).salvar(unidade);

        String retorno = bean.salvar();

        assertThat(retorno).isNull();
        verify(parametroUnidadeService).salvarParametrosVisitanteUnidade(Collections.<ParametroUnidade>emptyList(), unidade);
        verify(facesUtil).exibirMensagem(
                FacesMessage.SEVERITY_WARN,
                "ERRO",
                "Quantidade de visita por recuperando não pode ser negativa");
    }

    private UnidadePrisional unidade(Long id, Integer quantidadeVisitaPorRecuperando) {
        UnidadePrisional unidade = new UnidadePrisional(id);
        unidade.setNome("Unidade");
        unidade.setQuantidadeVisitaPorRecuperando(quantidadeVisitaPorRecuperando);
        return unidade;
    }

    private void setField(String nome, Object valor) throws Exception {
        Field field = UnidadePrisionalBean.class.getDeclaredField(nome);
        field.setAccessible(true);
        field.set(bean, valor);
    }
}
