package br.gov.mt.sejudh.sigepen.dao;

import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.Parametro;

@RequestScoped
public class ParametroDao {

	@Inject
	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(Parametro param) {
		if (param.getId() > 0) {
			entityManager.merge(param);
		} else {
			Long id = listarParametrosSistema().stream().max(Comparator.comparing(Parametro::getId)).get().getId()+1;
			param.setId(id);
			entityManager.persist(param);
		}		
	}
	
	public void excluir(Parametro param) {
		Parametro pAux = entityManager.find(Parametro.class, param.getId());
		
		if (pAux != null) {
			entityManager.remove(pAux);
		}
	}
	
	public List<Parametro> listarParametrosSistema() {
		TypedQuery<Parametro> query = entityManager.createQuery("select p from Parametro p", Parametro.class);
		return query.getResultList();
	}
}
