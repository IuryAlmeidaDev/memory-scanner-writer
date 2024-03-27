package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.Status;

public class FiltroTipoDieta extends FiltroConsulta {

	private static final long serialVersionUID = -2825896853502036577L;
	
	private String descricao;
	private Boolean dietaEspecial;
	private Status status;
	
	@Override
	public void configurarFiltros() {
		Projection proj = Projections.projectionList().add(Projections.property("id").as("id"))
				.add(Projections.property("descricao").as("descricao"))
				.add(Projections.property("status").as("status"))
				.add(Projections.property("especial").as("especial"));

		getCriteria().setProjection(proj);
		
		if (this.getDescricao() != null && !this.getDescricao().isEmpty()) {
			getCriteria().add(Restrictions.eq("descricao", this.getDescricao()));
		}
		
		if (this.getDietaEspecial() != null) {
			getCriteria().add(Restrictions.eq("especial", this.getDietaEspecial()));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getDietaEspecial() {
		return dietaEspecial;
	}

	public void setDietaEspecial(Boolean dietaEspecial) {
		this.dietaEspecial = dietaEspecial;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
