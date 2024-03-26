package br.gov.mt.sejudh.sigepen.biometrics;

import com.google.gson.Gson;

public class BiometriaMessage {

	private String acao;
	private String valor;

	public static BiometriaMessage converterJsonEmMensagem(String json) {
		return new Gson().fromJson(json, BiometriaMessage.class);
	}
	
	public String converterEmJsonString() {
		return new Gson().toJson(this);
	}
	
	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
