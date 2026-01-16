package br.gov.mt.sejudh.sigepen.dao.filter;
import javax.persistence.EntityManager;
import org.hibernate.Criteria;
import br.gov.mt.sejudh.sigepen.enumerator.DiasSemana;
import br.gov.mt.sejudh.sigepen.model.TipoDieta;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.primefaces.model.SortOrder;

import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.auxiliar.ObjetoSelectOne;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class FiltroCardapio extends FiltroConsulta {

	private static final long serialVersionUID = -918407996185341526L;
	private long id;
	private Contrato contrato;
	private UnidadePrisional unidadePrisional;
	private TipoRefeicao tipoRefeicao;
	private TipoDieta tipoDieta;
	private DiasSemana diaSemana;
	private Boolean festivo;
	private ObjetoSelectOne semana;
	private boolean listarTodasUnidades;

	private boolean paraContagem;

	@Override
	public <T> Criteria criarCriteriaParaQuantidade(Class<T> classe, EntityManager em) {
		this.paraContagem = true;
		return super.criarCriteriaParaQuantidade(classe, em);
	}

	@Override
	public <T> Criteria criarCriteriaParaBusca(Class<T> classe, EntityManager em) {
		this.paraContagem = false;
		return super.criarCriteriaParaBusca(classe, em);
	}

	@Override
	public <T> Criteria criarCriteriaParaBusca(Class<T> classe, EntityManager em, boolean isBuscaRelatorio) {
		this.paraContagem = false;
		return super.criarCriteriaParaBusca(classe, em, isBuscaRelatorio);
	}

	@Override
	public void configurarFiltros() {
		getCriteria().createAlias("tipoRefeicao", "tipoRefeicao", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("contrato", "contrato", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("contrato.unidades", "unidade", JoinType.LEFT_OUTER_JOIN);
		getCriteria().createAlias("tipoDieta", "tipoDieta", JoinType.LEFT_OUTER_JOIN);

		if (this.isListarTodasUnidades()) {
			if (this.getUnidadePrisional() != null) {
				getCriteria().add(Restrictions.eq("unidade.id", this.getUnidadePrisional().getId()));
			}
		} else {
			getCriteria().add(Restrictions.eq("unidade", SecurityUtil.getUnidadeSelecionada()));
		}

		if (!paraContagem) {
			if ("diaSemana".equals(getPropriedadeOrdenacao())) {
				if (getSortOrder() != null) {
					if (SortOrder.ASCENDING.equals(getSortOrder())) {
						setOrdenacao(Order.asc("diaSemana"));
					} else {
						setOrdenacao(Order.desc("diaSemana"));
					}
				}
			} else if ("diaSemana.descricao".equals(getPropriedadeOrdenacao())) {
				if (getSortOrder() != null) {
					if (SortOrder.ASCENDING.equals(getSortOrder())) {
						setOrdenacao(Order.asc("diaSemana"));
					} else {
						setOrdenacao(Order.desc("diaSemana"));
					}
				}
			}

			Projection proj = Projections.projectionList()
					.add(Projections.property("id").as("id"))
					.add(Projections.property("semana").as("semana"))
					.add(Projections.property("diaSemana").as("diaSemana"))
					.add(Projections.property("tipoRefeicao.descricao").as("nomeRefeicao"))
					.add(Projections.property("tipoDieta.descricao").as("nomeDieta"))
					.add(Projections.property("contrato.numContrato").as("numContrato"))
					.add(Projections.property("unidade.nome").as("nomeUnidadePrisional"))
					.add(Projections.property("festivo").as("festivo"));

			getCriteria().setProjection(proj);
		}

		if (this.getContrato() != null) {
			getCriteria().add(Restrictions.eq("contrato.id", this.getContrato().getId()));
		}

		if (this.getTipoRefeicao() != null) {
			getCriteria().add(Restrictions.eq("tipoRefeicao.id", this.getTipoRefeicao().getId()));
		}

		if (this.getTipoDieta() != null) {
			getCriteria().add(Restrictions.eq("tipoDieta.id", this.getTipoDieta().getId()));
		}

		if (this.getFestivo() != null) {
			getCriteria().add(Restrictions.eq("festivo", this.getFestivo()));
		}

		if (this.getSemana() != null) {
			getCriteria().add(Restrictions.eq("semana", this.getSemana().getId()));
		}

		if (this.getDiaSemana() != null) {
			getCriteria().add(Restrictions.eq("diaSemana", this.getDiaSemana()));
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Boolean getFestivo() {
		return festivo;
	}

	public void setFestivo(Boolean festivo) {
		this.festivo = festivo;
	}

	public ObjetoSelectOne getSemana() {
		return semana;
	}

	public void setSemana(ObjetoSelectOne semana) {
		this.semana = semana;
	}

	public TipoRefeicao getTipoRefeicao() {
		return tipoRefeicao;
	}

	public void setTipoRefeicao(TipoRefeicao tipoRefeicao) {
		this.tipoRefeicao = tipoRefeicao;
	}

	public boolean isListarTodasUnidades() {
		return listarTodasUnidades;
	}

	public void setListarTodasUnidades(boolean listarTodasUnidades) {
		this.listarTodasUnidades = listarTodasUnidades;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
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

	public DiasSemana getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DiasSemana diaSemana) {
		this.diaSemana = diaSemana;
	}
}
