package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name="NOME_COMPLETO_ESTRUTURA", schema=Sigepen.SEJUDH_SCHEMA)
public class EstruturaComplemento {

	@Id
	private Integer id;

	@Column(name="IS_UNIDADE")
	private boolean isUnidade;
	
	@Column(name="NOME_COMPLETO")
	private String nomeCompleto;
	
	@Column(name="NOME_COMPLETO_UNIDADE")
	private String nomeCompletoUnidade;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ID")
	private Estrutura estrutura;

	public boolean getIsUnidade() {
		return isUnidade;
	}

	public void setIsUnidade(boolean isUnidade) {
		this.isUnidade = isUnidade;
	}

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	public String getNomeCompletoUnidade() {
		return nomeCompletoUnidade;
	}

	public void setNomeCompletoUnidade(String nomeCompletoUnidade) {
		this.nomeCompletoUnidade = nomeCompletoUnidade;
	}

	public Estrutura getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(Estrutura estrutura) {
		this.estrutura = estrutura;
	}
}
