package br.gov.mt.sejudh.sigepen.realm;

public class UsuarioSenhaSalt {

	private String senha;
	private String salt;
	
	public UsuarioSenhaSalt(String senha, String salt) {
		this.senha = senha;
		this.salt = salt;
	}
	
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
}
