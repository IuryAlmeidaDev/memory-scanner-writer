package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

public class ContratoDTO {

	private long id;
	private Long numContrato;
	private Date vigenciaContrato;

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

	@Override
	public String toString() {
		return "Contrato [id=" + id + ", numero Contrato = " + numContrato + "]";
	}

}
