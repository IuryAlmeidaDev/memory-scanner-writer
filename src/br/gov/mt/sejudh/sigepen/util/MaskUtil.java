package br.gov.mt.sejudh.sigepen.util;

import java.text.SimpleDateFormat;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class MaskUtil {

	private static String telefone = "(99)9999-9999";
	private static String CPF = "999.999.999-99";
	private static String data = "99/99/9999";
	private static String patternData = "dd/mm/yyyy";
	private static String patternDataHora = "dd/mm/yyyy HH:mm:ss";
	private static String CEP = "99.999-999";
	private static SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat formatoDataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public static String getData() {
		return data;
	}

	public static String getPatternData() {
		return patternData;
	}

	public static String getPatternDataHora() {
		return patternDataHora;
	}

	public static String getCEP() {
		return CEP;
	}

	public String getTelefone() {
		return telefone;
	}

	public String getCPF() {
		return CPF;
	}

	public static SimpleDateFormat getFormatoData() {
		return formatoData;
	}

	public static SimpleDateFormat getFormatoDataHora() {
		return formatoDataHora;
	}
	
}