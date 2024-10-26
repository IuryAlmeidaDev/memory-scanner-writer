package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.UnidadeMedida;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

public class PedidoConsultaDTO {

	private String refeicao;
	private String cardapio;
	private String item;
	private Long recuperandoTotal;
	private boolean especial; 
	private double masc;
	private double fem;
	private String nomeEstrutura;
	private String sexo;
	private String siglaUnidadeMedida;

	public PedidoConsultaDTO(Object[] row) {
		this.montarCardapio(row);
	}
	
	private void montarCardapio(Object[] row) {
		this.refeicao = (String) row[6];
		this.cardapio = (String) row[5];
		this.item = (String) row[1];
		this.especial = row[4].equals("ESPECIAL");
		this.masc = ((BigDecimal) row[2]).longValue();
		this.fem = ((BigDecimal) row[3]).longValue();
		this.recuperandoTotal = ((BigDecimal) row[0]).longValue();
		this.setSexo((String) row[7]);
		this.siglaUnidadeMedida = UnidadeMedida.getSiglaPorId(((BigDecimal) row[8]).intValue());
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

	public Long getRecuperandoTotal() {
		return recuperandoTotal;
	}

	public void setRecuperandoTotal(Long recuperandoTotal) {
		this.recuperandoTotal = recuperandoTotal;
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

	public String getSiglaUnidadeMedida() {
		return siglaUnidadeMedida;
	}

	public void setSiglaUnidadeMedida(String siglaUnidadeMedida) {
		this.siglaUnidadeMedida = siglaUnidadeMedida;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("refeicao", refeicao)
				.append("cardapio", cardapio)
				.append("item", item)
				.append("recuperandoTotal", recuperandoTotal)
				.append("especial", especial)
				.append("masc", masc)
				.append("fem", fem)
				.append("nomeEstrutura", nomeEstrutura)
				.append("sexo", sexo)
				.append("unidadeMedida", siglaUnidadeMedida)
				.toString();
	}
}
