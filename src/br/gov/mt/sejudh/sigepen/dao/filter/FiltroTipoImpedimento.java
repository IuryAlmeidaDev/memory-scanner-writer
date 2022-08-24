package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroTipoImpedimento extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private String descricao;
	
	private SgpStatus status;
	
	private boolean selecionarCertidao;
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = Sigepen.formatarEspacos(descricao);
	}

	public SgpStatus getStatus() {
		return status;
	}

	public void setStatus(SgpStatus status) {
		this.status = status;
	}

	@Override
	public void configurarFiltros() {
		if(this.getDescricao()!= null && !this.getDescricao().equalsIgnoreCase("") ){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("descricao", this.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}

		if(this.selecionarCertidao) {
			getCriteria().add(Restrictions.eq("status", SgpStatus.A));
		}
	}
	
	public void setSelecionarCertidao(boolean selecionarCertidao) {
		this.selecionarCertidao = selecionarCertidao;
	}
}
