package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@RequestScoped
@URLMapping(id = "Instaladores", pattern = "/instaladores", viewId = "/WEB-INF/pages/outros/Instaladores.xhtml")
public class UtilBean {
	
	public String getBiometriaVersao() {
		return "26032020"; //bioService.buscarVersaoAtualAplicativo();
	}
	
	public String refresh() {
		return "ok";
	}
}
