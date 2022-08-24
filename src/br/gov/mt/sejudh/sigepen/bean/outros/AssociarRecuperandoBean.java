package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarRecuperando", pattern = "/associar/recuperando", viewId = "/WEB-INF/pages/_dialogs/AssociarRecuperando.xhtml")
public class AssociarRecuperandoBean extends MetodosDialogBean {

	private FiltroRecuperandoView filtro;
	
	private LazyDataModel<RecuperandoView> recuperandos;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private RecuperandoService service;
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			if (this.isDialogInvalida()) {
				return "pretty:dashboard";
			}
            this.setFiltro((FiltroRecuperandoView) facesUtil.getObjetoSessao("filtroRecuperando"));
			
			this.pesquisar();
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}
	
	private void pesquisar() {
	    if (this.getFiltro() == null) {
            this.setFiltro(new FiltroRecuperandoView());
        }
		
		this.filtro.setUnidade(SecurityUtil.getUnidadeSelecionada());
		
		this.filtro.setStatus(StatusRecuperando.ATIVO);
		
		this.setRecuperandos(new LazyDataModel<RecuperandoView>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<RecuperandoView> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {	
				setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));
			
				if (first >= getRowCount()) {
					first = 0;                    
					DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("registrosForm:dataTable");
					dt.setFirst(first);
				}
			 
				getFiltro().organizaFiltro(first, pageSize, sortField, sortOrder);
				
				return service.buscarRecuperandosPorFiltro(getFiltro(), false);
			}
		});
	}
	
	public void selecionarRecuperando(RecuperandoView recuperando) {
		facesUtil.fecharPaginaDialog(recuperando);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}
	
	public String corPeric(String peric) {

		if (peric != null) {
			switch (peric) {

			case "BAIXA":	return "#008000";

			case "MÉDIA":	return "#fbf514";

			case "ALTA":	return "#e50000";

			case "ALTÍSSIMA":	return "#b20000";

			default:	return "";
			}
		}
		return "";
	}

	public FiltroRecuperandoView getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroRecuperandoView filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<RecuperandoView> getRecuperandos() {
		return recuperandos;
	}

	public void setRecuperandos(LazyDataModel<RecuperandoView> recuperandos) {
		this.recuperandos = recuperandos;
	}
	
}
