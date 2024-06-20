package br.gov.mt.sejudh.sigepen.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.annotations.QueryHints;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.model.EntradaSaida;
import br.gov.mt.sejudh.sigepen.model.EntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.dto.EntradaSaidaDTO;
import org.primefaces.model.SortOrder;

@RequestScoped
public class EntradaSaidaRecuperandoDAO {

	@Inject
	private EntityManager em;

	public int buscarQuantidadeFiltrada(FiltroEntradaSaidaRecuperando filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(EntradaSaidaRecuperando.class, em);
		criteria.addQueryHint(QueryHints.READ_ONLY);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<EntradaSaidaDTO> buscarEntradasPorFiltro(FiltroEntradaSaidaRecuperando filtro, boolean isRelatorio) {
		
		Criteria criteria = filtro.criarCriteriaParaBusca(EntradaSaidaRecuperando.class, em, isRelatorio);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(EntradaSaidaDTO.class));
		criteria.addQueryHint(QueryHints.READ_ONLY);
		
		List<EntradaSaidaDTO> entradas = criteria.list();

		return entradas;
	}


	public List<EntradaSaidaDTO> buscarEntradasPorFiltro(FiltroEntradaSaidaRecuperando filtro, String sortField, SortOrder sortOrder, int first) {
		StringBuilder jpql = new StringBuilder();
		if (sortOrder.name().equals("DESCENDING")) {
			jpql.append("SELECT * FROM ( ")
					.append("SELECT row_.*, rownum rownum_ FROM (")
					.append(script())
					.append(filtro.configureFiltros())
					.append(" order by ")
					.append(sortField)
					.append(" desc ) row_ ")
					.append("where rownum <= :rowFinal) where rownum_ > :rowInicial");

		} else {
			jpql.append("SELECT * FROM ( ")
					.append("SELECT row_.*, rownum rownum_ FROM (")
					.append(script())
					.append(filtro.configureFiltros())
					.append("order by ")
					.append(sortField)
					.append(" asc ) row_ ")
					.append("where rownum <= :rowFinal) where rownum_ > :rowInicial");

		}
		Query query =  em.createNativeQuery(jpql.toString(), EntradaSaidaDTO.class);
		filtro.configureParametos(query,first);
		return query.getResultList();
	}

	public int buscarEntradasPorFiltroCount(FiltroEntradaSaidaRecuperando filtro, String sortField, SortOrder sortOrder, int first) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT count(*) as id FROM ( ")
			.append("SELECT row_.*, rownum rownum_ FROM (")
			.append(script())
			.append(filtro.configureFiltros())
			.append("  ) row_ ")
			.append(") ");
		
		Query query = em.createNativeQuery(jpql.toString());
	    filtro.configureParametos(query, -1);
	    
	    BigDecimal result = (BigDecimal) query.getSingleResult();
	    
	    return result.intValue();
		
		
	}

	private String script() {
        return "SELECT " +
				"esv.ID AS id, " +
				"tipo_visita.DESC_TIPO_VISITA AS tipoVisita, " +
				"pessoa_v.NOME AS nomeVisitante, " +
				"vis.ID_VISITANTE AS idVisitante, " +
				"pessoa_resp.NOME AS nomeResponsavel, " +
				"resp.ID_VISITANTE AS idResponsavel, " +
				"parentesco.DESCRICAO AS parentesco, " +
				"cred.PROFISSAO AS profissao, " +
				"pessoa_rec.NOME AS nomeRecuperando, " +
				"esv.DATA_ENTRADA AS dataEntrada, " +
				"esv.DATA_SAIDA AS dataSaida, " +
				"esv.DATA_CANCELAMENTO AS dataCancelamento, " +
				"esv.SITUACAO AS situacao, " +
				"esv.MOTIVO_FINALIZACAO AS motivoFinalizacao, " +
				"esv.MOTIVO_ENTRADA AS motivoEntrada, " +
				"esv.MOTIVO_CANCELAMENTO AS motivoCancelamento, " +
				"up.ID AS idUnidade, " +
				"up.NOME AS nomeUnidade, " +
				"up.SIGLA AS siglaUnidade " +
				"FROM " +
				"SIGEPEN_OWNER.SGP_ENTRADA_SAIDA_RECUP esr " +
				"INNER JOIN " +
				"SIGEPEN_OWNER.SGP_CREDENCIAMENTO cred " +
				"ON esr.ID_CREDENCIAMENTO = cred.ID_CREDENCIAMENTO " +
				"LEFT OUTER JOIN " +
				"SIGEPEN_OWNER.PARENTE parente " +
				"ON cred.ID_PARENTE = parente.ID " +
				"LEFT OUTER JOIN " +
				"SIGEPEN_OWNER.PARENTESCO parentesco " +
				"ON parente.PARENTESCO_ID = parentesco.ID " +
				"LEFT OUTER JOIN " +
				"SIGEPEN_OWNER.RECUPERANDO rec " +
				"ON cred.ID_RECUPERANDO = rec.ID " +
				"LEFT OUTER JOIN " +
				"SIGEPEN_OWNER.PESSOA pessoa_rec " +
				"ON rec.PESSOA_ID = pessoa_rec.ID " +
				"INNER JOIN " +
				"SIGEPEN_OWNER.SGP_TIPO_VISITA tipo_visita " +
				"ON cred.ID_TIPO_VISITA = tipo_visita.ID_TIPO_VISITA " +
//				"INNER JOIN " +
//				"SIGEPEN_OWNER.UNIDADE_PRISIONAL up " +
//				"ON cred.ID_UNIDADE_PRISIONAL = up.ID " +
				"INNER JOIN " +
				"SIGEPEN_OWNER.SGP_ENTRADA_SAIDA esv " +
				"ON esr.ID_ENTRADA_SAIDA = esv.ID " +
				"INNER JOIN " +
				"SIGEPEN_OWNER.UNIDADE_PRISIONAL up " +
				"ON esv.ID_UNIDADE_PRISIONAL = up.ID " +
				"INNER JOIN " +
				"SIGEPEN_OWNER.SGP_VISITANTE vis " +
				"ON esv.ID_VISITANTE = vis.ID_VISITANTE " +
				"INNER JOIN " +
				"SIGEPEN_OWNER.PESSOA pessoa_v " +
				"ON vis.ID_PESSOA_VISITANTE = pessoa_v.ID " +
				"LEFT OUTER JOIN " +
				"SIGEPEN_OWNER.SGP_VISITANTE resp " +
				"ON resp.ID_VISITANTE = ( " +
				"SELECT DISTINCT vr.VISITANTE_RESPONSAVEL " +
				"FROM sigepen_owner.sgp_entrada_saida es " +
				"LEFT JOIN sigepen_owner.sgp_visitante v ON es.id_visitante = v.id_visitante " +
				"LEFT JOIN sigepen_owner.sgp_visitante_responsavel vr ON vr.visitante = v.id_visitante " +
				"WHERE es.id_visitante = vis.ID_VISITANTE AND " +
				"((vr.visitante_responsavel IS NOT NULL AND vr.sta_visitante_responsavel = 'A') " +
				"AND EXISTS (SELECT 1 " +
				"FROM sigepen_owner.sgp_entrada_saida ss " +
				"WHERE ss.id_visitante = vr.visitante_responsavel " +
				"AND trunc(ss.data_entrada) = trunc(es.data_entrada)))) " +
				"LEFT OUTER JOIN " +
				"SIGEPEN_OWNER.PESSOA pessoa_resp " +
				"ON resp.ID_PESSOA_VISITANTE = pessoa_resp.ID ";

	}

	public List<TipoVisita> listarTipoVisitas(TipoEntrada te) {
		if(te == null) {
			TypedQuery<TipoVisita> query = em.createQuery("select f from TipoVisita f order by f.descricao asc",
					TipoVisita.class);
			return query.getResultList();
		} else {
			TypedQuery<TipoVisita> query = em.createQuery("select f from TipoVisita f where f.tipoEntrada = :te order by f.descricao asc",
					TipoVisita.class);
			query.setParameter("te", te);
			return query.getResultList();
		}
	}

	public EntradaSaida salvar(EntradaSaida entrada) {
		if(entrada.getId() == null)
			this.em.persist(entrada);
		else
			entrada = this.em.merge(entrada);
		return entrada;
	}

	public List<EntradaSaidaDTO> buscarEntradasPorFiltro(FiltroEntradaSaidaRecuperando filtro) {
		StringBuilder jpql = new StringBuilder();
			jpql.append(script())
					.append(filtro.configureFiltros())
					.append(" order by id desc ");

		Query query =  em.createNativeQuery(jpql.toString(), EntradaSaidaDTO.class);
		filtro.configureParametos(query, -1);
		return query.getResultList();
	}
}
