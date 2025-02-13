package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.Estrutura;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRemanejamento;
import br.gov.mt.sejudh.sigepen.model.Remanejamento;
import br.gov.mt.sejudh.sigepen.model.dto.RemanejamentoDTO;


@RequestScoped
public class RemanejamentoDAO {
	
	@Inject
	private EntityManager entityManager;

	@Inject
	private EstruturaDAO estruturaDAO;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(Remanejamento remanejamento) {
		if(remanejamento.getId()>0){
			remanejamento = entityManager.merge(remanejamento);
		}else{
			entityManager.persist(remanejamento);	
		}
	}

	public void excluir(Remanejamento remanejamento) {
		entityManager.remove(entityManager.merge(remanejamento));
	}	
		
	@SuppressWarnings("unchecked")
	public List<RemanejamentoDTO> buscaPorFiltro(FiltroRemanejamento filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Remanejamento.class, entityManager);		
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(RemanejamentoDTO.class));
		
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroRemanejamento filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Remanejamento.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}	
	
	public Remanejamento buscaRemanejamentoPorId(long id) {
		try {
			String jpql="select r from Remanejamento r "
					+ "join fetch r.pedido p "
					+ "join fetch r.unidadeRemanejada ur "
					+ "join fetch r.estruturaDestino ed "
					+ "join fetch p.unidadeOrigem u "
					+ "join fetch p.recuperando rec "
					+ "join fetch rec.passagens pas "
					+ "join fetch pas.lotacao lot "
					+ "where r.id = :id "
					+ "and r.pedido.id = p.id "
					+ "and p.recuperando.id = rec.id";
			TypedQuery<Remanejamento> query = entityManager.createQuery(jpql, Remanejamento.class);
			query.setParameter("id", id);			
			Remanejamento r = query.getSingleResult();

			estruturaDAO.buscarEstruturasCompleta(r.getEstruturaDestino());

			return r;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	private boolean existeAlias(Criteria criteria, String alias) {
	    return criteria.toString().contains(alias);
	}


}
