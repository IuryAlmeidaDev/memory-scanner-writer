package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVisitanteImpedimento;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarResponsavel", pattern = "/associar/responsavel", viewId = "/WEB-INF/pages/_dialogs/AssociarResponsavel.xhtml")
public class AssociarResponsavelBean extends MetodosDialogBean {

    private Visitante visitante;

    private FiltroVisitanteImpedimento filtro;
    private LazyDataModel<Visitante> lazyDataModel;

    @Inject
    private VisitanteService service;

    @Inject
    private FacesUtil facesUtil;

    @URLAction(onPostback = false)
    public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		pesquisar();
		
        return null;
    }

    public void selecionarRecuperando(Visitante visitante) {
        facesUtil.fecharPaginaDialog(visitante);
    }

    public void incluirContato() {
        facesUtil.fecharPaginaDialog(visitante);
    }

    public void fecharDialog(ActionEvent event) {
        facesUtil.fecharPaginaDialog(null);
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }

    public FiltroVisitanteImpedimento getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroVisitanteImpedimento filtro) {
        this.filtro = filtro;
    }

    public LazyDataModel<Visitante> getLazyDataModel() {
        return lazyDataModel;
    }
    
	public void pesquisar() {

		this.setFiltro(new FiltroVisitanteImpedimento());
		this.setLazyDataModel(new LazyDataModel<Visitante>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Visitante> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {	
				setRowCount(service.quantidadeFiltrados(getFiltro()));
			
				if (first >= getRowCount()) {
					first = 0;                    
					DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("registrosForm:dataTable");
					dt.setFirst(first);
				}
			 
				getFiltro().organizaFiltro(first, pageSize, sortField, sortOrder);
				return service.buscarPorFiltro(getFiltro(), false); 
			}
		});
	}

	public void setLazyDataModel(LazyDataModel<Visitante> lazyDataModel) {
		this.lazyDataModel = lazyDataModel;
	}
	
}
