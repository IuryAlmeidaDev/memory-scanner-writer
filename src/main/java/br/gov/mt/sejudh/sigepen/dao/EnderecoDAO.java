package br.gov.mt.sejudh.sigepen.dao;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.model.Endereco;


@RequestScoped
public class EnderecoDAO {
	
	@Inject
	private EntityManager entityManager;
	
	public EnderecoDAO(){		
		
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Endereco salvar(Endereco endereco) {
		if(endereco.getId()>0){
			endereco = entityManager.merge(endereco);
			Hibernate.initialize(endereco.getPessoa());
			Hibernate.initialize(endereco.getUnidadePrisional());
			Hibernate.initialize(endereco.getServidor());
			return endereco;
		}else{
			entityManager.persist(endereco);
			return endereco;
		}
	}

	public void excluir(Endereco endereco) {
		entityManager.remove(entityManager.merge(endereco));
	}
		
	
}
