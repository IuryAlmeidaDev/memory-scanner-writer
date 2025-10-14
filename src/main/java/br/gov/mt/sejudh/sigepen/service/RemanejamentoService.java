package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.PedidoDAO;
import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.RemanejamentoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRemanejamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Remanejamento;
import br.gov.mt.sejudh.sigepen.model.dto.RemanejamentoDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;


@Stateless
public class RemanejamentoService {
	
	@Inject
	private RemanejamentoDAO dao;	
	
	@Inject
	private PedidoDAO daoPedido;
	
	@Inject
	private RecuperandoDAO recuperandoDao;
	
	@Inject
	private AuditoriaDAO auditDAO;

	@Interceptors(EJBExcInterceptor.class)
	public void salvar(Remanejamento remanejamento) throws ValidacaoException {
		
		Date dataFim = new Date();
		if (remanejamento.getPedido().getRecuperando().getPassagemAtual().getLotacaoAtual().getDataEntrada().after(dataFim)) {
			throw new ValidacaoException("Data de Entrada menor que a Data de Saída!");			
		}
		remanejamento.getPedido().getRecuperando().getPassagemAtual().getLotacaoAtual().setDataSaida(dataFim);
		PassagemLotacao novaLotacao = new PassagemLotacao(remanejamento.getPedido().getRecuperando().getPassagemAtual(), TipoTransito.REMANEJAMENTO);
		novaLotacao.setEstrutura(remanejamento.getEstruturaDestino());
		novaLotacao.setDataEntrada(new Date());
		remanejamento.getPedido().getRecuperando().getPassagemAtual().getLotacao().add(novaLotacao);
		recuperandoDao.salvar(remanejamento.getPedido().getRecuperando());
		remanejamento.getPedido().setStatus(StatusPedido.SUSPENSO);
		daoPedido.salvar(remanejamento.getPedido());		
		remanejamento.setDataInicio(dataFim);		
		dao.salvar(remanejamento);
	}	
	
	public List<RemanejamentoDTO> buscaPorFiltro(FiltroRemanejamento filtro) {
		return dao.buscaPorFiltro(filtro);
    }	
		
	public int quantidadeFiltrados(FiltroRemanejamento filtro){
		return dao.quantidadeFiltrados(filtro);
	}
	
	public Remanejamento buscaRemanejamentoPorId(long id) {
		Remanejamento remanejamento = dao.buscaRemanejamentoPorId(id);
		
		if (remanejamento != null) {
			remanejamento.setAutorRegistro(auditDAO.autorRegistro(remanejamento, remanejamento.getId()));
			remanejamento.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(remanejamento, remanejamento.getId()));
		}
		
		return remanejamento;
    }

	@Interceptors(EJBExcInterceptor.class)
	public void finalizarRemanejamento(Remanejamento remanejamento) throws ValidacaoException {
		Date dataFim = new Date();
		if (remanejamento.getPedido().getRecuperando().getPassagemAtual().getLotacaoAtual().getDataEntrada().after(dataFim)) {
			throw new ValidacaoException("Data de Entrada menor que a Data de Saída!");			
		}
		remanejamento.getPedido().getRecuperando().getPassagemAtual().getLotacaoAtual().setDataSaida(dataFim);
		PassagemLotacao novaLotacao = new PassagemLotacao(remanejamento.getPedido().getRecuperando().getPassagemAtual(), TipoTransito.REMANEJAMENTO);
		novaLotacao.setEstrutura(remanejamento.getEstruturaOrigem());
		novaLotacao.setDataEntrada(new Date());
		remanejamento.getPedido().getRecuperando().getPassagemAtual().getLotacao().add(novaLotacao);
		recuperandoDao.salvar(remanejamento.getPedido().getRecuperando());
		remanejamento.getPedido().setStatus(StatusPedido.EM_ANDAMENTO);
		daoPedido.salvar(remanejamento.getPedido());		
		remanejamento.setDataConclusao(new Date());	
		remanejamento.setStatus(StatusMovimentacao.FINALIZADO);
		dao.salvar(remanejamento);
	}

    public int countRecebimentoTransferencia(FiltroRemanejamento filtro) {
		return quantidadeFiltrados(filtro);
    }
}
