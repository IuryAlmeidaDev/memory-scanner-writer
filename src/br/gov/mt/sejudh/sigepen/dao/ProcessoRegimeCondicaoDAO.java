package br.gov.mt.sejudh.sigepen.dao;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import br.gov.mt.sejudh.sigepen.model.ProcessoRegimeCondicao;

@RequestScoped
public class ProcessoRegimeCondicaoDAO {
	
	@Inject
	private EntityManager entityManager;

	public void salvar(ProcessoRegimeCondicao processoRegimeCondicao) {
		if(processoRegimeCondicao.getId() > 0){
			entityManager.merge(processoRegimeCondicao);
		} else {
			entityManager.persist(processoRegimeCondicao);
		}
	}

}
