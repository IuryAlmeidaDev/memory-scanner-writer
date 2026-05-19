package br.gov.mt.sejudh.sigepen.model.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.Status;

public class DietaEspecialDTO {

	private long id;
	private Date inicio;
	private Date fim;
	private String nomeRecuperando;
	private String nomeSocialRecuperando;
	private String nomeUnidadePrisional;
	private String tipoDieta;
	private Status status;
	
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

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public String getTipoDieta() {
		return tipoDieta;
	}

	public void setTipoDieta(String tipoDieta) {
		this.tipoDieta = tipoDieta;
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getStatusAtual() {
		Date agora = new Date();

		if (Status.INATIVO.equals(status) || (fim != null && !fim.after(agora))) {
			return "Encerrada";
		}

		if (Status.SUSPENSO.equals(status)) {
			return "Suspensa";
		}

		return "Ativa";
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
				.append("status", status)
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
