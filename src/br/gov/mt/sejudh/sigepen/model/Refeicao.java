package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "REFEICAO", schema = Sigepen.SEJUDH_SCHEMA)
public class Refeicao extends BaseModel<Long> {

	private static final long serialVersionUID = -1368037331203253875L;

	@SequenceGenerator(name = "SEQ_REFEICAO_GENERATOR", sequenceName = "SEQ_REFEICAO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REFEICAO_GENERATOR")
	@Column(name = "ID")
	private long id;

	@ManyToOne
	@JoinColumn(name = "TIPO_REFEICAO_ID")
	private TipoRefeicao tipoRefeicao;

	@Column(name = "VALOR", nullable = false)
	private Float valor;

	@Column(name = "VALOR_ESPECIAL", nullable = false)
	private Float valorEspecial;

	@ManyToOne
	@JoinColumn(name = "APOSTILAMENTO_ID")
	private Apostilamento apostilamento;

	public Refeicao() {
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TipoRefeicao getTipoRefeicao() {
		return tipoRefeicao;
	}

	public void setTipoRefeicao(TipoRefeicao tipoRefeicao) {
		this.tipoRefeicao = tipoRefeicao;
	}

	public Float getValor() {
		return valor;
	}

	public void setValor(Float valor) {
		this.valor = valor;
	}

	public Float getValorEspecial() {
		return valorEspecial;
	}

	public void setValorEspecial(Float valorEspecial) {
		this.valorEspecial = valorEspecial;
	}

	public Apostilamento getApostilamento() {
		return apostilamento;
	}

	public void setApostilamento(Apostilamento apostilamento) {
		this.apostilamento = apostilamento;
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
		Refeicao other = (Refeicao) obj;
		if (id != other.id)
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		if (valorEspecial != other.valorEspecial)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Refeicao [id=" + id + ", nome=" + tipoRefeicao.getDescricao() + ", valor=" + valor + ", valorEspecial=" + valorEspecial + "]";
	}

}
