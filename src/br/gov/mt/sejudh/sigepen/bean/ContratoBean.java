package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroContrato;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.Fornecedor;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoDTO;
import br.gov.mt.sejudh.sigepen.service.ContratoService;
import br.gov.mt.sejudh.sigepen.service.FornecedorService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarContrato", pattern = "/contrato", viewId = "/WEB-INF/pages/contrato/ListarContrato.xhtml"),
		@URLMapping(id = "CadastrarContrato", pattern = "/contrato/cadastrar", viewId = "/WEB-INF/pages/contrato/CadastrarContrato.xhtml"),
		@URLMapping(id = "AlterarContrato", pattern = "/contrato/alterar/#{id}", viewId = "/WEB-INF/pages/contrato/CadastrarContrato.xhtml"),
		@URLMapping(id = "VisualizarContrato", pattern = "/contrato/visualizar/#{id}", viewId = "/WEB-INF/pages/contrato/CadastrarContrato.xhtml") })
public class ContratoBean extends MetodosDePaginaBean {

	private FiltroContrato filtro;

	private Contrato contrato;

	private LazyDataModel<ContratoDTO> contratos;

	@Inject
	private ContratoService contratoService;
	
	@Inject
	private FornecedorService fornecedorService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@Inject
	private SecurityUtil security;

	private Long contratoSelecionada;
	
	private List<UnidadePrisional> unidadesPrisionais;
	
	private List<Fornecedor> fornecedores;
	
	@Inject
	private UnidadePrisionalService unidadeService;

	public Contrato getcontrato() {
		return contrato;
	}

	public void setcontrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public Long getcontratoSelecionada() {
		return this.contratoSelecionada;
	}

	public void setcontratoSelecionada(Long contratoSelecionada) {
		this.contratoSelecionada = contratoSelecionada;
	}

	public LazyDataModel<ContratoDTO> getContratos() {
		return contratos;
	}

	public void setContratos(LazyDataModel<ContratoDTO> contratos) {
		this.contratos = contratos;
	}

	public FiltroContrato getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroContrato filtro) {
		this.filtro = filtro;
	}
	
	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}
	
	public List<Fornecedor> getFornecedores() {
		return fornecedores;
	}

	public void setFornecedores(List<Fornecedor> fornecedores) {
		this.fornecedores = fornecedores;
	}
	
	public FornecedorService getFornecedorService() {
		return fornecedorService;
	}

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		if (getAcaoDeTela().equals(AcaoDeTela.ALTERAR) || getAcaoDeTela().equals(AcaoDeTela.VISUALIZAR)) {
			if (facesUtil.getQueryParameter("id") != null) {
				this.contratoSelecionada = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
			}
		}
		if (!security.possuiPermissao("ListarTodasUnidades")) {
			this.setFiltro(new FiltroContrato());
			this.filtro.setId(SecurityUtil.getUnidadeSelecionada().getId());

			super.carregarFiltroPaginacao(FiltroContrato.class, this, getFiltro());
		} else {
			super.carregarFiltroPaginacao(FiltroContrato.class, this);
		}
	}

	@URLAction(onPostback = false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.setUnidadesPrisionais(unidadeService.listarUnidadesNaoInativas());
				this.fornecedores = fornecedorService.listar();
				this.contrato = new Contrato();
				this.contrato.setFornecedor(new Fornecedor());
				break;
			case VISUALIZAR:
			case ALTERAR:

				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

				if (!buscarUnidade(id)) {
					facesUtil.exibirMensagemErro("Contrato não encontrada.", true);
					return "pretty:ListarContrato";
				}

				logDeAcesso(log, this.contrato);

				break;

			case LISTAR:
				pesquisar();
				logDeAcesso(log, "Contrato");
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

	private boolean buscarUnidade(long id) {
		if (id <= 0) {
			return false;
		}

		this.contrato = contratoService.buscaPorId(id);

		if (this.contrato == null) {
			return false;
		}

		return true;
	}

	public void pesquisar() {
		this.contratos = new LazyDataModel<ContratoDTO>() {
			private static final long serialVersionUID = 1L;

			public List<ContratoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(contratoService.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return contratoService.buscarPorFiltro(getFiltro());
			}
		};
	}

	public String salvar() {
		String retorno = null;

		try {
			contratoService.salvar(this.contrato);
			facesUtil.exibirMensagemInfo("contrato " + this.contrato.getNumContrato() + " salva com Sucesso!", true);
			retorno = "pretty:ListarContrato";
			LoggerUtil.logInfo(Acao.SALVAR, contrato, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao salvar contrato (" + this.contrato.getNumContrato() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, contrato, e, log);
		}

		return retorno;
	}

	public void abrirDialogEndereco(ActionEvent event) {
//		facesUtil.setObjetoSessao("endereco", this.contrato.getEndereco());
		facesUtil.exibirPaginaDialog("pretty:AssociarEndereco");
	}

	public void incluirEndereco(SelectEvent event) {
//		Endereco endereco = (Endereco) event.getObject();
//		
//		try {
//			if (endereco != null) {
//				endereco.setContrato(contrato);
//				this.contrato.setEndereco(endereco);
//				
//				if (this.contrato.getId() > 0) {	
//					enderecoService.salvarEndereco(endereco);
//				}
//			}
//		} catch (Exception e) {
//			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir endereço n contrato: " + e.getMessage());
//			LoggerUtil.logErro(Acao.ADICIONAR, endereco, e, log);
//		}
	}

	public void excluirContato(Contato contato) {
//		try {
//			this.contrato.getContatos().remove(contato);
//			
//			if (this.contrato.getId()>0) {
//				contatoService.excluirContato(contato);
//				LoggerUtil.logInfo(Acao.EXCLUIR, contato, log);
//			}else {
//				LoggerUtil.logInfo(Acao.REMOVER, contato, log);
//			}
//		} catch (Exception e) {
//			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir contato do contrato: " + e.getMessage());
//			LoggerUtil.logErro(Acao.ASSOCIAR, contato, e, log);
//		}
	}

	public void abrirDialogContato(ActionEvent event) {
//		this.contato = null;
//		facesUtil.exibirPaginaDialog("pretty:AssociarContato");
	}

	public void abrirDialogAlterarContato(Contato contato) {
//		this.contato = contato;
//		facesUtil.setObjetoSessao("contato", contato);		
//		facesUtil.exibirPaginaDialog("pretty:AssociarContato");
//		LoggerUtil.logInfo(Acao.ALTERAR, contato, log);
	}

	public void incluirContato(SelectEvent event) {
//		Contato contato = (Contato) event.getObject();
//		
//		try {
//			if (contato != null) {			
//				if (this.contato != null) {
//					this.contrato.getContatos().remove(this.contato);
//					contrato.getContatos().add(contato);
//				} else {
//					contato.setContrato(this.contrato);
//					this.contrato.getContatos().add(contato);
//				}
//				
//				if (this.contrato.getId()>0) {
//					contatoService.salvarContato(contato);
//					LoggerUtil.logInfo(Acao.SALVAR, contato, log);
//				}else {
//					LoggerUtil.logInfo(Acao.ADICIONAR, contato, log);	
//				}
//			}
//		} catch (Exception e) {
//			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir contato no contrato: " + e.getMessage());
//			LoggerUtil.logErro(Acao.ADICIONAR, contato, e, log);
//		}
	}

}
