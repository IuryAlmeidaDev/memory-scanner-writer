package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name = "PARAMETRO", schema = Sigepen.SEJUDH_SCHEMA)
public class Parametro {

	@Column(name = "NOME", length = 100, nullable = false)
	@Id
	private String nome;
	
	@Column(name = "VALOR", length = 100, nullable = false)
	private String valor;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
