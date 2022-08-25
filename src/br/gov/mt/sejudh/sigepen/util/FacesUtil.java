package br.gov.mt.sejudh.sigepen.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;

@Named
@RequestScoped
public class FacesUtil implements Serializable{

	private static final long serialVersionUID = 3661001768806290249L;
	
	public void exibirPaginaDialog(String nomeDialog) {
		exibirPaginaDialog(nomeDialog, null, 0, 0);
	}
	
	public void exibirPaginaDialog(String nomeDialog, Integer altura, Integer largura) {
		exibirPaginaDialog(nomeDialog, null, altura, largura);
	}
	
	public void exibirPaginaDialog(String nomeDialog, Map<String, Object> options) {
		exibirPaginaDialog(nomeDialog, options, 0, 0);
	}
	
	public void exibirPaginaDialog(String nomeDialog, Map<String, Object> options, Integer altura, Integer largura) {
		if (options == null) {
			options = new HashMap<String, Object>();
			options.put("modal", true);
			options.put("draggable", false);
			options.put("resizable", false);
			options.put("closable", true);
		}

        if (altura != null && altura > 0) {
        	options.put("height", altura);
        	options.put("contentHeight", "'100%'");
        }
        
        if (largura != null && largura > 0) {
        	options.put("width", largura);
        	options.put("contentWidth", "'100%'");
        }

		RequestContext.getCurrentInstance().openDialog(nomeDialog, options, null);
	}
	
	public void fecharPaginaDialog(Object objeto) {
		RequestContext.getCurrentInstance().closeDialog(objeto);
	}
	
	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	
	public void exibirMensagemInfo(String mensagem, boolean msgVaiParaOutraPagina) {
		if (msgVaiParaOutraPagina) {
			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		}
		
		 FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, mensagem, mensagem);
	     getFacesContext().addMessage(null, facesMessage);
	}
	
	public void exibirMensagemErro(String mensagem, boolean msgVaiParaOutraPagina) {
		if (msgVaiParaOutraPagina) {
			FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		}
		
		FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem, mensagem);
        getFacesContext().addMessage(null, facesMessage);
	}
	
   	public Object getFlashParameter(String name) {
		return FacesContext.getCurrentInstance().getExternalContext().getFlash().get(name);
	}
   	
   	public Map<String, String> getParameters() {
   		return getFacesContext().getExternalContext().getRequestParameterMap();
   	}

	public void setFlashParameter(String name, Object value) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().put(name, value);
	}
	
	public void setObjetoSessao(String name, Object value) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(name, value);
	}
	
	public Object getObjetoSessao(String name) {
		return getObjetoSessao(name, true);
	}
	
	public Object getObjetoSessao(String name, boolean removerDaSessao) {
		Object objeto = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(name);
		
		if (objeto == null) {
			return null;
		}
		
		if (removerDaSessao) {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(name);
		}
		
		return objeto;
	}
	
	public String subString(String texto, int inicio, int fim){
		if(texto.length() > fim){
			texto = texto.substring(inicio, fim)+" ...";
		}
		return texto;
	}
	
	public void exibirMensagem(FacesMessage.Severity tipo, String titulo, String mensagem) {
		FacesMessage message = new FacesMessage(tipo, titulo, mensagem);
        RequestContext.getCurrentInstance().showMessageInDialog(message);
	}
	
	public String getQueryParameter(String nome) {
		return ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter(nome);
	}
	
	public void setQueryParameter(String nome, String valor) {
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(nome, valor);
	}
}
