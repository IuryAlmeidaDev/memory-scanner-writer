package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;

import java.util.Arrays;
import java.util.Date;


public class TransferenciaPedidoTransitoDTO {
	
	private Long idTransferencia;
	private String unidadeOrigem;
	private Long unidadeOrigemId;
	
	private String statusStr;
	private StatusPedido status;
	
	public String getStatusStr() {
		return statusStr;
	}
	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}
	public StatusPedido getStatus() {
		return StatusPedido.valueOf(this.statusStr);
	}
	
	public void setStatus(StatusPedido status) {
		this.status = status;
	}
	public Long getIdTransferencia() {
		return idTransferencia;
	}
	public void setIdTransferencia(Long idTransferencia) {
		this.idTransferencia = idTransferencia;
	}
	public String getUnidadeOrigem() {
		return unidadeOrigem;
	}
	public void setUnidadeOrigem(String unidadeOrigem) {
		this.unidadeOrigem = unidadeOrigem;
	}
	public Long getUnidadeOrigemId() {
		return unidadeOrigemId;
	}
	public void setUnidadeOrigemId(Long unidadeOrigemId) {
		this.unidadeOrigemId = unidadeOrigemId;
	}
	private Long nr;
	private Long matricula;
	private String nome;
	private Date dataPrevista;
	private String unidadeDestino;
	

	public Long getNr() {
		return nr;
	}
	public void setNr(Long nr) {
		this.nr = nr;
	}
	public Long getMatricula() {
		return matricula;
	}
	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Date getDataPrevista() {
		return dataPrevista;
	}
	public void setDataPrevista(Date dataPrevista) {
		this.dataPrevista = dataPrevista;
	}
	public String getUnidadeDestino() {
		return unidadeDestino;
	}
	public void setUnidadeDestino(String unidadeDestino) {
		this.unidadeDestino = unidadeDestino;
	}
	

	
	
}
