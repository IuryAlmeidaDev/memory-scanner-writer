package br.gov.mt.sejudh.sigepen.model.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.List;

public class DietaEspecialEstruturaDTO {
	private final String nomeEstrutura;
	private final String tipoDieta;
	private final Integer qtdRecuperando;
	private List<String> nomesRecuperandos;
	private final Long estruturaId;

	public DietaEspecialEstruturaDTO(final Object[] row) {
		this.estruturaId = ((BigDecimal) row[1]).longValue();
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

	public List<String> getNomesRecuperandos() {
		return nomesRecuperandos;
	}

	public void setNomesRecuperandos(List<String> nomesRecuperandos) {
		this.nomesRecuperandos = nomesRecuperandos;
	}

	public Long getEstruturaId() {
		return estruturaId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("estruturaNome", nomeEstrutura)
				.append("tipoDieta", tipoDieta)
				.append("qtdRecuperando", qtdRecuperando)
				.append("nomes", nomesRecuperandos)
				.append("estruturaId", estruturaId)
				.toString();
	}

}
