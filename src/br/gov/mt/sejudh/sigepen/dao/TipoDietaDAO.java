package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoDieta;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.TipoDieta;

public class TipoDietaDAO extends BaseDAO<TipoDieta, Long> {

	private static final long serialVersionUID = 7349792788091954256L;

	@SuppressWarnings("unchecked")
	public List<TipoDieta> buscaPorFiltro(FiltroTipoDieta filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(TipoDieta.class, em);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(TipoDieta.class));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoDieta> listarTodos() {
		String jpql = "select td from TipoDieta td ";
		Query query = em.createQuery(jpql, TipoDieta.class);
		List<TipoDieta> tiposDieta = query.getResultList();

		return tiposDieta;
	}

	@SuppressWarnings("unchecked")
	public List<TipoDieta> listarAtivos() {
		String jpql = "select td from TipoDieta td where td.status = :status ";
		Query query = em.createQuery(jpql, TipoDieta.class);
		query.setParameter("status", Status.ATIVO);
		List<TipoDieta> tiposDieta = query.getResultList();

		return tiposDieta;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoDieta> listarEspecialAtivas() {
		String jpql = "select td from TipoDieta td where status = :status and especial = 1 ";
		Query query = em.createQuery(jpql, TipoDieta.class);
		query.setParameter("status", Status.ATIVO);
		List<TipoDieta> tiposDieta = query.getResultList();

		return tiposDieta;
	}
		
	@SuppressWarnings("unchecked")
	public boolean existeDietaPadrao() {
		boolean result = false;
		String jpql = "select t from TipoDieta t where t.especial = 0 and t.status = 1 ";
		Query query = em.createQuery(jpql, TipoDieta.class);
		List<TipoDieta> tipoDieta = (List<TipoDieta>) query.getResultList();
		for(TipoDieta dieta : tipoDieta ) {
			return dieta != null && dieta.getId() != null && dieta.getId() > 0;	
		}

		return result;
	}

}
