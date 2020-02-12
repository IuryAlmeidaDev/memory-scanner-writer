package br.gov.mt.sejudh.sigepen.biometrics;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import br.gov.mt.sejudh.sigepen.enumerator.BiometriaTipoDedo;
import br.gov.mt.sejudh.sigepen.model.Biometria;
import br.gov.mt.sejudh.sigepen.model.BiometriaAmostra;
import br.gov.mt.sejudh.sigepen.model.BiometriaDedo;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class BiometriaUtil {
	
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
}
