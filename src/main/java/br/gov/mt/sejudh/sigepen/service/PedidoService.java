package br.gov.mt.sejudh.sigepen.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.MovimentacaoExternaDAO;
import br.gov.mt.sejudh.sigepen.dao.MovimentacaoInternaDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.TransferenciaDAO;
import br.gov.mt.sejudh.sigepen.dao.VisitanteDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.enumerator.MovimentacaoPassos;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.enumerator.TipoMovimentacao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;


@Stateless
public class PedidoService {
	
	@Inject
	private PedidoDAO dao;
	
	@Inject
	private MovimentacaoInternaDAO movIntDao;
	
	@Inject
	private MovimentacaoExternaDAO movExtDao;
	
	@Inject
	private TransferenciaDAO transfDao;	
	
	@Inject
	private RecuperandoDAO recuperandoDao;
	
	@Inject
	private AuditoriaDAO auditDAO; 

	@Inject
	private RecuperandoService recuperandoService;
	
	@Inject
	private VisitanteDAO visitanteDao;
	
	@Inject
	private MovimentacaoInternaService movtoInternaService;

	@Interceptors(EJBExcInterceptor.class)
	public void salvar(PedidoTransito pedido) {		
		dao.salvar(pedido);						
	}
	
	public void excluir(PedidoTransitoDTO pedido) {
		PedidoTransito ped = buscaPedidoPorId(pedido.getId());
		dao.excluir(ped);
	}		

	public List<PedidoTransitoDTO> buscaPorFiltro(FiltroPedTransito filtro, boolean isBuscaRelatorio) {		 
		return dao.buscaPorFiltro(filtro, isBuscaRelatorio);
    }
	
	public PedidoTransito buscaPedidoPorId(long id) {		 
		PedidoTransito pedidoTransito = dao.buscarPedidoCompletoPorId(id);
		
		if (pedidoTransito != null) {
			pedidoTransito.setAutorRegistro(auditDAO.autorRegistro(pedidoTransito, pedidoTransito.getId()));
			pedidoTransito.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(pedidoTransito, pedidoTransito.getId()));
		}
		
		return pedidoTransito;
    }

	public PedidoTransito buscarPedidoTransferenciaPorId(long id) {
		PedidoTransito pedidoTransito = dao.buscarPedidoTransferenciaPorId(id);

		if (pedidoTransito != null) {
			pedidoTransito.setAutorRegistro(auditDAO.autorRegistro(pedidoTransito, pedidoTransito.getId()));
			pedidoTransito.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(pedidoTransito, pedidoTransito.getId()));
		}

		return pedidoTransito;
    }

	public PedidoTransito buscarPedidoTransferenciaImpressaoPorId(long id) {
		PedidoTransito pedidoTransito = dao.buscarPedidoTransferenciaImpressaoPorId(id);

		if (pedidoTransito != null) {
			pedidoTransito.setAutorRegistro(auditDAO.autorRegistro(pedidoTransito, pedidoTransito.getId()));
			pedidoTransito.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(pedidoTransito, pedidoTransito.getId()));
		}

		return pedidoTransito;
    }

	public PedidoTransito buscaPedidoTransitoPorId(long id) {
		PedidoTransito pedidoTransito = dao.buscaPedidoTransitoPorId(id);

		if (pedidoTransito != null) {
			
			if(pedidoTransito.getRecuperando() != null) {
				Hibernate.initialize(pedidoTransito.getRecuperando().getPessoa());
			}
			
			pedidoTransito.setAutorRegistro(auditDAO.autorRegistro(pedidoTransito, pedidoTransito.getId()));
			pedidoTransito.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(pedidoTransito, pedidoTransito.getId()));
		}

		return pedidoTransito;
    }

	public PedidoTransito buscaPedidoTransitoImpressaoPorId(long id) {
		PedidoTransito pedidoTransito = dao.buscaPedidoTransitoImpressaoPorId(id);

		if (pedidoTransito != null) {
			
			if(pedidoTransito.getRecuperando() != null) {
				Hibernate.initialize(pedidoTransito.getRecuperando().getPessoa());
			}
			pedidoTransito.setAutorRegistro(auditDAO.autorRegistro(pedidoTransito, pedidoTransito.getId()));
			pedidoTransito.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(pedidoTransito, pedidoTransito.getId()));
		}

		return pedidoTransito;
    }

	public PedidoTransito buscaPedidoRemanejamentoPorId(long id) {
		PedidoTransito pedidoTransito = dao.buscaPedidoRemanejamentoPorId(id);

		if (pedidoTransito != null) {
			pedidoTransito.setAutorRegistro(auditDAO.autorRegistro(pedidoTransito, pedidoTransito.getId()));
			pedidoTransito.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(pedidoTransito, pedidoTransito.getId()));
		}

		return pedidoTransito;
    }

	public PedidoTransito buscarPedidoPorId(long id, TipoMovimentacao tipoMovimentacao) {
		PedidoTransito pedidoTransito = dao.buscarPedidoPorId(id, tipoMovimentacao);

		if (pedidoTransito != null) {
			pedidoTransito.setAutorRegistro(auditDAO.autorRegistro(pedidoTransito, pedidoTransito.getId()));
			pedidoTransito.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(pedidoTransito, pedidoTransito.getId()));
		}

		return pedidoTransito;
    }
	
	public PedidoTransito buscarPedidoPorIdComPassagens(long id, TipoMovimentacao tipoMovimentacao) {
		PedidoTransito pedidoTransito = dao.buscarPedidoPorIdComPassagens(id, tipoMovimentacao);


		return pedidoTransito;
    }
		
	public int quantidadeFiltrados(FiltroPedTransito filtro){
		return dao.quantidadeFiltrados(filtro);
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void autorizarPedido(PedidoTransito pedido, boolean autorizado) {
		if(autorizado){
			pedido.setStatus(StatusPedido.AUTORIZADO);	
		}else{
			pedido.setStatus(StatusPedido.NAO_AUTORIZADO);
		}		
		pedido.setDataAutorizacao(new Date());
		dao.salvar(pedido);
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void cancelarPedido(PedidoTransito pedido) {
		pedido.setStatus(StatusPedido.CANCELADO);
		pedido.setDataCancelamento(new Date());
		dao.salvar(pedido);
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void cancelarPedidoMovInt(PedidoTransito pedido) {
		pedido.setStatus(StatusPedido.CANCELADO);
		pedido.setDataCancelamento(new Date());
		dao.salvar(pedido);
		pedido.getMovimentacaoInterna().setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);
		movIntDao.salvar(pedido.getMovimentacaoInterna());
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void cancelarPedidoMovExt(PedidoTransito pedido) {
		pedido.setStatus(StatusPedido.CANCELADO);
		pedido.setDataCancelamento(new Date());
		dao.salvar(pedido);
		pedido.getMovimentacaoExterna().setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);
		movExtDao.salvar(pedido.getMovimentacaoExterna());
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void cancelarPedidoTransferencia(PedidoTransito pedido) {
		pedido.setStatus(StatusPedido.CANCELADO);
		pedido.setDataCancelamento(new Date());
		dao.cancelar(pedido);
		pedido.getTransferencia().setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);
		transfDao.cancelar(pedido.getTransferencia());
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void finalizarPedidoMovInt(PedidoTransito pedido) throws ValidacaoException {
		// validar data fim maior que data inicio
		Date dataFim = new Date();
		if (pedido.getRecuperando().getPassagemAtual().getLotacaoAtual().getDataEntrada().after(dataFim)) {
			throw new ValidacaoException("Data de entrada maior que a Data de Saída!");			
		}
		
		if (movIntDao.isPedidoFinalizadoOuCancelado(pedido.getId())) {
			throw new ValidacaoException("O pedido " + pedido.getId() + " já está finalizado.");
		}
		
		if(pedido.getTipoTransito().equals(TipoTransito.INTERNA)) {
			movtoInternaService.validarMovimentacaoInterna(pedido, pedido.getMovimentacaoInterna(), MovimentacaoPassos.FINALIZAR, true);
		}

		
		pedido.getRecuperando().getPassagemAtual().getLotacaoAtual().setDataSaida(dataFim);
		PassagemLotacao novaLotacao = new PassagemLotacao(pedido.getRecuperando().getPassagemAtual(), TipoTransito.INTERNA);
		novaLotacao.setEstrutura(pedido.getEstruturaDestino());
		novaLotacao.setDataEntrada(new Date());
		pedido.getRecuperando().getPassagemAtual().getLotacao().add(novaLotacao);
		recuperandoDao.salvar(pedido.getRecuperando());
		pedido.setStatus(StatusPedido.FINALIZADO);
		pedido.setDataConclusao(new Date());
		dao.salvar(pedido);
		pedido.getMovimentacaoInterna().setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);
		movIntDao.salvar(pedido.getMovimentacaoInterna());
		
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void finalizarPedidoMovExt(PedidoTransito pedido) throws ValidacaoException {		
		pedido.setStatus(StatusPedido.FINALIZADO);
		pedido.setDataConclusao(new Date());
		dao.salvar(pedido);
		pedido.getMovimentacaoExterna().setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);
		movExtDao.salvar(pedido.getMovimentacaoExterna());
		
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void finalizarPedidoTransferencia(PedidoTransito pedido) throws Exception {
		// validar data fim maior que data inicio
		Date dataFim = new Date();
		if (pedido.getRecuperando().getPassagemAtual().getLotacaoAtual().getDataEntrada().after(dataFim)) {
			throw new ValidacaoException("Data de entrada maior que a Data de Saída!");			
		}
		pedido.getRecuperando().getPassagemAtual().getLotacaoAtual().setDataSaida(dataFim);
		PassagemLotacao novaLotacao = new PassagemLotacao(pedido.getRecuperando().getPassagemAtual(), TipoTransito.TRANSFERENCIA);
		novaLotacao.setEstrutura(pedido.getEstruturaDestino());
		novaLotacao.setDataEntrada(new Date());
		pedido.getRecuperando().getPassagemAtual().getLotacao().add(novaLotacao);
		recuperandoDao.salvar(pedido.getRecuperando());
		pedido.setStatus(StatusPedido.FINALIZADO);
		pedido.setDataConclusao(new Date());
		dao.salvar(pedido);
		if(transfDao.possuiPedidoEmAndamento(pedido.getTransferencia())){
			pedido.getTransferencia().setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);			
		}else {
			pedido.getTransferencia().setStatus(StatusMovimentacao.AGUARDANDO_FINALIZAR);	
		}
		transfDao.salvar(pedido.getTransferencia());
		
		//Inativar Credenciamentos vinculados a este recuperando
		//visitanteDao.inativarCredenciamentosVinculadosAoRecuperando(pedido.getRecuperando());
	}
	
	public void expirarPedidosTransito(){
		Calendar dataExpirar = Calendar.getInstance();
		dataExpirar.add(Calendar.MONTH, -3);
		dao.expirarPedidosTransito(dataExpirar.getTime());
	}
	
	public boolean recuperandoEmMovimentacao(Recuperando recuperando){
		return dao.recuperandoEmMovimentacao(recuperando);
	}
	
	public boolean recuperandoEmMovimentacaoInterna(Recuperando recuperando){
		return dao.recuperandoEmMovimentacaoInterna(recuperando);
	}
	
	public boolean recuperandoEmMovimentacaoExterna(Recuperando recuperando){
		return dao.recuperandoEmMovimentacaoExterna(recuperando);
	}
	
	public boolean recuperandoEmTransferencia(Recuperando recuperando){
		return dao.recuperandoEmTransferencia(recuperando);
	}

	public void validarPedido(PedidoTransito pedido, MovimentacaoPassos passoAtual) throws ValidacaoException {
		if (pedido.getRecuperando() == null) {
			throw new ValidacaoException("É necessário adicionar um recuperando!");
		}
		
		validarStatusRecuperando(pedido.getRecuperando());
		validarOrigemDoRecuperando(pedido);
		
		if (passoAtual == MovimentacaoPassos.SALVAR || passoAtual == MovimentacaoPassos.AUTORIZAR) {
	        if (pedido.getMotivo() == null || pedido.getMotivo().trim().isEmpty()) {
	            throw new ValidacaoException("É necessário adicionar um motivo!");
	        }
	    }

		
		switch (passoAtual) {
			case ASSOCIAR_ESTRUTURA:
				validarEstruturaOrigemDestinoSaoIguais(pedido);
				break;
			case SALVAR:
				switch (pedido.getTipoTransito()) {
					case INTERNA:
						validarEstruturaAssociada(pedido);
						validarEstruturaOrigemDestinoSaoIguais(pedido);
						validarSeRecuperandoEstaAssociadoEmOutroPedidoAutorizadoOuVinculado(pedido);
						validarSeRecuperandoJaPossuiMovInternaNaoFinalizadoOuCanceladaParaMesmoDestino(pedido);
						break;
						
					case EXTERNA:
						validarEstabelecimentoAssociado(pedido);
						break;
						
					case TRANSFERENCIA:
						validarUnidadeOrigemDestinoSaoIguais(pedido);
						break;
						
					default:
						break;
				}
			break;
				
			case AUTORIZAR:
				switch (pedido.getTipoTransito()) {
					case INTERNA:
						validarSeRecuperandoEstaAssociadoEmOutroPedidoAutorizadoOuVinculado(pedido);
						validarSeRecuperandoEstaEmMovimentacaoInterna(pedido);
						break;
						
					case TRANSFERENCIA:
						validarSeRecuperandoEstaAssociadoEmOutroPedidoAutorizadoOuVinculadoOuAbertoOuSuspenso(pedido);
						break;
						
					default:
						break;
				}
				break;
				
			default:
				break;
						
		}
	}

	private void validarStatusRecuperando(Recuperando recuperando) throws ValidacaoException {
		StatusRecuperando status = recuperandoService.statusAtualRecuperando(recuperando.getId());
		
		if (status.equals(StatusRecuperando.INATIVO)) {
			throw new ValidacaoException("Recuperando selecionado foi desligado, altere ou cancele este Pedido de Trânsito!");
		}
	}

	private void validarSeRecuperandoEstaAssociadoEmOutroPedidoAutorizadoOuVinculadoOuAbertoOuSuspenso(PedidoTransito pedido) throws ValidacaoException {
		List<PedidoTransito> pedidosDoRecuperando = dao.buscarPedidosDoRecuperando(pedido.getRecuperando().getId());
		
		Predicate<PedidoTransito> pedidoAutorizado = p -> p.getStatus() == StatusPedido.AUTORIZADO;
		Predicate<PedidoTransito> pedidoVinculado = p -> p.getStatus() == StatusPedido.VINCULADO;
		Predicate<PedidoTransito> pedidoAndamento = p -> p.getStatus() == StatusPedido.EM_ANDAMENTO;
		Predicate<PedidoTransito> pedidoSuspenso = p -> p.getStatus() == StatusPedido.SUSPENSO;
		Predicate<PedidoTransito> pedidoAtualEhDiferenteDosExistentes = p -> p.getId() != pedido.getId();
		
		long count = pedidosDoRecuperando.stream()
				.filter(pedidoAutorizado.or(pedidoVinculado).or(pedidoAndamento).or(pedidoSuspenso))
				.filter(pedidoAtualEhDiferenteDosExistentes)
				.count();

		if (count > 0) {
			throw new ValidacaoException("Já existe um Pedido de Trânsito em aberto para esse Recuperando. Operação não permitida!");
		}
	}
	
	private void validarSeRecuperandoEstaAssociadoEmOutroPedidoAutorizadoOuVinculado(PedidoTransito pedido) throws ValidacaoException {
		List<PedidoTransito> pedidosDoRecuperandoPorTipo = dao.buscarPedidosDoRecuperandoPorTipo(TipoTransito.INTERNA, pedido.getRecuperando().getId());
		
		Predicate<PedidoTransito> pedidoAutorizado = p -> p.getStatus() == StatusPedido.AUTORIZADO;
		Predicate<PedidoTransito> pedidoVinculado = p -> p.getStatus() == StatusPedido.VINCULADO;
		Predicate<PedidoTransito> pedidoAtualEhDiferenteDosExistentes = p -> p.getId() != pedido.getId();
		
		long count = pedidosDoRecuperandoPorTipo.stream()
				.filter(pedidoAutorizado.or(pedidoVinculado))
				.filter(pedidoAtualEhDiferenteDosExistentes)
				.count();

		if (count > 0) {
			throw new ValidacaoException("Já existe um Pedido de Trânsito Autorizado para esse Recuperando. Operação não permitida!");
		}
	}
	
	private void validarSeRecuperandoEstaEmMovimentacaoInterna(PedidoTransito pedido) throws ValidacaoException {
		if (recuperandoEmMovimentacaoInterna(pedido.getRecuperando())) {
			throw new ValidacaoException("Recuperando já está em uma movimentação interna.");
		}
	}
	
	private void validarEstabelecimentoAssociado(PedidoTransito pedido) throws ValidacaoException {
		if (pedido.getEstabelecimento() == null) {
			throw new ValidacaoException("É necessário adicionar um estabelecimento!");
		}
	}
	
	private void validarEstruturaAssociada(PedidoTransito pedido) throws ValidacaoException {
		if (pedido.getEstruturaDestino() == null) {
			throw new ValidacaoException("É necessário adicionar uma estrutura!");
		}
	}
	
	private void validarUnidadeOrigemDestinoSaoIguais(PedidoTransito pedido) throws ValidacaoException {
		if (pedido.getUnidadeDestino().getId() == pedido.getUnidadeOrigem().getId()) {
			throw new ValidacaoException("Unidade de destino não pode ser igual a unidade de origem!");
		}
	}
	
	private void validarEstruturaOrigemDestinoSaoIguais(PedidoTransito pedido) throws ValidacaoException {
		if (pedido.getEstruturaDestino().getId() == pedido.getEstruturaOrigem().getId()) {
			throw new ValidacaoException("Estrutura de destino não pode ser igual a estrutura de origem!");
		}
	}
	
	private void validarOrigemDoRecuperando(PedidoTransito pedido) throws ValidacaoException {
		Estrutura lotacaoAtual = recuperandoService.lotacaoAtual(pedido.getRecuperando());
		
		if (lotacaoAtual.getId() != pedido.getEstruturaOrigem().getId()) {
			throw new ValidacaoException("A Estrutura de Origem (LOTAÇÃO ATUAL) do Pedido nº " + pedido.getId() + " não é a mesma, altere ou cancele este Pedido de Trânsito!");
		}
	}
	
	private void validarSeRecuperandoJaPossuiMovInternaNaoFinalizadoOuCanceladaParaMesmoDestino(PedidoTransito pedido) throws ValidacaoException {
		List<PedidoTransito> pedidosDoRecuperandoPorTipo = dao.buscarPedidosDoRecuperandoPorTipo(TipoTransito.INTERNA, pedido.getRecuperando().getId());
		
		Predicate<PedidoTransito> diferenteDeFinalizado = p -> p.getStatus() != StatusPedido.FINALIZADO;
		Predicate<PedidoTransito> diferenteDeCancelado = p -> p.getStatus() != StatusPedido.CANCELADO;
		Predicate<PedidoTransito> diferenteDeNaoAutorizado = p -> p.getStatus() != StatusPedido.NAO_AUTORIZADO;
		Predicate<PedidoTransito> pedidoAtualEhDiferenteDosExistentes = p -> p.getId() != pedido.getId();
		Predicate<PedidoTransito> estruturaDoPedidoJaExisteEmOutro = p -> p.getEstruturaDestino().getId() == pedido.getEstruturaDestino().getId();
		
		long count = pedidosDoRecuperandoPorTipo.stream()
				.filter(diferenteDeFinalizado
						.and(diferenteDeCancelado)
						.and(diferenteDeNaoAutorizado)
						.and(pedidoAtualEhDiferenteDosExistentes)
						.and(estruturaDoPedidoJaExisteEmOutro))
				.count();

		if (count > 0) {
			throw new ValidacaoException("Recuperando já possui um pedido do tipo movimentação interna em aberto para o mesmo destino.");
		}
	}
	
	public List<PedidoTransito> buscarPedidosDoRecuperando(long recuperandoId) {
		return this.dao.buscarPedidosDoRecuperando(recuperandoId); 
	}

	public List<PedidoTransito> buscarPedidosPorMovimentacaoId(long id) {
		return this.dao.buscarPedidosPorMovimentacaoId(id);
	}

	public int countRecebimentoTransferencia(FiltroPedTransito filtro) {
		return quantidadeFiltrados(filtro);
	}
}
