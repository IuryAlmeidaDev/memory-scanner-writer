package br.gov.mt.sejudh.sigepen.model.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.gov.mt.sejudh.sigepen.model.Usuario;

public class UsuarioResertSenhaDTO {

	private Usuario usuario;
	private String cpf;
	private Date nascimento;
	private Set<String> nomeMaeList = new HashSet<String>();
	private Set<Long> matriculaList = new HashSet<Long>();
	private String nomeMaeSelecionada;
	private Long matriculaSelecionada;
	private boolean respostaNomeMaeCorreta = false;
	private boolean respostaMatriculaCorerta = false;
	
	public Usuario getUsuario() {
		return usuario;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
		this.nomeMaeList.add(usuario.getServidor().getMae());
		this.matriculaList.add(usuario.getServidor().getMatricula());
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
	
	public Set<String> getNomeMaeList() {
		return nomeMaeList;
	}
	
	public void setNomeMaeList(Set<String> nomeMaeList) {
		this.nomeMaeList = nomeMaeList;
	}
	
	public Set<Long> getMatriculaList() {
		return matriculaList;
	}
	
	public void setMatriculaList(Set<Long> matriculaList) {
		this.matriculaList = matriculaList;
	}
	
	public void  setNomeMaeSelecionada(String nomeMaeSelecionada) {
		this.nomeMaeSelecionada = nomeMaeSelecionada;
	}
	
	public String getNomeMaeSelecionada() {
		return this.nomeMaeSelecionada;
	}
	
	public void  setMatriculaSelecionada(Long matriculaSelecionada) {
		this.matriculaSelecionada = matriculaSelecionada;
	}
	
	public Long getMatriculaSelecionada() {
		return this.matriculaSelecionada;
	}
	
	public boolean isRespostaNomeMaeCorreta() {
		return respostaNomeMaeCorreta;
	}

	public void setRespostaNomeMaeCorreta(boolean respostaNomeMaeCorreta) {
		this.respostaNomeMaeCorreta = respostaNomeMaeCorreta;
	}

	public boolean isRespostaMatriculaCorerta() {
		return respostaMatriculaCorerta;
	}

	public void setRespostaMatriculaCorerta(boolean respostaMatriculaCorerta) {
		this.respostaMatriculaCorerta = respostaMatriculaCorerta;
	}
	
}
