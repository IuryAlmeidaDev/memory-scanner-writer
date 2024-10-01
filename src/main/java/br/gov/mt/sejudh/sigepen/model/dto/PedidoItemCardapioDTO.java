package br.gov.mt.sejudh.sigepen.model.dto;

public class PedidoItemCardapioDTO {

	private String descricao;
	private Double quantidade;
	private Double masc;
	private Double fem;
	
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
	
	public Double getMasc() {
		return masc;
	}
	
	public void setMasc(Double masc) {
		this.masc = masc;
	}
	
	public double getFem() {
		return fem;
	}
	
	public void setFem(double fem) {
		this.fem = fem;
	}
	
	public String toString() {
		return "PedidoItemCardapioDTO [+ descricao = " + descricao + "]";
	}

}
