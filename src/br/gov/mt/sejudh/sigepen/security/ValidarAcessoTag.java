package br.gov.mt.sejudh.sigepen.security;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

import org.apache.shiro.SecurityUtils;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

import com.ocpsoft.pretty.PrettyContext;

public class ValidarAcessoTag extends TagHandler {
	
	public ValidarAcessoTag(TagConfig config) {
		super(config);
	}
	
	public ValidarAcessoTag(ComponentConfig config) {
		super(config);
	}
	
	@Override
	public void apply(FaceletContext context, UIComponent component) throws IOException {
		if (FacesContext.getCurrentInstance().getCurrentPhaseId() != PhaseId.RENDER_RESPONSE) {
			nextHandler.apply(context, component);
			return;
		}
		
		validarPermissoes(context, component);
	}
	
	private void validarPermissoes(FaceletContext context, UIComponent component) throws IOException {
		String tela = PrettyContext.getCurrentInstance().getCurrentMapping().getId();
		
		//TODO Retirar após inserir permissão para unificar recuperando
		if (tela.equals("UnificarRecuperando") || tela.equals("ListarUnificarRecuperando") || possuiPermissao(tela)) {
			nextHandler.apply(context, component);
		} else {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("permissao", "Usuário não tem permissão para acessar: " + PrettyContext.getCurrentInstance().getCurrentMapping().getPattern());			
			FacesContext.getCurrentInstance().getExternalContext().redirect(Sigepen.APPLICATION_DASHBOARD_URL);
		}
	}
	
	private boolean possuiPermissao(String tela) {
		return SecurityUtils.getSubject().isPermitted(tela);
	}
}
