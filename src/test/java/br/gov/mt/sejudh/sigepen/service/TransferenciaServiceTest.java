package br.gov.mt.sejudh.sigepen.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoDAO;
import br.gov.mt.sejudh.sigepen.dao.TransferenciaDAO;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Transferencia;
import br.gov.mt.sejudh.sigepen.model.dto.TransferenciaPedidoTransitoDTO;

public class TransferenciaServiceTest extends BaseUnitTest {

    @Mock
    private TransferenciaDAO dao;
    @Mock
    private PedidoDAO daoPedido;
    @Mock
    private AuditoriaDAO auditDAO;
    @Mock
    private DietaEspecialService dietaEspecialService;

    @InjectMocks
    private TransferenciaService service;

    @Test
    public void iniciarTransferencia_suspendeDietasDosRecuperandos() {
        Transferencia transferencia = transferenciaComRecuperandos(10L, 99L, 100L, 99L);

        service.iniciarTransferencia(transferencia);

        verify(daoPedido).atualizaPedidosParaEmAndamentoTransitoPorTransferenciaId(
                eq(10L), eq(StatusPedido.EM_ANDAMENTO), any(Date.class));
        verify(dietaEspecialService).suspenderDietasAtivasDoRecuperando(99L);
        verify(dietaEspecialService).suspenderDietasAtivasDoRecuperando(100L);
        verify(dao).salvar(transferencia);
    }

    @Test
    public void cancelarTransferencia_emAndamento_reativaDietasSuspensasDosRecuperandos() throws Exception {
        Transferencia transferencia = transferenciaComRecuperandos(10L, 99L, 100L);
        transferencia.setStatus(StatusMovimentacao.EM_ANDAMENTO);

        service.cancelarTransferencia(transferencia);

        verify(daoPedido).atualizaPedidosParaCanceladoTransitoPorTransferenciaId(
                eq(10L), eq(StatusPedido.CANCELADO), any(Date.class));
        verify(dietaEspecialService).reativarDietasSuspensasDoRecuperando(99L);
        verify(dietaEspecialService).reativarDietasSuspensasDoRecuperando(100L);
        verify(dao).salvar(transferencia);
    }

    @Test
    public void cancelarTransferencia_pendente_naoReativaDietas() throws Exception {
        Transferencia transferencia = transferenciaComRecuperandos(10L, 99L);
        transferencia.setStatus(StatusMovimentacao.PENDENTE);

        service.cancelarTransferencia(transferencia);

        verify(dietaEspecialService, never()).reativarDietasSuspensasDoRecuperando(99L);
        verify(dao).salvar(transferencia);
    }

    @Test
    public void cancelarTransferencia_finalizada_lancaValidacaoENaoAlteraPedidos() {
        Transferencia transferencia = new Transferencia();
        transferencia.setId(10L);
        transferencia.setStatus(StatusMovimentacao.FINALIZADO);

        assertThatThrownBy(() -> service.cancelarTransferencia(transferencia))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("finalizada");

        verify(daoPedido, never()).atualizaPedidosParaCanceladoTransitoPorTransferenciaId(any(Long.class), any(), any());
        verify(dao, never()).salvar(any(Transferencia.class));
    }

    @Test
    public void iniciarTransferencia_semDtosBuscaPedidosNoDaoParaSuspenderDietas() {
        Transferencia transferencia = new Transferencia();
        transferencia.setId(10L);
        transferencia.setListTransferenciaPedidoTransitoDTO(null);
        when(daoPedido.buscarPedidosPorTransferencia(10L))
                .thenReturn(Arrays.asList(pedidoDoRecuperando(99L), pedidoDoRecuperando(100L), pedidoDoRecuperando(99L)));

        service.iniciarTransferencia(transferencia);

        verify(dietaEspecialService).suspenderDietasAtivasDoRecuperando(99L);
        verify(dietaEspecialService).suspenderDietasAtivasDoRecuperando(100L);
        verify(dao).salvar(transferencia);
    }

    @Test
    public void iniciarTransferencia_semDtosEDaoRetornaNull_naoSuspendeDietas() {
        Transferencia transferencia = new Transferencia();
        transferencia.setId(10L);
        transferencia.setListTransferenciaPedidoTransitoDTO(null);
        when(daoPedido.buscarPedidosPorTransferencia(10L)).thenReturn(null);

        service.iniciarTransferencia(transferencia);

        verify(dietaEspecialService, never()).suspenderDietasAtivasDoRecuperando(any(Long.class));
        verify(dao).salvar(transferencia);
    }

    private Transferencia transferenciaComRecuperandos(Long transferenciaId, Long... recuperandoIds) {
        Transferencia transferencia = new Transferencia();
        transferencia.setId(transferenciaId);
        transferencia.setListTransferenciaPedidoTransitoDTO(Arrays.asList(
                Arrays.stream(recuperandoIds)
                        .map(this::pedidoDTO)
                        .toArray(TransferenciaPedidoTransitoDTO[]::new)));
        return transferencia;
    }

    private TransferenciaPedidoTransitoDTO pedidoDTO(Long recuperandoId) {
        TransferenciaPedidoTransitoDTO dto = new TransferenciaPedidoTransitoDTO();
        dto.setMatricula(recuperandoId);
        return dto;
    }

    private PedidoTransito pedidoDoRecuperando(Long recuperandoId) {
        Recuperando recuperando = new Recuperando();
        recuperando.setId(recuperandoId);

        PedidoTransito pedido = new PedidoTransito();
        pedido.setRecuperando(recuperando);
        return pedido;
    }
}
