package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Alimento;

public class AlimentoDAO extends BaseDAO<Alimento, Long> {

	private static final long serialVersionUID = 3689374908012254663L;

	@SuppressWarnings("unchecked")
	public List<Alimento> buscaPorFiltro(FiltroAlimento filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Alimento.class, em);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(Alimento.class));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Alimento> listarAtivos() {
		String jpql = "select a from Alimento a where status = :status order by descricao asc";
		Query query = em.createQuery(jpql, Alimento.class);
		query.setParameter("status", Status.ATIVO);

        return (List<Alimento>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Alimento> listarTodos() {
		String jpql = "select a from Alimento a order by descricao asc";
		Query query = em.createQuery(jpql, Alimento.class);

        return (List<Alimento>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<String> listarDescricoesParaFiltro(String consulta, int limite) {
		String consultaNormalizada = consulta != null ? consulta.trim().toLowerCase() : null;
		if (consultaNormalizada == null || consultaNormalizada.isEmpty()) {
			return java.util.Collections.emptyList();
		}

		String jpql = "select distinct a.descricao from Alimento a "
				+ "where a.descricao is not null "
				+ "and lower(a.descricao) like :consulta "
				+ "order by a.descricao asc";
		Query query = em.createQuery(jpql, String.class);
		query.setParameter("consulta", "%" + consultaNormalizada + "%");
		if (limite > 0) {
			query.setMaxResults(limite);
		}

		return (List<String>) query.getResultList();
	}

	public Alimento buscarAlimento(Alimento alimento) {
		String jpql = "select a from Alimento a where status = :status and descricao = :descricao and :unidadeMedida = :unidadeMedida";
		Query query = em.createQuery(jpql, Alimento.class);
		query.setParameter("status", Status.ATIVO);
		query.setParameter("descricao", alimento.getDescricao());
		query.setParameter("unidadeMedida", alimento.getUnidadeMedida());
		try {
			return (Alimento) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
