package br.gov.mt.sejudh.sigepen.util;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class JPAUtil implements Serializable {

	private static final long serialVersionUID = 4159215508630849789L;

	@PersistenceContext(unitName = "sigepen")
	private EntityManager entityManager;

	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		return entityManager;
	}

}
