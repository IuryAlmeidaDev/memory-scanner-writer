package br.gov.mt.sejudh.sigepen.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import br.gov.mt.sejudh.sigepen.model.BiometriaTemporaria;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.service.BiometriaService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@Path("/biometria")
@RequestScoped
public class BiometriaWebService {
	
	@Inject
	private BiometriaService biometriaService;
	
	@Inject
	private RecuperandoService recuperandoService;
	
	@Inject
	private Logger log;
	
	@POST
	@Path("/verificar-amostras-identicas")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String verificarSeAmostrasSaoIdenticas(String template) {
		Boolean resultado = null;
		
		try {
			resultado = biometriaService.verificarSeAmostrasSaoIdenticas(template);
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.logWS(e, log);
			
			JsonObject js = new JsonObject();
			js.addProperty("erro", "Não foi possível verificar amostras biométricas.");
			
			return js.toString();
		}
		
		return resultado.toString();
	}
	
	@POST
	@Path("/verificar-dedos-repetidos")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String verificarSeExistemDedosRepetidos(String template) {
		String dedosRepetidos = null;
		
		try {
			dedosRepetidos = biometriaService.verificarSeExistemDedosRepetidos(template);
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.logWS(e, log);
			
			JsonObject js = new JsonObject();
			js.addProperty("erro", "Não foi possível verificar amostras biométricas.");
			
			return js.toString();
		}
		
		return dedosRepetidos;
	}
	
	@POST
	@Path("/pesquisar")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String buscarBiometriaPorTemplate(String template) {
		List<Long> biometrias = new ArrayList<Long>();
		
		try {
			biometrias = biometriaService.pesquisarBiometriaPorTemplateString(template);
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.logWS(e, log);
			JsonObject js = new JsonObject();
			js.addProperty("erro", e.getMessage());
			
			return js.toString();
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.logWS(e, log);
		}
		
		return biometrias.toString();
	}
	
	@POST
	@Path("/buscar-id-recuperando")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String buscarRecuperandoIdPorTemplate(String template) {
		Long idRecuperando = (long) 0;
		
		try {
			idRecuperando = biometriaService.pesquisaPorTemplateParaRecuperando(template);
			
			if (idRecuperando > 0) {
				Recuperando recup = recuperandoService.buscarRecuperandoSimplesPorIdPessoa(idRecuperando);
				
				if (recup != null) {
					idRecuperando = recup.getId();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			LoggerUtil.logWS(e, log);
			JsonObject js = new JsonObject();
			js.addProperty("erro", e.getMessage());
			
			return js.toString();
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.logWS(e, log);
		}
		
		return String.valueOf(idRecuperando);
	}
	
	@POST
	@Path("/salvar")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String salvarBiometriaTemporaria(String json) {
		BiometriaTemporaria bt = null;
		
		try {
			bt = biometriaService.salvarBiometriaTemporaria(json);
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.logWS(e, log);
			
			JsonObject js = new JsonObject();
			js.addProperty("erro", "Não foi possível salvar biometria temporaria");
			
			return js.toString();
		}
		
		return String.valueOf(bt.getId());
	}
	
	@POST
	@Path("/versao-atual")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String buscarVersaoAtualAplicativo(String versaoApp) {
		String versao = "";
		
		try {
			versao = biometriaService.buscarVersaoAtualAplicativo(versaoApp);
		} catch(Exception e) {
			e.printStackTrace();
			LoggerUtil.logWS(e, log);
			
			JsonObject js = new JsonObject();
			js.addProperty("erro", "Não foi possível verificar versão do aplicativo.");
			
			return js.toString();
		}
		
		return versao;
	}
}
