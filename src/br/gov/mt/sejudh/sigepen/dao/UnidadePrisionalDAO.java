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
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;


@RequestScoped
public class UnidadePrisionalDAO {
	
	@Inject
	private EntityManager entityManager;

	public UnidadePrisional salvar(UnidadePrisional unidade) {
		if(unidade.getId()>0){
			entityManager.merge(unidade);
		}else{			
			entityManager.persist(unidade);	
		}
		return unidade;
	}
	
	public void alterar(UnidadePrisional unidade){
		unidade = entityManager.merge(unidade);		
	}

	public void excluir(long idUnidadePrisional) {
		UnidadePrisional u = buscaPorId(idUnidadePrisional);
		entityManager.remove(entityManager.merge(u));		
	}

	public UnidadePrisional buscaPorId(Long id) {
		String jpql="select u from UnidadePrisional u left join fetch u.estrutura left join fetch u.responsavel left join fetch u.contatos where u.id = :id";
				Query query = entityManager.createQuery(jpql, UnidadePrisional.class);
				query.setParameter("id", id);
				UnidadePrisional unidade = (UnidadePrisional) query.getSingleResult();
				Hibernate.initialize(unidade.getEndereco());
				Hibernate.initialize(unidade.getTipoUnidade());
				Hibernate.initialize(unidade.getContatos());
		return unidade;
	}
	
	public UnidadePrisional buscaSimplesPorId(Long id) {
		String jpql="select new br.gov.mt.sejudh.sigepen.model.UnidadePrisional(u.id, u.nome) from UnidadePrisional u where u.id = :id";
				Query query = entityManager.createQuery(jpql, UnidadePrisional.class);
				query.setParameter("id", id);
				UnidadePrisional unidade = (UnidadePrisional) query.getSingleResult();
		return unidade;
	}
	
	@SuppressWarnings("unchecked")
	public List<UnidadePrisionalDTO> buscaPorFiltro(FiltroUnidadePrisional filtro, boolean isRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(UnidadePrisional.class, entityManager, isRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(UnidadePrisionalDTO.class));
		return criteria.list();
	}
	
	public int quantidadeFiltrados(FiltroUnidadePrisional filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(UnidadePrisional.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	public boolean jaExisteUnidadePrisionalComMesmoNome(UnidadePrisional unidadePrisional) {
		Query query = entityManager.createQuery("select count(u) from UnidadePrisional u where lower(u.nome) = :nome  AND u.id <> :unidadeId  AND ( u.estrutura.situacaoUnidade="+ SituacaoUnidade.ATIVO.ordinal() +""
																															+ " OR u.estrutura.situacaoUnidade="+ SituacaoUnidade.INTERDITADO_PARCIAL.ordinal() +""
																															+ " OR u.estrutura.situacaoUnidade="+ SituacaoUnidade.INTERDITADO_TOTAL.ordinal() +")");
		query.setParameter("nome", unidadePrisional.getNome().toLowerCase());
		query.setParameter("unidadeId", unidadePrisional.getId());
		return (long) query.getSingleResult() > 0;
	}
	
	public List<UnidadePrisional> listarUnidadesNaoInativas(){
		try {
			String jpql="select new br.gov.mt.sejudh.sigepen.model.UnidadePrisional(u.id, u.nome) from UnidadePrisional u where u.estrutura.situacaoUnidade <> :situacao";
			TypedQuery<UnidadePrisional> query = entityManager.createQuery(jpql, UnidadePrisional.class);
			query.setParameter("situacao", SituacaoUnidade.INATIVO);
			
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Long quantidadeAtual(Long id) {
		String jpql="SELECT count(pl.id) FROM PassagemLotacao as pl INNER JOIN pl.estrutura as est_ WHERE est_.unidadePrisional.id = :id AND pl.dataSaida IS NULL";
		Query query = entityManager.createQuery(jpql, Long.class);
		query.setParameter("id", id);
		return (Long) query.getSingleResult();
	}

	public boolean possuiServidorLotado(long unidadeId) {
		String sql = "select count(l.id) from Lotacao l where l.unidade.id = :unidadeId and l.dataFim is null";
		Query query = entityManager.createQuery(sql, Long.class);
		query.setParameter("unidadeId", unidadeId);
		return (long) query.getSingleResult() > 0;
	}

	public Boolean verificarSeBiometriaEhObrigatoria(long unidadeId) {
		String sql = "select u.biometriaObrigatoria from UnidadePrisional u where u.id = :unidadeId";
		Query query = entityManager.createQuery(sql, Boolean.class);
		query.setParameter("unidadeId", unidadeId);
		return Boolean.parseBoolean(query.getSingleResult().toString());
	}
}
