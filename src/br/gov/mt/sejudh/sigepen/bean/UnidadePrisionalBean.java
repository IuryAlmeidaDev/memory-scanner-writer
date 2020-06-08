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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.ContatoService;
import br.gov.mt.sejudh.sigepen.service.EnderecoService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarUnidade", pattern = "/unidade", viewId = "/WEB-INF/pages/unidade/ListarUnidadePrisional.xhtml")
,@URLMapping(id = "CadastrarUnidade", pattern = "/unidade/cadastrar", viewId = "/WEB-INF/pages/unidade/CadastrarUnidadePrisional.xhtml")
,@URLMapping(id = "AlterarUnidade", pattern = "/unidade/alterar/#{id}", viewId = "/WEB-INF/pages/unidade/CadastrarUnidadePrisional.xhtml")
,@URLMapping(id = "VisualizarUnidade", pattern = "/unidade/visualizar/#{id}", viewId = "/WEB-INF/pages/unidade/CadastrarUnidadePrisional.xhtml")
})
public class UnidadePrisionalBean extends MetodosDePaginaBean {

	private FiltroUnidadePrisional filtro;
	
	private Boolean disabledAbaEstrutura;

	private UnidadePrisional unidade;

	private LazyDataModel<UnidadePrisionalDTO> unidades;
	
    private Estrutura estrutura;
	
	private TreeNode root;
	
	private Contato contato;
	
	@Inject
	private ServidorService servidorService;
	
	@Inject
	private UnidadePrisionalService unidadeService;
	
	@Inject
	private EnderecoService enderecoService;
	
	@Inject
	private ContatoService contatoService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private SecurityUtil security;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		if (!security.possuiPermissao("ListarTodasUnidades")) {
			this.setFiltro(new FiltroUnidadePrisional());
			this.filtro.setId(SecurityUtil.getUnidadeSelecionada().getId());
			
			super.carregarFiltroPaginacao(FiltroUnidadePrisional.class, this, getFiltro());
		} else {
			super.carregarFiltroPaginacao(FiltroUnidadePrisional.class, this);
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
		
		this.unidade = unidadeService.buscaPorId(id);
		
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
	
	public String salvar() {
		String retorno = null;
		
		try {
			unidadeService.salvar(this.unidade);
			facesUtil.exibirMensagemInfo("Unidade Prisional " + this.unidade.getNome() + " salva com Sucesso!", true);
			retorno = "pretty:ListarUnidade";			
			LoggerUtil.logInfo(Acao.SALVAR, unidade, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar unidade prisional (" + this.unidade.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, unidade, e, log);	
		}

		return retorno;
	}

	public void excluir(UnidadePrisionalDTO unidade) {
		try{
			unidadeService.excluir(unidade.getId());
			LoggerUtil.logInfo(Acao.EXCLUIR, unidade, log);
			facesUtil.exibirMensagemInfo("Unidade " + unidade.getNome() + " excluída com sucesso!", false);		
		}catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir unidade (" + unidade.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, unidade, e, log);
		}
	}
	
	public boolean possuiServidorLotado() {
		boolean possuiServidorLotado = false;
		
		try {
			possuiServidorLotado = unidadeService.possuiServidorLotado(this.unidade.getId());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao validar servidor lotado: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return possuiServidorLotado;
	}

	public void pesquisar() {
		this.unidades = new LazyDataModel<UnidadePrisionalDTO>() {
			private static final long serialVersionUID = 1L;
			
			public List<UnidadePrisionalDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters){
				setRowCount(unidadeService.quantidadeFiltrados(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return unidadeService.buscarPorFiltro(getFiltro(), false);
			}
		};
	}
	
	public Boolean possuiServidor() {
		return (this.unidade.getResponsavel() != null);
	}
	
	public void associarServidor(SelectEvent event) {
		ServidorDTO s = (ServidorDTO) event.getObject();	
		
		try {
			if (s == null) {
				return;
			}
			
			Servidor servidor = servidorService.buscarServidorSimplesPorId(s.getId());
			
			if (servidor == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Servidor não encontrado.");
				return;
			}
				
			this.unidade.setResponsavel(servidor);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao associar servidor na unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, s, e, log);
		}
	}
	
	public void abrirDialogServidor(ActionEvent event) {
		facesUtil.setObjetoSessao("unidadeLotacao", this.unidade);
		facesUtil.exibirPaginaDialog("pretty:AssociarServidor");
	}

	public void abrirDialogContato(ActionEvent event) {
		this.contato = null;
		facesUtil.exibirPaginaDialog("pretty:AssociarContato");
	}	
	
	public void abrirDialogAlterarContato(Contato contato) {
		this.contato = contato;
		facesUtil.setObjetoSessao("contato", contato);		
		facesUtil.exibirPaginaDialog("pretty:AssociarContato");
		LoggerUtil.logInfo(Acao.ALTERAR, contato, log);
	}
	
	public void abrirDialogEndereco(ActionEvent event) {
		facesUtil.setObjetoSessao("endereco", this.unidade.getEndereco());
		facesUtil.exibirPaginaDialog("pretty:AssociarEndereco");
	}
	
	public void incluirEndereco(SelectEvent event) {
		Endereco endereco = (Endereco) event.getObject();
		
		try {
			if (endereco != null) {
				endereco.setUnidadePrisional(this.unidade);
				this.unidade.setEndereco(endereco);
				
				if (this.unidade.getId() > 0) {	
					enderecoService.salvarEndereco(endereco);
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir endereço na unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, endereco, e, log);
		}
	}
	
	public void incluirContato(SelectEvent event) {
		Contato contato = (Contato) event.getObject();
		
		try {
			if (contato != null) {			
				if (this.contato != null) {
					unidade.getContatos().remove(this.contato);
					unidade.getContatos().add(contato);
				} else {
					contato.setUnidadePrisional(this.unidade);
					unidade.getContatos().add(contato);
				}
				
				if (this.unidade.getId()>0) {
					contatoService.salvarContato(contato);
					LoggerUtil.logInfo(Acao.SALVAR, contato, log);
				}else {
					LoggerUtil.logInfo(Acao.ADICIONAR, contato, log);	
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir contato na unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, contato, e, log);
		}
	}
	
	public void excluirContato(Contato contato) {
		try {
			this.unidade.getContatos().remove(contato);
			
			if (this.unidade.getId()>0) {
				contatoService.excluirContato(contato);
				LoggerUtil.logInfo(Acao.EXCLUIR, contato, log);
			}else {
				LoggerUtil.logInfo(Acao.REMOVER, contato, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir contato da unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, contato, e, log);
		}
	}
	
	public void imprimir() {
		try {
			List<UnidadePrisionalDTO> lista = unidadeService.buscarPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarUnidadePrisional));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Unidades", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de unidades: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Unidades", e, log);
		}
	}
	
	public Boolean verificarSeBiometriaEhObrigatoria() {
		return unidadeService.verificarSeBiometriaEhObrigatoria(this.unidade.getId());
	}
	
	public void selecionarEstruturaChamada(UnidadePrisionalDTO unidade) {
		facesUtil.setObjetoSessao("unidade", unidade);
		facesUtil.setObjetoSessao("tipo_relatorio", "unidade");
	}
	
	public void selecionarEstruturaChamada(Estrutura estrutura) {
		facesUtil.setObjetoSessao("estrutura", estrutura);
		facesUtil.setObjetoSessao("tipo_relatorio", "estrutura");
	}
	
	public FiltroUnidadePrisional getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroUnidadePrisional filtro) {
		this.filtro = filtro;
	}

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public LazyDataModel<UnidadePrisionalDTO> getUnidades() {
		return unidades;
	}

	public void setUnidades(LazyDataModel<UnidadePrisionalDTO> unidades) {
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
	
}
