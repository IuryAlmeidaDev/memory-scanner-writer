package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.model.SubstituicaoFotoJustificativa;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarSubstituicaoFotoJustificativa", pattern = "/associar/associarSubstituicaoFotoJustificativa", viewId = "/WEB-INF/pages/_dialogs/AssociarSubstituicaoFotoJustificativa.xhtml")
public class AssociarSubstituicaoFotoJustificativaBean extends MetodosDialogBean {

	private SubstituicaoFotoJustificativa substituicaoFotoJustificativa;	
			
	@Inject
	private FacesUtil facesUtil;
	
	@URLAction(onPostback=false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		this.substituicaoFotoJustificativa = (SubstituicaoFotoJustificativa) facesUtil.getObjetoSessao("substituicaoFotoJustificativa");
		
		if (this.substituicaoFotoJustificativa == null) {
			this.substituicaoFotoJustificativa = new SubstituicaoFotoJustificativa();
		}
		return null;
	}	

	public void incluirJustificativa() {
		facesUtil.fecharPaginaDialog(substituicaoFotoJustificativa);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public SubstituicaoFotoJustificativa getSubstituicaoFotoJustificativa() {
		return substituicaoFotoJustificativa;
	}


	public void setSubstituicaoFotoJustificativa(SubstituicaoFotoJustificativa substituicaoFotoJustificativa) {
		this.substituicaoFotoJustificativa = substituicaoFotoJustificativa;
	}
	
}
