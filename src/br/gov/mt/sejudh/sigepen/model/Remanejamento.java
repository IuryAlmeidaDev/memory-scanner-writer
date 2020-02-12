package br.gov.mt.sejudh.sigepen.model;

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

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "REMANEJAMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class Remanejamento {

	@SequenceGenerator(name = "SEQ_REMANEJAMENTO_GENERATOR", sequenceName = "SEQ_REMANEJAMENTO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REMANEJAMENTO_GENERATOR")
	@Column(name = "ID")
	private long id;

	@ManyToOne
	@JoinColumn(name = "PEDIDO_ID")
	private PedidoTransito pedido;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "UNIDADE_REMANEJADA_ID")
	private UnidadePrisional unidadeRemanejada;
	
	@ManyToOne
	@JoinColumn(name = "ESTRUTURA_ORIGEM_ID")
	private Estrutura estruturaOrigem;	
	
	@ManyToOne
	@JoinColumn(name = "ESTRUTURA_DESTINO_ID")
	private Estrutura estruturaDestino;
	
	@ManyToOne
	@JoinColumn(name = "FOTO_ID")
	private Foto foto;	
		
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INICIO")
	private Date dataInicio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CONCLUSAO")
	private Date dataConclusao;
	
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
	
	public Remanejamento() {
	}
	
	public Remanejamento(StatusMovimentacao status, UnidadePrisional unidade) {
		this.unidadeRemanejada = unidade;
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PedidoTransito getPedido() {
		return pedido;
	}

	public void setPedido(PedidoTransito pedido) {
		this.pedido = pedido;
	}
	
	public UnidadePrisional getUnidadeRemanejada() {
		return unidadeRemanejada;
	}

	public void setUnidadeRemanejada(UnidadePrisional unidadeRemanejada) {
		this.unidadeRemanejada = unidadeRemanejada;
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
	
	public Estrutura getEstruturaOrigem() {
		return estruturaOrigem;
	}

	public void setEstruturaOrigem(Estrutura estruturaOrigem) {
		this.estruturaOrigem = estruturaOrigem;
	}

	public Estrutura getEstruturaDestino() {
		return estruturaDestino;
	}

	public void setEstruturaDestino(Estrutura estruturaDestino) {
		this.estruturaDestino = estruturaDestino;
	}

	public Foto getFoto() {
		return foto;
	}

	public void setFoto(Foto foto) {
		this.foto = foto;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((estruturaOrigem == null) ? 0 : estruturaOrigem.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((pedido == null) ? 0 : pedido.hashCode());
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
		Remanejamento other = (Remanejamento) obj;
		if (estruturaOrigem == null) {
			if (other.estruturaOrigem != null)
				return false;
		} else if (!estruturaOrigem.equals(other.estruturaOrigem))
			return false;
		if (id != other.id)
			return false;
		if (pedido == null) {
			if (other.pedido != null)
				return false;
		} else if (!pedido.equals(other.pedido))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Remanejamento [id=" + id + "]";
	}
	
	
}