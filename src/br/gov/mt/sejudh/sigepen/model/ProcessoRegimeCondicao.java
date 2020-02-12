package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.enumerator.TipoProcesso;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "PROCESSO_REGIME_CONDICAO", schema = Sigepen.SEJUDH_SCHEMA)
public class ProcessoRegimeCondicao implements Serializable {

	private static final long serialVersionUID = -7079631233903409761L;

	@Id
	@SequenceGenerator(name = "SEQ_REGIME_CONDICAO_PROCESSO_GENERATOR", sequenceName = "SEQ_REGIME_CONDICAO_PROCESSO", initialValue = 1,  allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REGIME_CONDICAO_PROCESSO_GENERATOR")
	@Column(name = "ID")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROCESSO_ID")
	private Processo processo;
	
	@Enumerated(EnumType.STRING)
	@Column(name="TIPO_PROCESSO")
	private TipoProcesso tipoProcesso;
	
	@Column(name="NUMERO_DOCUMENTO", length = 80)
	private String numeroDocumento;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_DOCUMENTO", nullable = false)
	private Date dataDocumento;
	
	@Column(name = "OBSERVACAO")
	private String observacao;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@OneToOne
	@JoinColumn(name = "REGIME_ID")
	private Regime regime;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "CONDICAO")
	private Condicao condicao;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "MODALIDADE")
	private Modalidade modalidade;
	
	@Column(name="DATA_SOLTURA")
	@Temporal(TemporalType.DATE)
	private Date dataSoltura;
	
	public String getSituacaoTemporaria() {		
		Integer resultado = validaVencimentoDocumento();
		
		if (resultado == null)
			return null;
		
		if (resultado == 0) {
			return "Vence hoje";
		} else if (resultado < 0) {
			return "Vencido";
		} else if (resultado > 0) {
			return "A vencer";
		} else {
			return null;
		}
	}
	
	public Integer validaVencimentoDocumento() {
		if (dataSoltura == null)
			return null;
		
		Date dataHoje = DateUtils.truncate(Calendar.getInstance().getTime(), Calendar.DATE);
		Date dataDaSoltura = DateUtils.truncate(dataSoltura, Calendar.DATE);
		
		return dataDaSoltura.compareTo(dataHoje);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public TipoProcesso getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(TipoProcesso tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

	public Date getDataDocumento() {
		return dataDocumento;
	}

	public void setDataDocumento(Date dataDocumento) {
		this.dataDocumento = dataDocumento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = Sigepen.formatarEspacos(observacao);
	}

	public Regime getRegime() {
		return regime;
	}

	public void setRegime(Regime regime) {
		this.regime = regime;
	}

	public Condicao getCondicao() {
		return condicao;
	}
	
	public void setCondicao(Condicao condicao) {
		this.condicao = condicao;
	}

	public Modalidade getModalidade() {
		return modalidade;
	}

	public void setModalidade(Modalidade modalidade) {
		this.modalidade = modalidade;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	
	public Date getDataSoltura() {
		return dataSoltura;
	}

	public void setDataSoltura(Date dataSoltura) {
		this.dataSoltura = dataSoltura;
	}

	@Override
	public String toString() {
		return "Regime Condicao [id=" + id + ", processo="+ processo + "]";
	}
}
