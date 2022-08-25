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
@Table(name = "SGP_PARAMETRO_VISITA_ESTRUTURA", schema = Sigepen.SEJUDH_SCHEMA)
public class ParametroVisitaEstrutura  extends BaseModel<Long> {

	private static final long serialVersionUID = -1203679757730469017L;

	@SequenceGenerator(name = "SEQ_PARAMETRO_VISITA_ESTRUTURA_GENERATOR", sequenceName = "SEQ_PARAMETRO_VISITA_ESTRUTURA", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PARAMETRO_VISITA_ESTRUTURA_GENERATOR")
	@Column(name = "ID", nullable = false)
	@Id
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "ID_PARAMETRO_VISITA")
	private ParametroVisita parametroVisita;

	@ManyToOne
	@JoinColumn(name = "ID_ESTRUTURA")
	private Estrutura estrutura;

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

	public Estrutura getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(Estrutura estrutura) {
		this.estrutura = estrutura;
	}
	
	@Override
	public String toString() {
		return "Parametro Visita Estrutura [id=" + id + "]";
	}
}
