package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.model.ExclusaoDesligamentoJustificativa;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarExclusaoDesligamentoJustificativa", pattern = "/associar/justificativa/desligamento", viewId = "/WEB-INF/pages/_dialogs/AssociarExclusaoDesligamentoJustificativa.xhtml")
public class AssociarJustificativaExclusaoDesligamentoBean extends MetodosDialogBean {

	private ExclusaoDesligamentoJustificativa exclusaoDesligamentoJustificativa;	
//	private String descricao;
			
	@Inject
	private FacesUtil facesUtil;
	
	@URLAction(onPostback=false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		this.exclusaoDesligamentoJustificativa = (ExclusaoDesligamentoJustificativa) facesUtil.getObjetoSessao("exclusaoDesligamentoJustificativa");
		
		if (this.exclusaoDesligamentoJustificativa == null) {
			this.exclusaoDesligamentoJustificativa = new ExclusaoDesligamentoJustificativa();
		}
		return null;
	}	

	public void incluirJustificativa() {
		facesUtil.setObjetoSessao("exclusaoDesligamentoJustificativa",exclusaoDesligamentoJustificativa);
		facesUtil.fecharPaginaDialog(exclusaoDesligamentoJustificativa);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public ExclusaoDesligamentoJustificativa getExclusaoDesligamentoJustificativa() {
		return exclusaoDesligamentoJustificativa;
	}


	public void setExclusaoDesligamentoJustificativa(ExclusaoDesligamentoJustificativa exclusaoDesligamentoJustificativa) {
		this.exclusaoDesligamentoJustificativa = exclusaoDesligamentoJustificativa;
	}

//	public String getDescricao() {
//		return descricao;
//	}
//
//	public void setDescricao(String descricao) {
//		this.descricao = descricao;
//	}
	
}
