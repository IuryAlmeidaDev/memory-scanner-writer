package br.gov.mt.sejudh.sigepen.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "TRANSFERENCIA", schema = Sigepen.SEJUDH_SCHEMA)
public class Transferencia {

	@SequenceGenerator(name = "SEQ_TRANSF_GENERATOR", sequenceName = "SEQ_TRANSFERENCIA", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TRANSF_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;

	@OneToMany(mappedBy="transferencia", fetch = FetchType.LAZY)
	private List<PedidoTransito> pedidos;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "UNIDADE_ORIGEM_ID")
	private UnidadePrisional unidadeOrigem;
	
	@OneToMany(mappedBy="transferencia", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Escolta> escolta;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_PREVISTA")
	private Date dataPrevista;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INICIO")
	private Date dataInicio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CONCLUSAO")
	private Date dataConclusao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CANCELAMENTO")
	private Date dataCancelamento;
	
	@Column(name = "OBSERVACAO", length = 250)
	private String observacao;
			
	@Column(name = "RESULTADO", length = 250)
	private String resultado;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private StatusMovimentacao status;
	
	@Transient
	private AutoriaRegistro autorRegistro;
	
	@Transient
	private AutoriaRegistro autorUltimaAlteracao;
	
	public Transferencia() {
		this.pedidos = new ArrayList<PedidoTransito>();
		this.escolta = new ArrayList<Escolta>();
	}
	
	public Transferencia(StatusMovimentacao status, UnidadePrisional unidade) {
		this.unidadeOrigem = unidade;
		this.pedidos = new ArrayList<PedidoTransito>();
		this.escolta = new ArrayList<Escolta>();
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<PedidoTransito> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<PedidoTransito> pedidos) {
		this.pedidos = pedidos;
	}

	public List<Escolta> getEscolta() {
		return escolta;
	}

	public void setEscolta(List<Escolta> escolta) {
		this.escolta = escolta;
	}

	public Date getDataPrevista() {
		return dataPrevista;
	}

	public void setDataPrevista(Date dataPrevista) {
		this.dataPrevista = dataPrevista;
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

	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = Sigepen.formatarEspacos(observacao);
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = Sigepen.formatarEspacos(resultado);
	}

	public StatusMovimentacao getStatus() {
		return status;
	}

	public void setStatus(StatusMovimentacao status) {
		this.status = status;
	}	
	
	public UnidadePrisional getUnidadeOrigem() {
		return unidadeOrigem;
	}

	public void setUnidadeOrigem(UnidadePrisional unidadeOrigem) {
		this.unidadeOrigem = unidadeOrigem;
	}

	public AutoriaRegistro getAutorRegistro() {
		return autorRegistro;
	}

	public void setAutorRegistro(AutoriaRegistro autorRegistro) {
		this.autorRegistro = autorRegistro;
	}

	public AutoriaRegistro getAutorUltimaAlteracao() {
		return autorUltimaAlteracao;
	}

	public void setAutorUltimaAlteracao(AutoriaRegistro autorUltimaAlteracao) {
		this.autorUltimaAlteracao = autorUltimaAlteracao;
	}

	@Override
	public String toString() {
		return "Transferencia [id=" + id + "]";
	}	
}