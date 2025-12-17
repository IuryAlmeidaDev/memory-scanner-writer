package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.CondicaoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamentoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntradaRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoRelatorioEntradaSaidaRec;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioEntradaSaidaRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
public class RelatorioEntradaSaidaRecuperandoDAO {
	
	@Inject
	private EntityManager em;

	public List<RelatorioEntradaSaidaRecuperandoDTO> buscarRelatorioEntradaSaidaPorFiltro(FiltroRelatorioEntradaSaidaRecuperando filtro) {
		return buscarDados(filtro);
	}
	
	private List<RelatorioEntradaSaidaRecuperandoDTO> buscarDados(FiltroRelatorioEntradaSaidaRecuperando filtro) {
        List<RelatorioEntradaSaidaRecuperandoDTO> retorno = new ArrayList<>();
		if(TipoRelatorioEntradaSaidaRec.ENTRADA_SAIDA.equals(filtro.getTipoRelatorioEntradaSaidaRec()) || TipoRelatorioEntradaSaidaRec.ENTRADA.equals(filtro.getTipoRelatorioEntradaSaidaRec())) {
            retorno.addAll(this.buscarDadosEntradas(filtro));
		}

		if(TipoRelatorioEntradaSaidaRec.ENTRADA_SAIDA.equals(filtro.getTipoRelatorioEntradaSaidaRec()) || TipoRelatorioEntradaSaidaRec.SAIDA.equals(filtro.getTipoRelatorioEntradaSaidaRec())) {
			List<RelatorioEntradaSaidaRecuperandoDTO> r = this.buscarDadosSaidas(filtro);
            retorno.addAll(r);
		}

        if (!retorno.isEmpty()) {
			return retorno.stream().distinct().collect(Collectors.toList());
		} else {
			return retorno;
		}
	}

	private List<RelatorioEntradaSaidaRecuperandoDTO> buscarDadosEntradas(FiltroRelatorioEntradaSaidaRecuperando filtro) {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    
	    Calendar calInicio = Calendar.getInstance();
	    calInicio.setTime(filtro.getDataInicial());
	    calInicio.set(Calendar.HOUR_OF_DAY, 0);
	    calInicio.set(Calendar.MINUTE, 0);
	    calInicio.set(Calendar.SECOND, 0);
	    calInicio.set(Calendar.MILLISECOND, 0);
	    Date dataIni = calInicio.getTime();
	    
	    Calendar calFim = Calendar.getInstance();
	    calFim.setTime(filtro.getDataFinal());
	    calFim.set(Calendar.HOUR_OF_DAY, 23);
	    calFim.set(Calendar.MINUTE, 59);
	    calFim.set(Calendar.SECOND, 59);
	    calFim.set(Calendar.MILLISECOND, 999);
	    Date dataFim = calFim.getTime();
	    
	    String dataInicial = sdf.format(dataIni);
	    String dataFinal = sdf.format(dataFim);
	    
	   
	    
	    StringBuilder hql = new StringBuilder();

	    hql.append(" select distinct  ");
	    hql.append(" r.matricula, ");
	    hql.append(" pe.nome, ");
	    hql.append(" pe.nome_mae, ");
	    hql.append(" pe.data_nascimento, ");
	    hql.append(" p.data_entrada, ");
	    hql.append(" p.data_saida, ");
	    hql.append(" pe.nome_social ");
	    hql.append(" from " + Sigepen.SEJUDH_SCHEMA + ".passagem p, " + Sigepen.SEJUDH_SCHEMA + ".lista_todos_recuperandos r, " + Sigepen.SEJUDH_SCHEMA + ".pessoa pe, " + Sigepen.SEJUDH_SCHEMA + ".processo pro, " + Sigepen.SEJUDH_SCHEMA + ".processo_regime_condicao prc ");
	    hql.append(" where ");
	    hql.append("  p.data_entrada >= to_date('" + dataInicial + "','DD/MM/YYYY HH24:MI:SS') and p.data_entrada <= to_date('" + dataFinal + "','DD/MM/YYYY HH24:MI:SS') ");
	    hql.append(" and p.recuperando_id = r.matricula ");
	    hql.append(" and r.id_pessoa = pe.id ");
	    hql.append(" and r.matricula = pro.recuperando_id ");
	    hql.append(" and prc.processo_id = pro.id ");
	    hql.append(" and prc.tipo_processo not in ('OUTRO_SISTEMA_PENITENCIARIO', 'FUGA', 'OBITO', 'ALVARA_DE_SOLTURA') ");	
	    
	    if(filtro.getUnidade() > 0) {
	        hql.append(" and r.id_unidade = " + filtro.getUnidade());
	    }
	    
	    if(filtro.getRegime() != null) {
	        hql.append(" and r.regime = '" + filtro.getRegime().getDescricao() + "' ");
	    }
	    
	    if(filtro.getCondicaoAtual() != null && !CondicaoRelatorio.TODAS.equals(filtro.getCondicaoAtual())) {
	        hql.append(" and r.condicao = '" + filtro.getCondicaoAtual().name() + "'");
	    }
	    
	    if(filtro.getTipoProcesso() != null && !TipoEntradaRelatorio.TODAS.equals(filtro.getTipoProcesso())) {
	        hql.append(" and prc.tipo_processo = '" + filtro.getTipoProcesso().name() + "'");
	    }
	    
	    if(filtro.getTipoDesligamento() != null && !TipoDesligamentoRelatorio.TODOS.equals(filtro.getTipoDesligamento())) {
	        hql.append(" and p.tipo_desligamento = '" + filtro.getTipoDesligamento().name() + "'");
	    }
	    
	    hql.append(" order by p.data_entrada, pe.nome, p.data_saida ");
	    
	    System.out.println("QUERY: " + hql.toString());
	    
	    Query query = this.em.createNativeQuery(hql.toString());
	    
	    @SuppressWarnings("unchecked")
	    List<Object[]> results = query.getResultList();
	    
	    System.out.println("Registros encontrados: " + results.size());
	    
	    List<RelatorioEntradaSaidaRecuperandoDTO> retorno = new ArrayList<RelatorioEntradaSaidaRecuperandoDTO>();

	    results.forEach((record) -> {
	        Long matricula = ((BigDecimal) record[0]).longValue();
	        String nome = (String) record[1];
	        String nomeMae = (String) record[2];
	        Date dtNascimento = (Date) record[3];
	        Date dtEntrada = (Date) record[4];
	        Date dtSaida = (Date) record[5];
	        String tipoEntrada = null;
	        String tipoDesligamento = null;
	        String regime = null;
	        String condicao = null;
	        String situacao = null;
	        String nomeSocial = (String) record[6];

	        RelatorioEntradaSaidaRecuperandoDTO r = new RelatorioEntradaSaidaRecuperandoDTO(matricula, nome, nomeSocial, nomeMae, dtNascimento, dtEntrada, dtSaida, tipoEntrada, tipoDesligamento, regime, condicao, situacao, filtro);
	        retorno.add(r);
	    });
	    
	    return retorno;
	}
	private List<RelatorioEntradaSaidaRecuperandoDTO> buscarDadosSaidas(FiltroRelatorioEntradaSaidaRecuperando filtro) {
		
		
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    
	    Calendar calInicio = Calendar.getInstance();
	    calInicio.setTime(filtro.getDataInicial());
	    calInicio.set(Calendar.HOUR_OF_DAY, 0);
	    calInicio.set(Calendar.MINUTE, 0);
	    calInicio.set(Calendar.SECOND, 0);
	    calInicio.set(Calendar.MILLISECOND, 0);
	    Date dataIni = calInicio.getTime();
	    
	    Calendar calFim = Calendar.getInstance();
	    calFim.setTime(filtro.getDataFinal());
	    calFim.set(Calendar.HOUR_OF_DAY, 23);
	    calFim.set(Calendar.MINUTE, 59);
	    calFim.set(Calendar.SECOND, 59);
	    calFim.set(Calendar.MILLISECOND, 999);
	    Date dataFim = calFim.getTime();
	    
	    String dataInicial = sdf.format(dataIni);
	    String dataFinal = sdf.format(dataFim);
	    
	    
	    StringBuilder hql = new StringBuilder();

	    hql.append(" select distinct  ");
	    hql.append(" r.matricula, ");
	    hql.append(" pe.nome, ");
	    hql.append(" pe.nome_mae, ");
	    hql.append(" pe.data_nascimento, ");
	    hql.append(" p.data_entrada, ");
	    hql.append(" p.data_saida, ");
	    hql.append(" pe.nome_social ");
	    hql.append(" from " + Sigepen.SEJUDH_SCHEMA + ".passagem p, " + Sigepen.SEJUDH_SCHEMA + ".lista_todos_recuperandos r, " + Sigepen.SEJUDH_SCHEMA + ".pessoa pe ");
	    hql.append(" where ");
	    hql.append("  p.data_saida >= to_date('" + dataInicial + "','DD/MM/YYYY HH24:MI:SS') and p.data_saida <= to_date('" + dataFinal + "','DD/MM/YYYY HH24:MI:SS') ");
	    hql.append(" and p.recuperando_id = r.matricula ");
	    hql.append(" and r.id_pessoa = pe.id ");
	    
	    if(filtro.getUnidade() > 0) {
	        hql.append(" and r.id_unidade = " + filtro.getUnidade());
	    }
	    
	    if(filtro.getRegime() != null) {
	        hql.append(" and r.regime = '" + filtro.getRegime().getDescricao() + "' ");
	    }
	    
	    if(filtro.getCondicaoAtual() != null && !CondicaoRelatorio.TODAS.equals(filtro.getCondicaoAtual())) {
	        hql.append(" and r.condicao = '" + filtro.getCondicaoAtual().name() + "'");
	    }
	    
	    if(filtro.getTipoDesligamento() != null && !TipoDesligamentoRelatorio.TODOS.equals(filtro.getTipoDesligamento())) {
	        hql.append(" and p.tipo_desligamento = '" + filtro.getTipoDesligamento().name() + "'");
	    }
	    
	    hql.append(" order by p.data_entrada, pe.nome, p.data_saida ");
	    
	    Query query = this.em.createNativeQuery(hql.toString());
	    
	    @SuppressWarnings("unchecked")
	    List<Object[]> results = query.getResultList();
	    
	    List<RelatorioEntradaSaidaRecuperandoDTO> retorno = new ArrayList<RelatorioEntradaSaidaRecuperandoDTO>();
	    
	    results.stream().forEach((record) -> {
	        Long matricula = ((BigDecimal) record[0]).longValue();
	        String nome = (String) record[1];
	        String nomeMae = (String) record[2];
	        Date dtNascimento = (Date) record[3];
	        Date dtEntrada = (Date) record[4];
	        Date dtSaida = (Date) record[5];
	        String tipoEntrada = null;
	        String tipoDesligamento = null;
	        String regime = null;
	        String condicao = null;
	        String situacao = null;
	        String nomeSocial = (String) record[6];

	        RelatorioEntradaSaidaRecuperandoDTO r = new RelatorioEntradaSaidaRecuperandoDTO(matricula, nome, nomeSocial, nomeMae, dtNascimento, dtEntrada, dtSaida, tipoEntrada, tipoDesligamento, regime, condicao, situacao, filtro);
	        retorno.add(r);
	    });
	    
	    return retorno;
	}

	private String descricaoSaida(String descricao) {
		if("OUTRO_SISTEMA_PENITENCIARIO".equals(descricao))
			return "Outro sistema penitenciario";
		if("FUGA".equals(descricao))
			return "Fuga";
		if("OBITO".equals(descricao))
			return "Óbito";
		if("ALVARA_DE_SOLTURA".equals(descricao))
			return "Alvará de soltura";
		if("TERMO_USO_TORNOZELEIRA".equals(descricao))
			return "Termo de uso de tornozeleira";		
		return descricao;
	}

	private String descricaoEntrada(String descricao) {
		if("NOTA_CULPA".equals(descricao))
			return "Nota de culpa";
		if("GUIA_DE_EXECUCAO".equals(descricao))
			return "Guia de execução";
		if("MANDADO_SEGURANÇA".equals(descricao))
			return "Mandado de segurança";
		if("OFICIO".equals(descricao))
			return "Ofício";
		if("ACAO_PENAL".equals(descricao))
			return "Ação penal";
		return descricao;
	}

	public List<Regime> buscarRegimes() {
		StringBuilder jpql = new StringBuilder();
		jpql.append("select r from Regime r ");
		TypedQuery<Regime> query = em.createQuery(jpql.toString(), Regime.class);
		return query.getResultList();
	}
}
