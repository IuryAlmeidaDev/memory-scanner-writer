package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.dto.EstruturaDto;
import br.gov.mt.sejudh.sigepen.service.EstruturaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "AssociarEstrutura", pattern = "/associar/estrutura", viewId = "/WEB-INF/pages/_dialogs/AssociarEstrutura.xhtml"),
		@URLMapping(id = "AssociarQualquerEstrutura", pattern = "/associar/estrutura/qualquer", viewId = "/WEB-INF/pages/_dialogs/AssociarEstrutura.xhtml"),
})
public class AssociarEstruturaBean extends MetodosDialogBean {

	private DefaultTreeNode root;

	private boolean associarQualquerEstrutura = false;
	
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

			if(PrettyContext.getCurrentInstance().getCurrentMapping().getId().equals("AssociarQualquerEstrutura")){
				this.associarQualquerEstrutura=true;
			}
			
			carregarArvore();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}
	
	public void carregarArvore() {
		Long unidadeSelecionadaUsuario = null;
		try {
			unidadeSelecionadaUsuario = (Long) facesUtil.getObjetoSessao("unidadeSelecionadaUsuario");
		} catch (Exception e) {
		}

		root = new DefaultTreeNode( new EstruturaDto() , null);
		root.setExpanded(true);

		if(unidadeSelecionadaUsuario == null)
			unidadeSelecionadaUsuario = SecurityUtil.getUnidadeSelecionada().getId();
		
		List<EstruturaDto> estruturas = service.buscarEstruturasUnidade(unidadeSelecionadaUsuario);
		service.montarArvoreDaUnidade(estruturas, root);
	}
	
	public void selecionarEstrutura(EstruturaDto estrutura) {
		Estrutura e = service.buscaPorId(estrutura.getId());
		facesUtil.fecharPaginaDialog(e);
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

	public boolean isAssociarQualquerEstrutura() {
		return associarQualquerEstrutura;
	}

	public void setAssociarQualquerEstrutura(boolean associarQualquerEstrutura) {
		this.associarQualquerEstrutura = associarQualquerEstrutura;
	}
}
