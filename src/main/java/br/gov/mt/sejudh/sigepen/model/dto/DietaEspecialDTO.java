package br.gov.mt.sejudh.sigepen.model.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class DietaEspecialDTO {

	private long id;
	private Date inicio;
	private Date fim;
	private String nomeRecuperando;
	private String nomeSocialRecuperando;
	private String nomeUnidadePrisional;
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

	public String getNomeUnidadePrisional() {
		return nomeUnidadePrisional;
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("id", id)
				.append("inicio", inicio)
				.append("fim", fim)
				.append("nomeRecuperando", nomeRecuperando)
				.append("nomeUnidadePrisional", nomeUnidadePrisional)
				.append("tipoDieta", tipoDieta)
				.append("dataEntrada", dataEntrada)
				.toString();
	}

    public String getNomeSocialRecuperando() {
        return nomeSocialRecuperando;
    }

    public void setNomeSocialRecuperando(String nomeSocialRecuperando) {
        this.nomeSocialRecuperando = nomeSocialRecuperando;
    }
}
