package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "CRIME", schema = Sigepen.SEJUDH_SCHEMA)
public class Crime implements Serializable{	
	private static final long serialVersionUID = 3553305530826660461L;
			
	@SequenceGenerator(name = "SEQ_CRIME_GENERATOR", sequenceName = "SEQ_CRIME", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CRIME_GENERATOR")
	@Id
	@Column(name = "ID")
	private long id;

	@Column(name = "ID_LEGISLACAO")
	private long idLegislacao;

	@Column(name = "NOME_LEGISLACAO", columnDefinition="NVARCHAR2", nullable = false)
	private String nomeLegislacao;

	@Column(name = "ARTIGO", columnDefinition="NVARCHAR2", nullable = false)
	private String numArtigo;
	
	@Column(name = "PARAGRAFO", columnDefinition="NVARCHAR2")
	private String numParagrafo;
	
	@Column(name = "INCISO", columnDefinition="NVARCHAR2")
	private String numInciso;
	
	@Column(name = "ALINEA", columnDefinition="NVARCHAR2")
	private String numAlinea;
	
	@Column(name = "DETALHAMENTO", columnDefinition="NVARCHAR2", nullable = false)
	private String detalhamento;
	
	@Enumerated
	@Column(name = "STATUS")
	private Status status = Status.ATIVO;
	

	public Crime() {
	}		

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdLegislacao() {
		return idLegislacao;
	}

	public void setIdLegislacao(long idLegislacao) {
		this.idLegislacao = idLegislacao;
	}

	public String getNomeLegislacao() {
		return nomeLegislacao;
	}

	public void setNomeLegislacao(String nomeLegislacao) {
		this.nomeLegislacao = nomeLegislacao;
	}

	public String getNumArtigo() {
		return numArtigo;
	}

	public void setNumArtigo(String numArtigo) {
		this.numArtigo = numArtigo;
	}

	public String getNumParagrafo() {
		return numParagrafo;
	}

	public void setNumParagrafo(String numParagrafo) {
		this.numParagrafo = numParagrafo;
	}

	public String getNumInciso() {
		return numInciso;
	}

	public void setNumInciso(String numInciso) {
		this.numInciso = numInciso;
	}

	public String getNumAlinea() {
		return numAlinea;
	}

	public void setNumAlinea(String numAlinea) {
		this.numAlinea = numAlinea;
	}

	public String getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}
		
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}


	@Override
	public String toString() {
		return "Crime [id=" + id + ", nome=" + detalhamento + "]";
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
		Crime other = (Crime) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}