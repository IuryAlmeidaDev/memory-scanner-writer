package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.dao.PedidoAlimentoDAO;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;

import javax.inject.Inject;

public class PedidoAlimentoService {

    @Inject
    private PedidoAlimentoDAO dao;

    public void salvarRegistroRelatorio(
            final PedidoAlimento pedidoAlimento) {
        dao.salvarRelatorio(pedidoAlimento);
    }
}