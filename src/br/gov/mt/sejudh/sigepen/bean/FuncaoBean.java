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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFuncao;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cargo;
import br.gov.mt.sejudh.sigepen.model.Funcao;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.CargoService;
import br.gov.mt.sejudh.sigepen.service.FuncaoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarFuncao", pattern = "/funcao", viewId = "/WEB-INF/pages/funcao/ListarFuncao.xhtml")
,@URLMapping(id = "CadastrarFuncao", pattern = "/funcao/cadastrar", viewId = "/WEB-INF/pages/funcao/CadastrarFuncao.xhtml")
,@URLMapping(id = "AlterarFuncao", pattern = "/funcao/alterar/#{id}", viewId = "/WEB-INF/pages/funcao/CadastrarFuncao.xhtml")
,@URLMapping(id = "VisualizarFuncao", pattern = "/funcao/visualizar/#{id}", viewId = "/WEB-INF/pages/funcao/CadastrarFuncao.xhtml")
})
public class FuncaoBean extends MetodosDePaginaBean {

	private FiltroFuncao filtro;

	private Funcao funcao;

	private LazyDataModel<Funcao> funcoes;
	
	private List<Cargo> cargos;

	@Inject
	private FuncaoService service;
	
	@Inject
	private CargoService cargoService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		super.carregarFiltroPaginacao(FiltroFuncao.class, this);
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.funcao = new Funcao(Status.ATIVO);
					this.setCargos(this.cargoService.listarCargoAtivo());
					break;
					
				case ALTERAR:
				case VISUALIZAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarFuncao(id)) {
						facesUtil.exibirMensagemErro("Função não encontrada.", true);
						return "pretty:ListarFuncao";
					}
					this.setCargos(this.cargoService.listarCargoAtivo());
					logDeAcesso(log, this.funcao);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Funções");
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
	
	private boolean buscarFuncao(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.funcao = service.buscarPorId(id);
		
		if (this.funcao == null) {
			return false;
		}
		
		return true;
	}


	public String salvar() {
		String retorno = null;
		
		try {
			service.salvar(this.funcao);
			facesUtil.exibirMensagemInfo("Função " + this.funcao.getNome() + " salva com sucesso!", true);
			LoggerUtil.logInfo(Acao.SALVAR, this.funcao, log);
			retorno = "pretty:ListarFuncao";
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar função (" + this.funcao.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.funcao, e, log);
		}
		
		return retorno;
	}

	public void excluir(Funcao funcao) {
		try {
			service.excluir(funcao);
			facesUtil.exibirMensagemInfo("Função " + funcao.getNome() + " excluída com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, funcao, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir função (" + funcao.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, funcao, e, log);	
		}
	}
	
	public void pesquisar() {
		this.funcoes = new LazyDataModel<Funcao>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Funcao> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return service.buscaPorFiltro(getFiltro());
			}
		};
	}
	
	public void imprimir() {
		try {
			List<Funcao> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarFuncao));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Funções", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de funções: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Funções", e, log);
		}
	}

	public FiltroFuncao getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroFuncao filtro) {
		this.filtro = filtro;
	}	

	public Funcao getFuncao() {
		return funcao;
	}

	public void setFuncao(Funcao funcao) {
		this.funcao = funcao;
	}

	public LazyDataModel<Funcao> getFuncoes() {
		return funcoes;
	}

	public List<Cargo> getCargos() {
		return cargos;
	}

	public void setCargos(List<Cargo> cargos) {
		this.cargos = cargos;
	}
	
	
}
