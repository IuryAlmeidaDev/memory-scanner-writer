package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;

public class CertidaoDTO {
    private Long id;
    private String descricao;
    private SgpStatus status;
    private SimNao entregue;
    private SimNao obrigatorio;
    private SimNao validado;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public SgpStatus getStatus() {
		return status;
	}
	public void setStatus(SgpStatus status) {
		this.status = status;
	}
	public SimNao getEntregue() {
		return entregue;
	}
	public void setEntregue(SimNao entregue) {
		this.entregue = entregue;
	}
	public SimNao getObrigatorio() {
		return obrigatorio;
	}
	public void setObrigatorio(SimNao obrigatorio) {
		this.obrigatorio = obrigatorio;
	}
	public SimNao getValidado() {
		return validado;
	}
	public void setValidado(SimNao validado) {
		this.validado = validado;
	}
    

}
