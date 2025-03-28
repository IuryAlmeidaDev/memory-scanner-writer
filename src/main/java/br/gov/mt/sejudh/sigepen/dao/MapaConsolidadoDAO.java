package br.gov.mt.sejudh.sigepen.dao;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

}
