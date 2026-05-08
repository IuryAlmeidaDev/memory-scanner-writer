package br.gov.mt.sejudh.sigepen.ws;

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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import br.gov.mt.sejudh.sigepen.model.BiometriaTemporaria;
import br.gov.mt.sejudh.sigepen.model.dto.PessoaBiometriaWSDTO;
import br.gov.mt.sejudh.sigepen.service.BiometriaService;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@Path("/biometria")
@RequestScoped
public class BiometriaWebService {
	
	@Inject
	private BiometriaService biometriaService;

	@Inject
	private Logger log;
	
	@POST
	@Path("/verificar-amostras-identicas")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String verificarSeAmostrasSaoIdenticasPorImagem(String template) {
		Boolean resultado = null;
		
		try {
			resultado = biometriaService.verificarSeAmostrasSaoIdenticasPorImagem(template);
		} catch (Exception e) {
			LoggerUtil.logWS(e, log);
			
			JsonObject js = new JsonObject();
			js.addProperty("erro", "Não foi possível verificar amostras biométricas.");
			
			return js.toString();
		}
		
		return resultado.toString();
	}
	
	@POST
	@Path("/verificar-amostras-identicas-v2")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String verificarSeAmostrasSaoIdenticasPorTemplate(String template) {
		Boolean resultado = null;
		
		try {
			resultado = biometriaService.verificarSeAmostrasSaoIdenticasPorTemplate(template);
		} catch (Exception e) {
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
	public String verificarSeExistemDedosRepetidosPorImagem(String template) {
		String dedosRepetidos = null;
		
		try {
			dedosRepetidos = biometriaService.verificarSeExistemDedosRepetidosPorImagem(template);
		} catch (Exception e) {
			LoggerUtil.logWS(e, log);
			
			JsonObject js = new JsonObject();
			js.addProperty("erro", "Não foi possível verificar amostras biométricas.");
			
			return js.toString();
		}
		
		return dedosRepetidos;
	}
	
	@POST
	@Path("/verificar-dedos-repetidos-v2")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String verificarSeExistemDedosRepetidosPorTemplate(String template) {
		String dedosRepetidos = null;
		
		try {
			dedosRepetidos = biometriaService.verificarSeExistemDedosRepetidosPorTemplate(template);
		} catch (Exception e) {
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
		} catch (RuntimeException e) {
			LoggerUtil.logWS(e, log);
			JsonObject js = new JsonObject();
			js.addProperty("erro", e.getMessage());
			Throwable root = e;
			while (root.getCause() != null && root.getCause() != root) {
				root = root.getCause();
			}
			if (root != e) {
				js.addProperty("detalhe", root.getClass().getName() + ": " + root.getMessage());
			}

			return js.toString();
		} catch (Exception e) {
			LoggerUtil.logWS(e, log);
		}

		return biometrias.toString();
	}
	
	@POST
	@Path("/buscar-ids")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String buscarIdsPorTemplate(String template) {
		Long idPessoa = (long) 0;
		
		try {
			idPessoa = biometriaService.pesquisaPorTemplateParaPessoaWS(template);
			
			if (idPessoa > 0) {
				PessoaBiometriaWSDTO pessoa = biometriaService.buscarIdsVinculadosAPessoa(idPessoa);

				if (pessoa != null) {
					return new Gson().toJson(pessoa);
				} else {
					return null;
				}
			}
			
			return null;
		} catch (Exception e) {
			LoggerUtil.logWS(e, log);
			JsonObject js = new JsonObject();
			js.addProperty("erro", e.getMessage());
			Throwable root = e;
			while (root.getCause() != null && root.getCause() != root) {
				root = root.getCause();
			}
			if (root != e) {
				js.addProperty("detalhe", root.getClass().getName() + ": " + root.getMessage());
			}

			return js.toString();
		}
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
			LoggerUtil.logWS(e, log);
			
			JsonObject js = new JsonObject();
			js.addProperty("erro", "Não foi possível salvar biometria temporaria");
			
			return js.toString();
		}
		
		return String.valueOf(bt.getId());
	}
}
