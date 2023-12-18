package br.gov.mt.sejudh.sigepen.model.dto;

public class PedidoItemCardapioDTO {

	private String descricao;
	private Double quantidade;
	private Double mg_masc;
	private Double mg_fem;
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Double quantidade, Double peso) {
		this.quantidade = quantidade * (peso / 1000);
	}
	
	public Double getMg_masc() {
		return mg_masc;
	}
	
	public void setMg_masc(Double mg_masc) {
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
		return "PedidoItemCardapioDTO [+ descricao = " + descricao + "]";
	}

}
