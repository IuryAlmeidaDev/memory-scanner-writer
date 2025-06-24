package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.UnidadeMedida;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

public class RefeicaoDetalhadaDTO {
	private String refeicao;
	private String item;
	private Long recuperandoTotal;
	private double masc;
	private double fem;
	private String nomeEstrutura;
	private String sexo;
	private String siglaUnidadeMedida;
	private Long itemCardapioId;

	public RefeicaoDetalhadaDTO(Object[] row) {
		this.montarCardapio(row);
	}

	private void montarCardapio(Object[] row) {
		this.recuperandoTotal = ((BigDecimal) row[0]).longValue();
		this.item = (String) row[1];
		this.masc = ((BigDecimal) row[2]).longValue();
		this.fem = ((BigDecimal) row[3]).longValue();
		this.refeicao = (String) row[4];
		this.setSexo((String) row[5]);
		this.siglaUnidadeMedida = UnidadeMedida.getSiglaPorId(((BigDecimal) row[6]).intValue());
		this.itemCardapioId = row.length > 7 ? ((BigDecimal) row[7]).longValue() : null;
	}

	public String getRefeicao() {
		return refeicao;
	}

	public void setRefeicao(String refeicao) {
		this.refeicao = refeicao;
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

	public Long getItemCardapioId() {
		return itemCardapioId;
	}

	public void setItemCardapioId(Long itemCardapioId) {
		this.itemCardapioId = itemCardapioId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("refeicao", refeicao)
				.append("item", item)
				.append("recuperandoTotal", recuperandoTotal)
				.append("masc", masc)
				.append("fem", fem)
				.append("nomeEstrutura", nomeEstrutura)
				.append("sexo", sexo)
				.append("unidadeMedida", siglaUnidadeMedida)
				.append("itemCardapioId", itemCardapioId)
				.toString();
	}
}
