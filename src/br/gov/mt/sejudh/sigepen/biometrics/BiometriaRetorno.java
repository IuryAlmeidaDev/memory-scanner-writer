package br.gov.mt.sejudh.sigepen.biometrics;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;

import org.apache.commons.lang3.NotImplementedException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.gov.mt.sejudh.sigepen.model.dto.PessoaBiometriaWSDTO;
import br.gov.mt.sejudh.sigepen.service.BiometriaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@RequestScoped
public class BiometriaRetorno {

	private static BiometriaOperacao operacao;
	
	private static String valor;
	
	private static FacesUtil facesUtil;
	
	public static <T> T getValor(FacesUtil faces) {
		facesUtil = faces;
		
		return getValor();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValor() {
		switch (operacao) {
			case Pesquisar:
				return new Gson().fromJson(valor, new TypeToken<ArrayList<Long>>(){}.getType());
			case Registrar:
				return (T) Long.valueOf(valor);
			case Capturar:
				return (T) getGenericTypeValue(String.class, valor);
			case Coletar:
				return (T) new BiometriaService().converterBiometriaColetadaAppEmBiometriaObjeto(valor);
			case RegistroEncontrado:
				return (T) new Gson().fromJson(valor, PessoaBiometriaWSDTO.class);
			default:
				throw new NotImplementedException("Operação não implementada. " + operacao.toString());
		}
	}
	
	private static <T> T getGenericTypeValue(Class<T> tipo, String obj) {
		return tipo.cast(obj);
	}

	public static BiometriaOperacao getOperacao(FacesUtil faces) {
		facesUtil = faces;
		Map<String, String> map = facesUtil.getFacesContext().getExternalContext().getRequestParameterMap();
		BiometriaOperacao retorno = null;
		
		Optional<Entry<String, String>> entry = map.entrySet().stream().filter(e -> e.getKey().contains("execute")).findFirst();
		
		if (entry.isPresent()) {
			String[] split = entry.get().getKey().split("@");
			operacao = BiometriaOperacao.getValue(split[1]);
			valor = entry.get().getValue();
			
			retorno = operacao;
		}
		
	    return retorno;
	}
}
