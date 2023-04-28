package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.model.Fornecedor;

public class ContratoDTO {

	private long id;
	private Long numContrato;
	private Date vigenciaContrato;
	private Fornecedor fornecedor;

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

	public Fornecedor getFornecedor() {
		return this.fornecedor;
	}
	
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	@Override
	public String toString() {
		return "Contrato [id=" + id + ", numero Contrato = " + numContrato + "]";
	}

}
