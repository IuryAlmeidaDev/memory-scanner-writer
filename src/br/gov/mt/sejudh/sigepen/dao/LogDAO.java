package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroLog;
import br.gov.mt.sejudh.sigepen.model.Log;

@RequestScoped
public class LogDAO {

	@Inject
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<Log> buscarLogsPorFiltro(FiltroLog filtro, boolean isRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Log.class, em, isRelatorio);
		return criteria.list();
	}
	
	public int buscarQuantidadeFiltrada(FiltroLog filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Log.class, em);		
		criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	public long buscarIdUsuarioPorServidorId(long servidorId) {
		try {
			String jpql="select u.id from Usuario u where u.servidor.id = :id";
			Query query = em.createQuery(jpql);
			query.setParameter("id", servidorId);
			return (long) query.getSingleResult();
		} catch (NoResultException e) {
			return  0;
		}
	}
}
