package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import javax.faces.application.FacesMessage;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.MovimentacaoPassos;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

public class PedidoTransitoBeanTest extends BaseUnitTest {

    @Mock
    private PedidoService service;
    @Mock
    private FacesUtil facesUtil;
    @Mock
    private Logger log;

    @InjectMocks
    private PedidoTransitoBean bean;

    @Test
    public void salvar_validacaoFalha_exibeAvisoENaoSalvaPedido() throws Exception {
        PedidoTransito pedido = new PedidoTransito();
        pedido.setId(77L);
        bean.setPedido(pedido);
        org.mockito.Mockito.doThrow(new ValidacaoException("pedido inválido"))
                .when(service).validarPedido(pedido, MovimentacaoPassos.SALVAR);

        String retorno = bean.salvar();

        assertThat(retorno).isNull();
        verify(facesUtil).exibirMensagem(org.mockito.Mockito.eq(FacesMessage.SEVERITY_WARN),
                org.mockito.ArgumentMatchers.contains("ATEN"), org.mockito.Mockito.eq("pedido inválido"));
        verify(service, never()).salvar(any(PedidoTransito.class));
    }
}
