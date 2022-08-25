package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;

import com.ocpsoft.pretty.PrettyContext;

@ManagedBean
@RequestScoped
public class UtilBean {

	public String refresh() {
		String viewId = PrettyContext.getCurrentInstance().getCurrentMapping().getPattern();
	    return viewId + "?faces-redirect=true";
	}
	
	public String getSigepenJsVersao() {
		return "07062021";
	}
}