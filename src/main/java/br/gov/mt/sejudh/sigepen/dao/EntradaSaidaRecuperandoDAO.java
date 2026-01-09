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

	public List<EntradaSaidaDTO> buscarEntradasPorFiltro(FiltroEntradaSaidaRecuperando filtro, String sortField,
			SortOrder sortOrder, int first) {
		StringBuilder jpql = new StringBuilder();
		if (sortOrder.name().equals("DESCENDING")) {
			jpql.append("SELECT * FROM ( ").append("SELECT row_.*, rownum rownum_ FROM (").append(script())
					.append(filtro.configureFiltros()).append(" order by ").append(sortField).append(" desc ) row_ ")
					.append("where rownum <= :rowFinal) where rownum_ > :rowInicial");

		} else {
			jpql.append("SELECT * FROM ( ").append("SELECT row_.*, rownum rownum_ FROM (").append(script())
					.append(filtro.configureFiltros()).append("order by ").append(sortField).append(" asc ) row_ ")
					.append("where rownum <= :rowFinal) where rownum_ > :rowInicial");

		}
		Query query = em.createNativeQuery(jpql.toString(), EntradaSaidaDTO.class);
		filtro.configureParametos(query, first);
		List<EntradaSaidaDTO> sql = query.getResultList();
		sql.forEach(dto -> {
			dto.criarVisitante();
			dto.criarResponsavel();
		});
		return sql;
	}

	public int buscarEntradasPorFiltroCount(FiltroEntradaSaidaRecuperando filtro, String sortField, SortOrder sortOrder,
			int first) {
		StringBuilder jpql = new StringBuilder();
		jpql.append("SELECT count(*) as id FROM ( ").append("SELECT row_.*, rownum rownum_ FROM (").append(script())
				.append(filtro.configureFiltros()).append("  ) row_ ").append(") ");

		Query query = em.createNativeQuery(jpql.toString());
		filtro.configureParametos(query, -1);

		BigDecimal result = (BigDecimal) query.getSingleResult();

		return result.intValue();

	}

	private String script() {
		return "SELECT " + "    esv.ID AS id, " + "    esv.DATA_ENTRADA AS dataEntrada, "
				+ "    esv.DATA_SAIDA AS dataSaida, " + "    esv.DATA_CANCELAMENTO AS dataCancelamento, "
				+ "    esv.SITUACAO AS situacao, " + "    esv.MOTIVO_FINALIZACAO AS motivoFinalizacao, "
				+ "    esv.MOTIVO_ENTRADA AS motivoEntrada, " + "    esv.MOTIVO_CANCELAMENTO AS motivoCancelamento, "
				+ "    tipo_visita.DESC_TIPO_VISITA AS tipoVisita, " + "    parentesco.DESCRICAO AS parentesco, "
				+ "    cred.PROFISSAO AS profissao, " + "    pessoa_rec.NOME AS nomeRecuperando, "
				+ "    up.ID AS idUnidade, " + "    up.NOME AS nomeUnidade, " + "    up.SIGLA AS siglaUnidade, "
				+ "    vis.ID_VISITANTE AS visitanteId, " + "    vis.FLAG_PNE AS pneVisitante, "
				+ "    vis.FLAG_PNE_PERMANENTE AS pnePermanenteVisitante, "
				+ "    vis.FLAG_EMANCIPADO AS emancipadoVisitante, " + "    pessoa_v.NOME AS nomeVisitante, "
				+ "    pessoa_v.SEXO AS sexoVisitante, " + "    pessoa_v.NOME_MAE AS nomeMaeVisitante, "
				+ "    pessoa_v.NOME_PAI AS nomePaiVisitante, " + "    pessoa_v.NOME_SOCIAL AS nomeSocialVisitante, "
				+ "    pessoa_v.DATA_NASCIMENTO AS dataNascimentoVisitante, "
				+ "    nacionalidade.DESCRICAO AS nacionalidadeVisitante, " + "    estado.NOME AS estadoVisitante, "
				+ "    cidade.NOME AS cidadeVisitante, "
				+ "    identidade_genero.DESCRICAO AS identidadeGeneroVisitante, "
				+ "    resp.ID_VISITANTE AS idResponsavel, " + "    resp.FLAG_PNE AS pneResponsavel, "
				+ "    resp.FLAG_PNE_PERMANENTE AS pnePermanenteResponsavel, "
				+ "    resp.FLAG_EMANCIPADO AS emancipadoResponsavel, " + "    pessoa_resp.NOME AS nomeResponsavel, "
				+ "    pessoa_resp.SEXO AS sexoResponsavel, " + "    pessoa_resp.NOME_MAE AS nomeMaeResponsavel, "
				+ "    pessoa_resp.NOME_PAI AS nomePaiResponsavel, "
				+ "    pessoa_resp.NOME_SOCIAL AS nomeSocialResponsavel, "
				+ "    pessoa_resp.DATA_NASCIMENTO AS dataNascimentoResponsavel, "
				+ "    nacionalidade_resp.DESCRICAO AS nacionalidadeResponsavel, "
				+ "    estado_resp.NOME AS estadoResponsavel, " + "    cidade_resp.NOME AS cidadeResponsavel, "
				+ "    identidade_genero_resp.DESCRICAO AS identidadeGeneroResponsavel " + "FROM "
				+ "    SIGEPEN_OWNER.SGP_ENTRADA_SAIDA_RECUP esr "
				+ "    INNER JOIN SIGEPEN_OWNER.SGP_CREDENCIAMENTO cred ON esr.ID_CREDENCIAMENTO = cred.ID_CREDENCIAMENTO "
				+ "    LEFT OUTER JOIN SIGEPEN_OWNER.PARENTE parente ON cred.ID_PARENTE = parente.ID "
				+ "    LEFT OUTER JOIN SIGEPEN_OWNER.PARENTESCO parentesco ON parente.PARENTESCO_ID = parentesco.ID "
				+ "    LEFT OUTER JOIN SIGEPEN_OWNER.RECUPERANDO rec ON cred.ID_RECUPERANDO = rec.ID "
				+ "    LEFT OUTER JOIN SIGEPEN_OWNER.PESSOA pessoa_rec ON rec.PESSOA_ID = pessoa_rec.ID "
				+ "    INNER JOIN SIGEPEN_OWNER.SGP_TIPO_VISITA tipo_visita ON cred.ID_TIPO_VISITA = tipo_visita.ID_TIPO_VISITA "
				+ "    INNER JOIN SIGEPEN_OWNER.SGP_ENTRADA_SAIDA esv ON esr.ID_ENTRADA_SAIDA = esv.ID "
				+ "    INNER JOIN SIGEPEN_OWNER.UNIDADE_PRISIONAL up ON esv.ID_UNIDADE_PRISIONAL = up.ID "
				+ "    INNER JOIN SIGEPEN_OWNER.SGP_VISITANTE vis ON esv.ID_VISITANTE = vis.ID_VISITANTE "
				+ "    LEFT OUTER JOIN SIGEPEN_OWNER.IDENTIDADE_GENERO identidade_genero ON vis.ID_IDENTIDADE_GENERO = identidade_genero.ID "
				+ "    INNER JOIN SIGEPEN_OWNER.PESSOA pessoa_v ON vis.ID_PESSOA_VISITANTE = pessoa_v.ID "
				+ "    LEFT OUTER JOIN SIGEPEN_OWNER.NACIONALIDADE nacionalidade ON pessoa_v.NACIONALIDADE_ID = nacionalidade.ID "
				+ "    LEFT OUTER JOIN SIGEPEN_OWNER.MUNICIPIO cidade ON pessoa_v.CIDADE_ID = cidade.ID "
				+ "    LEFT OUTER JOIN SIGEPEN_OWNER.ESTADO estado ON cidade.ESTADO = estado.UF "
				+ "    LEFT JOIN SIGEPEN_OWNER.SGP_VISITANTE resp ON resp.Id_Visitante = esv.Id_Visitante_Responsavel "
				+ "	 LEFT OUTER JOIN SIGEPEN_OWNER.IDENTIDADE_GENERO identidade_genero_resp ON resp.ID_IDENTIDADE_GENERO = identidade_genero.ID "
				+ "    LEFT OUTER JOIN SIGEPEN_OWNER.PESSOA pessoa_resp ON pessoa_resp.ID = resp.ID_PESSOA_VISITANTE "
				+ "    LEFT JOIN SIGEPEN_OWNER.NACIONALIDADE nacionalidade_resp ON pessoa_resp.NACIONALIDADE_ID = nacionalidade_resp.ID "
				+ "    LEFT JOIN SIGEPEN_OWNER.MUNICIPIO cidade_resp ON pessoa_resp.CIDADE_ID = cidade_resp.ID "
				+ "    LEFT JOIN SIGEPEN_OWNER.ESTADO estado_resp ON cidade_resp.ESTADO = estado_resp.UF ";

	}

	public List<TipoVisita> listarTipoVisitas(TipoEntrada te) {
		if (te == null) {
			TypedQuery<TipoVisita> query = em.createQuery("select f from TipoVisita f order by f.descricao asc",
					TipoVisita.class);
			return query.getResultList();
		} else {
			TypedQuery<TipoVisita> query = em.createQuery(
					"select f from TipoVisita f where f.tipoEntrada = :te order by f.descricao asc", TipoVisita.class);
			query.setParameter("te", te);
			return query.getResultList();
		}
	}

	public EntradaSaida salvar(EntradaSaida entrada) {
		if (entrada.getId() == null)
			this.em.persist(entrada);
		else
			entrada = this.em.merge(entrada);
		return entrada;
	}

	public List<EntradaSaidaDTO> buscarEntradasPorFiltro(FiltroEntradaSaidaRecuperando filtro, String sortField,
			SortOrder sortOrder) {

		String colunaOrdenacao = mapSortField(sortField);

		String direcao = SortOrder.DESCENDING.equals(sortOrder) ? "DESC" : "ASC";

		String orderBy = colunaOrdenacao + " " + direcao;

		StringBuilder sql = new StringBuilder();
		sql.append(script()).append(filtro.configureFiltros()).append(" ORDER BY ").append(orderBy);

		Query query = em.createNativeQuery(sql.toString(), EntradaSaidaDTO.class);
		filtro.configureParametos(query, -1);

		List<EntradaSaidaDTO> lista = query.getResultList();
		lista.forEach(dto -> {
			dto.criarVisitante();
			dto.criarResponsavel();
		});

		return lista;
	}

	private String mapSortField(String sortField) {

		if (sortField == null) {
			return "ID";
		}

		switch (sortField) {
		case "tipoVisita":
			return "TIPOVISITA";

		case "nomeVisitante":
			return "NOMEVISITANTE";

		case "nomeResponsavel":
			return "NOMERESPONSAVEL";

		case "nomeRecuperando":
			return "NOMERECUPERANDO";

		case "dataEntrada":
			return "DATA_ENTRADA";

		case "dataSaida":
			return "DATA_SAIDA";

		default:
			return "ID";
		}
	}

}
