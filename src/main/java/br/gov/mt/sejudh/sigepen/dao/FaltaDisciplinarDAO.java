package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.FaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.FaltaDisciplinarDTO;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
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
	
	@SuppressWarnings("unchecked")
	public List<FaltaDisciplinar> listarFaltaDisciplinarPorRecuperandoId(Long id) {
		Session session = this.entityManager.unwrap(Session.class);

		 return session.createSQLQuery("select * from sigepen_owner.sgp_falta_disciplinar f where f.id_recuperando = :recuperandoId").addEntity(FaltaDisciplinar.class).setParameter("recuperandoId", id).list();
	}
	
	
	public PassagemLotacao buscaPassagemLotacaoPorRecuperandoId(Long id) {
		Session session = this.entityManager.unwrap(Session.class);
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT PASL.* FROM SIGEPEN_OWNER.PASSAGEM PAS                               ");
		sql.append("INNER JOIN SIGEPEN_OWNER.PASSAGEM_LOTACAO PASL ON PASL.PASSAGEM_ID = PAS.ID ");
		sql.append("WHERE PAS.RECUPERANDO_ID = :recuperandoId                                   ");
		sql.append("AND PAS.Data_Saida is null                                                  ");
		sql.append("and PASL.Data_Saida is null                                                 ");
		 return (PassagemLotacao) session.createSQLQuery(sql.toString()).addEntity(PassagemLotacao.class).setParameter("recuperandoId", id).uniqueResult();
	}
	
	
	public UnidadePrisional buscaUnidadePriosionalAtualRecuperandoId(Long id) {
		Session session = this.entityManager.unwrap(Session.class);
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT UP.* FROM SIGEPEN_OWNER.PASSAGEM PAS                                 ");
		sql.append("INNER JOIN SIGEPEN_OWNER.PASSAGEM_LOTACAO PASL ON PASL.PASSAGEM_ID = PAS.ID ");
		sql.append("INNER JOIN SIGEPEN_OWNER.ESTRUTURA E ON E.ID = PASL.ESTRUTURA_ID            ");
		sql.append("INNER JOIN SIGEPEN_OWNER.UNIDADE_PRISIONAL UP ON UP.ID = E.UNIDADEPRISIONAL_ID     ");
		sql.append("WHERE PAS.RECUPERANDO_ID = :recuperandoId                                   ");
		sql.append("AND PAS.Data_Saida is null                                                  ");
		sql.append("and PASL.Data_Saida is null                                                 ");
		 return (UnidadePrisional) session.createSQLQuery(sql.toString()).addEntity(UnidadePrisional.class).setParameter("recuperandoId", id).uniqueResult();
	}
	
}
