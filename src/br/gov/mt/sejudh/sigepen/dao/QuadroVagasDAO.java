package br.gov.mt.sejudh.sigepen.dao;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public List<QuadroVagasDTO> buscaQuantidadeRecuperados(FiltroQuadroVagas filtroQuadroVagas, boolean isCount, boolean relatorio) {
		
		String filtro = montarFiltro(filtroQuadroVagas);
		String orderBy = montarGroupBy(filtroQuadroVagas);
		
		String jpql = "SELECT new br.gov.mt.sejudh.sigepen.model.dto.QuadroVagasDTO("
				+ " SUM(CASE WHEN r.condicaoAtual = 'CONDENADO' THEN 1 ELSE 0 END) as quantidadeCondenado,"
				+ " SUM(CASE WHEN r.condicaoAtual = 'PROVISORIO' THEN 1 ELSE 0 END) as quantidadeProvisorios,"
				+ " COUNT(*) as capacidadeTotal, up.id as id, up.sigla as sigla, up.nome as nomeUnidade, e.situacaoUnidade, c.nome as cidade)"
				+ " FROM PassagemLotacao pl"
				+ " INNER JOIN pl.passagem p"
				+ " INNER JOIN pl.estrutura e"
				+ " INNER JOIN e.unidadePrisional up"
				+ " INNER JOIN p.recuperando r"
				+ " INNER JOIN up.endereco en"
				+ " INNER JOIN en.cidade c"
				+ " WHERE pl.dataSaida IS NULL AND e.cela = 1"
				+ " AND r.condicaoAtual IN('CONDENADO', 'PROVISORIO')"
				+ filtro
				+ " GROUP BY up.estrutura.id, up.id, up.nome, e.situacaoUnidade, up.sigla, c.nome "
				+  orderBy;	
		Query query = entityManager.createQuery(jpql, QuadroVagasDTO.class);
		
		if(!isCount && !relatorio) {
			query.setFirstResult(filtroQuadroVagas.getPaginacaoIndex());
			query.setMaxResults(filtroQuadroVagas.getQuantidadeRegistros());
		}
		
		return query.getResultList();
	}
	
	private String montarGroupBy(FiltroQuadroVagas filtroQuadroVagas) {
		Set<String> noFiltro = new HashSet<>(Arrays.asList("vagas", "deficit", "totalRecuperando"));
		String ordenador = " asc";
		String orderBy = " ORDER BY id";
		if(filtroQuadroVagas.getOrdenacao() != null && !filtroQuadroVagas.getOrdenacao().getPropertyName().isEmpty()){
			if(!noFiltro.contains(filtroQuadroVagas.getOrdenacao().getPropertyName())) {				
				orderBy = "ORDER BY " + filtroQuadroVagas.getOrdenacao().getPropertyName();
				ordenador = filtroQuadroVagas.isAscendente() ? " asc" : " desc";
			}
						
		}
		return orderBy + ordenador;
	}
	
	private String montarFiltro(FiltroQuadroVagas filtroQuadroVagas) {
		String filtro = " ";
		if(filtroQuadroVagas.getSigla() != null && !filtroQuadroVagas.getSigla().isEmpty()) {
			filtro += consultaLikeIgnoraAcentos("up.sigla",filtroQuadroVagas.getSigla());
		}
		if(filtroQuadroVagas.getNome() != null && !filtroQuadroVagas.getNome().isEmpty()) {
			filtro += consultaLikeIgnoraAcentos("up.nome",filtroQuadroVagas.getNome());
		}
		
		if(filtroQuadroVagas.getCidade() != null && !filtroQuadroVagas.getCidade().isEmpty()) {
			filtro += consultaLikeIgnoraAcentos("c.nome",filtroQuadroVagas.getCidade());
		}
		
		return filtro;
	}
	
	private String consultaLikeIgnoraAcentos(String coluna, String param) {
		return " AND translate(lower("+ coluna +"), 'àáâãäèéêëìíîïòóôõöùúûüç', 'aaaaaeeeeiiiiooooouuuuc') LIKE translate('%" + param.toLowerCase() + "%', 'àáâãäèéêëìíîïòóôõöùúûüç', 'aaaaaeeeeiiiiooooouuuuc')";
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
