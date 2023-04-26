package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;

public class FiltroContrato extends FiltroConsulta{

	private static final long serialVersionUID = 1L;
	private long id;	
	private String razaoSocial;
	private String nomeFantasia;
	private String cnpj;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getNomeFantasia() {
		return nomeFantasia;
	}
	
	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}
	
	public String getRazaoSocial() {
		return razaoSocial;
	}
	
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getCnpj() {
		return cnpj;
	}
	
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	@Override
	public void configurarFiltros() {
		
		Projection proj = Projections.projectionList()
				.add(Projections.property("id").as("id")) 
				.add(Projections.property("razaoSocial").as("razaoSocial"))
				.add(Projections.property("nomeFantasia").as("nomeFantasia"))
				.add(Projections.property("cnpj").as("cnpj"));
		getCriteria().setProjection(proj);

		if(this.getId() > 0){
			getCriteria().add(Restrictions.eq("id", this.getId()));
		}
		
		if(this.getNomeFantasia() !=null && !this.getNomeFantasia().equalsIgnoreCase("")){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nomeFantasia", this.getNomeFantasia(), MatchMode.ANYWHERE));
		}
		
		if(this.getRazaoSocial() !=null && !this.getRazaoSocial().equalsIgnoreCase("")){
			getCriteria().add(Restrictions.ilike("razaoSocial", this.getRazaoSocial(), MatchMode.ANYWHERE));
		}
		
		if(this.getCnpj() !=null && !this.getCnpj().equalsIgnoreCase("")){
			getCriteria().add(Restrictions.ilike("cnpj", this.getCnpj(), MatchMode.ANYWHERE));
		}
	}

}
