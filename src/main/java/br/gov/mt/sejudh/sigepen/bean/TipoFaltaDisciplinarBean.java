package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.TipoFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.TipoFaltaDisciplinarService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarTipoFaltaDisciplinar", pattern = "/tipofaltadisciplinar", viewId = "/WEB-INF/pages/tipofaltadisciplinar/ListarTipoFaltaDisciplinar.xhtml")
,@URLMapping(id = "CadastrarTipoFaltaDisciplinar", pattern = "/tipofaltadisciplinar/cadastrar", viewId = "/WEB-INF/pages/tipofaltadisciplinar/CadastrarTipoFaltaDisciplinar.xhtml")
,@URLMapping(id = "AlterarTipoFaltaDisciplinar", pattern = "/tipofaltadisciplinar/alterar/#{id}", viewId = "/WEB-INF/pages/tipofaltadisciplinar/CadastrarTipoFaltaDisciplinar.xhtml")
,@URLMapping(id = "VisualizarTipoFaltaDisciplinar", pattern = "/tipofaltadisciplinar/visualizar/#{id}", viewId = "/WEB-INF/pages/tipofaltadisciplinar/CadastrarTipoFaltaDisciplinar.xhtml")
})
public class TipoFaltaDisciplinarBean extends MetodosDePaginaBean {

	private FiltroTipoFaltaDisciplinar filtro;

	private TipoFaltaDisciplinar tipoFaltaDisciplinar;

	private LazyDataModel<TipoFaltaDisciplinar> tipoFaltaDisciplinars;

	@Inject
	private TipoFaltaDisciplinarService service;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		super.carregarFiltroPaginacao(FiltroTipoFaltaDisciplinar.class, this);
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch(this.getAcaoDeTela()) {
				case CADASTRAR:
					this.tipoFaltaDisciplinar = new TipoFaltaDisciplinar(Status.ATIVO);
					break;
					
				case ALTERAR:					
				case VISUALIZAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if(!buscarTipoFaltaDisciplinar(id)) {
						facesUtil.exibirMensagemErro("Tipo Falta Disciplinar não encontrada.", true);
						return "pretty:ListarTipoFaltaDisciplinar";
					}

					logDeAcesso(log, this.tipoFaltaDisciplinar);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "TipoFaltaDisciplinar");
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

	private boolean buscarTipoFaltaDisciplinar(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.tipoFaltaDisciplinar = service.buscarPorId(id);
		
		if (this.tipoFaltaDisciplinar == null) {
			return false;
		}
		
		return true;
	}

	public String salvar() {
		String retorno = null;
		
		try {
			service.salvar(this.tipoFaltaDisciplinar);
			facesUtil.exibirMensagemInfo("Tipo Falta Disciplinar " + this.tipoFaltaDisciplinar.getDescricao() + " salvo com sucesso!", true);
			retorno = "pretty:ListarTipoFaltaDisciplinar";
			LoggerUtil.logInfo(Acao.SALVAR, this.tipoFaltaDisciplinar, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar Tipo Falta Disciplinar (" + this.tipoFaltaDisciplinar.getDescricao() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.tipoFaltaDisciplinar, e, log);
		}
		
		return retorno;
	}

	public void excluir(TipoFaltaDisciplinar tipoFaltaDisciplinar) {
		try {
			service.excluir(tipoFaltaDisciplinar);
			facesUtil.exibirMensagemInfo("Tipo Falta Disciplinar " + tipoFaltaDisciplinar.getDescricao() + " excluída com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, tipoFaltaDisciplinar, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Tipo Falta Disciplinar (" + tipoFaltaDisciplinar.getDescricao() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, tipoFaltaDisciplinar, e, log);
		}
	}
	
	public void pesquisar() {
		this.tipoFaltaDisciplinars = new LazyDataModel<TipoFaltaDisciplinar>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<TipoFaltaDisciplinar> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
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
			List<TipoFaltaDisciplinar> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarTipoFaltaDisciplinar));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "RELATÓRIO TIPO FALTA DISCIPLINAR", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de Tipo Falta Disciplinar: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Relatório de Tipo Falta Disciplinar", e, log);
		}
	}

	public FiltroTipoFaltaDisciplinar getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroTipoFaltaDisciplinar filtro) {
		this.filtro = filtro;
	}

	public TipoFaltaDisciplinar getTipoFaltaDisciplinar() {
		return tipoFaltaDisciplinar;
	}

	public void setTipoFaltaDisciplinar(TipoFaltaDisciplinar tipoFaltaDisciplinar) {
		this.tipoFaltaDisciplinar = tipoFaltaDisciplinar;
	}

	public LazyDataModel<TipoFaltaDisciplinar> getTipoFaltaDisciplinars() {
		return tipoFaltaDisciplinars;
	}
}
