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
@Table(name = "FUNCAO", schema = Sigepen.SEJUDH_SCHEMA)
public class Funcao implements Serializable {
	
	private static final long serialVersionUID = 3612035747861457515L;

	@SequenceGenerator(name = "SEQ_FUNCAO_GENERATOR", sequenceName = "SEQ_FUNCAO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FUNCAO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private long versao;
	
	@Column(name = "NOME", length = 80, nullable = false)
	private String nome;
	
	@ManyToOne
	@JoinColumn(name = "CARGO_ID", nullable = false)
	private Cargo cargo;
	
	@Column(name = "LOTACAO_OBRIGATORIA")
	private Boolean lotacaoObrigatoria;
	
	@Enumerated
	@Column(name = "STATUS")
	private Status status;
	
	@Transient
	private AutoriaRegistro autorRegistro;
	
	@Transient
	private AutoriaRegistro autorUltimaAlteracao;
	
	public Funcao(){		
	}	
	
	public Funcao(Status status){
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

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public Boolean getLotacaoObrigatoria() {
		return lotacaoObrigatoria;
	}

	public void setLotacaoObrigatoria(Boolean lotacaoObrigatoria) {
		this.lotacaoObrigatoria = lotacaoObrigatoria;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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
		Funcao other = (Funcao) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Funcao [id=" + id + ", nome=" + nome + "]";
	}
}
