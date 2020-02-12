package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.util.FacesUtil;

import com.ocpsoft.pretty.PrettyContext;

public abstract class MetodosDialogBean {
	
	@Inject
	private FacesUtil facesUtil;

	protected boolean isDialogInvalida() {
		String dialogIdServidor = (String)facesUtil.getObjetoSessao("pfdlcid");
		
		if (dialogIdServidor == null) {
			return true;
		}
		
		String dialogIdcliente = PrettyContext.getCurrentInstance().getRequestQueryString().getParameter("pfdlgcid");
		
		if (dialogIdcliente == null || !dialogIdcliente.equals(dialogIdServidor)) {
			return true;
		}
		
		return false;
	}
}
