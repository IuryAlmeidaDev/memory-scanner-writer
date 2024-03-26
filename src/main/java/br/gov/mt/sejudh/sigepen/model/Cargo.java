package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "CARGO", schema = Sigepen.SEJUDH_SCHEMA)
public class Cargo implements Serializable {

	private static final long serialVersionUID = -2410768790864490762L;

	@SequenceGenerator(name = "SEQ_CARGO_GENERATOR", sequenceName = "SEQ_CARGO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CARGO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private long versao;
	
	@Column(name = "NOME", length = 80, nullable = false)
	private String nome;
	
	@Enumerated
	@Column(name = "STATUS")
	private Status status;
	
	@ManyToOne
	@JoinColumn(name = "CATEGORIA_ID", nullable = false)
	private Categoria categoria;
	
	@Column(name = "ESCOLTA", nullable = false)
	private Boolean escolta;
	
	@Transient
	private AutoriaRegistro autorRegistro;
	
	@Transient
	private AutoriaRegistro autorUltimaAlteracao;
	
	public Cargo(){		
	}	
	
	public Cargo(Status status){
		this.setStatus(status);	
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public AutoriaRegistro getAutorRegistro() {
		return autorRegistro;
	}

	public void setAutorRegistro(AutoriaRegistro autorRegistro) {
		this.autorRegistro = autorRegistro;
	}

	public AutoriaRegistro getAutorUltimaAlteracao() {
		return autorUltimaAlteracao;
	}

	public void setAutorUltimaAlteracao(AutoriaRegistro autorUltimaAlteracao) {
		this.autorUltimaAlteracao = autorUltimaAlteracao;
	}

	public Boolean getEscolta() {
		return escolta;
	}

	public void setEscolta(Boolean escolta) {
		this.escolta = escolta;
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
		Cargo other = (Cargo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Cargo [id=" + id + ", nome=" + nome + "]";
	}	
}
