package br.gov.mt.sejudh.sigepen.model.dto;

import java.math.BigDecimal;

public class PedidoConsultaDTO {

	private String refeicao;
	private String cardapio;
	private String item;
	private Long qtd;
	private boolean especial; 
	private double mg_masc;
	private double mg_fem;
	private String nomeEstrutura;
	private String sexo;

	public PedidoConsultaDTO(Object[] row) {
		this.montarCardapio(row);
	}
	
	private void montarCardapio(Object[] row) {
		this.refeicao = (String) row[7];
		this.cardapio = (String) row[6];
		this.item = (String) row[2];
		this.especial = (boolean) row[5].equals("ESPECIAL") ? true : false;
		this.mg_masc = ((BigDecimal) row[3]).longValue();
		this.mg_fem = ((BigDecimal) row[4]).longValue();
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
				+ ", especial=" + especial + ", mg_masc=" + mg_masc + ", mg_fem=" + mg_fem + "]";
	}

}
