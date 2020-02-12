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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarUnidade", pattern = "/associar/unidade", viewId = "/WEB-INF/pages/_dialogs/AssociarUnidade.xhtml")
public class AssociarUnidadeBean extends MetodosDialogBean {

	private FiltroUnidadePrisional filtro;
	private LazyDataModel<UnidadePrisionalDTO> unidades;
	
	@Inject
	private UnidadePrisionalService service;
	
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
			
			pesquisar();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}
	
	public void pesquisar() {
		this.setFiltro(new FiltroUnidadePrisional());
		this.getFiltro().getListaSituacao().add(SituacaoUnidade.ATIVO);
		this.getFiltro().getListaSituacao().add(SituacaoUnidade.INTERDITADO_PARCIAL);
		this.getFiltro().getListaSituacao().add(SituacaoUnidade.INTERDITADO_TOTAL);
		this.setUnidades(new LazyDataModel<UnidadePrisionalDTO>() {
			private static final long serialVersionUID = 1L;
			
			public List<UnidadePrisionalDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));
				
				if (first >= getRowCount()) {
					first = 0;
					DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formUnidades:dataTable");
					dt.setFirst(first);
				}
								 
				getFiltro().organizaFiltro(first, pageSize, sortField, sortOrder);
			
				return service.buscarPorFiltro(getFiltro(), false);
			}
		});
	}
	
	public void selecionarUnidade(UnidadePrisionalDTO unidade) {
		facesUtil.fecharPaginaDialog(unidade);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public LazyDataModel<UnidadePrisionalDTO> getUnidades() {
		return unidades;
	}

	public void setUnidades(LazyDataModel<UnidadePrisionalDTO> unidades) {
		this.unidades = unidades;
	}

	public FiltroUnidadePrisional getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroUnidadePrisional filtro) {
		this.filtro = filtro;
	}
}
