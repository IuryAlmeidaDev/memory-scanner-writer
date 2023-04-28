package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.model.Fornecedor;

public class FiltroContrato extends FiltroConsulta{

	private static final long serialVersionUID = 1L;
	private long id;
	private Long numContrato;
	private Date vigenciaContrato;
	private Fornecedor fornecedor;	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Long getNumContrato() {
		return this.numContrato;
	}
	
	public void setNumContrato(Long numContrato) {
		this.numContrato = numContrato;
	}
	
	public Date getVigenciaContrato() {
		return this.vigenciaContrato;
	}
	
	public void setVigenciaContrato(Date vigenciaContrato) {
		this.vigenciaContrato = vigenciaContrato;
	}
	
	public Fornecedor getFornecedor() {
		return this.fornecedor;
	}
	
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Override
	public void configurarFiltros() {
		getCriteria()
		.createAlias("unidade", "unidade", JoinType.LEFT_OUTER_JOIN);
		
		Projection proj = Projections.projectionList()
				.add(Projections.property("id").as("id")) 
				.add(Projections.property("numContrato").as("numContrato"))
				.add(Projections.property("unidade.id").as("idcontrato"))
				.add(Projections.property("vigenciaContrato").as("vigenciaContrato"));
		getCriteria().setProjection(proj);

		if(this.getId() > 0){
			getCriteria().add(Restrictions.eq("id", this.getId()));
		}
		
		if(this.getNumContrato() !=null){
			getCriteria().add(Restrictions.eq("numContrato", this.getNumContrato()));
		}
		
		if(this.getVigenciaContrato() !=null){
			getCriteria().add(Restrictions.eq("vigenciaContrato", this.getVigenciaContrato()));
		}
		
	}

}
