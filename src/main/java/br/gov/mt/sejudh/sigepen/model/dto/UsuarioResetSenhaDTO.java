package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mt.sejudh.sigepen.model.Usuario;

public class UsuarioResetSenhaDTO {

	private Usuario usuario;
	private String cpf;
	private Date nascimento;
	private List<String> nomeMaeList = new ArrayList<String>();
	private List<Long> matriculaList = new ArrayList<Long>();
	private int indexNomeMaeSelecionada;
	private int indexMatriculaSelecionada;
	private String nomeMae;
	private Long matricula;
	
	public Usuario getUsuario() {
		return usuario;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
		this.nomeMae = usuario.getServidor().getMae();
		this.matricula = usuario.getServidor().getMatricula();
	}
	
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getNascimento() {
		return nascimento;
	}

	public void setNascimento(Date nascimento) {
		this.nascimento = nascimento;
	}
	
	public List<String> getNomeMaeList() {
		return this.nomeMaeList;
	}
	
	public void setNomeMaeList(List<String> nomeMaeList) {
		this.nomeMaeList = nomeMaeList;
	}
	
	public List<Long> getMatriculaList() {
		return matriculaList;
	}
	
	public void setMatriculaList(List<Long> matriculaList) {
		this.matriculaList = matriculaList;
	}
	

	public void  setIndexNomeMaeSelecionada(int indexNomeMaeSelecionada) {
		this.indexNomeMaeSelecionada = indexNomeMaeSelecionada;
	}
	
	public int getIndexNomeMaeSelecionada() {
		return this.indexNomeMaeSelecionada;
	}
	
	public void  setIndexMatriculaSelecionada(int indexMatriculaSelecionada) {
		this.indexMatriculaSelecionada = indexMatriculaSelecionada;
	}
	
	public int getIndexMatriculaSelecionada() {
		return this.indexMatriculaSelecionada;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}
	
}
