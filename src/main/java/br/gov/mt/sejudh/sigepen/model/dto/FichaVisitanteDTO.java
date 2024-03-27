package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;

public class FichaVisitanteDTO {
	private TipoVisita tipoVisita;
	private VisitanteDTO visitante;
	private Visitante visitanteEntity;
	private List<RecuperandoView> recuperando = new ArrayList<RecuperandoView>();
	private List<DocumentoDTO> documentos = new ArrayList<DocumentoDTO>();
	private List<EnderecoDTO> enderecos = new ArrayList<EnderecoDTO>();
	private List<CertidaoDTO> certidoes = new ArrayList<CertidaoDTO>();
	private List<ContatoDTO> contatos = new ArrayList<ContatoDTO>();
	private List<FaseDoCredenciamentoDTO> credenciamentos = new ArrayList<FaseDoCredenciamentoDTO>();
	private Date dataValidade;
	
	public VisitanteDTO getVisitante() {
		return visitante;
	}
	public void setVisitante(VisitanteDTO visitante) {
		this.visitante = visitante;
	}
	public List<DocumentoDTO> getDocumentos() {
		return documentos;
	}
	public void setDocumentos(List<DocumentoDTO> documentos) {
		this.documentos = documentos;
	}
	public List<EnderecoDTO> getEnderecos() {
		return enderecos;
	}
	public void setEnderecos(List<EnderecoDTO> enderecos) {
		this.enderecos = enderecos;
	}
	public List<CertidaoDTO> getCertidoes() {
		return certidoes;
	}
	public void setCertidoes(List<CertidaoDTO> certidoes) {
		this.certidoes = certidoes;
	}
	public List<ContatoDTO> getContatos() {
		return contatos;
	}
	public void setContatos(List<ContatoDTO> contatos) {
		this.contatos = contatos;
	}
	public List<FaseDoCredenciamentoDTO> getCredenciamentos() {
		return credenciamentos;
	}
	public void setCredenciamentos(List<FaseDoCredenciamentoDTO> credenciamentos) {
		this.credenciamentos = credenciamentos;
	}
	public List<RecuperandoView> getRecuperando() {
		return recuperando;
	}
	public void setRecuperando(List<RecuperandoView> recuperando) {
		this.recuperando = recuperando;
	}
	public TipoVisita getTipoVisita() {
		return tipoVisita;
	}
	public void setTipoVisita(TipoVisita tipoVisita) {
		this.tipoVisita = tipoVisita;
	}
	public Date getDataValidade() {
		return dataValidade;
	}
	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}
	public Visitante getVisitanteEntity() {
		return visitanteEntity;
	}
	public void setVisitanteEntity(Visitante visitanteEntity) {
		this.visitanteEntity = visitanteEntity;
	}
	
	
	
	
}
