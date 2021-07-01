package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "PASSAGEM_LOTACAO", schema = Sigepen.SEJUDH_SCHEMA)
public class PassagemLotacao implements Serializable {

	private static final long serialVersionUID = 6444931776829511655L;

	@SequenceGenerator(name = "SEQ_PASSAGEM_LOTACAO_GENERATOR", sequenceName = "SEQ_PASSAGEM_LOTACAO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PASSAGEM_LOTACAO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "ESTRUTURA_ID")
	private Estrutura estrutura;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ENTRADA", nullable = false)
	private Date dataEntrada;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_SAIDA")
	private Date dataSaida;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_TRANSITO")
	private TipoTransito tipoTransito;
	
	@ManyToOne(optional = false, fetch=FetchType.LAZY)
	@JoinColumn(name = "PASSAGEM_ID")
	private Passagem passagem;

	public PassagemLotacao() {
		
	}
	
	public PassagemLotacao(Passagem passagem, TipoTransito tipoTransito) {
		this.passagem = passagem;
		this.tipoTransito = tipoTransito;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Estrutura getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(Estrutura estrutura) {
		this.estrutura = estrutura;
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

	public Passagem getPassagem() {
		return passagem;
	}

	public void setPassagem(Passagem passagem) {
		this.passagem = passagem;
	}

	public TipoTransito getTipoTransito() {
		return tipoTransito;
	}

	public void setTipoTransito(TipoTransito tipoTransito) {
		this.tipoTransito = tipoTransito;
	}

	@Override
	public String toString() {
		return "PassagemLotacao [id=" + id + "]";
	}
}
