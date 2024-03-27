package br.gov.mt.sejudh.sigepen.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public class RelatorioMapaAlimentacaoConsultaDTO {
	
	private String nomeUnidade;
	private Date virgenciaContartoInicio;
	private Date virgenciaContartoFim;
	private String numeroContrato;
	private String nomeFornecedor;
	private String refeicao;
	private Double valorNormal;
	private Double valorEspecial;
	private Double soma;
	
	public RelatorioMapaAlimentacaoConsultaDTO() {
		
	}
	
	public RelatorioMapaAlimentacaoConsultaDTO(Object[] row) {
		this.virgenciaContartoInicio = ((Date) row[0]);
		this.virgenciaContartoFim = (Date) row[1];		
		this.numeroContrato = ((String) row[2]);
		this.nomeFornecedor = ((String) row[3]);
		this.refeicao = ((String) row[4]);
		this.valorNormal = ((BigDecimal) row[5]).doubleValue();
		this.valorEspecial = ((BigDecimal) row[6]).doubleValue();
		
		
	}

	public RelatorioMapaAlimentacaoConsultaDTO(String nomeUnidade, Date virgenciaContartoInicio, Date virgenciaContartoFim,
			String numeroContrato, String nomeFornecedor, String mesReferencia, String valorRefeicao,
			String nomeRecuperando, String matriculaRecuperando, Date entradaRecuperando, Date saidaRecuperando,
			Integer diasRecuperando, Integer cafeManha, Integer almoco, Integer janta, Integer cafeManhaEspecial,
			Integer almocoEspecial, Integer jantaEspecial, Double soma) {
		this.nomeUnidade = nomeUnidade;
		this.virgenciaContartoInicio = virgenciaContartoInicio;
		this.virgenciaContartoFim = virgenciaContartoFim;
		this.numeroContrato = numeroContrato;
		this.nomeFornecedor = nomeFornecedor;
		this.soma = soma;
	}



	public String getNomeUnidade() {
		return nomeUnidade;
	}

	public void setNomeUnidade(String nomeUnidade) {
		this.nomeUnidade = nomeUnidade;
	}

	public Date getVirgenciaContartoInicio() {
		return virgenciaContartoInicio;
	}

	public void setVirgenciaContartoInicio(Date virgenciaContartoInicio) {
		this.virgenciaContartoInicio = virgenciaContartoInicio;
	}

	public Date getVirgenciaContartoFim() {
		return virgenciaContartoFim;
	}

	public void setVirgenciaContartoFim(Date virgenciaContartoFim) {
		this.virgenciaContartoFim = virgenciaContartoFim;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public String getNomeFornecedor() {
		return nomeFornecedor;
	}

	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}

	public String getRefeicao() {
		return refeicao;
	}

	public void setRefeicao(String refeicao) {
		this.refeicao = refeicao;
	}

	public Double getValorNormal() {
		return valorNormal;
	}

	public void setValorNormal(Double valorNormal) {
		this.valorNormal = valorNormal;
	}

	public Double getValorEspecial() {
		return valorEspecial;
	}

	public void setValorEspecial(Double valorEspecial) {
		this.valorEspecial = valorEspecial;
	}

	public Double getSoma() {
		return soma;
	}

	public void setSoma(Double soma) {
		this.soma = soma;
	}

	@Override
	public String toString() {
		return "RelatorioMapaAlimentacaoConsultaDTO [nomeUnidade=" + nomeUnidade + ", virgenciaContartoInicio="
				+ virgenciaContartoInicio + ", virgenciaContartoFim=" + virgenciaContartoFim + ", numeroContrato="
				+ numeroContrato + ", nomeFornecedor=" + nomeFornecedor + ", refeicao=" + refeicao + ", valorNormal="
				+ valorNormal + ", valorEspecial=" + valorEspecial + ", soma=" + soma + "]";
	}

}
