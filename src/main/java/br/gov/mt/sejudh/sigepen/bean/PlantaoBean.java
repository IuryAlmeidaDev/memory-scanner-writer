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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPLantao;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPlantao;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.PlantaoService;
import br.gov.mt.sejudh.sigepen.service.RegistroPlantaoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "CadastrarPlantao",  pattern = "/plantao/cadastrar", viewId = "/WEB-INF/pages/plantao/CadastrarRegistroPlantao.xhtml")

})
public class PlantaoBean extends MetodosDePaginaBean {

	private FiltroPLantao filtro = new FiltroPLantao();
	//private FiltroRegistroPLantao filtroRegistro = new FiltroRegistroPLantao();

	private Plantao plantao;
	private RegistroPlantao registroPlantao;
	private UnidadePrisional unidadePrisional;
	
	private List<UnidadePrisional> unidadesPrisionais;
	private LazyDataModel<Plantao> plantoes;
	private LazyDataModel<RegistroPlantao> registroPlantoes;
		
	private StatusPlantao statusPlantao;
	
	
	@Inject
	private RegistroPlantaoService registroPlantaoService;
	
	@Inject
	private PlantaoService plantaoService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		super.carregarFiltroPaginacao(FiltroPLantao.class, this);
	}
	

	@URLAction(onPostback = false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.registroPlantao = new RegistroPlantao();
				break;

			case ALTERAR:
			case VISUALIZAR:
				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
								
				if (!buscarPLantao(id)) {
					facesUtil.exibirMensagemErro("Plantão não encontrado.", true);
					return "pretty:ListarPlantao";
				}

				logDeAcesso(log, this.registroPlantao);
				break;

			case LISTAR:
				pesquisar();				
				logDeAcesso(log, "Plantoes");
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

	private boolean buscarPLantao(long id) {
		if (id <= 0) {
			return false;
		}

		this.registroPlantao = registroPlantaoService.buscarPorId(id);

		if (this.registroPlantao == null) {
			return false;
		}

		return true;
	}
	
	
	public void pesquisar() {
		this.registroPlantoes = new LazyDataModel<RegistroPlantao>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<RegistroPlantao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(registroPlantaoService.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return registroPlantaoService.buscaPorFiltro(getFiltro(), false);
			}
		};
	}

	public void imprimir() {
		try {
			List<Plantao> lista = plantaoService.buscaPorFiltro(filtro, true);

			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
						"Relatório não impresso, nenhum registro encontrado!");
				return;
			}

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarCargos));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de PLantões", log);

		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro imprimir lista de plantoes: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de PLantões", e, log);
		}
	}

	
	
	/* --------------------------- get & setts -------------------------------------- */

	
	public FiltroPLantao getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroPLantao filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<Plantao> getPlantoes() {
		return plantoes;
	}

	public void setPlantoes(LazyDataModel<Plantao> plantoes) {
		this.plantoes = plantoes;
	}

	public Plantao getPlantao() {
		return plantao;
	}

	public void setPlantao(Plantao plantao) {
		this.plantao = plantao;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}


	public LazyDataModel<RegistroPlantao> getRegistroPlantoes() {
		return registroPlantoes;
	}


	public void setRegistroPlantoes(LazyDataModel<RegistroPlantao> registroPlantoes) {
		this.registroPlantoes = registroPlantoes;
	}


	public RegistroPlantao getRegistroPlantao() {
		return registroPlantao;
	}


	public void setRegistroPlantao(RegistroPlantao registroPlantao) {
		this.registroPlantao = registroPlantao;
	}
	
	
	public StatusPlantao getStatusPlantao() {
		return statusPlantao;
	}

	public void setStatusPlantao(StatusPlantao statusPlantao) {
		this.statusPlantao = statusPlantao;
	}
	
	public StatusPlantao[] getStatusPlantoes() {
        return StatusPlantao.values();
    }

}
