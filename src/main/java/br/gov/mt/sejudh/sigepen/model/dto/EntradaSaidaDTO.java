package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAutorizacaoValidacaoSemBio;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Immutable
@Entity
public class EntradaSaidaDTO {

	@Id
	private Long id;
	private String tipoVisita;
	private String nomeVisitante;
	private Long idVisitante;
	private String nomeResponsavel;
	private Long idResponsavel;
	private String parentesco;
	private String profissao;
	private String nomeRecuperando;
	private Date dataEntrada;
	private Date dataSaida;
	private Date dataCancelamento;

	@Convert(converter = EnumCustomConverter.class, disableConversion=true)
	@Type(type = "br.gov.mt.sejudh.sigepen.conversores.EnumCustomType")
	private SituacaoVisita situacao;
	private String motivoFinalizacao;
	private String motivoEntrada;
	private String motivoCancelamento;
	private Long idUnidade;
	private String nomeUnidade;
	private String siglaUnidade;

	@Transient
	private VisitanteDTO visitante;

	@Transient
	private List<AutorizacaoSemBiometriaDTO> autorizacoes;
	
	public AutorizacaoSemBiometriaDTO getAutorizacao(TipoAutorizacaoValidacaoSemBio acao) {
		Optional<AutorizacaoSemBiometriaDTO> opt = this.autorizacoes.stream().filter(a -> a.getTipoAutorizacao().equals(acao)).findFirst();
		
		if (opt.isPresent())
			return opt.get();
		else
			return null;
	}
	
	public List<AutorizacaoSemBiometriaDTO> getAutorizacoes() {
		return autorizacoes;
	}
	public void setAutorizacoes(List<AutorizacaoSemBiometriaDTO> autorizacoes) {
		this.autorizacoes = autorizacoes;
	}
	public VisitanteDTO getVisitante() {
		return visitante;
	}
	public void setVisitante(VisitanteDTO visitante) {
		this.visitante = visitante;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTipoVisita() {
		return tipoVisita;
	}
	public void setTipoVisita(String tipoVisita) {
		this.tipoVisita = tipoVisita;
	}
	public String getNomeVisitante() {
		return nomeVisitante;
	}
	public void setNomeVisitante(String nomeVisitante) {
		this.nomeVisitante = nomeVisitante;
	}
	public Long getIdVisitante() {
		return idVisitante;
	}
	public void setIdVisitante(Long idVisitante) {
		this.idVisitante = idVisitante;
	}
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}
	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
	public String getParentesco() {
		return parentesco;
	}
	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
	}
	public String getNomeRecuperando() {
		return nomeRecuperando;
	}
	public void setNomeRecuperando(String nomeRecuperando) {
		this.nomeRecuperando = nomeRecuperando;
	}
	public Date getDataEntrada() {
		return dataEntrada;
	}
	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}
	public Date getDataSaida() {
		return dataSaida;
	}
	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}
	public SituacaoVisita getSituacao() {
		return situacao;
	}
	public void setSituacao(SituacaoVisita situacao) {
		this.situacao = situacao;
	}
	public String getProfissao() {
		return profissao;
	}
	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}
	public String getMotivoFinalizacao() {
		return motivoFinalizacao;
	}
	public void setMotivoFinalizacao(String motivoFinalizacao) {
		this.motivoFinalizacao = motivoFinalizacao;
	}
	public Long getIdResponsavel() {
		return idResponsavel;
	}
	public void setIdResponsavel(Long idResponsavel) {
		this.idResponsavel = idResponsavel;
	}
	public Date getDataCancelamento() {
		return dataCancelamento;
	}
	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}
	public String getMotivoEntrada() {
		return motivoEntrada;
	}
	public void setMotivoEntrada(String motivoEntrada) {
		this.motivoEntrada = motivoEntrada;
	}
	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}
	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}
	public String getNomeUnidade() {
		return nomeUnidade;
	}
	public void setNomeUnidade(String nomeUnidade) {
		this.nomeUnidade = nomeUnidade;
	}
	public String getSiglaUnidade() {
		return siglaUnidade;
	}
	public void setSiglaUnidade(String siglaUnidade) {
		this.siglaUnidade = siglaUnidade;
	}
	public Long getIdUnidade() {
		return idUnidade;
	}
	public void setIdUnidade(Long idUnidade) {
		this.idUnidade = idUnidade;
	}
}
