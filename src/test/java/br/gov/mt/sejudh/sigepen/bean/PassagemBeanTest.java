package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import javax.faces.application.FacesMessage;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.PassagemProcedencia;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.service.PassagemService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

public class PassagemBeanTest extends BaseUnitTest {

    @Mock
    private PassagemService passagemService;
    @Mock
    private RecuperandoService recuperandoService;
    @Mock
    private FacesUtil facesUtil;
    @Mock
    private Logger log;

    @Spy
    @InjectMocks
    private PassagemBean bean;

    @Test
    public void salvar_semLotacao_exibeValidacaoENaoPersistePassagem() throws Exception {
        org.mockito.Mockito.doReturn(AcaoDeTela.ALTERAR).when(bean).getAcaoDeTela();
        bean.setRecuperando(new Recuperando());
        bean.setPassagem(new Passagem());
        bean.setLotacao(new PassagemLotacao());
        bean.setPassagemProcedencia(new PassagemProcedencia());

        String retorno = bean.salvar();

        assertThat(retorno).isNull();
        verify(facesUtil).exibirMensagem(org.mockito.Mockito.eq(FacesMessage.SEVERITY_WARN), org.mockito.Mockito.eq("ERRO"),
                contains("estrutura"));
        verify(passagemService, never()).alterar(any(Passagem.class));
        verify(recuperandoService, never()).salvar(any(Recuperando.class));
    }
}
