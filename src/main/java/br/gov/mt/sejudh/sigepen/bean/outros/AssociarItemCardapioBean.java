package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.model.Alimento;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.ItemCardapio;
import br.gov.mt.sejudh.sigepen.service.AlimentoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarItemCardapio", pattern = "/associar/itemCardapio", viewId = "/WEB-INF/pages/_dialogs/AssociarItemCardapio.xhtml")
public class AssociarItemCardapioBean extends MetodosDialogBean {

	private ItemCardapio itemCardapio;
	private List<Alimento> alimentos;

	@Inject
	private AlimentoService alimentoService;

	@Inject
	private FacesUtil facesUtil;

	@URLAction(onPostback = false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		this.alimentos = alimentoService.listarAtivos();
		this.itemCardapio = (ItemCardapio) facesUtil.getObjetoSessao("itemCardapio");

		if (this.itemCardapio == null) {
			this.itemCardapio = new ItemCardapio();
		}

		return null;
	}

	public void incluirItemCardapio() {
		facesUtil.fecharPaginaDialog(itemCardapio);
	}

	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public ItemCardapio getItemCardapio() {
		return itemCardapio;
	}

	public void setItemCardapio(ItemCardapio itemCardapio) {
		this.itemCardapio = itemCardapio;
	}

	public List<Alimento> getAlimentos() {
		return alimentos;
	}

	public void setAlimentos(List<Alimento> alimentos) {
		this.alimentos = alimentos;
	}

}
