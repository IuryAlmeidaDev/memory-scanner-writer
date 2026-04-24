package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import br.gov.mt.sejudh.sigepen.enumerator.ParametroVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.model.Acesso;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

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
				Hibernate.initialize(unidade.getParametrosVisita());
		return unidade;
	}
	
	public UnidadePrisional buscaSimplesPorId(Long id) {
		String jpql="select new br.gov.mt.sejudh.sigepen.model.UnidadePrisional(u.id, u.nome) from UnidadePrisional u where u.id = :id";
				Query query = entityManager.createQuery(jpql, UnidadePrisional.class);
				query.setParameter("id", id);
				UnidadePrisional unidade = (UnidadePrisional) query.getSingleResult();
		return unidade;
	}

	public List<UnidadePrisional> listarPorIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return new ArrayList<>();
		}
		String jpql = "select new br.gov.mt.sejudh.sigepen.model.UnidadePrisional(u.id, u.nome) from UnidadePrisional u where u.id in :ids order by u.nome";
		TypedQuery<UnidadePrisional> query = entityManager.createQuery(jpql, UnidadePrisional.class);
		query.setParameter("ids", ids);
		return query.getResultList();
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
	
	@SuppressWarnings("unchecked")
	public List<UnidadePrisional> listarUnidadesComContratosAtivo(){
		
		try {
			StringBuilder jpql = new StringBuilder(
					"select DISTINCT u.id, u.nome, e.ID as estruturaId FROM " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c ");
			jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO_UNIDADE_PRISIONAL cup ON cup.CONTRATO_ID = c.ID ");
			jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".UNIDADE_PRISIONAL u ON cup.UNIDADE_PRISIONAL_ID = u.ID ");
			jpql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".ESTRUTURA e ON e.UNIDADEPRISIONAL_ID = u.ID AND ESTRUTURAPAI_ID IS null ");
			Query query = entityManager.createNativeQuery(jpql.toString());
			
			return (List<UnidadePrisional>) query.getResultList().stream()
					.map(row -> new UnidadePrisional((Object[]) row)).collect(Collectors.toList());
		} catch (NoResultException e) {
			return null;
		}		
	}
	
	public List<UnidadePrisional> listarUnidadesNaoInativasAndEstrutura(){
		try {
			String jpql="select new br.gov.mt.sejudh.sigepen.model.UnidadePrisional(u.id, u.nome, e) from UnidadePrisional u INNER JOIN u.estrutura e where u.estrutura.situacaoUnidade <> :situacao";
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
	
	public List<UnidadePrisional> listarUnidadesComParamVisitanteAtivo(boolean listarTodasUnidades){
		List<UnidadePrisional> retorno = new ArrayList<UnidadePrisional>();
		List<UnidadePrisional> unidades = new ArrayList<UnidadePrisional>();
		for (Acesso acesso : SecurityUtil.getUsuario().getAcessos()) {
			unidades.add(acesso.getUnidade());
		}

		try {
			String jpql="select u from UnidadePrisional u join fetch u.parametrosUnidade pu WHERE pu.nome =:paramNome and pu.validar =:paramValidar ORDER BY u.nome ";
			TypedQuery<UnidadePrisional> query = entityManager.createQuery(jpql, UnidadePrisional.class);
			query.setParameter("paramNome", ParametroVisitante.RECEBIMENTO_DOCUMENTACAO);
			query.setParameter("paramValidar", SimNao.S);
			List<UnidadePrisional> lista = query.getResultList();
			if(listarTodasUnidades) {
				return lista;
			}
			for(UnidadePrisional l : lista) {
				for(UnidadePrisional u : unidades) {
					if(u.getId() == l.getId()) {
						retorno.add(l);
					}
				}
			}
			return retorno;
		} catch (NoResultException e) {
			return null;
		}
	}
}
