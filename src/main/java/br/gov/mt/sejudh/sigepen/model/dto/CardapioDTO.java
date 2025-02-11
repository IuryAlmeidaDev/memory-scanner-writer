package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.DiasSemana;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CardapioDTO {

	private long id;
	private Long semana;
	private DiasSemana diaSemana;
	private String nomeRefeicao;
	private String nomeDieta;
	private Boolean festivo;
	private String nomeUnidadePrisional;
	private String numContrato;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Long getSemana() {
		return semana;
	}
	public void setSemana(Long semana) {
		this.semana = semana;
	}
	public DiasSemana getDiaSemana() {
		return diaSemana;
	}
	public void setDiaSemana(DiasSemana diaSemana) {
		this.diaSemana = diaSemana;
	}
	public String getNomeRefeicao() {
		return nomeRefeicao;
	}
	public void setNomeRefeicao(String nomeRefeicao) {
		this.nomeRefeicao = nomeRefeicao;
	}
	public Boolean getFestivo() {
		return festivo;
	}
	public void setFestivo(Boolean festivo) {
		this.festivo = festivo;
	}
	public String getNomeDieta() {
		return nomeDieta;
	}
	public void setNomeDieta(String nomeDieta) {
		this.nomeDieta = nomeDieta;
	}
	public String getNomeUnidadePrisional() {
		return nomeUnidadePrisional;
	}
	public void setNomeUnidadePrisional(String nomeUnidadePrisional) {
		this.nomeUnidadePrisional = nomeUnidadePrisional;
	}
	public String getNumContrato() {
		return numContrato;
	}
	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("id", id)
				.append("semana", semana)
				.append("diaSemana", diaSemana)
				.append("nomeRefeicao", nomeRefeicao)
				.append("nomeDieta", nomeDieta)
				.append("festivo", festivo)
				.append("nomeUnidadePrisional", nomeUnidadePrisional)
				.append("numContrato", numContrato)
				.toString();
	}
}
