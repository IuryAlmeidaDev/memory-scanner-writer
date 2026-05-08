package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroDietaEspecial;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.StatusFiltroDietaEspecial;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.DietaRecuperando;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TipoDieta;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialDTO;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.service.DietaEspecialService;
import br.gov.mt.sejudh.sigepen.service.FornecedorService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.TipoDietaService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.PermissaoUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarDietaEspecial", pattern = "/dietaEspecial", viewId = "/WEB-INF/pages/dietaEspecial/ListarDietaEspecial.xhtml"),
		@URLMapping(id = "CadastrarDietaEspecial", pattern = "/dietaEspecial/cadastrar", viewId = "/WEB-INF/pages/dietaEspecial/CadastrarDietaEspecial.xhtml"),
		@URLMapping(id = "AlterarDietaEspecial", pattern = "/dietaEspecial/alterar/#{id}", viewId = "/WEB-INF/pages/dietaEspecial/CadastrarDietaEspecial.xhtml"),
		@URLMapping(id = "VisualizarDietaEspecial", pattern = "/dietaEspecial/visualizar/#{id}", viewId = "/WEB-INF/pages/dietaEspecial/CadastrarDietaEspecial.xhtml") })
public class DietaEspecialBean extends MetodosDePaginaBean {

	private FiltroDietaEspecial filtro;

	private DietaRecuperando dietaEspecial;
	
	private Recuperando recuperandoSelecionado;
	
	private final List<Recuperando> recuperandos = new ArrayList<>();
	
	private List<TipoDieta> tipoDietas;

	private LazyDataModel<DietaEspecialDTO> dietasEspeciais;
	
	private List<UnidadePrisional> unidadesPrisionais;

	@Inject
	private DietaEspecialService dietaEspecialService;
	
	@Inject
    private RecuperandoService recuperandoService;
	
	@Inject
	private FornecedorService fornecedorService;
	
	@Inject
	private TipoDietaService tipoDietaService;
	
	@Inject
	private UnidadePrisionalService unidadeService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@Inject
	private SecurityUtil security;
	
	@Inject
    private PermissaoUtil permissaoUtil;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	private Long contratoSelecionada;
	
	public DietaRecuperando getDietaEspecial() {
		return dietaEspecial;
	}

	public void setDietaEspecial(DietaRecuperando dietaEspecial) {
		this.dietaEspecial = dietaEspecial;
	}

	public Long getcontratoSelecionada() {
		return this.contratoSelecionada;
	}

	public void setcontratoSelecionada(Long contratoSelecionada) {
		this.contratoSelecionada = contratoSelecionada;
	}

	public LazyDataModel<DietaEspecialDTO> getDietasEspeciais() {
		return dietasEspeciais;
	}

	public void setDietasEspeciais(LazyDataModel<DietaEspecialDTO> dietasEspeciais) {
		this.dietasEspeciais = dietasEspeciais;
	}

	public FiltroDietaEspecial getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroDietaEspecial filtro) {
		this.filtro = filtro;
	}
	
	public List<Recuperando> getRecuperandos() {
		return recuperandos;
	}
	
	public FornecedorService getFornecedorService() {
		return fornecedorService;
	}
	
	public List<TipoDieta> getTipoDietas() {
		return this.tipoDietas;
	}

	public StatusFiltroDietaEspecial[] getStatusFiltroDietaEspecial() {
		return StatusFiltroDietaEspecial.values();
	}

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		if (getAcaoDeTela().equals(AcaoDeTela.ALTERAR) || getAcaoDeTela().equals(AcaoDeTela.VISUALIZAR)) {
			if (facesUtil.getQueryParameter("id") != null) {
				this.contratoSelecionada = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
			}
		}
		if (!security.possuiPermissao("ListarDietaEspecial")) {
			this.setFiltro(new FiltroDietaEspecial());

			super.carregarFiltroPaginacao(FiltroDietaEspecial.class, this, getFiltro());
		} else {
			super.carregarFiltroPaginacao(FiltroDietaEspecial.class, this);
		}

		if (this.filtro == null) {
			this.filtro = new FiltroDietaEspecial();
		}
		if (this.filtro.getStatusDieta() == null) {
			this.filtro.setStatusDieta(StatusFiltroDietaEspecial.ATIVO);
		}
	}

	@URLAction(onPostback = false)
	public String init() {
		try {
			this.unidadesPrisionais = unidadeService.listarUnidadesNaoInativas();
			this.tipoDietas = tipoDietaService.listarEspecialAtivas();
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:				
				this.dietaEspecial = new DietaRecuperando();
				break;
			case VISUALIZAR:
			case ALTERAR:

				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

				if (!buscarDietaEspecial(id)) {
					facesUtil.exibirMensagemErro("Dieta especial não encontrada.", true);
					return "pretty:ListarDietaEspecial";
				}

				logDeAcesso(log, this.dietaEspecial);

				break;

			case LISTAR:
				this.tipoDietas = tipoDietaService.listarTodos();
				pesquisar();
				logDeAcesso(log, "Dieta Especial");
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

	private boolean buscarDietaEspecial(long id) {
		if (id <= 0) {
			return false;
		}

		this.dietaEspecial = dietaEspecialService.buscaPorId(id);
		this.recuperandos.add(dietaEspecial.getRecuperando());

        return this.dietaEspecial != null;
    }

	public void pesquisar() {
	    this.dietasEspeciais = new LazyDataModel<DietaEspecialDTO>() {
	        private static final long serialVersionUID = 1L;

	        public List<DietaEspecialDTO> load(int first, int pageSize, String sortField, 
	                SortOrder sortOrder, Map<String, Object> filters) {
	            
	            getFiltro().setListarTodasUnidades(security.possuiPermissao("ListarTodasUnidades"));
	            setRowCount(dietaEspecialService.quantidadeFiltrados(getFiltro()));

	            try {
	              
	                configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
	            } catch (Exception e) {
	                LoggerUtil.logErro(e, log);
	            }
	            
	            return dietaEspecialService.buscarPorFiltro(getFiltro());
	        }
	    };
	}
	
	
	
	private boolean validarDataDietaEspecial() {
		if(this.dietaEspecial.getFim() != null && !this.dietaEspecial.getInicio().before(this.dietaEspecial.getFim())) {
			facesUtil.exibirMensagemErro("A data fim não pode ser menor que a início!", true);
			return false;
		}
		return true;
	}
	
	public String salvar() {
		String retorno = null;
		
		try {
			if(!validarDataDietaEspecial()) {	
				retorno = "pretty:ListarDietaEspecial";
			}else {
				dietaEspecialService.salvar(this.dietaEspecial);
				facesUtil.exibirMensagemInfo("Dieta especial salva com Sucesso!", true);
				retorno = "pretty:ListarDietaEspecial";
				LoggerUtil.logInfo(Acao.SALVAR, dietaEspecial, log);				
			}
			
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "AVISO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "AVISO",
					"Erro ao salvar uma dieta especial: " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, dietaEspecial, e, log);
		}

		return retorno;
	}
	
	public void associarRecuperando(SelectEvent event) {
		RecuperandoView r = (RecuperandoView) event.getObject();
		this.recuperandoSelecionado = recuperandoService.buscarRecuperandoPorId(r.getId(), false);
		this.recuperandos.clear();
		this.recuperandos.add(recuperandoSelecionado);
		this.dietaEspecial.setRecuperandos(recuperandoSelecionado);
		
		try {
			if (recuperandoSelecionado == null) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Recuperando não encontrado.");
            }
		} catch (Exception e) {
 			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir um Recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, recuperandoSelecionado, e, log);
		}
	}
	
	public void excluirDietaEspecialBean(Recuperando recuperando) {
		try {
			this.recuperandos.remove(recuperando);
			this.dietaEspecial.setRecuperandos(null);
			
			if (this.dietaEspecial.getId()>0) {
				//contatoService.excluirContato(contato);
				LoggerUtil.logInfo(Acao.EXCLUIR, recuperando, log);
			}else {
				LoggerUtil.logInfo(Acao.REMOVER, recuperando, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao remover recuperando da dieta especial: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, recuperando, e, log);
		}
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}
	
	public void abrirDialogRecuperando(ActionEvent event) {
		this.permissaoUtil.filtrarUnidadesComBaseNaPermissaoListarTodasRecuperandos(true);
		facesUtil.exibirPaginaDialog("pretty:AssociarRecuperando");
    }

	public void imprimir() {
		try {
			int totalRegistros = dietasEspeciais.getRowCount();
			if (totalRegistros == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "Atenção", "Não há registros para serem impressos");
				return;
			}

			Arquivo relatorio = RelatorioUtil.gerarRelatorio(dietasEspeciais.load(0, totalRegistros, null, SortOrder.UNSORTED, null), Relatorio.ListarDietaEspecialRecuperando);
			fileDownload.prepararArquivo(relatorio);

			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Listar Dieta Especial Recuperando", log);

		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Dieta Especial", e, log);
		}
	}

}
