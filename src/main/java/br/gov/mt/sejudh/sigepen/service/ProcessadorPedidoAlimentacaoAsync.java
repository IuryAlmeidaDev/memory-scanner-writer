package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ProcessadorPedidoAlimentacaoAsync {

    private static final Logger LOGGER = Logger.getLogger(ProcessadorPedidoAlimentacaoAsync.class.getName());

    @Inject
    private PedidoAlimentoService pedidoAlimentoService;

    @Asynchronous
    public void processarUnidade(Long unidadeId, LocalDateTime referencia) {
        try {
            pedidoAlimentoService.gerarPedidosAutomaticosDaUnidade(unidadeId, referencia);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao processar pedido automatico da unidade " + unidadeId
                    + " na referencia " + referencia, e);
        }
    }

    @Asynchronous
    public void processarCatchUpUnidade(Long unidadeId, LocalDateTime referencia, int diasRetroativos) {
        try {
            pedidoAlimentoService.gerarPedidosAutomaticosDaUnidadeComCatchUp(unidadeId, referencia, diasRetroativos);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erro ao processar catch-up de pedido automatico da unidade " + unidadeId
                    + " na referencia " + referencia, e);
        }
    }
}
