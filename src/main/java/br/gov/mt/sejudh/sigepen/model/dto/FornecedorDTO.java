package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.Status;

public class FornecedorDTO {

	private long id;
	private String razaoSocial;
	private String nomeFantasia;
	private String cnpj;
	private Status status;
	private String creador;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	
	public String getStatus() {
		return status.getDescricao();
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getCreador() {
		return this.creador;
	}
	public void setCreador(String creador) {
		this.creador = creador;
	}

	@Override
	public String toString() {
		return "Fornecedor [id=" + id + ", nome = " + nomeFantasia + "]";
	}

}
