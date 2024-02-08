package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import br.gov.mt.sejudh.sigepen.enumerator.TipoContato;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "CONTATO", schema = Sigepen.SEJUDH_SCHEMA)
public class Contato implements Serializable {

	private static final long serialVersionUID = -5458424664985027105L;
	
	@SequenceGenerator(name = "SEQ_CONTATO_GENERATOR", sequenceName = "SEQ_CONTATO", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONTATO_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Transient
	private Long versao;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "PESSOA_ID")
	private Pessoa pessoa;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "SERVIDOR_ID")
	private Servidor servidor;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "UNIDADE_PRISIONAL_ID")
	private UnidadePrisional unidadePrisional;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "FORNECEDOR_ID")
	private Fornecedor fornecedor;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_CONTATO", nullable = false, length = 80)
	private TipoContato tipoContato;	
	
	@Column(name = "VALOR", length = 80, nullable = false)
	private String valor;
	
	@Column(name = "NOME", length = 80, nullable = false)
	private String nome;
	
	@Column(name = "OBSERVACAO", length = 250) 
	private String observacao;
	
	public Contato() {
	}	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TipoContato getTipoContato() {
		return tipoContato;
	}

	public void setTipoContato(TipoContato tipoContato) {
		this.tipoContato = tipoContato;
	}	

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = Sigepen.formatarEspacos(valor);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = Sigepen.formatarEspacos(observacao);
	}	

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
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
	
	public Fornecedor getFornecedor() {
		return this.fornecedor;
	}
	
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result
				+ ((tipoContato == null) ? 0 : tipoContato.hashCode());
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
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
		Contato other = (Contato) obj;
		if (id != other.id)
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (tipoContato != other.tipoContato)
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
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
		
		return "Contato [id=" + id +", Tipo=" + tipoContato.getDescricao() + ", Valor=" + valor + titular + "]";
	}

}
