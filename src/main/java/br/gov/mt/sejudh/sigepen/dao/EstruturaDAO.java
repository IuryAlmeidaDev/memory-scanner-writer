package br.gov.mt.sejudh.sigepen.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.SituacaoEstruturaUnidade;
import br.gov.mt.sejudh.sigepen.model.dto.EstruturaDto;
import br.gov.mt.sejudh.sigepen.model.dto.GerarPedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoConsultaDTO;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class EstruturaDAO {

	@Inject
	private EntityManager entityManager;

	public EstruturaDAO() {

	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Estrutura salvar(Estrutura estrutura) {
		if (estrutura.getId() > 0) {
			estrutura = entityManager.merge(estrutura);
		} else {
			entityManager.persist(estrutura);
		}
		return estrutura;
	}

	public void alterar(Estrutura estrutura) {
		estrutura = entityManager.merge(estrutura);
	}

	public void excluir(Estrutura estrutura) {
		entityManager.remove(entityManager.merge(estrutura));
	}

	public Estrutura buscaPorId(Long id) {
		String jpql = "select e from Estrutura e where e.id = :id";
		Query query = entityManager.createQuery(jpql, Estrutura.class);
		query.setParameter("id", id);
		Estrutura estrutura = (Estrutura) query.getSingleResult();
		return estrutura;
	}

	public Estrutura buscaEstraturaRaizUnidade(Long id) {
		String jpql = "select e from Estrutura e, UnidadePrisional u where u.id = :id and u.estrutura = e";
		Query query = entityManager.createQuery(jpql, Estrutura.class);
		query.setParameter("id", id);
		Estrutura estrutura = (Estrutura) query.getSingleResult();
		return estrutura;
	}

	public Estrutura buscaEstruturaCompletaPorId(Long id) {
		String jpql = "select e from Estrutura e join fetch e.estruturas where e.id = :id";
		Query query = entityManager.createQuery(jpql, Estrutura.class);
		query.setParameter("id", id);
		Estrutura estrutura = (Estrutura) query.getSingleResult();
		return estrutura;
	}

	@SuppressWarnings("unchecked")
	public List<Estrutura> buscaEstruturaFilho(Estrutura estrutura) {
		String jpql = "select e from Estrutura e where e.estruturaPai = :id ORDER BY e.id";
		Query query = entityManager.createQuery(jpql, Estrutura.class);
		query.setParameter("id", estrutura);
		return (List<Estrutura>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<GerarPedidoEstruturaDTO> buscaEstruturaFilhoGerarPedido(GerarPedidoEstruturaDTO estrutura,
			String dataHora, Long tipoRefeicao) {
		int numeroSemana = this.obterNumeroSemanaAno(dataHora);
		int semana = this.obterSemana(numeroSemana);
		StringBuilder where = new StringBuilder("WHERE ");
		StringBuilder sql = new StringBuilder("SELECT " + "e.id,e.CAPACIDADE,e.cela,e.nome "
				+ ",SUM(CASE WHEN r.id IS not NULL AND dr.ID IS null THEN 1 ELSE 0 END) "
				+ ",SUM(CASE WHEN r.id IS not NULL AND dr.ID IS not null AND c.id IS NOT null THEN 1 ELSE 0 END) ");
		sql.append("FROM " + Sigepen.SEJUDH_SCHEMA + ".estrutura e ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA + ".passagem_lotacao plo ON plo.ESTRUTURA_ID = e.id ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA + ".passagem  p ON p.ID = plo.PASSAGEM_ID ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA + ".recuperando  r ON r.id = p.RECUPERANDO_ID ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".DIETA_RECUPERANDO dr ON dr.RECUPERANDO_ID = r.id and dr.STATUS = 1 and (dr.FIM IS null or dr.FIM > SYSDATE) ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".CARDAPIO c ON c.TIPO_DIETA_ID = dr.TIPO_DIETA_ID AND c.TIPO_REFEICAO_ID = :tipoRefeicao AND c.SEMANA = :semana AND c.CODG_DIA_SEMANA = (select to_char(TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS'),'D') from dual) - 1 ");
		// jpql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA + ".PEDIDO_TRANSITO PT on
		// PT.RECUPERANDO_ID = P.RECUPERANDO_ID AND PT.DATA_CONCLUSAO IS NULL AND
		// PT.STATUS IN ('SUSPENSO', 'EM_ANDAMENTO') ");

		where.append("(e.ESTRUTURAPAI_ID = :id AND e.CELA = 0) ");
		where.append(
				"OR (e.ESTRUTURAPAI_ID = :id AND e.CELA = 1 AND TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS') >= plo.DATA_ENTRADA AND (TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS') <= plo.DATA_SAIDA OR plo.DATA_SAIDA IS NULL)) ");
		sql.append(where);
		sql.append("GROUP BY e.id,e.CAPACIDADE,e.cela,e.nome,e.SITUACAO,e.ESTRUTURAPAI_ID,e.UNIDADEPRISIONAL_ID ");
		sql.append("ORDER BY e.id");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter("id", estrutura.getId());
		query.setParameter("data", dataHora);
		query.setParameter("tipoRefeicao", tipoRefeicao);
		query.setParameter("semana", semana);
		return (List<GerarPedidoEstruturaDTO>) query.getResultList().stream()
				.map(row -> new GerarPedidoEstruturaDTO((Object[]) row)).collect(Collectors.toList());
	}

	private int obterNumeroSemanaAno(String dataString) {

		String dataFormatada = formataData(dataString);

		LocalDate data = LocalDate.parse(dataFormatada);
		int numeroSemana = data.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

		return numeroSemana;
	}

	private String formataData(String dataString) {

		try {

			SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");
			Date dataConvert = formatoEntrada.parse(dataString);
			SimpleDateFormat formatoSaida = new SimpleDateFormat("yyyy-MM-dd");
			return formatoSaida.format(dataConvert);
		} catch (ParseException e) {
			return "";
		}
	}

	private int obterSemana(int numeroSemana) {
		if (numeroSemana <= 6) {
			return numeroSemana;
		}

		int resultado = numeroSemana;
		int razao = 6;

		while (resultado > 6) {
			resultado = resultado - razao;
		}

		return resultado;
	}

	@SuppressWarnings("unchecked")
	public List<PedidoConsultaDTO> buscaPedidoDetalhado(String dataHora, Long id, Long tipoRefeicao) {
		int numeroSemana = this.obterNumeroSemanaAno(dataHora);
		int semana = this.obterSemana(numeroSemana);
		StringBuilder where = new StringBuilder("WHERE ");
		StringBuilder sql = new StringBuilder("SELECT count(pas.ID) AS QTD ,sum(pas.ID) * ic.MASC_MG  AS PESO "
				+ ",a.DESCRICAO, ic.MASC_MG, ic.FEM_MG,'PADRAO' AS tipo, td.DESCRICAO AS cardapio, tr.DESCRICAO AS TIPO_REFEICAO, p.SEXO ");
		sql.append("FROM " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM PAS ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".RECUPERANDO r ON r.ID = pas.RECUPERANDO_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".PESSOA p ON p.ID = r.PESSOA_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM_LOTACAO L on l.PASSAGEM_ID = pas.ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".ESTRUTURA e2 ON e2.ID = l.ESTRUTURA_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".CONTRATO_UNIDADE_PRISIONAL cup ON cup.UNIDADE_PRISIONAL_ID = e2.UNIDADEPRISIONAL_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c2 ON c2.ID = cup.CONTRATO_ID ");
		sql.append(
				"AND (c2.STATUS = 1 AND c2.VIGENCIA_CONTRATO_INICIO <= TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS') AND c2.VIGENCIA_CONTRATO_FIM >= TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS')) ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".CARDAPIO c ON c.CONTRATO_ID = cup.CONTRATO_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".ITEM_CARDAPIO ic ON ic.CARDAPIO_ID = c.ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".ALIMENTO a on a.id = ic.ALIMENTO_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".TIPO_DIETA td ON td.ID = c.TIPO_DIETA_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".TIPO_REFEICAO tr ON tr.ID = c.TIPO_REFEICAO_ID ");

		where.append(
				"c.TIPO_REFEICAO_ID = :tipoRefeicao AND c.SEMANA = :semana and td.ESPECIAL = 0 and c.CODG_DIA_SEMANA = (select to_char(TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS'),'D') from dual) - 1 and ");
		where.append("l.ESTRUTURA_ID in(SELECT id FROM " + Sigepen.SEJUDH_SCHEMA
				+ ".estrutura e START WITH id = :id CONNECT BY PRIOR id = ESTRUTURAPAI_ID) and ");
		where.append(
				"TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS') >= l.DATA_ENTRADA AND (TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS') <= l.DATA_SAIDA OR L.DATA_SAIDA IS NULL) and ");
		where.append("pas.RECUPERANDO_ID NOT IN (SELECT dr1.recuperando_id FROM " + Sigepen.SEJUDH_SCHEMA
				+ ".DIETA_RECUPERANDO dr1 where dr1.STATUS = 1 and (dr1.FIM IS null or dr1.FIM > SYSDATE)) ");
		sql.append(where);

		sql.append("GROUP BY a.DESCRICAO, ic.MASC_MG, ic.FEM_MG, td.DESCRICAO, tr.DESCRICAO,p.SEXO ");
		sql.append("union ");

		sql.append("SELECT count(pas.ID) AS QTD ,sum(pas.ID) * ic.MASC_MG  AS PESO "
				+ ",a.DESCRICAO, ic.MASC_MG, ic.FEM_MG,'ESPECIAL' AS tipo, td.DESCRICAO AS cardapio, tr.DESCRICAO AS TIPO_REFEICAO, p.SEXO ");
		sql.append("FROM " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM PAS ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".RECUPERANDO r ON r.ID = pas.RECUPERANDO_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".PESSOA p ON p.ID = r.PESSOA_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM_LOTACAO L on l.PASSAGEM_ID = pas.ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".ESTRUTURA e2 ON e2.ID = l.ESTRUTURA_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".CONTRATO_UNIDADE_PRISIONAL cup ON cup.UNIDADE_PRISIONAL_ID = e2.UNIDADEPRISIONAL_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c2 ON c2.ID = cup.CONTRATO_ID ");
		sql.append(
				"AND (c2.STATUS = 1 AND c2.VIGENCIA_CONTRATO_INICIO <= TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS') AND c2.VIGENCIA_CONTRATO_FIM >= TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS')) ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".CARDAPIO c ON c.CONTRATO_ID = cup.CONTRATO_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".ITEM_CARDAPIO ic ON ic.CARDAPIO_ID = c.ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".ALIMENTO a on a.id = ic.ALIMENTO_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".DIETA_RECUPERANDO dr ON dr.RECUPERANDO_ID = PAS.RECUPERANDO_ID AND dr.STATUS = 1 and (dr.FIM IS null or dr.FIM > SYSDATE) ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".TIPO_DIETA td ON td.ID = c.TIPO_DIETA_ID AND td.ID = c.TIPO_DIETA_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".TIPO_REFEICAO tr ON tr.ID = c.TIPO_REFEICAO_ID ");

		where = new StringBuilder("WHERE ");
		where.append(
				"c.TIPO_REFEICAO_ID = :tipoRefeicao AND c.SEMANA = :semana and td.ESPECIAL = 1 and c.CODG_DIA_SEMANA = (select to_char(TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS'),'D') from dual) - 1 and ");
		where.append("l.ESTRUTURA_ID in(SELECT id FROM " + Sigepen.SEJUDH_SCHEMA
				+ ".estrutura e START WITH id = :id CONNECT BY PRIOR id = ESTRUTURAPAI_ID) and ");
		where.append(
				"TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS') >= l.DATA_ENTRADA AND (TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS') <= l.DATA_SAIDA OR L.DATA_SAIDA IS NULL) ");
		sql.append(where);

		sql.append("GROUP BY a.DESCRICAO, ic.MASC_MG, ic.FEM_MG, td.DESCRICAO, tr.DESCRICAO,p.SEXO ");
		sql.append("ORDER BY cardapio ");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter("id", id);
		query.setParameter("data", dataHora);
		query.setParameter("tipoRefeicao", tipoRefeicao);
		query.setParameter("semana", semana);

		return (List<PedidoConsultaDTO>) query.getResultList().stream()
				.map(row -> new PedidoConsultaDTO((Object[]) row)).collect(Collectors.toList());
	}

	public String buscarNomeEstrutura(Long idEstrutura) {

		StringBuilder sql = new StringBuilder("SELECT NOME_COMPLETO FROM " + Sigepen.SEJUDH_SCHEMA
				+ ".NOME_COMPLETO_ESTRUTURA where ID = :idEstrutura");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter("idEstrutura", idEstrutura);

		return (String) query.getSingleResult();
	}

	public void salvarSituacaoEstrutura(SituacaoEstruturaUnidade situacaoUnidadePrisional) {
		try {
			entityManager.persist(situacaoUnidadePrisional);
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<SituacaoEstruturaUnidade> listarPorUnidade(Long unidadePrisional) {
		String jpql = "select s from SituacaoEstruturaUnidade s left join fetch s.unidadePrisional u left join fetch s.estrutura e where u.id = :id AND s.origemAlteracao = 1 ORDER BY s.dataRegistro ASC";
		Query query = entityManager.createQuery(jpql, SituacaoEstruturaUnidade.class);
		query.setParameter("id", unidadePrisional);
		return (List<SituacaoEstruturaUnidade>) query.getResultList();
	}

	public long quantidadeRecuperandosLotados(Estrutura estrutura) {
		Query query = entityManager.createQuery(
				"select count(p) from PassagemLotacao p where p.estrutura = :estrutura and p.dataSaida is null");
		query.setParameter("estrutura", estrutura);
		return (long) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<EstruturaDto> buscarEstruturasUnidade(Long idUnidade) {
		Query query = entityManager.createNativeQuery("select level nivel," + "       e.id,"
				+ "       e.estruturapai_id," + "       e.nome," + "       e.capacidade," + "       e.cela" + " from  "
				+ Sigepen.SEJUDH_SCHEMA + ".estrutura e" + " start with e.estruturapai_id is null"
				+ " and e.unidadeprisional_id = :idUnidade" + " connect by prior e.id = e.estruturapai_id"
				+ " and e.situacao <> 0" + " order siblings by e.id", EstruturaDto.class);
		query.setParameter("idUnidade", idUnidade);
		return query.getResultList();
	}

	public void buscarEstruturasCompleta(Estrutura ed) {
		if (ed != null && ed.getEstruturaPai() != null) {
			String jpqlED = "select e from Estrutura e where e.id = :id";
			TypedQuery<Estrutura> queryD = entityManager.createQuery(jpqlED, Estrutura.class);
			queryD.setParameter("id", ed.getId());
			Estrutura estruturaPai = queryD.getSingleResult().getEstruturaPai();
			ed.setEstruturaPai(estruturaPai);
			buscarEstruturasCompleta(ed.getEstruturaPai());
		}
    }
}
