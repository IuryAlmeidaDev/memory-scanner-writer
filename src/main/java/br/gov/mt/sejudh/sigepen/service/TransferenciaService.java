package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import br.gov.mt.sejudh.sigepen.model.dto.TransferenciaPedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;


@Stateless
public class TransferenciaService {
	
	@Inject
	private TransferenciaDAO dao;	
	
	@Inject
	private PedidoDAO daoPedido;
	
	@Inject
	private AuditoriaDAO auditDAO;

	@Inject
	private DietaEspecialService dietaEspecialService;

	@Interceptors(EJBExcInterceptor.class)
	public void salvar(Transferencia transferencia) {
		dao.salvar(transferencia);
		List<Long> idsPedidos = new ArrayList<>();
		
		for(TransferenciaPedidoTransitoDTO transfPedidoDTO : transferencia.getListTransferenciaPedidoTransitoDTO()){
			idsPedidos.add(transfPedidoDTO.getNr());
		  }		
		
		this.daoPedido.atualizaPedidosTransitoVinculandoComTransferencia(idsPedidos, StatusPedido.VINCULADO, transferencia.getId());
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
		
		daoPedido.atualizaPedidosParaEmAndamentoTransitoPorTransferenciaId(transferencia.getId(), StatusPedido.EM_ANDAMENTO, transferencia.getDataInicio());
		suspenderDietasDosRecuperandos(transferencia);
		dao.salvar(transferencia);
	
	}

	public List<Transferencia> buscaPorFiltro(FiltroTransferencia filtro, boolean isBuscaRelatorio) {		 
		return dao.buscaPorFiltro(filtro, isBuscaRelatorio);
    }	
	
	public List<Transferencia> buscaPorFiltroRelatorio(FiltroTransferencia filtro, boolean isBuscaRelatorio) {		 
		return dao.buscaPorFiltroRelatorio(filtro, isBuscaRelatorio);
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
	
	
	public Transferencia buscaTransferenciaImprimirRegistro(long id) {		 
		Transferencia transferencia = dao.buscaTransfernciaPorIdIniciandoObjetos(id);
		
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
	
	public boolean isRecuperandoPossuiPedidoIncluso(List<TransferenciaPedidoTransitoDTO> pedidos, PedidoTransito pedido){
		for(TransferenciaPedidoTransitoDTO p : pedidos){
			if(p.getMatricula()== pedido.getRecuperando().getId()){
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
			boolean transferenciaEmAndamento = transferencia.getStatus() == StatusMovimentacao.EM_ANDAMENTO;
			Date dataCancelamento = new Date();

			transferencia.setStatus(StatusMovimentacao.CANCELADO);
			transferencia.setDataCancelamento(dataCancelamento);
	
			daoPedido.atualizaPedidosParaCanceladoTransitoPorTransferenciaId(transferencia.getId(), StatusPedido.CANCELADO, dataCancelamento);
			if (transferenciaEmAndamento) {
				reativarDietasDosRecuperandos(transferencia);
			}
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

    public int countMovimentacaoExterna(FiltroTransferencia filtro) {
		return quantidadeFiltrados(filtro);
    }

	private void suspenderDietasDosRecuperandos(Transferencia transferencia) {
		for (Long recuperandoId : buscarIdsRecuperandosDaTransferencia(transferencia)) {
			dietaEspecialService.suspenderDietasAtivasDoRecuperando(recuperandoId);
		}
	}

	private void reativarDietasDosRecuperandos(Transferencia transferencia) {
		for (Long recuperandoId : buscarIdsRecuperandosDaTransferencia(transferencia)) {
			dietaEspecialService.reativarDietasSuspensasDoRecuperando(recuperandoId);
		}
	}

	private Set<Long> buscarIdsRecuperandosDaTransferencia(Transferencia transferencia) {
		Set<Long> ids = new HashSet<>();

		if (transferencia.getListTransferenciaPedidoTransitoDTO() != null
				&& !transferencia.getListTransferenciaPedidoTransitoDTO().isEmpty()) {
			for (TransferenciaPedidoTransitoDTO pedido : transferencia.getListTransferenciaPedidoTransitoDTO()) {
				if (pedido.getMatricula() != null) {
					ids.add(pedido.getMatricula());
				}
			}
			return ids;
		}

		List<PedidoTransito> pedidos = daoPedido.buscarPedidosPorTransferencia(transferencia.getId());
		if (pedidos == null) {
			return ids;
		}

		for (PedidoTransito pedido : pedidos) {
			if (pedido.getRecuperando() != null) {
				ids.add(pedido.getRecuperando().getId());
			}
		}

		return ids;
	}
}
