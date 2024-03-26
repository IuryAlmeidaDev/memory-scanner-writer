package br.gov.mt.sejudh.sigepen.model.dto;

public class ItemCardapioDTO {

	private long id;
	private String descricao;
	private double mg_masc;
	private double mg_fem;
	
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
	
	public double getMg_masc() {
		return mg_masc;
	}
	
	public void setMg_masc(double mg_masc) {
		this.mg_masc = mg_masc;
	}
	
	public double getMg_fem() {
		return mg_fem;
	}
	
	public void setMg_fem(double mg_fem) {
		this.mg_fem = mg_fem;
	}

	@Override
	public String toString() {
		return "Apostilamento [id=" + id + ", descricao = " + descricao + "]";
	}

}
