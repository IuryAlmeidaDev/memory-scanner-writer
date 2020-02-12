package br.gov.mt.sejudh.sigepen.security;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;

import br.gov.mt.sejudh.sigepen.model.Usuario;

public class HashedPassSaltGenerator {

	public static Usuario gerarSenhaESalt(Usuario usuario, String senha) {
		int iterations = 200000;
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		ByteSource salt = rng.nextBytes();

		String senhaLimpa = senha;
		String senhaComHash = new Sha256Hash(senhaLimpa, salt, iterations).toBase64();

		String usuarioSalt = salt.toBase64();
		String usuarioSenha = senhaComHash;
		
		usuario.setSalt(usuarioSalt);
		usuario.setSenha(usuarioSenha);
		
		return usuario;
	}
}
