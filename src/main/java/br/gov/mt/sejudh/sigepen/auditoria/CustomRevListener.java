package br.gov.mt.sejudh.sigepen.auditoria;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.envers.RevisionListener;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class CustomRevListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevEntity customRevEntity = (CustomRevEntity) revisionEntity;


        if (getContext() != null && getContext().getExternalContext() != null) {
            customRevEntity.setUrl(getUrl());
            customRevEntity.setIp(getIp());
            customRevEntity.setHost(getHost());
            
            if (SecurityUtil.getUsuario() != null) {
                customRevEntity.setUsuarioId(SecurityUtil.getUsuario().getId());
            }
        } else {
            customRevEntity.setUrl("SISTEMA/AGENDADOR");
            customRevEntity.setIp("127.0.0.1");
            customRevEntity.setHost("LOCALHOST");
            customRevEntity.setUsuarioId(1L); 
        }
    }

    public FacesContext getContext() {
        return FacesContext.getCurrentInstance();
    }

    public HttpServletRequest getRequest() {
        FacesContext ctx = getContext();
        if (ctx != null && ctx.getExternalContext() != null) {
            return (HttpServletRequest) ctx.getExternalContext().getRequest();
        }
        return null;
    }

    public String getIp() {
        HttpServletRequest request = getRequest();
        if (request == null) return "127.0.0.1";
        
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public String getHost() {
        HttpServletRequest request = getRequest();
        return (request != null) ? request.getRemoteHost() : "localhost";
    }

    public String getUrl() {
        HttpServletRequest request = getRequest();
        return (request != null) ? request.getRequestURI() : "background-process";
    }
}