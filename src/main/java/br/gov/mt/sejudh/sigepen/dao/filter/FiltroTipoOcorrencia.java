package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;


public class FiltroTipoOcorrencia extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private String tipo;
	
	private String descricao;
	
	private Status status;
	
	private String classificacao;


	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {		
		this.tipo = Sigepen.formatarEspacos(tipo);
	}

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

	public String getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(String classificacao) {		
		this.classificacao = Sigepen.formatarEspacos(classificacao);
	}

	@Override
	public void configurarFiltros() {		
				
		if(this.getTipo()!= null && !this.getTipo().equalsIgnoreCase("") ){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("tipo", this.getTipo(), MatchMode.ANYWHERE));
		}
		
		if(this.getDescricao()!= null && !this.getDescricao().equalsIgnoreCase("") ){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("descricao", this.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
		if(this.getClassificacao()!= null && !this.getClassificacao().equalsIgnoreCase("") ){
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("classificacao", this.getClassificacao(), MatchMode.ANYWHERE));
		}
				
	}
}