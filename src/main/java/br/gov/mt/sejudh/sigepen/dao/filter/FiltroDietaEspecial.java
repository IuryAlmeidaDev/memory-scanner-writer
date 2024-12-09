package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.TipoDieta;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class FiltroDietaEspecial extends FiltroConsulta {

	private static final long serialVersionUID = 1L;
	private UnidadePrisional unidadePrisional;
	private TipoDieta tipoDieta;
	private String recuperando;
	private Boolean listarTodasUnidades;
	

	@Override
	public void configurarFiltros() {
		getCriteria().createAlias("recuperando", "recuperando_", JoinType.INNER_JOIN);
		getCriteria().createAlias("recuperando_.pessoa", "pessoa", JoinType.INNER_JOIN);
		getCriteria().createAlias("tipoDieta", "tipoDieta", JoinType.INNER_JOIN);
		
		// Buscar unidade prisional do recuperando
		getCriteria().createAlias("recuperando_.passagens", "passagem_", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("passagem_.lotacao", "lotacao", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("lotacao.estrutura", "estrutura", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("estrutura.unidadePrisional", "unidade", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("recuperando_.fotos", "foto_", JoinType.LEFT_OUTER_JOIN);

		Projection proj = Projections.distinct(Projections.projectionList()
				.add(Projections.property("id").as("id"))
				.add(Projections.property("inicio").as("inicio"))
				.add(Projections.property("fim").as("fim"))
				.add(Projections.property("fim").as("fim"))
				.add(Projections.property("tipoDieta.descricao").as("tipoDieta"))
				.add(Projections.property("pessoa.nome").as("nomeRecuperando"))
				.add(Projections.property("unidade.nome").as("nomeUnidadePrisional"))
				.add(Projections.property("passagem_.dataEntrada").as("dataEntrada"))
				);

		getCriteria().setProjection(proj);
		
		//Subselect para trazer apenas a ultima passagem e ultima lotação
		DetachedCriteria critPassagem = DetachedCriteria.forClass(Passagem.class, "pass")
				.add(Restrictions.eqProperty("pass.recuperando.id", "recuperando_.id"))
				.setProjection(Projections.projectionList().add(Projections.max("pass.id")));
		
		DetachedCriteria critLotacao = DetachedCriteria.forClass(PassagemLotacao.class, "lot")
				.add(Restrictions.eqProperty("lot.passagem.id", "passagem_.id"))
				.setProjection(Projections.projectionList().add(Projections.max("lot.id")));
		
		getCriteria().add(
				Restrictions.or(
					Restrictions.conjunction()
						.add(Restrictions.isNotNull("recuperando_.id"))
						.add(Restrictions.and(Restrictions.eq("foto_.isAtual", true)))
						.add(Restrictions.and(Restrictions.eq("foto_.tipo", TipoFoto.FRENTE)))
						.add(Restrictions.or(
								Restrictions.isNull("passagem_.id"),
								Restrictions.conjunction()
									.add(Subqueries.propertyEq("passagem_.id", critPassagem))
									.add(Subqueries.propertyEq("lotacao.id", critLotacao)))),
					Restrictions.isNull("recuperando_.id")));

		if (this.getRecuperando() != null && !this.getRecuperando().isEmpty()) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nome", this.getRecuperando(), MatchMode.ANYWHERE));
		}
		
		if(this.getTipoDieta() != null) {
			getCriteria().add(Restrictions.eq("tipoDieta.id", this.getTipoDieta().getId()));
		}
		
		if (this.isListarTodasUnidades()) {
			if(this.getUnidadePrisional() != null) {
				getCriteria().add(Restrictions.eq("unidade.id", this.getUnidadePrisional().getId()));
			}
		} else {
			getCriteria().add(Restrictions.eq("unidade.id", SecurityUtil.getUnidadeSelecionada().getId()));
		}
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public TipoDieta getTipoDieta() {
		return tipoDieta;
	}

	public void setTipoDieta(TipoDieta tipoDieta) {
		this.tipoDieta = tipoDieta;
	}

	public String getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(String recuperando) {
		this.recuperando = recuperando;
	}

	public boolean isListarTodasUnidades() {
		return listarTodasUnidades;
	}

	public void setListarTodasUnidades(boolean listarTodasUnidades) {
		this.listarTodasUnidades = listarTodasUnidades;
	}

}
