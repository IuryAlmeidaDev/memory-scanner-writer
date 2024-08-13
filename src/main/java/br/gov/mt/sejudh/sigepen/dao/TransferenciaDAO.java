package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.Escolta;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;

import org.hibernate.type.LongType;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTransferencia;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Transferencia;
import br.gov.mt.sejudh.sigepen.model.dto.TransferenciaPedidoTransitoDTO;


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
		criteria.setFetchMode("pedidos", FetchMode.JOIN);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}	
	
	@SuppressWarnings("unchecked")
	public List<Transferencia> buscaPorFiltroRelatorio(FiltroTransferencia filtro, boolean isBuscaRelatorio) {
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
			
			t.setListTransferenciaPedidoTransitoDTO(this.buscarDadosPedidosDaTransferencia(id));
			
			Hibernate.initialize(t.getEscolta());
			Hibernate.initialize(t.getUnidadeOrigem());

			
			return t;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public Transferencia buscaTransfernciaPorIdIniciandoObjetos(long id) {
		try {
			String jpql = "SELECT t FROM Transferencia t " +
					"LEFT JOIN FETCH t.pedidos p " +
					"LEFT JOIN FETCH t.unidadeOrigem " +
					"LEFT JOIN FETCH p.unidadeDestino " +
					"LEFT JOIN FETCH p.recuperando rec " +
					"LEFT JOIN FETCH rec.pessoa " +
					"WHERE t.id = :id";

			TypedQuery<Transferencia> query = entityManager.createQuery(jpql, Transferencia.class);
			query.setParameter("id", id);

			Transferencia t = query.getSingleResult();

			if (t.getEscolta() != null) {
				Hibernate.initialize(t.getEscolta());
			}




			return t;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<TransferenciaPedidoTransitoDTO> buscarDadosPedidosDaTransferencia(Long id) {
  		Session session = entityManager.unwrap(Session.class);
  		StringBuilder sql = new StringBuilder();
  		
  		sql.append("select pt.transferencia_id as idTransferencia,    ");
  		sql.append("	   upo.nome 	 	as unidadeOrigem, 	      ");
  		sql.append("	   upo.id 	 	    as unidadeOrigemId, 	  ");
  		sql.append(" 	   pt.id            as nr,                    ");
  		sql.append("       r.id             as matricula,             ");
  		sql.append("       p.nome           as nome,                  ");
  		sql.append("       pt.data_prevista as dataPrevista,          ");
  		sql.append("       upd.nome         as unidadeDestino,        ");
  		sql.append("       pt.status        as statusStr              ");
  		sql.append(" from sigepen_owner.pedido_transito pt            ");
  		sql.append(" inner join sigepen_owner.unidade_prisional upd on upd.id = pt.unidade_destino_id ");
  		sql.append(" inner join sigepen_owner.recuperando r on r.id = pt.recuperando_id ");
  		sql.append(" inner join sigepen_owner.pessoa p on p.id = r.pessoa_id ");
  		sql.append(" inner join sigepen_owner.unidade_prisional upo on upo.id = pt.unidade_origem_id ");
  		sql.append(" where pt.transferencia_id = :id ");
  		
  		SQLQuery query = session.createSQLQuery(sql.toString());
  		query.setParameter("id", id);
  		query.addScalar("idTransferencia", LongType.INSTANCE);
  		query.addScalar("unidadeOrigem");
  		query.addScalar("unidadeOrigemId", LongType.INSTANCE);
  		query.addScalar("nr", LongType.INSTANCE);
  		query.addScalar("matricula", LongType.INSTANCE);
  		query.addScalar("nome");
  		query.addScalar("dataPrevista", DateType.INSTANCE);
  		query.addScalar("unidadeDestino");
  		query.addScalar("statusStr");
  		query.setResultTransformer(Transformers.aliasToBean(TransferenciaPedidoTransitoDTO.class));

		return  (List<TransferenciaPedidoTransitoDTO>) query.list();
	}
	
	
	public boolean possuiPedidoEmAndamento(Transferencia transferencia){
		try {

		 Query query = entityManager.createQuery("select COUNT(1) from PedidoTransito p where p.transferencia = :transferencia AND p.status = :status");		
		query.setParameter("transferencia",transferencia);
		query.setParameter("status", StatusPedido.EM_ANDAMENTO);
		long quantidade = (Long)query.getSingleResult();
		return quantidade>0;
		} catch (NoResultException e) {
			return true;
		}	
		
	}
	
	
	public boolean isTranferenciaEmAndamento(final long transferenciaId) {
		try {
			Query query = entityManager.createQuery("select COUNT(1) from Transferencia t where t.id = :transferenciaId AND t.status = :status");

			query.setParameter("transferenciaId", transferenciaId);
			query.setParameter("status", StatusMovimentacao.EM_ANDAMENTO);
			long quantidade = (Long) query.getSingleResult();
			return quantidade > 0;
		} catch (NoResultException e) {
			return false;
		}
	}

	public void cancelar(Transferencia transferencia) {
		Query query = entityManager.createQuery(
				"UPDATE Transferencia p SET p.status = :status " +
						"WHERE p.id = :id"
		);

		query.setParameter("status", transferencia.getStatus());
		query.setParameter("id", transferencia.getId());
		query.executeUpdate();
	}
}
