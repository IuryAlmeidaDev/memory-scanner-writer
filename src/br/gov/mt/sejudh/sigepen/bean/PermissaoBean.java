package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPermissao;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.model.Permissao;
import br.gov.mt.sejudh.sigepen.service.PermissaoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "ListarPermissao", pattern = "/permissao", viewId = "/WEB-INF/pages/permissao/ListarPermissao.xhtml")
public class PermissaoBean {
	
	private LazyDataModel<Permissao> permissoes;
	private FiltroPermissao filtro;
	
	@Inject
	private PermissaoService service;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@PostConstruct
	public void init() {
		try {	
			LoggerUtil.logInfo(Acao.ACESSAR, "Permissões", log);
			pesquisar();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void pesquisar() {
		this.setFiltro(new FiltroPermissao());
		this.permissoes = new LazyDataModel<Permissao>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Permissao> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
				setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));		
				
                if (first >= getRowCount()) {
                    first = 0;                    
                    DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("registrosForm:dataTable");
                    dt.setFirst(first);
                }
				
				getFiltro().organizaFiltro(first, pageSize, sortField, sortOrder);
				
				return service.buscarPermissoesPorFiltro(getFiltro());
			}
		};
	}

	public LazyDataModel<Permissao> getPermissoes() {
		return permissoes;
	}

	public FiltroPermissao getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroPermissao filtro) {
		this.filtro = filtro;
	}
}
