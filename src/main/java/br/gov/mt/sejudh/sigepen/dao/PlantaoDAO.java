package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Plantao;

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
		
	/* ------------------------  Métodos  ------------------------ */
	
	public List<Plantao> listarPlantaoAtivo() {		
		return super.buscaPorCampo("status", Status.ATIVO);
	}
	
	

}
