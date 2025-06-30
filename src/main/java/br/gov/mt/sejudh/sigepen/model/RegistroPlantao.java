package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import br.gov.mt.sejudh.sigepen.enumerator.StatusPlantao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "REGISTRO_PLANTAO", schema = Sigepen.SEJUDH_SCHEMA)
public class RegistroPlantao extends BaseModel<Long>  {
	
	private static final long serialVersionUID = -2841675579819366317L;
	
	
	@SequenceGenerator(name = "SEQ_REGISTRO_PLANTAO_GENERATOR", sequenceName = "SEQ_REGISTRO_PLANTAO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REGISTRO_PLANTAO_GENERATOR")
	@Id
	@Column(name = "ID")
	private Long id;
		                                                      
	@Column(name = "PLANTAO_ANTERIOR")
	private String plantaoAnterior; 	
	
	@Column(name = "DATA_PLANTAO")
    @Temporal(TemporalType.TIMESTAMP)
	private Date dataPlantao;
	
	@Column(name = "DATA_PLANTAO_ENCERRAMENTO")
    @Temporal(TemporalType.TIMESTAMP)
	private Date dataPlantaoEncerramento;
		
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "TIPO_PLANTAO")
	private TipoPlantao tipoPlantao;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "UNIDADE_PRISIONAL_ID")
	private UnidadePrisional unidadePrisional;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_PLANTAO")
	private StatusPlantao statusPlantao;
		
	@Column(name = "OCORRENCIA")
	private String ocorrencia; 	
	

	
	/*---------------------------------------------------------------- */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPlantaoAnterior() {
		return plantaoAnterior;
	}

	public void setPlantaoAnterior(String plantaoAnterior) {
		this.plantaoAnterior = plantaoAnterior;
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

	public TipoPlantao getTipoPlantao() {
		return tipoPlantao;
	}

	public void setTipoPlantao(TipoPlantao tipoPlantao) {
		this.tipoPlantao = tipoPlantao;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public StatusPlantao getStatusPlantao() {
		return statusPlantao;
	}

	public void setStatusPlantao(StatusPlantao statusPlantao) {
		this.statusPlantao = statusPlantao;
	}
		

	public String getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegistroPlantao other = (RegistroPlantao) obj;
		return id == other.id;
	}

}
