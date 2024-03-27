package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.List;

import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;

import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;

public class CredenciamentoDTO {
	
	private Long id;
	
	private Long unidadeId;
	
	private StatusCredenciamento status;
	
	private FaseCredenciamento faseAtual;
	
	private List<FaseDoCredenciamentoDTO> fases;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUnidadeId() {
		return unidadeId;
	}

	public void setUnidadeId(Long unidadeId) {
		this.unidadeId = unidadeId;
	}

	public StatusCredenciamento getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = StatusCredenciamento.getValue(status);
	}

	public FaseCredenciamento getFaseAtual() {
		return faseAtual;
	}

	public void setFaseAtual(Integer faseAtual) {
		this.faseAtual = FaseCredenciamento.getValue(faseAtual);
	}

	public List<FaseDoCredenciamentoDTO> getFases() {
		return fases;
	}

	public void setFases(List<FaseDoCredenciamentoDTO> fases) {
		this.fases = fases;
	}
}
