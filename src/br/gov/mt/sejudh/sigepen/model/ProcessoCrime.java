package br.gov.mt.sejudh.sigepen.model;

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
import javax.persistence.Version;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "PROCESSO_CRIME", schema = Sigepen.SEJUDH_SCHEMA)
public class ProcessoCrime {

	@SequenceGenerator(name = "SEQ_PROCESSO_CRIME_GENERATOR", sequenceName = "SEQ_PROCESSO_CRIME", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PROCESSO_CRIME_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Version
	private Long versao;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "PROCESSO_ID")
	private Processo processo;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "CRIME_ID")
	private Crime crime;
	
	@Column(name="DATA_INCLUSAO")
	@Temporal(TemporalType.DATE)
	private Date dataInclusao;
	
	@Column(name = "MOTIVO", length = 250)
	private String motivo;

	@Enumerated
	@Column(name = "STATUS")
	private Status status;	

	public ProcessoCrime() {
	}
	
	public ProcessoCrime(Status status) {
		this.setStatus(status);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public Crime getCrime() {
		return crime;
	}

	public void setCrime(Crime crime) {
		this.crime = crime;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = Sigepen.formatarEspacos(motivo);
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
		result = prime * result + ((crime == null) ? 0 : crime.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((processo == null) ? 0 : processo.hashCode());
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
		ProcessoCrime other = (ProcessoCrime) obj;
		if (crime == null) {
			if (other.crime != null)
				return false;
		} else if (!crime.equals(other.crime))
			return false;
		if (id != other.id)
			return false;
		if (processo == null) {
			if (other.processo != null)
				return false;
		} else if (!processo.equals(other.processo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Processo Crime [id=" + id + ", processo=" + processo + "]";
	}

		
	
}