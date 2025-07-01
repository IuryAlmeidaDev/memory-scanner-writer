package br.gov.mt.sejudh.sigepen.model.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Acesso;
import br.gov.mt.sejudh.sigepen.model.Perfil;

public class UsuarioDTO {

	private long id;
	private String nome;
	private String login;
	private String email;
	private Status status;
	private String unidadePerfilSolicitadaResetSenha;
	private Date dataSolicitacaoResetSenha;
	private List<Acesso> acessos = new ArrayList<Acesso>();
	private String perfisToString;

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

	public List<Acesso> getAcessos() {
		return acessos;
	}

	public void setAcessos(List<Acesso> acessos) {
		setPerfisToStrig(acessos);
		this.acessos = acessos;
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

    public String getPerfisToString() {
        return perfisToString;
    }

    public void setPerfisToString(String perfisToStrig) {
        this.perfisToString = perfisToStrig;
    }

	public void setPerfisToStrig(List<Acesso> acessos) {

		StringBuilder perfisToString = new StringBuilder();

		for(Acesso acesso : acessos){
			for(Perfil perfil : acesso.getPerfis()){
				perfisToString.append(perfil.getNome());
				perfisToString.append(" | ");
			}
		}

		this.perfisToString = perfisToString.toString();
	}
}
