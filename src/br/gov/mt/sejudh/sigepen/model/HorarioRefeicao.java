package br.gov.mt.sejudh.sigepen.model;

import java.math.BigDecimal;
import java.util.Date;

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
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "HORARIO_REFEICAO", schema = Sigepen.SEJUDH_SCHEMA)
public class HorarioRefeicao extends BaseModel<Long> {
	
	private static final long serialVersionUID = -6635699183741253357L;
	
	@SequenceGenerator(name = "SEQ_HORARIO_REFEICAO_GENERATOR", sequenceName = "SEQ_HORARIO_REFEICAO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_HORARIO_REFEICAO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "TIPO_REFEICAO_ID")
	private TipoRefeicao tipoRefeicao;
	
	@ManyToOne(optional=true)
	@JoinColumn(name = "CONTRATO_ID", nullable=true)
	private Contrato contrato;
	
	@ManyToOne
	@JoinColumn(name = "UNIDADE_PRISIONAL_ID")
	private UnidadePrisional unidadePrisional;
	
	@Column(name = "DATA_INICIO", nullable = false)
	private Date dataInicio;

	@Column(name = "DATA_FIM", nullable = false)
	private Date dataFim;
	
	@Column(name = "HORARIO")
	private String horario;
	
	public HorarioRefeicao() {
		
	}
	
	public HorarioRefeicao(Object[] row) {
		TipoRefeicao tipoRefeicao = new TipoRefeicao();
		this.horario = (String) row[0];
		tipoRefeicao.setId(((BigDecimal) row[1]).longValue());
		this.tipoRefeicao = tipoRefeicao;
	}

	public String horarioFormatado() {
		return this.horario.substring(0, 2) + ":" + this.horario.substring(2);
	}

	@Override
	public Long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public TipoRefeicao getTipoRefeicao() {
		return tipoRefeicao;
	}

	public void setTipoRefeicao(TipoRefeicao tiporefeicao) {
		this.tipoRefeicao = tiporefeicao;
	}
	
	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
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
	
	public String getHorario() {
		return horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}
	
	private String formatarHora(String hora) {
		if(hora.length() != 4)
			return hora;
		return hora.substring(0,2) + ":" + hora.substring(2,4);
	}
	
	public String horario() {
		if(this.horario == null)
			this.horario = "";
		return formatarHora(this.horario);
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
		HorarioRefeicao other = (HorarioRefeicao) obj;
		if (id != other.id)
			return false;
		if (tipoRefeicao != other.tipoRefeicao)
			return false;
		return true;
	}
    
    @Override
	public String toString() {
		return "Refeicao [id=" + id + ", nome=" + tipoRefeicao.getDescricao() + ", horario=" + horario + "]";
	}
	
}
