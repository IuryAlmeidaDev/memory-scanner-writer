package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.HistoricoRecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.PassagemDAO;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@Stateless
public class HistoricoRecuperandoService {

	@Inject
	private HistoricoRecuperandoDAO dao;

	@Inject
	private PassagemDAO passagemDAO;

	@Interceptors(EJBExcInterceptor.class)
	public void salvar(HistoricoRecuperando historicoRecuperando) throws ValidacaoException {
		validarDataRegistroComPassagemAtual(historicoRecuperando);
		dao.salvar(historicoRecuperando);
	}

	private void validarDataRegistroComPassagemAtual(HistoricoRecuperando historicoRecuperando)
			throws ValidacaoException {
		if (historicoRecuperando == null) {
			throw new ValidacaoException("Histórico do recuperando não informado.");
		}

		if (historicoRecuperando.getRecuperando() == null || historicoRecuperando.getRecuperando().getId() <= 0) {
			throw new ValidacaoException("Recuperando do histórico é obrigatório.");
		}

		if (historicoRecuperando.getDataRegistro() == null) {
			throw new ValidacaoException("Data do histórico é obrigatória.");
		}

		Optional<Date> dataEntradaPassagemAtual = passagemDAO
				.buscarDataEntradaPassagemAtual(historicoRecuperando.getRecuperando().getId());

		if (!dataEntradaPassagemAtual.isPresent()) {
			throw new ValidacaoException("Recuperando não possui passagem atual.");
		}

		if (historicoRecuperando.getDataRegistro().before(dataEntradaPassagemAtual.get())) {
			throw new ValidacaoException(
					"A data do histórico não pode ser anterior à data de entrada da passagem atual do recuperando.");
		}
	}

	public List<HistoricoRecuperando> listarHistoricoRecuperando(Long recuperandoId){
		return listarHistoricoRecuperandoPorPeriodo(recuperandoId, null, null);
	}

	public List<HistoricoRecuperando> listarHistoricoRecuperandoPermitidos(Long recuperandoId) {
		return dao.listarHistoricoRecuperandoPorPeriodoPermissao(recuperandoId, null, null);
	}

	public List<HistoricoRecuperando> listarHistoricoRecuperandoPermitidosPorPeriodo(Long recuperandoId, Date dataInicial, Date dataFinal) {
		return dao.listarHistoricoRecuperandoPorPeriodoPermissao(recuperandoId, dataInicial, dataFinal);
	}

	public List<HistoricoRecuperando> listarHistoricoRecuperandoPorPeriodo(Long recuperandoId, Date dataInicial, Date dataFinal) {
		return dao.listarHistoricoRecuperandoPorPeriodo(recuperandoId, dataInicial, dataFinal);
	}

	public Optional<HistoricoRecuperando> buscarHistoricoPorId(long id) {
		return this.dao.buscarHistoricoPorId(id);
	}

	public void removerHistoricoRecuperando(Long recuperandoId, Long historicoId) throws ValidacaoException {
		if (!possuiPermissaoExcluirHistorico()) {
			throw new ValidacaoException("Usuário sem permissão para excluir histórico do recuperando.");
		}

		dao.removerHistoricoRecuperando(recuperandoId, historicoId);
	}

	protected boolean possuiPermissaoExcluirHistorico() {
		return SecurityUtil.possuiPermissaoNaUnidadeLogada("ExcluirHistoricoRecuperando");
	}
}
