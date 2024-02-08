package br.gov.mt.sejudh.sigepen.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "CONTRATO", schema = Sigepen.SEJUDH_SCHEMA)
public class Contrato extends BaseModel<Long> {

	private static final long serialVersionUID = -689264275741833273L;

	@SequenceGenerator(name = "SEQ_CONTRATO_GENERATOR", sequenceName = "SEQ_CONTRATO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONTRATO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Column(name = "NUM_CONTRATO", nullable = false)
	private String numContrato;
	
	@Column(name = "VIGENCIA_CONTRATO_INICIO")
	private Date vigenciaContratoInicio;
	
	@Column(name = "VIGENCIA_CONTRATO_FIM")
	private Date vigenciaContratoFim;

	@Enumerated
	@Column(name = "STATUS")
	private Status status;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "FORNECEDOR_ID")
	private Fornecedor fornecedor;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "contrato")
	@NotFound(action = NotFoundAction.IGNORE)
	private List<Apostilamento> apostilamentos;
	
	@ManyToMany
	@JoinTable(name = "CONTRATO_UNIDADE_PRISIONAL", schema = Sigepen.SEJUDH_SCHEMA,
	   joinColumns = @JoinColumn(name = "CONTRATO_ID"), 
	   inverseJoinColumns = @JoinColumn(name = "UNIDADE_PRISIONAL_ID"))
	@NotAudited
	private List<UnidadePrisional> unidades = new ArrayList<UnidadePrisional>(); 
	
	@OneToMany(orphanRemoval = true, mappedBy = "contrato", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotFound(action = NotFoundAction.IGNORE)
	private List<HorarioRefeicao> horariosRefeicao = new ArrayList<HorarioRefeicao>();
	
	@NotAudited
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "contrato", fetch = FetchType.LAZY)
	private Justificativa justificativa;
	
	public Contrato() {
		this.status = Status.ATIVO;
	}

	public Contrato(long id, String numContrato, Date vigenciaContratoInicio, Date vigenciaContratoFim, Fornecedor fornecedor) {
		this.id = id;
		this.numContrato = numContrato;
		this.vigenciaContratoInicio = vigenciaContratoInicio;
		this.vigenciaContratoFim = vigenciaContratoFim;
		this.fornecedor = fornecedor;
	}

	public Contrato (Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getNumContrato() {
		return this.numContrato;
	}
	
	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
	}
	
	public Date getVigenciaContratoInicio() {
		return this.vigenciaContratoInicio;
	}
	
	public void setVigenciaContratoInicio(Date vigenciaContratoInicio) {
		this.vigenciaContratoInicio = vigenciaContratoInicio;
	}
	
	public Date getVigenciaContratoFim() {
		return this.vigenciaContratoFim;
	}
	
	public void setVigenciaContratoFim(Date vigenciaContratoFim) {
		this.vigenciaContratoFim = vigenciaContratoFim;
	}
	
	public Fornecedor getFornecedor() {
		return this.fornecedor;
	}
	
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	public List<UnidadePrisional> getUnidades(){
		return this.unidades;
	}
	
	public void setUnidades(List<UnidadePrisional> unidades) {
		this.unidades = unidades;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Justificativa getJustificativa() {
		return this.justificativa;
	}
	
	public void setJustificativa(Justificativa justificativa) {
		this.justificativa = justificativa;
	}
	
	public List<HorarioRefeicao> getHorariosRefeicao() {
		return horariosRefeicao;
	}

	public void setHorariosRefeicao(List<HorarioRefeicao> horariosRefeicao) {
		this.horariosRefeicao = horariosRefeicao;
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
		Contrato other = (Contrato) obj;
		if (id != other.id)
			return false;
		if (numContrato == null) {
			if (other.numContrato != null)
				return false;
		} else if (!numContrato.equals(other.numContrato))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Contrato [id=" + id + ", numContrato = " + this.numContrato + "]";
	}

}
