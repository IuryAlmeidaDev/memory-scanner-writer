package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class FiltroCrime extends FiltroConsulta {

	private static final long serialVersionUID = 1L;

	private String legislacao;
	
	private String artigo;
	
	

	public String getLegislacao() {
		return legislacao;
	}

	public void setLegislacao(String legislacao) {
		this.legislacao = legislacao;
	}
	public String getArtigo() {
		return artigo;
	}
	public void setArtigo(String artigo) {
		this.artigo = artigo;
	}

	@Override
	public void configurarFiltros() {
		if(this.getLegislacao()!= null && !this.getLegislacao().equalsIgnoreCase("")){
			getCriteria().add(Restrictions.ilike("nomeLegislacao", this.getLegislacao(), MatchMode.ANYWHERE));
		}
		
		if(this.getArtigo()!= null && !this.getArtigo().equalsIgnoreCase("")){
			getCriteria().add(Restrictions.eq("numArtigo", this.getArtigo()));
		}
	}
	
	@Override
	public void configurarOrdenacao() {
		super.configurarOrdenacao();
		
		if (this.getOrdenacao() == null) {
			getCriteria().addOrder(Order.asc("idLegislacao"));
			getCriteria().addOrder(Order.asc("numArtigo"));
			getCriteria().addOrder(Order.asc("numParagrafo"));
			getCriteria().addOrder(Order.asc("numInciso"));
			getCriteria().addOrder(Order.asc("numAlinea"));
		}
	}
}
			

