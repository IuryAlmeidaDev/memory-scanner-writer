package br.gov.mt.sejudh.sigepen.biometrics;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class BiometriaDecoder implements Decoder.Text<BiometriaMessage> {

	private static final Logger log = LogManager.getLogger(BiometriaDecoder.class);

	@Override
	public BiometriaMessage decode(String str) throws DecodeException {

		BiometriaMessage bio = null;
		Gson gson;

		try {
			gson = new Gson();
			bio = gson.fromJson(str, BiometriaMessage.class);
		} catch (Exception e) {
			log.error("Erro ao decodificar BiometriaMessage", e);
		}

		return bio;
	}

	@Override
	public boolean willDecode(String str) {
		if (str == null) {
			return false;
		}

		try {
			JsonElement je = new JsonParser().parse(str);
			return (je != null);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void destroy() {	}

	@Override
	public void init(EndpointConfig arg0) {	}
}
