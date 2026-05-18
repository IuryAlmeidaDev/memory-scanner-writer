package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mt.sejudh.sigepen.util.MonetaryFormatUtil;

public class MapaConsolidadoDTO {
	
	private String nomeUnidade;
	private Date virgenciaContartoInicio;
	private Date virgenciaContartoFim;
	private String numeroContrato;
	private String nomeFornecedor;
	private String mesReferencia;
	private String valorRefeicao;
	private Integer totalCafe;
	private Integer totalAlmoco;
	private Integer totalJanta;
	private Integer totalCafeEspecial;
	private Integer totalAlmocoEspecial;
	private Integer totalJantaEspecial;
	private Double totalValorCafe;
	private Double totalValorAlmoco;
	private Double totalValorJanta;
	private Double totalValorCafeEspecial;
	private Double totalValorAlmocoEspecial;
	private Double totalValorJantaEspecial;
	private List<MapaConsolidadoRecuperandoDTO> consolidadoRecuperandoDTOs = new ArrayList<>();
	
	public MapaConsolidadoDTO() {
		
	}

	public MapaConsolidadoDTO(String nomeUnidade, Date virgenciaContartoInicio, Date virgenciaContartoFim,
			String numeroContrato, String nomeFornecedor, String mesReferencia, String valorRefeicao,
			String nomeRecuperando, String matriculaRecuperando, Date entradaRecuperando, Date saidaRecuperando,
			Integer diasRecuperando, Double cafeManha, Double almoco, Double janta, Double cafeManhaEspecial,
			Double almocoEspecial, Double jantaEspecial, Double soma) {
		this.nomeUnidade = nomeUnidade;
		this.virgenciaContartoInicio = virgenciaContartoInicio;
		this.virgenciaContartoFim = virgenciaContartoFim;
		this.numeroContrato = numeroContrato;
		this.nomeFornecedor = nomeFornecedor;
		this.mesReferencia = mesReferencia;
		this.valorRefeicao = valorRefeicao;
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

	public String getMesReferencia() {
		return mesReferencia;
	}

	public void setMesReferencia(String mesReferencia) {
		this.mesReferencia = mesReferencia;
	}

	public String getValorRefeicao() {
		return valorRefeicao;
	}

	public void setValorRefeicao(String valorRefeicao) {
		this.valorRefeicao = valorRefeicao;
	}

	public List<MapaConsolidadoRecuperandoDTO> getConsolidadoRecuperandoDTOs() {
		return consolidadoRecuperandoDTOs;
	}

	public void setConsolidadoRecuperandoDTOs(List<MapaConsolidadoRecuperandoDTO> consolidadoRecuperandoDTOs) {
		this.consolidadoRecuperandoDTOs = consolidadoRecuperandoDTOs;
	}

	public Integer getTotalCafe() {
		return totalCafe;
	}

	public void setTotalCafe(Integer totalCafe) {
		this.totalCafe = totalCafe;
	}

	public Integer getTotalAlmoco() {
		return totalAlmoco;
	}

	public void setTotalAlmoco(Integer totalAlmoco) {
		this.totalAlmoco = totalAlmoco;
	}

	public Integer getTotalJanta() {
		return totalJanta;
	}

	public void setTotalJanta(Integer totalJanta) {
		this.totalJanta = totalJanta;
	}

	public Integer getTotalCafeEspecial() {
		return totalCafeEspecial;
	}

	public void setTotalCafeEspecial(Integer totalCafeEspecial) {
		this.totalCafeEspecial = totalCafeEspecial;
	}

	public Integer getTotalAlmocoEspecial() {
		return totalAlmocoEspecial;
	}

	public void setTotalAlmocoEspecial(Integer totalAlmocoEspecial) {
		this.totalAlmocoEspecial = totalAlmocoEspecial;
	}

	public Integer getTotalJantaEspecial() {
		return totalJantaEspecial;
	}

	public void setTotalJantaEspecial(Integer totalJantaEspecial) {
		this.totalJantaEspecial = totalJantaEspecial;
	}

	public Double getTotalValorCafe() {
		return totalValorCafe;
	}

	public void setTotalValorCafe(Double totalValorCafe) {
		this.totalValorCafe = MonetaryFormatUtil.roundTwoDecimals(totalValorCafe);
	}

	public Double getTotalValorAlmoco() {
		return totalValorAlmoco;
	}

	public void setTotalValorAlmoco(Double totalValorAlmoco) {
		this.totalValorAlmoco = MonetaryFormatUtil.roundTwoDecimals(totalValorAlmoco);
	}

	public Double getTotalValorJanta() {
		return totalValorJanta;
	}

	public void setTotalValorJanta(Double totalValorJanta) {
		this.totalValorJanta = MonetaryFormatUtil.roundTwoDecimals(totalValorJanta);
	}

	public Double getTotalValorCafeEspecial() {
		return totalValorCafeEspecial;
	}

	public void setTotalValorCafeEspecial(Double totalValorCafeEspecial) {
		this.totalValorCafeEspecial = MonetaryFormatUtil.roundTwoDecimals(totalValorCafeEspecial);
	}

	public Double getTotalValorAlmocoEspecial() {
		return totalValorAlmocoEspecial;
	}

	public void setTotalValorAlmocoEspecial(Double totalValorAlmocoEspecial) {
		this.totalValorAlmocoEspecial = MonetaryFormatUtil.roundTwoDecimals(totalValorAlmocoEspecial);
	}

	public Double getTotalValorJantaEspecial() {
		return totalValorJantaEspecial;
	}

	public void setTotalValorJantaEspecial(Double totalValorJantaEspecial) {
		this.totalValorJantaEspecial = MonetaryFormatUtil.roundTwoDecimals(totalValorJantaEspecial);
	}

	@Override
	public String toString() {
		return "MapaConsolidadoDTO [nomeUnidade=" + nomeUnidade + ", virgenciaContartoInicio=" + virgenciaContartoInicio
				+ ", virgenciaContartoFim=" + virgenciaContartoFim + ", numeroContrato=" + numeroContrato
				+ ", nomeFornecedor=" + nomeFornecedor + ", mesReferencia=" + mesReferencia + ", valorRefeicao="
				+ valorRefeicao + ", consolidadoRecuperandoDTOs=" + consolidadoRecuperandoDTOs + "]";
	}

}
