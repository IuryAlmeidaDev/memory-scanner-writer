package br.gov.mt.sejudh.sigepen.model.auxiliar;

import java.io.Serializable;

public class Relatorio implements Serializable {

	private static final long serialVersionUID = -671152927173073738L;
	
	private String id;
	private String nome;

	public Relatorio() {
		super();
		this.nome = "";
	}

	public Relatorio(String id, String nome) {
		super();
		this.id = id;
		this.nome = nome;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

}
