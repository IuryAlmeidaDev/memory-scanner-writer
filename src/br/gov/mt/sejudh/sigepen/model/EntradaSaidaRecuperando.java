package br.gov.mt.sejudh.sigepen.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SGP_ENT_SAI_RECUPERANDO", schema = Sigepen.SEJUDH_SCHEMA)
public class EntradaSaidaRecuperando extends BaseModel<Long> {

	private static final long serialVersionUID = 863475178001620366L;

	@Id
	@SequenceGenerator(name = "SEQ_ENTRADA_SAIDA_RECUPERANDO_GENERATOR", sequenceName = "SEQ_ENTRADA_SAIDA_RECUPERANDO", allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ENTRADA_SAIDA_RECUPERANDO_GENERATOR")
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "ID_ENTRADA_SAIDA")
	private EntradaSaida entrada;

	@OneToOne
	@JoinColumn(name = "ID_CREDENCIAMENTO")
	private Credenciamento credenciamento;
	
	@Override
	public String toString() {
		return "Entrada/Saida Recuperando [id=" + id + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public EntradaSaida getEntrada() {
		return entrada;
	}

	public void setEntrada(EntradaSaida entrada) {
		this.entrada = entrada;
	}

	public Credenciamento getCredenciamento() {
		return credenciamento;
	}

	public void setCredenciamento(Credenciamento credenciamento) {
		this.credenciamento = credenciamento;
	}

}
