package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "OUTROS_NOMES", schema = Sigepen.SEJUDH_SCHEMA)
public class OutroNome implements Serializable {

	private static final long serialVersionUID = -5458424664985027105L;
	
	@SequenceGenerator(name = "SEQ_OUTROS_NOMES_GENERATOR", sequenceName = "SEQ_OUTROS_NOMES", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OUTROS_NOMES_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "RECUPERANDO_ID")
	private Recuperando recuperando;
	
	@Column(name = "NOME", length = 80, nullable = false)
	private String nome;
	
	@Column(name = "NOME_MAE", length = 80, nullable = false)
	private String nomeMae;
	
	@Column(name = "DATA_NASCIMENTO", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dataNascimento;
	
	public OutroNome() {
	}	

	public OutroNome(OutroNome outroNome) {
		super();
		this.id = outroNome.getId();
		this.recuperando = outroNome.getRecuperando();
		this.setNome(outroNome.getNome());
		this.setNomeMae(outroNome.getNomeMae());
		this.dataNascimento = outroNome.getDataNascimento();
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

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = Sigepen.formatarEspacos(nomeMae);
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
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
		OutroNome other = (OutroNome) obj;
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
		return "OutroNome [id=" + id + ", nome=" + this.getNome() + "]";
	}
}
