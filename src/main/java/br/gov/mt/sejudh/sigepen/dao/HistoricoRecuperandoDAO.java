package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;


@RequestScoped
public class HistoricoRecuperandoDAO {

	@Inject
	private EntityManager entityManager;
	
	public HistoricoRecuperandoDAO(){
		
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public void salvar(HistoricoRecuperando historicoRecuperando){
		if (historicoRecuperando.getId()>0) {
			historicoRecuperando = entityManager.merge(historicoRecuperando);
		} else {
			entityManager.persist(historicoRecuperando);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<HistoricoRecuperando> listarHistoricoRecuperando(Long recuperandoId) {
		String jpql="select h from HistoricoRecuperando h where h.recuperando.id = :id ORDER BY h.dataRegistro ASC";
		Query query = entityManager.createQuery(jpql, HistoricoRecuperando.class);
		query.setParameter("id", recuperandoId);
		List<HistoricoRecuperando> lista = query.getResultList();
		for(HistoricoRecuperando h : lista) {
			Hibernate.initialize(h.getRecuperando());
		}
		return lista;
	}
	
	public HistoricoRecuperando buscarHistoricoPorId(long id) {
		String jpql = "SELECT h FROM HistoricoRecuperando h " +
				"JOIN FETCH h.recuperando rec " +
				"JOIN FETCH rec.pessoa pessoa " +
				"WHERE h.id = :id";

		TypedQuery<HistoricoRecuperando> query = entityManager.createQuery(jpql, HistoricoRecuperando.class);
		query.setParameter("id", id);

		return query.getSingleResult();
	}
	
}
