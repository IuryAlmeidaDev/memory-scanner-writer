package br.gov.mt.sejudh.sigepen.biometrics;

import java.util.Arrays;

import org.apache.commons.lang3.NotImplementedException;

public enum BiometriaOperacao {

	Pesquisar("pesquisar"), //Retorna lista de IDs de pessoas encontradas
	Registrar("registrar"), //Retorna ID da biometria temporária que deve ser buscada no bean e vinculada ao objeto da pessoa
	Capturar("capturar"), //Retorna template do dedo capturado
	Coletar("coletar"), //Retorna Objeto Biometria completo dos dedos capturados, imagens e templates
	RegistroEncontrado("registro_encontrado"); //Json retornado pelo app biométrico
	
	String operacao;

	BiometriaOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getOperacao() {
		return operacao;
	}

	public static BiometriaOperacao getValue(String operacao) throws NotImplementedException {		
        return Arrays.stream(values())
        		.filter(op -> op.getOperacao().equals(operacao))
        		.findFirst()
        		.orElseThrow(() -> new NotImplementedException("Operação não implementada na biometria: " + operacao));
	}
}
