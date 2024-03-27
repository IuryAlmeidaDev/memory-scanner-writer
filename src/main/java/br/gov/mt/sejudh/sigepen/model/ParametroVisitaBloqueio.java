package br.gov.mt.sejudh.sigepen.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.conversores.EnumCustomConverter;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_PARAM_VISITA_BLOQUEIO", schema = Sigepen.SEJUDH_SCHEMA)
public class ParametroVisitaBloqueio extends BaseModel<Long> {

	private static final long serialVersionUID = -709898524753237423L;

	@Id
	@SequenceGenerator(name = "SEQ_PARAMETRO_VISITA_BLOQUEIO_GENERATOR", sequenceName = "SEQ_PARAMETRO_VISITA_BLOQUEIO", allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PARAMETRO_VISITA_BLOQUEIO_GENERATOR")
	@Column(name = "ID")
	private Long id;

	@ManyToOne
    @JoinColumn(name = "ID_PARAMETRO_VISITA")
	private ParametroVisita parametroVisita;

	@Column(name = "DATA_INICIO")
	private Date dataInicio;

	@Column(name = "DATA_FIM")
	private Date dataFim;
	
	@Column(name = "DESCRICAO")
	private String descricao;

	@Override
	public String toString() {
		return "Parametro Visita Bloqueio [id=" + id + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ParametroVisita getParametroVisita() {
		return parametroVisita;
	}

	public void setParametroVisita(ParametroVisita parametroVisita) {
		this.parametroVisita = parametroVisita;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = Sigepen.formatarEspacos(descricao);
	}
}
