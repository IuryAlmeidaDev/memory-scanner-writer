package br.gov.mt.sejudh.sigepen.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.MovimentacaoExternaDAO;
import br.gov.mt.sejudh.sigepen.dao.MovimentacaoInternaDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.TransferenciaDAO;
import br.gov.mt.sejudh.sigepen.dao.VisitanteDAO;
import br.gov.mt.sejudh.sigepen.enumerator.MovimentacaoPassos;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Transferencia;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;

public class PedidoServiceTest extends BaseUnitTest {

    @Mock
    private PedidoDAO dao;
    @Mock
    private MovimentacaoInternaDAO movIntDao;
    @Mock
    private MovimentacaoExternaDAO movExtDao;
    @Mock
    private TransferenciaDAO transfDao;
    @Mock
    private RecuperandoDAO recuperandoDao;
    @Mock
    private AuditoriaDAO auditDAO;
    @Mock
    private RecuperandoService recuperandoService;
    @Mock
    private VisitanteDAO visitanteDao;
    @Mock
    private MovimentacaoInternaService movtoInternaService;
    @Mock
    private DietaEspecialService dietaEspecialService;

    @InjectMocks
    private PedidoService service;

    @Test
    public void finalizarPedidoTransferencia_naoAlteraDietaEspecial() throws Exception {
        PedidoTransito pedido = new PedidoTransito();
        pedido.setEstruturaDestino(new Estrutura());
        pedido.setTransferencia(new Transferencia());

        Recuperando recuperando = org.mockito.Mockito.mock(Recuperando.class);
        Passagem passagemAtual = org.mockito.Mockito.mock(Passagem.class);
        PassagemLotacao lotacaoAtual = org.mockito.Mockito.mock(PassagemLotacao.class);

        when(recuperando.getId()).thenReturn(99L);
        when(recuperando.getPassagemAtual()).thenReturn(passagemAtual);
        when(passagemAtual.getLotacaoAtual()).thenReturn(lotacaoAtual);
        when(passagemAtual.getLotacao()).thenReturn(new ArrayList<PassagemLotacao>());
        when(lotacaoAtual.getDataEntrada()).thenReturn(new Date(System.currentTimeMillis() - 60_000));

        pedido.setRecuperando(recuperando);

        service.finalizarPedidoTransferencia(pedido);

        ArgumentCaptor<Date> dataFimCaptor = ArgumentCaptor.forClass(Date.class);
        verify(lotacaoAtual).setDataSaida(dataFimCaptor.capture());
        verify(dietaEspecialService, never()).encerrarDietasAtivasDoRecuperando(eq(99L), eq(dataFimCaptor.getValue()));
        verify(dietaEspecialService, never()).inativarDietasAtivasOuSuspensasDoRecuperando(eq(99L), eq(dataFimCaptor.getValue()));
        verify(dietaEspecialService, never()).suspenderDietasAtivasDoRecuperando(eq(99L));
    }

    @Test
    public void validarPedido_semRecuperando_lancaValidacao() {
        PedidoTransito pedido = new PedidoTransito();

        assertThatThrownBy(() -> service.validarPedido(pedido, MovimentacaoPassos.SALVAR))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("recuperando");
    }

    @Test
    public void validarPedido_transferenciaComUnidadeDestinoIgualOrigem_lancaValidacao() {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade");
        Estrutura estruturaOrigem = new Estrutura();
        estruturaOrigem.setId(1L);
        Recuperando recuperando = new Recuperando();
        recuperando.setId(99L);

        PedidoTransito pedido = new PedidoTransito();
        pedido.setId(77L);
        pedido.setRecuperando(recuperando);
        pedido.setEstruturaOrigem(estruturaOrigem);
        pedido.setUnidadeOrigem(unidade);
        pedido.setUnidadeDestino(unidade);
        pedido.setTipoTransito(TipoTransito.TRANSFERENCIA);
        pedido.setMotivo("motivo");

        when(recuperandoService.statusAtualRecuperando(99L)).thenReturn(StatusRecuperando.ATIVO);
        when(recuperandoService.lotacaoAtual(recuperando)).thenReturn(estruturaOrigem);

        assertThatThrownBy(() -> service.validarPedido(pedido, MovimentacaoPassos.SALVAR))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Unidade de destino");
    }
}
