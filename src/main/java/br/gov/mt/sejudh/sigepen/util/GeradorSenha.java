package br.gov.mt.sejudh.sigepen.util;

import java.util.Scanner;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;

public class GeradorSenha {

	public static void main(String[] args) {		
	
		int iterations = 200000;
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		ByteSource salt = rng.nextBytes();
		System.out.print("Senha: ");
		@SuppressWarnings("resource")
		String senhaLimpa = new Scanner(System.in).nextLine();
		String senhaComHash = new Sha256Hash(senhaLimpa, salt, iterations).toBase64();
		
		System.out.println("Senha Gerada: " + senhaComHash);		
		System.out.println("Salt Gerado: " + salt.toBase64());
	}
}
