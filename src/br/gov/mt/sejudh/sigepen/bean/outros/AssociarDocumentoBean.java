package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarDocumento", pattern = "/associar/documento", viewId = "/WEB-INF/pages/_dialogs/AssociarDocumento.xhtml")
public class AssociarDocumentoBean extends MetodosDialogBean {

	private Documento documento;
		
	@Inject
	private FacesUtil facesUtil;
	
	@URLAction(onPostback=false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		this.documento = (Documento) facesUtil.getObjetoSessao("documento");
		
		if (this.documento == null) {
			this.documento = new Documento();
		}
		
		return null;
	}	

	public void incluirDocumento() {
		facesUtil.fecharPaginaDialog(documento);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}
	
}
