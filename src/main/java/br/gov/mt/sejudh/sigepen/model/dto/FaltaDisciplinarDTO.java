package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.SituacaoFaltaDisciplinar;

import java.util.Date;


public class FaltaDisciplinarDTO {

	private long id;

	private long matricula;

	private String recuperando;

	private String tipoFalta;

	private Date data;

	private SituacaoFaltaDisciplinar situacao;
	
	private String nomeSocial;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMatricula() {
		return matricula;
	}

	public void setMatricula(long matricula) {
		this.matricula = matricula;
	}

	public String getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(String recuperando) {
		this.recuperando = recuperando;
	}

	public String getTipoFalta() {
		return tipoFalta;
	}

	public void setTipoFalta(String tipoFalta) {
		this.tipoFalta = tipoFalta;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public SituacaoFaltaDisciplinar getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoFaltaDisciplinar situacao) {
		this.situacao = situacao;
	}		

	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	@Override
	public String toString() {
		return "Falta Disciplinar [id=" + id + "]";
	}
}