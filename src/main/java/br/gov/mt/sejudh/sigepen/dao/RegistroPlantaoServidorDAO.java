package br.gov.mt.sejudh.sigepen.dao;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantaoServidor;

@RequestScoped
public class RegistroPlantaoServidorDAO extends BaseDAO<RegistroPlantaoServidor, Long>{

	private static final long serialVersionUID = 1L;
	
	
	@Inject
	private EntityManager entityManager;

	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	
	
	/* ------------------------  Métodos  ------------------------ */
	
	public RegistroPlantaoServidor salvar(RegistroPlantaoServidor registroPlantaoServidor) {
        if (registroPlantaoServidor.getId() != null && registroPlantaoServidor.getId() > 0) {
            entityManager.merge(registroPlantaoServidor);
        } else {
            entityManager.persist(registroPlantaoServidor);
        }
		return registroPlantaoServidor;
    }
		
	
	
	
}
