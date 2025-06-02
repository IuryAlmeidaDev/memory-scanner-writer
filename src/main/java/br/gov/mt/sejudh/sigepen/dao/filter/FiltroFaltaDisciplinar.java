package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.SituacaoFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.TipoFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroFaltaDisciplinar extends FiltroConsulta {

	private static final long serialVersionUID = 1L;

	private Long matricula;

	private Date dataInicio;

	private Date dataFim;

	private String nome;

	public UnidadePrisional unidadePrisional;

	private TipoFaltaDisciplinar tipo;

	private Status status;

	private SituacaoFaltaDisciplinar situacao;
	
	private String nomeSocial;

	public FiltroFaltaDisciplinar() {
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public TipoFaltaDisciplinar getTipo() {
		return tipo;
	}

	public void setTipo(TipoFaltaDisciplinar tipo) {
		this.tipo = tipo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public SituacaoFaltaDisciplinar getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoFaltaDisciplinar situacao) {
		this.situacao = situacao;
	}
	
	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	@Override
	public void configurarFiltros() {
		
		getCriteria()
		.createAlias("recuperando", "recuperando_", JoinType.INNER_JOIN)
		.createAlias("recuperando_.pessoa", "pessoa", JoinType.INNER_JOIN)
		.createAlias("tipo", "tipo", JoinType.INNER_JOIN)
		.createAlias("estruturaOcorrencia", "estrutura", JoinType.LEFT_OUTER_JOIN)
		.createAlias("estrutura.unidadePrisional", "unidadePrisional", JoinType.LEFT_OUTER_JOIN);

		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("id").as("id"))
				.add(Projections.property("recuperando_.id").as("matricula"))
				.add(Projections.property("pessoa.nome").as("recuperando"))
				.add(Projections.property("tipo.descricao").as("tipoFalta"))
				.add(Projections.property("data").as("data"))
	            .add(Projections.property("situacao").as("situacao"))
	            .add(Projections.property("pessoa.nomeSocial").as("nomeSocial"));


		
		getCriteria().setProjection(projectionList);

		if(this.getMatricula()!= null){
			getCriteria().add(Restrictions.eq("recuperando.id", this.getMatricula()));
		}

		if (this.getNome()!= null && !this.getNome().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nome", this.getNome(), MatchMode.ANYWHERE));
		}

		if (this.getTipo() !=null) {
			getCriteria().add(Restrictions.eq("tipo", this.getTipo()));
		}

		if (this.getUnidadePrisional() != null) {
			getCriteria().add(Restrictions.eq("unidadePrisional.id", this.getUnidadePrisional().getId()));
		}

		if (this.getDataInicio() != null) {
			getCriteria().add(Restrictions.ge("data", Sigepen.getDataConsultaMaiorIgual(this.getDataInicio())));
		}

		if(this.getDataFim()!= null){
			getCriteria().add(Restrictions.le("data", Sigepen.getDataConsultaMenorIgual(this.getDataFim())));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}

		if (this.getSituacao() != null) {
			getCriteria().add(Restrictions.eq("situacao", this.getSituacao()));
		}
		
		if(this.getNomeSocial() !=null && !this.getNomeSocial().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nomeSocial", this.getNomeSocial(), MatchMode.ANYWHERE));
		}

	}
}
