package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRemanejamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.model.Foto;


public class RemanejamentoDTO {


	private long id;
	
	private long pedidoId;

	private long recId;
	
	private String recuperando;	
	
	private Date dataInicio;
	
	private Date dataConclusao;
	
	private String unidadeOrigem;
	
	private String observacao;
	
	private String nomeMaeRecuperando;
	
	private String numeroDocumento;
	
	private Date dataDocumento;
	
	private String nomeUnidadeDestino;
	
	private String motivo;
	
	private Foto foto;
	
	private String resultado;
	
	private String unidadeRecuperando;
	
	private StatusMovimentacao status;

	private TipoTransito tipoTransito;
	
	private FiltroRemanejamento filtro;

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
	
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Override
	public String toString() {
		return "Remanejamento [id=" + id + "]";
	}

	public Date getDataDocumento() {
		return dataDocumento;
	}

	public void setDataDocumento(Date dataDocumento) {
		this.dataDocumento = dataDocumento;
	}

	public String getNomeMaeRecuperando() {
		return nomeMaeRecuperando;
	}

	public void setNomeMaeRecuperando(String nomeMaeRecuperando) {
		this.nomeMaeRecuperando = nomeMaeRecuperando;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Foto getFoto() {
		return foto;
	}

	public void setFoto(Foto foto) {
		this.foto = foto;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	public String getNomeUnidadeDestino() {
		return nomeUnidadeDestino;
	}

	public void setNomeUnidadeDestino(String nomeUnidadeDestino) {
		this.nomeUnidadeDestino = nomeUnidadeDestino;
	}

	public String getUnidadeRecuperando() {
		return unidadeRecuperando;
	}

	public void setUnidadeRecuperando(String unidadeRecuperando) {
		this.unidadeRecuperando = unidadeRecuperando;
	}

	public FiltroRemanejamento getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroRemanejamento filtro) {
		this.filtro = filtro;
	}
}