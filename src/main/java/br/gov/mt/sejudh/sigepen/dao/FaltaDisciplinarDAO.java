package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.FaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.model.dto.FaltaDisciplinarDTO;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;


@RequestScoped
public class FaltaDisciplinarDAO {
	
	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(FaltaDisciplinar faltaDisciplinar) {
		if(faltaDisciplinar.getId()>0){
			faltaDisciplinar = entityManager.merge(faltaDisciplinar);
		}else{
			entityManager.persist(faltaDisciplinar);	
		}
	}

	public void excluir(FaltaDisciplinar pedido) {
		entityManager.remove(entityManager.merge(pedido));
	}	
		
	@SuppressWarnings("unchecked")
	public List<FaltaDisciplinarDTO> buscaPorFiltro(FiltroFaltaDisciplinar filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(FaltaDisciplinar.class, entityManager, isBuscaRelatorio);			
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(FaltaDisciplinarDTO.class));
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroFaltaDisciplinar filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(FaltaDisciplinar.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	public FaltaDisciplinar buscarFaltaDisciplinarCompletaPorId(long id) {
		try {
			String jpql="select p from FaltaDisciplinar p where p.id = :id";
			TypedQuery<FaltaDisciplinar> query = entityManager.createQuery(jpql, FaltaDisciplinar.class);
			query.setParameter("id", id);
			
			FaltaDisciplinar p = query.getSingleResult();
			Hibernate.initialize(p.getEstruturaOcorrencia());
			Hibernate.initialize(p.getEstruturaOcorrencia().getUnidadePrisional());
			Hibernate.initialize(p.getRecuperando().getFotos());

			return p;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<FaltaDisciplinar> listarFaltaDisciplinarAtivo() {
		String jpql="select fd from FaltaDisciplinar fd where fd.status = :status order by fd.data";
		Query query = entityManager.createQuery(jpql, FaltaDisciplinar.class);
		query.setParameter("status", Status.ATIVO);
		List<FaltaDisciplinar> faltaDisciplinars = query.getResultList();
		return faltaDisciplinars;
	}
}
