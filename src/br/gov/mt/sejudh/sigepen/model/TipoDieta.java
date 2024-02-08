package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "TIPO_DIETA", schema = Sigepen.SEJUDH_SCHEMA)
public class TipoDieta extends BaseModel<Long> {

	private static final long serialVersionUID = 1989588200757977450L;

	@SequenceGenerator(name = "SEQ_TIPO_DIETA_GENERATOR", sequenceName = "SEQ_TIPO_DIETA", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TIPO_DIETA_GENERATOR")
	@Column(name = "ID")
	private long id;

	@Column(name = "DESCRICAO", length = 80, nullable = false)
	private String descricao;

	@Column(name = "ESPECIAL", nullable = false)
	private Boolean especial;

	@Enumerated
	@Column(name = "STATUS", nullable = false)
	private Status status;

	@Override
	public Long getId() {
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

	public Boolean getEspecial() {
		return especial;
	}

	public void setEspecial(Boolean especial) {
		this.especial = especial;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public TipoDieta() {
		this.status = Status.ATIVO;
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoDieta other = (TipoDieta) obj;
		if (id != other.id)
			return false;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (especial == null) {
			if (other.especial != null)
				return false;
		} else if (!especial.equals(other.especial))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TipoDieta [id=" + id + ", descricao=" + descricao + ", especial=" + especial + "]";
	}

}
