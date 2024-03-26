package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
}
