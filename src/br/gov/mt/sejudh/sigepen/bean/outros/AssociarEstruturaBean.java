package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;

import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.service.EstruturaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarEstrutura", pattern = "/associar/estrutura", viewId = "/WEB-INF/pages/_dialogs/AssociarEstrutura.xhtml")
public class AssociarEstruturaBean extends MetodosDialogBean {

	private DefaultTreeNode root;
	
	@Inject
	private EstruturaService service;	
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			if (this.isDialogInvalida()) {
				return "pretty:dashboard";
			}
			
			carregarArvore();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}
	
	public void carregarArvore() {
		root = new DefaultTreeNode( new Estrutura() , null);
		root.setExpanded(true);
		Estrutura estrutura = service.buscaEstraturaRaizUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		service.montarArvoreEstrutura(estrutura, root);
		service.capacidadeTotalEstrutura(root);
	}
	
	public void selecionarEstrutura(Estrutura estrutura) {		
		facesUtil.fecharPaginaDialog(estrutura);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public DefaultTreeNode getRoot() {
		return root;
	}

	public void setRoot(DefaultTreeNode root) {
		this.root = root;
	}
}
