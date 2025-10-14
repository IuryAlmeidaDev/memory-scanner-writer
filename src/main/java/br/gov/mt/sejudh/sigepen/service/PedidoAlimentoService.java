package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.dao.PedidoAlimentoDAO;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger; // Importar o logger

public class PedidoAlimentoService {

    @Inject
    private PedidoAlimentoDAO dao;

    private static final Logger LOGGER = Logger.getLogger(PedidoAlimentoService.class.getName());

    /**
     * Verifica a existência de um pedido com base em critérios específicos.
     *
     * @param dataPedido A data do pedido.
     * @param idTipoRefeicao O ID do tipo de refeição.
     * @param idUnidadePrisional O ID da unidade prisional.
     * @param idContrato O ID do contrato.
     * @return true se o pedido existir, false caso contrário.
     */
    public boolean verificarExistenciaPedido(String dataPedido, Long idTipoRefeicao, Long idUnidadePrisional, Long idContrato) {
        return dao.existePedido(dataPedido, idTipoRefeicao, idUnidadePrisional, idContrato);
    }

    /**
     * Implementa a lógica transacional para salvar um novo PedidoAlimento.
     * Garante que os pedidos existentes (e seus filhos ItemPedidoAlimento e DietaEspecialPedidoAlimento)
     * que correspondem aos critérios do novo pedido sejam desativados **em massa** na mesma transação
     * em que o novo pedido é salvo.
     *
     * @param novoPedidoAlimento O novo PedidoAlimento a ser persistido.
     */
    @Transactional
    public void salvarPedidoAlimento(final PedidoAlimento novoPedidoAlimento) {
        // 1. Busca os IDs dos pedidos ativos existentes que correspondem aos critérios
        List<Long> idsPedidosExistentesParaDesativar = dao.findActivePedidoAlimentoIdsByCriteria(
                novoPedidoAlimento.getDataPedido(),
                novoPedidoAlimento.getIdTipoRefeicao(),
                novoPedidoAlimento.getIdUnidadePrisional(),
                novoPedidoAlimento.getIdContrato()
        );

        if (!idsPedidosExistentesParaDesativar.isEmpty()) {
            LOGGER.info("Encontrados " + idsPedidosExistentesParaDesativar.size() + " pedidos ativos para desativar. IDs: " + idsPedidosExistentesParaDesativar);

            int itensAtualizados = dao.bulkDeactivateItemPedidoAlimentoByPedidoIds(idsPedidosExistentesParaDesativar);
            LOGGER.info(itensAtualizados + " ItemPedidoAlimento desativados em massa.");

            int dietasAtualizadas = dao.bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(idsPedidosExistentesParaDesativar);
            LOGGER.info(dietasAtualizadas + " DietaEspecialPedidoAlimento desativados em massa.");

            int pedidosAtualizados = dao.bulkDeactivatePedidoAlimentoByIds(idsPedidosExistentesParaDesativar);
            LOGGER.info(pedidosAtualizados + " PedidoAlimento desativados em massa.");
        } else {
            LOGGER.info("Nenhum pedido ativo existente encontrado para desativar com os critérios fornecidos.");
        }

        dao.salvar(novoPedidoAlimento);
        LOGGER.info("Novo PedidoAlimento salvo com sucesso. ID: " + novoPedidoAlimento.getId());
    }
}