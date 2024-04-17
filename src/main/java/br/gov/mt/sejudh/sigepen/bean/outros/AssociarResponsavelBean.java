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
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroAssociarResponsavel;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.view.AssociarVisitanteResponsavelView;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "AssociarResponsavel", pattern = "/associar/responsavel", viewId = "/WEB-INF/pages/_dialogs/AssociarResponsavel.xhtml")
})
public class AssociarResponsavelBean extends MetodosDialogBean {

    private Visitante visitante;

    
    private FiltroAssociarResponsavel filtroAssociarResp;
 
    private LazyDataModel<AssociarVisitanteResponsavelView> lazyDataModel;
    
    private boolean exibirMatricula = false;
   
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
    
    
    public void selecionarAssociarResponsavel(AssociarVisitanteResponsavelView associarVisitanteResponsavelView) {
    	Visitante visitante = this.service.buscarPorId(associarVisitanteResponsavelView.getVisitanteId(), false);
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

    

    public LazyDataModel<AssociarVisitanteResponsavelView> getLazyDataModel() {
        return lazyDataModel;
    }
    

	public boolean isExibirMatricula() {
		return exibirMatricula;
	}

	public void setExibirMatricula(boolean exibirMatricula) {
		this.exibirMatricula = exibirMatricula;
	}

	public void pesquisar() {		
//		FiltroVisitanteImpedimento filtro = new FiltroVisitanteImpedimento();
		FiltroAssociarResponsavel filtro = new FiltroAssociarResponsavel();
		
//		Object matriculaObj = facesUtil.getObjetoSessao("matricula", false);
//		if(matriculaObj != null) {
//			filtro.setMatricula(Sigepen.converteStringEmLong(facesUtil.getObjetoSessao("matricula", false).toString()));
//		}
		this.setFiltroAssociarResp(filtro);
		
		this.setLazyDataModel(new LazyDataModel<AssociarVisitanteResponsavelView>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<AssociarVisitanteResponsavelView> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {	
				setRowCount(service.buscaQtdRegistroFiltrado(filtro));
			
				if (first >= getRowCount()) {
					first = 0;                    
					DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("registrosForm:dataTable");
					dt.setFirst(first);
				}
			 
				filtro.organizaFiltro(first, pageSize, sortField, sortOrder);
				return service.buscarPorFiltroAssociarResponsavel(filtro, false); 
			}
		});
	}

	public void setLazyDataModel(LazyDataModel<AssociarVisitanteResponsavelView> lazyDataModel) {
		this.lazyDataModel = lazyDataModel;
	}

	public FiltroAssociarResponsavel getFiltroAssociarResp() {
		return filtroAssociarResp;
	}

	public void setFiltroAssociarResp(FiltroAssociarResponsavel filtroAssociarResp) {
		this.filtroAssociarResp = filtroAssociarResp;
	}
	
}
