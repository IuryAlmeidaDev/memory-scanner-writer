package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCrime;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.model.Crime;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.CrimeService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarCrime", pattern = "/crime", viewId = "/WEB-INF/pages/crime/ListarCrime.xhtml")
,@URLMapping(id = "VisualizarCrime", pattern = "/crime/visualizar/#{id}", viewId = "/WEB-INF/pages/crime/CadastrarCrime.xhtml")
})
public class CrimeBean extends MetodosDePaginaBean {

	private FiltroCrime filtro;

	private Crime crime;

	private LazyDataModel<Crime> crimes;
	
	private List<String> legislacoes;
	
	private List<String> artigos;

	@Inject
	private CrimeService service;

	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		super.carregarFiltroPaginacao(FiltroCrime.class, this);
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch(this.getAcaoDeTela()) {				
				case VISUALIZAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarCrime(id)) {
						facesUtil.exibirMensagemErro("Crime não encontrado.", true);
						return "pretty:ListarCrime";
					}
					
					logDeAcesso(log, this.crime);
					break;
					
				case LISTAR:
					this.setLegislacoes(service.listarLegislacoes());				
					this.artigos = new ArrayList<String>();
					
					pesquisar();
					logDeAcesso(log, "Crimes");
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

	private boolean buscarCrime(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.crime = service.buscarPorId(id);
		
		if (this.crime == null) {
			return false;
		}
		
		return true;
	}
	
	public void pesquisar() {
		this.crimes = new LazyDataModel<Crime>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Crime> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
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
			List<Crime> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarCrimes));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Crimes", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de crimes: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Crimes", e, log);
		}
	}
	
	public void listarArtigos(){
		this.setArtigos(service.listarArtigos(this.filtro.getLegislacao()));
	}

	public FiltroCrime getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroCrime filtro) {
		this.filtro = filtro;
	}		

	public Crime getCrime() {
		return crime;
	}

	public void setCrime(Crime crime) {
		this.crime = crime;
	}

	public LazyDataModel<Crime> getCrimes() {
		return crimes;
	}

	public List<String> getLegislacoes() {
		return legislacoes;
	}

	public void setLegislacoes(List<String> legislacoes) {
		this.legislacoes = legislacoes;
	}

	public List<String> getArtigos() {
		return artigos;
	}

	public void setArtigos(List<String> artigos) {
		this.artigos = artigos;
	}
}
