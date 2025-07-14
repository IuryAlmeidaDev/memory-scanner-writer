package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.UnidadeMedida;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "ALIMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class Alimento extends BaseModel<Long>{

	private static final long serialVersionUID = 3553305530826660461L;

	@SequenceGenerator(name = "SEQ_ALIMENTO_GENERATOR", sequenceName = "SEQ_ALIMENTO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ALIMENTO_GENERATOR")
	@Column(name = "ID")
	private long id;

	@Column(name = "DESCRICAO", length = 80, nullable = false)
	private String descricao;

	@Enumerated(EnumType.ORDINAL)
	@Convert(converter = EnumCustomConverter.class, disableConversion=true)
	@Column(name = "UNIDADE_MEDIDA", nullable = false)
	private UnidadeMedida unidadeMedida;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "STATUS", nullable = false)
	private Status status;

	@Column(name = "PROTEICO", nullable = false)
	private boolean proteico;

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

	public UnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isProteico() {
		return proteico;
	}

	public void setProteico(boolean proteico) {
		this.proteico = proteico;
	}

	public Alimento() {
		this.status = Status.ATIVO;
		this.proteico = false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Long.hashCode(id);
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
		Alimento other = (Alimento) obj;
		if (id != other.id)
			return false;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
        return proteico == other.proteico;
    }

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("id", id)
				.append("descricao", descricao)
				.append("unidadeMedida", unidadeMedida)
				.append("status", status)
				.append("proteico", proteico)
				.toString();
	}
}