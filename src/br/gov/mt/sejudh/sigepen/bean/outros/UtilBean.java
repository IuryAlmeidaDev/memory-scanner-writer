package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.service.BiometriaService;

@ManagedBean
@ViewScoped
@URLMapping(id = "Instaladores", pattern = "/instaladores", viewId = "/WEB-INF/pages/outros/Instaladores.xhtml")
public class UtilBean {

	@Inject
	private BiometriaService bioService;
	
	public String getBiometriaVersao() {
		return bioService.buscarVersaoAtualAplicativo();
	}
}
