package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Date;

import javax.faces.application.FacesMessage;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.DietaRecuperando;
import br.gov.mt.sejudh.sigepen.service.DietaEspecialService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

public class DietaEspecialBeanTest extends BaseUnitTest {

    @Mock
    private DietaEspecialService dietaEspecialService;
    @Mock
    private FacesUtil facesUtil;
    @Mock
    private Logger log;

    @InjectMocks
    private DietaEspecialBean bean;

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
        org.mockito.Mockito.when(dietaEspecialService.salvar(dieta))
                .thenThrow(new br.gov.mt.sejudh.sigepen.exception.ValidacaoException("dieta invÃ¡lida"));

        String retorno = bean.salvar();

        assertThat(retorno).isNull();
        verify(facesUtil).exibirMensagem(FacesMessage.SEVERITY_WARN, "AVISO", "dieta invÃ¡lida");
    }
}
