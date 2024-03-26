package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name = "CRIME", schema = Sigepen.SEJUDH_SCHEMA)
public class Crime {
	
	@Id
	@Column(name = "ID")
	private long id;

	@Column(name = "ID_LEGISLACAO")
	private long idLegislacao;

	@Column(name = "NOME_LEGISLACAO", columnDefinition="NVARCHAR2")
	private String nomeLegislacao;

	@Column(name = "ARTIGO", columnDefinition="NVARCHAR2")
	private String numArtigo;
	
	@Column(name = "PARAGRAFO", columnDefinition="NVARCHAR2")
	private String numParagrafo;
	
	@Column(name = "INCISO", columnDefinition="NVARCHAR2")
	private String numInciso;
	
	@Column(name = "ALINEA", columnDefinition="NVARCHAR2")
	private String numAlinea;
	
	@Column(name = "DETALHAMENTO", columnDefinition="NVARCHAR2")
	private String detalhamento;	
	
	public Crime() {
	}		

	public long getId() {
		return id;
	}

	public long getIdLegislacao() {
		return idLegislacao;
	}

	public String getNomeLegislacao() {
		return nomeLegislacao;
	}

	public String getNumArtigo() {
		return numArtigo;
	}

	public String getNumParagrafo() {
		return numParagrafo;
	}

	public String getNumInciso() {
		return numInciso;
	}

	public String getNumAlinea() {
		return numAlinea;
	}

	public String getDetalhamento() {
		return detalhamento;
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