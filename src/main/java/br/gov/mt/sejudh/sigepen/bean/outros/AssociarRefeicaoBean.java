package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.model.Refeicao;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarRefeicao", pattern = "/associar/refeicao", viewId = "/WEB-INF/pages/_dialogs/AssociarRefeicao.xhtml")
public class AssociarRefeicaoBean extends MetodosDialogBean {

	private Refeicao refeicao;
	private List<TipoRefeicao> tiposRefeicao;

	@Inject
	private TipoRefeicaoService service;
	
	@Inject
	private FacesUtil facesUtil;

	@URLAction(onPostback = false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		this.tiposRefeicao = service.listarTodos();

		this.refeicao = (Refeicao) facesUtil.getObjetoSessao("refeicao");

		if (this.refeicao == null) {
			this.refeicao = new Refeicao();
		}

		return null;
	}

	public void incluirRefeicao() {
		facesUtil.fecharPaginaDialog(refeicao);
	}

	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public Refeicao getRefeicao() {
		return refeicao;
	}

	public void setRefeicao(Refeicao refeicao) {
		this.refeicao = refeicao;
	}

	public List<TipoRefeicao> getTiposRefeicao() {
		return tiposRefeicao;
	}

	public void setTiposRefeicao(List<TipoRefeicao> tiposRefeicao) {
		this.tiposRefeicao = tiposRefeicao;
	}

}
