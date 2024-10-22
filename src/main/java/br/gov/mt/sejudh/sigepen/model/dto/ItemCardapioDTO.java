package br.gov.mt.sejudh.sigepen.model.dto;

public class ItemCardapioDTO {

	private long id;
	private String descricao;
	private double masc;
	private double fem;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public double getMasc() {
		return masc;
	}
	
	public void setMasc(double masc) {
		this.masc = masc;
	}
	
	public double getFem() {
		return fem;
	}
	
	public void setFem(double fem) {
		this.fem = fem;
	}

	@Override
	public String toString() {
		return "Apostilamento [id=" + id + ", descricao = " + descricao + "]";
	}

}
