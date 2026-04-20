package br.gov.mt.sejudh.sigepen.dao;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.mt.sejudh.sigepen.enumerator.Mes;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioMapaAlimentacaoConsultaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioMapaAlimentacaoRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class MapaConsolidadoDAO {

	@Inject
	private EntityManager entityManager;

	public MapaConsolidadoDAO() {

	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	private String convertLocalDateToString(LocalDate localDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return localDate.format(formatter);
	}

	private String obterDataInicio(int mes, int ano) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return YearMonth.of(ano, mes).atDay(1).format(formatter);
	}

	private String obterDataFim(int mes, int ano) {
		YearMonth yearMonth = YearMonth.of(ano, mes);
		int ultimoDiaDoMes = yearMonth.lengthOfMonth();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return yearMonth.atDay(ultimoDiaDoMes).format(formatter);

	}

	@SuppressWarnings("unchecked")
	public List<RelatorioMapaAlimentacaoRecuperandoDTO> buscaRelatorioMapaAlimentacaoRecuperando(Long idEstrutura,
			Mes mes, String ano) {
		String dataInicio = obterDataInicio(mes.getId().intValue(), Integer.parseInt(ano));
		String dataFim = obterDataFim(mes.getId().intValue(), Integer.parseInt(ano));

		StringBuilder sql = new StringBuilder(
				"SELECT p2.NOME, r.ID AS matricula, l.DATA_ENTRADA , l.DATA_SAIDA,SUM(CASE WHEN dr.ID IS null THEN 1 ELSE 0 END) AS QTD_padrao,SUM(CASE WHEN dr.ID IS not null THEN 1 ELSE 0 END) AS QTD_especial,dr.INICIO, dr.FIM, p2.NOME_SOCIAL ");
		sql.append("FROM " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM PAS ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".RECUPERANDO r ON r.id = pas.RECUPERANDO_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".PESSOA p2 ON p2.ID = r.PESSOA_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM_LOTACAO L on l.PASSAGEM_ID = pas.ID ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".DIETA_RECUPERANDO dr ON dr.RECUPERANDO_ID = PAS.RECUPERANDO_ID AND (dr.INICIO >= TO_DATE(:dataInicio, 'DD/MM/YYYY') AND (dr.FIM <= TO_DATE(:dataFim, 'DD/MM/YYYY') OR dr.fim IS null)) ");
		sql.append("WHERE l.ESTRUTURA_ID in(SELECT id FROM " + Sigepen.SEJUDH_SCHEMA
				+ ".estrutura e START WITH id = :id CONNECT BY PRIOR id = ESTRUTURAPAI_ID) ");
		sql.append("AND l.DATA_ENTRADA <= TO_DATE(:dataFim, 'DD/MM/YYYY') ");
		sql.append("AND (l.DATA_SAIDA >= TO_DATE(:dataInicio, 'DD/MM/YYYY')	OR l.DATA_SAIDA IS null) ");
		sql.append("GROUP BY p2.NOME, p2.NOME_SOCIAL, r.ID, l.DATA_ENTRADA , l.DATA_SAIDA,dr.INICIO, dr.FIM ");
		sql.append("ORDER BY matricula, l.DATA_ENTRADA, QTD_especial desc ");

		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter("id", idEstrutura);
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("dataFim", dataFim);
		return (List<RelatorioMapaAlimentacaoRecuperandoDTO>) query.getResultList().stream()
				.map(row -> new RelatorioMapaAlimentacaoRecuperandoDTO((Object[]) row)).collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	public List<RelatorioMapaAlimentacaoRecuperandoDTO> buscaRelatorioMapaAlimentacaoRecuperando(Long idEstrutura,
			LocalDate dataInicio, LocalDate dataFim) {

		StringBuilder sql = new StringBuilder(
				"SELECT p2.NOME, r.ID AS matricula, l.DATA_ENTRADA , l.DATA_SAIDA,SUM(CASE WHEN dr.ID IS null THEN 1 ELSE 0 END) AS QTD_padrao,SUM(CASE WHEN dr.ID IS not null THEN 1 ELSE 0 END) AS QTD_especial,dr.INICIO, dr.FIM, p2.NOME_SOCIAL ");
		sql.append("FROM " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM PAS ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".RECUPERANDO r ON r.id = pas.RECUPERANDO_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".PESSOA p2 ON p2.ID = r.PESSOA_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM_LOTACAO L on l.PASSAGEM_ID = pas.ID ");
		sql.append("LEFT JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".DIETA_RECUPERANDO dr ON dr.RECUPERANDO_ID = PAS.RECUPERANDO_ID AND (dr.INICIO >= TO_DATE(:dataInicio, 'DD/MM/YYYY') AND (dr.FIM <= TO_DATE(:dataFim, 'DD/MM/YYYY') OR dr.fim IS null)) ");
		sql.append("WHERE l.ESTRUTURA_ID in(SELECT id FROM " + Sigepen.SEJUDH_SCHEMA
				+ ".estrutura e START WITH id = :id CONNECT BY PRIOR id = ESTRUTURAPAI_ID) ");
		sql.append("AND l.DATA_ENTRADA <= TO_DATE(:dataFim, 'DD/MM/YYYY') ");
		sql.append("AND (l.DATA_SAIDA >= TO_DATE(:dataInicio, 'DD/MM/YYYY')	OR l.DATA_SAIDA IS null) ");
		sql.append("GROUP BY p2.NOME, p2.NOME_SOCIAL, r.ID, l.DATA_ENTRADA , l.DATA_SAIDA,dr.INICIO, dr.FIM ");
		sql.append("ORDER BY matricula, l.DATA_ENTRADA, QTD_especial desc ");

		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter("id", idEstrutura);
		query.setParameter("dataInicio", this.convertLocalDateToString(dataInicio));
		query.setParameter("dataFim", this.convertLocalDateToString(dataFim));
		return (List<RelatorioMapaAlimentacaoRecuperandoDTO>) query.getResultList().stream()
				.map(row -> new RelatorioMapaAlimentacaoRecuperandoDTO((Object[]) row)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public List<RelatorioMapaAlimentacaoConsultaDTO> buscarRelatorioMapaAlimentacaoConsulta(Long idUnidade, Long idContrato) {
		StringBuilder sql = new StringBuilder(
				"SELECT c.VIGENCIA_CONTRATO_INICIO AS inicio, c.VIGENCIA_CONTRATO_FIM AS fim,c.NUM_CONTRATO, f.NOME_FANTAZIA, t.DESCRICAO,r.VALOR AS normal, r.VALOR_ESPECIAL AS especial ");
		sql.append("FROM " + Sigepen.SEJUDH_SCHEMA + ".CONTRATO c ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA
				+ ".CONTRATO_UNIDADE_PRISIONAL cup ON c.id = cup.CONTRATO_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".FORNECEDOR f ON f.ID = c.FORNECEDOR_ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".APOSTILAMENTO a ON a.CONTRATO_ID = c.id ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".REFEICAO r ON r.APOSTILAMENTO_ID = a.ID ");
		sql.append("JOIN " + Sigepen.SEJUDH_SCHEMA + ".TIPO_REFEICAO t ON t.ID = r.TIPO_REFEICAO_ID ");
		sql.append("WHERE cup.UNIDADE_PRISIONAL_ID = :id and c.id = :idContrato ");
		sql.append("ORDER BY c.NUM_CONTRATO, r.TIPO_REFEICAO_ID ");

		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter("id", idUnidade);
		query.setParameter("idContrato", idContrato);
		return (List<RelatorioMapaAlimentacaoConsultaDTO>) query.getResultList().stream()
				.map(row -> new RelatorioMapaAlimentacaoConsultaDTO((Object[]) row)).collect(Collectors.toList());
	}
	
	
	public Map<String, Object> buscarTotaisPedidosAuditados(Long idUnidade, String dataInicio, String dataFim) {
	    Map<String, Object> totaisRelatorio = new HashMap<>();
	    totaisRelatorio.put("AUDIT_CAFE", 0);
	    totaisRelatorio.put("AUDIT_ALMOCO", 0);
	    totaisRelatorio.put("AUDIT_JANTA", 0);
	    totaisRelatorio.put("AUDIT_CAFE_ESPECIAL", 0);
	    totaisRelatorio.put("AUDIT_ALMOCO_ESPECIAL", 0);
	    totaisRelatorio.put("AUDIT_JANTA_ESPECIAL", 0);

	    String schema = Sigepen.SEJUDH_SCHEMA + ".";
	    StringBuilder sql = new StringBuilder();
	    
	    sql.append("SELECT tr.DESCRICAO, ");
	    sql.append("  SUM(sub.QTD_TOTAL_COMUM) AS TOTAL_COMUM, "); 
	    sql.append("  SUM(NVL(esp.QTD_ESPECIAL, 0)) AS TOTAL_ESPECIAL ");
	    sql.append("FROM " + schema + "SIGEPEN_PEDIDO_ALIMENTO p ");
	    sql.append("JOIN " + schema + "TIPO_REFEICAO tr ON tr.ID = p.ID_TIPO_REFEICAO ");
	    
	    sql.append("JOIN ( ");
	    sql.append("  SELECT ID_PEDIDO_ALIMENTO, SUM(QTD_ESTRUTURA) AS QTD_TOTAL_COMUM ");
	    sql.append("  FROM ( ");
	    sql.append("    SELECT ID_PEDIDO_ALIMENTO, ID_ESTRUTURA, MAX(QTD_RECUPERANDO) AS QTD_ESTRUTURA ");
	    sql.append("    FROM " + schema + "SGP_ITEM_PEDIDO_ALIMENTO ");
	    sql.append("    GROUP BY ID_PEDIDO_ALIMENTO, ID_ESTRUTURA ");
	    sql.append("  ) ");
	    sql.append("  GROUP BY ID_PEDIDO_ALIMENTO ");
	    sql.append(") sub ON sub.ID_PEDIDO_ALIMENTO = p.ID_PEDIDO_ALIMENTO ");
	    
	    sql.append("LEFT JOIN ( ");
	    sql.append("  SELECT d.ID_PEDIDO_ALIMENTO, SUM(d.QTD_RECUPERANDO) AS QTD_ESPECIAL ");
	    sql.append("  FROM " + schema + "SGP_DIETA_ESP_PEDIDO d ");
	    sql.append("  GROUP BY d.ID_PEDIDO_ALIMENTO ");
	    sql.append(") esp ON esp.ID_PEDIDO_ALIMENTO = p.ID_PEDIDO_ALIMENTO ");	    
	    
	    sql.append("WHERE p.ID_UNIDADE_PRISIONAL = :idUnidade ");
	    sql.append("AND p.STAT_PEDIDO = 1 "); 	    
	    sql.append("AND TO_DATE(SUBSTR(p.DATA_PEDIDO, 1, 10), 'DD/MM/YYYY') ");
	    sql.append("    BETWEEN TO_DATE(:inicio, 'DD/MM/YYYY') AND TO_DATE(:fim, 'DD/MM/YYYY') ");
	    sql.append("GROUP BY tr.DESCRICAO ");

	    try {
	        Query query = entityManager.createNativeQuery(sql.toString());
	        query.setParameter("idUnidade", idUnidade);
	        query.setParameter("inicio", dataInicio);
	        query.setParameter("fim", dataFim);

	        List<Object[]> resultados = query.getResultList();

	        for (Object[] linha : resultados) {
	            String desc = (linha[0] != null) ? linha[0].toString().toUpperCase() : "";
	            
	            int comum    = ((Number) linha[1]).intValue(); 
	            int especial = linha[2] != null ? ((Number) linha[2]).intValue() : 0;

	            if (desc.contains("CAF")) {
	                totaisRelatorio.put("AUDIT_CAFE", comum);
	                totaisRelatorio.put("AUDIT_CAFE_ESPECIAL", especial);
	            } else if (desc.contains("ALMOC") || desc.contains("ALMOÇ")) {
	                totaisRelatorio.put("AUDIT_ALMOCO", comum);
	                totaisRelatorio.put("AUDIT_ALMOCO_ESPECIAL", especial);
	            } else if (desc.contains("JANT")) {
	                totaisRelatorio.put("AUDIT_JANTA", comum);
	                totaisRelatorio.put("AUDIT_JANTA_ESPECIAL", especial);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return totaisRelatorio;
	}

}
