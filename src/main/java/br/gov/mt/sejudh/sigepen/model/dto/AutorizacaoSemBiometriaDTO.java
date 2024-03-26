package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.TipoAutorizacaoValidacaoSemBio;

public class AutorizacaoSemBiometriaDTO {
	private Long id;
	private String nomeAutorizador;
	private Date dataAutorizacao;
	private String observacao;
	private TipoAutorizacaoValidacaoSemBio tipoAutorizacao;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNomeAutorizador() {
		return nomeAutorizador;
	}
	public void setNomeAutorizador(String nomeAutorizador) {
		this.nomeAutorizador = nomeAutorizador;
	}
	public Date getDataAutorizacao() {
		return dataAutorizacao;
	}
	public void setDataAutorizacao(Date dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public TipoAutorizacaoValidacaoSemBio getTipoAutorizacao() {
		return tipoAutorizacao;
	}
	public void setTipoAutorizacao(TipoAutorizacaoValidacaoSemBio tipoAutorizacao) {
		this.tipoAutorizacao = tipoAutorizacao;
	}
}
