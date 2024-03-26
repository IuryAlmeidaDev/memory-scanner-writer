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
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "ORGAO", schema = Sigepen.SEJUDH_SCHEMA)
public class Orgao implements Serializable {

	private static final long serialVersionUID = -7206725876402782070L;

	@SequenceGenerator(name = "SEQ_ORGAO_GENERATOR", sequenceName = "SEQ_ORGAO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ORGAO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private long versao;

	@Column(name = "NOME", length = 100, nullable = false)
	private String nome;

	@Column(name = "LOGRADOURO", length = 100, nullable = false)
	private String logradouro;

	@Column(name = "NUMERO", length = 10)
	private String numero;

	@Column(name = "BAIRRO", length = 100, nullable = false)
	private String bairro;

	@Column(name = "CEP", length = 8)
	private String cep;

	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "CIDADE_ID")
	private Cidade cidade;

	@Enumerated
	@Column(name = "STATUS")
	private Status status;
	
	@Transient
	private AutoriaRegistro autorRegistro;
	
	@Transient
	private AutoriaRegistro autorUltimaAlteracao;

	public Orgao() {
	}	

	public Orgao(long id, String nome) {
		this.id = id;
		this.setNome(nome);
	}

	public Orgao(Status status)  {
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
	
	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = Sigepen.formatarEspacos(logradouro);
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = Sigepen.formatarEspacos(numero);
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = Sigepen.formatarEspacos(bairro);
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = Sigepen.formatarEspacos(cep);
	}
	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
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
		Orgao other = (Orgao) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Orgao [id=" + id + ", nome=" + nome + "]";
	}
}