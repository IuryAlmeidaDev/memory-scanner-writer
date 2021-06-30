package br.gov.mt.sejudh.sigepen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "UNIDADE_PRISIONAL", schema = Sigepen.SEJUDH_SCHEMA)
public class UnidadePrisional implements Serializable {

	private static final long serialVersionUID = -689264275741833273L;

	@SequenceGenerator(name = "SEQ_UNIDADE_PRISIONAL_GENERATOR", sequenceName = "SEQ_UNIDADE_PRISIONAL", allocationSize = 1, initialValue = 1, schema = Sigepen.SEJUDH_SCHEMA)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_UNIDADE_PRISIONAL_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Version
	private Long versao;

	@Column(name = "NOME", length = 80, nullable = false)
	private String nome;

	@Column(name = "SIGLA", length = 15)
	private String sigla;

	@Column(name = "CNPJ", length = 14)
	private String cnpj;

	@OneToOne(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="unidadePrisional", fetch = FetchType.LAZY)
	private Endereco endereco;
	
	@OneToOne(fetch=FetchType.LAZY)
	private Servidor responsavel;

	@Transient
	private int capacidadeTotal;

	@Transient
	private Long quantidadeAtual;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="unidadePrisional", fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	private List<Contato> contatos; 

	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(fetch = FetchType.LAZY)
	private TipoUnidade tipoUnidade;
	
	@OneToOne(fetch=FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Estrutura estrutura;
	
	@Transient
	private AutoriaRegistro autorRegistro;
	
	@Transient
	private AutoriaRegistro autorUltimaAlteracao;
	
	@Column(name = "BIOMETRIA_OBRIGATORIA", nullable = false, columnDefinition = "NUMBER(1) DEFAULT 1")
	private Boolean biometriaObrigatoria;

	public UnidadePrisional() {
		this.contatos = new ArrayList<Contato>();
		this.biometriaObrigatoria = true;
	}

	public UnidadePrisional(long id, String nome) {
		this.contatos = new ArrayList<Contato>();
		this.id = id;
		this.setNome(nome);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCapacidadeTotal() {
		return capacidadeTotal;
	}

	public void setCapacidadeTotal(int capacidadeTotal) {
		this.capacidadeTotal = capacidadeTotal;
	}

	public Long getQuantidadeAtual() {
		return quantidadeAtual;
	}

	public void setQuantidadeAtual(Long quantidadeAtual) {
		this.quantidadeAtual = quantidadeAtual;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = Sigepen.formatarEspacos(sigla);
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}	

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public TipoUnidade getTipoUnidade() {
		return tipoUnidade;
	}

	public void setTipoUnidade(TipoUnidade tipoUnidade) {
		this.tipoUnidade = tipoUnidade;
	}

	public Estrutura getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(Estrutura estrutura) {
		this.estrutura = estrutura;
	}
	
	public List<Contato> getContatos() {
		return contatos;
	}

	public void setContatos(List<Contato> contatos) {
		this.contatos = contatos;
	}

	public Servidor getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Servidor responsavel) {
		this.responsavel = responsavel;
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
	public String toString() {
		return "Unidade Prisional [id=" + id + ", nome=" + nome + "]";
	}

	public Boolean getBiometriaObrigatoria() {
		return biometriaObrigatoria;
	}

	public void setBiometriaObrigatoria(Boolean biometriaObrigatoria) {
		this.biometriaObrigatoria = biometriaObrigatoria;
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
		UnidadePrisional other = (UnidadePrisional) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
