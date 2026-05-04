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

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.Transferencia;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.TransferenciaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

public class TransferenciaBeanTest extends BaseUnitTest {

    @Mock
    private TransferenciaService service;
    @Mock
    private RecuperandoService serviceRecuperando;
    @Mock
    private FacesUtil facesUtil;
    @Mock
    private Logger log;

    @InjectMocks
    private TransferenciaBean bean;

    @Test
    public void salvar_semPedidos_exibeValidacaoENaoSalvaTransferencia() {
        Transferencia transferencia = new Transferencia();
        transferencia.setId(10L);
        bean.setTransferencia(transferencia);

        String retorno = bean.salvar();

        assertThat(retorno).isNull();
        verify(facesUtil).exibirMensagem(org.mockito.Mockito.eq(FacesMessage.SEVERITY_WARN),
                org.mockito.Mockito.eq("ERRO"), contains("adicionar um pedido"));
        verify(service, never()).salvar(any(Transferencia.class));
    }

    @Test
    public void confirmarInicioTransferencia_semEscolta_exibeValidacaoENaoInicia() {
        Transferencia transferencia = new Transferencia();
        transferencia.setId(10L);
        transferencia.getListTransferenciaPedidoTransitoDTO().add(new br.gov.mt.sejudh.sigepen.model.dto.TransferenciaPedidoTransitoDTO());
        bean.setTransferencia(transferencia);

        String retorno = bean.confirmarInicioTransferencia();

        assertThat(retorno).isNull();
        verify(facesUtil).exibirMensagem(org.mockito.Mockito.eq(FacesMessage.SEVERITY_FATAL),
                org.mockito.Mockito.eq("ERRO"), contains("adicionar uma escolta"));
        verify(service, never()).iniciarTransferencia(any(Transferencia.class));
    }
}
