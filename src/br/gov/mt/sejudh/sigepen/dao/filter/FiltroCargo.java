package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroCargo extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private String nome;
	
	private Status status;
	
	private Boolean escolta;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Boolean getEscolta() {
		return escolta;
	}

	public void setEscolta(Boolean escolta) {
		this.escolta = escolta;
	}

	@Override
	public void configurarFiltros() {
		getCriteria().createAlias("categoria", "categoria");
		
		if(this.getNome()!= null && !this.getNome().equalsIgnoreCase("") ){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
		if (this.getEscolta() != null) {
			getCriteria().add(Restrictions.eq("escolta", this.getEscolta()));
		}
	}
}
