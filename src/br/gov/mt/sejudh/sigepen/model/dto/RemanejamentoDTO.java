package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;


public class RemanejamentoDTO {


	private long id;
	
	private long pedidoId;

	private long recId;
	
	private String recuperando;	
	
	private Date dataInicio;
	
	private Date dataConclusao;
	
	private String unidadeOrigem;
	
	private StatusMovimentacao status;

	private TipoTransito tipoTransito;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(long pedidoId) {
		this.pedidoId = pedidoId;
	}

	public long getRecId() {
		return recId;
	}

	public void setRecId(long recId) {
		this.recId = recId;
	}

	public String getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(String recuperando) {
		this.recuperando = recuperando;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataConclusao() {
		return dataConclusao;
	}

	public void setDataConclusao(Date dataConclusao) {
		this.dataConclusao = dataConclusao;
	}

	public StatusMovimentacao getStatus() {
		return status;
	}

	public void setStatus(StatusMovimentacao status) {
		this.status = status;
	}

	public String getUnidadeOrigem() {
		return unidadeOrigem;
	}

	public void setUnidadeOrigem(String unidadeOrigem) {
		this.unidadeOrigem = unidadeOrigem;
	}

	public TipoTransito getTipoTransito() {
		return tipoTransito;
	}

	public void setTipoTransito(TipoTransito tipoTransito) {
		this.tipoTransito = tipoTransito;
	}

	@Override
	public String toString() {
		return "Remanejamento [id=" + id + "]";
	}

		
}