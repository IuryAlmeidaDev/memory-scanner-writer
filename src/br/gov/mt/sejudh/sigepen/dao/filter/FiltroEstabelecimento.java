package br.gov.mt.sejudh.sigepen.dao.filter;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.util.RestrictionsSigepen;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class FiltroEstabelecimento extends FiltroConsulta {
	
	private static final long serialVersionUID = 1L;
	
	private String nome;
	
	private Cidade cidade;
	
	private Estado estado;
	
	private Boolean procedencia;
		
	private Status status;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}
	
	public Boolean getProcedencia() {
		return procedencia;
	}

	public void setProcedencia(Boolean procedencia) {
		this.procedencia = procedencia;
	}

	@Override
	public void configurarFiltros() {
		getCriteria().createAlias("cidade", "cidade");
		
		if (this.getNome()!= null && !this.getNome().equalsIgnoreCase("") ) {
			getCriteria().add(RestrictionsSigepen.ignoraAcentoLike("nome", this.getNome(), MatchMode.ANYWHERE));
		}
		
		if(this.getEstado() != null){
			getCriteria().add(Restrictions.eq("cidade.estado", this.getEstado()));
		}
		
		if (this.getCidade() != null) {
			getCriteria().add(Restrictions.eq("cidade", this.getCidade()));
		}
	
		if (this.getStatus() != null) {
			getCriteria().add(Restrictions.eq("status", this.getStatus()));
		}
		
		if (this.getProcedencia() != null) {
			getCriteria().add(Restrictions.eq("proced", this.getProcedencia()));
		}
	}
}

