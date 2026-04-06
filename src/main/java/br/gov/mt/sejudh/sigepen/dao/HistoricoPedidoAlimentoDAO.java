package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroHistoricoPedidoAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoComboDTO;

import java.util.List;

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
        return em.createQuery(
                "SELECT new br.gov.mt.sejudh.sigepen.model.dto.ContratoComboDTO(" +
                "  c.id, " +
                "  c.numContrato" +
                ") " +
                "FROM Contrato c " +
                "WHERE c.status = :status " +
                "ORDER BY c.numContrato",
                ContratoComboDTO.class
        )
        .setParameter("status", Status.ATIVO)
        .getResultList();
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