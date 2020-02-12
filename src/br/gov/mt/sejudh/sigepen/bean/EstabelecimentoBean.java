package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEstabelecimento;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.service.EstabelecimentoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarEstabelecimento", pattern = "/estabelecimento", viewId = "/WEB-INF/pages/estabelecimento/ListarEstabelecimento.xhtml")
,@URLMapping(id = "CadastrarEstabelecimento", pattern = "/estabelecimento/cadastrar", viewId = "/WEB-INF/pages/estabelecimento/CadastrarEstabelecimento.xhtml")
,@URLMapping(id = "AlterarEstabelecimento", pattern = "/estabelecimento/alterar/#{id}", viewId = "/WEB-INF/pages/estabelecimento/CadastrarEstabelecimento.xhtml")
,@URLMapping(id = "VisualizarEstabelecimento", pattern = "/estabelecimento/visualizar/#{id}", viewId = "/WEB-INF/pages/estabelecimento/CadastrarEstabelecimento.xhtml")
})
public class EstabelecimentoBean extends MetodosDePaginaBean {

	private Estabelecimento estabelecimento;
	
	private FiltroEstabelecimento filtro;

	private LazyDataModel<Estabelecimento> estabelecimentos;
	
	private List<Cidade> cidades;
	
	private Estado estado;	
	
	@Inject
	private EstabelecimentoService estabelecimentoService;
	
	@Inject
	private FacesUtil facesUtil;

	@Inject
	private ComboService comboService;
	
	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		super.carregarFiltroPaginacao(FiltroEstabelecimento.class, this);
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.estabelecimento = new Estabelecimento(Status.ATIVO);
					break;
					
				case VISUALIZAR:
				case ALTERAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarEstabelecimento(id)) {
						facesUtil.exibirMensagemErro("Estabelecimento não encontrado.", true);
						return "pretty:ListarEstabelecimento";
					}

					this.estado = this.estabelecimento.getCidade().getEstado();
					this.cidades = comboService.listarCidade(this.estabelecimento.getCidade().getEstado());
					
					logDeAcesso(log, this.estabelecimento);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Estabelecimentos");
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
	
	private boolean buscarEstabelecimento(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.estabelecimento = estabelecimentoService.buscarPorId(id);
		
		if (this.estabelecimento == null) {
			return false;
		}
		
		return true;
	}

		
	public void listaCidades(AjaxBehaviorEvent event){
		try {
			Estado est = (Estado) ((SelectOneMenu) event.getSource()).getValue();
			cidades = comboService.listarCidade(est);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar cidades: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public String salvar() {
		String retorno = null;
		
		try {
			estabelecimentoService.salvar(this.estabelecimento);
			facesUtil.exibirMensagemInfo("Estabelecimento " + this.estabelecimento.getNome() + " salva com sucesso!", true);
			retorno = "pretty:ListarEstabelecimento";
			LoggerUtil.logInfo(Acao.SALVAR, this.estabelecimento, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar estabelecimento (" + this.estabelecimento.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.estabelecimento, e, log);
		}
		
		return retorno;
	}

	public void excluir(Estabelecimento estabelecimento) {
		try {
			estabelecimentoService.excluir(estabelecimento);
			facesUtil.exibirMensagemInfo("Estabelecimento " + estabelecimento.getNome() + " excluída com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, estabelecimento, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir estabelecimento (" + estabelecimento.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, estabelecimento, e, log);	
		}
	}
	
	public void pesquisar() {
		this.estabelecimentos = new LazyDataModel<Estabelecimento>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Estabelecimento> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {				
				setRowCount(estabelecimentoService.quantidadeFiltrados(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				return estabelecimentoService.buscaPorFiltro(getFiltro(), false);
			}
		};	
	}
	
	public void imprimir() {
		try {
			List<Estabelecimento> lista = estabelecimentoService.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarEstabelecimentos));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Estabelecimentos", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de estabelecimentos: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Estabelecimentos", e, log);
		}
	}

	public FiltroEstabelecimento getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroEstabelecimento filtro) {
		this.filtro = filtro;
	}	

	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}

	public LazyDataModel<Estabelecimento> getEstabelecimentos() {
		return estabelecimentos;
	}
	
	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
}
