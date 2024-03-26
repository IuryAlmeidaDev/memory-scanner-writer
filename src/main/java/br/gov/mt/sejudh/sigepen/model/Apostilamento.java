package br.gov.mt.sejudh.sigepen.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "APOSTILAMENTO", schema = Sigepen.SEJUDH_SCHEMA)
public class Apostilamento extends BaseModel<Long> {

	private static final long serialVersionUID = -7426057012911064630L;

	@SequenceGenerator(name = "SEQ_APOSTILAMENTO_GENERATOR", sequenceName = "SEQ_APOSTILAMENTO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_APOSTILAMENTO_GENERATOR")
	@Column(name = "ID")
	private long id;

	@Column(name = "DATA_INICIO", nullable = false)
	private Date dataInicio;

	@Column(name = "DATA_FIM", nullable = false)
	private Date dataFim;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CONTRATO_ID")
	private Contrato contrato;

	@OneToMany(orphanRemoval = true, mappedBy = "apostilamento", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	private List<Refeicao> refeicoes;

	public Apostilamento() {
		this.refeicoes = new ArrayList<Refeicao>();
	}

	public Apostilamento(long id, Date dataInicio, Contrato contrato) {
		this.id = id;
		this.dataInicio = dataInicio;
		this.contrato = contrato;
		this.refeicoes = new ArrayList<Refeicao>();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public List<Refeicao> getRefeicoes() {
		return refeicoes;
	}

	public void setRefeicoes(List<Refeicao> refeicoes) {
		this.refeicoes = refeicoes;
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
		Apostilamento other = (Apostilamento) obj;
		if (id != other.id)
			return false;
		if (dataInicio == null) {
			if (other.dataInicio != null)
				return false;
		} else if (!dataInicio.equals(other.dataInicio))
			return false;
		if (dataFim == null) {
			if (other.dataFim != null)
				return false;
		} else if (!dataFim.equals(other.dataFim))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Apostilamento [id=" + id + ", dataInicio=" + dataInicio + ", dataFim=" + dataFim + "]";
	}

}
