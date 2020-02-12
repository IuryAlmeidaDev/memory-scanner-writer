package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCargo;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cargo;
import br.gov.mt.sejudh.sigepen.model.Categoria;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.CargoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarCargo", pattern = "/cargo", viewId = "/WEB-INF/pages/cargo/ListarCargo.xhtml")
,@URLMapping(id = "CadastrarCargo", pattern = "/cargo/cadastrar", viewId = "/WEB-INF/pages/cargo/CadastrarCargo.xhtml")
,@URLMapping(id = "AlterarCargo", pattern = "/cargo/alterar/#{id}", viewId = "/WEB-INF/pages/cargo/CadastrarCargo.xhtml")
,@URLMapping(id = "VisualizarCargo", pattern = "/cargo/visualizar/#{id}", viewId = "/WEB-INF/pages/cargo/CadastrarCargo.xhtml")
})
public class CargoBean extends MetodosDePaginaBean {

	private FiltroCargo filtro;

	private Cargo cargo;

	private LazyDataModel<Cargo> cargos;
	
	private List<Categoria> categorias;

	@Inject
	private CargoService service;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		super.carregarFiltroPaginacao(FiltroCargo.class, this);
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch(this.getAcaoDeTela()) {
				case CADASTRAR:
					this.cargo = new Cargo(Status.ATIVO);
					this.setCategorias(service.listarCategoriaAtiva());
					break;
					
				case ALTERAR:					
				case VISUALIZAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if(!buscarCargo(id)) {
						facesUtil.exibirMensagemErro("Cargo não encontrado.", true);
						return "pretty:ListarCargo";
					}

					this.setCategorias(service.listarCategoriaAtiva());
					logDeAcesso(log, this.cargo);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Cargos");
					break;
					
				default:
					break;
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}

	private boolean buscarCargo(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.cargo = service.buscarPorId(id);
		
		if (this.cargo == null) {
			return false;
		}
		
		return true;
	}

	public String salvar() {
		String retorno = null;
		
		try {
			service.salvar(this.cargo);
			facesUtil.exibirMensagemInfo("Cargo " + this.cargo.getNome() + " salvo com sucesso!", true);
			retorno = "pretty:ListarCargo";
			LoggerUtil.logInfo(Acao.SALVAR, this.cargo, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar cargo (" + this.cargo.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.cargo, e, log);	
		}
		
		return retorno;
	}

	public void excluir(Cargo cargo) {
		try {
			service.excluir(cargo);
			facesUtil.exibirMensagemInfo("Cargo " + cargo.getNome() + " excluído com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, cargo, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir cargo (" + cargo.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, cargo, e, log);		
		}
	}
	
	public void pesquisar() {
		this.cargos = new LazyDataModel<Cargo>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Cargo> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return service.buscaPorFiltro(getFiltro(), false);
			}
		};
	}
	
	public void imprimir() {
		try {
			List<Cargo> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarCargos));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Cargos", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de cargos: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Cargos", e, log);
		}
	}

	public FiltroCargo getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroCargo filtro) {
		this.filtro = filtro;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public LazyDataModel<Cargo> getCargos() {
		return cargos;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}	
}
