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
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoOcorrencia;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.TipoOcorrencia;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.TipoOcorrenciaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarOcorrencia", pattern = "/tipoOcorrencia", viewId = "/WEB-INF/pages/tipoOcorrencia/ListarOcorrencia.xhtml")
,@URLMapping(id = "CadastrarOcorrencia", pattern = "/tipoOcorrencia/cadastrar", viewId = "/WEB-INF/pages/tipoOcorrencia/CadastrarOcorrencia.xhtml")
,@URLMapping(id = "AlterarOcorrencia", pattern = "/tipoOcorrencia/alterar/#{id}", viewId = "/WEB-INF/pages/tipoOcorrencia/CadastrarOcorrencia.xhtml")
,@URLMapping(id = "VisualizarOcorrencia", pattern = "/tipoOcorrencia/visualizar/#{id}", viewId = "/WEB-INF/pages/tipoOcorrencia/CadastrarOcorrencia.xhtml")
})
public class TipoOcorrenciaBean extends MetodosDePaginaBean {

	private FiltroTipoOcorrencia filtro;

	private TipoOcorrencia tipoOcorrencia;

	private LazyDataModel<TipoOcorrencia> tipoOcorrencias;

	@Inject
	private TipoOcorrenciaService service;	

	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		super.carregarFiltroPaginacao(FiltroTipoOcorrencia.class, this);
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch(this.getAcaoDeTela()) {
				case CADASTRAR:
					this.tipoOcorrencia = new TipoOcorrencia(Status.ATIVO);
					break;
					
				case ALTERAR:					
				case VISUALIZAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarTipoOcorrencia(id)) {
						facesUtil.exibirMensagemErro("Tipo ocorrencia não encontrada.", true);
						return "pretty:ListarOcorrencia";
					}
					
					logDeAcesso(log, this.tipoOcorrencia);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "tipoOcorrencias");
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
	
	private boolean buscarTipoOcorrencia(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.tipoOcorrencia = service.buscarPorId(id, true);
		
		if (this.tipoOcorrencia == null) {
			return false;
		}
		
		return true;
	}

	public String salvar() {
		String retorno = null;
		
		try	{
			service.salvar(this.tipoOcorrencia);
			facesUtil.exibirMensagemInfo("Tipo Ocorrencia " + this.tipoOcorrencia.getDescricao() + " salva com sucesso!", true);
			retorno = "pretty:ListarOcorrencia";
			LoggerUtil.logInfo(Acao.SALVAR, this.tipoOcorrencia, log);
			
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
			
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar tipo ocorrencia (" + this.tipoOcorrencia.getDescricao() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.tipoOcorrencia, e, log);
		}
		
		return retorno;
	}

	public void excluir(TipoOcorrencia tipoOcorrencia) {
		try {
			service.excluir(tipoOcorrencia);
			facesUtil.exibirMensagemInfo("Tipo Ocorrencia " + tipoOcorrencia.getDescricao() + " excluída com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, tipoOcorrencia, log);
			
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
			
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir tipo ocorrencia (" + tipoOcorrencia.getDescricao() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, tipoOcorrencia, e, log);	
		}
	}

	public void pesquisar() {
		this.tipoOcorrencias = new LazyDataModel<TipoOcorrencia>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<TipoOcorrencia> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
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
			List<TipoOcorrencia> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarTipoOcorrencia));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Tipo Ocorrencia", log);
			
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de tipo ocorrencia : " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Tipo Ocorrencia", e, log);
		}
	}
	
	
	
	/*Get & Setts*/
	
	public TipoOcorrencia getTipoOcorrencia() {
		return tipoOcorrencia;
	}

	public void setTipoOcorrencia(TipoOcorrencia tipoOcorrencia) {
		this.tipoOcorrencia = tipoOcorrencia;
	}

	public LazyDataModel<TipoOcorrencia> getTipoOcorrencias() {
		return tipoOcorrencias;
	}

	public void setTipoOcorrencias(LazyDataModel<TipoOcorrencia> tipoOcorrencias) {
		this.tipoOcorrencias = tipoOcorrencias;
	}
	
	public FiltroTipoOcorrencia getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroTipoOcorrencia filtro) {
		this.filtro = filtro;
	}
	
}
