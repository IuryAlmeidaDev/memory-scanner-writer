package br.gov.mt.sejudh.sigepen.dao.filter;

import br.gov.mt.sejudh.sigepen.enumerator.NaturezaFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class FiltroTipoFaltaDisciplinar extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private String decricacao;
	
	private Status status;
	
	private NaturezaFaltaDisciplinar natureza;

	public String getDecricacao() {
		return decricacao;
	}

	public void setDecricacao(String decricacao) {
		this.decricacao = Sigepen.formatarEspacos(decricacao);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public NaturezaFaltaDisciplinar getNatureza() {
		return natureza;
	}

	public void setNatureza(NaturezaFaltaDisciplinar natureza) {
		this.natureza = natureza;
	}

	@Override
	public void configurarFiltros() {
		if(this.getDecricacao()!= null && !this.getDecricacao().equalsIgnoreCase("") ){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("descricao", this.getDecricacao(), MatchMode.ANYWHERE));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
		if (this.getNatureza() != null) {
			getCriteria().add(Restrictions.eq("natureza", this.getNatureza()));
		}
	}
}
