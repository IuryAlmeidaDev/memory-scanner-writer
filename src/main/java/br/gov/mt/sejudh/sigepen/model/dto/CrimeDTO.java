package br.gov.mt.sejudh.sigepen.model.dto;

import javax.persistence.Column;
import javax.persistence.Enumerated;

import br.gov.mt.sejudh.sigepen.enumerator.Status;

public class CrimeDTO {

	private long id;
	private String idLegislacao;
	private String nomeLegislacao;
	private String numArtigo;
	private String numParagrafo;
	private String numInciso;
	private String numAlinea;	
	private String detalhamento;
	
	@Enumerated
	@Column(name = "STATUS")
	private Status status;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getIdLegislacao() {
		return idLegislacao;
	}
	public void setIdLegislacao(String idLegislacao) {
		this.idLegislacao = idLegislacao;
	}
	public String getNomeLegislacao() {
		return nomeLegislacao;
	}
	public void setNomeLegislacao(String nomeLegislacao) {
		this.nomeLegislacao = nomeLegislacao;
	}
	public String getNumArtigo() {
		return numArtigo;
	}
	public void setNumArtigo(String numArtigo) {
		this.numArtigo = numArtigo;
	}
	public String getNumParagrafo() {
		return numParagrafo;
	}
	public void setNumParagrafo(String numParagrafo) {
		this.numParagrafo = numParagrafo;
	}
	public String getNumInciso() {
		return numInciso;
	}
	public void setNumInciso(String numInciso) {
		this.numInciso = numInciso;
	}
	public String getNumAlinea() {
		return numAlinea;
	}
	public void setNumAlinea(String numAlinea) {
		this.numAlinea = numAlinea;
	}
	public String getDetalhamento() {
		return detalhamento;
	}
	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	
}
