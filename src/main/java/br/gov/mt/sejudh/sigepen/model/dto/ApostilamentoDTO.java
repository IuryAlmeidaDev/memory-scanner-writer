package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

public class ApostilamentoDTO {

	private long id;
	private Date dataInicio;
	private Date dataFim;
	private String numContrato;
	private String statusVigencia;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	
	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getNumContrato() {
		return numContrato;
	}

	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
	}

	public String getStatusVigencia() {
		return statusVigencia;
	}

	public void setStatusVigencia(String statusVigencia) {
		this.statusVigencia = statusVigencia;
	}

	@Override
	public String toString() {
		return "Apostilamento [id=" + id + ", Data = " + dataInicio + "]";
	}
}
