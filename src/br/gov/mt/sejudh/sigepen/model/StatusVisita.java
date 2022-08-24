package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamentosVisita;

public class StatusVisita {
	StatusCredenciamentosVisita status;
	String observacao;
	
	
	public StatusVisita() {
		super();
	}
	
	public StatusVisita(StatusCredenciamentosVisita status, String observacao) {
		super();
		this.status = status;
		this.observacao = observacao;
	}
	public StatusCredenciamentosVisita getStatus() {
		return status;
	}
	public void setStatus(StatusCredenciamentosVisita status) {
		this.status = status;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
