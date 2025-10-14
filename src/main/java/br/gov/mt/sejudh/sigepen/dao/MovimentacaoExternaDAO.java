package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroMovimentacaoExterna;
import br.gov.mt.sejudh.sigepen.model.MovimentacaoExterna;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;


@RequestScoped
public class MovimentacaoExternaDAO {
	
	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(MovimentacaoExterna movimentacao) {
		if(movimentacao.getId()>0){
			movimentacao = entityManager.merge(movimentacao);
		}else{
			entityManager.persist(movimentacao);	
		}
	}

	public void excluir(MovimentacaoExterna movimentacao) {
		entityManager.remove(entityManager.merge(movimentacao));
	}	
		
	@SuppressWarnings("unchecked")
	public List<MovimentacaoExterna> buscaPorFiltro(FiltroMovimentacaoExterna filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(MovimentacaoExterna.class, entityManager, isBuscaRelatorio);	
		criteria.setFetchMode("pedidos", FetchMode.JOIN);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroMovimentacaoExterna filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(MovimentacaoExterna.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}	
	
	public MovimentacaoExterna buscaMovimentacaoPorId(long id) {
		try {
			String jpql="select m from MovimentacaoExterna m where m.id = :id";
			TypedQuery<MovimentacaoExterna> query = entityManager.createQuery(jpql, MovimentacaoExterna.class);
			query.setParameter("id", id);
			MovimentacaoExterna m = query.getSingleResult();
			Hibernate.initialize(m.getPedidos());
			Hibernate.initialize(m.getEscolta());
			Hibernate.initialize(m.getUnidadeOrigem());

			for(PedidoTransito p : m.getPedidos()) {
				Hibernate.initialize(p.getEstabelecimento());
				Hibernate.initialize(p.getRecuperando().getPessoa());
				Hibernate.initialize(p.getTipoMovimentacao());
			}


			return m;
		} catch (NoResultException e) {
			return null;
		}
	}

    public Long countPorUnidadeNaData(Long unidadeId, Date dataInicio, Date dataFim) {
        String jpql = "SELECT COUNT(m) FROM MovimentacaoExterna m " +
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
