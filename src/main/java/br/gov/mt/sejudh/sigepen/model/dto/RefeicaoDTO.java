package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.model.Apostilamento;

public class RefeicaoDTO {

	private long id;
	private String nome;
	private Float valor;
	private Boolean especial;
	private Apostilamento apostilamento;

	public RefeicaoDTO() {

	}

	public RefeicaoDTO(long id, String nome, Float valor, Boolean especial, Apostilamento apostilamento) {
		super();
		this.id = id;
		this.nome = nome;
		this.valor = valor;
		this.especial = especial;
		this.apostilamento = apostilamento;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Float getValor() {
		return valor;
	}

	public void setValor(Float valor) {
		this.valor = valor;
	}

	public Boolean getEspecial() {
		return especial;
	}

	public void setEspecial(Boolean especial) {
		this.especial = especial;
	}

	public Apostilamento getApostilamento() {
		return apostilamento;
	}

	public void setApostilamento(Apostilamento apostilamento) {
		this.apostilamento = apostilamento;
	}
}
