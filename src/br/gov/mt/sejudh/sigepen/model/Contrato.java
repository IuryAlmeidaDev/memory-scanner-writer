package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "CONTRATO", schema = Sigepen.SEJUDH_SCHEMA)
public class Contrato implements Serializable {

	private static final long serialVersionUID = -689264275741833273L;

	@SequenceGenerator(name = "SEQ_CONTRATO_GENERATOR", sequenceName = "SEQ_CONTRATO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONTRATO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Column(name = "NUM_CONTRATO", nullable = false)
	private Long numContrato;
	
	@Column(name = "VIGENCIA_CONTRATO")
	private Date vigenciaContrato;

	@OneToOne(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="contrato", fetch = FetchType.LAZY)
	private Fornecedor fornecedor;	

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany
	@JoinTable(name = "CONTRATO")
	private List<UnidadePrisional> unidades; 	
	
	public Contrato() {
	}

	public Contrato(long id, Long numContrato, Date vigenciaContrato, Fornecedor fornecedor) {
		this.id = id;
		this.numContrato = numContrato;
		this.vigenciaContrato = vigenciaContrato;
		this.fornecedor = fornecedor;
	}

	public Contrato (Long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public Long getNumContrato() {
		return this.numContrato;
	}
	
	public void setNumContrato(Long numContrato) {
		this.numContrato = numContrato;
	}
	
	public Date getVigenciaContrato() {
		return this.vigenciaContrato;
	}
	
	public void setVigenciaContrato(Date vigenciaContrato) {
		this.vigenciaContrato = vigenciaContrato;
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
	
    @Override
	public String toString() {
		return "Contrato [id=" + id + ", Numero do contrato = " + this.numContrato + "]";
	}
}
