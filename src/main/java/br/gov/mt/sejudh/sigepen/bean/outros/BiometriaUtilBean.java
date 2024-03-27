package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.biometrics.BiometriaDedoPosicaoImagem;
import br.gov.mt.sejudh.sigepen.enumerator.BiometriaTipoDedo;
import br.gov.mt.sejudh.sigepen.model.Biometria;
import br.gov.mt.sejudh.sigepen.model.BiometriaAmostra;
import br.gov.mt.sejudh.sigepen.model.BiometriaDedo;
import br.gov.mt.sejudh.sigepen.service.BiometriaService;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@RequestScoped
@URLMapping(id = "Instaladores", pattern = "/instaladores", viewId = "/WEB-INF/pages/outros/Instaladores.xhtml")
public class BiometriaUtilBean {
	
	@Inject
	private BiometriaService biometriaService;
	
	@Inject
	private Logger log;
	
	private String bioJsonString;
	private JsonObject bioJsonObject;
	
	public JsonObject getBiometriaJsonObject() {	
		return this.bioJsonObject;
	}
	
	public void setBioJsonString(String bioJsonString, boolean gerarJsonObject) {
		this.bioJsonString = bioJsonString;
		
		if (gerarJsonObject) {
			try {
				this.bioJsonObject = new JsonParser().parse(bioJsonString).getAsJsonObject();
			} catch (Exception e) {
				this.bioJsonObject = null;
			}
		}
	}

	public String getBiometriaJsonString() {
		return this.bioJsonString;
	}
	
	public byte[] getBiometriaTemplate() {
		String templString = bioJsonObject.get("template").getAsString();
		byte[] template = Sigepen.decodeStringToByte(templString);
		
		return template;
	}
	
	public ArrayList<byte[]> getAmostrasFromJson() {
		ArrayList<byte[]> amostras = new ArrayList<>();
		ArrayList<String> amostrasJson = new Gson().fromJson(this.bioJsonString, new TypeToken<ArrayList<String>>(){}.getType());
		
		for (String str : amostrasJson) {
			byte[] imagem = Sigepen.decodeStringToByte(str);
			amostras.add(imagem);
		}
		
		return amostras;
	}
	
	public ArrayList<BiometriaDedoPosicaoImagem> getDedosListFromJson() {
		ArrayList<BiometriaDedoPosicaoImagem> dedosJson = new Gson().fromJson(this.bioJsonString, new TypeToken<ArrayList<BiometriaDedoPosicaoImagem>>(){}.getType());
		return dedosJson;
	}
	
	public Biometria getBiometriaFromJson(Biometria biometria) {
		JsonElement joDedos = this.bioJsonObject.get("dedos");
		
		for (JsonElement jeDedo : joDedos.getAsJsonArray()) {
			BiometriaDedo dedo = new BiometriaDedo();
			JsonObject joDedo = jeDedo.getAsJsonObject();
			
			JsonElement joAmostras = joDedo.get("amostras");
			
			for (JsonElement jeAmostra : joAmostras.getAsJsonArray()) {
				BiometriaAmostra amostra = new BiometriaAmostra();
				JsonObject joAmostra = jeAmostra.getAsJsonObject();
				
				String templateAmostraString = joAmostra.get("template").getAsString();
				String imagemAmostraString = joAmostra.get("imagem").getAsString();
				Integer qualidadeAmostra = joAmostra.get("qualidade").getAsInt();
				Integer numeroAmostra = joAmostra.get("numero").getAsInt();
				
				byte[] templateAmostra = Sigepen.decodeStringToByte(templateAmostraString);
				byte[] imagemAmostra = Sigepen.decodeStringToByte(imagemAmostraString);
				
				amostra.setQualidade(qualidadeAmostra);
				amostra.setAmostraNumero(numeroAmostra);
				amostra.setData(templateAmostra);
				amostra.setImagem(imagemAmostra);
				amostra.setDedo(dedo);
				
				dedo.getAmostras().add(amostra);
			}
			
			BiometriaTipoDedo tipoDedo = BiometriaTipoDedo.valueOf(joDedo.get("tipo").getAsString());
			
			dedo.setTipoDedo(tipoDedo);
			dedo.setBiometria(biometria);
			biometria.getDedos().add(dedo);
		}
		
		return biometria;
	}

	
	public String getBiometriaJsVersao(String modulo) {
		switch(modulo.toLowerCase()) {
			case "instaladores":
				return "09032022";
			case "core":
				return "10082021";
			case "cliente":
				return "10082021";
			case "entrada-saida":
				return "10082021";
			default:
				return "0";
		}
	}

	public String getBiometriaVersaoAtual() {
		String versao = "";
		
		try {
			versao = biometriaService.buscarVersaoAtualAplicativo();
		} catch(Exception e) {
			LoggerUtil.logErro(e, log);
		}
		
		return versao;
	}
}
