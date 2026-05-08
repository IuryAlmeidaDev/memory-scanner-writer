package br.gov.mt.sejudh.sigepen.biometrics;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class BiometriaEncoder implements Encoder.Text<BiometriaMessage> {

	private static final Logger log = LogManager.getLogger(BiometriaEncoder.class);

	@Override
	public String encode(BiometriaMessage bio) throws EncodeException {
		Gson gson;
		String retorno = null;

		try {
			gson = new Gson();
			retorno = gson.toJson(bio);
		} catch (Exception e) {
			log.error("Erro ao codificar BiometriaMessage", e);
		}

		return retorno;
	}

	@Override
	public void destroy() { }

	@Override
	public void init(EndpointConfig arg0) { }
}
