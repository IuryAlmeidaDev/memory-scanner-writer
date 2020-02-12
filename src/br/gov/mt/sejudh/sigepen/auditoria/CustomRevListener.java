package br.gov.mt.sejudh.sigepen.auditoria;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.envers.RevisionListener;

import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class CustomRevListener  implements RevisionListener{

	@Override
	public void newRevision(Object revisionEntity) {
		
		CustomRevEntity customRevEntity = (CustomRevEntity) revisionEntity;
       
		customRevEntity.setUrl(getUrl());

		customRevEntity.setUsuarioId(SecurityUtil.getUsuario().getId());
		
		customRevEntity.setIp(getIp());
		
		customRevEntity.setHost(getHost());
	}
	
	public FacesContext getContext(){
		FacesContext context = FacesContext.getCurrentInstance();
		return context;
	}
	
	public  HttpServletRequest getRequest(){
		HttpServletRequest request = (HttpServletRequest) getContext().getExternalContext().getRequest();
		return request;
	}
	
	public String getIp() {
		String ip =  getRequest().getHeader("X-FORWARDED-FOR");
		if (ip == null) {
		    ip = getRequest().getRemoteAddr();
		}		
       
        return ip;
    }
	
	public String getHost() {
        String host = getRequest().getRemoteHost();
        return host;
    }
	
	public String getUrl(){
		String url = getRequest().getRequestURI();
		return url;
	}
}
