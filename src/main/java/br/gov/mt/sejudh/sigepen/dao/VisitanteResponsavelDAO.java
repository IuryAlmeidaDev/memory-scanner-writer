package br.gov.mt.sejudh.sigepen.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.VisitanteResponsavel;

@RequestScoped
public class VisitanteResponsavelDAO extends BaseDAO<VisitanteResponsavel, Long> {

	private static final long serialVersionUID = -6672647515344617367L;
	
	 @Inject
	 protected EntityManager em;
	 

	@Transactional
	public void salvarVisitanteReponsavel(Visitante visitante, Visitante visitanteResponsavel) {
		VisitanteResponsavel responsavel = new VisitanteResponsavel();
		responsavel.setVisitante(visitante);
		responsavel.setVisitanteResponsavel(visitanteResponsavel);
		responsavel.setUsuarioCadastro(visitante.getUsuarioCadastro());
		responsavel.setStatus(StatusVisitante.A);
		responsavel.setDataHoraCadastro(new Date());
		super.salvar(responsavel);
	}
	
	
	public List<VisitanteResponsavel> buscarListaVisitantesReponsaveis(Visitante visitante){
		String jpql = "SELECT vr FROM VisitanteResponsavel vr "
				+ "JOIN FETCH vr.visitante "
				+ "JOIN FETCH vr.visitanteResponsavel "
				+ "WHERE vr.visitante.id = :idVisitante "
				+ "AND vr.status = :status";
		Query query = em.createQuery(jpql, VisitanteResponsavel.class)
				.setParameter("idVisitante", visitante.getId())
				.setParameter("status", StatusVisitante.A);
	    return query.getResultList();
	}

	public boolean buscarVisitanteResponsavelPorIdVisitante(Visitante visitante, Visitante vr) {
	    String jpql = "SELECT vr FROM VisitanteResponsavel vr "
	    		+ "WHERE vr.visitante.id = :idVisitante "
	    		+ "and vr.visitanteResponsavel = :visitanteResponsavel "
	    		+ "and vr.status = :status";
	    Query query = em.createQuery(jpql, VisitanteResponsavel.class)
	                   .setParameter("idVisitante", visitante.getId())
	                   .setParameter("visitanteResponsavel", vr)
	                   .setParameter("status", StatusVisitante.A);
	    return query.getResultList().isEmpty();
	}
	
	public void atualizarVisitanteResponsavel(Visitante visitante, Visitante vr) {
		 String jpql = "UPDATE VisitanteResponsavel vr "
		            + "SET vr.status = :status "
		            + "WHERE vr.visitante.id = :idVisitante "
		            + "AND vr.visitanteResponsavel = :visitanteResponsavel "
		            + "AND vr.status = :statusA";

		    Query query = em.createQuery(jpql)
		                   .setParameter("status", StatusVisitante.I)
		                   .setParameter("idVisitante", visitante.getId())
		                   .setParameter("visitanteResponsavel", vr)
		                   .setParameter("statusA", StatusVisitante.A);

		    query.executeUpdate();
	}
	
	public void desativarVisitanteReponsavel() {
		
	}

}
