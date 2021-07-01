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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "ENDERECO", schema = Sigepen.SEJUDH_SCHEMA)
public class Endereco implements Serializable {

	private static final long serialVersionUID = 9058386485116814463L;

	@SequenceGenerator(name = "SEQ_ENDERECO_GENERATOR", sequenceName = "SEQ_ENDERECO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ENDERECO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "SERVIDOR_ID")
	private Servidor servidor;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "PESSOA_ID")
	private Pessoa pessoa;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "UNIDADE_PRISIONAL_ID")
	private UnidadePrisional unidadePrisional;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(optional = false)
	@JoinColumn(name = "CIDADE_ID")
	private Cidade cidade;
	
	@Column(name = "LOGRADOURO", length = 100, nullable = false)
	private String logradouro;

	@Column(name = "BAIRRO", length = 100, nullable = false)
	private String bairro;

	@Column(name = "NUMERO", length = 10)
	private String numero;
	
	@Column(name = "CEP", length = 8)
	private String cep;
	
	@Column(name="COMPLEMENTO", length = 250)
	private String complemento;	
	
	public Endereco() {
	}	
	
	public Endereco(Endereco endereco) {
		this.id = endereco.getId();
		this.servidor = endereco.getServidor();
		this.unidadePrisional = endereco.getUnidadePrisional();
		this.pessoa = endereco.getPessoa();
		this.cidade = endereco.getCidade();
		this.setLogradouro(endereco.getLogradouro());
		this.setBairro(endereco.getBairro());
		this.numero = endereco.getNumero();
		this.cep = endereco.getCep();
		this.setComplemento(endereco.getComplemento());
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = Sigepen.formatarEspacos(logradouro);
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = Sigepen.formatarEspacos(bairro);
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = Sigepen.formatarEspacos(numero);
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = Sigepen.formatarEspacos(complemento);
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bairro == null) ? 0 : bairro.hashCode());
		result = prime * result + ((cep == null) ? 0 : cep.hashCode());
		result = prime * result + ((cidade == null) ? 0 : cidade.hashCode());
		result = prime * result
				+ ((complemento == null) ? 0 : complemento.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((logradouro == null) ? 0 : logradouro.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
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
		Endereco other = (Endereco) obj;
		if (bairro == null) {
			if (other.bairro != null)
				return false;
		} else if (!bairro.equals(other.bairro))
			return false;
		if (cep == null) {
			if (other.cep != null)
				return false;
		} else if (!cep.equals(other.cep))
			return false;
		if (cidade == null) {
			if (other.cidade != null)
				return false;
		} else if (!cidade.equals(other.cidade))
			return false;
		if (complemento == null) {
			if (other.complemento != null)
				return false;
		} else if (!complemento.equals(other.complemento))
			return false;
		if (id != other.id)
			return false;
		if (logradouro == null) {
			if (other.logradouro != null)
				return false;
		} else if (!logradouro.equals(other.logradouro))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		return true;
	}

	@Override
	public String toString() {
		
	String titular = "";
		
		if(pessoa != null){
			if(pessoa.getId()>0){
				titular = ", Titular=" + pessoa.toString();
			}else{
				titular = ", Titular=novo Titular";				
			}
		}else if(unidadePrisional != null){
			if(unidadePrisional.getId()>0){
				titular = ", Titular=" + unidadePrisional.toString();
			}else{
				titular = ", Titular=novo Titular";				
			}
		}else if(servidor != null ){
			if(servidor.getId()>0){
				titular = ", Titular=" + servidor.toString();
			}else{
				titular = ", Titular=novo Titular";				
			}
		}
		
		return "Endereço [id=" + id + titular + "]";
	}
	

	
}
