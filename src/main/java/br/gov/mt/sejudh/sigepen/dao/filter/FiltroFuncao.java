package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroFuncao extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private String descricao;
	
	private Status status;
	
	private Boolean lotacaoObrigatoria;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = Sigepen.formatarEspacos(descricao);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Boolean getLotacaoObrigatoria() {
		return lotacaoObrigatoria;
	}

	public void setLotacaoObrigatoria(Boolean lotacaoObrigatoria) {
		this.lotacaoObrigatoria = lotacaoObrigatoria;
	}

	@Override
	public void configurarFiltros() {
		if(this.getDescricao()!= null && !this.getDescricao().equalsIgnoreCase("") ){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
		if (this.getLotacaoObrigatoria() != null) {
			getCriteria().add(Restrictions.eq("lotacaoObrigatoria", this.getLotacaoObrigatoria()));
		}
	}
}
