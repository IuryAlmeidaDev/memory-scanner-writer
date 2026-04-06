package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.dao.HistoricoPedidoAlimentoDAO;
import br.gov.mt.sejudh.sigepen.dao.UnidadePrisionalDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroHistoricoPedidoAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoComboDTO;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.List;
import java.util.logging.Logger;

public class HistoricoPedidoAlimentoService {

    private static final Logger LOGGER =
            Logger.getLogger(HistoricoPedidoAlimentoService.class.getName());

    @Inject
    private HistoricoPedidoAlimentoDAO dao;
    
    @Inject
    private UnidadePrisionalDAO upDao;

    public List<PedidoAlimento> buscarPorFiltroHistorico(FiltroHistoricoPedidoAlimento filtro) {
        LOGGER.info("Buscando histórico de pedidos de alimento.");
        return dao.buscarPorFiltroHistorico(filtro);
    }

    public int contarPorFiltroHistorico(FiltroHistoricoPedidoAlimento filtro) {
        return dao.contarPorFiltroHistorico(filtro);
    }

    public PedidoAlimento buscarPorId(Long id) {
        LOGGER.info("Buscando PedidoAlimento por ID: " + id);
        return dao.buscarPorId(id);
    }

    public List<ContratoComboDTO> listarContratosParaCombo() {
        return dao.listarContratosParaCombo();
    }
    
    @Transactional
    public PedidoAlimento buscarPorIdComItens(Long id) {
        LOGGER.info("Buscando PedidoAlimento com itens para relatório. ID: " + id);
        PedidoAlimento pedido = dao.buscarPorIdComItens(id);

        if (pedido != null) {
            dao.initialize(pedido.getDietasEspeciais());
        }

        return pedido;
    }
    
    @Transactional
    public void finalizarPedido(Long idPedido) {
        try {
            PedidoAlimento pedido = dao.buscarPorId(idPedido);
            
            if (pedido == null) {
                throw new RuntimeException("Pedido não encontrado.");
            }
            
            if (!Status.PENDENTE.equals(pedido.getStatus())) {
                throw new RuntimeException("Apenas pedidos PENDENTES podem ser finalizados.");
            }

            dao.alterarStatus(idPedido, Status.ATIVO);
            
            LOGGER.info("Pedido " + idPedido + " finalizado com sucesso.");
        } catch (Exception e) {
            LOGGER.severe("Erro ao finalizar pedido: " + e.getMessage());
            throw e; 
        }
    }
    
    public String buscarNomeUnidadePorId(Long idUnidade) {
    	UnidadePrisional up = upDao.buscaSimplesPorId(idUnidade);
    	return up != null ? up.getNome() : "";
    }
    
    
    
   
}