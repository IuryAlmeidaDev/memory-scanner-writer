package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.MovimentacaoInternaDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroMovimentacaoInterna;
import br.gov.mt.sejudh.sigepen.enumerator.MovimentacaoPassos;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Escolta;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.MovimentacaoInterna;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Servidor;


@Stateless
public class MovimentacaoInternaService {
	
	@Inject
	private MovimentacaoInternaDAO dao;	
	
	@Inject
	private PedidoDAO daoPedido;
	
	@Inject
	private AuditoriaDAO auditDAO;

	@Inject
	private RecuperandoService recuperandoService;
	
	@Inject
	private PedidoService pedidoService;

	public void salvar(MovimentacaoInterna movimentacao) {
		dao.salvar(movimentacao);
		
		for(PedidoTransito pedido : movimentacao.getPedidos()){
			  pedido.setStatus(StatusPedido.VINCULADO);
			  pedido.setMovimentacaoInterna(movimentacao);	
			  daoPedido.salvar(pedido);
		  }		
	}
	
	public void excluir(MovimentacaoInterna movimentacao) {
		MovimentacaoInterna mov = buscaMovimentacaoPorId(movimentacao.getId());
		for(PedidoTransito pedido : mov.getPedidos()){
			  pedido.setMovimentacaoInterna(null);
			  pedido.setStatus(StatusPedido.AUTORIZADO);
			  daoPedido.salvar(pedido);		
		}
		dao.excluir(movimentacao);
	}
	
	public void iniciarMovimentacao(MovimentacaoInterna movimentacao) {
		movimentacao.setStatus(StatusMovimentacao.EM_ANDAMENTO);
		movimentacao.setDataInicio(new Date());
		
		dao.salvar(movimentacao);
		
		for(PedidoTransito pedido : movimentacao.getPedidos()){
			pedido.setStatus(StatusPedido.EM_ANDAMENTO);
			pedido.setDataInicio(movimentacao.getDataInicio());
			
			daoPedido.salvar(pedido);
		}							
	}

	public List<MovimentacaoInterna> buscaPorFiltro(FiltroMovimentacaoInterna filtro, boolean isBuscaRelatorio) {		 
		return dao.buscaPorFiltro(filtro, isBuscaRelatorio);
    }	
		
	public int quantidadeFiltrados(FiltroMovimentacaoInterna filtro){
		return dao.quantidadeFiltrados(filtro);
	}
	
	public MovimentacaoInterna buscaMovimentacaoPorId(long id) {		 
		MovimentacaoInterna movimentacao = dao.buscaMovimentacaoPorId(id);
		
		if (movimentacao != null) {
			movimentacao.setAutorRegistro(auditDAO.autorRegistro(movimentacao, movimentacao.getId()));
			movimentacao.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(movimentacao, movimentacao.getId()));
		}
		
		return movimentacao;
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
	
	public void cancelarMovimentacao(MovimentacaoInterna movimentacao) {
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
	
	public void finalizarMovimentacao(MovimentacaoInterna movimentacao) {
		movimentacao.setStatus(StatusMovimentacao.FINALIZADO);
		movimentacao.setDataConclusao(new Date());		
		dao.salvar(movimentacao);
	}
	
	public void validarMovimentacaoInterna(PedidoTransito pedido, MovimentacaoInterna movimentacao, MovimentacaoPassos passoAtual) throws ValidacaoException {
		if (pedido.getRecuperando().getStatus() != StatusRecuperando.ATIVO) {
			throw new ValidacaoException("Recuperando inativo, remova o pedido " + pedido.getId() + ".");
		}

		switch (passoAtual) {
			case ASSOCIAR_PEDIDO:
				validaLotacao(pedido);

				if (recuperandoPossuiPedidoIncluso(movimentacao.getPedidos(), pedido)) {
					throw new ValidacaoException("Recuperando já possui Pedido adicionado.");
				}
				break;
				
			case INICIAR:
				validaLotacao(pedido);
				
				if (pedidoService.recuperandoEmMovimentacao(pedido.getRecuperando())) {
					throw new ValidacaoException("Recuperando em movimentação, remova o pedido "+pedido.getId()+".");
				}
			
			case SALVAR:
				validaLotacao(pedido);
				
				if (pedido.getStatus() != StatusPedido.AUTORIZADO && movimentacao.getId() != pedido.getMovimentacaoInterna().getId()) {
					throw new ValidacaoException("Pedido " + pedido.getId() + " não está autorizado.");
				}
				break;
				
			case FINALIZAR:
				if (movimentacao.getStatus() != StatusMovimentacao.FINALIZADO_PARCIAL) {
					validaLotacao(pedido);
				}
				
				if (pedido.getStatus() != StatusPedido.CANCELADO && pedido.getStatus() != StatusPedido.FINALIZADO){
					throw new ValidacaoException("Finalize ou cancele o pedido "+ pedido.getId() + ".");
				}
				break;
				
			default:
				break;
		}
	}
	
	private void validaLotacao(PedidoTransito pedido) throws ValidacaoException {
		Estrutura lotacaoAtual = recuperandoService.lotacaoAtual(pedido.getRecuperando());
		
		if (lotacaoAtual.getId() != pedido.getEstruturaOrigem().getId()) {
			throw new ValidacaoException("Recuperando não se encontra mais na mesma estrutura de origem, CANCELE o pedido de trânsito!");
		}
	}
}
