package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.MovimentacaoExternaDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroMovimentacaoExterna;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.model.Escolta;
import br.gov.mt.sejudh.sigepen.model.MovimentacaoExterna;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Servidor;


@Stateless
public class MovimentacaoExternaService {
	
	@Inject
	private MovimentacaoExternaDAO dao;	
	
	@Inject
	private PedidoDAO daoPedido;
	
	@Inject
	private AuditoriaDAO auditDAO;

	public void salvar(MovimentacaoExterna movimentacao) {
		dao.salvar(movimentacao);
		for(PedidoTransito pedido : movimentacao.getPedidos()){
			  pedido.setStatus(StatusPedido.VINCULADO);
			  pedido.setMovimentacaoExterna(movimentacao);	
			  daoPedido.salvar(pedido);
		  }		
	}
	
	public void salvarMovimentacao(MovimentacaoExterna movimentacao) {
		dao.salvar(movimentacao);
		  		
	}
	
	public void excluir(MovimentacaoExterna movimentacao) {
		MovimentacaoExterna mov = buscaMovimentacaoPorId(movimentacao.getId());
		for(PedidoTransito pedido : mov.getPedidos()){
			  pedido.setMovimentacaoExterna(null);
			  pedido.setStatus(StatusPedido.AUTORIZADO);
			  daoPedido.salvar(pedido);		
		}
		dao.excluir(movimentacao);
	}
	
	public void iniciarMovimentacao(MovimentacaoExterna movimentacao) {
		movimentacao.setStatus(StatusMovimentacao.EM_ANDAMENTO);
		movimentacao.setDataInicio(new Date());
		
		dao.salvar(movimentacao);
		
		for(PedidoTransito pedido : movimentacao.getPedidos()){
			pedido.setStatus(StatusPedido.EM_ANDAMENTO);
			pedido.setDataInicio(movimentacao.getDataInicio());
			daoPedido.salvar(pedido);
		}							
	}

	public List<MovimentacaoExterna> buscaPorFiltro(FiltroMovimentacaoExterna filtro, boolean isBuscaRelatorio) {		 
		return dao.buscaPorFiltro(filtro, isBuscaRelatorio);
    }	
	
	public int quantidadeFiltrados(FiltroMovimentacaoExterna filtro){
		return dao.quantidadeFiltrados(filtro);
	}
	
	public MovimentacaoExterna buscaMovimentacaoPorId(long id) {		 
		MovimentacaoExterna movimentacaoExterna = dao.buscaMovimentacaoPorId(id);
		
		if (movimentacaoExterna != null) {
			movimentacaoExterna.setAutorRegistro(auditDAO.autorRegistro(movimentacaoExterna, movimentacaoExterna.getId()));
			movimentacaoExterna.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(movimentacaoExterna, movimentacaoExterna.getId()));
		}
		
		return movimentacaoExterna;
    }
	
	public boolean recuperandoPossuiPedidoIncluso(List<PedidoTransito> pedidos, PedidoTransito pedido){
		for(PedidoTransito p : pedidos){
			if(p.getRecuperando().getId()== pedido.getRecuperando().getId()){
				return true;
			}
		}
		return false;
	}
	
	public boolean servidorInclusoEscolta(List<Escolta> escolta, Servidor servidor){
		for(Escolta e : escolta){
			if(e.getServidor() != null){
			if(e.getServidor().getId()== servidor.getId()){
				return true;
			}
			}
		}
		return false;
	}
	
	public void cancelarMovimentacao(MovimentacaoExterna movimentacao) {
		for(PedidoTransito pedido : movimentacao.getPedidos()){
			pedido.setResultado(movimentacao.getResultado());
			pedido.setStatus(StatusPedido.CANCELADO);
			pedido.setDataCancelamento(new Date());
			daoPedido.salvar(pedido);	
		}
		movimentacao.setStatus(StatusMovimentacao.CANCELADO);
		movimentacao.setDataCancelamento(new Date());		
		dao.salvar(movimentacao);
	}
	
	public void finalizarMovimentacao(MovimentacaoExterna movimentacao) {
		movimentacao.setStatus(StatusMovimentacao.FINALIZADO);
		movimentacao.setDataConclusao(new Date());		
		dao.salvar(movimentacao);
	}
}
