package br.gov.mt.sejudh.sigepen.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.gov.mt.sejudh.sigepen.enumerator.Sexo;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Audited
@Entity
@Table(name = "SERVIDOR", schema = Sigepen.SEJUDH_SCHEMA)
public class Servidor {
	@SequenceGenerator(name = "SEQ_SERVIDOR_GENERATOR", sequenceName = "SEQ_SERVIDOR", initialValue = 1, allocationSize = 1, schema = Sigepen.SEJUDH_SCHEMA)
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SERVIDOR_GENERATOR")
	@Column(name = "ID")
	private long id;
	
	@Column(name = "MATRICULA", nullable = false, length = 10)
	private Long matricula;
	
	@Column(name = "NOME", length = 100, nullable = false)
	private String nome;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_NASCIMENTO" , nullable = false)
	private Date dataNascimento;
	
	@Column(name = "NOME_MAE", length = 100, nullable = false)
	private String mae;
	
	@Column(name = "NOME_PAI", length = 100)
	private String pai;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SEXO", length = 10)
	private Sexo sexo;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="RACA_ID")
	private Raca raca;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="RELIGIAO_ID")
	private Religiao religiao;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="TIPOSANGUINEO_ID")
	private TipoSanguineo tipoSanguineo;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ESTADOCIVIL_ID")
	private EstadoCivil estadoCivil;
	
	@Column(name = "CPF", nullable = false, unique = true, length = 11)
	private String cpf;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="servidor", fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	private List<Documento> documentos;
	
	@Column(name = "EMAIL", length = 100)
	private String email;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="servidor", fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	private List<Endereco> enderecos;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ESCOLARIDADE_ID")
	private Escolaridade escolaridade;
	
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="TIPOSERVIDOR_ID")
	private TipoServidor tipoServidor;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="ORGAO_ID")
	private Orgao orgao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CARGO_ID")
	private Cargo cargo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FUNCAO_ID")
	private Funcao funcao;	
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="servidor", fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	private List<Contato> contatos;
	
	@Enumerated
	@Column(name = "STATUS")
	private Status status;
	
	@OneToMany(mappedBy="servidor", cascade=CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
	private List<Lotacao> lotacao;
	
	@Transient
	private AutoriaRegistro autorRegistro;
	
	@Transient
	private AutoriaRegistro autorUltimaAlteracao;
	
	public Servidor(){
		this.documentos = new ArrayList<Documento>();
		this.contatos = new ArrayList<Contato>();
		this.enderecos = new ArrayList<Endereco>();
		this.lotacao = new ArrayList<Lotacao>();
	}
	
	public Servidor(long id) {
		this.id = id;
	}
	
	public Servidor(Status status){
		this.documentos = new ArrayList<Documento>();
		this.contatos = new ArrayList<Contato>();
		this.enderecos = new ArrayList<Endereco>();
		this.lotacao = new ArrayList<Lotacao>();
		this.setStatus(status);
	}

	public Servidor(long id, Long matricula, String nome, String cpf, String email) {
		this.id = id;
		this.matricula = matricula;
		this.setNome(nome);
		this.cpf = cpf;
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = Sigepen.formatarEspacos(nome);
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getMae() {
		return mae;
	}

	public void setMae(String mae) {
		this.mae = Sigepen.formatarEspacos(mae);
	}

	public String getPai() {
		return pai;
	}

	public void setPai(String pai) {
		this.pai = Sigepen.formatarEspacos(pai);
	}

	public Sexo getSexo() {
		return sexo;
	}

	public void setSexo(Sexo sexo) {
		this.sexo = sexo;
	}

	public Raca getRaca() {
		return raca;
	}

	public void setRaca(Raca raca) {
		this.raca = raca;
	}

	public Religiao getReligiao() {
		return religiao;
	}

	public void setReligiao(Religiao religiao) {
		this.religiao = religiao;
	}

	public TipoSanguineo getTipoSanguineo() {
		return tipoSanguineo;
	}

	public void setTipoSanguineo(TipoSanguineo tipoSanguineo) {
		this.tipoSanguineo = tipoSanguineo;
	}

	public EstadoCivil getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(EstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}	

	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = Sigepen.formatarEspacos(email);
	}	

	public List<Endereco> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<Endereco> endereco) {
		this.enderecos = endereco;
	}

	public Escolaridade getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(Escolaridade escolaridade) {
		this.escolaridade = escolaridade;
	}

	public TipoServidor getTipoServidor() {
		return tipoServidor;
	}

	public void setTipoServidor(TipoServidor tipoServidor) {
		this.tipoServidor = tipoServidor;
	}

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public Funcao getFuncao() {
		return funcao;
	}

	public void setFuncao(Funcao funcao) {
		this.funcao = funcao;
	}	

	public List<Contato> getContatos() {
		return contatos;
	}

	public void setContatos(List<Contato> contatos) {
		this.contatos = contatos;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Lotacao> getLotacao() {
		return lotacao;
	}

	public void setLotacao(List<Lotacao> lotacao) {
		this.lotacao = lotacao;
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
		return "Servidor [id=" + id + ", nome=" + nome + "]";
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
		Servidor other = (Servidor) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
