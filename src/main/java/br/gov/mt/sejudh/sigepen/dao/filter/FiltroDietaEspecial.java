package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.primefaces.model.SortOrder;

import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.StatusFiltroDietaEspecial;
import java.util.Date;
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
	private StatusFiltroDietaEspecial statusDieta;

	public FiltroDietaEspecial() {
		this.statusDieta = StatusFiltroDietaEspecial.ATIVO;
	}

	public FiltroDietaEspecial copiarParaRelatorio(boolean listarTodasUnidades) {
		FiltroDietaEspecial copia = new FiltroDietaEspecial();
		copia.setUnidadePrisional(this.unidadePrisional);
		copia.setTipoDieta(this.tipoDieta);
		copia.setRecuperando(this.recuperando);
		copia.setStatusDieta(this.statusDieta);
		copia.setListarTodasUnidades(listarTodasUnidades);
		copia.setPaginacaoIndex(this.getPaginacaoIndex());
		if (this.getPropriedadeOrdenacao() != null) {
			SortOrder ordenacao = SortOrder.DESCENDING.equals(this.getSortOrder())
					? SortOrder.DESCENDING
					: SortOrder.ASCENDING;
			copia.organizaFiltro(this.getPaginacaoIndex(), Integer.MAX_VALUE, this.getPropriedadeOrdenacao(), ordenacao);
		}
		return copia;
	}
	

	@Override
	public void configurarFiltros() {
		getCriteria().createAlias("recuperando", "recuperando_", JoinType.INNER_JOIN);
		getCriteria().createAlias("recuperando_.pessoa", "pessoa", JoinType.INNER_JOIN);
		getCriteria().createAlias("tipoDieta", "tipoDieta", JoinType.INNER_JOIN);
		
		getCriteria().createAlias("recuperando_.passagens", "passagem_", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("passagem_.lotacao", "lotacao", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("lotacao.estrutura", "estrutura", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("estrutura.unidadePrisional", "unidade", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("recuperando_.fotos", "foto_", JoinType.LEFT_OUTER_JOIN);

		Projection proj = Projections.distinct(Projections.projectionList()
				.add(Projections.property("id").as("id"))
				.add(Projections.property("inicio").as("inicio"))
				.add(Projections.property("fim").as("fim"))
				.add(Projections.property("status").as("status"))
				.add(Projections.property("tipoDieta.descricao").as("tipoDieta"))
				.add(Projections.property("pessoa.nome").as("nomeRecuperando"))
						.add(Projections.property("pessoa.nomeSocial").as("nomeSocialRecuperando"))
				.add(Projections.property("unidade.nome").as("nomeUnidadePrisional"))
				.add(Projections.property("passagem_.dataEntrada").as("dataEntrada"))
				);

		getCriteria().setProjection(proj);
		
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
		
		if (StatusFiltroDietaEspecial.ATIVO.equals(this.statusDieta)) {
			getCriteria().add(Restrictions.eq("status", Status.ATIVO));
			getCriteria().add(Restrictions.or(
					Restrictions.isNull("fim"),
					Restrictions.gt("fim", new Date())
			));
		} else if (StatusFiltroDietaEspecial.SUSPENSO.equals(this.statusDieta)) {
			getCriteria().add(Restrictions.eq("status", Status.SUSPENSO));
			getCriteria().add(Restrictions.or(
					Restrictions.isNull("fim"),
					Restrictions.gt("fim", new Date())
			));
		} else if (StatusFiltroDietaEspecial.ENCERRADO.equals(this.statusDieta)) {
			getCriteria().add(Restrictions.or(
					Restrictions.eq("status", Status.INATIVO),
					Restrictions.and(
							Restrictions.isNotNull("fim"),
							Restrictions.le("fim", new Date()))));
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
	
	public StatusFiltroDietaEspecial getStatusDieta() {
	    return statusDieta;
	}

	public void setStatusDieta(StatusFiltroDietaEspecial statusDieta) {
	    this.statusDieta = statusDieta;
	}


}
