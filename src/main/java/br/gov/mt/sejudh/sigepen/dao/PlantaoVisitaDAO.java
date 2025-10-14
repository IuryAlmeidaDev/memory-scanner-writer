package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.model.Equipe;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.PlantaoServidor;
import br.gov.mt.sejudh.sigepen.model.PlantaoVisita;
import org.hibernate.Hibernate;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

@RequestScoped
public class PlantaoVisitaDAO extends BaseDAO<PlantaoVisita, Long>{

	private static final long serialVersionUID = -318366946850533179L;
	
	
	@Inject
	private EntityManager entityManager;
	
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

    public PlantaoVisita buscarPlantaoVisita(Long idPlantao , TipoEntrada tipoEntrada) {

			String jpql= "SELECT pv FROM PlantaoVisita pv WHERE pv.tipoEntrada = :tipoEntrada AND pv.plantao.id = :idPlantao ";

			TypedQuery<PlantaoVisita> query = em.createQuery(jpql, PlantaoVisita.class);
			query.setParameter("idPlantao", idPlantao);
			query.setParameter("tipoEntrada", tipoEntrada);


			try{
				PlantaoVisita result = query.getSingleResult();
				Hibernate.initialize(result.getPlantaoVisitaEstruturas());

				return result;
			} catch (NoResultException e) {
				e.printStackTrace();
				return null;
			}
		}
}
