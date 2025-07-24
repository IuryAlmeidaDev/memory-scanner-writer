package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import org.hibernate.Hibernate;

@RequestScoped
public class PlantaoDAO extends BaseDAO<Plantao, Long>{

	private static final long serialVersionUID = -318366946850533179L;
	
	
	@Inject
	private EntityManager entityManager;
	
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Plantao buscarUltimoPlantao() {
		String jpql = "SELECT r FROM Plantao r ORDER BY r.id DESC";

		try {
			return entityManager.createQuery(jpql, Plantao.class)
					.setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public Plantao buscarPorId(Long id) {

		StringBuilder jpql= new StringBuilder("SELECT p FROM Plantao p where p.id = :id");

		TypedQuery<Plantao> query = em.createQuery(jpql.toString(), Plantao.class);
		query.setParameter("id", id);


		try{
			Plantao result = query.getSingleResult();
			Hibernate.initialize(result.getEquipes());

			for(Equipe equipe : result.getEquipes()){
				Hibernate.initialize(equipe.getPlantaoServidor());
				for(PlantaoServidor plantaoServidor : equipe.getPlantaoServidor()){
					Hibernate.initialize(plantaoServidor);
					Hibernate.initialize(plantaoServidor.getServidor());
					Hibernate.initialize(plantaoServidor.getServidor().getFuncao());
					Hibernate.initialize(plantaoServidor.getServidor().getCargo());
				}
			}

			return result;
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

}
