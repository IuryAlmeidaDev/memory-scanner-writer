package br.gov.mt.sejudh.sigepen.dao;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.Escolta;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroMovimentacaoInterna;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.model.MovimentacaoInterna;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;


@RequestScoped
public class MovimentacaoInternaDAO {
	
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

	public void salvar(MovimentacaoInterna movimentacao) {
		if(movimentacao.getId()>0){
			movimentacao = entityManager.merge(movimentacao);
		}else{
			entityManager.persist(movimentacao);
		}
	}

	public void excluir(MovimentacaoInterna movimentacao) {
		entityManager.remove(entityManager.merge(movimentacao));
	}

	@SuppressWarnings("unchecked")
	public List<MovimentacaoInterna> buscaPorFiltro(FiltroMovimentacaoInterna filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(MovimentacaoInterna.class, entityManager, isBuscaRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}

	public int quantidadeFiltrados(FiltroMovimentacaoInterna filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(MovimentacaoInterna.class, entityManager);
	    criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	public MovimentacaoInterna buscaMovimentacaoPorId(long id) {
		try {
			String jpql = "SELECT DISTINCT m FROM MovimentacaoInterna m " +
					"LEFT JOIN FETCH m.pedidos p " +
					"LEFT JOIN FETCH m.unidadeOrigem " +
					"LEFT JOIN p.estruturaDestino " +
					"LEFT JOIN FETCH p.recuperando rec " +
					"LEFT JOIN FETCH rec.pessoa " +
					"LEFT JOIN rec.passagens passagem " +
					"LEFT JOIN passagem.lotacao " +
					"WHERE m.id = :id";

			TypedQuery<MovimentacaoInterna> query = entityManager.createQuery(jpql, MovimentacaoInterna.class);
			query.setParameter("id", id);

			MovimentacaoInterna m = query.getSingleResult();

			if (m.getEscolta() != null) {
				String jpqlEscolta = "SELECT e FROM Escolta e " +
						"LEFT JOIN e.movimentacaoInterna m " +
						"WHERE m.id = :id";
				TypedQuery<Escolta> queryEscolta = entityManager.createQuery(jpqlEscolta, Escolta.class);
				queryEscolta.setParameter("id", m.getId());
				List<Escolta> escoltas = queryEscolta.getResultList();
				m.setEscolta(escoltas);
			}

			m.getPedidos().forEach(pedidoTransito -> estruturaDAO.buscarEstruturasCompleta(pedidoTransito.getEstruturaDestino()));



			return m;
		} catch (NoResultException e) {
			return null;
		}
	}
//	public MovimentacaoInterna buscaMovimentacaoPorId(long id) {
//		try {
//			String jpql="select m from MovimentacaoInterna m where m.id = :id";
//			TypedQuery<MovimentacaoInterna> query = entityManager.createQuery(jpql, MovimentacaoInterna.class);
//			query.setParameter("id", id);
//			MovimentacaoInterna m = query.getSingleResult();
//			Hibernate.initialize(m.getPedidos());
//			Hibernate.initialize(m.getEscolta());
//			Hibernate.initialize(m.getUnidadeOrigem());
//
//			for (PedidoTransito p : m.getPedidos()) {
//				Hibernate.initialize(p.getEstruturaDestino());
//				Hibernate.initialize(p.getRecuperando());
//				Hibernate.initialize(p.getRecuperando().getPessoa());
//				Hibernate.initialize(p.getRecuperando().getPassagens());
//
//				for (Passagem pa : p.getRecuperando().getPassagens()) {
//					Hibernate.initialize(pa.getLotacao());
//				}
//			}
//
//			return m;
//		} catch (NoResultException e) {
//			return null;
//		}
//	}

	public boolean isPedidoFinalizadoOuCancelado(long pedidoId) {
		try {
			Query query = entityManager.createQuery("select COUNT(1) from PedidoTransito p where p.status in (:status) AND p.id = :pedidoId");
			query.setParameter("status", Arrays.asList(StatusPedido.FINALIZADO, StatusPedido.CANCELADO));
			query.setParameter("pedidoId", pedidoId);
			long quantidade = (Long) query.getSingleResult();
			return (quantidade > 0);
		} catch (NoResultException e) {
			return false;
		}
	}
}
