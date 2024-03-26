package br.gov.mt.sejudh.sigepen.biometrics;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

public class BiometriaEncoder implements Encoder.Text<BiometriaMessage> {

	@Override
	public String encode(BiometriaMessage bio) throws EncodeException {
		Gson gson;
		String retorno = null;
		
		try {
			gson = new Gson();
			retorno = gson.toJson(bio);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retorno;
	}

	@Override
	public void destroy() { }

	@Override
	public void init(EndpointConfig arg0) { }
}