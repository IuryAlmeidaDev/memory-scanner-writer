package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.service.BiometriaService;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@ManagedBean
@RequestScoped
@URLMapping(id = "Instaladores", pattern = "/instaladores", viewId = "/WEB-INF/pages/outros/Instaladores.xhtml")
public class UtilBean {
	
	@Inject
	private BiometriaService biometriaService;
	
	@Inject
	private Logger log;
	
	public String getBiometriaVersao() {
		return "11122020"; //bioService.buscarVersaoAtualAplicativo();
	}
	
	public String refresh() {
		return "ok";
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
