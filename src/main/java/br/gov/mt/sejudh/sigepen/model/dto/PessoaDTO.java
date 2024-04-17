package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;

import br.gov.mt.sejudh.sigepen.enumerator.Sexo;
import br.gov.mt.sejudh.sigepen.model.Biometria;

public class PessoaDTO {

	private long id;
	private String nome;
	private String cpf;
	private Date dataNascimento;
	private String nomeMae;
	private String nomeSocial;
	private String nomePai;
	
	private String nacionalidadeDescricao;
	private String paisNome;
	private String estadoNome;
	private String naturalidadeNome;
	
	private Long biometriaId;
	public Long getBiometriaId() {
		return biometriaId;
	}

	public void setBiometriaId(Long biometriaId) {
		this.biometriaId = biometriaId;
	}

	private Biometria biometria;

	public Biometria getBiometria() {
		return biometria;
	}

	public void setBiometria(Biometria biometria) {
		this.biometria = biometria;
	}

	private Sexo sexo;
	
	public Sexo getSexo() {
		return sexo;
	}

	public void setSexo(Sexo sexo) {
		this.sexo = sexo;
	}

	public String getNacionalidadeDescricao() {
		return nacionalidadeDescricao;
	}

	public void setNacionalidadeDescricao(String nacionalidadeDescricao) {
		this.nacionalidadeDescricao = nacionalidadeDescricao;
	}

	public String getPaisNome() {
		return paisNome;
	}

	public void setPaisNome(String paisNome) {
		this.paisNome = paisNome;
	}

	public String getEstadoNome() {
		return estadoNome;
	}

	public void setEstadoNome(String estadoNome) {
		this.estadoNome = estadoNome;
	}

	public String getNaturalidadeNome() {
		return naturalidadeNome;
	}

	public void setNaturalidadeNome(String naturalidadeNome) {
		this.naturalidadeNome = naturalidadeNome;
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
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	
	public String getNomeSocial() {
		return nomeSocial;
	}

	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	public String getNomePai() {
		return nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	@Override
	public String toString() {
		return "Pessoa [id=" + id + ", nome=" + nome + "]";
	}
}
