package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

import br.gov.mt.sejudh.sigepen.enumerator.StatusPlantao;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "PLANTAO", schema = Sigepen.SEJUDH_SCHEMA)
public class Plantao extends BaseModel<Long> {

	private static final long serialVersionUID = -2841675579819366317L;

	@SequenceGenerator(name = "SEQ_PLANTAO_GENERATOR", sequenceName = "SEQ_PLANTAO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PLANTAO_GENERATOR")
	@Id
	@Column(name = "ID")
	private Long id;	

	@Enumerated
	@Column(name = "PLANTAO")
	private TipoPlantao tipoPlantao;

	@Column(name = "PLANTAO_ANTERIOR")
	private String plantaoAnterior;

	@Column(name = "DATA_PLANTAO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataPlantao;

	@Column(name = "DATA_PLANTAO_ENCERRAMENTO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataPlantaoEncerramento;

	@OneToMany(mappedBy = "plantao", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Equipe> equipes = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "UNIDADE_PRISIONAL_ID")
	private UnidadePrisional unidadePrisional;

	@Enumerated
	@Column(name = "STATUS")
	private Status status;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_PLANTAO")
	private StatusPlantao statusPlantao = StatusPlantao.INICIADO;

	@OneToMany(mappedBy = "plantao", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlantaoVisita> plantaoVisitas = new ArrayList<PlantaoVisita>();

	@PreUpdate
	@PrePersist
	public void prePersist() {
		this.getEquipes().forEach(e -> e.setPlantao(this));
	}

	public Plantao() {
	}

	public Plantao(Status status) {
		this.status = status;
	}

	public Plantao(Long id, TipoPlantao tipoPlantao, String plantaoAnterior, Date dataPlantao, List<Equipe> equipes, UnidadePrisional unidadePrisional, Status status, StatusPlantao statusPlantao) {
		this.id = id;
		this.tipoPlantao = tipoPlantao;
		this.plantaoAnterior = plantaoAnterior;
		this.dataPlantao = dataPlantao;
		this.equipes = equipes;
		this.unidadePrisional = unidadePrisional;
		this.status = status;
		this.statusPlantao = statusPlantao;
	}

	public Plantao(TipoPlantao tipoPlantao, Date dataPlantao) {
		this.tipoPlantao = tipoPlantao;
		this.dataPlantao = dataPlantao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoPlantao getTipoPlantao() {
		return tipoPlantao;
	}

	public void setTipoPlantao(TipoPlantao tipoPlantao) {
		this.tipoPlantao = tipoPlantao;
	}

	public Date getDataPlantao() {
		return dataPlantao;
	}

	public void setDataPlantao(Date dataPlantao) {
		this.dataPlantao = dataPlantao;
	}

	public Date getDataPlantaoEncerramento() {
		return dataPlantaoEncerramento;
	}

	public void setDataPlantaoEncerramento(Date dataPlantaoEncerramento) {
		this.dataPlantaoEncerramento = dataPlantaoEncerramento;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

    public List<Equipe> getEquipes() {
        return equipes;
    }

    public void setEquipes(List<Equipe> equipes) {
        this.equipes = equipes;
    }

    public String getPlantaoAnterior() {
        return plantaoAnterior;
    }

    public void setPlantaoAnterior(String plantaoAnterior) {
        this.plantaoAnterior = plantaoAnterior;
    }

	public List<PlantaoVisita> getPlantaoVisitas() {
		return plantaoVisitas;
	}

	public void setPlantaoVisitas(List<PlantaoVisita> plantaoVisitas) {
		this.plantaoVisitas = plantaoVisitas;
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
		Plantao other = (Plantao) obj;
		return id == other.id;
	}

	public StatusPlantao getStatusPlantao() {
		return statusPlantao;
	}

    public void setStatusPlantao(StatusPlantao statusPlantao) {
        this.statusPlantao = statusPlantao;
    }
}
