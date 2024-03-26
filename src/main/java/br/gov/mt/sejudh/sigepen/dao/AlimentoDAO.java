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
		String jpql = "select a from Alimento a where status = :status";
		Query query = em.createQuery(jpql, Alimento.class);
		query.setParameter("status", Status.ATIVO);
		List<Alimento> alimentos = query.getResultList();

		return alimentos;
	}
	
	public Alimento buscarAlimentoPorNome(Alimento alimento) {
		String jpql = "select a from Alimento a where status = :status and descricao = :descricao";
		Query query = em.createQuery(jpql, Alimento.class);
		query.setParameter("status", Status.ATIVO);
		query.setParameter("descricao", alimento.getDescricao());
		Alimento alimentoRetorno = new Alimento();
		try {
			alimentoRetorno = (Alimento) query.getSingleResult();			
		}catch(NoResultException e) {
			return null;
		}

		return alimentoRetorno;
	}

}
