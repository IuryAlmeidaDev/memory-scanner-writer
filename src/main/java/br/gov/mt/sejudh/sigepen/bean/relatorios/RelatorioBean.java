package br.gov.mt.sejudh.sigepen.bean.relatorios;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import org.primefaces.component.selectonemenu.SelectOneMenu;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.bean.outros.MetodosDialogBean;
import br.gov.mt.sejudh.sigepen.model.Funcionalidade;
import br.gov.mt.sejudh.sigepen.model.auxiliar.Relatorio;
import br.gov.mt.sejudh.sigepen.service.RelatorioService;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "Relatorio", pattern = "/relatorio", viewId = "/WEB-INF/pages/relatorio/Relatorio.xhtml"),
})
public class RelatorioBean extends MetodosDialogBean {

	private String relatorioSelecionado;
	
	private List<Relatorio> relatorios;
	
	private List<Funcionalidade> funcionalidades;
	
	private Funcionalidade funcionalidade;
	
	@Inject
	private RelatorioService service;
	
	@URLAction(onPostback=false)
	public String init() {
		this.relatorioSelecionado = "";
		this.funcionalidades = service.listarFuncionalidadesComRelatorio();
		return "Pretty:Relatorio";
	}	

	public String getRelatorioSelecionado() {
		return relatorioSelecionado;
	}

	public void setRelatorioSelecionado(String relatorioSelecionado) {
		this.relatorioSelecionado = relatorioSelecionado;
	}

	public List<Relatorio> getRelatorios() {
		return relatorios;
	}

	public void setRelatorios(List<Relatorio> relatorios) {
		this.relatorios = relatorios;
	}
	
	public void selecionarFuncionalidade(AjaxBehaviorEvent event) {
		if (this.funcionalidade != null) {
			List<Relatorio> lista  = this.service.buscarPorFuncionalidade(this.funcionalidade);
			this.relatorios = lista;
			this.relatorioSelecionado = "";
		} else {
			this.relatorios = new ArrayList<Relatorio>();
		}
	}

	public Funcionalidade getFuncionalidade() {
		return funcionalidade;
	}

	public void setFuncionalidade(Funcionalidade funcionalidade) {
		this.funcionalidade = funcionalidade;
	}
	
	public List<Funcionalidade> getFuncionalidades() {
		return funcionalidades;
	}

	public void setFuncionalidades(List<Funcionalidade> funcionalidades) {
		this.funcionalidades = funcionalidades;
	}

	public void mostrarFiltros2(AjaxBehaviorEvent event) throws IOException {
		String value = (String)((SelectOneMenu)event.getSource()).getValue();
		if(value != null) {
			String url = Sigepen.getApplicationName() + "/" + value;
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		}
	}

	public void imprimir01() {
	}
}
