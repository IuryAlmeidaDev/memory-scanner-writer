package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name="ORIENTACAO_SEXUAL", schema = Sigepen.SEJUDH_SCHEMA)
public class OrientacaoSexual implements Serializable{

	private static final long serialVersionUID = 219608097611907547L;

	@Id
	@Column(name="ID")
	private long id;
	
	@Column(name="DESCRICAO", length=80, nullable = false)
	private String descricao;

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
		OrientacaoSexual other = (OrientacaoSexual) obj;
		if (id != other.id)
			return false;
		return true;
	}

}