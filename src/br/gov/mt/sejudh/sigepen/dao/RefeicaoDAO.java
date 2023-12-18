package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.Query;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.model.Apostilamento;
import br.gov.mt.sejudh.sigepen.model.Refeicao;

@RequestScoped
public class RefeicaoDAO extends BaseDAO<Refeicao, Long> {
	private static final long serialVersionUID = -3327341121419415963L;

	@SuppressWarnings("unchecked")
	public List<Refeicao> listarRefeicoesApostilamento(Long id) {		
		String jpql = "select r from Refeicao r join fetch r.apostilamento a where a.id = :id ";
		Query query = em.createQuery(jpql, Refeicao.class);
		query.setParameter("id", id);
		List<Refeicao> refeicao = query.getResultList();

		return refeicao;
	}

}