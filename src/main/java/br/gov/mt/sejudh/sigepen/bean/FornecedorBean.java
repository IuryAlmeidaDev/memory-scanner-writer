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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFornecedor;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.TipoArquivo;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.ArquivoJustificativa;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Fornecedor;
import br.gov.mt.sejudh.sigepen.model.Justificativa;
import br.gov.mt.sejudh.sigepen.model.dto.FornecedorDTO;
import br.gov.mt.sejudh.sigepen.service.ContatoService;
import br.gov.mt.sejudh.sigepen.service.EnderecoService;
import br.gov.mt.sejudh.sigepen.service.FornecedorService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarFornecedor", pattern = "/fornecedor", viewId = "/WEB-INF/pages/fornecedor/ListarFornecedor.xhtml"),
		@URLMapping(id = "CadastrarFornecedor", pattern = "/fornecedor/cadastrar", viewId = "/WEB-INF/pages/fornecedor/CadastrarFornecedor.xhtml"),
		@URLMapping(id = "Alterarfornecedor", pattern = "/fornecedor/alterar/#{id}", viewId = "/WEB-INF/pages/fornecedor/CadastrarFornecedor.xhtml"),
		@URLMapping(id = "Visualizarfornecedor", pattern = "/fornecedor/visualizar/#{id}", viewId = "/WEB-INF/pages/fornecedor/CadastrarFornecedor.xhtml") })
public class FornecedorBean extends MetodosDePaginaBean {

	private FiltroFornecedor filtro;

	private Fornecedor fornecedor;

	private Contato contato;

	private Status inativo = Status.INATIVO;

	private LazyDataModel<FornecedorDTO> fornecedores;

	@Inject
	private FornecedorService fornecedorService;

	@Inject
	private EnderecoService enderecoService;

	@Inject
	private ContatoService contatoService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	private Long fornecedorSelecionada;

	public Fornecedor getfornecedor() {
		return fornecedor;
	}

	public void setfornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Long getfornecedorSelecionada() {
		return this.fornecedorSelecionada;
	}

	public void setfornecedorSelecionada(Long fornecedorSelecionada) {
		this.fornecedorSelecionada = fornecedorSelecionada;
	}

	public LazyDataModel<FornecedorDTO> getFornecedores() {
		return fornecedores;
	}

	public void setFornecedores(LazyDataModel<FornecedorDTO> fornecedores) {
		this.fornecedores = fornecedores;
	}

	public FiltroFornecedor getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroFornecedor filtro) {
		this.filtro = filtro;
	}

	public boolean getInativo() {
		return this.inativo.equals(this.fornecedor.getStatus());
	}

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		this.setFiltro(new FiltroFornecedor());
		super.carregarFiltroPaginacao(FiltroFornecedor.class, this);
	}

	@URLAction(onPostback = false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.fornecedor = new Fornecedor();
				break;
			case VISUALIZAR:
			case ALTERAR:

				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

				if (!buscarFornecedor(id)) {
					facesUtil.exibirMensagemErro("Fornecedor não encontrada.", true);
					return "pretty:ListarFornecedor";
				}

				logDeAcesso(log, this.fornecedor);

				break;

			case LISTAR:
				pesquisar();
				logDeAcesso(log, "Fornecedor");
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

	private boolean buscarFornecedor(long id) {
		if (id <= 0) {
			return false;
		}

		this.fornecedor = fornecedorService.buscaPorId(id);

		if (this.fornecedor == null) {
			return false;
		}
		if (this.fornecedor.getJustificativa() == null) {
			this.fornecedor.setJustificativa(new Justificativa());
		}

		return true;
	}

	public void pesquisar() {
		this.fornecedores = new LazyDataModel<FornecedorDTO>() {
			private static final long serialVersionUID = 1L;

			public List<FornecedorDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(fornecedorService.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					LoggerUtil.logErro(e, log);
				}

				return fornecedorService.buscarPorFiltro(getFiltro());
			}
		};
	}

	private boolean validar() {
		return validarEndereco() && validarContato();
	}
	
	private boolean validarEndereco() {
		boolean validar = this.fornecedor.getEndereco() != null;
		if(!validar) {			
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Endereço é Obrigatorio");
		}
		return validar;
	}
	
	private boolean validarContato() {
		boolean validar = this.fornecedor.getContatos().size() > 0;
		if(!validar) {			
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Contado é Obrigatorio");
		}
		return validar;
	}

	public String salvar() {
		String retorno = null;

		if (this.validar()) {
			try {
				fornecedorService.salvar(this.fornecedor);
				facesUtil.exibirMensagemInfo("fornecedor " + this.fornecedor.getNomeFantasia() + " salva com Sucesso!",
						true);
				retorno = "pretty:ListarFornecedor";
				LoggerUtil.logInfo(Acao.SALVAR, fornecedor, log);
			} catch (ValidacaoException e) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
			} catch (Exception e) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
						"Erro ao salvar fornecedor (" + this.fornecedor.getNomeFantasia() + "): " + e.getMessage());
				LoggerUtil.logErro(Acao.SALVAR, fornecedor, e, log);
			}

		}
		return retorno;		
	}

	public void abrirDialogEndereco(ActionEvent event) {
		facesUtil.setObjetoSessao("endereco", this.fornecedor.getEndereco());
		facesUtil.exibirPaginaDialog("pretty:AssociarEndereco");
	}

	public void incluirEndereco(SelectEvent event) {
		Endereco endereco = (Endereco) event.getObject();

		try {
			if (endereco != null) {
				endereco.setFornecedor(fornecedor);
				this.fornecedor.setEndereco(endereco);

				if (this.fornecedor.getId() > 0) {
					enderecoService.salvarEndereco(endereco);
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao incluir endereço n fornecedor: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, endereco, e, log);
		}
	}

	public void excluirContato(Contato contato) {
		try {
			this.fornecedor.getContatos().remove(contato);

			if (this.fornecedor.getId() > 0) {
				contatoService.excluirContato(contato);
				LoggerUtil.logInfo(Acao.EXCLUIR, contato, log);
			} else {
				LoggerUtil.logInfo(Acao.REMOVER, contato, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao excluir contato do fornecedor: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, contato, e, log);
		}
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

	public void incluirContato(SelectEvent event) {
		Contato contato = (Contato) event.getObject();

		try {
			if (contato != null) {
				if (this.contato != null) {
					this.fornecedor.getContatos().remove(this.contato);
					fornecedor.getContatos().add(contato);
				} else {
					contato.setFornecedor(this.fornecedor);
					this.fornecedor.getContatos().add(contato);
				}

				if (this.fornecedor.getId() > 0) {
					contatoService.salvarContato(contato);
					LoggerUtil.logInfo(Acao.SALVAR, contato, log);
				} else {
					LoggerUtil.logInfo(Acao.ADICIONAR, contato, log);
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao incluir contato no fornecedor: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, contato, e, log);
		}
	}

	public void associarArquivoJustificativa(FileUploadEvent event) {

		if (event == null) {
			return;
		}

		ArquivoJustificativa arquivoJustificativa = new ArquivoJustificativa();
		arquivoJustificativa.setJustificativa(this.fornecedor.getJustificativa());
		arquivoJustificativa.setDataInclusao(Sigepen.getDataAtual());
		arquivoJustificativa.setNome(event.getFile().getFileName());
		arquivoJustificativa.setArquivo(event.getFile().getContents());
		arquivoJustificativa.setContentType(event.getFile().getContentType());

		this.fornecedor.getJustificativa().setArquivoJustificativa(arquivoJustificativa);

		LoggerUtil.logInfo(Acao.ADICIONAR, arquivoJustificativa + " " + this.fornecedor, log);
	}

	public void downloadArquivoJustificativa() {
		Arquivo arquivo = new Arquivo();
		ArquivoJustificativa arquivoJustificativa = this.fornecedor.getJustificativa().getArquivoJustificativa();
		byte[] pdf = arquivoJustificativa.getArquivo();
		arquivo.setBytes(pdf);
		arquivo.setContentType("application/pdf");
		arquivo.setNome(arquivoJustificativa.getNome());
		arquivo.setTipo(TipoArquivo.PDF);
		fileDownload.prepararArquivo(arquivo);
		RequestContext.getCurrentInstance().execute("download()");
	}

}
