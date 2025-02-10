package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.List;

import br.gov.mt.sejudh.sigepen.model.*;
import org.hibernate.NullPrecedence;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.enumerator.Periculosidade;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import br.gov.mt.sejudh.sigepen.util.criterion.ExpressaoFonetica.MatchModeFonetica;
import br.gov.mt.sejudh.sigepen.util.criterion.Restricao;
import org.hibernate.type.StandardBasicTypes;

public class FiltroRecuperandoView extends FiltroConsulta {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Long pessoaId;
	private String nome;
	private String nomeMae;
	private UnidadePrisional unidade;
	private StatusRecuperando status;
	private Periculosidade periculosidade;
	private Regime regime;
	private Condicao condicaoAtual;
	private Modalidade modalidade;
	private String alcunha;
	private List<Long> pessoasIds;
	private String outrosNomes;
	private String outraMae;
	private String siglaUnidade;

	private Boolean isCadastroCompleto;

	private Boolean listarTodasUnidades = false;

	private Boolean isBuscarFotoFrente = false;
	private Boolean isTrazerEstruturaNome = false;

	private Boolean isPesquisaFonetica = false;

	private Boolean isDietaEspecial = false;

	private List<Long> recuperandoIds;

	private Estrutura estrutura;

	@Override
	public void configurarFiltros() {
		if (this.getId() != null) {
			getCriteria().add(Restrictions.eq("id", this.getId()));
		}

		if (this.getIsPesquisaFonetica()) {
			if (this.getNome() != null && !this.getNome().equalsIgnoreCase("")) {
				getCriteria().add(Restricao.fonetiza("nome", this.getNome(), MatchModeFonetica.ANYWHERE));
			}

			if (this.getNomeMae() != null && !this.getNomeMae().equalsIgnoreCase("")) {
				getCriteria().add(Restricao.fonetiza("nomeMae", this.getNome(), MatchModeFonetica.ANYWHERE));
			}
		} else {
			if (this.getNome() != null && !this.getNome().equalsIgnoreCase("")) {
				getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
			}

			if (this.getNomeMae() != null && !this.getNomeMae().equalsIgnoreCase("")) {
				getCriteria()
						.add(RestrictionsSigepen.ignoraAcentoLike("nomeMae", this.getNomeMae(), MatchMode.ANYWHERE));
			}
		}

		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus().getId().toString()));
		}

		if (this.getCondicaoAtual() != null) {
			getCriteria().add(Restrictions.eq("condicaoAtual", this.getCondicaoAtual().toString()));
		}

		if (this.getPericulosidade() != null) {
			getCriteria().add(Restrictions.eq("periculosidade", this.getPericulosidade().toString()));
		}

		if (this.getRegime() != null) {
			getCriteria().add(Restrictions.eq("regimeId", this.getRegime().getId()));
		}

		if (this.getPessoasIds() != null && this.getPessoasIds().size() > 0) {
			getCriteria().add(Restrictions.in("pessoaId", this.getPessoasIds()));
		}

		if (this.getUnidade() != null && !this.getListarTodasUnidades() && this.getEstrutura() == null) {
			getCriteria().add(Restrictions.eq("unidadeId", this.getUnidade().getId()));
		}


		if (this.getEstrutura() != null) {
			DetachedCriteria critEstrutura = DetachedCriteria.forClass(Estrutura.class, "e")
					.add(Restrictions.sqlRestriction(
							"ID IN (SELECT ID, NOME, SITUACAO " +
									"FROM SIGEPEN_OWNER.ESTRUTURA e" +
									"WHERE e.SITUACAO = 1" +
									"START WITH e.ID = ? " +
									"CONNECT BY PRIOR e.ID = e.ESTRUTURAPAI_ID)",
							this.getEstrutura().getId(),
							StandardBasicTypes.LONG
					))
					.setProjection(Projections.property("e.id"));

			getCriteria().add(Subqueries.propertyIn("estruturaId", critEstrutura));
		}



		if (this.getAlcunha() != null && !this.getAlcunha().equalsIgnoreCase("")) {
			DetachedCriteria critAlcunha = DetachedCriteria.forClass(Alcunha.class, "a")
					.add(Restrictions.ilike("a.nome", this.getAlcunha(), MatchMode.ANYWHERE))
					.setProjection(Projections.property("a.recuperando.id"));

			getCriteria().add(Subqueries.propertyIn("id", critAlcunha));
		}

		boolean filtroTemOutroNome = this.getOutrosNomes() != null && !this.getOutrosNomes().equalsIgnoreCase("");
		boolean filtroTemOutraMae = this.getOutraMae() != null && !this.getOutraMae().equalsIgnoreCase("");

		if (filtroTemOutroNome || filtroTemOutraMae) {
			DetachedCriteria critOutrosNomes = DetachedCriteria.forClass(OutroNome.class, "o");

			if (filtroTemOutroNome) {
				critOutrosNomes
						.add(RestrictionsSigepen.ignoraAcentoLike("o.nome", this.getOutrosNomes(), MatchMode.ANYWHERE));
			}

			if (filtroTemOutraMae) {
				critOutrosNomes
						.add(RestrictionsSigepen.ignoraAcentoLike("o.nomeMae", this.getOutraMae(), MatchMode.ANYWHERE));
			}

			critOutrosNomes.setProjection(Projections.property("o.recuperando.id"));
			getCriteria().add(Subqueries.propertyIn("id", critOutrosNomes));
		}

		if (this.getIsCadastroCompleto() != null) {
			DetachedCriteria critCadastro = DetachedCriteria.forClass(SituacaoCadastroRecuperando.class, "s")
					.add(Restrictions.eq("s.cadastroCompleto", this.getIsCadastroCompleto()))
					.setProjection(Projections.property("s.id"));

			getCriteria().add(Subqueries.propertyIn("pessoaId", critCadastro));
		}

		if (this.getListarTodasUnidades() && !this.getIsDietaEspecial()) {
			DetachedCriteria critUnidade = DetachedCriteria.forClass(UnidadePrisional.class, "u")
					.createAlias("u.parametrosUnidade", "p", JoinType.INNER_JOIN)
					.add(Restrictions.eq("p.validar", SimNao.S)).setProjection(Projections.property("u.id"));

			getCriteria().add(Subqueries.propertyIn("unidadeId", critUnidade));
		}
		
		if (this.getRecuperandoIds() != null && this.getRecuperandoIds().size() > 0) {
			getCriteria().add(Restrictions.in("id", this.getRecuperandoIds()));
		}

		if (this.getModalidade() != null) {
			DetachedCriteria critModalidade = DetachedCriteria.forClass(ProcessoRegimeCondicao.class, "prc")
					.add(Restrictions.eq("prc.modalidade", this.getModalidade()))
					.createAlias("prc.processo", "proc", JoinType.INNER_JOIN)
					.setProjection(Projections.projectionList().add(Projections.groupProperty("proc.recuperando.id")));

			critModalidade.add(Restrictions.eq("proc.modalidade", this.getModalidade()));

			if (this.getModalidade() == Modalidade.TEMPORARIA) {
				critModalidade.add(Restrictions.ne("prc.regime.id", 4L));
			}

			getCriteria().add(Subqueries.propertyIn("id", critModalidade));
		}

		if (this.getIsTrazerEstruturaNome()) {
			getCriteria().createAlias("estrutura", "e", JoinType.INNER_JOIN)
					.add(Restrictions.eqProperty("e.id", "estruturaId"));
		}
	}

	@Override
	public void configurarOrdenacao() {
		super.configurarOrdenacao();

		if (this.getOrdenacao() != null) {
			getCriteria()
					.addOrder((this.getOrdenacao().isAscending() ? Order.asc("pessoaId") : Order.desc("pessoaId")));

			if (this.getPropriedadeOrdenacao().equals("id")) {
				if (this.getOrdenacao().isAscending())
					this.getOrdenacao().nulls(NullPrecedence.FIRST);
				else
					this.getOrdenacao().nulls(NullPrecedence.LAST);
			}
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPessoaId() {
		return pessoaId;
	}

	public void setPessoaId(Long pessoaId) {
		this.pessoaId = pessoaId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = Sigepen.formatarEspacos(nomeMae);
	}

	public Boolean getIsPesquisaFonetica() {
		return isPesquisaFonetica;
	}

	public void setIsPesquisaFonetica(Boolean isPesquisaFonetica) {
		this.isPesquisaFonetica = isPesquisaFonetica;
	}

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public StatusRecuperando getStatus() {
		return status;
	}

	public void setStatus(StatusRecuperando status) {
		this.status = status;
	}

	public Periculosidade getPericulosidade() {
		return periculosidade;
	}

	public void setPericulosidade(Periculosidade periculosidade) {
		this.periculosidade = periculosidade;
	}

	public Regime getRegime() {
		return regime;
	}

	public void setRegime(Regime regime) {
		this.regime = regime;
	}

	public Condicao getCondicaoAtual() {
		return condicaoAtual;
	}

	public void setCondicaoAtual(Condicao condicaoAtual) {
		this.condicaoAtual = condicaoAtual;
	}

	public Modalidade getModalidade() {
		return modalidade;
	}

	public void setModalidade(Modalidade modalidade) {
		this.modalidade = modalidade;
	}

	public String getAlcunha() {
		return alcunha;
	}

	public void setAlcunha(String alcunha) {
		this.alcunha = Sigepen.formatarEspacos(alcunha);
	}

	public List<Long> getPessoasIds() {
		return pessoasIds;
	}

	public void setPessoasIds(List<Long> pessoasIds) {
		this.pessoasIds = pessoasIds;
	}

	public String getOutrosNomes() {
		return outrosNomes;
	}

	public void setOutrosNomes(String outrosNomes) {
		this.outrosNomes = Sigepen.formatarEspacos(outrosNomes);
	}

	public String getOutraMae() {
		return outraMae;
	}

	public void setOutraMae(String outraMae) {
		this.outraMae = outraMae;
	}

	public String getSiglaUnidade() {
		return siglaUnidade;
	}

	public void setSiglaUnidade(String siglaUnidade) {
		this.siglaUnidade = siglaUnidade;
	}

	public Boolean getIsCadastroCompleto() {
		return isCadastroCompleto;
	}

	public void setIsCadastroCompleto(Boolean isCadastroCompleto) {
		this.isCadastroCompleto = isCadastroCompleto;
	}

	public Boolean getListarTodasUnidades() {
		return listarTodasUnidades;
	}

	public void setListarTodasUnidades(Boolean listarTodasUnidades) {
		this.listarTodasUnidades = listarTodasUnidades;
	}

	public Boolean getIsBuscarFotoFrente() {
		return isBuscarFotoFrente;
	}

	public void setIsBuscarFotoFrente(Boolean isBuscarFotoFrente) {
		this.isBuscarFotoFrente = isBuscarFotoFrente;
	}

	public Boolean getIsTrazerEstruturaNome() {
		return isTrazerEstruturaNome;
	}

	public void setIsTrazerEstruturaNome(Boolean isTrazerEstruturaNome) {
		this.isTrazerEstruturaNome = isTrazerEstruturaNome;
	}

	public List<Long> getRecuperandoIds() {
		return recuperandoIds;
	}

	public Boolean getIsDietaEspecial() {
		return isDietaEspecial;
	}

	public void setRecuperandoIds(List<Long> recuperandoIds) {
		this.recuperandoIds = recuperandoIds;
	}

	public void setIsDietaEspecial(Boolean isDietaEspecial) {
		this.isDietaEspecial = isDietaEspecial;
	}

	public Estrutura getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(Estrutura estrutura) {
		this.estrutura = estrutura;
	}
}
