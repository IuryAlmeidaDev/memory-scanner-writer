package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Servidor;


public class ServidorDTO {

	private long id;	
	private Long matricula;	
	private String nome;	
	private String mae;
	private String orgao;	
	private String cargo;	
	private String tipo;	
	private Status status;
	private String funcao;
	
	
	public ServidorDTO() {
	    // Construtor padrão necessário para o Hibernate
	}

	public ServidorDTO(Long id, String nome, String orgao, String cargo, Long matricula) {
		super();
		this.id = id;
		this.nome = nome;
		this.orgao = orgao;
		this.cargo = cargo;
		this.matricula = matricula;
	}

	public ServidorDTO(Servidor servidor){
		this.id = servidor.getId();
		this.nome = servidor.getNome();
		this.orgao = servidor.getFuncao() != null ? servidor.getFuncao().getNome() : null;
		this.cargo = servidor.getCargo() != null ? servidor.getCargo().getNome() : null;
		this.matricula = servidor.getMatricula();
	}
	

	public ServidorDTO(String funcao) {
		super();		
		this.funcao = funcao;
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
		this.nome = nome;
	}
	
	public String getMae() {
		return mae;
	}

	public void setMae(String mae) {
		this.mae = mae;
	}

	public String getOrgao() {
		return orgao;
	}

	public void setOrgao(String orgao) {
		this.orgao = orgao;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getStatus() {
		return status.getDescricao();
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getFuncao() {
		return funcao;
	}


	public void setFuncao(String funcao) {
		this.funcao = funcao;
	}


	@Override
	public String toString() {
		return "Servidor [id=" + id + ", nome=" + nome + "]";
	}
}
