package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.enumerator.StatusProcesso;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "PROCESSO", schema = Sigepen.SEJUDH_SCHEMA)
public class Processo implements Serializable{

	private static final long serialVersionUID = -6212296100996425071L;

	@SequenceGenerator(name = "SEQ_PROCESSO_GENERATOR", sequenceName = "SEQ_PROCESSO", allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PROCESSO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;
	
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;

	@Column(name = "NUMERO_PROCESSO", length = 80)
	private String numeroProcesso;
	
	@Column(name = "NUMERO_INQUERITO", length = 80)
	private String numeroInquerito;
	
	@Column(name = "NUMERO_OFICIO", length = 80)
	private String numeroOficio;
	
	@Column(name = "ANO")
	private Long ano;
	
	@Column(name = "MES")
	private Long mes;
	
	@Column(name = "DIA")
	private Long dia;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="processo")
	private List<ProcessoCrime> crimes;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS_PROCESSO")
	private StatusProcesso statusProcesso;		
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="processo", fetch=FetchType.EAGER)
	private List<ProcessoRegimeCondicao> processosRegimeCondicao;
	
	@Column(name="DATA_FINALIZADO")
	@Temporal(TemporalType.DATE)
	private Date dataFinalizado;
	
	@Column(name = "OBSERVACAO_FINALIZADO", length = 250)
	private String observacaoFinalizado;
	
	@Transient
	private Regime regimeAtual;
	
	@Transient
	private Condicao condicaoAtual;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "MODALIDADE")
	private Modalidade modalidade;
	
	@Transient
	private ProcessoRegimeCondicao ultimoAdicionado;
	
	public Processo(){
		crimes = new ArrayList<ProcessoCrime>();
		processosRegimeCondicao = new ArrayList<ProcessoRegimeCondicao>();
	}	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	public String getNumeroInquerito() {
		return numeroInquerito;
	}

	public void setNumeroInquerito(String numeroInquerito) {
		this.numeroInquerito = numeroInquerito;
	}

	public String getNumeroOficio() {
		return numeroOficio;
	}

	public void setNumeroOficio(String numeroOficio) {
		this.numeroOficio = numeroOficio;
	}

	public Long getAno() {
		return ano;
	}

	public void setAno(Long ano) {
		this.ano = ano;
	}

	public Long getMes() {
		return mes;
	}

	public void setMes(Long mes) {
		this.mes = mes;
	}

	public Long getDia() {
		return dia;
	}

	public void setDia(Long dia) {
		this.dia = dia;
	}

	public List<ProcessoCrime> getCrimes() {
		return crimes;
	}

	public void setCrimes(List<ProcessoCrime> crimes) {
		this.crimes = crimes;
	}

	public StatusProcesso getStatusProcesso() {
		return statusProcesso;
	}

	public void setStatusProcesso(StatusProcesso statusProcesso) {
		this.statusProcesso = statusProcesso;
	}

	public List<ProcessoRegimeCondicao> getProcessosRegimeCondicao() {
		return processosRegimeCondicao;
	}

	public void setProcessosRegimeCondicao(
			List<ProcessoRegimeCondicao> processosRegimeCondicao) {
		this.processosRegimeCondicao = processosRegimeCondicao;
	}

	public Date getDataFinalizado() {
		return dataFinalizado;
	}

	public void setDataFinalizado(Date dataFinalizado) {
		this.dataFinalizado = dataFinalizado;
	}

	public String getObservacaoFinalizado() {
		return observacaoFinalizado;
	}

	public void setObservacaoFinalizado(String observacaoFinalizado) {
		this.observacaoFinalizado = observacaoFinalizado;
	}

	public Regime getRegimeAtual() {
		configuraUltmimoRegimeCondicao();
		
		return this.regimeAtual;
	}

	public void setRegimeAtual(Regime regimeAtual) {
		this.regimeAtual = regimeAtual;
	}

	public Condicao getCondicaoAtual() {
		configuraUltmimoRegimeCondicao();
		
		return this.condicaoAtual;
	}
	
	public Estabelecimento getEstabelecimentoAtual() {
		this.processosRegimeCondicao.sort(Comparator.comparing(ProcessoRegimeCondicao::getId, Comparator.nullsFirst(Comparator.reverseOrder())));
		Optional<ProcessoRegimeCondicao> optEsfera = this.processosRegimeCondicao.stream().findFirst();
		
		if (optEsfera.isPresent()) {
			ProcessoRegimeCondicao processoRegimeCondicao = optEsfera.get();
			
			return processoRegimeCondicao.getEstabelecimento();
		}
		
		return null;
	}

	private void configuraUltmimoRegimeCondicao() {
		this.processosRegimeCondicao.sort(Comparator.comparing(ProcessoRegimeCondicao::getId, Comparator.nullsFirst(Comparator.reverseOrder())));
		Optional<ProcessoRegimeCondicao> optCondicao = this.processosRegimeCondicao.stream().findFirst();
		
		if (optCondicao.isPresent()) {
			ProcessoRegimeCondicao processoRegimeCondicao = optCondicao.get();
			
			setRegimeAtual(processoRegimeCondicao.getRegime());
			setCondicaoAtual(processoRegimeCondicao.getCondicao());
		}
	}

	public void setCondicaoAtual(Condicao condicaoAtual) {
		this.condicaoAtual = condicaoAtual;
	}

	public ProcessoRegimeCondicao getUltimoAdicionado() {
		return ultimoAdicionado;
	}

	public void setUltimoAdicionado(ProcessoRegimeCondicao ultimoAdicionado) {
		this.ultimoAdicionado = ultimoAdicionado;
	}

	public Modalidade getModalidade() {
		return modalidade;
	}

	public void setModalidade(Modalidade modalidade) {
		this.modalidade = modalidade;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Processo other = (Processo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Processo [id=" + id + "]";
	}

}
