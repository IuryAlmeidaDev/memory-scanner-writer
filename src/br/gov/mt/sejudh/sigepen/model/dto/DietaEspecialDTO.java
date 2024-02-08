package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

public class DietaEspecialDTO {

	private long id;
	private Date inicio;
	private Date fim;
	private String nomeRecuperando;
	private String tipoDieta;
	
	private Date dataEntrada;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getInicio() {
		return inicio;
	}

	public Date getFim() {
		return fim;
	}
	
	public String getTipoDieta() {
		return tipoDieta;
	}
	
	public String setTipoDieta() {
		return tipoDieta;
	}
	
	public String getNomeRecuperando() {
		return nomeRecuperando;
	}
		
	@Override
	public String toString() {
		return "Contrato [id=" + id + ",nomeRecuperando = " + nomeRecuperando + "]";
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

}
