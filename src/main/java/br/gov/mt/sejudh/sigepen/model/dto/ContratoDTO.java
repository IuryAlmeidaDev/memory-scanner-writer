package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.Status;

public class ContratoDTO {

	private long id;
	private String numContrato;
	private Date vigenciaContratoInicio;
	private Date vigenciaContratoFim;
	private String cnpjFornecedor;
	private String nomeFornecedor;
	private Status status;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumContrato() {
		return this.numContrato;
	}

	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
	}

	public Date getVigenciaContratoInicio() {
		return this.vigenciaContratoInicio;
	}

	public void setVigenciaContratoInicio(Date vigenciaContratoInicio) {
		this.vigenciaContratoInicio = vigenciaContratoInicio;
	}
	
	public Date getVigenciaContratoFim() {
		return this.vigenciaContratoFim;
	}

	public void setVigenciaContratoFim(Date vigenciaContratoFim) {
		this.vigenciaContratoFim = vigenciaContratoFim;
	}

	public String getCnpjFornecedor() {
		return this.cnpjFornecedor;
	}
	public void setCnpjFornecedor(String cnpjFornecedor) {
		this.cnpjFornecedor = cnpjFornecedor;
	}
	
	public String getNomeFornecedor() {
		return this.nomeFornecedor;
	}
	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}
	
	public String getStatus() {
		return this.status.getDescricao();
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "Contrato [id=" + id + ", numero Contrato = " + numContrato + "]";
	}

}
