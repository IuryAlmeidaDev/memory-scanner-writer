package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.UserTbl;
import br.gov.mt.sejudh.sigepen.model.dto.PessoaNServerDTO;

@RequestScoped
public class NServerDAO {

	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Long getCountUserTbl() {
		Query query = entityManager.createQuery("select count(u) from UserTbl u");
		return (Long)query.getSingleResult();
	}

	public Long getCountPessoaComBiometria() {
		Query query = entityManager.createQuery("select count(p) from Pessoa p, Biometria b where p.biometria.id = b.id and b.template is not null");
		return (Long)query.getSingleResult();
	}

	public List<UserTbl> listarUserTbl() {
		TypedQuery<UserTbl> query = entityManager.createQuery("select u from UserTbl u where u.pessoa_id not in (select p.id from Pessoa p, Biometria b where p.biometria.id = b.id and b.template is not null)", UserTbl.class);
		return query.getResultList();
	}

	public List<PessoaNServerDTO> listarPessoas() {
		/*
		 * Query com visitante, substituir após o merge, efetuar o teste deste select antes de subir o código.
		 * List<PessoaNServerDTO> pessoas = entityManager.createQuery("select new br.gov.mt.sejudh.sigepen.model.dto.PessoaNServerDTO(p.id, r.id, v.id, b.id, p.nome, p.nomeMae, p.dataNascimento) from Pessoa p join p.biometria b left join p.recuperando r left join p.visitante v where b.template is not null and p.id not in (select u.pessoa_id from UserTbl u)", PessoaNServerDTO.class).getResultList();
		 * 
		 */
		List<PessoaNServerDTO> pessoas = entityManager.createQuery("select new br.gov.mt.sejudh.sigepen.model.dto.PessoaNServerDTO(p.id, r.id, 0L, b.id, p.nome, p.nomeMae, p.dataNascimento) from Pessoa p join p.biometria b left join p.recuperando r where b.template is not null and p.id not in (select u.pessoa_id from UserTbl u)", PessoaNServerDTO.class).getResultList();
		return pessoas;
	}
}
