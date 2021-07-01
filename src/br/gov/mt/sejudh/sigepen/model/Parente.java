package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "PARENTE", schema = Sigepen.SEJUDH_SCHEMA)
public class Parente implements Serializable {

	private static final long serialVersionUID = -2036826051154304793L;

	@SequenceGenerator(name = "SEQ_PARENTE_GENERATOR", sequenceName = "SEQ_PARENTE", initialValue = 1,  allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PARENTE_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;

	@Column(name = "NOME", length = 80, nullable = false)
	private String nome;
		
	@Column(name = "DATA_NASCIMENTO")
	@Temporal(TemporalType.DATE)
	private Date dataNascimento;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name="PARENTESCO_ID")
	private Parentesco parentesco;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;
	
	@Column(name = "JUDICIAL")
	private boolean judicial;
	
	@Enumerated
	@Column(name = "STATUS")
	private Status status;

	public Parente() {
		this.status = Status.ATIVO;
	}	
	

	public Parente(Parente parente) {
		this.id = parente.getId();
		this.setNome(parente.getNome());
		this.dataNascimento = parente.getDataNascimento();
		this.parentesco = parente.getParentesco();
		this.recuperando = parente.getRecuperando();
		this.status = parente.getStatus();
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Parentesco getParentesco() {
		return parentesco;
	}

	public void setParentesco(Parentesco parentesco) {
		this.parentesco = parentesco;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public boolean isJudicial() {
		return judicial;
	}

	public void setJudicial(boolean judicial) {
		this.judicial = judicial;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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
		Parente other = (Parente) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Parente [id=" + id + ", nome=" + this.getNome() + "]";
	}
}
