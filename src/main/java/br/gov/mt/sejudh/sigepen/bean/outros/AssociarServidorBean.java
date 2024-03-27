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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroServidor;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarServidor", pattern = "/associar/servidor", viewId = "/WEB-INF/pages/_dialogs/AssociarServidor.xhtml")
public class AssociarServidorBean extends MetodosDialogBean {

	private FiltroServidor filtro;
	private LazyDataModel<ServidorDTO> servidores;

	private UnidadePrisional unidadeLotacao;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@Inject
	private ServidorService service;

	@URLAction(onPostback=false)
	public String init() {
		try {
			if (this.isDialogInvalida()) {
				return "pretty:dashboard";
			}
			
			this.unidadeLotacao = (UnidadePrisional) facesUtil.getObjetoSessao("unidadeLotacao");
			pesquisar();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}

	private void pesquisar() {
		this.setFiltro(new FiltroServidor());
		this.filtro.setStatus(Status.ATIVO);
		Boolean escolta = (Boolean) facesUtil.getObjetoSessao("associarServidorEscolta");
		escolta = (escolta == null) ? Boolean.FALSE : escolta; 
		if(escolta) {
			this.filtro.setEscolta(escolta);
		}
		
		TipoTransito tipoTransito = (TipoTransito)facesUtil.getObjetoSessao("tipoTransito");
		if(tipoTransito == TipoTransito.INTERNA || tipoTransito == TipoTransito.EXTERNA){
			this.filtro.setUnidadeLotacao(this.unidadeLotacao);
			this.filtro.setLotacaoObrigatoria(Boolean.FALSE);
		}
		
		this.setServidores(new LazyDataModel<ServidorDTO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<ServidorDTO> load(int first, int pageSize,
					String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));

				if (first >= getRowCount()) {
					first = 0;
					DataTable dt = (DataTable) FacesContext
							.getCurrentInstance().getViewRoot()
							.findComponent("formServidores:dataTable");
					dt.setFirst(first);
				}

				getFiltro().organizaFiltro(first, pageSize, sortField,
						sortOrder);

				return service.buscaPorFiltro(getFiltro(), false);
			}
		});
	}

	public void selecionarServidor(ServidorDTO servidor) {
		facesUtil.fecharPaginaDialog(servidor);
	}

	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public FiltroServidor getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroServidor filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<ServidorDTO> getServidores() {
		return servidores;
	}

	public void setServidores(LazyDataModel<ServidorDTO> servidores) {
		this.servidores = servidores;
	}

	public UnidadePrisional getUnidadeLotacao() {
		return unidadeLotacao;
	}

	public void setUnidadeLotacao(UnidadePrisional unidadeLotacao) {
		this.unidadeLotacao = unidadeLotacao;
	}
}
