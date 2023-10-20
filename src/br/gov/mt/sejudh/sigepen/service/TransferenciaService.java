package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoDAO;
import br.gov.mt.sejudh.sigepen.dao.TransferenciaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTransferencia;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Escolta;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.Transferencia;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;


@Stateless
public class TransferenciaService {
	
	@Inject
	private TransferenciaDAO dao;	
	
	@Inject
	private PedidoDAO daoPedido;
	
	@Inject
	private AuditoriaDAO auditDAO;

	@Interceptors(EJBExcInterceptor.class)
	public void salvar(Transferencia transferencia) {
		dao.salvar(transferencia);
		for(PedidoTransito pedido : transferencia.getPedidos()){
			  pedido.setStatus(StatusPedido.VINCULADO);
			  pedido.setTransferencia(transferencia);	
			  daoPedido.salvar(pedido);
		  }		
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void excluir(Transferencia transferencia) throws ValidacaoException {
		Transferencia transf = buscaTransferenciaPorId(transferencia.getId());
		if(transferencia.getStatus() != StatusMovimentacao.PENDENTE){
			throw new ValidacaoException("Transferência não pode ser excluida!");	 
		}
		for(PedidoTransito pedido : transf.getPedidos()){
			  pedido.setTransferencia(null);
			  pedido.setStatus(StatusPedido.AUTORIZADO);
			  daoPedido.salvar(pedido);		
		}
		dao.excluir(transferencia);
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void iniciarTransferencia(Transferencia transferencia) {
		transferencia.setStatus(StatusMovimentacao.EM_ANDAMENTO);
		transferencia.setDataInicio(new Date());
		
		dao.salvar(transferencia);
		
		final List<PedidoTransito> listaPedidos = daoPedido.buscarPedidosPorTransferencia(transferencia.getId());
		
		for (PedidoTransito pedido : listaPedidos){
			pedido.setStatus(StatusPedido.EM_ANDAMENTO);
			pedido.setDataInicio(transferencia.getDataInicio());
			
			daoPedido.salvar(pedido);
		}		
	}

	public List<Transferencia> buscaPorFiltro(FiltroTransferencia filtro, boolean isBuscaRelatorio) {		 
		return dao.buscaPorFiltro(filtro, isBuscaRelatorio);
    }	
	
	public int quantidadeFiltrados(FiltroTransferencia filtro){
		return dao.quantidadeFiltrados(filtro);
	}
	
	public Transferencia buscaTransferenciaPorId(long id) {		 
		Transferencia transferencia = dao.buscaTransfernciaPorId(id);
		
		if (transferencia != null) {
			transferencia.setAutorRegistro(auditDAO.autorRegistro(transferencia, transferencia.getId()));
			transferencia.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(transferencia, transferencia.getId()));
		}
		
		return transferencia;
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
	
	@Interceptors(EJBExcInterceptor.class)
	public void cancelarTransferencia(Transferencia transferencia) throws ValidacaoException {
		if(transferencia.getStatus() == StatusMovimentacao.PENDENTE || transferencia.getStatus() == StatusMovimentacao.EM_ANDAMENTO){			
		
		for(PedidoTransito pedido : transferencia.getPedidos()){
			pedido.setResultado(transferencia.getResultado());
			pedido.setStatus(StatusPedido.CANCELADO);
			pedido.setDataCancelamento(new Date());
			daoPedido.salvar(pedido);	
		}
		transferencia.setStatus(StatusMovimentacao.CANCELADO);
		transferencia.setDataCancelamento(new Date());		
		dao.salvar(transferencia);
		}else{
			throw new ValidacaoException("Transferência não pode ser finalizada!");	 
		}
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void finalizarTransferencia(Transferencia transferencia) {
		transferencia.setStatus(StatusMovimentacao.FINALIZADO);
		transferencia.setDataConclusao(new Date());		
		dao.salvar(transferencia);
	}
	
	public boolean isTranferenciaEmAndamento(final long transferenciaId){
		return dao.isTranferenciaEmAndamento(transferenciaId);
	}
}
