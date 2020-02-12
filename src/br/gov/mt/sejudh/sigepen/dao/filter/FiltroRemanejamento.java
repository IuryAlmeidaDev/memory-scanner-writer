package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;

public class FiltroRemanejamento extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String recuperandoNome;
	
	private UnidadePrisional unidadeOrigem;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	public UnidadePrisional unidadeRemanejada;
	
	private StatusPedido status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRecuperandoNome() {
		return recuperandoNome;
	}

	public void setRecuperandoNome(String recuperandoNome) {
		this.recuperandoNome = recuperandoNome;
	}

	public UnidadePrisional getUnidadeOrigem() {
		return unidadeOrigem;
	}

	public void setUnidadeOrigem(UnidadePrisional unidadeOrigem) {
		this.unidadeOrigem = unidadeOrigem;
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

	public UnidadePrisional getUnidadeRemanejada() {
		return unidadeRemanejada;
	}

	public void setUnidadeRemanejada(UnidadePrisional unidadeRemanejada) {
		this.unidadeRemanejada = unidadeRemanejada;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public void setStatus(StatusPedido status) {
		this.status = status;
	}

	@Override
	public void configurarFiltros() {
		
		getCriteria()
		.createAlias("pedido", "pedido", JoinType.LEFT_OUTER_JOIN)
		.createAlias("pedido.unidadeOrigem", "unidadeOrigem", JoinType.LEFT_OUTER_JOIN)
		.createAlias("pedido.recuperando", "recuperando", JoinType.LEFT_OUTER_JOIN)
		.createAlias("recuperando.pessoa", "pessoa", JoinType.LEFT_OUTER_JOIN);
		
		
		Projection proj = Projections.projectionList()
				.add(Projections.property("id").as("id"))
				.add(Projections.property("recuperando.id").as("recId"))
				.add(Projections.property("pedido.id").as("pedidoId"))
				.add(Projections.property("pessoa.nome").as("recuperando"))
				.add(Projections.property("dataInicio").as("dataInicio"))
				.add(Projections.property("dataConclusao").as("dataConclusao"))
				.add(Projections.property("unidadeOrigem.nome").as("unidadeOrigem"))
	            .add(Projections.property("status").as("status"))
	            .add(Projections.property("pedido.tipoTransito").as("tipoTransito"));
		
		getCriteria().setProjection(proj);	
		
		if (this.getId() != null) {
			getCriteria().add(Restrictions.eq("id", this.getId()));
		}
		
		if (this.getRecuperandoNome() != null && !this.getRecuperandoNome().equalsIgnoreCase("")) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nome", this.getRecuperandoNome(), MatchMode.ANYWHERE));
		}
		
		if (this.getUnidadeOrigem()!= null){
			getCriteria().add(Restrictions.eq("pedido.unidadeOrigem", this.getUnidadeOrigem()));
		}
		
		if(this.getUnidadeRemanejada()!= null){
			getCriteria().add(Restrictions.eq("unidadeRemanejada", this.getUnidadeRemanejada()));
		}
		
		if(this.getDataInicio() != null){
			getCriteria().add(Restrictions.ge("dataInicio", this.getDataInicio()));
		}
		
		if(this.getDataFim()!= null){
			getCriteria().add(Restrictions.le("dataInicio", this.getDataFim()));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
	}
}
