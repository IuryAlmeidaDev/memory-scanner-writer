package br.gov.mt.sejudh.sigepen.dao;

import java.util.*;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.SituacaoEstruturaUnidade;
import br.gov.mt.sejudh.sigepen.model.dto.EstruturaDto;
import br.gov.mt.sejudh.sigepen.model.dto.GerarPedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RefeicaoDetalhadaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialEstruturaDTO;
import br.gov.mt.sejudh.sigepen.util.CardapioUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.Hibernate;

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
		buscarEstruturasCompleta(estrutura);
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
		int numeroSemana = CardapioUtil.obterNumeroSemanaAno(dataHora);
		int semana = CardapioUtil.obterSemana(numeroSemana);
		StringBuilder where = new StringBuilder("WHERE ");
		StringBuilder sql = new StringBuilder("SELECT " + "e.id,e.CAPACIDADE,e.cela,e.nome, e.SITUACAO "
				+ ",SUM(CASE WHEN r.id IS not NULL AND dr.ID IS null THEN 1 ELSE 0 END) "
				+ ",SUM(CASE WHEN r.id IS not NULL AND dr.ID IS not null THEN 1 ELSE 0 END) ");
		sql.append("FROM " + Sigepen.SEJUDH_SCHEMA + ".estrutura e ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA + ".passagem_lotacao plo ON plo.ESTRUTURA_ID = e.id ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA + ".passagem  p ON p.ID = plo.PASSAGEM_ID ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA + ".recuperando  r ON r.id = p.RECUPERANDO_ID ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".DIETA_RECUPERANDO dr ON dr.RECUPERANDO_ID = r.id and dr.STATUS = 1 and (dr.INICIO < TO_DATE(SUBSTR(:data, 1, 19), 'DD/MM/YYYY HH24:MI:SS')) and (dr.FIM IS null or dr.FIM > TO_DATE(SUBSTR(:data, 1, 19), 'DD/MM/YYYY HH24:MI:SS')) ");
		// sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA
		// 		+ ".CARDAPIO c ON c.TIPO_DIETA_ID = dr.TIPO_DIETA_ID AND c.TIPO_REFEICAO_ID = :tipoRefeicao AND c.SEMANA = :semana AND c.CODG_DIA_SEMANA = (select to_char(TO_DATE(SUBSTR(:data, 1, 19), 'DD/MM/YYYY HH24:MI:SS'),'D') from dual) - 1 ");
		// jpql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA + ".PEDIDO_TRANSITO PT on
		// PT.RECUPERANDO_ID = P.RECUPERANDO_ID AND PT.DATA_CONCLUSAO IS NULL AND
		// PT.STATUS IN ('SUSPENSO', 'EM_ANDAMENTO') ");

		where.append("(e.ESTRUTURAPAI_ID = :id AND e.CELA = 0) ");
		where.append(
				"OR (e.ESTRUTURAPAI_ID = :id AND e.CELA = 1 AND TO_DATE(SUBSTR(:data, 1, 19), 'DD/MM/YYYY HH24:MI:SS') >= plo.DATA_ENTRADA AND (TO_DATE(SUBSTR(:data, 1, 19), 'DD/MM/YYYY HH24:MI:SS') <= plo.DATA_SAIDA OR plo.DATA_SAIDA IS NULL)) ");
		sql.append(where);
		sql.append("GROUP BY e.id,e.CAPACIDADE,e.cela,e.nome,e.SITUACAO,e.ESTRUTURAPAI_ID,e.UNIDADEPRISIONAL_ID ");
		sql.append("ORDER BY e.id");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter("id", estrutura.getId());
		query.setParameter("data", dataHora);
		// query.setParameter("tipoRefeicao", tipoRefeicao);
		// query.setParameter("semana", semana);
		return (List<GerarPedidoEstruturaDTO>) query.getResultList().stream()
				.map(row -> new GerarPedidoEstruturaDTO((Object[]) row)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public List<RefeicaoDetalhadaDTO> buscaPedidoDetalhado(final String dataHora, final Long id, final Long tipoRefeicao) {
	    final int numeroSemana = CardapioUtil.obterNumeroSemanaAno(dataHora);
	    final int semana = CardapioUtil.obterSemana(numeroSemana);
	    final StringBuilder sql = new StringBuilder();

	    sql.append("SELECT count(pas.ID) AS QTD, a.DESCRICAO, ic.MASC_MG, ic.FEM_MG, td.DESCRICAO AS cardapio, p.SEXO, a.UNIDADE_MEDIDA, ic.ID AS item_cardapio_id ")
	       .append("FROM ").append(Sigepen.SEJUDH_SCHEMA).append(".PASSAGEM pas ")
	       .append("JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".RECUPERANDO r ON r.ID = pas.RECUPERANDO_ID ")
	       .append("JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".PESSOA p ON p.ID = r.PESSOA_ID ")
	       .append("JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".PASSAGEM_LOTACAO l ON l.PASSAGEM_ID = pas.ID ")
	       .append("JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".ESTRUTURA e2 ON e2.ID = l.ESTRUTURA_ID ")
	       .append("JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".CONTRATO_UNIDADE_PRISIONAL cup ON cup.UNIDADE_PRISIONAL_ID = e2.UNIDADEPRISIONAL_ID ")
	       .append("JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".CARDAPIO c ON c.CONTRATO_ID = cup.CONTRATO_ID ")
	       .append("JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".ITEM_CARDAPIO ic ON ic.CARDAPIO_ID = c.ID ")
	       .append("JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".ALIMENTO a ON a.ID = ic.ALIMENTO_ID ")
	       .append("JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".TIPO_DIETA td ON td.ID = c.TIPO_DIETA_ID ")
	       .append("WHERE c.TIPO_REFEICAO_ID = :tipoRefeicao AND c.SEMANA = :semana AND td.ESPECIAL = 0 ")
	       .append("AND c.CODG_DIA_SEMANA = (SELECT TO_CHAR(TO_DATE(SUBSTR(:data, 1, 19), 'DD/MM/YYYY HH24:MI:SS'), 'D') FROM dual) - 1 ")
	       .append("AND l.ESTRUTURA_ID IN (SELECT id FROM ").append(Sigepen.SEJUDH_SCHEMA).append(".ESTRUTURA START WITH id = :id CONNECT BY PRIOR id = ESTRUTURAPAI_ID) ")
	       .append("AND TO_DATE(SUBSTR(:data, 1, 19), 'DD/MM/YYYY HH24:MI:SS') BETWEEN l.DATA_ENTRADA AND NVL(l.DATA_SAIDA, TO_DATE('31/12/9999','DD/MM/YYYY')) ")
	       .append("AND NOT EXISTS (SELECT 1 FROM ").append(Sigepen.SEJUDH_SCHEMA).append(".DIETA_RECUPERANDO dr1 WHERE dr1.recuperando_id = pas.RECUPERANDO_ID AND dr1.STATUS = 1 AND (dr1.FIM IS NULL OR dr1.FIM > SYSDATE)) ")
	       .append("GROUP BY a.DESCRICAO, ic.MASC_MG, ic.FEM_MG, td.DESCRICAO, p.SEXO, a.UNIDADE_MEDIDA, ic.ID ");

	    final Query query = entityManager.createNativeQuery(sql.toString());
	    query.setParameter("id", id);
	    query.setParameter("data", dataHora);
	    query.setParameter("tipoRefeicao", tipoRefeicao);
	    query.setParameter("semana", semana);

	    return (List<RefeicaoDetalhadaDTO>) query.getResultList().stream()
	            .map(row -> new RefeicaoDetalhadaDTO((Object[]) row))
	            .collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public List<DietaEspecialEstruturaDTO> buscarDietasEspeciaisPorEstrutura(Long idEstrutura, String dataHora) {
	    StringBuilder sql = new StringBuilder();
	    sql.append("WITH hierarquia_estruturas AS ( ")
	       .append("    SELECT id, nome, estruturapai_id, LEVEL as nivel ")
	       .append("    FROM ").append(Sigepen.SEJUDH_SCHEMA).append(".ESTRUTURA ")
	       .append("    START WITH id = :idEstrutura ")
	       .append("    CONNECT BY PRIOR id = estruturapai_id ")
	       .append("), ")

	       .append("recuperandos_dieta AS ( ")
	       .append("    SELECT ")
	       .append("        l.ESTRUTURA_ID, ")
	       .append("        td.DESCRICAO as tipo_dieta, ")
	       .append("        p.NOME as nome_pessoa, ")
	       .append("        pas.RECUPERANDO_ID ")
	       .append("    FROM ").append(Sigepen.SEJUDH_SCHEMA).append(".PASSAGEM pas ")
	       .append("    JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".PASSAGEM_LOTACAO l ON l.PASSAGEM_ID = pas.ID ")
	       .append("    JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".DIETA_RECUPERANDO dr ON dr.RECUPERANDO_ID = pas.RECUPERANDO_ID ")
	       .append("    JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".TIPO_DIETA td ON td.ID = dr.TIPO_DIETA_ID ")
	       .append("    JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".RECUPERANDO r ON r.ID = pas.RECUPERANDO_ID ")
	       .append("    JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".PESSOA p ON p.ID = r.PESSOA_ID ")
	       .append("    WHERE dr.STATUS = 1 ")
	       .append("      AND td.ESPECIAL = 1 ")
	       .append("      AND TO_DATE(SUBSTR(:DATA, 1, 19), 'DD/MM/YYYY HH24:MI:SS') BETWEEN l.DATA_ENTRADA AND NVL(l.DATA_SAIDA, TO_DATE('31/12/9999','DD/MM/YYYY')) ")
	       .append("      AND TO_DATE(SUBSTR(:DATA, 1, 19), 'DD/MM/YYYY HH24:MI:SS') BETWEEN dr.INICIO AND NVL(dr.FIM, TO_DATE('31/12/9999','DD/MM/YYYY')) ")
	       .append(") ")

	       .append("SELECT ")
	       .append("    h_pai.nivel, ")
	       .append("    h_pai.id as estrutura_id, ")
	       .append("    h_pai.nome as estrutura_nome, ")
	       .append("    rd.tipo_dieta, ")
	       .append("    COUNT(DISTINCT rd.RECUPERANDO_ID) as qtd_recuperando, ")
	       .append("    LISTAGG(rd.nome_pessoa, ',') WITHIN GROUP (ORDER BY rd.nome_pessoa) as nomes_recuperandos ")
	       .append("FROM hierarquia_estruturas h_pai ")
	       
	       .append("JOIN hierarquia_estruturas h_filhas ON h_filhas.id = h_pai.id OR h_filhas.id IN (")
	       .append("    SELECT id FROM hierarquia_estruturas START WITH estruturapai_id = h_pai.id CONNECT BY PRIOR id = estruturapai_id")
	       .append(") ")
	       .append("JOIN recuperandos_dieta rd ON rd.ESTRUTURA_ID = h_filhas.id ")
	       .append("WHERE h_pai.nivel = 2 ") 
	       .append("GROUP BY h_pai.nivel, h_pai.id, h_pai.nome, rd.tipo_dieta ")
	       .append("ORDER BY h_pai.id, rd.tipo_dieta ");

	    final Query query = entityManager.createNativeQuery(sql.toString());
	    query.setParameter("idEstrutura", idEstrutura);
	    query.setParameter("DATA", dataHora);

	    return (List<DietaEspecialEstruturaDTO>) query.getResultList().stream()
	            .map(row -> {
	                Object[] columns = (Object[]) row;
	                DietaEspecialEstruturaDTO dto = new DietaEspecialEstruturaDTO(columns);
	                
	                String nomesString = (String) columns[5];
	                if (nomesString != null && !nomesString.isEmpty()) {
	                    dto.setNomesRecuperandos(Arrays.asList(nomesString.split(",")));
	                }
	                return dto;
	            })
	            .collect(Collectors.toList());
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
		String jpql = "select s from SituacaoEstruturaUnidade s left join fetch s.unidadePrisional u left join fetch s.estrutura e where u.id = :id AND s.origemAlteracao = true ORDER BY s.dataRegistro ASC";
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
	public List<Estrutura> buscarEstruturas(Long idUnidade) {
	    Query query = entityManager.createNativeQuery(
	        "SELECT e.ID, " +
					" sigepen_owner.buscaestruturanomecompleto(e.ID)" +
					"FROM sigepen_owner.estrutura e " +
					"START WITH e.ESTRUTURAPAI_ID IS NULL " +
					"       AND e.unidadeprisional_id = :idUnidade " +
					"CONNECT BY PRIOR e.id = e.estruturapai_id " +
					"ORDER SIBLINGS BY e.id");
		query.setParameter("idUnidade", idUnidade);

		List<Object[]> resultados = query.getResultList();
		List<Estrutura> estruturas = new ArrayList<>();

		for (Object[] row : resultados) {
			Estrutura estrutura = new Estrutura();
			estrutura.setId(((Number) row[0]).longValue());
			estrutura.setNome((String) row[1]);
			estruturas.add(estrutura);
		}

		return estruturas;
	}

	public void buscarEstruturasCompleta(Estrutura ed) {
		if(ed != null && ed.getEstruturaComplemento() != null){
			Hibernate.initialize(ed.getEstruturaComplemento());
		}
	}

	@SuppressWarnings("unchecked")
	public List<PedidoEstruturaDTO> montarEstruturaPedido(final String data, final long idEstrutura) {
	    final StringBuilder sql = new StringBuilder();

	    sql.append("WITH dados_lotacao AS ( ")
	       .append("    SELECT l.ESTRUTURA_ID, ")
	       .append("           SUM(CASE WHEN dr.ID IS NULL THEN 1 ELSE 0 END) AS QTD_PADRAO, ")
	       .append("           SUM(CASE WHEN dr.ID IS NOT NULL THEN 1 ELSE 0 END) AS QTD_ESPECIAL ")
	       .append("    FROM ").append(Sigepen.SEJUDH_SCHEMA).append(".PASSAGEM_LOTACAO l ")
	       .append("    JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".PASSAGEM pas ON l.PASSAGEM_ID = pas.ID ")
	       .append("    LEFT JOIN ").append(Sigepen.SEJUDH_SCHEMA).append(".DIETA_RECUPERANDO dr ON dr.RECUPERANDO_ID = pas.RECUPERANDO_ID ")
	       .append("         AND dr.STATUS = 1 ")
	       .append("         AND TO_DATE(SUBSTR(:data, 1, 19), 'DD/MM/YYYY HH24:MI:SS') BETWEEN dr.INICIO AND NVL(dr.FIM, TO_DATE('31/12/9999','DD/MM/YYYY')) ")
	       .append("    WHERE TO_DATE(SUBSTR(:data, 1, 16), 'DD/MM/YYYY HH24:MI') BETWEEN l.DATA_ENTRADA AND NVL(l.DATA_SAIDA, TO_DATE('31/12/9999','DD/MM/YYYY')) ")
	       .append("    GROUP BY l.ESTRUTURA_ID ")
	       .append("), ")
	       .append("hierarquia_completa AS ( ")
	       .append("    SELECT id, nome, estruturapai_id, LEVEL as nivel_h, ")
	       .append("           SYS_CONNECT_BY_PATH(LPAD(id, 10, '0'), '/') as ordenacao_path ") 
	       .append("    FROM ").append(Sigepen.SEJUDH_SCHEMA).append(".ESTRUTURA ")
	       .append("    START WITH id = :idEstrutura ")
	       .append("    CONNECT BY PRIOR id = estruturapai_id ")
	       .append(") ")
	       .append("SELECT h.nivel_h, h.id, h.nome, ")
	       .append("      (SELECT SUM(nvl(d.QTD_PADRAO, 0)) FROM ").append(Sigepen.SEJUDH_SCHEMA).append(".ESTRUTURA e_sub ")
	       .append("       LEFT JOIN dados_lotacao d ON d.ESTRUTURA_ID = e_sub.id ")
	       .append("       START WITH e_sub.id = h.id CONNECT BY PRIOR e_sub.id = e_sub.estruturapai_id) as QTD_PADRAO, ")
	       .append("      (SELECT SUM(nvl(d.QTD_ESPECIAL, 0)) FROM ").append(Sigepen.SEJUDH_SCHEMA).append(".ESTRUTURA e_sub ")
	       .append("       LEFT JOIN dados_lotacao d ON d.ESTRUTURA_ID = e_sub.id ")
	       .append("       START WITH e_sub.id = h.id CONNECT BY PRIOR e_sub.id = e_sub.estruturapai_id) as QTD_ESPECIAL ")
	       .append("FROM hierarquia_completa h ")
	       .append("ORDER BY h.ordenacao_path "); 

	    final Query query = entityManager.createNativeQuery(sql.toString());
	    query.setParameter("data", data);
	    query.setParameter("idEstrutura", idEstrutura);

	    return (List<PedidoEstruturaDTO>) query.getResultList().stream()
	            .map(row -> new PedidoEstruturaDTO((Object[]) row))
	            .collect(Collectors.toList());
	}
	
	/**
	 * Busca a configuração de horário de uma refeição específica para uma unidade prisional.
	 * @param unidadeId ID da Unidade Prisional
	 * @param tipoRefeicaoId ID do Tipo de Refeição (Café, Almoço, etc)
	 * @return Objeto HorarioRefeicao com os limites de horário
	 */
	public HorarioRefeicao buscarConfiguracaoHorarioRefeicao(Long unidadeId, Long tipoRefeicaoId) {
	        StringBuilder jpql = new StringBuilder();
	        jpql.append("SELECT h FROM HorarioRefeicao h ")
	            .append("WHERE h.unidadePrisional.id = :unidadeId ")
	            .append("AND h.tipoRefeicao.id = :tipoRefeicaoId");

	        Query query = entityManager.createQuery(jpql.toString(), HorarioRefeicao.class);
	        query.setParameter("unidadeId", unidadeId);
	        query.setParameter("tipoRefeicaoId", tipoRefeicaoId);

	        List<HorarioRefeicao> resultados = query.getResultList();
	        return resultados.isEmpty() ? null : resultados.get(0);

	}
}
