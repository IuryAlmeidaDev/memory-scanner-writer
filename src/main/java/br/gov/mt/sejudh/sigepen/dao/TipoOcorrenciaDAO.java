package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.model.TipoOcorrencia;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;


@RequestScoped
public class TipoOcorrenciaDAO extends BaseDAO<TipoOcorrencia, Long> {


	private static final long serialVersionUID = 1635530084073694819L;
	
	@Inject
	private EntityManager entityManager;


	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
