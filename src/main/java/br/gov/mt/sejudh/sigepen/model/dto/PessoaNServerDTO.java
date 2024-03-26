package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

public class PessoaNServerDTO {

	private Long id;
	private Long matricula;
	private Long codigo_visitante;
	private Long biometria_id;
	private String nome;
	private String nomeMae;
	private Date dataNascimento;
	
	public PessoaNServerDTO() {
		
	}
	
	public PessoaNServerDTO(Long id, Long matricula, Long codigo_visitante, Long biometria_id, String nome, String nomeMae, Date dataNascimento) {
		this.id = id;
		this.matricula = matricula;
		this.codigo_visitante = codigo_visitante;
		this.biometria_id = biometria_id;
		this.nome = nome;
		this.nomeMae = nomeMae;
		this.dataNascimento = dataNascimento;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getMatricula() {
		return matricula;
	}
	
	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}
	
	public Long getCodigo_visitante() {
		return codigo_visitante;
	}
	
	public void setCodigo_visitante(Long codigo_visitante) {
		this.codigo_visitante = codigo_visitante;
	}
	
	public Long getBiometria_id() {
		return biometria_id;
	}
	
	public void setBiometria_id(Long biometria_id) {
		this.biometria_id = biometria_id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getNomeMae() {
		return nomeMae;
	}
	
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	
	public Date getDataNascimento() {
		return dataNascimento;
	}
	
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
}
