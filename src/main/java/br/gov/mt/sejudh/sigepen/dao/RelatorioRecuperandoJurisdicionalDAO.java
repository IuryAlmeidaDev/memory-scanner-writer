package br.gov.mt.sejudh.sigepen.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.QueryHints;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioRecuperandoJurisdicional;
import br.gov.mt.sejudh.sigepen.enumerator.CondicaoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.Esfera;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamentoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntradaRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoRelatorioEntradaSaidaRec;
import br.gov.mt.sejudh.sigepen.enumerator.UfRelatorio;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioRecuperandoJurisdicionalDTO;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class RelatorioRecuperandoJurisdicionalDAO {
	
	@Inject
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<RelatorioRecuperandoJurisdicionalDTO> buscarRelatorioNovo(FiltroRelatorioRecuperandoJurisdicional filtro) {
		Session session = em.unwrap(Session.class);
		Criteria criteria = session.createCriteria(Recuperando.class);
		criteria.addQueryHint(QueryHints.READ_ONLY);
		
		criteria.createAlias("pessoa", "pessoa", JoinType.INNER_JOIN)
			.createAlias("passagens", "passagem", JoinType.INNER_JOIN)
			.createAlias("passagem.passagemProcedencia", "procedencia", JoinType.INNER_JOIN)
			.createAlias("procedencia.procedencia", "estabProc", JoinType.INNER_JOIN)
			.createAlias("processos", "processo", JoinType.INNER_JOIN)
			.createAlias("processo.processosRegimeCondicao", "prc", JoinType.INNER_JOIN)
			.createAlias("prc.estabelecimento", "estab", JoinType.INNER_JOIN)
			.createAlias("estab.cidade", "cidade", JoinType.INNER_JOIN)
			.createAlias("cidade.estado", "estado", JoinType.INNER_JOIN)
			.createAlias("regimeAtual", "regimeAtual", JoinType.LEFT_OUTER_JOIN);
		
		Projection proj = Projections.projectionList()
				.add(Projections.property("id").as("matricula"))
				.add(Projections.property("pessoa.nome").as("nome"))
				.add(Projections.property("pessoa.nomeSocial").as("nomeSocial"))
				.add(Projections.property("pessoa.nomeMae").as("nomeMae"))
				//.add(Projections.property("regimeAtual").as("regimeAtual"))
				.add(Projections.property("regimeAtual.descricao").as("regimeAtualDescricao"))

				.add(Projections.property("condicaoAtual").as("condicaoAtual"))
				
				.add(Projections.property("passagem.id").as("idPassagem"))
				.add(Projections.property("estabProc.nome").as("passagemProcedencia"))
				.add(Projections.property("passagem.dataEntrada").as("passagemDataEntrada"))
				.add(Projections.property("passagem.dataSaida").as("passagemDataSaida"))
				
				.add(Projections.property("processo.id").as("idProcesso"))
				.add(Projections.property("processo.numeroProcesso").as("processoNumero"))
				.add(Projections.property("processo.numeroOficio").as("processoOficio"))
				.add(Projections.property("processo.numeroInquerito").as("processoInquerito"))
				
				.add(Projections.property("prc.id").as("idDocumento"))
				.add(Projections.property("prc.numeroDocumento").as("documentoNumero"))
				.add(Projections.property("prc.tipoProcesso").as("documentoTipo"))
				.add(Projections.property("prc.dataDocumento").as("documentoData"))
				.add(Projections.property("estab.nome").as("documentoSecao"))
				.add(Projections.property("prc.observacao").as("documentoObservacao"))
				.add(Projections.property("prc.regime").as("documentoRegime"))
				.add(Projections.property("prc.condicao").as("documentoCondicao"))
				.add(Projections.property("prc.modalidade").as("documentoModalidade"))
				.add(Projections.property("prc.dataSoltura").as("documentoDataSoltura"))
				;
		
		criteria.setProjection(proj);
		
		if (filtro.getTipoRelatorioEntradaSaidaRec() != null) {
			switch (filtro.getTipoRelatorioEntradaSaidaRec()) {
				case ENTRADA:
					criteria.add(Restrictions.between("passagem.dataEntrada", filtro.getDataInicial(), filtro.getDataFinal()));
					break;
					
				case SAIDA:
					criteria.add(Restrictions.between("passagem.dataSaida", filtro.getDataInicial(), filtro.getDataFinal()));
					break;
	
				case ENTRADA_SAIDA:
					criteria.add(Restrictions.or(Restrictions.between("passagem.dataEntrada", filtro.getDataInicial(), filtro.getDataFinal()),
							Restrictions.between("passagem.dataSaida", filtro.getDataInicial(), filtro.getDataFinal())));
					break;
			}
		}
		
		if(filtro.getTipoProcesso() != null && !TipoEntradaRelatorio.TODAS.equals(filtro.getTipoProcesso())) {
			criteria.add(Restrictions.eq("prc.tipoProcesso", filtro.getTipoProcesso()));
		}
		
		if(filtro.getTipoDesligamento() != null && !TipoDesligamentoRelatorio.TODOS.equals(filtro.getTipoDesligamento())) {
			criteria.add(Restrictions.eq("passagem.tipoDesligamento", filtro.getTipoDesligamento()));
		}
		
		if(filtro.getNumeroProcesso() != null && !"".equals(filtro.getNumeroProcesso())) {
			criteria.add(Restrictions.eq("processo.numeroProcesso", filtro.getNumeroProcesso()));
		}
		
		if(filtro.getNumeroDocumento() != null && StringUtils.isNotBlank(filtro.getNumeroDocumento())) {
			criteria.add(Restrictions.eq("prc.numeroDocumento", filtro.getNumeroDocumento()));
		}
		
		if(filtro.getEstabelecimento() != null) {
			criteria.add(Restrictions.eq("prc.estabelecimento", filtro.getEstabelecimento()));
		}
		
		if(filtro.getUnidade() > 0) {
			DetachedCriteria criteriaUnidadeRecuperando =  DetachedCriteria.forClass(RecuperandoView.class, "r")
					.add(Restrictions.eq("r.unidadeId", filtro.getUnidade()))
					.setProjection(Projections.property("r.id"));
			
			criteria.add(Subqueries.propertyIn("id", criteriaUnidadeRecuperando));
		}
		
		if(filtro.getRegime() != null) {
			criteria.add(Restrictions.eq("regimeAtual", filtro.getRegime()));
		}
		
		if(filtro.getCondicaoAtual() != null && !CondicaoRelatorio.TODAS.equals(filtro.getCondicaoAtual())) {
			criteria.add(Restrictions.eq("condicaoAtual", filtro.getCondicaoAtual()));
		}
		
		if(filtro.getEstadoEstabelecimento() != null && !UfRelatorio.TT.equals(filtro.getEstadoEstabelecimento())) {
			criteria.add(Restrictions.eq("estado.uf", filtro.getEstadoEstabelecimento().name()));
		}
		
		if(filtro.getEsferaEstabelecimento() != null && !Esfera.TODAS.equals(filtro.getEsferaEstabelecimento())) {
			criteria.add(Restrictions.eq("estab.esfera", filtro.getEsferaEstabelecimento()));
		}

		criteria.setResultTransformer(new AliasToBeanResultTransformer(RelatorioRecuperandoJurisdicionalDTO.class));
		
		return criteria.list();
	}
	
	public List<RelatorioRecuperandoJurisdicionalDTO> buscarRelatorio(FiltroRelatorioRecuperandoJurisdicional filtro) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dataInicial = sdf.format(filtro.getDataInicial());
		String dataFinal = sdf.format(filtro.getDataFinal());
		
		StringBuilder hql = new StringBuilder();

		hql.append(" select  ");
		hql.append(" r.matricula, ");
		hql.append(" pe.nome, ");
		hql.append(" pe.nome_mae, ");
		hql.append(" pe.data_nascimento, ");
		hql.append(" p.data_entrada, ");
		hql.append(" p.data_saida, ");
		hql.append(" prc.tipo_processo, ");
		hql.append(" p.tipo_desligamento, ");
		hql.append(" r.regime, ");
		hql.append(" r.condicao, ");
		hql.append(" r.status, ");
		hql.append(" est.nome nomeEstabelecimento, ");
		hql.append(" pro.numero_processo ");
		
		hql.append(" from " + Sigepen.SEJUDH_SCHEMA + ".passagem p, " + Sigepen.SEJUDH_SCHEMA + ".lista_todos_recuperandos r, " + Sigepen.SEJUDH_SCHEMA + ".pessoa pe, " + Sigepen.SEJUDH_SCHEMA + ".processo pro, " + Sigepen.SEJUDH_SCHEMA + ".processo_regime_condicao prc, " + Sigepen.SEJUDH_SCHEMA + ".estabelecimento est, ");
		hql.append(Sigepen.SEJUDH_SCHEMA + ".MUNICIPIO mun ");
		hql.append(" where ");
		if(TipoRelatorioEntradaSaidaRec.ENTRADA_SAIDA.equals(filtro.getTipoRelatorioEntradaSaidaRec())) {
			hql.append("  ((p.data_entrada >= to_date('" + dataInicial + " 00:00:00','DD/MM/YYYY HH24:MI:SS') and p.data_entrada <= to_date('" + dataFinal + " 23:59:59','DD/MM/YYYY HH24:MI:SS')) or ");
			hql.append("  (p.data_saida >= to_date('" + dataInicial + " 00:00:00','DD/MM/YYYY HH24:MI:SS') and p.data_saida <= to_date('" + dataFinal + " 23:59:59','DD/MM/YYYY HH24:MI:SS'))) ");
		}else if(TipoRelatorioEntradaSaidaRec.ENTRADA.equals(filtro.getTipoRelatorioEntradaSaidaRec())) {
			hql.append("  p.data_entrada >= to_date('" + dataInicial + " 00:00:00','DD/MM/YYYY HH24:MI:SS') and p.data_entrada <= to_date('" + dataFinal + " 23:59:59','DD/MM/YYYY HH24:MI:SS') ");
		}else {
			hql.append("  p.data_saida >= to_date('" + dataInicial + " 00:00:00','DD/MM/YYYY HH24:MI:SS') and p.data_saida <= to_date('" + dataFinal + " 23:59:59','DD/MM/YYYY HH24:MI:SS') ");
		}
		hql.append(" and p.recuperando_id = r.matricula ");
		hql.append(" and r.id_pessoa = pe.id ");
		hql.append(" and r.matricula = pro.recuperando_id ");
		hql.append(" and prc.processo_id = pro.id ");
		hql.append(" and est.id = prc.estabelecimento_id ");
		hql.append(" and mun.id = est.cidade_id");
		
		if(filtro.getTipoProcesso() != null && !TipoEntradaRelatorio.TODAS.equals(filtro.getTipoProcesso())) {
			hql.append(" and prc.tipo_processo = '" + filtro.getTipoProcesso().name() + "'");
		}
		
		if(filtro.getTipoDesligamento() != null && !TipoDesligamentoRelatorio.TODOS.equals(filtro.getTipoDesligamento())) {
			hql.append(" and r.tipo_desligamento = '" + filtro.getTipoDesligamento().name() + "'");
		}
		
		if(filtro.getNumeroProcesso() != null && !"".equals(filtro.getNumeroProcesso())) {
			hql.append(" and pro.numero_processo = '" + filtro.getNumeroProcesso() + "'");
		}
		
		if(filtro.getEstabelecimento() != null) {
			hql.append(" and est.id = " + filtro.getEstabelecimento().getId());
		}
		
		if(filtro.getUnidade() > 0) {
			hql.append(" and r.id_unidade = " + filtro.getUnidade());
		}
		
		if(filtro.getRegime() != null) {
			hql.append(" and r.regime = '" + filtro.getRegime().getDescricao() + "' ");
		}
		
		if(filtro.getCondicaoAtual() != null && !CondicaoRelatorio.TODAS.equals(filtro.getCondicaoAtual())) {
			hql.append(" and r.condicao = '" + filtro.getCondicaoAtual().name() + "'");
		}
		
		if(filtro.getEstadoEstabelecimento() != null && !UfRelatorio.TT.equals(filtro.getEstadoEstabelecimento())) {
			hql.append(" and mun.estado = '" + filtro.getEstadoEstabelecimento() + "'");
		}
		
		if(filtro.getEsferaEstabelecimento() != null && !Esfera.TODAS.equals(filtro.getEsferaEstabelecimento())) {
			hql.append(" and est.esfera = '" + filtro.getEsferaEstabelecimento().name() + "'");
		}
		
		Query query = this.em.createNativeQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		
		List<RelatorioRecuperandoJurisdicionalDTO> retorno = new ArrayList<RelatorioRecuperandoJurisdicionalDTO>();
		
		results.stream().forEach((record) -> {
	        Long matricula = ((BigDecimal) record[0]).longValue();
	        String nome = (String) record[1];
	        Date dtEntrada = (Date) record[4];
	        Date dtSaida = (Date) record[5];
	        String situacao = (String) record[10];
	        String nomeEstabelecimento = (String) record[11];
	        String numeroProcesso = (String) record[12];

	       /* RelatorioRecuperandoJurisdicionalDTO r = new RelatorioRecuperandoJurisdicionalDTO(matricula, nome, dtEntrada, dtSaida, situacao, numeroProcesso, nomeEstabelecimento, filtro);
	        retorno.add(r);*/
		});
		
		return retorno;
	}

	
}
