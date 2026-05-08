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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroOrgao;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.model.Orgao;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.service.OrgaoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarOrgao", pattern = "/orgao", viewId = "/WEB-INF/pages/orgao/ListarOrgao.xhtml")
,@URLMapping(id = "CadastrarOrgao", pattern = "/orgao/cadastrar", viewId = "/WEB-INF/pages/orgao/CadastrarOrgao.xhtml")
,@URLMapping(id = "AlterarOrgao", pattern = "/orgao/alterar/#{id}", viewId = "/WEB-INF/pages/orgao/CadastrarOrgao.xhtml")
,@URLMapping(id = "VisualizarOrgao", pattern = "/orgao/visualizar/#{id}", viewId = "/WEB-INF/pages/orgao/CadastrarOrgao.xhtml")})
public class OrgaoBean extends MetodosDePaginaBean {
	
	private Orgao orgao;
	
	private FiltroOrgao filtro;

	private LazyDataModel<Orgao> orgaos;
	
	private List<Cidade> cidades;
	
	private Estado estado;
	
	@Inject
	private OrgaoService orgaoService;

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
		super.carregarFiltroPaginacao(FiltroOrgao.class, this);
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch(this.getAcaoDeTela()) {
				case CADASTRAR:
					this.orgao = new Orgao(Status.ATIVO);
					break;
					
				case ALTERAR:
				case VISUALIZAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

					if (!buscarOrgao(id)) {
						facesUtil.exibirMensagemErro("Orgão não encontrado.", true);
						return "pretty:ListarOrgao";
					}
					
					if (!validarOrgao()) {
						facesUtil.exibirMensagemErro("Operação não permitida", true);
						return "pretty:ListarOrgao";
					}

					this.estado = this.orgao.getCidade().getEstado();
					this.cidades = comboService.listarCidade(this.orgao.getCidade().getEstado());
					logDeAcesso(log, this.orgao);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Órgãos");
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
	
	private boolean buscarOrgao(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.orgao = orgaoService.buscarPorId(id);
		
		if (this.orgao == null) {
			return false;
		}
		
		return true;
	}
	
	private boolean validarOrgao() {
		if (this.orgao.getId() == Sigepen.SEJUDH_ID && this.getAcaoDeTela() == AcaoDeTela.ALTERAR) {
			return false;
		}
		
		return true;
	}

	public void listaCidades(AjaxBehaviorEvent event) {
		try {
			Estado est = (Estado) ((SelectOneMenu)event.getSource()).getValue();
			cidades = comboService.listarCidade(est);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public String salvar() {
		String retorno = null;
		
		try {
			orgaoService.salvar(this.orgao);
			facesUtil.exibirMensagemInfo("Orgão " + this.orgao.getNome() + " salvo com sucesso!", true);
			retorno = "pretty:ListarOrgao";
			LoggerUtil.logInfo(Acao.SALVAR, this.orgao, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar orgão (" + this.orgao.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.orgao, e, log);
		}
		
		return retorno;
	}

	public void excluir(Orgao orgao) {
		try {
			orgaoService.excluir(orgao);
			facesUtil.exibirMensagemInfo("Orgão " + orgao.getNome() + " excluído com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, orgao, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir orgão (" + orgao.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, orgao, e, log);
		}
	}

	public void pesquisar() {
		this.orgaos = new LazyDataModel<Orgao>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Orgao> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
				setRowCount(orgaoService.quantidadeFiltrados(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					LoggerUtil.logErro(e, log);
				}
				
				return orgaoService.buscaPorFiltro(getFiltro(), false);
			}
		};	
	}

	public void imprimir() {
		try {
			List<Orgao> lista = orgaoService.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarOrgaos));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Orgãos", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de orgãos: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Orgãos", e, log);
		}
	}
	
	public FiltroOrgao getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroOrgao filtro) {
		this.filtro = filtro;
	}		

	public Orgao getOrgao() {
		return orgao;
	}

	public void setOrgao(Orgao orgao) {
		this.orgao = orgao;
	}

	public LazyDataModel<Orgao> getorgaos() {
		return orgaos;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}
}
