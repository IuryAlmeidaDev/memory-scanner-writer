package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.dao.PedidoAlimentoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger; 

public class PedidoAlimentoService {

    @Inject
    private PedidoAlimentoDAO dao;

    private static final Logger LOGGER = Logger.getLogger(PedidoAlimentoService.class.getName());

    public boolean verificarExistenciaPedidoAtivo(String dataPedido,
            Long idTipoRefeicao, Long idUnidadePrisional, Long idContrato) {
        return dao.existePedidoAtivo(dataPedido, idTipoRefeicao,
                idUnidadePrisional, idContrato);
    }

    @Transactional
    public void salvarPedidoPendente(PedidoAlimento novoPedido) {
        novoPedido.setStatus(Status.PENDENTE);
        dao.salvar(novoPedido);
        LOGGER.info("Pedido PENDENTE salvo. ID: " + novoPedido.getId());
    }
    
    @Transactional
    public void salvarPedidoAssinadoSubstituindo(PedidoAlimento novoPedido) {
        List<Long> idsParaInativar = dao.findPedidosParaInativarAoAssinar(
                novoPedido.getDataPedido(),
                novoPedido.getIdTipoRefeicao(),
                novoPedido.getIdUnidadePrisional(),
                novoPedido.getIdContrato());

        if (!idsParaInativar.isEmpty()) {
            LOGGER.info("Inativando " + idsParaInativar.size()
                    + " pedido(s) anteriores. IDs: " + idsParaInativar);

            dao.bulkDeactivateItemPedidoAlimentoByPedidoIds(idsParaInativar);
            dao.bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(idsParaInativar);
            dao.bulkDeactivatePedidoAlimentoByIds(idsParaInativar);
        }

        novoPedido.setStatus(Status.ATIVO);
        dao.salvar(novoPedido);
        LOGGER.info("Pedido ATIVO (assinado) salvo. ID: " + novoPedido.getId());
    }

    @Transactional
    public void salvarPrimeiroPedidoAssinado(PedidoAlimento novoPedido) {
        List<Long> idsPendentes = dao.findActivePedidoAlimentoIdsByCriteria(
                novoPedido.getDataPedido(),
                novoPedido.getIdTipoRefeicao(),
                novoPedido.getIdUnidadePrisional(),
                novoPedido.getIdContrato());

        if (!idsPendentes.isEmpty()) {
            dao.bulkDeactivateItemPedidoAlimentoByPedidoIds(idsPendentes);
            dao.bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(idsPendentes);
            dao.bulkDeactivatePedidoAlimentoByIds(idsPendentes);
        }

        novoPedido.setStatus(Status.ATIVO);
        dao.salvar(novoPedido);
        LOGGER.info("Primeiro pedido ATIVO (assinado) salvo. ID: " + novoPedido.getId());
    }

    @Transactional
    public void salvarPedidoAlimento(final PedidoAlimento novoPedidoAlimento) {
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