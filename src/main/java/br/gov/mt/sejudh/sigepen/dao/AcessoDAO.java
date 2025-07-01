package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Acesso;
import br.gov.mt.sejudh.sigepen.model.Alimento;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.transform.AliasToBeanResultTransformer;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class AcessoDAO extends BaseDAO<Acesso, Long> {

	private static final long serialVersionUID = 3689374908012254663L;

	public List<Acesso> buscarAcessosPorUsuarioId(long idUsuario) {
		String jpql = "select a from Acesso a where usuario.id = :idUsuario ";
		Query query = em.createQuery(jpql, Acesso.class);
		query.setParameter("idUsuario", idUsuario);
		try {
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Acesso> buscarAcessosPorUsuarioIdAndUnidadeId(long idUsuario, long idUnidade) {
		String jpql = "select a from Acesso a where usuario.id = :idUsuario and unidade.id = :idUnidade ";
		Query query = em.createQuery(jpql, Acesso.class);
		query.setParameter("idUsuario", idUsuario);
		query.setParameter("idUnidade", idUnidade);
		try {
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
}
