package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Table(name="TIPO_HISTORICO", schema = Sigepen.SEJUDH_SCHEMA)
public class TipoHistorico implements Serializable{

	private static final long serialVersionUID = 9169969099919295013L;

	@Id
	@Column(name="ID")
	private long id;

	@Column(name="DESCRICAO", length=80, nullable = false)
	private String descricao;

	@ManyToOne
	@JoinColumn(name="FUNCIONALIDADE_ID")
	private Funcionalidade funcionalidade;

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

	public Funcionalidade getFuncionalidade() {
		return funcionalidade;
	}

	public void setFuncionalidade(Funcionalidade funcionalidade) {
		this.funcionalidade = funcionalidade;
	}

	@Override
	public String toString() {
		return "TipoHistorico [id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descricao == null) ? 0 : descricao.hashCode());
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
		TipoHistorico other = (TipoHistorico) obj;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (id != other.id)
			return false;
		return true;
	}	

}
