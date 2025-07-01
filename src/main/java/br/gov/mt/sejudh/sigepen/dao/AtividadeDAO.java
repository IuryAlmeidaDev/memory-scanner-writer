package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.model.Atividade;


@RequestScoped
public class AtividadeDAO extends BaseDAO<Atividade, Long>{

	private static final long serialVersionUID = -318366946850533179L;
	
	
	@Inject
	private EntityManager entityManager;
	
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	
	
	/* ------------------------  Métodos  ------------------------ */
		
	public List<Atividade> listarTodasAtividades() {
	    String jpql = "SELECT a FROM Atividade a"; 
	    Query query = entityManager.createQuery(jpql, Atividade.class);
	    return query.getResultList();
	}
		
	

}
