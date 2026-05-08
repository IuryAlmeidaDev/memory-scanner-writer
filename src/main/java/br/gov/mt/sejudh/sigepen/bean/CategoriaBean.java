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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCategoria;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Categoria;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.CategoriaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarCategoria", pattern = "/categoria", viewId = "/WEB-INF/pages/categoria/ListarCategoria.xhtml")
,@URLMapping(id = "CadastrarCategoria", pattern = "/categoria/cadastrar", viewId = "/WEB-INF/pages/categoria/CadastrarCategoria.xhtml")
,@URLMapping(id = "AlterarCategoria", pattern = "/categoria/alterar/#{id}", viewId = "/WEB-INF/pages/categoria/CadastrarCategoria.xhtml")
,@URLMapping(id = "VisualizarCategoria", pattern = "/categoria/visualizar/#{id}", viewId = "/WEB-INF/pages/categoria/CadastrarCategoria.xhtml")
})
public class CategoriaBean extends MetodosDePaginaBean {

	private FiltroCategoria filtro;

	private Categoria categoria;

	private LazyDataModel<Categoria> categorias;

	@Inject
	private CategoriaService service;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		super.carregarFiltroPaginacao(FiltroCategoria.class, this);
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch(this.getAcaoDeTela()) {
				case CADASTRAR:
					this.categoria = new Categoria(Status.ATIVO);
					break;
					
				case ALTERAR:					
				case VISUALIZAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarCategoria(id)) {
						facesUtil.exibirMensagemErro("Categoria não encontrada.", true);
						return "pretty:ListarCategoria";
					}
					
					logDeAcesso(log, this.categoria);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Categorias");
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
	
	private boolean buscarCategoria(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.categoria = service.buscarPorId(id);
		
		if (this.categoria == null) {
			return false;
		}
		
		return true;
	}

	public String salvar() {
		String retorno = null;
		
		try	{
			service.salvar(this.categoria);
			facesUtil.exibirMensagemInfo("Categoria " + this.categoria.getNome() + " salva com sucesso!", true);
			retorno = "pretty:ListarCategoria";
			LoggerUtil.logInfo(Acao.SALVAR, this.categoria, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar categoria (" + this.categoria.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.categoria, e, log);
		}
		
		return retorno;
	}

	public void excluir(Categoria categoria) {
		try {
			service.excluir(categoria);
			facesUtil.exibirMensagemInfo("Categoria " + categoria.getNome() + " excluída com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, categoria, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir categoria (" + categoria.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, categoria, e, log);	
		}
	}

	public void pesquisar() {
		this.categorias = new LazyDataModel<Categoria>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Categoria> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					LoggerUtil.logErro(e, log);
				}
				
				return service.buscaPorFiltro(getFiltro(), false);
			}
		};
	}
	
	public void imprimir() {
		try {
			List<Categoria> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarCategorias));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Categorias", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de categorias: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Categorias", e, log);
		}
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public LazyDataModel<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(LazyDataModel<Categoria> categorias) {
		this.categorias = categorias;
	}

	public FiltroCategoria getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroCategoria filtro) {
		this.filtro = filtro;
	}
}
