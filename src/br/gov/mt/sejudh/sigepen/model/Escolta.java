package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "ESCOLTA", schema = Sigepen.SEJUDH_SCHEMA)
public class Escolta {

	@SequenceGenerator(name = "SEQ_ESCOLTA_GENERATOR", sequenceName = "SEQ_ESCOLTA", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ESCOLTA_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;
	
	@ManyToOne
	@JoinColumn(name = "SERVIDOR_ID")
	private Servidor servidor;

	@Column(name = "NOME", length = 80)
	private String nome;

	@Column(name = "MATRICULA", length = 10)
	private Long matricula;
	
	@Column(name = "CPF", length = 11)
	private String cpf;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INCLUSAO")
	private Date dataInclusao;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "MOV_INTERNA_ID")
	private MovimentacaoInterna movimentacaoInterna;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "TRANSFERENCIA_ID")
	private Transferencia transferencia;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "MOV_EXTERNA_ID")
	private MovimentacaoExterna movimentacaoExterna;
	

	public Escolta() {
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public Servidor getServidor() {
		return servidor;
	}


	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}


	public Long getMatricula() {
		return matricula;
	}


	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}


	public String getCpf() {
		return cpf;
	}


	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}


	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}


	public MovimentacaoInterna getMovimentacaoInterna() {
		return movimentacaoInterna;
	}

	public void setMovimentacaoInterna(MovimentacaoInterna movimentacaoInterna) {
		this.movimentacaoInterna = movimentacaoInterna;
	}


	public Transferencia getTransferencia() {
		return transferencia;
	}


	public void setTransferencia(Transferencia transferencia) {
		this.transferencia = transferencia;
	}


	public MovimentacaoExterna getMovimentacaoExterna() {
		return movimentacaoExterna;
	}


	public void setMovimentacaoExterna(MovimentacaoExterna movimentacaoExterna) {
		this.movimentacaoExterna = movimentacaoExterna;
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
		Escolta other = (Escolta) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Escolta [id=" + id + "]";
	}
		
}