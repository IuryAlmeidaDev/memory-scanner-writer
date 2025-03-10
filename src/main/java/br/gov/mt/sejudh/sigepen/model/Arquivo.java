package br.gov.mt.sejudh.sigepen.model;

import br.gov.mt.sejudh.sigepen.enumerator.TipoArquivo;


public class Arquivo {

	private String contentType;

	private String nome;
	
	private byte[] bytes;
	
	private TipoArquivo tipo;
	
	private String caminho;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getNome() {
	
		if (nome == null) { 
			throw new NullPointerException("Nome do arquivo está nulo."); 
		}
		
		return this.nome + "." + this.tipo.toString().toLowerCase();
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public TipoArquivo getTipo() {
		return tipo;
	}

	public void setTipo(TipoArquivo tipo) {
		this.tipo = tipo;
	}

	public String getCaminho() {
		return caminho;
	}

	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}
	
	
}
