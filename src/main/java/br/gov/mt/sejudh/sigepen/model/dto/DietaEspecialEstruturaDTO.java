package br.gov.mt.sejudh.sigepen.model.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

public class DietaEspecialEstruturaDTO {
	private final String nomeEstrutura;
	private final String tipoDieta;
	private final Integer qtdRecuperando;

	public DietaEspecialEstruturaDTO(final Object[] row) {
		this.nomeEstrutura = (String) row[2];
		this.tipoDieta = (String) row[3];
        this.qtdRecuperando = ((BigDecimal) row[4]).intValue();
    }

	public String getTipoDieta() {
		return tipoDieta;
	}

	public String getNomeEstrutura() {
		return nomeEstrutura;
	}

	public Integer getQtdRecuperando() {
		return qtdRecuperando;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("estruturaNome", nomeEstrutura)
				.append("tipoDieta", tipoDieta)
				.append("qtdRecuperando", qtdRecuperando)
				.toString();
	}
}
