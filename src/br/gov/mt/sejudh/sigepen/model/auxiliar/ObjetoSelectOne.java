package br.gov.mt.sejudh.sigepen.model.auxiliar;

import java.io.Serializable;
import java.util.Objects;

public class ObjetoSelectOne implements Serializable {

	private static final long serialVersionUID = -1036236421268857249L;
	
	private Long id;
	private String descricao;

	public ObjetoSelectOne() {
		super();
		this.descricao = "";
	}

	public ObjetoSelectOne(int id, String descricao) {
		super();
		this.id = new Long(id);
		this.descricao = descricao;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setId(int id) {
		this.id = new Long(id);
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjetoSelectOne other = (ObjetoSelectOne) obj;
		return Objects.equals(id, other.id);
	}
}
