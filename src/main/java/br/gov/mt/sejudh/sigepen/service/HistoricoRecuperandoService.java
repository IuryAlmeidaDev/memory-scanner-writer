package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.HistoricoRecuperandoDAO;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class HistoricoRecuperandoService {

	@Inject
	private HistoricoRecuperandoDAO dao;

	@Interceptors(EJBExcInterceptor.class)
	public void salvar(HistoricoRecuperando historicoRecuperando){
		dao.salvar(historicoRecuperando);
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

	public void removerHistoricoRecuperando(Long recuperandoId, Long historicoId){
		dao.removerHistoricoRecuperando(recuperandoId, historicoId);
	}
}
