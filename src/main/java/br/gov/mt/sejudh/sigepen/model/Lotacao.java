package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name="LOTACAO", schema = Sigepen.SEJUDH_SCHEMA)
public class Lotacao {

	@Id
	@SequenceGenerator(name = "SEQ_LOTACAO_GENERATOR", sequenceName = "SEQ_LOTACAO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LOTACAO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	private Servidor servidor;

	@ManyToOne(optional=false)
	private UnidadePrisional unidade;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_INICIO")
	private Date dataInicio;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_FIM")
	private Date dataFim;
	
	@Column(name = "PLANTAO", length = 50)
	private String plantao;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
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
		

	public String getPlantao() {
		return plantao;
	}

	public void setPlantao(String plantao) {
		this.plantao = plantao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataFim == null) ? 0 : dataFim.hashCode());
		result = prime * result
				+ ((dataInicio == null) ? 0 : dataInicio.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((unidade == null) ? 0 : unidade.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Lotacao))
			return false;
		Lotacao other = (Lotacao) obj;
		if (dataFim == null) {
			if (other.dataFim != null)
				return false;
		} else if (!dataFim.equals(other.dataFim))
			return false;
		if (dataInicio == null) {
			if (other.dataInicio != null)
				return false;
		} else if (!dataInicio.equals(other.dataInicio))
			return false;
		if (id != other.id)
			return false;
		if (unidade == null) {
			if (other.unidade != null)
				return false;
		} else if (!(unidade.getId() == other.unidade.getId()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Lotação de Servidor [id=" + id +", " + servidor +", " + unidade + "]";
	}
}
