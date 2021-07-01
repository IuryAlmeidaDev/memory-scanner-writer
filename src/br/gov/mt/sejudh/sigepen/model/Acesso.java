package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "ACESSO", schema = Sigepen.SEJUDH_SCHEMA)
public class Acesso implements Serializable {
	
	private static final long serialVersionUID = 5487435987114310141L;

	@Id
	@SequenceGenerator(name = "SEQ_ACESSO_GENERATOR", sequenceName = "SEQ_ACESSO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ACESSO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;

	@OneToOne
	@JoinColumn(name = "UNIDADE_ID")
	private UnidadePrisional unidade;
	
	@ManyToOne(optional=false, fetch = FetchType.LAZY)
	@JoinColumn(name = "USUARIO_ID")
	private Usuario usuario;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(
			name = "ACESSO_PERFIL", schema = Sigepen.SEJUDH_SCHEMA,
			joinColumns = @JoinColumn(name="ACESSO_ID"),
			inverseJoinColumns = @JoinColumn(name = "PERFIL_ID"))
	private List<Perfil> perfis;

	public Acesso(Usuario usuario) {
		this.perfis = new ArrayList<Perfil>();
		this.usuario = usuario;
	}
	
	public Acesso() {
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public List<Perfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((perfis == null) ? 0 : perfis.hashCode());
		result = prime * result + ((unidade == null) ? 0 : unidade.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Acesso))
			return false;
		Acesso other = (Acesso) obj;
		if (id != other.id)
			return false;
		if (perfis == null) {
			if (other.perfis != null)
				return false;
		} else if (!perfis.equals(other.perfis))
			return false;
		if (unidade == null) {
			if (other.unidade != null)
				return false;
		} else if (!unidade.equals(other.unidade))
			return false;
		return true;
	}
}
