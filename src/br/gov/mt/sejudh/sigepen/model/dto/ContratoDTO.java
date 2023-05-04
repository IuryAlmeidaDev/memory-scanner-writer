package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

public class ContratoDTO {

	private long id;
	private Long numContrato;
	private Date vigenciaContrato;
	private String cnpjFornecedor;
	private String unidade;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getNumContrato() {
		return this.numContrato;
	}

	public void setNumContrato(Long numContrato) {
		this.numContrato = numContrato;
	}

	public Date getVigenciaContrato() {
		return this.vigenciaContrato;
	}

	public void setVigenciaContrato(Date vigenciaContrato) {
		this.vigenciaContrato = vigenciaContrato;
	}

	public String getCnpjFornecedor() {
		return this.cnpjFornecedor;
	}
	public void setCnpjFornecedor(String cnpjFornecedor) {
		this.cnpjFornecedor = cnpjFornecedor;
	}
	
	public String getUnidade() {
		return this.unidade;
	}
	
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	
		
	@Override
	public String toString() {
		return "Contrato [id=" + id + ", numero Contrato = " + numContrato + "]";
	}

}
