package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.model.Justificativa;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarJustificativa", pattern = "/associar/justificativa", viewId = "/WEB-INF/pages/_dialogs/AssociarJustificativa.xhtml")
public class AssociarJustificativaBean extends MetodosDialogBean {

	private Justificativa justificativa;	
	private String descricao;
			
	@Inject
	private FacesUtil facesUtil;
	
	@URLAction(onPostback=false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		this.justificativa = (Justificativa) facesUtil.getObjetoSessao("justificativa");
		
		if (this.justificativa == null) {
			this.justificativa = new Justificativa();
		}
		return null;
	}	

	public void incluirJustificativa() {
		justificativa.setDescricao(descricao);
		facesUtil.fecharPaginaDialog(justificativa);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public Justificativa getJustificativa() {
		return justificativa;
	}


	public void setJustificativa(Justificativa justificativa) {
		this.justificativa = justificativa;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
