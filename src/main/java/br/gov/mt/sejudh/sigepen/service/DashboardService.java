package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.DashboardDAO;

@Stateless
public class DashboardService {

	@Inject
	private DashboardDAO dao;
	
	public Long totalRecuperandosAtivos() {
		return dao.totalRecuperandosAtivos();
	}
	
	public Long totalRecuperandosAtivosNaUnidade(long unidadeId) {
		return dao.totalRecuperandosAtivosNaUnidade(unidadeId);
	}
	
	public Long totalRecuperandosComCadastroIncompleto(long unidadeId) {
		return dao.totalRecuperandosComCadastroIncompleto(unidadeId);
	}
	
	public List<Long> recuperandosTemporariosNaUnidadeDataSolturaAVencer(long unidadeId) {
		return dao.recuperandosTemporariosNaUnidadeDataSolturaAVencer(unidadeId);
	}
	
	public List<Long> recuperandosTemporariosNaUnidadeDataSolturaVencida(long unidadeId) {
		return dao.recuperandosTemporariosNaUnidadeDataSolturaVencida(unidadeId);
	}
	
	public List<Long> recuperandosTemporariosNaUnidadeDataSolturaVenceHoje(long unidadeId) {
		return dao.recuperandosTemporariosNaUnidadeDataSolturaVenceHoje(unidadeId);
	}
	
	public List<Long> recuperandosTemporariosNoSistemaDataSolturaAVencer() {
		return dao.recuperandosTemporariosNoSistemaDataSolturaAVencer();
	}
	
	public List<Long> recuperandosTemporariosNoSistemaDataSolturaVencida() {
		return dao.recuperandosTemporariosNoSistemaDataSolturaVencida();
	}
	
	public List<Long> recuperandosTemporariosNoSistemaDataSolturaVenceHoje() {
		return dao.recuperandosTemporariosNoSistemaDataSolturaVenceHoje();
	}	
		
	public Long countRecuperandosProvisorios90a180Dias(Long unidadeId) {
	    return dao.countRecuperandosProvisorios90a180Dias(unidadeId);
	}

	public Long countRecuperandosProvisoriosMais180Dias(Long unidadeId) {
	    return dao.countRecuperandosProvisoriosMais180Dias(unidadeId);
	}

	public Long solicitacaoVisitaPendente(long unidadeId) {
		return dao.solicitacaoVisitaPendente(unidadeId);
	}

	public Long totalCredenciamentoRecebimentoPendente(long id) {
		return dao.totalCredenciamentoRecebimentoPendente(id);
	}

	public Long totalCredenciamentoChecagemPendente(long id) {
		return dao.totalCredenciamentoChecagemPendente(id);
	}

	public Long totalCredenciamentoConferenciaPendente(long id) {
		return dao.totalCredenciamentoConferenciaPendente(id);
	}

	public Long totalCredenciamentoAnalisePendente(long id) {
		return dao.totalCredenciamentoAnalisePendente(id);
	}

	public Long totalCredenciamentoAprovacaoPendente(long id) {
		return dao.totalCredenciamentoAprovacaoPendente(id);
	}
}
