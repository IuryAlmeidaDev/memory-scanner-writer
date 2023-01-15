package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
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
import br.gov.mt.sejudh.sigepen.enumerator.ParametroVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.model.Acesso;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.QuadroVagasDTO;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@RequestScoped
public class QuadroVagasDAO {
	
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
	
	@SuppressWarnings("unchecked")
	public List<QuadroVagasDTO> buscaPorFiltro(FiltroUnidadePrisional filtro, boolean isRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(UnidadePrisional.class, entityManager, isRelatorio);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(QuadroVagasDTO.class));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<QuadroVagasDTO> buscaCapacidadeTotalUnidade() {
		
		String jpql = "SELECT new br.gov.mt.sejudh.sigepen.model.dto.QuadroVagasDTO("
				+ " sum(e.capacidade), e.unidadePrisional.id)"
				+ " FROM Estrutura e"
				+ " WHERE e.cela = 1"
				+ " GROUP BY e.unidadePrisional.id"
				+ " ORDER BY e.unidadePrisional.id";	
		
		Query query = entityManager.createQuery(jpql, QuadroVagasDTO.class);
		
		List<QuadroVagasDTO> quadro = query.getResultList();
		
		return quadro;
	}
	
	@SuppressWarnings("unchecked")
	public List<QuadroVagasDTO> buscaQuantidadeRecuperados(FiltroUnidadePrisional filtroUnidadePrisional) {
		
		String filtro = montarFiltro(filtroUnidadePrisional);
		
		String jpql = "SELECT new br.gov.mt.sejudh.sigepen.model.dto.QuadroVagasDTO("
				+ " SUM(CASE WHEN lr.condicaoAtual = 'CONDENADO' THEN 1 ELSE 0 END),"
				+ " SUM(CASE WHEN lr.condicaoAtual = 'PROVISORIO' THEN 1 ELSE 0 END),"
				+ " COUNT(*), lr.unidadeId, lr.siglaUnidade, lr.nomeUnidade)"
				+ " FROM RecuperandoView lr"
				+ " WHERE lr.regimeId IN(1,2)"
				+ " AND lr.condicaoAtual IN('CONDENADO', 'PROVISORIO')"
				+ filtro
				+ " GROUP BY lr.unidadeId, lr.siglaUnidade, lr.nomeUnidade"
				+ " ORDER BY lr.unidadeId";	
		
		Query query = entityManager.createQuery(jpql, QuadroVagasDTO.class);

		return query.getResultList();
	}
	
	private String montarFiltro(FiltroUnidadePrisional filtroUnidadePrisional) {
		String filtro = " ";
		filtro = filtroUnidadePrisional.getSigla() != null && !filtroUnidadePrisional.getSigla().isEmpty() ? " AND lr.siglaUnidade = '" + filtroUnidadePrisional.getSigla() + "'" : "";
		//filtro = filtroUnidadePrisional.getId() > 0  ? " AND lr.unidadeId = " + filtroUnidadePrisional.getId() : "";
		
		return filtro;
	}
	
	public long CapacidadeTotalUnidade(UnidadePrisional unidadePrisional) {
		Query query = entityManager.createQuery("SELECT sum(e.CAPACIDADE) FROM SIGEPEN_OWNER_DES.ESTRUTURA "
				+ " WHERE e.CELA = 1 AND e.UNIDADEPRISIONAL_ID = :unidadeId");		
		query.setParameter("unidadeId", unidadePrisional.getId());
		return (long) query.getSingleResult();
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
