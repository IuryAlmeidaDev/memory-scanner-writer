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
