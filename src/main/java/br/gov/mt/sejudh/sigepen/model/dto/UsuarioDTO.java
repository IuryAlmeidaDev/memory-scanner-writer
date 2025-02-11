package br.gov.mt.sejudh.sigepen.model.dto;

import java.sql.Date;

import br.gov.mt.sejudh.sigepen.enumerator.Status;

public class UsuarioDTO {

	private long id;
	private String nome;
	private String login;
	private String email;
	private Status status;
	private String unidadePerfilSolicitadaResetSenha;
	private Date dataSolicitacaoResetSenha;

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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status.getDescricao();
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getUnidadePerfilSolicitadaResetSenha() {
		return unidadePerfilSolicitadaResetSenha;
	}

	public void setUnidadePerfilSolicitadaResetSenha(String unidadePerfilSolicitadaResetSenha) {
		this.unidadePerfilSolicitadaResetSenha = unidadePerfilSolicitadaResetSenha;
	}

	public Date getDataSolicitacaoResetSenha() {
		return dataSolicitacaoResetSenha;
	}

	public void setDataSolicitacaoResetSenha(Date dataSolicitacaoResetSenha) {
		this.dataSolicitacaoResetSenha = dataSolicitacaoResetSenha;
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", login=" + login + "]";
	}
}
