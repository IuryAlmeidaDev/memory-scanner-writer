package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.DiasSemana;

public class CardapioDTO {

	private long id;
	private Long semana;
	private DiasSemana diaSemana;
	private String nomeRefeicao;
	private String nomeDieta;
	private Boolean festivo;
	
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

	@Override
	public String toString() {
		return "CardapioDAO [id=" + id + ", semana=" + semana + ", diaSemana=" + diaSemana + ", nomeRefeicao="
				+ nomeRefeicao + "]";
	}
}
