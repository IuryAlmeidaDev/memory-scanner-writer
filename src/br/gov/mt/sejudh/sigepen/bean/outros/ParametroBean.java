package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.model.Parametro;
import br.gov.mt.sejudh.sigepen.service.ParametroService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "Parametro", pattern = "/parametros", viewId = "/WEB-INF/pages/parametro/parametros.xhtml")
public class ParametroBean {

	private Parametro param;
	
	private List<Parametro> params;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private ParametroService service;
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			if (!SecurityUtil.getLoginUsuario().equals("ADMIN")) {
				return "pretty:dashboard";
			}
			
			carregarParametros();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
		}
		
		return null;
	}
	
	private void carregarParametros() {
		params = service.getParametrosSistema();
	}
	
	public void salvar(ActionEvent event) {
		try {
			service.salvar(this.param);
			carregarParametros();
			facesUtil.exibirMensagemInfo("Parametro " + this.param.getNome() + " salvo com sucesso!", true);
			
			this.param = null;
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar parametro (" + this.param.getNome() + "): " + e.getMessage());
		}
	}
	
	public void configurarParametro(ActionEvent event) {
		this.param = (Parametro) event.getComponent().getAttributes().get("parametro");
		
		if (this.param == null) {
			this.param = new Parametro();
		}
	}
	
	public void excluir(Parametro p) {
		try {
			service.excluir(p);
			carregarParametros();
			facesUtil.exibirMensagemInfo("Parametro " + p.getNome() + " excluído com sucesso!", false);
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir param (" + p.getNome() + "): " + e.getMessage());
		}
	}

	public void setParams(List<Parametro> params) {
		this.params = params;
	}

	public List<Parametro> getParams() {
		return params;
	}

	public Parametro getParam() {
		return param;
	}

	public void setParam(Parametro param) {
		this.param = param;
	}
	
}
