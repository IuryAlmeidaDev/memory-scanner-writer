package br.gov.mt.sejudh.sigepen.util;

import java.util.concurrent.TimeUnit;

public class Temporizador {
	private long inicio;
	private long diferenca;
	private long resultado;
	private String nomeMetodo;
	
	private Temporizador(String nomeMetodo) {
		this.inicio = System.nanoTime();
		this.nomeMetodo = nomeMetodo;
	}
	
	public static Temporizador start(String nomeMetodo) {
		return new Temporizador(nomeMetodo);
	}
	
	public void stop() {
		this.diferenca = System.nanoTime() - this.inicio;
		this.resultado = TimeUnit.MILLISECONDS.convert(this.diferenca, TimeUnit.NANOSECONDS);
	}
	
	public String getResult() {
		return nomeMetodo + " demorou " + resultado + " ms.";
	}
}