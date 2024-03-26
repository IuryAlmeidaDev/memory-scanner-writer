package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroServidor;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cargo;
import br.gov.mt.sejudh.sigepen.model.Categoria;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Funcao;
import br.gov.mt.sejudh.sigepen.model.Lotacao;
import br.gov.mt.sejudh.sigepen.model.Orgao;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.TipoDocumento;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.CargoService;
import br.gov.mt.sejudh.sigepen.service.CategoriaService;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.service.ContatoService;
import br.gov.mt.sejudh.sigepen.service.DocumentoService;
import br.gov.mt.sejudh.sigepen.service.EnderecoService;
import br.gov.mt.sejudh.sigepen.service.FuncaoService;
import br.gov.mt.sejudh.sigepen.service.OrgaoService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarServidor", pattern = "/servidor", viewId = "/WEB-INF/pages/servidor/ListarServidor.xhtml")
,@URLMapping(id = "CadastrarServidor", pattern = "/servidor/cadastrar", viewId = "/WEB-INF/pages/servidor/CadastrarServidor.xhtml")
,@URLMapping(id = "AlterarServidor", pattern = "/servidor/alterar/#{id}", viewId = "/WEB-INF/pages/servidor/CadastrarServidor.xhtml")
,@URLMapping(id = "VisualizarServidor", pattern = "/servidor/visualizar/#{id}", viewId = "/WEB-INF/pages/servidor/CadastrarServidor.xhtml")
})
public class ServidorBean extends MetodosDePaginaBean {

	private FiltroServidor filtro;

	private Servidor servidor;

	private LazyDataModel<ServidorDTO> servidores;
	
	private List<Orgao> orgaos;
	
	private List<Categoria> categorias;
	
	private List<Cargo> cargos;
	
	private List<Funcao> funcoes;
	
	private Categoria categoria;

	private Endereco endereco;	
	
	private Lotacao lotacao;
	
	private Contato contato;
	
	private Documento doc;
	
	private Lotacao ultimaLotacaoAdicionada;
	
	private List<Lotacao> lotacoesDoServidor;

	@Inject
	private ServidorService service;	
	
	@Inject
	private CargoService cargoService;
	
	@Inject
	private FuncaoService funcaoService;
	
	@Inject
	private CategoriaService categoriaService;
	
	@Inject
	private OrgaoService orgaoService;
	
	@Inject
	private UnidadePrisionalService unidadeService;
	
	@Inject
	private EnderecoService enderecoService;
	
	@Inject
	private ContatoService contatoService;
	
	@Inject
	private DocumentoService documentoService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@Inject
	private SecurityUtil security;
	
	private List<UnidadePrisional> unidadesPrisionais;
	
	@Inject
	private ComboService comboService;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		if(!security.possuiPermissao("ListarTodosServidores")) {
			this.setFiltro(new FiltroServidor());
			this.filtro.setUnidadeLotacao(SecurityUtil.getUnidadeSelecionada());
			
			super.carregarFiltroPaginacao(FiltroServidor.class, this, getFiltro());
		} else {
			super.carregarFiltroPaginacao(FiltroServidor.class, this);
		}
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch(this.getAcaoDeTela()) {
				case CADASTRAR:
					this.servidor = new Servidor(Status.ATIVO);
					inicializaListas();
					break;
					
				case ALTERAR:
				case VISUALIZAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarServidor(id)) {
						facesUtil.exibirMensagemErro("Servidor não encontrado.", true);
						return "pretty:ListarServidor";
					}
					
					inicializaListas();
					preencherCategoria();
					logDeAcesso(log, this.servidor);
					break;
					
				case LISTAR:
					unidadesPrisionais   = comboService.getUnidadesPrisionais();
					pesquisar();
					logDeAcesso(log, "Servidores");
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

	private boolean buscarServidor(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.servidor = service.buscarServidorCompletoPorId(id);
		
		if (this.servidor == null) {
			return false;
		}
		
		return true;
	}
	
	private void inicializaListas() {
		this.orgaos = orgaoService.listarOrgaoAtivo();
		this.categorias = categoriaService.listarCategoriaAtiva();
	}
	
	private void preencherCategoria() {
		if (this.servidor.getCargo() != null) {
			if (this.servidor.getCargo().getCategoria() != null) {
				this.categoria = this.servidor.getCargo().getCategoria();
				carregarCargoFuncao();
			}
		}
	}
	
	private void listarCargosPorCategoria() {
		this.cargos = cargoService.listarCargosPorCategoria(categoria);
	}
	
	private void listarFuncoesPorCargo() {
		this.funcoes = funcaoService.listarFuncoesPorCargo(servidor.getCargo());
	}
	
	private void carregarCargoFuncao() {
		listarCargosPorCategoria();
		listarFuncoesPorCargo();
	}

	public String salvar() {
		String retorno = null;
		
		try {
			if(service.jaExisteServidorComMatricula(this.servidor)){
				throw new ValidacaoException("Já existe um servidor com a matrícula nº. "+ this.servidor.getMatricula());
			}
			
			validarLotacaoEmAberto();
			
			service.salvar(this.servidor);
			facesUtil.exibirMensagemInfo("Servidor(a) " + this.servidor.getNome() + " salvo com sucesso!", true);
		 	retorno = "pretty:ListarServidor";
		 	LoggerUtil.logInfo(Acao.SALVAR, this.servidor, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		}
		catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar servidor(a) (" + this.servidor.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.servidor, e, log);		
		}

		return retorno;
	}

	public void excluir(ServidorDTO servidor) {
		try {
			service.excluir(servidor);
			facesUtil.exibirMensagemInfo("Servidor(a) " + servidor.getNome() + " excluído(a) com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, servidor, log);
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir servidor (" + servidor.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, servidor, e, log);
		}
	}	

	public void pesquisar() {
		this.servidores = new LazyDataModel<ServidorDTO>() {
			private static final long serialVersionUID = 1L;
			
			public List<ServidorDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {				
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
	
	public void excluirContato(Contato contato) {
		try {
			this.servidor.getContatos().remove(contato);
			
			if (this.servidor.getId()>0) {
				contatoService.excluirContato(contato);
				LoggerUtil.logInfo(Acao.EXCLUIR, contato, log);
			} else {
				LoggerUtil.logInfo(Acao.REMOVER, contato, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir contato do servidor (" + contato.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, contato, e, log);
		}
	}
	
	public void excluirEndereco(Endereco endereco) {
		try {
			this.servidor.getEnderecos().remove(endereco);
			
			if (this.servidor.getId()>0) {
				enderecoService.excluirEndereco(endereco);
				LoggerUtil.logInfo(Acao.EXCLUIR, endereco, log);
			} else {
				LoggerUtil.logInfo(Acao.REMOVER, endereco, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir endereço do servidor (" + endereco.getLogradouro() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, endereco, e, log);
		}
	}
	
	public void excluirDocumento(Documento documento) {
		try {
			this.servidor.getDocumentos().remove(documento);
		
			if (this.servidor.getId()>0) {
				documentoService.excluirDocumento(documento);
				LoggerUtil.logInfo(Acao.EXCLUIR, documento, log);
			} else {
				LoggerUtil.logInfo(Acao.REMOVER, documento, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir documento do servidor (" + documento.getTipoDocumento().getDescricao() +
					" / " + documento.getNumero() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, documento, e, log);
		}
	}
	
	private void validarLotacaoEmAberto() throws ValidacaoException {
		Optional<Lotacao> lotacaoAberta = servidor.getLotacao().stream().filter(x -> x.getDataFim() == null).findFirst();
		
		if (servidor.getFuncao() != null) {
			if (servidor.getFuncao().getLotacaoObrigatoria()) {
				if (!lotacaoAberta.isPresent()) {
					throw new ValidacaoException("Insira uma lotação.");
				}
			} else {
				if (lotacaoAberta.isPresent()) {
					if (servidor.getId() > 0) {
						throw new ValidacaoException("Função selecionada não permite lotação, por favor finalize a lotação.");
					} else {
						Predicate<Lotacao> podeRemover = lot -> lot.equals(ultimaLotacaoAdicionada);
						servidor.getLotacao().removeIf(podeRemover);
						throw new ValidacaoException("Função selecionada não permite lotação, a lotação foi removida.");
					}
				}
			}
		}
	}
	
	public void configurarLotacao() {
		try {
			validarLotacaoEmAberto();
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
		}
	}
		
	public void configurarCargoFuncao(AjaxBehaviorEvent event) {
		try {
			listarCargosPorCategoria();
			servidor.setCargo(null);
			configurarFuncao(null);
			validarLotacaoEmAberto();
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao carregar cargos e funções: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void configurarFuncao(AjaxBehaviorEvent event) {
		try {
			listarFuncoesPorCargo();
			servidor.setFuncao(null);
			validarLotacaoEmAberto();
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao carregar funções: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void abrirDialogContato(ActionEvent event) {
		this.contato = null;
		facesUtil.exibirPaginaDialog("pretty:AssociarContato");
	}
	
	public void abrirDialogEndereco(ActionEvent event) {
		this.endereco = null;
		facesUtil.exibirPaginaDialog("pretty:AssociarEndereco");
	}
	
	public void abrirDialogDocumento(ActionEvent event) {
		this.doc = null;
		facesUtil.exibirPaginaDialog("pretty:AssociarDocumento");
	}
	
	public void abrirDialogAlterarContato(Contato contato) {
		this.contato = contato;
		facesUtil.setObjetoSessao("contato", contato);		
		facesUtil.exibirPaginaDialog("pretty:AssociarContato");
		LoggerUtil.logInfo(Acao.ALTERAR, contato, log);
	}
	
	public void abrirDialogAlterarEndereco(Endereco endereco) {
		this.endereco = endereco;
		facesUtil.setObjetoSessao("endereco", endereco);		
		facesUtil.exibirPaginaDialog("pretty:AssociarEndereco");
		LoggerUtil.logInfo(Acao.ALTERAR, endereco, log);
	}
	
	public void abrirDialogAlterarDocumento(Documento documento) {
		this.doc = new Documento(documento);
		facesUtil.setObjetoSessao("documento", documento);		
		facesUtil.exibirPaginaDialog("pretty:AssociarDocumento");
		LoggerUtil.logInfo(Acao.ALTERAR, documento, log);
	}
	
	public void incluirContato(SelectEvent event) {
		Contato contato = (Contato) event.getObject();
		
		try {
			if (contato != null) {
				if (this.contato != null) {
					servidor.getContatos().remove(this.contato);
					servidor.getContatos().add(contato);
				} else {
					contato.setServidor(this.servidor);
					servidor.getContatos().add(contato);
				}
				
				if (this.servidor.getId()>0) {
					contato = contatoService.salvarContato(contato);
					LoggerUtil.logInfo(Acao.SALVAR, contato, log);	
				} else {
					LoggerUtil.logInfo(Acao.ADICIONAR, contato, log);	
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir contato no servidor: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, contato, e, log);
		}
	}
	
	public void incluirEndereco(SelectEvent event) {
		Endereco endereco = (Endereco) event.getObject();
		
		try {
			if (endereco != null) {				
				if (this.endereco != null) {
					servidor.getEnderecos().remove(this.endereco);				
					servidor.getEnderecos().add(endereco);				
				} else {
					endereco.setServidor(this.servidor);
					servidor.getEnderecos().add(endereco);
				}
				
				if (this.servidor.getId()>0) {
					endereco = enderecoService.salvarEndereco(endereco);
					LoggerUtil.logInfo(Acao.SALVAR, endereco, log);	
				} else {
					LoggerUtil.logInfo(Acao.ADICIONAR, endereco, log);	
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir endereço no servidor: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, endereco, e, log);
		}
	}
	
	public void incluirDocumento(SelectEvent event) {
		Documento documento = (Documento) event.getObject();
		
		try {
			if (documento != null) {
				if (this.doc == null) {
					if (jaPossuiTipoDocumento(servidor.getDocumentos(),documento.getTipoDocumento())) {
						throw new ValidacaoException("Servidor ja possui este tipo de Documento!");					
					} else {
						documento.setServidor(this.servidor);
						servidor.getDocumentos().add(documento);
					}
				} else {
					servidor.getDocumentos().remove(documento);
					
					if (jaPossuiTipoDocumento(servidor.getDocumentos(),	documento.getTipoDocumento())) {
						servidor.getDocumentos().add(this.doc);
						throw new ValidacaoException("Servidor ja possui este tipo de Documento!");
					} else {						
						servidor.getDocumentos().add(documento);
					}
				}
				
				if (this.servidor.getId()>0) {
					documento = documentoService.salvarDocumento(documento);
					LoggerUtil.logInfo(Acao.SALVAR, documento, log);	
				} else {
					LoggerUtil.logInfo(Acao.ADICIONAR, documento, log);					
				}
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir documento no servidor: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, documento, e, log);
		}
	}
	
	public boolean jaPossuiTipoDocumento(List<Documento> documentos, TipoDocumento tipoDoc){
		for(Documento doc: documentos){
			if(doc.getTipoDocumento().equals(tipoDoc) ){
				return true;				
			}
		}		
		return false;
	}
	
	public Documento buascaDocumentoPorId(List<Documento> documentos, long id){
		for(Documento doc: documentos){
			if(doc.getId() == id){
				return doc;				
			}
		}		
		return null;
	}
	
	public void configurarDialogLotacao(ActionEvent event) {
		this.lotacao = new Lotacao();
	}
	
	public void abrirDialogUnidade(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarUnidade");
	}
	
	public void associarUnidade(SelectEvent event) {
		UnidadePrisionalDTO u = (UnidadePrisionalDTO) event.getObject();
		
		try {
			if (u == null) {
				return;
			}
			
			UnidadePrisional unidade = unidadeService.buscaSimplesPorId(u.getId());
			
			if (unidade == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Unidade não encontrada.");
				return;
			}
				
			this.lotacao.setUnidade(unidade);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao associar unidade no servidor: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, u, e, log);
		}
	}
	
	public void alterarLotacao(Lotacao l) {
		this.lotacao = l;
		RequestContext.getCurrentInstance().update("formDlg");
		RequestContext.getCurrentInstance().execute("PF('dlg').show()");
		LoggerUtil.logInfo(Acao.ALTERAR, this.lotacao, log);
	}
	
	public void incluirLotacao(ActionEvent event) {
		try {
			if (this.lotacao.getUnidade() == null) {
				throw new ValidacaoException("É necessário associar uma unidade antes de continuar.");
			}
			
			if (this.lotacao.getDataFim() != null) {
				if (this.lotacao.getDataFim().before(this.lotacao.getDataInicio())) {
					throw new ValidacaoException("A data final deve ser maior que a data de inicio.");
				}
			}
			
			for (Lotacao l : this.servidor.getLotacao()) {
				if (l.getDataFim() == null && !this.lotacao.equals(l)) {
					throw new ValidacaoException("Preencha a data final da lotação " + l.getUnidade().getNome() + " antes de adicionar uma nova lotação.");
				}					
			}
			
			for (Lotacao l : this.servidor.getLotacao()) {
				if (novaLotacaoEstaNoMesmoIntervalo(this.lotacao.getDataInicio(), this.lotacao.getDataFim(), l.getDataInicio(), l.getDataFim())) {
					throw new ValidacaoException("No período informado  o servidor esteve em " + l.getUnidade().getNome() + ", favor verificar.");
				}
			}
			
			if (this.servidor.getId() > 0) {
				if (this.lotacao.getDataFim() != null && service.servidorEhResponsavelDaUnidade(this.lotacao)) {
					this.lotacao.setDataFim(null);
					throw new ValidacaoException("Não é possível finalizar lotação, pois o servidor é responsável da unidade.");
				}
			}
			
			this.lotacao.setServidor(this.servidor);
			this.servidor.getLotacao().remove(this.lotacao);
			this.servidor.getLotacao().add(this.lotacao);
			LoggerUtil.logInfo(Acao.ADICIONAR, this.lotacao, log);
			ultimaLotacaoAdicionada = this.lotacao;
			this.lotacao = null;
			
			RequestContext.getCurrentInstance().execute("PF('dlg').hide()");
			RequestContext.getCurrentInstance().update("servidorForm:lotacao");
		} catch (ValidacaoException e) { 
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Erro ao incluir lotação no servidor: " + e.getMessage());
		}
	}
	
	public boolean validarLotacaoBloqueada(Lotacao lotacao) {
		boolean lotacaoBloqueada = false;
		
		try {
			if (lotacao.getId() == 0) {
				return false;
			}
			
			lotacaoBloqueada = service.validarLotacaoBloqueada(lotacao.getId());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Erro ao validar lotação: " + e.getMessage());
		}
		
		return lotacaoBloqueada;
	}

	/** Valida se o intervalo dataInicio/dataFim conflita com o intervalo dataIntervaloInicio/dataIntervaloFim  */
	private boolean novaLotacaoEstaNoMesmoIntervalo(Date dataInicio, Date dataFim, Date dataIntervaloInicio, Date dataIntervaloFim) {
		
		boolean dataInicioEstaNoIntervalo = dataInicio.after(dataIntervaloInicio) && dataInicio.before(dataIntervaloFim);
		boolean dataFimEstaNoIntervalo = (dataFim != null) ? dataFim.after(dataIntervaloInicio) && dataFim.before(dataIntervaloFim) : false;
		boolean dataInicioEDataFimEstaoNoIntervalo = dataInicioEstaNoIntervalo && dataFimEstaNoIntervalo;
		boolean dataInicioEDataFimSaoMaioresQueIntervalo = (dataFim != null) ? dataIntervaloInicio.after(dataInicio) && dataIntervaloInicio.before(dataFim) && dataIntervaloFim.after(dataInicio) && dataIntervaloFim.before(dataFim) : false;
		
		return dataInicioEstaNoIntervalo || dataFimEstaNoIntervalo || dataInicioEDataFimEstaoNoIntervalo || dataInicioEDataFimSaoMaioresQueIntervalo;
	}

	public Date getDataMinimaLotacao() {
		Date data = null;

		for (Lotacao l : this.servidor.getLotacao()) {
			if (!l.equals(this.lotacao)) {
				if (data == null) {
					data = l.getDataFim();
				}
				
				if (l.getDataFim() != null) {
					if(l.getDataFim().after(data)) {
						data = l.getDataFim();
					}
				}
			}
		}
		
		return data;
	}
	
	public Date getDataMaximaLotacao() {
		Calendar c = Calendar.getInstance();
		return c.getTime();
	}
	
	public void imprimir() {
		try {
			List<ServidorDTO> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarServidor));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Servidores", log);
			
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de servidores: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Servidores", e, log);
		}
	}
	
	public void imprimirRegistro(ServidorDTO servidor) {
		try {
			List<Servidor> lista = new ArrayList<Servidor>();
			Servidor serv = service.buscarServidorCompletoPorId(servidor.getId());
			lista.add(serv);
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.RegistroServidor));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, servidor, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir registro do servidor (" + servidor.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, servidor, e, log);
		}
	}
	
	public FiltroServidor getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroServidor filtro) {
		this.filtro = filtro;
	}
	
	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public LazyDataModel<ServidorDTO> getServidores() {
		return servidores;
	}

	public List<Cargo> getCargos() {
		return cargos;
	}

	public void setCargos(List<Cargo> cargos) {
		this.cargos = cargos;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public List<Orgao> getOrgaos() {
		return orgaos;
	}

	public void setOrgaos(List<Orgao> orgaos) {
		this.orgaos = orgaos;
	}

	public List<Funcao> getFuncoes() {
		return funcoes;
	}

	public void setFuncoes(List<Funcao> funcoes) {
		this.funcoes = funcoes;
	}

	public Lotacao getLotacao() {
		return lotacao;
	}

	public void setLotacao(Lotacao lotacao) {
		this.lotacao = lotacao;
	}

	public List<Lotacao> getLotacoesDoServidor() {
		return lotacoesDoServidor;
	}

	public void setLotacoesDoServidor(List<Lotacao> lotacoesDoServidor) {
		this.lotacoesDoServidor = lotacoesDoServidor;
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}
}
