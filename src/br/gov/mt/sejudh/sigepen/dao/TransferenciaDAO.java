package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTransferencia;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Transferencia;


@RequestScoped
public class TransferenciaDAO {
	
	@Inject
	private EntityManager entityManager;
	
	public TransferenciaDAO(){		
		
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(Transferencia transferencia) {
		if(transferencia.getId()>0){
			transferencia = entityManager.merge(transferencia);
		}else{
			entityManager.persist(transferencia);	
		}
	}

	public void excluir(Transferencia transferencia) {
		entityManager.remove(entityManager.merge(transferencia));
	}	
		
	@SuppressWarnings("unchecked")
	public List<Transferencia> buscaPorFiltro(FiltroTransferencia filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Transferencia.class, entityManager, isBuscaRelatorio);	
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroTransferencia filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Transferencia.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}	
	
	public Transferencia buscaTransfernciaPorId(long id) {
		try {
			String jpql="select t from Transferencia t where t.id = :id";
			TypedQuery<Transferencia> query = entityManager.createQuery(jpql, Transferencia.class);
			query.setParameter("id", id);			
			
			Transferencia t = query.getSingleResult();
			
			Hibernate.initialize(t.getPedidos());
			Hibernate.initialize(t.getEscolta());
			Hibernate.initialize(t.getUnidadeOrigem());
			
			for (PedidoTransito p : t.getPedidos()) {
				Hibernate.initialize(p.getUnidadeDestino());
			}
			
			return t;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public boolean possuiPedidoEmAndamento(Transferencia transferencia){
		try {;
		String jpql="select COUNT(p) from PedidoTransito p where p.transferencia = :transferencia AND p.status = :status";
		 Query query = entityManager.createQuery(jpql);		
		query.setParameter("transferencia",transferencia);
		query.setParameter("status", StatusPedido.EM_ANDAMENTO);
		long quantidade = (Long)query.getSingleResult();
		return quantidade>0;
	} catch (NoResultException e) {
		return true;
	}	
		
	}
}
