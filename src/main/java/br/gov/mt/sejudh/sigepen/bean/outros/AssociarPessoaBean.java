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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPessoa;
import br.gov.mt.sejudh.sigepen.model.dto.PessoaDTO;
import br.gov.mt.sejudh.sigepen.service.PessoaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarPessoa", pattern = "/associar/pessoa", viewId = "/WEB-INF/pages/_dialogs/AssociarPessoa.xhtml")
public class AssociarPessoaBean extends MetodosDialogBean {

	private FiltroPessoa filtro;
	private LazyDataModel<PessoaDTO> pessoas;
	
	@Inject
	private PessoaService service;
	
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
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}
	
	public void pesquisar() {
		this.setFiltro(new FiltroPessoa());
		this.setPessoas(new LazyDataModel<PessoaDTO>() {
			private static final long serialVersionUID = 1L;
			
			public List<PessoaDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));
				
				if (first >= getRowCount()) {
					first = 0;
					DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formPessoas:dataTable");
					dt.setFirst(first);
				}
				
				getFiltro().organizaFiltro(first, pageSize, sortField, sortOrder);
				
				return service.buscaPorFiltro(getFiltro());
			}
		});
	}
	
	public void selecionarPessoa(PessoaDTO pessoa) {
		facesUtil.fecharPaginaDialog(pessoa);
	}

	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}
	
	public LazyDataModel<PessoaDTO> getPessoas() {
		return pessoas;
	}

	public void setPessoas(LazyDataModel<PessoaDTO> pessoas) {
		this.pessoas = pessoas;
	}	
	
	public FiltroPessoa getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroPessoa filtro) {
		this.filtro = filtro;
	}
}
