package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;


@Entity
@Table(name = "ESTADO", schema = Sigepen.SEJUDH_SCHEMA)
public class Estado implements Serializable {

	private static final long serialVersionUID = 2721233435072908140L;

		
	@Column(name="NOME", columnDefinition="NVARCHAR2")
	private String nome;
	
	@Id
	@Column(name="UF", columnDefinition="NVARCHAR2")
	private String uf;
	
	@Column(name="STATUS", columnDefinition="CHAR")
	private String status;
	
	public Estado() {
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = Sigepen.formatarEspacos(uf);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uf == null) ? 0 : uf.hashCode());
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
		Estado other = (Estado) obj;
		if (uf == null) {
			if (other.uf != null)
				return false;
		} else if (!uf.equals(other.uf))
			return false;
		return true;
	}
		
}
