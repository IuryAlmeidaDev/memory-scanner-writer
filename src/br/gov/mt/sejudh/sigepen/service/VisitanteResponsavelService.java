package br.gov.mt.sejudh.sigepen.service;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.VisitanteDAO;
import br.gov.mt.sejudh.sigepen.dao.VisitanteResponsavelDAO;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.VisitanteResponsavel;

@Stateless
public class VisitanteResponsavelService extends  BaseService<VisitanteResponsavel, Long>{

	private static final long serialVersionUID = 7230046305068303422L;
	
	@Inject
	private VisitanteResponsavelDAO visitanteResponsavelDAO;
	
	
	public void salvarVisitanteResponsavel(Visitante visitante, Visitante visitanteResponsavel) {
		visitanteResponsavelDAO.salvarVisitanteReponsavel(visitante, visitanteResponsavel);
	}
	
	public boolean buscarVisitanteResponsavelPorIdVisitante(Visitante visitante, Visitante vr) {
		return visitanteResponsavelDAO.buscarVisitanteResponsavelPorIdVisitante(visitante, vr);
	}
	
	public List<VisitanteResponsavel> buscarListaVisitantesReponsaveis(Visitante visitante){
		return this.visitanteResponsavelDAO.buscarListaVisitantesReponsaveis(visitante);
	}
	
	public void atualizarVisitanteResponsavel(Visitante visitante, Visitante vr) {
		this.visitanteResponsavelDAO.atualizarVisitanteResponsavel(visitante, vr);
	}

}
