package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEstabelecimento;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.service.EstabelecimentoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarEstabelecimento", pattern = "/associar/estabelecimento", viewId = "/WEB-INF/pages/_dialogs/AssociarEstabelecimento.xhtml")
public class AssociarEstabelecimentoBean extends MetodosDialogBean {

	private FiltroEstabelecimento filtro;
	private LazyDataModel<Estabelecimento> estabelecimentos;
	private List<Cidade> cidades;
	
	private Boolean procedencia;
	
	@Inject
	private ComboService comboService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private EstabelecimentoService service;
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			if (this.isDialogInvalida()) {
				return "pretty:dashboard";
			}
			
			this.procedencia = (Boolean) facesUtil.getObjetoSessao("proced");
			pesquisar();
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}
	
	public void pesquisar() {

		this.setFiltro(new FiltroEstabelecimento());
		this.filtro.setProcedencia(this.procedencia);
		this.filtro.setStatus(Status.ATIVO);
		this.setEstabelecimentos(new LazyDataModel<Estabelecimento>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Estabelecimento> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {	
				setRowCount(service.quantidadeFiltrados(getFiltro()));
			
				if (first >= getRowCount()) {
					first = 0;                    
					DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formProcedencias:dataTable");
					dt.setFirst(first);
				}
			 
				getFiltro().organizaFiltro(first, pageSize, sortField, sortOrder);
				
				return service.buscaPorFiltro(getFiltro(), false);
			}
		});
	}
	
	public void selecionarEstabelecimento(Estabelecimento estabelecimento){
		facesUtil.fecharPaginaDialog(estabelecimento);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}
	
	public void listaCidades(AjaxBehaviorEvent event){
		try {
			Estado est = (Estado) ((SelectOneMenu) event.getSource()).getValue();
			cidades = comboService.listarCidade(est);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public FiltroEstabelecimento getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroEstabelecimento filtro) {
		this.filtro = filtro;
	}	

	public LazyDataModel<Estabelecimento> getEstabelecimentos() {
		return estabelecimentos;
	}

	public void setEstabelecimentos(LazyDataModel<Estabelecimento> estabelecimentos) {
		this.estabelecimentos = estabelecimentos;
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

}