package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;

@RequestScoped
public class TipoRefeicaoDAO {
	
	@Inject
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<TipoRefeicao> listarTodos() {
		String jpql = "select t from TipoRefeicao t";
		Query query = entityManager.createQuery(jpql, TipoRefeicao.class);
		
		return query.getResultList();
	}

}
