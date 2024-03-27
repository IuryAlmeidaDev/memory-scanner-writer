package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class FiltroApostilamento extends FiltroConsulta {

	private static final long serialVersionUID = -800579742317793364L;
	private long id;
	private Contrato contrato;
	private String cnpj;
	private UnidadePrisional unidadePrisional;
	private boolean listarTodasUnidades;

	@Override
	public void configurarFiltros() {
		getCriteria().createAlias("contrato", "contrato", JoinType.INNER_JOIN);
		getCriteria().createAlias("contrato.unidades", "unidade", JoinType.INNER_JOIN);
		getCriteria().createAlias("contrato.fornecedor", "fornecedor", JoinType.INNER_JOIN);

		Projection proj = Projections.distinct(Projections.projectionList()
				.add(Projections.property("id").as("id"))
				.add(Projections.property("dataInicio").as("dataInicio"))
				.add(Projections.property("dataFim").as("dataFim"))
				.add(Projections.property("contrato.numContrato").as("numContrato")));
		getCriteria().setProjection(proj);
		
		if (this.getCnpj() != null && !this.getCnpj().isEmpty()) {
			getCriteria().add(Restrictions.ilike("fornecedor.cnpj", this.getCnpj()));
		}

		if (this.getContrato() != null) {
			getCriteria().add(Restrictions.eq("contrato.id", this.getContrato().getId()));
		}
		
		if (!this.isListarTodasUnidades()) {
			getCriteria().add(Restrictions.eq("unidade.id", SecurityUtil.getUnidadeSelecionada().getId()));
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public boolean isListarTodasUnidades() {
		return listarTodasUnidades;
	}

	public void setListarTodasUnidades(boolean listarTodasUnidades) {
		this.listarTodasUnidades = listarTodasUnidades;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}
}
