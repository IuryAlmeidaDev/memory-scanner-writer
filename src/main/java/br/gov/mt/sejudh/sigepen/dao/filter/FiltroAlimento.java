package br.gov.mt.sejudh.sigepen.dao.filter;

import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.UnidadeMedida;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.Status;

public class FiltroAlimento extends FiltroConsulta{
	
	private static final long serialVersionUID = 8633286254025511932L;
	
	private String descricao;
	private UnidadeMedida unidadeMedida;
	private Status status;
	private SimNao proteico;

	@Override
	public void configurarFiltros() {
		Projection proj = Projections.projectionList().add(Projections.property("id").as("id"))
				.add(Projections.property("descricao").as("descricao"))
				.add(Projections.property("unidadeMedida").as("unidadeMedida"))
				.add(Projections.property("status").as("status"))
				.add(Projections.property("proteico").as("proteico"));

		getCriteria().setProjection(proj);
		
		if (this.getDescricao() != null && !this.getDescricao().isEmpty()) {
			getCriteria().add(Restrictions.eq("descricao", this.getDescricao()));
		}

		if (this.getUnidadeMedida() != null) {
			getCriteria().add(Restrictions.eq("unidadeMedida", this.getUnidadeMedida()));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}

		if (this.getProteico() != null) {
			getCriteria().add(Restrictions.eq("proteico", this.getProteico().equals(SimNao.S)));
		}
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public UnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public SimNao getProteico() {
		return proteico;
	}

	public void setProteico(SimNao proteico) {
		this.proteico = proteico;
	}
}
