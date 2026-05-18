package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;

public class FiltroFornecedor extends FiltroConsulta{

	private static final long serialVersionUID = 1L;
	private String cnpj;
	private String creador;
	private String razaoSocial;
	private String nomeFantasia;
	private Status status;

	@Override
	public void configurarFiltros() {
		
		Projection proj = Projections.projectionList()
				.add(Projections.property("id").as("id")) 
				.add(Projections.property("razaoSocial").as("razaoSocial"))
				.add(Projections.property("nomeFantasia").as("nomeFantasia"))
				.add(Projections.property("status").as("status"))
				.add(Projections.property("creador").as("creador"))
				.add(Projections.property("cnpj").as("cnpj"));
		getCriteria().setProjection(proj);
		
		if(this.getCnpj() !=null && !this.getCnpj().equalsIgnoreCase("")){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("cnpj", this.getCnpj(), MatchMode.ANYWHERE));
		}
		
		if(this.getCreador() !=null && !this.getCreador().equalsIgnoreCase("")){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("creador", this.getCreador(), MatchMode.ANYWHERE));
		}
		
		if(this.getRazaoSocial() !=null && !this.getRazaoSocial().equalsIgnoreCase("")){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("razaoSocial", this.getRazaoSocial(), MatchMode.ANYWHERE));
		}
		
		if(this.getNomeFantasia() !=null && !this.getNomeFantasia().equalsIgnoreCase("")){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nomeFantasia", this.getNomeFantasia(), MatchMode.ANYWHERE));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
		if(this.getRazaoSocial() !=null && !this.getRazaoSocial().equalsIgnoreCase("")){
			getCriteria().add(Restrictions.ilike("razaoSocial", this.getRazaoSocial(), MatchMode.ANYWHERE));
		}
		if(this.getNomeFantasia() !=null && !this.getNomeFantasia().equalsIgnoreCase("")){
			getCriteria().add(Restrictions.ilike("nomeFantasia", this.getNomeFantasia(), MatchMode.ANYWHERE));
		}
	}
	
	public String getCnpj() {
		return cnpj;
	}
	
	public void setCnpj(String cnpj) {
		this.cnpj = normalizarDocumento(cnpj);
	}

	private String normalizarDocumento(String documento) {
		if (documento == null) {
			return null;
		}
		return documento.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
	}
	
	public String getCreador() {
		return creador;
	}
	
	public void setCreador(String creador) {
		this.creador = creador;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
