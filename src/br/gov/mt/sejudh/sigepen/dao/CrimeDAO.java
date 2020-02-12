package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCrime;
import br.gov.mt.sejudh.sigepen.model.Crime;

@RequestScoped
public class CrimeDAO {

	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}


	@SuppressWarnings("unchecked")
	public List<Crime> buscaPorFiltro(FiltroCrime filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Crime.class, entityManager, isBuscaRelatorio);			
		return criteria.list();
	}
	
	public int quantidadeFiltrados(FiltroCrime filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Crime.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
	    return ((Number) criteria.uniqueResult()).intValue();
	}	
	
	@SuppressWarnings("unchecked")
	public List<String> listarLegislacoes() {
		String jpql="select distinct c.nomeLegislacao from Crime c order by c.nomeLegislacao";
		Query query = entityManager.createQuery(jpql);
		List<String> legislacoes = query.getResultList();
		return legislacoes;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> listarArtigos(String legislacao) {
		String jpql="select distinct c.numArtigo from Crime c where c.nomeLegislacao = :legislacao order by c.numArtigo";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("legislacao", legislacao);
		List<String> artigos = query.getResultList();
		return artigos;
	}

	public Crime buscarPorId(long id) {
		return entityManager.find(Crime.class, id);
	}
}
