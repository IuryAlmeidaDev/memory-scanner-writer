package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Usuario;

public class FaseDoCredenciamentoDTO {
    private Long id;
    private String observacao;
    private StatusCredenciamento statusCredenciamento;
    private FaseCredenciamento faseCredenciamento;
    private Date dataRecebimento;
    private Date dataAprovacao;
    private Date dataDespacho;
    private Date dataValidade;
    private Credenciamento credenciamento;
    private Usuario usuarioAprovacao;
    private List<DocumentoDTO> credencialDocumentos = new ArrayList<DocumentoDTO>();
    private List<CertidaoDTO> credencialCertidoes = new ArrayList<CertidaoDTO>();
    
    private Long credenciamentoId;
    private Long visitanteId;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public StatusCredenciamento getStatusCredenciamento() {
		return statusCredenciamento;
	}
	public void setStatusCredenciamento(StatusCredenciamento statusCredenciamento) {
		this.statusCredenciamento = statusCredenciamento;
	}
	public FaseCredenciamento getFaseCredenciamento() {
		return faseCredenciamento;
	}
	public void setFaseCredenciamento(FaseCredenciamento faseCredenciamento) {
		this.faseCredenciamento = faseCredenciamento;
	}
	public Date getDataRecebimento() {
		return dataRecebimento;
	}
	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}
	public Date getDataAprovacao() {
		return dataAprovacao;
	}
	public void setDataAprovacao(Date dataAprovacao) {
		this.dataAprovacao = dataAprovacao;
	}
	public Date getDataDespacho() {
		return dataDespacho;
	}
	public void setDataDespacho(Date dataDespacho) {
		this.dataDespacho = dataDespacho;
	}
	public Date getDataValidade() {
		return dataValidade;
	}
	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}
	public Credenciamento getCredenciamento() {
		return credenciamento;
	}
	public void setCredenciamento(Credenciamento credenciamento) {
		this.credenciamento = credenciamento;
	}
	public Usuario getUsuarioAprovacao() {
		return usuarioAprovacao;
	}
	public void setUsuarioAprovacao(Usuario usuarioAprovacao) {
		this.usuarioAprovacao = usuarioAprovacao;
	}
	public List<DocumentoDTO> getCredencialDocumentos() {
		return credencialDocumentos;
	}
	public void setCredencialDocumentos(List<DocumentoDTO> credencialDocumentos) {
		this.credencialDocumentos = credencialDocumentos;
	}
	public List<CertidaoDTO> getCredencialCertidoes() {
		return credencialCertidoes;
	}
	public void setCredencialCertidoes(List<CertidaoDTO> credencialCertidoes) {
		this.credencialCertidoes = credencialCertidoes;
	}
	public Long getCredenciamentoId() {
		return credenciamentoId;
	}
	public void setCredenciamentoId(Long credenciamentoId) {
		this.credenciamentoId = credenciamentoId;
	}
	public Long getVisitanteId() {
		return visitanteId;
	}
	public void setVisitanteId(Long visitanteId) {
		this.visitanteId = visitanteId;
	}
}
