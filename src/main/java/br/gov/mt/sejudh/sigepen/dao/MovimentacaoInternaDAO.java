package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroMovimentacaoInterna;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.model.Escolta;
import br.gov.mt.sejudh.sigepen.model.MovimentacaoInterna;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


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
					"LEFT JOIN FETCH p.estruturaDestino " +
					"LEFT JOIN FETCH p.estruturaOrigem " +
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

    public Long countPorUnidadeNaData(Long unidadeId, Date dataInicio, Date dataFim) {
        String jpql = "SELECT COUNT(m) FROM MovimentacaoInterna m " +
                "WHERE m.unidadeOrigem.id = :unidadeId " +
                "AND m.dataInicio BETWEEN :dataInicio AND :dataFim";
        try {
            return entityManager.createQuery(jpql, Long.class)
                    .setParameter("unidadeId", unidadeId)
                    .setParameter("dataInicio", dataInicio)
                    .setParameter("dataFim", dataFim)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }
}
