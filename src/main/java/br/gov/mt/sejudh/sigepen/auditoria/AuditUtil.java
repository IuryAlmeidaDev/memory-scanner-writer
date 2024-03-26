package br.gov.mt.sejudh.sigepen.auditoria;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

@ApplicationScoped
public class AuditUtil implements Serializable {

	private static final long serialVersionUID = 7904637630443987079L;
	
	@Inject
	private EntityManager entityManager;
	
	@Produces
	@RequestScoped
	public AuditReader getAuditReader() {
		return  AuditReaderFactory.get(entityManager);
	}

}
