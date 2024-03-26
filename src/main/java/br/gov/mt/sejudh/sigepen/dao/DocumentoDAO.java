package br.gov.mt.sejudh.sigepen.dao;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.model.Documento;


@RequestScoped
public class DocumentoDAO {
	
	@Inject
	private EntityManager entityManager;
	
	public DocumentoDAO(){		
		
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Documento salvar(Documento documento) {
		if(documento.getId()>0){
			documento = entityManager.merge(documento);
			Hibernate.initialize(documento.getPessoa());
			Hibernate.initialize(documento.getRecuperando());
			Hibernate.initialize(documento.getServidor());
			return documento;
		}else{
			entityManager.persist(documento);	
			return documento;
		}
	}

	public void excluir(Documento documento) {
		entityManager.remove(entityManager.merge(documento));
	}
		
	
}
