package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;

public class UnidadePrisionalDTO {

	private long id;
	private String nome;
	private String sigla;
	private String tipo;
	private String responsavel;
	private String municipio;
	private SituacaoUnidade situacaoUnidade;
    private Integer quantidadeAtual;
    private long idEstrutura;

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

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getSituacaoUnidade() {
		return situacaoUnidade.getDescricao();
	}

	public void setSituacaoUnidade(SituacaoUnidade situacaoUnidade) {
		this.situacaoUnidade = situacaoUnidade;
	}

	public Integer getQuantidadeAtual() {
		return quantidadeAtual;
	}

	public void setQuantidadeAtual(Integer quantidadeAtual) {
		this.quantidadeAtual = quantidadeAtual;
	}

	public long getIdEstrutura() {
		return idEstrutura;
	}

	public void setIdEstrutura(long idEstrutura) {
		this.idEstrutura = idEstrutura;
	}

	@Override
	public String toString() {
		return "Unidade Prisional [id=" + id + ", nome=" + nome + "]";
	}

}
