package br.gov.mt.sejudh.sigepen.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.DiasSemana;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "CARDAPIO", schema = Sigepen.SEJUDH_SCHEMA)
public class Cardapio extends BaseModel<Long> {

	private static final long serialVersionUID = -2841675579819366317L;

	@SequenceGenerator(name = "SEQ_CARDAPIO_GENERATOR", sequenceName = "SEQ_CARDAPIO", allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CARDAPIO_GENERATOR")
	@Column(name = "ID")
	private Long id;

	@Column(name = "SEMANA", nullable = false)
	private Long semana;

	@Enumerated
	@Column(name = "CODG_DIA_SEMANA", nullable = false)
	private DiasSemana diaSemana;

	@ManyToOne
	@JoinColumn(name = "TIPO_REFEICAO_ID")
	private TipoRefeicao tipoRefeicao;

	@ManyToOne
	@JoinColumn(name = "CONTRATO_ID")
	private Contrato contrato;

	@ManyToOne
	@JoinColumn(name = "TIPO_DIETA_ID", nullable = false)
	private TipoDieta tipoDieta;

	@OneToMany(orphanRemoval = true, mappedBy = "cardapio", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	private List<ItemCardapio> itensCardapio;

	@Column(name = "FESTIVO", nullable = false)
	private Boolean festivo;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA")
	private Date data;

	@javax.persistence.Transient
	private Date proximaData;

	public Cardapio() {
		this.itensCardapio = new ArrayList<>();
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSemana() {
		return semana;
	}

	public void setSemana(Long semana) {
		this.semana = semana;
	}

	public DiasSemana getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DiasSemana diaSemana) {
		this.diaSemana = diaSemana;
	}

	public TipoRefeicao getTipoRefeicao() {
		return tipoRefeicao;
	}

	public void setTipoRefeicao(TipoRefeicao tipoRefeicao) {
		this.tipoRefeicao = tipoRefeicao;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public Boolean getFestivo() {
		return festivo;
	}

	public void setFestivo(Boolean festivo) {
		this.festivo = festivo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getProximaData() {
		return proximaData;
	}

	public void setProximaData(Date proximaData) {
		this.proximaData = proximaData;
	}

	public TipoDieta getTipoDieta() {
		return tipoDieta;
	}

	public void setTipoDieta(TipoDieta tipoDieta) {
		this.tipoDieta = tipoDieta;
	}

	public List<ItemCardapio> getItensCardapio() {
		return itensCardapio;
	}

	public void setItensCardapio(List<ItemCardapio> itensCardapio) {
		this.itensCardapio = itensCardapio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
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
		Cardapio other = (Cardapio) obj;
		if (!Objects.equals(id, other.id))
			return false;
		if (semana == null) {
			if (other.semana != null)
				return false;
		} else if (!semana.equals(other.semana))
			return false;
		if (diaSemana == null) {
			if (other.diaSemana != null)
				return false;
		} else if (!diaSemana.equals(other.diaSemana))
			return false;
		if (festivo == null) {
            return other.festivo == null;
		} else return festivo.equals(other.festivo);
    }

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("id", id)
				.append("semana", semana)
				.append("diaSemana", diaSemana)
				.append("tipoRefeicao", tipoRefeicao)
				.append("contrato", contrato)
				.append("tipoDieta", tipoDieta)
				.append("itensCardapio", itensCardapio)
				.append("festivo", festivo)
				.append("data", data)
				.toString();
	}
}
