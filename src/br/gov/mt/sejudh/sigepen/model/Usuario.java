package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "USUARIO", schema = Sigepen.SEJUDH_SCHEMA)
public class Usuario implements Serializable {

	private static final long serialVersionUID = -6624053166780250512L;

	@Id
	@SequenceGenerator(name = "SEQ_USUARIO_GENERATOR", sequenceName = "SEQ_USUARIO", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USUARIO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;
	
	@Column(name = "LOGIN", length = 11, nullable = false)
	private String login;
	
	@Column(name = "SENHA", length = 100, nullable = false)
	private String senha;
	
	@Column(name = "SALT", length = 100, nullable = false)
	private String salt;
	
	@Column(name = "EMAIL", length = 100)
	private String email;
	
	@Enumerated
	@Column(name = "STATUS")
	private Status status;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_EXPIRACAO", nullable = false)
	private Date dataExpiracao;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_CADASTRO", nullable = false)
	private Date  dataCadastro;
	
	@OneToOne(optional=true)
	@JoinColumn(name = "SERVIDOR_ID")
	private Servidor servidor;
	
	@Column(name = "IS_PRIMEIRO_ACESSO", nullable = false)
	private Boolean primeiroAcesso;
	
	@OneToMany(mappedBy = "usuario", orphanRemoval=true, fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
	@Fetch(FetchMode.SUBSELECT)	
	private List<Acesso> acessos;
	
	@Transient
	private AutoriaRegistro autorRegistro;
	
	@Transient
	private AutoriaRegistro autorUltimaAlteracao;
	
	public Usuario(Status status) {
		this.status = status;
		this.acessos = new ArrayList<Acesso>();
	}
	
	public Usuario() {
		this.acessos = new ArrayList<Acesso>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha.trim();
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Boolean getPrimeiroAcesso() {
		return primeiroAcesso;
	}

	public void setPrimeiroAcesso(Boolean primeiroAcesso) {
		this.primeiroAcesso = primeiroAcesso;
	}

	public List<Acesso> getAcessos() {
		return acessos;
	}

	public void setAcessos(List<Acesso> acessos) {
		this.acessos = acessos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = Sigepen.formatarEspacos(email);
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
		result = prime * result + ((acessos == null) ? 0 : acessos.hashCode());
		result = prime * result
				+ ((dataCadastro == null) ? 0 : dataCadastro.hashCode());
		result = prime * result
				+ ((dataExpiracao == null) ? 0 : dataExpiracao.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		result = prime * result + ((senha == null) ? 0 : senha.hashCode());
		result = prime * result
				+ ((servidor == null) ? 0 : servidor.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Usuario))
			return false;
		Usuario other = (Usuario) obj;
		if (acessos == null) {
			if (other.acessos != null)
				return false;
		} else if (!acessos.equals(other.acessos))
			return false;
		if (dataCadastro == null) {
			if (other.dataCadastro != null)
				return false;
		} else if (!dataCadastro.equals(other.dataCadastro))
			return false;
		if (dataExpiracao == null) {
			if (other.dataExpiracao != null)
				return false;
		} else if (!dataExpiracao.equals(other.dataExpiracao))
			return false;
		if (id != other.id)
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		if (salt == null) {
			if (other.salt != null)
				return false;
		} else if (!salt.equals(other.salt))
			return false;
		if (senha == null) {
			if (other.senha != null)
				return false;
		} else if (!senha.equals(other.senha))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", login=" + login + "]";
	}
}
