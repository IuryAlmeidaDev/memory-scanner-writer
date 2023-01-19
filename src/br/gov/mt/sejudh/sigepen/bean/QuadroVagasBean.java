package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroQuadroVagas;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.ParametroUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.QuadroVagasDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.QuadroVagasService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarQuadroVagas", pattern = "/quadrovagas", viewId = "/WEB-INF/pages/quadrovagas/ListarQuadroVagas.xhtml")
})
public class QuadroVagasBean extends MetodosDePaginaBean {

	private FiltroQuadroVagas filtro;
	
	private Boolean disabledAbaEstrutura;

	private UnidadePrisional unidade;

	private LazyDataModel<QuadroVagasDTO> unidades;
	
    private Estrutura estrutura;
	
	private TreeNode root;
	
	private List<ParametroUnidade> parametrosVisitante;

	@Inject
	private QuadroVagasService quadroVagasService;

	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private SecurityUtil security;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	private Long unidadeSelecionada;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		if (getAcaoDeTela().equals(AcaoDeTela.ALTERAR) || getAcaoDeTela().equals(AcaoDeTela.VISUALIZAR)) {
			if(facesUtil.getQueryParameter("id") != null) {
				this.unidadeSelecionada = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
			}
		}
		if (!security.possuiPermissao("ListarTodosQuadroVagas")) {
			this.setFiltro(new FiltroQuadroVagas());
			this.filtro.setId(SecurityUtil.getUnidadeSelecionada().getId());
			
			super.carregarFiltroPaginacao(FiltroQuadroVagas.class, this, getFiltro());
		} else {
			super.carregarFiltroPaginacao(FiltroQuadroVagas.class, this);
		}
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.unidade = new UnidadePrisional();
					this.setDisabledAbaEstrutura(true);	
					break;
					
				case VISUALIZAR:
				case ALTERAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarUnidade(id)) {
						facesUtil.exibirMensagemErro("Unidade prisional não encontrada.", true);
						return "pretty:ListarUnidade";
					}
					
					if (!validarUnidade()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarUnidade";
					}

					logDeAcesso(log, this.unidade);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Unidades Prisionais");
					break;
					
				default:
					break;
			}
            //this.carregarParametrosVisitante();
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}

	private boolean buscarUnidade(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.unidade = quadroVagasService.buscaPorId(id);
		
		if (this.unidade == null) {
			return false;
		}
		
		return true;
	}
	
	private boolean validarUnidade() {
		if (!security.possuiPermissao("ListarTodasUnidades") && this.unidade.getId() != SecurityUtil.getUnidadeSelecionada().getId()) {
			return false;
		}
		
		return true;
	}

	public void pesquisar() {
		this.unidades = new LazyDataModel<QuadroVagasDTO>() {
			private static final long serialVersionUID = 1L;
			
			public List<QuadroVagasDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters){
				setRowCount(quadroVagasService.quantidadeFiltrados(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return quadroVagasService.buscaQuantidadeRecuperados(getFiltro(), false);
			}
		};
	}
	
	public void imprimir() {
		try {
			List<QuadroVagasDTO> lista = quadroVagasService.buscaQuantidadeRecuperados(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarQuadroVagas));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Quadro Vagas", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir Lista de Quadro Vagas: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Quadro Vagas", e, log);
		}
	}

	
	public FiltroQuadroVagas getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroQuadroVagas filtro) {
		this.filtro = filtro;
	}

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public LazyDataModel<QuadroVagasDTO> getUnidades() {
		return unidades;
	}

	public void setUnidades(LazyDataModel<QuadroVagasDTO> unidades) {
		this.unidades = unidades;
	}

	public Estrutura getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(Estrutura estrutura) {
		this.estrutura = estrutura;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public Boolean getDisabledAbaEstrutura() {
		return disabledAbaEstrutura;
	}

	public void setDisabledAbaEstrutura(Boolean disabledAbaEstrutura) {
		this.disabledAbaEstrutura = disabledAbaEstrutura;
	}

    public List<ParametroUnidade> getParametrosVisitante() {
        return parametrosVisitante;
    }

    public void abrirDialogEstrutura(ActionEvent event) {
    	facesUtil.setObjetoSessao("unidadeSelecionadaUsuario", this.unidadeSelecionada);
        facesUtil.exibirPaginaDialog("pretty:AssociarQualquerEstrutura");
    }	
	
}
