package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Funcao;
import br.gov.mt.sejudh.sigepen.model.Lotacao;
import br.gov.mt.sejudh.sigepen.model.Orgao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroServidor extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private String nome;

	private String mae;
	
	private String cpf;
	
	private Status status;
	
	private Long matricula;
	
	private Funcao funcao;
	
	private Boolean escolta;
	
	private Boolean lotacaoObrigatoria;
	
	private Orgao orgao;
	
	private UnidadePrisional unidadeLotacao;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}
	
	public String getMae() {
		return mae;
	}

	public void setMae(String mae) {
		this.mae = Sigepen.formatarEspacos(mae);
	}
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}	

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public Funcao getFuncao() {
		return funcao;
	}

	public void setFuncao(Funcao funcao) {
		this.funcao = funcao;
	}

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}	

	public UnidadePrisional getUnidadeLotacao() {
		return unidadeLotacao;
	}

	public void setUnidadeLotacao(UnidadePrisional unidadeLotacao) {
		this.unidadeLotacao = unidadeLotacao;
	}

	public Boolean getEscolta() {
		return escolta;
	}

	public void setEscolta(Boolean escolta) {
		this.escolta = escolta;
	}
	
	public Boolean getLotacaoObrigatoria() {
		return lotacaoObrigatoria;
	}

	public void setLotacaoObrigatoria(Boolean lotacaoObrigatoria) {
		this.lotacaoObrigatoria = lotacaoObrigatoria;
	}

	@Override
	public void configurarFiltros() {	
		getCriteria()
		.createAlias("orgao", "orgao", JoinType.LEFT_OUTER_JOIN)
		.createAlias("cargo", "cargo", JoinType.LEFT_OUTER_JOIN)
		.createAlias("funcao", "funcao", JoinType.LEFT_OUTER_JOIN)
		.createAlias("tipoServidor", "tipoServidor", JoinType.LEFT_OUTER_JOIN)
		.createAlias("lotacao", "lotacao", JoinType.LEFT_OUTER_JOIN);
		
		Projection proj = Projections.projectionList()
				.add(Projections.property("id").as("id"))
				.add(Projections.property("nome").as("nome"))
				.add(Projections.property("matricula").as("matricula"))
				.add(Projections.property("orgao.nome").as("orgao"))
				.add(Projections.property("cargo.nome").as("cargo"))
				.add(Projections.property("tipoServidor.descricao").as("tipo"))
				.add(Projections.property("status").as("status"));
		
		getCriteria().setProjection(proj);

		DetachedCriteria subCrit = DetachedCriteria.forClass(Lotacao.class, "lot")
				.add(Restrictions.eqProperty("lot.servidor.id", "lotacao.servidor.id"))
				.setProjection(Projections.max("lot.dataFim"));
		
		DetachedCriteria subCrit2 = DetachedCriteria.forClass(Lotacao.class, "lot")
				.add(Restrictions.isNull("lot.dataFim"))
				.setProjection(Projections.property("lot.servidor.id"));
		
		getCriteria().add(Restrictions.or(
				Restrictions.isNull("lotacao.dataFim"),
				Restrictions.conjunction()
					.add(Subqueries.propertyEq("lotacao.dataFim", subCrit))
					.add(Subqueries.propertyNotIn("id", subCrit2))));
		
		if (this.getNome()!= null && !this.getNome().equalsIgnoreCase("") ){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
		
		if (this.getMae()!= null && !this.getMae().equalsIgnoreCase("") ){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("mae", this.getMae(), MatchMode.ANYWHERE));
		}
		
		if (this.getCpf()!= null && !this.getCpf().equalsIgnoreCase("") ){
			getCriteria().add(Restrictions.like("cpf", this.getCpf(), MatchMode.ANYWHERE));
		}
		
		if (this.getMatricula() != null){
			getCriteria().add(Restrictions.eq("matricula", this.getMatricula()));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
		if (this.getOrgao() != null) {
			getCriteria().add(Restrictions.eq("orgao", this.getOrgao()));
		}
		
		if (this.getUnidadeLotacao() != null) {
			getCriteria().add(Restrictions.or(
					Restrictions.eq("lotacao.unidade", this.getUnidadeLotacao()),
					Restrictions.eq("funcao.lotacaoObrigatoria", this.getLotacaoObrigatoria())
					));
		}
		
		if (this.getEscolta() != null) {
			getCriteria().add(Restrictions.eq("cargo.escolta", this.getEscolta()));
		}
	}
}
