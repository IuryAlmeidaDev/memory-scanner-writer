package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import br.gov.mt.sejudh.sigepen.base.model.BaseModel;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "DIETA_RECUPERANDO", schema = Sigepen.SEJUDH_SCHEMA)
public class DietaRecuperando extends BaseModel<Long> {

	private static final long serialVersionUID = -689264275741833273L;

	@SequenceGenerator(name = "SEQ_DIETA_RECUPERANDO_GENERATOR", sequenceName = "SEQ_DIETA_RECUPERANDO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DIETA_RECUPERANDO_GENERATOR")
	@Column(name = "ID")
	private long id;

	@Column(name = "INICIO", nullable = false)
	private Date inicio;

	@Column(name = "FIM")
	private Date fim;
	
	@Enumerated
	@Column(name = "STATUS")
	private Status status;

	@OneToOne
	@NotAudited
	private Recuperando recuperando;

	@OneToOne
	@JoinColumn(name = "TIPO_DIETA_ID")
	private TipoDieta tipoDieta;

	public DietaRecuperando() {
		this.status = Status.ATIVO;
	}

	public DietaRecuperando(long id, Date inicio, Date fim, Recuperando recuperando) {
		this.id = id;
		this.inicio = inicio;
		this.fim = fim;
		this.recuperando = recuperando;
	}

	public DietaRecuperando(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}
	
	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperandos(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public TipoDieta getTipoDieta() {
		return this.tipoDieta;
	}

	public void setTipoDieta(TipoDieta tipoDieta) {
		this.tipoDieta = tipoDieta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "DietaEspecial [id=" + id + ", inicio=" + inicio + ", fim=" + fim + ", recuperandos=" + recuperando
				+ "]";
	}

}
