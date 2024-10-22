package br.gov.mt.sejudh.sigepen.model.dto;

import java.math.BigDecimal;

public class PedidoConsultaDTO {

	private String refeicao;
	private String cardapio;
	private String item;
	private Long qtd;
	private boolean especial; 
	private double masc;
	private double fem;
	private String nomeEstrutura;
	private String sexo;

	public PedidoConsultaDTO(Object[] row) {
		this.montarCardapio(row);
	}
	
	private void montarCardapio(Object[] row) {
		this.refeicao = (String) row[7];
		this.cardapio = (String) row[6];
		this.item = (String) row[2];
		this.especial = row[5].equals("ESPECIAL");
		this.masc = ((BigDecimal) row[3]).longValue();
		this.fem = ((BigDecimal) row[4]).longValue();
		this.qtd = ((BigDecimal) row[0]).longValue();
		//this.nomeEstrutura = (String) row[8];
		this.setSexo((String) row[8]);
	}

	public String getRefeicao() {
		return refeicao;
	}

	public void setRefeicao(String refeicao) {
		this.refeicao = refeicao;
	}

	public String getCardapio() {
		return cardapio;
	}

	public void setCardapio(String cardapio) {
		this.cardapio = cardapio;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public Long getQtd() {
		return qtd;
	}

	public void setQtd(Long qtd) {
		this.qtd = qtd;
	}
	
	public boolean isEspecial() {
		return especial;
	}

	public void setEspecial(boolean especial) {
		this.especial = especial;
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
	
	public String getNomeEstrutura() {
		return nomeEstrutura;
	}

	public void setNomeEstrutura(String nomeEstrutura) {
		this.nomeEstrutura = nomeEstrutura;
	}
	
	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	@Override
	public String toString() {
		return "PedidoConsultaDTO [refeicao=" + refeicao + ", cardapio=" + cardapio + ", item=" + item + ", qtd=" + qtd
				+ ", especial=" + especial + ", masc=" + masc + ", fem=" + fem + "]";
	}

}
