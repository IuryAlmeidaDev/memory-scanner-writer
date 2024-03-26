package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

public class HorarioRefeicaoDTO {
	
	private long id; // Id da Unidade
	private String nomeUnidade;
	private Date dataInicio;
	private Date dataFim;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNomeUnidade() {
		return nomeUnidade;
	}
	public void setNomeUnidade(String nomUnidade) {
		this.nomeUnidade = nomUnidade;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

}
