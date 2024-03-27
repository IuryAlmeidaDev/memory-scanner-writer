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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarPedidoRemanejamento", pattern = "/associar/pedido-de-remanejamento", viewId = "/WEB-INF/pages/_dialogs/AssociarPedRemanejamento.xhtml")
public class AssociarPedRemanejamentoBean extends MetodosDialogBean {

	private FiltroPedTransito filtro;
	
	private LazyDataModel<PedidoTransitoDTO> pedidos;	
		
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private PedidoService service;
	
	@URLAction(onPostback=false)
	public String init(){
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

		this.setFiltro(new FiltroPedTransito());
			this.getFiltro().setStatus(StatusPedido.EM_ANDAMENTO);
			this.getFiltro().getListaTipoTransito().add(TipoTransito.EXTERNA);
			this.getFiltro().getListaTipoTransito().add(TipoTransito.TRANSFERENCIA);
			this.getFiltro().setForaDomicilio(true);
			this.getFiltro().setUnidadeOrigem(SecurityUtil.getUnidadeSelecionada());
		
		this.setPedidos(new LazyDataModel<PedidoTransitoDTO>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public List<PedidoTransitoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {	
				setRowCount(service.quantidadeFiltrados(getFiltro()));
			
				if (first >= getRowCount()) {
					first = 0;                    
					DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formPedidos:dataTable");
					dt.setFirst(first);
				}
			 
				getFiltro().organizaFiltro(first, pageSize, sortField, sortOrder);
				
				return service.buscaPorFiltro(getFiltro(), false);
			}
		});
	}
	
	public void selecionarPedido(PedidoTransitoDTO pedido){
		facesUtil.fecharPaginaDialog(pedido);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public FiltroPedTransito getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroPedTransito filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<PedidoTransitoDTO> getPedidos() {
		return pedidos;
	}

	public void setPedidos(LazyDataModel<PedidoTransitoDTO> pedidos) {
		this.pedidos = pedidos;
	}	

}