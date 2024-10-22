package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.hibernate.Hibernate;
import br.gov.mt.sejudh.sigepen.model.Processo;

@RequestScoped
public class ProcessoDAO {
	
	@Inject
	private EntityManager entityManager;
	
	public Processo salvar(Processo processo){
		if(processo.getId() > 0){
			processo = entityManager.merge(processo);
		} else{
			entityManager.persist(processo);
		}
		return processo;
	}
	
	public List<Processo> processosPorRecuperando(long idRecuperando){
		TypedQuery<Processo> query = entityManager.createQuery("select p from Processo p where p.recuperando.id = :idRecuperando", Processo.class);
		query.setParameter("idRecuperando", idRecuperando);
		return query.getResultList();
	}

	public Processo buscaProcessoPorId(Processo processo) {
		try{
			String jpql="select p from Processo p where p.id = :id";
			TypedQuery<Processo> query = entityManager.createQuery(jpql, Processo.class);
			query.setParameter("id", processo.getId());			
			Processo p = query.getSingleResult();
			Hibernate.initialize(p.getCrimes());
			return p;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public void excluirProcesso(Processo processo) {
		
			Processo managedProcesso = entityManager.merge(processo);
			
			String sql = "DELETE FROM SIGEPEN_OWNER.PROCESSO_REGIME_CONDICAO WHERE ID = :id";
		    Query query = entityManager.createNativeQuery(sql);		    
		    query.setParameter("id", processo.getId());		    
		    query.executeUpdate();		    		       		    
		    		    		    
		    String sql2 = "DELETE FROM SIGEPEN_OWNER.PROCESSO_CRIME WHERE ID = :id";
		    Query query2 = entityManager.createNativeQuery(sql2);		    
		    query2.setParameter("id", processo.getId());
		    query2.executeUpdate();		    		    		   		    		
		    
		entityManager.remove(managedProcesso);				
	}
	
	
	public long contarProcessosPorRecuperando(long idRecuperando) {
	    TypedQuery<Long> query = entityManager.createQuery(
	        "select count(p) from Processo p where p.recuperando.id = :idRecuperando", Long.class);
	    query.setParameter("idRecuperando", idRecuperando);
	    return query.getSingleResult();
	}
	
	
	
}
