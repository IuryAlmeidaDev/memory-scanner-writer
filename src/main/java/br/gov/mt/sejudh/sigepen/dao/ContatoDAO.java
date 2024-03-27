package br.gov.mt.sejudh.sigepen.dao;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.VisitanteCertidao;


@RequestScoped
public class ContatoDAO {
	
	@Inject
	private EntityManager entityManager;
	
	public ContatoDAO(){		
		
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Contato salvar(Contato contato) {
		if(contato.getId()>0){
			contato = entityManager.merge(contato);
			Hibernate.initialize(contato.getPessoa());
			Hibernate.initialize(contato.getUnidadePrisional());
			Hibernate.initialize(contato.getServidor());
			return contato;
		}else{
			entityManager.persist(contato);	
			return contato;
		}
	}

	public void excluir(Contato contato) {
		entityManager.remove(entityManager.merge(contato));
	}
	
	public void excluir(Endereco endereco) {
		entityManager.remove(entityManager.merge(endereco));
	}
	
	public void excluir(VisitanteCertidao certidao) {
		entityManager.remove(entityManager.merge(certidao));
	}
		
	
}
