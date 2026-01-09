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
import org.hibernate.transform.Transformers;

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
	public List<QuadroVagasDTO> buscaQuantidadeRecuperados(
	        FiltroQuadroVagas filtroQuadroVagas, boolean relatorio) {

	    String filtro = montarFiltro(filtroQuadroVagas);

	    String jpql =
	        "SELECT new br.gov.mt.sejudh.sigepen.model.dto.QuadroVagasDTO(" +
	        " SUM(CASE WHEN r.condicaoAtual = 'CONDENADO' THEN 1 ELSE 0 END), " +
	        " SUM(CASE WHEN r.condicaoAtual = 'PROVISORIO' THEN 1 ELSE 0 END), " +
	        " 0L, " +
	        " COUNT(pl.id), " +
	        " 0L, " +
	        " 0L, " +
	        " up.id, up.sigla, up.nome, e.situacaoUnidade, c.nome) " +
	        "FROM PassagemLotacao pl " +
	        "JOIN pl.passagem p " +
	        "JOIN pl.estrutura e " +
	        "JOIN e.unidadePrisional up " +
	        "JOIN p.recuperando r " +
	        "JOIN up.endereco en " +
	        "JOIN en.cidade c " +
	        "WHERE pl.dataSaida IS NULL AND e.cela = 1 " +
	        filtro +
	        " GROUP BY up.id, up.sigla, up.nome, e.situacaoUnidade, c.nome";

	    return entityManager
	            .createQuery(jpql, QuadroVagasDTO.class)
	            .getResultList();
	}
	
	private String montarFiltro(FiltroQuadroVagas filtroQuadroVagas) {

	    if (filtroQuadroVagas == null) {
	        return "";
	    }

	    String filtro = "";

	    if (filtroQuadroVagas.getSigla() != null && !filtroQuadroVagas.getSigla().isEmpty()) {
	        filtro += consultaLikeIgnoraAcentos("up.sigla", filtroQuadroVagas.getSigla());
	    }

	    if (filtroQuadroVagas.getNome() != null && !filtroQuadroVagas.getNome().isEmpty()) {
	        filtro += consultaLikeIgnoraAcentos("up.nome", filtroQuadroVagas.getNome());
	    }

	    if (filtroQuadroVagas.getCidade() != null && !filtroQuadroVagas.getCidade().isEmpty()) {
	        filtro += consultaLikeIgnoraAcentos("c.nome", filtroQuadroVagas.getCidade());
	    }

	    return filtro;
	}

	
	private String consultaLikeIgnoraAcentos(String coluna, String param) {
		return " AND translate(lower("+ coluna +"), 'àáâãäèéêëìíîïòóôõöùúûüç', 'aaaaaeeeeiiiiooooouuuuc') LIKE translate('%" + param.toLowerCase() + "%', 'àáâãäèéêëìíîïòóôõöùúûüç', 'aaaaaeeeeiiiiooooouuuuc')";
	}
	
	public Long quantidadeFiltrados(FiltroQuadroVagas filtroQuadroVagas) {
	    String filtro = montarFiltro(filtroQuadroVagas);

	    String jpql =
	        "SELECT COUNT(DISTINCT up.id) " +
	        "FROM PassagemLotacao pl " +
	        "JOIN pl.passagem p " +
	        "JOIN pl.estrutura e " +
	        "JOIN e.unidadePrisional up " +
	        "JOIN p.recuperando r " +
	        "JOIN up.endereco en " +
	        "JOIN en.cidade c " +
	        "WHERE pl.dataSaida IS NULL " +
	        "AND e.cela = 1 " +
	        filtro;

	    return entityManager
	        .createQuery(jpql, Long.class)
	        .getSingleResult();
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
