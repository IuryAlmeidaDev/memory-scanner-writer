package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;

import javax.transaction.Transactional;

public class PedidoAlimentoDAO extends BaseDAO<PedidoAlimento, Long> {

    private static final long serialVersionUID = 1L;

    @Transactional
    public void salvarRelatorio(final PedidoAlimento pedido) {
        em.persist(pedido);
    }
}
