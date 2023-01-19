package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroQuadroVagas;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.QuadroVagasDTO;

@RequestScoped
public class QuadroVagasDAO {
	
	@Inject
	private EntityManager entityManager;

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
	public List<QuadroVagasDTO> buscaQuantidadeRecuperados(FiltroQuadroVagas filtroUnidadePrisional, boolean isCount, boolean relatorio) {
		
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
		
		if(!isCount && !relatorio) {
			query.setFirstResult(filtroUnidadePrisional.getPaginacaoIndex());
			query.setMaxResults(filtroUnidadePrisional.getQuantidadeRegistros());
		}
		
		return query.getResultList();
	}
	
	private String montarFiltro(FiltroQuadroVagas filtroUnidadePrisional) {
		String filtro = " ";
		if(filtroUnidadePrisional.getSigla() != null && !filtroUnidadePrisional.getSigla().isEmpty()) {
			filtro = " AND upper(lr.siglaUnidade) = '" + filtroUnidadePrisional.getSigla().toUpperCase() + "'";
		}
		if(filtroUnidadePrisional.getNome() != null && !filtroUnidadePrisional.getNome().isEmpty()) {			
			filtro = " AND upper(lr.nomeUnidade) = '" + filtroUnidadePrisional.getNome().toUpperCase() + "'";
		}
		
		return filtro;
	}
	
	
	public int quantidadeFiltrados(FiltroQuadroVagas filtro) {	
		return buscaQuantidadeRecuperados(filtro,true, false).size();
	}
	
	public boolean jaExisteUnidadePrisionalComMesmoNome(UnidadePrisional unidadePrisional) {
		Query query = entityManager.createQuery("select count(u) from UnidadePrisional u where lower(u.nome) = :nome  AND u.id <> :unidadeId  AND ( u.estrutura.situacaoUnidade="+ SituacaoUnidade.ATIVO.ordinal() +""
																															+ " OR u.estrutura.situacaoUnidade="+ SituacaoUnidade.INTERDITADO_PARCIAL.ordinal() +""
																															+ " OR u.estrutura.situacaoUnidade="+ SituacaoUnidade.INTERDITADO_TOTAL.ordinal() +")");
		query.setParameter("nome", unidadePrisional.getNome().toLowerCase());
		query.setParameter("unidadeId", unidadePrisional.getId());
		return (long) query.getSingleResult() > 0;
	}
	
	public Long quantidadeAtual(Long id) {
		String jpql="SELECT count(pl.id) FROM PassagemLotacao as pl INNER JOIN pl.estrutura as est_ WHERE est_.unidadePrisional.id = :id AND pl.dataSaida IS NULL";
		Query query = entityManager.createQuery(jpql, Long.class);
		query.setParameter("id", id);
		return (Long) query.getSingleResult();
	}

}
