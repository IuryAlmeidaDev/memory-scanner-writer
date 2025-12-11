package br.gov.mt.sejudh.sigepen.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroInfopen;
import br.gov.mt.sejudh.sigepen.enumerator.IdadeRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Lgbt;
import br.gov.mt.sejudh.sigepen.enumerator.PopulacaoFeminino;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.TempoCondenacao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDeficienciaRelatorio;
import br.gov.mt.sejudh.sigepen.model.Escolaridade;
import br.gov.mt.sejudh.sigepen.model.EstadoCivil;
import br.gov.mt.sejudh.sigepen.model.Etnia;
import br.gov.mt.sejudh.sigepen.model.Nacionalidade;
import br.gov.mt.sejudh.sigepen.model.Raca;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.Religiao;
import br.gov.mt.sejudh.sigepen.model.dto.InfopenDTO;
import br.gov.mt.sejudh.sigepen.model.dto.InfopenRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class InfopenDAO {

	@Inject
	private EntityManager em;

	public Nacionalidade[] buscarNacionalidades(long idUnidade) {
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append(" select NA.id, NA.descricao, count(r.id) quantidade\r\n" + "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, \r\n" + " " + Sigepen.SEJUDH_SCHEMA + ".PESSOA PE"
					+ " where l.id_pessoa = pe.id\r\n" + "   and r.id = l.matricula\r\n" + "   and l.id_unidade = "
					+ idUnidade + " \r\n" + "   and r.status = 1\r\n" + " AND NA.ID = PE.NACIONALIDADE_ID"
					+ " group by NA.id, NA.descricao\r\n" + " order by NA.descricao\r\n" + "");

			Query query = this.em.createNativeQuery(jpql.toString());

			@SuppressWarnings("unchecked")
			List<Object[]> lista = (List<Object[]>) query.getResultList();
			Nacionalidade[] nacionalidades = new Nacionalidade[lista.size()];
			int i = 0;
			for (Object[] item : lista) {
				Nacionalidade nac = new Nacionalidade(((BigDecimal) item[0]).longValue(), (String) item[1]);
				nacionalidades[i] = nac;
				i++;
			}
			return nacionalidades;
		} catch (Exception e) {
		}
		return new Nacionalidade[0];
	}

	public Regime[] buscarRegimes(long idUnidade) {
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append(" select re.id, re.descricao, count(r.id) quantidade\r\n" + "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".regime re, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l\r\n" + " where r.regime_atual_id = re.id\r\n"
					+ "   and r.id = l.matricula\r\n" + "   and l.id_unidade = " + idUnidade + " \r\n"
					+ "   and r.status = 1\r\n" + " group by re.id, re.descricao\r\n" + " order by re.descricao\r\n"
					+ "");

			Query query = this.em.createNativeQuery(jpql.toString());

			@SuppressWarnings("unchecked")
			List<Object[]> lista = (List<Object[]>) query.getResultList();
			Regime[] regimes = new Regime[lista.size()];
			int i = 0;
			for (Object[] item : lista) {
				Regime nac = new Regime(((BigDecimal) item[0]).longValue(), (String) item[1]);
				regimes[i] = nac;
				i++;
			}
			return regimes;
		} catch (Exception e) {
		}
		return new Regime[0];
	}

	public EstadoCivil[] buscarEstadoCivil(long idUnidade) {
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append(" select E.id, E.descricao, count(r.id) quantidade\r\n" + "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, \r\n" + " " + Sigepen.SEJUDH_SCHEMA + ".PESSOA PE, " + " "
					+ Sigepen.SEJUDH_SCHEMA + ".ESTADO_CIVIL E " + " where l.id_pessoa = pe.id\r\n"
					+ "   and r.id = l.matricula\r\n" + "   and l.id_unidade = " + idUnidade + " \r\n"
					+ "   and r.status = 1\r\n" + " AND NA.ID = PE.NACIONALIDADE_ID" + " AND E.ID = R.ESTADO_CIVIL_ID "
					+ " group by E.id, E.descricao\r\n" + " order by E.descricao\r\n" + "");

			Query query = this.em.createNativeQuery(jpql.toString());

			@SuppressWarnings("unchecked")
			List<Object[]> lista = (List<Object[]>) query.getResultList();
			EstadoCivil[] estados = new EstadoCivil[lista.size()];
			int i = 0;
			for (Object[] item : lista) {
				EstadoCivil nac = new EstadoCivil(((BigDecimal) item[0]).longValue(), (String) item[1]);
				estados[i] = nac;
				i++;
			}
			return estados;
		} catch (Exception e) {
		}
		return new EstadoCivil[0];
	}

	public Religiao[] buscarReligiao(long idUnidade) {

		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append(" select E.id, E.descricao, count(r.id) quantidade\r\n" + "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, \r\n" + " " + Sigepen.SEJUDH_SCHEMA + ".PESSOA PE, " + " "
					+ Sigepen.SEJUDH_SCHEMA + ".RELIGIAO E " + " where l.id_pessoa = pe.id\r\n"
					+ "   and r.id = l.matricula\r\n" + "   and l.id_unidade = " + idUnidade + " \r\n"
					+ "   and r.status = 1\r\n" + " AND NA.ID = PE.NACIONALIDADE_ID" + " AND E.ID = R.RELIGIAO_ID "
					+ " group by E.id, E.descricao\r\n" + " order by E.descricao\r\n" + "");

			Query query = this.em.createNativeQuery(jpql.toString());

			@SuppressWarnings("unchecked")
			List<Object[]> lista = (List<Object[]>) query.getResultList();
			Religiao[] religioes = new Religiao[lista.size()];
			int i = 0;
			for (Object[] item : lista) {
				Religiao nac = new Religiao(((BigDecimal) item[0]).longValue(), (String) item[1]);
				religioes[i] = nac;
				i++;
			}
			return religioes;
		} catch (Exception e) {
		}
		return new Religiao[0];
	}

	public Escolaridade[] buscarEscolaridade(long idUnidade) {

		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append(" select E.id, E.descricao, count(r.id) quantidade\r\n" + "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, \r\n" + " " + Sigepen.SEJUDH_SCHEMA + ".PESSOA PE, " + " "
					+ Sigepen.SEJUDH_SCHEMA + ".ESCOLARIDADE E " + " where l.id_pessoa = pe.id\r\n"
					+ "   and r.id = l.matricula\r\n" + "   and l.id_unidade = " + idUnidade + " \r\n"
					+ "   and r.status = 1\r\n" + " AND NA.ID = PE.NACIONALIDADE_ID" + " AND E.ID = R.ESCOLARIDADE_ID "
					+ " group by E.id, E.descricao\r\n" + " order by E.descricao\r\n" + "");

			Query query = this.em.createNativeQuery(jpql.toString());

			@SuppressWarnings("unchecked")
			List<Object[]> lista = (List<Object[]>) query.getResultList();
			Escolaridade[] escolaridades = new Escolaridade[lista.size()];
			int i = 0;
			for (Object[] item : lista) {
				Escolaridade nac = new Escolaridade(((BigDecimal) item[0]).longValue(), (String) item[1]);
				escolaridades[i] = nac;
				i++;
			}
			return escolaridades;
		} catch (Exception e) {
		}
		return new Escolaridade[0];
	}

	public Raca[] buscarRaca(long idUnidade) {
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append(" select E.id, E.descricao, count(r.id) quantidade\r\n" + "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, \r\n" + " " + Sigepen.SEJUDH_SCHEMA + ".PESSOA PE, " + " "
					+ Sigepen.SEJUDH_SCHEMA + ".RACA E " + " where l.id_pessoa = pe.id\r\n"
					+ "   and r.id = l.matricula\r\n" + "   and l.id_unidade = " + idUnidade + " \r\n"
					+ "   and r.status = 1\r\n" + " AND NA.ID = PE.NACIONALIDADE_ID" + " AND E.ID = R.RACA_ID "
					+ " group by E.id, E.descricao\r\n" + " order by E.descricao\r\n" + "");

			Query query = this.em.createNativeQuery(jpql.toString());

			@SuppressWarnings("unchecked")
			List<Object[]> lista = (List<Object[]>) query.getResultList();
			Raca[] racas = new Raca[lista.size()];
			int i = 0;
			for (Object[] item : lista) {
				Raca nac = new Raca(((BigDecimal) item[0]).longValue(), (String) item[1]);
				racas[i] = nac;
				i++;
			}
			return racas;
		} catch (Exception e) {
		}
		return new Raca[0];
	}

	private List<InfopenRecuperandoDTO> buscarRecuperandos(FiltroInfopen filtro) {
		boolean usarMultiplasUnidades = filtro.getUnidadesIds() != null && !filtro.getUnidadesIds().isEmpty();
		
		String unidadeCondicao;
		if (usarMultiplasUnidades) {
		    unidadeCondicao = "IN (:UNIDADES)";
		} else {
		    unidadeCondicao = "= :UNIDADE";
		}


		String sql = "";
		if (filtro.getTempoCondenacao() != null && !TempoCondenacao.T.equals(filtro.getTempoCondenacao())) {
			sql = "select DISTINCT R.ID, P.NOME, P.NOME_MAE AS NOMEMAE, RE.DESCRICAO AS REGIME, R.CONDICAO_ATUAL AS CONDICAOATUAL,  \r\n"
					+ "   CASE R.STATUS \r\n" + "  	WHEN 1 THEN 'ATIVO'\r\n" + "  	WHEN 0 THEN 'INATIVO'\r\n"
					+ "  	WHEN 2 THEN 'INATIVO_PARCIAL'\r\n" + "  END AS STATUS, \r\n" + "  l.estrutura, \r\n "
					+ "	u.nome AS UNIDADE_NOME \r\n"	
					+ "  from " + Sigepen.SEJUDH_SCHEMA + ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".regime re, "
					+ Sigepen.SEJUDH_SCHEMA + ".lista_todos_recuperandos l, " + Sigepen.SEJUDH_SCHEMA
					+ ".pessoa p, \r\n" + " " + Sigepen.SEJUDH_SCHEMA + ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA
					+ ".processo pro, " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM p2, " + Sigepen.SEJUDH_SCHEMA
					+ ".unidade_prisional u" + " where r.regime_atual_id = re.id\r\n" + "   and r.id = l.matricula\r\n"
					+ "   and l.id_unidade " + unidadeCondicao + "\r\n"
					+ "   and r.status = 1\r\n" + "   AND r.PESSOA_ID = p.id\r\n" + " AND na.id = p.nacionalidade_id "
					+ " AND r.id = pro.recuperando_id " + " AND R.CONDICAO_ATUAL = 'CONDENADO'"
					+ " AND PRO.STATUS_PROCESSO <> 'FINALIZADO'" + " AND r.ID = p2.RECUPERANDO_ID"
					+ " AND p2.DATA_ENTRADA <= :DATA_FINAL "
					+ " AND (p2.DATA_SAIDA IS NULL OR p2.DATA_SAIDA >= :DATA_INICIAL) ";

		} else if (filtro.getPopulacaoFeminino() != null && (PopulacaoFeminino.C.equals(filtro.getPopulacaoFeminino())
				|| PopulacaoFeminino.D.equals(filtro.getPopulacaoFeminino()))) {
			sql = "select DISTINCT R.ID, P.NOME, P.NOME_MAE AS NOMEMAE, RE.DESCRICAO AS REGIME, R.CONDICAO_ATUAL AS CONDICAOATUAL,  \r\n"
					+ "   CASE R.STATUS \r\n" + "   WHEN 1 THEN 'ATIVO'\r\n" + "   WHEN 0 THEN 'INATIVO'\r\n"
					+ "   WHEN 2 THEN 'INATIVO_PARCIAL'\r\n" + "  END AS STATUS, \r\n" + "  l.estrutura, \r\n "
					+ "	u.nome AS UNIDADE_NOME \r\n"
					+ "  from " + Sigepen.SEJUDH_SCHEMA + ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".regime re, "
					+ Sigepen.SEJUDH_SCHEMA + ".lista_todos_recuperandos l, " + Sigepen.SEJUDH_SCHEMA
					+ ".pessoa p, \r\n" + " " + Sigepen.SEJUDH_SCHEMA + ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA
					+ ".parente par " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM p2, " 
					+ " " + Sigepen.SEJUDH_SCHEMA + ".unidade_prisional u "
					+ " where r.regime_atual_id = re.id\r\n"
					+ "   and r.id = l.matricula\r\n" + "   and l.id_unidade " + unidadeCondicao + "\r\n"
					+ "   and r.status = 1\r\n" + "   AND r.PESSOA_ID = p.id\r\n" 
					+ " AND na.id = p.nacionalidade_id "
					+ " and par.recuperando_id = r.id" + " AND r.ID = p2.RECUPERANDO_ID"
					+ " AND p2.DATA_ENTRADA <= :DATA_FINAL "
					+ " AND (p2.DATA_SAIDA IS NULL OR p2.DATA_SAIDA >= :DATA_INICIAL) ";

		} else if (filtro.getTipoRelatorio() == 1 && filtro.getDataInicial() != null && filtro.getDataFinal() != null) {
			sql = "select DISTINCT R.ID, P.NOME, P.NOME_MAE AS NOMEMAE, RE.DESCRICAO AS REGIME, R.CONDICAO_ATUAL AS CONDICAOATUAL, "
					+ "   CASE R.STATUS " + "   WHEN 1 THEN 'ATIVO' " + "   WHEN 0 THEN 'INATIVO' "
					+ "   WHEN 2 THEN 'INATIVO_PARCIAL' " + "  END AS STATUS, " + "  l.estrutura, "
					+ "  p2.DATA_ENTRADA, " + "  p2.DATA_SAIDA, "
					+ " u.nome AS UNIDADE_NOME "+ "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".regime re, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, " + Sigepen.SEJUDH_SCHEMA + ".pessoa p, " + Sigepen.SEJUDH_SCHEMA
					+ ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM p2, "
					+ " " + Sigepen.SEJUDH_SCHEMA + ".unidade_prisional u "
					+ " where r.regime_atual_id = re.id " + "   AND l.id_unidade = u.id " 
					+ "   and r.id = l.matricula " 
					+ "   and l.id_unidade " + unidadeCondicao + "\r\n" 
					+ "   AND r.PESSOA_ID = p.id "
					+ "   AND na.id = p.nacionalidade_id " + "   AND r.ID = p2.RECUPERANDO_ID "
					+ " AND p2.DATA_ENTRADA <= :DATA_FINAL "
					+ " AND (p2.DATA_SAIDA IS NULL OR p2.DATA_SAIDA >= :DATA_INICIAL) ";

			/* Infopen Sequenciado Detalhado */
		} else if (filtro.getTipoRelatorio() == 4 && filtro.getDataInicial() == null && filtro.getDataFinal() == null) {
			sql = "select DISTINCT R.ID, P.NOME, P.NOME_MAE AS NOMEMAE, RE.DESCRICAO AS REGIME, R.CONDICAO_ATUAL AS CONDICAOATUAL, "
					+ "   CASE R.STATUS " + "   WHEN 1 THEN 'ATIVO' " + "   WHEN 0 THEN 'INATIVO' "
					+ "   WHEN 2 THEN 'INATIVO_PARCIAL' " + "  END AS STATUS, " + "  l.estrutura, "
					+ "  p2.DATA_ENTRADA, " + "  p2.DATA_SAIDA, " 
					+ " u.nome AS UNIDADE_NOME "
					+ "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".regime re, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, " + Sigepen.SEJUDH_SCHEMA + ".pessoa p, " + Sigepen.SEJUDH_SCHEMA
					+ ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM p2, "
					+ " " + Sigepen.SEJUDH_SCHEMA + ".unidade_prisional u "
					+ " where r.regime_atual_id = re.id " + "   AND l.id_unidade = u.id " 
					+ "   and r.id = l.matricula " 
					+ "   and l.id_unidade "
					+ unidadeCondicao + "\r\n" + "   and r.status = 1 " + "   AND r.PESSOA_ID = p.id "
					+ "   AND na.id = p.nacionalidade_id " + "   AND r.ID = p2.RECUPERANDO_ID";

			/* Infopen Sequenciado Por Data */
		} else if (filtro.getTipoRelatorio() == 4 && filtro.getDataInicial() != null && filtro.getDataFinal() != null) {
			sql = "select DISTINCT R.ID, P.NOME, P.NOME_MAE AS NOMEMAE, RE.DESCRICAO AS REGIME, R.CONDICAO_ATUAL AS CONDICAOATUAL, "
					+ "   CASE R.STATUS " + "   WHEN 1 THEN 'ATIVO' " + "   WHEN 0 THEN 'INATIVO' "
					+ "   WHEN 2 THEN 'INATIVO_PARCIAL' " + "  END AS STATUS, " + "  l.estrutura, "
					+ "  p2.DATA_ENTRADA, " + "  p2.DATA_SAIDA, "
					+ " u.nome AS UNIDADE_NOME "
					+ "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".regime re, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, " + Sigepen.SEJUDH_SCHEMA + ".pessoa p, " + Sigepen.SEJUDH_SCHEMA
					+ ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM p2, "
					+ " " + Sigepen.SEJUDH_SCHEMA + ".unidade_prisional u "
					+ " where r.regime_atual_id = re.id " + "   AND l.id_unidade = u.id " 
					+ "   and r.id = l.matricula "
					+ "   and l.id_unidade " + unidadeCondicao + "\r\n" 
					+ "   AND r.PESSOA_ID = p.id "
					+ "   AND na.id = p.nacionalidade_id " + "   AND r.ID = p2.RECUPERANDO_ID"
					+ " AND p2.DATA_ENTRADA <= :DATA_FINAL "
					+ " AND (p2.DATA_SAIDA IS NULL OR p2.DATA_SAIDA >= :DATA_INICIAL) ";

			/* Infopen Unificado */
		} else if (filtro.getTipoRelatorio() == 5 && filtro.getDataInicial() == null && filtro.getDataFinal() == null) {
			sql = "select DISTINCT R.ID, P.NOME, P.NOME_MAE AS NOMEMAE, RE.DESCRICAO AS REGIME, R.CONDICAO_ATUAL AS CONDICAOATUAL, "
					+ "   CASE R.STATUS " + "   WHEN 1 THEN 'ATIVO' " + "   WHEN 0 THEN 'INATIVO' "
					+ "   WHEN 2 THEN 'INATIVO_PARCIAL' " + "  END AS STATUS, " + "  l.estrutura, "
					+ "  p2.DATA_ENTRADA, " + "  p2.DATA_SAIDA, " 
					+ " u.nome AS UNIDADE_NOME "
					+ "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".regime re, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, " + Sigepen.SEJUDH_SCHEMA + ".pessoa p, " + Sigepen.SEJUDH_SCHEMA
					+ ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM p2, "
					+ " " + Sigepen.SEJUDH_SCHEMA + ".unidade_prisional u "
					+ " where r.regime_atual_id = re.id " + "   AND l.id_unidade = u.id " 
					+ "   and r.id = l.matricula " + "   and l.id_unidade " + unidadeCondicao + "\r\n"
					+ "   and u.id " + unidadeCondicao + "\r\n"
					+ "   and r.status = 1 "
					+ "   AND r.PESSOA_ID = p.id " + "   AND na.id = p.nacionalidade_id "
					+ "   AND r.ID = p2.RECUPERANDO_ID";

			/* Infopen Unificado Por Data */
		} else if (filtro.getTipoRelatorio() == 5 && filtro.getDataInicial() != null && filtro.getDataFinal() != null) {
			sql = "select DISTINCT R.ID, P.NOME, P.NOME_MAE AS NOMEMAE, RE.DESCRICAO AS REGIME, R.CONDICAO_ATUAL AS CONDICAOATUAL, "
					+ "   CASE R.STATUS " + "   WHEN 1 THEN 'ATIVO' " + "   WHEN 0 THEN 'INATIVO' "
					+ "   WHEN 2 THEN 'INATIVO_PARCIAL' " + "  END AS STATUS, " + "  l.estrutura, "
					+ "  p2.DATA_ENTRADA, " + "  p2.DATA_SAIDA, " 
					+ " u.nome AS UNIDADE_NOME "
					+ "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".regime re, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, " + Sigepen.SEJUDH_SCHEMA + ".pessoa p, " + Sigepen.SEJUDH_SCHEMA
					+ ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM p2, "
					+ " " + Sigepen.SEJUDH_SCHEMA + ".unidade_prisional u "
					+ " where r.regime_atual_id = re.id " + "   AND l.id_unidade = u.id " 
					+ "   and r.id = l.matricula " + "   and l.id_unidade " + unidadeCondicao + "\r\n"
					+ "   and u.id " + unidadeCondicao + "\r\n"
					+ "   AND r.PESSOA_ID = p.id " + "   AND na.id = p.nacionalidade_id "
					+ "   AND r.ID = p2.RECUPERANDO_ID"
					+ " AND p2.DATA_ENTRADA <= :DATA_FINAL "
					+ " AND (p2.DATA_SAIDA IS NULL OR p2.DATA_SAIDA >= :DATA_INICIAL) ";

		} else {
			sql = "select DISTINCT R.ID, P.NOME, P.NOME_MAE AS NOMEMAE, RE.DESCRICAO AS REGIME, R.CONDICAO_ATUAL AS CONDICAOATUAL, "
					+ "   CASE R.STATUS " + "   WHEN 1 THEN 'ATIVO' " + "   WHEN 0 THEN 'INATIVO' "
					+ "   WHEN 2 THEN 'INATIVO_PARCIAL' " + "  END AS STATUS, " + "  l.estrutura, "
					+ "  p2.DATA_ENTRADA, " + "  p2.DATA_SAIDA, "
					+ " u.nome AS UNIDADE_NOME "
					+ "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".regime re, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, " + Sigepen.SEJUDH_SCHEMA + ".pessoa p, " + Sigepen.SEJUDH_SCHEMA
					+ ".nacionalidade na, " + Sigepen.SEJUDH_SCHEMA + ".PASSAGEM p2, "
					+ " " + Sigepen.SEJUDH_SCHEMA + ".unidade_prisional u "
					+ " where r.regime_atual_id = re.id " + "   AND l.id_unidade = u.id " 
					+ "   and r.id = l.matricula " + "   and l.id_unidade " + unidadeCondicao + "\r\n"
					+ "   and u.id " + unidadeCondicao + "\r\n"
					+ "   and r.status = 1 " + "   AND r.PESSOA_ID = p.id "
					+ "   AND na.id = p.nacionalidade_id " + "   AND r.ID = p2.RECUPERANDO_ID";

		}
		

		if (filtro.getNacionalidade() != null) {
			sql += " AND NA.ID = :NACIONALIDADE ";
		}
		if (filtro.getRegime() != null) {
			sql += " AND RE.ID = :REGIME ";
		}
		if (filtro.getEstadoCivil() != null) {
			sql += " AND R.ESTADO_CIVIL_ID = :ESTADO_CIVIL";
		}
		if (filtro.getReligiao() != null) {
			sql += " AND R.RELIGIAO_ID = :RELIGIAO";
		}
		if (filtro.getEscolaridade() != null) {
			sql += " AND R.ESCOLARIDADE_ID = :ESCOLARIDADE";
		}
		if (filtro.getRaca() != null) {
			sql += " AND R.RACA_ID = :RACA";
		}
		if (filtro.getIdadeRecuperando() != null && !IdadeRecuperando.T.equals(filtro.getIdadeRecuperando())) {
			String data = montarIdade(filtro.getIdadeRecuperando());
			sql += data;
		}
		if (filtro.getTempoCondenacao() != null && !TempoCondenacao.T.equals(filtro.getTempoCondenacao())) {
			String tempo = montarTempo(filtro.getTempoCondenacao());
			sql += tempo;
		}
		if (filtro.getLgbt() != null && !Lgbt.T.equals(filtro.getLgbt())) {
			sql += montarLgbt(filtro.getLgbt());
		}
		if (filtro.getEtnia() != null) {
			sql += " AND R.ID_ETNIA = :ETNIA";
		}
		if (!TipoDeficienciaRelatorio.TODOS.equals(filtro.getTipoDeficiencia())) {
			sql += " AND R.PNE IS NOT NULL AND R.PNE = :PNE";
		}
		if (filtro.getRecebeAuxilio() != null) {
			sql += " AND R.PNE IS NOT NULL AND R.PNE > 0 AND R.RECEBE_BENEFICIO = :RECEBE_BENEFICIO";
		}
		if (filtro.getPopulacaoFeminino() != null && !PopulacaoFeminino.T.equals(filtro.getPopulacaoFeminino())) {
			sql += montarPopulacaoFeminino(filtro.getPopulacaoFeminino());
		}
		if (filtro.getAposentado() != null) {
			sql += " AND trunc((trunc(sysdate) - P.DATA_NASCIMENTO) / 365) > 60 AND R.APOSENTADO = :APOSENTADO";
		}
		sql += " order by P.NOME ASC";
		List<InfopenRecuperandoDTO> recs = new ArrayList<InfopenRecuperandoDTO>();

		Query query = this.em.createNativeQuery(sql);

		if (usarMultiplasUnidades) {
		    if (filtro.getUnidadesIds() != null && !filtro.getUnidadesIds().isEmpty()) {
		        query.setParameter("UNIDADES", filtro.getUnidadesIds());
		    }
		} else {
		    if (filtro.getUnidade() > 0) {
		        query.setParameter("UNIDADE", filtro.getUnidade());
		    } else {
		        return new ArrayList<>();
		    }
		}


		

		if (filtro.getNacionalidade() != null) {
			query.setParameter("NACIONALIDADE", filtro.getNacionalidade().getId());
		}
		if (filtro.getRegime() != null) {
			query.setParameter("REGIME", filtro.getRegime().getId());
		}
		if (filtro.getEstadoCivil() != null) {
			query.setParameter("ESTADO_CIVIL", filtro.getEstadoCivil().getId());
		}
		if (filtro.getReligiao() != null) {
			query.setParameter("RELIGIAO", filtro.getReligiao().getId());
		}
		if (filtro.getEscolaridade() != null) {
			query.setParameter("ESCOLARIDADE", filtro.getEscolaridade().getId());
		}
		if (filtro.getRaca() != null) {
			query.setParameter("RACA", filtro.getRaca().getId());
		}
		if (filtro.getEtnia() != null) {
			query.setParameter("ETNIA", filtro.getEtnia().getId());
		}
		if (!TipoDeficienciaRelatorio.TODOS.equals(filtro.getTipoDeficiencia())) {
			query.setParameter("PNE", filtro.getTipoDeficiencia().ordinal() - 1);
		}
		if (filtro.getRecebeAuxilio() != null) {
			query.setParameter("RECEBE_BENEFICIO", SimNao.N.equals(filtro.getRecebeAuxilio()) ? 0 : 1);
		}
		if (filtro.getAposentado() != null) {
			query.setParameter("APOSENTADO", SimNao.N.equals(filtro.getAposentado()) ? 0 : 1);
		}

		if (filtro.getDataInicial() != null) {
			query.setParameter("DATA_INICIAL", filtro.getDataInicial());
		}

		if (filtro.getDataFinal() != null) {
			query.setParameter("DATA_FINAL", filtro.getDataFinal());
		}
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		String descricaoTitulo = "População carcerária por Nacionalidade";

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		results.stream().forEach((record) -> {
			Long id = ((BigDecimal) record[0]).longValue();
			String nome = (String) record[1];
			String nomeMae = (String) record[2];
			String regime = (String) record[3];
			String condicao = (String) record[4];
			String status = (String) record[5];
			String estrutura = (String) record[6];
			String descricaoFiltro = filtro.getNacionalidade() == null ? null
					: filtro.getNacionalidade().getDescricao();

			String dataEntrada = "";
			String dataSaida = "";

			String unidadeNome = (String) record[9];

			if (record.length > 7 && record[7] != null) {
				dataEntrada = sdf.format((Date) record[7]);
			}

			if (record.length > 8 && record[8] != null) {
				dataSaida = sdf.format((Date) record[8]);
			}

			InfopenRecuperandoDTO r = new InfopenRecuperandoDTO(id, nome, nomeMae, regime, condicao, status, filtro,
					descricaoFiltro, descricaoTitulo, estrutura, dataEntrada, dataSaida, unidadeNome);
			recs.add(r);
		});

		return recs;
	}

	private String montarPopulacaoFeminino(PopulacaoFeminino populacaoFeminino) {
		if (PopulacaoFeminino.A.equals(populacaoFeminino)) {
			return " AND P.SEXO = 'F' AND R.GESTANTE = 1";
		} else if (PopulacaoFeminino.B.equals(populacaoFeminino)) {
			return " AND P.SEXO = 'F' AND R.LACTANTE = 1";
		} else if (PopulacaoFeminino.C.equals(populacaoFeminino)) {
			return " AND P.SEXO = 'F' AND PAR.PARENTESCO_ID = 3 AND trunc((trunc(sysdate) - PAR.DATA_NASCIMENTO) / 365) < 12";
		} else if (PopulacaoFeminino.D.equals(populacaoFeminino)) {
			return " AND P.SEXO = 'F' AND R.POSSUI_FILHO = 1 AND R.FILHO_PCD = 1";
		}
		return " AND (1=1)";
	}

	private String montarLgbt(Lgbt lgbt) {
		if (Lgbt.A.equals(lgbt)) {
			return " AND P.SEXO = 'M' AND R.IDENTIDADE_GENERO_ID = 2";
		} else if (Lgbt.B.equals(lgbt)) {
			return " AND P.SEXO = 'F' AND R.IDENTIDADE_GENERO_ID = 2";
		} else if (Lgbt.C.equals(lgbt)) {
			return " AND R.IDENTIDADE_GENERO_ID = 1";
		} else if (Lgbt.D.equals(lgbt)) {
			return " AND R.ORIENTACAO_SEXUAL_ID = 3";
		} else if (Lgbt.E.equals(lgbt)) {
			return " AND R.ORIENTACAO_SEXUAL_ID = 2";
		} else if (Lgbt.F.equals(lgbt)) {
			return " AND R.ORIENTACAO_SEXUAL_ID = 1";
		}
		return " AND (1=1)";
	}

	private String montarTempo(TempoCondenacao tempoCondenacao) {
		if (TempoCondenacao.A.equals(tempoCondenacao)) {
			return " AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) > 365 * 100";
		} else if (TempoCondenacao.B.equals(tempoCondenacao)) {
			return " AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) < 365 * 4";
		} else if (TempoCondenacao.C.equals(tempoCondenacao)) {
			return " AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) > 365 * 4 AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) < 365 * 8 ";
		} else if (TempoCondenacao.D.equals(tempoCondenacao)) {
			return " AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) > 365 * 8 AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) < 365 * 15 ";
		} else if (TempoCondenacao.E.equals(tempoCondenacao)) {
			return " AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) > 365 * 15 AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) < 365 * 20 ";
		} else if (TempoCondenacao.F.equals(tempoCondenacao)) {
			return " AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) > 365 * 20 AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) < 365 * 30 ";
		} else if (TempoCondenacao.G.equals(tempoCondenacao)) {
			return " AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) > 365 * 30 AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) < 365 * 50 ";
		} else if (TempoCondenacao.H.equals(tempoCondenacao)) {
			return " AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) > 365 * 50 AND ((PRO.ANO * 365) + (PRO.MES * 30) + PRO.DIA) < 365 * 100 ";
		}
		return "AND (1=1)";
	}

	private String montarIdade(IdadeRecuperando idadeRecuperando) {
		if (IdadeRecuperando.A.equals(idadeRecuperando)) {
			return " AND trunc((trunc(sysdate) - p.data_nascimento) / 365) > 60";
		} else if (IdadeRecuperando.E.equals(idadeRecuperando)) {
			return " AND trunc((trunc(sysdate) - p.data_nascimento) / 365) >= 25 AND  trunc((trunc(sysdate) - p.data_nascimento) / 365) <= 29";
		} else if (IdadeRecuperando.F.equals(idadeRecuperando)) {
			return " AND trunc((trunc(sysdate) - p.data_nascimento) / 365) >= 18 AND  trunc((trunc(sysdate) - p.data_nascimento) / 365) <= 24";
		} else if (IdadeRecuperando.D.equals(idadeRecuperando)) {
			return " AND trunc((trunc(sysdate) - p.data_nascimento) / 365) >= 30 AND  trunc((trunc(sysdate) - p.data_nascimento) / 365) <= 34";
		} else if (IdadeRecuperando.C.equals(idadeRecuperando)) {
			return " AND trunc((trunc(sysdate) - p.data_nascimento) / 365) >= 35 AND  trunc((trunc(sysdate) - p.data_nascimento) / 365) <= 45";
		} else if (IdadeRecuperando.B.equals(idadeRecuperando)) {
			return " AND trunc((trunc(sysdate) - p.data_nascimento) / 365) >= 46 AND  trunc((trunc(sysdate) - p.data_nascimento) / 365) <= 60";
		}
		return "AND (1=1)";
	}

	public List<InfopenDTO> buscarInfopenDetalhadoPorFiltro(FiltroInfopen filtro) {

		List<InfopenRecuperandoDTO> recuperandos = buscarRecuperandos(filtro);

		Map<String, List<InfopenRecuperandoDTO>> recuperandosPorUnidade = recuperandos.stream()
				.collect(Collectors.groupingBy(InfopenRecuperandoDTO::getUnidade));

		List<InfopenDTO> retorno = new ArrayList<>();
		for (Map.Entry<String, List<InfopenRecuperandoDTO>> entry : recuperandosPorUnidade.entrySet()) {
			InfopenDTO info = new InfopenDTO();
			info.setRecuperandos(entry.getValue());
			info.setFiltro(filtro);
			info.setUnidade(entry.getKey());
			retorno.add(info);

		}

		return retorno;

	}

	public Etnia[] buscarEtnia(long idUnidade) {
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append(" select NA.id, NA.descricao, count(r.id) quantidade\r\n" + "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".recuperando r, " + Sigepen.SEJUDH_SCHEMA + ".etnia na, " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, \r\n" + " " + Sigepen.SEJUDH_SCHEMA + ".PESSOA PE"
					+ " where l.id_pessoa = pe.id\r\n" + "   and r.id = l.matricula\r\n" + "   and l.id_unidade = "
					+ idUnidade + " \r\n" + "   and r.status = 1\r\n" + "   and r.raca_id = 5\r\n"
					+ " AND NA.ID = r.ID_ETNIA " + " group by NA.id, NA.descricao\r\n" + " order by NA.descricao\r\n"
					+ "");

			Query query = this.em.createNativeQuery(jpql.toString());

			@SuppressWarnings("unchecked")
			List<Object[]> lista = (List<Object[]>) query.getResultList();
			Etnia[] etnias = new Etnia[lista.size()];
			int i = 0;
			for (Object[] item : lista) {
				Etnia etnia = new Etnia(((BigDecimal) item[0]).longValue(), (String) item[1]);
				etnias[i] = etnia;
				i++;
			}
			return etnias;
		} catch (Exception e) {
		}
		return new Etnia[0];
	}

	public long buscarFeminino(long idUnidade) {
		Long result = 0l;
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append(" select count(l.matricula) quantidade, 0\r\n" + "  from " + Sigepen.SEJUDH_SCHEMA
					+ ".lista_todos_recuperandos l, \r\n" + " " + Sigepen.SEJUDH_SCHEMA + ".PESSOA PE "
					+ " where l.id_pessoa = pe.id\r\n" + "   and l.id_unidade = " + idUnidade + " \r\n"
					+ "   and l.status = 'Ativo'\r\n" + "   and l.sexo = 'F'\r\n");

			Query query = this.em.createNativeQuery(jpql.toString());

			@SuppressWarnings("unchecked")
			List<Object[]> lista = (List<Object[]>) query.getResultList();
			for (Object[] item : lista) {
				result = ((BigDecimal) item[0]).longValue();
			}
		} catch (Exception e) {
		}
		return result;
	}
}
