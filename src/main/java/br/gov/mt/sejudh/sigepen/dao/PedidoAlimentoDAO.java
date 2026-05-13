package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.Status; // Importar a enum Status

import javax.persistence.TypedQuery;

import java.util.Arrays;
import java.util.List;

public class PedidoAlimentoDAO extends BaseDAO<PedidoAlimento, Long> {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_DIA_PEDIDO = 10;

    /**
     * Verifica se já existe um pedido ATIVO para a mesma data, tipo de refeição,
     * unidade prisional e contrato.
     *
     * @param dataPedido Data do pedido
     * @param idTipoRefeicao ID do tipo de refeição
     * @param idUnidadePrisional ID da unidade prisional
     * @param idContrato ID do contrato
     * @return true se já existir um pedido ATIVO com os parâmetros fornecidos, caso contrário, false.
     */
    /**
     * Verifica se já existe um pedido ATIVO (assinado) para a mesma data,
     * tipo de refeição, unidade prisional e contrato.
     */
    public boolean existePedidoAtivo(String dataPedido, Long idTipoRefeicao,
            Long idUnidadePrisional, Long idContrato) {
        TypedQuery<Boolean> query = em.createQuery(
                "SELECT CASE WHEN (COUNT(p) > 0) THEN true ELSE false END " +
                "FROM PedidoAlimento p " +
                "WHERE p.dataPedido LIKE :dataPedido " +
                "AND p.idTipoRefeicao = :idTipoRefeicao " +
                "AND p.idUnidadePrisional = :idUnidadePrisional " +
                "AND p.idContrato = :idContrato " +
                "AND p.status = :statusAtivo",
                Boolean.class);
        query.setParameter("dataPedido", dataPedidoDia(dataPedido));
        query.setParameter("idTipoRefeicao", idTipoRefeicao);
        query.setParameter("idUnidadePrisional", idUnidadePrisional);
        query.setParameter("idContrato", idContrato);
        query.setParameter("statusAtivo", Status.ATIVO);
        return query.getSingleResult();
    }

    public boolean existePedidoAtivoPorTipoRefeicaoEUnidade(String dataPedido, Long idTipoRefeicao,
            Long idUnidadePrisional, Long idContrato) {
        TypedQuery<Boolean> query = em.createQuery(
                "SELECT CASE WHEN (COUNT(p) > 0) THEN true ELSE false END " +
                "FROM PedidoAlimento p " +
                "WHERE p.dataPedido LIKE :dataPedido " +
                "AND p.idTipoRefeicao = :idTipoRefeicao " +
                "AND p.idUnidadePrisional = :idUnidadePrisional " +
                "AND p.idContrato = :idContrato " +
                "AND p.status = :statusAtivo",
                Boolean.class);
        query.setParameter("dataPedido", dataPedidoDia(dataPedido));
        query.setParameter("idTipoRefeicao", idTipoRefeicao);
        query.setParameter("idUnidadePrisional", idUnidadePrisional);
        query.setParameter("idContrato", idContrato);
        query.setParameter("statusAtivo", Status.ATIVO);
        return query.getSingleResult();
    }

    public List<Long> findPedidosEmAbertoPorTipoRefeicaoEUnidade(String dataPedido, Long idTipoRefeicao,
            Long idUnidadePrisional, Long idContrato) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT p.id FROM PedidoAlimento p " +
                "WHERE p.dataPedido LIKE :dataPedido " +
                "AND p.idTipoRefeicao = :idTipoRefeicao " +
                "AND p.idUnidadePrisional = :idUnidadePrisional " +
                "AND p.idContrato = :idContrato " +
                "AND p.status IN (:statusesEmAberto)",
                Long.class);
        query.setParameter("dataPedido", dataPedidoDia(dataPedido));
        query.setParameter("idTipoRefeicao", idTipoRefeicao);
        query.setParameter("idUnidadePrisional", idUnidadePrisional);
        query.setParameter("idContrato", idContrato);
        query.setParameter("statusesEmAberto", Arrays.asList(Status.PENDENTE, Status.ATIVO));
        return query.getResultList();
    }

    public List<Long> findPedidosPendentesPorTipoRefeicaoEUnidade(String dataPedido, Long idTipoRefeicao,
            Long idUnidadePrisional, Long idContrato) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT p.id FROM PedidoAlimento p " +
                "WHERE p.dataPedido LIKE :dataPedido " +
                "AND p.idTipoRefeicao = :idTipoRefeicao " +
                "AND p.idUnidadePrisional = :idUnidadePrisional " +
                "AND p.idContrato = :idContrato " +
                "AND p.status = :statusPendente",
                Long.class);
        query.setParameter("dataPedido", dataPedidoDia(dataPedido));
        query.setParameter("idTipoRefeicao", idTipoRefeicao);
        query.setParameter("idUnidadePrisional", idUnidadePrisional);
        query.setParameter("idContrato", idContrato);
        query.setParameter("statusPendente", Status.PENDENTE);
        return query.getResultList();
    }

    public PedidoAlimento salvarComFlush(PedidoAlimento pedidoAlimento) {
        PedidoAlimento salvo = salvar(pedidoAlimento);
        em.flush();
        return salvo;
    }

    /**
     * Busca IDs de pedidos PENDENTE ou ATIVO para inativar ao confirmar substituição
     * de um pedido assinado.
     */
    public List<Long> findPedidosParaInativarAoAssinar(String dataPedido,
            Long idTipoRefeicao, Long idUnidadePrisional, Long idContrato) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT p.id FROM PedidoAlimento p " +
                "WHERE p.dataPedido LIKE :dataPedido " +
                "AND p.idTipoRefeicao = :idTipoRefeicao " +
                "AND p.idUnidadePrisional = :idUnidadePrisional " +
                "AND p.idContrato = :idContrato " +
                "AND p.status IN (:statuses)",
                Long.class);
        query.setParameter("dataPedido", dataPedidoDia(dataPedido));
        query.setParameter("idTipoRefeicao", idTipoRefeicao);
        query.setParameter("idUnidadePrisional", idUnidadePrisional);
        query.setParameter("idContrato", idContrato);
        query.setParameter("statuses", Arrays.asList(Status.PENDENTE, Status.ATIVO));
        return query.getResultList();
    }

    /**
     * Busca os IDs de todos os PedidoAlimento ativos que correspondem aos critérios fornecidos.
     * Estes são os pedidos que precisam ser desativados.
     *
     * @param dataPedido Data do pedido
     * @param idTipoRefeicao ID do tipo de refeição
     * @param idUnidadePrisional ID da unidade prisional
     * @param idContrato ID do contrato
     * @return Uma lista de IDs de PedidoAlimento ativos que correspondem aos critérios.
     */
    public List<Long> findActivePedidoAlimentoIdsByCriteria(String dataPedido, Long idTipoRefeicao, Long idUnidadePrisional, Long idContrato) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT p.id FROM PedidoAlimento p " +
                        "WHERE p.dataPedido LIKE :dataPedido " +
                        "AND p.idTipoRefeicao = :idTipoRefeicao " +
                        "AND p.idUnidadePrisional = :idUnidadePrisional " +
                        "AND p.idContrato = :idContrato " +
                        "AND p.status IN (:statusesEmAberto)",
                Long.class
        );
        query.setParameter("dataPedido", dataPedidoDia(dataPedido));
        query.setParameter("idTipoRefeicao", idTipoRefeicao);
        query.setParameter("idUnidadePrisional", idUnidadePrisional);
        query.setParameter("idContrato", idContrato);
        query.setParameter("statusesEmAberto", Arrays.asList(Status.PENDENTE, Status.ATIVO));
        return query.getResultList();
    }

    private String dataPedidoDia(String dataPedido) {
        if (dataPedido == null || dataPedido.length() < TAMANHO_DIA_PEDIDO) {
            throw new IllegalArgumentException("dataPedido deve iniciar com dd/MM/yyyy.");
        }

        String dia = dataPedido.substring(0, TAMANHO_DIA_PEDIDO);
        if (!isDiaPedidoValido(dia)) {
            throw new IllegalArgumentException("dataPedido deve iniciar com dd/MM/yyyy.");
        }
        return dia + "%";
    }

    private boolean isDiaPedidoValido(String dia) {
        return Character.isDigit(dia.charAt(0))
                && Character.isDigit(dia.charAt(1))
                && dia.charAt(2) == '/'
                && Character.isDigit(dia.charAt(3))
                && Character.isDigit(dia.charAt(4))
                && dia.charAt(5) == '/'
                && Character.isDigit(dia.charAt(6))
                && Character.isDigit(dia.charAt(7))
                && Character.isDigit(dia.charAt(8))
                && Character.isDigit(dia.charAt(9));
    }

    /**
     * Desativa em massa PedidoAlimento pelo seus IDs.
     *
     * @param pedidoAlimentoIds Lista de IDs de PedidoAlimento a serem desativados.
     * @return O número de entidades PedidoAlimento atualizadas.
     */
    public int bulkDeactivatePedidoAlimentoByIds(List<Long> pedidoAlimentoIds) {
        if (pedidoAlimentoIds == null || pedidoAlimentoIds.isEmpty()) {
            return 0;
        }
        return em.createQuery(
                        "UPDATE PedidoAlimento p SET p.status = :statusInativo WHERE p.id IN (:ids)"
                )
                .setParameter("statusInativo", Status.INATIVO)
                .setParameter("ids", pedidoAlimentoIds)
                .executeUpdate();
    }

    /**
     * Desativa em massa ItemPedidoAlimento associados aos PedidoAlimento pelos IDs dos PedidoAlimento.
     *
     * @param pedidoAlimentoIds Lista de IDs de PedidoAlimento cujos itens devem ser desativados.
     * @return O número de entidades ItemPedidoAlimento atualizadas.
     */
    public int bulkDeactivateItemPedidoAlimentoByPedidoIds(List<Long> pedidoAlimentoIds) {
        if (pedidoAlimentoIds == null || pedidoAlimentoIds.isEmpty()) {
            return 0;
        }
        return em.createQuery(
                        "UPDATE ItemPedidoAlimento i SET i.status = :statusInativo WHERE i.pedidoAlimento.id IN (:ids)"
                )
                .setParameter("statusInativo", Status.INATIVO)
                .setParameter("ids", pedidoAlimentoIds)
                .executeUpdate();
    }

    /**
     * Desativa em massa DietaEspecialPedidoAlimento associados aos PedidoAlimento pelos IDs dos PedidoAlimento.
     *
     * @param pedidoAlimentoIds Lista de IDs de PedidoAlimento cujas dietas devem ser desativadas.
     * @return O número de entidades DietaEspecialPedidoAlimento atualizadas.
     */
    public int bulkDeactivateDietaEspecialPedidoAlimentoByPedidoIds(List<Long> pedidoAlimentoIds) {
        if (pedidoAlimentoIds == null || pedidoAlimentoIds.isEmpty()) {
            return 0;
        }
        return em.createQuery(
                        "UPDATE DietaEspecialPedidoAlimento d SET d.status = :statusInativo WHERE d.pedidoAlimento.id IN (:ids)"
                )
                .setParameter("statusInativo", Status.INATIVO)
                .setParameter("ids", pedidoAlimentoIds)
                .executeUpdate();
    }
}
