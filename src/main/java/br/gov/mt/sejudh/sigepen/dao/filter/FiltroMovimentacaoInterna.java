package br.gov.mt.sejudh.sigepen.dao.filter;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import org.hibernate.NullPrecedence;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.sql.JoinType;

import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroMovimentacaoInterna extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	
	private long numPedido;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	private StatusMovimentacao status;
	
	public UnidadePrisional unidadeOrigem;
	
	//private String nomeSocial;
	
/*
	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}*/

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

	public UnidadePrisional getUnidadeOrigem() {
		return unidadeOrigem;
	}

	public void setUnidadeOrigem(UnidadePrisional unidadeOrigem) {
		this.unidadeOrigem = unidadeOrigem;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getNumPedido() {
		return numPedido;
	}

	public void setNumPedido(long numPedido) {
		this.numPedido = numPedido;
	}

	public StatusMovimentacao getStatus() {
		return status;
	}

	public void setStatus(StatusMovimentacao status) {
		this.status = status;
	}

	@Override
	public void configurarFiltros() {
		
		DetachedCriteria pedidoMovimentacao = DetachedCriteria.forClass(PedidoTransito.class, "ped")
				.createAlias("movimentacaoInterna", "movimentacaoInterna")
			    .add(Restrictions.eq("id", numPedido))
			    .setProjection( Projections.property("movimentacaoInterna.id") );
		
		if(this.getNumPedido() > 0){
			getCriteria().add( Subqueries.propertyIn("id", pedidoMovimentacao) );
		}
		
		if(this.getId() > 0){
			getCriteria().add(Restrictions.eq("id", this.getId()));
		}
		
		if(this.getUnidadeOrigem()!= null){
			getCriteria().add(Restrictions.eq("unidadeOrigem", this.getUnidadeOrigem()));
		}
		
		if(this.getDataInicio() != null){
			getCriteria().add(Restrictions.ge("dataPrevista", Sigepen.getDataConsultaMaiorIgual(this.getDataInicio())));
		}
		
		if(this.getDataFim()!= null){
			getCriteria().add(Restrictions.le("dataPrevista", Sigepen.getDataConsultaMenorIgual(this.getDataFim())));
		}
		
		if(this.getStatus() != null){
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
		// Adicionando filtros para nome social
		/*
	    getCriteria()
	        .createAlias("unidadeOrigem.contatos", "contatos", JoinType.LEFT_OUTER_JOIN)
	        .createAlias("contatos.pessoa", "pessoa", JoinType.LEFT_OUTER_JOIN)
	        .setProjection(Projections.projectionList()
	            .add(Projections.property("id").as("id"))
	            .add(Projections.property("unidadeOrigem.id").as("unidadeOrigemId"))
	            .add(Projections.property("pessoa.nomeSocial").as("nomeSocial")));

	    if (this.getNomeSocial() != null && !this.getNomeSocial().equalsIgnoreCase("")) {
	        getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("pessoa.nomeSocial", this.getNomeSocial(), MatchMode.ANYWHERE));
	    }*/
		
		
		
	}
}
