package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SUBSTITUICAO_FOTO_JUSTIFICATIVA", schema = Sigepen.SEJUDH_SCHEMA)
public class SubstituicaoFotoJustificativa extends BaseModel<Long> implements Serializable {

	private static final long serialVersionUID = -689264275741833273L;

	@SequenceGenerator(name = "SEQ_SUBSTITUICAO_FOTO_JUSTIFICATIVA_GENERATOR", sequenceName = "SEQ_SUBSTITUICAO_FOTO_JUSTIFICATIVA", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SUBSTITUICAO_FOTO_JUSTIFICATIVA_GENERATOR")
	@Column(name = "ID")
	private Long id;

	@Column(name = "DESCRICAO", nullable = false)
	private String descricao;

	@Column(name = "DATA_SUBSTITUICAO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataSubstituicao = new Date();

	@Column(name = "FOTO_SUBSTITUTA_ID")
	private Long fotoSubstitutaId;

	@Column(name = "FOTO_SUBSTITUIDA_ID")
	private Long fotoSubstituidaId;

	@ManyToOne
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;

	public SubstituicaoFotoJustificativa() {
	}

	public SubstituicaoFotoJustificativa(long id, String descricao, Date dataSubstituicao, Long fotoSubstitutaId,
			Long fotoSubstituidaId, Recuperando recuperando) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.dataSubstituicao = dataSubstituicao;
		this.fotoSubstitutaId = fotoSubstitutaId;
		this.fotoSubstituidaId = fotoSubstituidaId;
		this.recuperando = recuperando;
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

	public Long getFotoSubstitutaId() {
		return fotoSubstitutaId;
	}

	public void setFotoSubstitutaId(Long fotoSubstitutaId) {
		this.fotoSubstitutaId = fotoSubstitutaId;
	}

	public Long getFotoSubstituidaId() {
		return fotoSubstituidaId;
	}

	public void setFotoSubstituidaId(Long fotoSubstituidaId) {
		this.fotoSubstituidaId = fotoSubstituidaId;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
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
		SubstituicaoFotoJustificativa other = (SubstituicaoFotoJustificativa) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return "SubstituicaoFotoJustificativa [id=" + id + ", descricao=" + descricao + ", dataSubstituicao="
				+ dataSubstituicao + ", fotoSubstitutaId=" + fotoSubstitutaId + ", fotoSubstituidaId="
				+ fotoSubstituidaId + ", recuperando=" + recuperando + "]";
	}

}
