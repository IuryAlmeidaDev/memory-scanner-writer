package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "EXCLUSAO_DESLIGAMENTO_JUSTIFICATIVA", schema = Sigepen.SEJUDH_SCHEMA)
public class ExclusaoDesligamentoJustificativa extends BaseModel<Long> implements Serializable {

	private static final long serialVersionUID = -689264275741833273L;

	@SequenceGenerator(name = "SEQ_EXCLUSAO_DESLIGAMENTO_JUSTIFICATIVA_GENERATOR", sequenceName = "SEQ_EXCLUSAO_DESLIGAMENTO_JUSTIFICATIVA", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EXCLUSAO_DESLIGAMENTO_JUSTIFICATIVA_GENERATOR")
	@Column(name = "ID")
	private Long id;

	@Column(name = "DESCRICAO", nullable = false)
	private String descricao;

	@Column(name = "DATA_SUBSTITUICAO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSubstituicao = new Date();

	@ManyToOne
	@JoinColumn(name = "PASSAGEM_ID")
	private Passagem passagem;

	public ExclusaoDesligamentoJustificativa() {
	}

	public ExclusaoDesligamentoJustificativa(long id, String descricao, Date dataSubstituicao,
			Passagem passagem) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.dataSubstituicao = dataSubstituicao;
		this.passagem = passagem;
	}

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
		this.descricao = descricao;
	}

	public Date getDataSubstituicao() {
		return dataSubstituicao;
	}

	public void setDataSubstituicao(Date dataSubstituicao) {
		this.dataSubstituicao = dataSubstituicao;
	}

	public Passagem getPassagem() {
		return passagem;
	}

	public void setPassagem(Passagem recuperando) {
		this.passagem = recuperando;
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
		ExclusaoDesligamentoJustificativa other = (ExclusaoDesligamentoJustificativa) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return "ExclusaoDesligamentoJustificativa [id=" + id + ", descricao=" + descricao + ", dataSubstituicao="
				+ dataSubstituicao + ", passagem=" + passagem + "]";
	}

}
