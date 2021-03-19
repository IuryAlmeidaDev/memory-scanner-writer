package br.gov.mt.sejudh.sigepen.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;

@Named
@ApplicationScoped
public class Sigepen {
	public static final long SEJUDH_ID = 1;
	public static final String SEJUDH_SCHEMA = "SIGEPEN_OWNER_DES";
	public static final String SEJUDH_AUDIT_SCHEMA = "SIGEPEN_AUDIT_OWNER_DES";
	public static final String SEJUDH_CONTEXT = "sigepen";

	public static final String APPLICATION_RECUPERANDO_URL = getApplicationName() + "/recuperando";
	public static final String APPLICATION_LOGIN_URL = getApplicationName() + "/login";
	public static final String APPLICATION_DASHBOARD_URL = getApplicationName() + "/dashboard";
	public static final String APPLICATION_ALTERAR_SENHA_URL = getApplicationName() + "/usuario/alterarsenha";
	public static final String APPLICATION_SELECIONAR_UNIDADE_URL = APPLICATION_LOGIN_URL + "/unidade";
	public static final String APPLICATION_DOWNLOAD_URL = getApplicationName() + "/download";
	
	public static final String ARQUIVOS_PATH = System.getProperty("jboss.home.dir") + File.separator + "sigepen-arquivos";
	
	public static final String IP_NSERVER = "127.0.0.1";
	
	//Porta Homolog 25452
	//Porta Desenv 25453
	public static final int PORT_NSERVER = 25452;
	
	private static final String INSTALADOR = "InstaladorSigepenBiometriaV2.exe";
	private static final String DRIVER_SECUGEN = "SecuGen.zip";

	@PostConstruct
	public void init() {
		File f = new File(ARQUIVOS_PATH);		
		
		if (!f.exists()) {
			f.mkdir();
		}
	}
	
	public static long getIdSejudh() {
		return SEJUDH_ID;
	}
	
	public static String getApplicationName() {
		return "/" + SEJUDH_CONTEXT;
	}

	public static String getApplicationDashboardUrl() {
		return APPLICATION_DASHBOARD_URL;
	}
	
	public static String getApplicationDownloadUrl() {
		return APPLICATION_DOWNLOAD_URL;
	}

	public static Date getDataAtual() {
		Calendar c = Calendar.getInstance();
		return c.getTime();
	}
	
	public static String getAnoAtual() {
		return new SimpleDateFormat("yyyy").format(getDataAtual());
	}
	
	public static Date getDataConsultaMaiorIgual(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	public static Date getDataConsultaMenorIgual(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

	
	public static String formatarEspacos(String texto){
		if (texto != null) {
			texto = texto.replaceAll("\n", " ");
			texto = texto.replaceAll("\r", "");
			texto = texto.replaceAll("\t", "");
			texto = texto.trim().replaceAll("\\s+"," ");
			texto = texto.toUpperCase();
		}
		
		return texto;
	}
	
	public static long converteStringEmLong(String numero) {
		try {
			return Long.parseLong(numero);
		} catch (Exception e) {
			return (long) 0;
		}
	}

	public static byte[] decodeStringToByte(String base64String) {
		return Base64.decodeBase64(base64String);
	}

	public static String encodeByteToString(byte[] data) {
		return Base64.encodeBase64String(data);
	}
	
	public static String getUrl() {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String reqUrl = req.getRequestURL().toString();
		
		if (reqUrl.contains("mt.gov.br")) {
			reqUrl = reqUrl.replace("http:", "https:");
		}
		
		return reqUrl.substring(0, reqUrl.indexOf("/sigepen")) + getApplicationName();
	}
	
	private static String getUrlArquivos() {
		return getUrl() + "/arquivos/";
	}
	
	public static String getUrlInstalador() {
		return getUrlArquivos() + INSTALADOR;
	}

	public static String getUrlDriver(String driver) {
		switch (driver) {
			case "secugen":
				return getUrlArquivos() + DRIVER_SECUGEN;
		}
		 
		return "";
	}

	public static Boolean getIsProducao() {
		return System.getProperty("ambiente").equals("producao");
	}
	
	public static String getWsUrl() {
		String url = getUrl();
		
		url = url.substring(0, url.indexOf("/sigepen"))
				.replace("https:", "wss:")
				.replace("http:", "ws:")
				.replace("sispen.", "wso.")
				.concat(":8443/sigepen/biometria");
		
		return url;
	}

	public static String removerAcentuacao(String texto) {
		if (texto != null) {
			texto = texto.toUpperCase();
			texto = texto.replaceAll("[ÂÀÁÄÃ]","A"); 
			texto = texto.replaceAll("[ÊÈÉË]","E"); 
			texto = texto.replaceAll("[ÎÍÌÏ]","I"); 
			texto = texto.replaceAll("[ÔÕÒÓÖ]","O"); 
			texto = texto.replaceAll("[ÛÙÚÜ]","U"); 
			texto = texto.replaceAll("[Ýÿ]","Y"); 
			texto = texto.replaceAll("Ñ","N"); 
		}
		
		return texto;
	}
}
