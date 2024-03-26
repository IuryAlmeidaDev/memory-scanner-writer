package br.gov.mt.sejudh.sigepen.model.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MapaConsolidadoRecuperandoDTO {
	
	private Integer numero;
	private String nomeRecuperando;
	private String matriculaRecuperando;
	private Date entradaRecuperando;
	private Date saidaRecuperando;
	private Integer diasRecuperando;
	private Long horarioEntrada;
	private Long horarioEspecialEntrada;
	private Long horarioSaida;
	private Long horarioEspecialSaida;
	private Integer diasRecuperandoNormal;
	private Integer diasRecuperandoEspecial;
	private Double cafeManha = 0D;
	private Double almoco = 0D;
	private Double janta = 0D;
	private Double cafeManhaEspecial = 0D;
	private Double almocoEspecial = 0D;
	private Double jantaEspecial = 0D;
	private Double soma = 0D;
	private Integer diasRecuperandoCafeNormal = 0;
	private Integer diasRecuperandoCafeEspecial = 0;
	private Integer diasRecuperandoAlmocoNormal= 0;
	private Integer diasRecuperandoAlmocoEspecial= 0;
	private Integer diasRecuperandoJantaNormal= 0;
	private Integer diasRecuperandoJantaEspecial= 0;
	
	public MapaConsolidadoRecuperandoDTO() {
		
	}

	public MapaConsolidadoRecuperandoDTO(String nomeUnidade, Date virgenciaContartoInicio, Date virgenciaContartoFim,
			String numeroContrato, String nomeFornecedor, String mesReferencia, String valorRefeicao,
			String nomeRecuperando, String matriculaRecuperando, Date entradaRecuperando, Date saidaRecuperando,
			Integer diasRecuperando, Double cafeManha, Double almoco, Double janta, Double cafeManhaEspecial,
			Double almocoEspecial, Double jantaEspecial, Double soma) {
		this.nomeRecuperando = nomeRecuperando;
		this.matriculaRecuperando = matriculaRecuperando;
		this.entradaRecuperando = entradaRecuperando;
		this.saidaRecuperando = saidaRecuperando;
		this.diasRecuperandoNormal = diasRecuperando;
		this.cafeManha = cafeManha;
		this.almoco = almoco;
		this.janta = janta;
		this.cafeManhaEspecial = cafeManhaEspecial;
		this.almocoEspecial = almocoEspecial;
		this.jantaEspecial = jantaEspecial;
		this.soma = soma;
	}
	
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getNomeRecuperando() {
		return nomeRecuperando;
	}

	public void setNomeRecuperando(String nomeRecuperando) {
		this.nomeRecuperando = nomeRecuperando;
	}

	public String getMatriculaRecuperando() {
		return matriculaRecuperando;
	}

	public void setMatriculaRecuperando(String matriculaRecuperando) {
		this.matriculaRecuperando = matriculaRecuperando;
	}
	
	public String getEntradaRecuperando() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.format(entradaRecuperando);
	}

	public void setEntradaRecuperando(Date entradaRecuperando) {
		this.entradaRecuperando = entradaRecuperando;
	}
	
	public String getSaidaRecuperando() {
		if(saidaRecuperando == null) {
			return "  / / 00:00";	
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.format(saidaRecuperando);
	}

	public void setSaidaRecuperando(Date saidaRecuperando) {
		this.saidaRecuperando = saidaRecuperando;
	}
	
	public Integer getDiasRecuperando() {
		return diasRecuperando;
	}

	public void setDiasRecuperando(Integer diasRecuperando) {
		this.diasRecuperando = diasRecuperando;
	}

	public Integer getDiasRecuperandoNormal() {
		return diasRecuperandoNormal;
	}

	public void setDiasRecuperandoNormal(Integer diasRecuperandoNormal) {
		this.diasRecuperandoNormal = diasRecuperandoNormal;
	}
	
	public void calculoDiasRecuperandoNormal(Integer diasRecuperando) {
		this.diasRecuperandoNormal = diasRecuperando - this.diasRecuperandoEspecial;
	}
	
	
	public Integer getDiasRecuperandoEspecial() {
		return diasRecuperandoEspecial;
	}

	public void setDiasRecuperandoEspecial(Integer diasRecuperandoEspecial) {
		this.diasRecuperandoEspecial = diasRecuperandoEspecial;
	}
	
	public Long getHorarioEntrada() {
		return this.horarioEntrada;
	}
	
	public void setHorarioEntrada(Long horarioEntrada) {
		this.horarioEntrada = horarioEntrada;
	}
	public Long getHorarioEspecialEntrada() {
		return this.horarioEspecialEntrada;
	}
	
	public void setHorarioEspecialEntrada(Long horarioEspecialEntrada) {
		this.horarioEspecialEntrada = horarioEspecialEntrada;
	}
	
	public Long getHorarioSaida() {
		return this.horarioSaida;
	}
	
	public void setHorarioSaida(Long horarioSaida) {
		this.horarioSaida = horarioSaida;
	}
	
	public Long getHorarioEspecialSaida() {
		return this.horarioEspecialSaida;
	}
	
	public void setHorarioEspecialSaida(Long horarioEspecialSaida) {
		this.horarioEspecialSaida = horarioEspecialSaida;
	}

	public Double getCafeManha() {
		return cafeManha;
	}

	public void setCafeManha(Double cafeManha) {
		this.cafeManha = cafeManha;
	}

	public Double getAlmoco() {
		return almoco;
	}

	public void setAlmoco(Double almoco) {
		this.almoco = almoco;
	}

	public Double getJanta() {
		return janta;
	}

	public void setJanta(Double janta) {
		this.janta = janta;
	}

	public Double getCafeManhaEspecial() {
		return cafeManhaEspecial;
	}

	public void setCafeManhaEspecial(Double cafeManhaEspecial) {
		this.cafeManhaEspecial = cafeManhaEspecial;
	}

	public Double getAlmocoEspecial() {
		return almocoEspecial;
	}

	public void setAlmocoEspecial(Double almocoEspecial) {
		this.almocoEspecial = almocoEspecial;
	}

	public Double getJantaEspecial() {
		return jantaEspecial;
	}

	public void setJantaEspecial(Double jantaEspecial) {
		this.jantaEspecial = jantaEspecial;
	}

	public Double getSoma() {
		return soma;
	}

	public void setSoma(Double soma) {
		this.soma = soma;
	}
	
	public Integer getDiasRecuperandoCafeNormal() {
		return diasRecuperandoCafeNormal;
	}

	public void setDiasRecuperandoCafeNormal(Integer diasRecuperandoCafeNormal) {
		this.diasRecuperandoCafeNormal = diasRecuperandoCafeNormal;
	}

	public Integer getDiasRecuperandoCafeEspecial() {
		return diasRecuperandoCafeEspecial;
	}

	public void setDiasRecuperandoCafeEspecial(Integer diasRecuperandoCafeEspecial) {
		this.diasRecuperandoCafeEspecial = diasRecuperandoCafeEspecial;
	}

	public Integer getDiasRecuperandoAlmocoNormal() {
		return diasRecuperandoAlmocoNormal;
	}

	public void setDiasRecuperandoAlmocoNormal(Integer diasRecuperandoAlmocoNormal) {
		this.diasRecuperandoAlmocoNormal = diasRecuperandoAlmocoNormal;
	}

	public Integer getDiasRecuperandoAlmocoEspecial() {
		return diasRecuperandoAlmocoEspecial;
	}

	public void setDiasRecuperandoAlmocoEspecial(Integer diasRecuperandoAlmocoEspecial) {
		this.diasRecuperandoAlmocoEspecial = diasRecuperandoAlmocoEspecial;
	}

	public Integer getDiasRecuperandoJantaNormal() {
		return diasRecuperandoJantaNormal;
	}

	public void setDiasRecuperandoJantaNormal(Integer diasRecuperandoJantaNormal) {
		this.diasRecuperandoJantaNormal = diasRecuperandoJantaNormal;
	}

	public Integer getDiasRecuperandoJantaEspecial() {
		return diasRecuperandoJantaEspecial;
	}

	public void setDiasRecuperandoJantaEspecial(Integer diasRecuperandoJantaEspecial) {
		this.diasRecuperandoJantaEspecial = diasRecuperandoJantaEspecial;
	}

	public void calculoSoma() {
		double soma = this.cafeManha + this.cafeManhaEspecial + this.almoco + this.almocoEspecial + this.janta + this.jantaEspecial;
		this.setSoma(soma);
	}

	@Override
	public String toString() {
		return "MapaConsolidadoRecuperandoDTO [nomeRecuperando=" + nomeRecuperando + ", matriculaRecuperando="
				+ matriculaRecuperando + ", entradaRecuperando=" + entradaRecuperando + ", saidaRecuperando="
				+ saidaRecuperando + ", diasRecuperando=" + diasRecuperandoNormal + ", cafeManha=" + cafeManha + ", almoco="
				+ almoco + ", janta=" + janta + ", cafeManhaEspecial=" + cafeManhaEspecial + ", almocoEspecial="
				+ almocoEspecial + ", jantaEspecial=" + jantaEspecial + ", soma=" + soma + "]";
	}

}
