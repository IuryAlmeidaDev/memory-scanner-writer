package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name = "PARAMETRO", schema = Sigepen.SEJUDH_SCHEMA)
public class Parametro {

	@Column(name = "ID", nullable = false)
	@Id
	private Long id;
	
	@Column(name = "NOME", length = 100, nullable = false)
	private String nome;
	
	@Column(name = "VALOR", length = 100, nullable = false)
	private String valor;
	
	public Parametro() {
		this.id = 0L;
	}
	
	public Parametro(Long id, String nome, String valor) {
		this.id = id;
		this.nome = nome.toUpperCase();
		this.valor = valor.toUpperCase();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome.toUpperCase();
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor.toUpperCase();
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
