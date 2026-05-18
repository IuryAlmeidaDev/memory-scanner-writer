package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroHistoricoPedidoAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoComboDTO;

import java.util.List;
import java.util.Collections;

import javax.transaction.Transactional;

public class HistoricoPedidoAlimentoDAO extends BaseDAO<PedidoAlimento, Long> {

    private static final long serialVersionUID = 1L;

    public List<PedidoAlimento> buscarPorFiltroHistorico(FiltroHistoricoPedidoAlimento filtro) {
        return buscaPorFiltro(filtro, false);
    }

    public int contarPorFiltroHistorico(FiltroHistoricoPedidoAlimento filtro) {
        return quantidadeFiltrados(filtro);
    }

    public List<ContratoComboDTO> listarContratosParaCombo() {
        return listarContratosParaCombo(true, null);
    }

    public List<ContratoComboDTO> listarContratosParaCombo(boolean listarTodasUnidades, Long unidadeId) {
        if (!listarTodasUnidades && unidadeId == null) {
            return Collections.emptyList();
        }

        StringBuilder jpql = new StringBuilder();
        jpql.append(
                "SELECT distinct new br.gov.mt.sejudh.sigepen.model.dto.ContratoComboDTO(" +
                "  c.id, " +
                "  c.numContrato" +
                ") " +
                "FROM Contrato c " +
                "LEFT JOIN c.unidades u " +
                "WHERE c.status = :status ");

        if (!listarTodasUnidades && unidadeId != null) {
            jpql.append("AND u.id = :unidadeId ");
        }

        jpql.append("ORDER BY c.numContrato");

        javax.persistence.TypedQuery<ContratoComboDTO> query = em.createQuery(jpql.toString(), ContratoComboDTO.class)
                .setParameter("status", Status.ATIVO);

        if (!listarTodasUnidades && unidadeId != null) {
            query.setParameter("unidadeId", unidadeId);
        }

        return query.getResultList();
    }
    
    public PedidoAlimento buscarPorIdComItens(Long id) {
        List<PedidoAlimento> result = em.createQuery(
                "SELECT DISTINCT p FROM PedidoAlimento p " +
                "LEFT JOIN FETCH p.itensPedido " +
                "WHERE p.id = :id",
                PedidoAlimento.class
        )
        .setParameter("id", id)
        .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
    
    @Transactional
    public void alterarStatus(Long id, Status novoStatus) {
        PedidoAlimento pedido = buscarPorId(id);
        if (pedido != null) {
            pedido.setStatus(novoStatus);
            em.merge(pedido);
        }
    }
}
