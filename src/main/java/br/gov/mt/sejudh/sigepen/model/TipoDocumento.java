package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(schema=Sigepen.SEJUDH_SCHEMA, name="TIPO_DOCUMENTO")
public class TipoDocumento implements Serializable{

	private static final long serialVersionUID = 219608097611907547L;

	@Id
	@Column(name="ID")
	private long id;
	
	@Column(name = "TEM_FOTO", nullable = false)
	private Boolean possuiFoto;
	
	@Column(name="DESCRICAO", columnDefinition="NVARCHAR2")
	private String descricao;
	
	@Column(name="STATUS", columnDefinition="CHAR")
	private String status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = Sigepen.formatarEspacos(descricao);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getPossuiFoto() {
		return possuiFoto;
	}

	public void setPossuiFoto(Boolean possuiFoto) {
		this.possuiFoto = possuiFoto;
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
		TipoDocumento other = (TipoDocumento) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}