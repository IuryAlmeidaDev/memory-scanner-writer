package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class FiltroContrato extends FiltroConsulta {

	private static final long serialVersionUID = 1L;
	private String numContrato;
	private String fornecedor;
	private String cnpj;
	private UnidadePrisional unidadePrisional;
	private Boolean listarTodasUnidades;
	private Status status;
	private String nome;

	@Override
	public void configurarFiltros() {
		getCriteria().createAlias("fornecedor", "fornecedor", JoinType.INNER_JOIN).createAlias("unidades", "unidade",
				JoinType.INNER_JOIN);

		Projection proj = Projections.distinct(Projections.projectionList().add(Projections.property("id").as("id"))
				.add(Projections.property("numContrato").as("numContrato"))
				.add(Projections.property("fornecedor.cnpj").as("cnpjFornecedor"))
				.add(Projections.property("fornecedor.nomeFantasia").as("nomeFornecedor"))
				.add(Projections.property("status").as("status"))
				.add(Projections.property("vigenciaContratoInicio").as("vigenciaContratoInicio"))
				.add(Projections.property("vigenciaContratoFim").as("vigenciaContratoFim")));
		getCriteria().setProjection(proj);

		if (this.getNumContrato() != null && !this.getNumContrato().isEmpty()) {
			getCriteria().add(
					RestrictionsSigepen.ignoraAcentoLike("numContrato", this.getNumContrato(), MatchMode.ANYWHERE));
		}

		if (this.getFornecedor() != null && !this.getFornecedor().isEmpty()) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("fornecedor.nomeFantasia", this.getFornecedor(),
					MatchMode.ANYWHERE));
		}

		if (this.getCnpj() != null && !this.getCnpj().isEmpty()) {
			getCriteria()
					.add(RestrictionsSigepen.ignoraAcentoLike("fornecedor.cnpj", this.getCnpj(), MatchMode.ANYWHERE));
		}

		if (this.getNome() != null && !this.getNome().equalsIgnoreCase("")) {
			getCriteria().add(Restrictions.ilike("fornecedor.nomeFantasia", this.getNome(), MatchMode.ANYWHERE));
		}

		if (!this.isListarTodasUnidades()) {
			getCriteria().add(Restrictions.eq("unidade.id", SecurityUtil.getUnidadeSelecionada().getId()));
		}else 
			if(this.getUnidadePrisional() != null) {
				getCriteria().add(Restrictions.eq("unidade.id", this.getUnidadePrisional().getId()));
			}

		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
	}

	public String getNumContrato() {
		return this.numContrato;
	}

	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isListarTodasUnidades() {
		return listarTodasUnidades;
	}

	public void setListarTodasUnidades(boolean listarTodasUnidades) {
		this.listarTodasUnidades = listarTodasUnidades;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

}
