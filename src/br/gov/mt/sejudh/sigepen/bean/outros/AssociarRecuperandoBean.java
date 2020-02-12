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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarRecuperando", pattern = "/associar/recuperando", viewId = "/WEB-INF/pages/_dialogs/AssociarRecuperando.xhtml")
public class AssociarRecuperandoBean extends MetodosDialogBean {

	private FiltroRecuperando filtro;
	
	private LazyDataModel<RecuperandoDTO> recuperandos;
	
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
			
			pesquisar();
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}
	
	private void pesquisar() {
		this.setFiltro(new FiltroRecuperando());
		
		this.filtro.setUnidade(SecurityUtil.getUnidadeSelecionada());
		
		this.filtro.setStatus(StatusRecuperando.ATIVO);
		
		this.setRecuperandos(new LazyDataModel<RecuperandoDTO>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<RecuperandoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {	
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
	
	public void selecionarRecuperando(RecuperandoDTO recuperando) {
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

	public FiltroRecuperando getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroRecuperando filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<RecuperandoDTO> getRecuperandos() {
		return recuperandos;
	}

	public void setRecuperandos(LazyDataModel<RecuperandoDTO> recuperandos) {
		this.recuperandos = recuperandos;
	}
	
}
