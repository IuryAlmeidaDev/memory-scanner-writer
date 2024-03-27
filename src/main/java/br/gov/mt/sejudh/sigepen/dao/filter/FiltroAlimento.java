package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.Status;

public class FiltroAlimento extends FiltroConsulta{
	
	private static final long serialVersionUID = 8633286254025511932L;
	
	private String descricao;
	private Status status;

	@Override
	public void configurarFiltros() {
		Projection proj = Projections.projectionList().add(Projections.property("id").as("id"))
				.add(Projections.property("descricao").as("descricao"))
				.add(Projections.property("status").as("status"));

		getCriteria().setProjection(proj);
		
		if (this.getDescricao() != null && !this.getDescricao().isEmpty()) {
			getCriteria().add(Restrictions.eq("descricao", this.getDescricao()));
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	
}
