package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "ALCUNHA", schema = Sigepen.SEJUDH_SCHEMA)
public class Alcunha implements Serializable {

	private static final long serialVersionUID = -5458424664985027105L;
	
	@SequenceGenerator(name = "SEQ_ALCUNHA_GENERATOR", sequenceName = "SEQ_ALCUNHA", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ALCUNHA_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;
	
	@Column(name = "NOME", length = 80, nullable = false)
	private String nome;
	
	@Column(name = "PRINCIPAL")
	private boolean principal;
	
	public Alcunha() {
	}	

	public Alcunha(Alcunha alcunha) {
		super();
		this.id = alcunha.getId();
		this.recuperando = alcunha.getRecuperando();
		this.nome = alcunha.getNome();
		this.principal = alcunha.isPrincipal();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((recuperando == null) ? 0 : recuperando.hashCode());
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
		Alcunha other = (Alcunha) obj;
		if (id != other.id)
			return false;
		if (recuperando == null) {
			if (other.recuperando != null)
				return false;
		} else if (!recuperando.equals(other.recuperando))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Alcunha [id=" + id + ", nome=" + this.getNome() + "]";
	}
}
