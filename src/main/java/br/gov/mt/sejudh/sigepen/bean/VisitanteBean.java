package br.gov.mt.sejudh.sigepen.bean;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.ParenteDAO;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.hibernate.criterion.Order;
import org.hibernate.exception.ConstraintViolationException;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import br.gov.mt.sejudh.sigepen.biometrics.BiometriaRetorno;
import br.gov.mt.sejudh.sigepen.biometrics.BiometriaSDKDao;
import br.gov.mt.sejudh.sigepen.dao.CredenciamentoDAO;
import br.gov.mt.sejudh.sigepen.dao.PassagemDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitante;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitanteRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.AutorizacaoCadastroSemBiometria;
import br.gov.mt.sejudh.sigepen.model.Biometria;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.CredencialCertidao;
import br.gov.mt.sejudh.sigepen.model.CredencialDocumento;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.SolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.model.VisitanteCertidao;
import br.gov.mt.sejudh.sigepen.model.VisitanteDocumento;
import br.gov.mt.sejudh.sigepen.model.VisitanteFoto;
import br.gov.mt.sejudh.sigepen.model.VisitanteResponsavel;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.BiometriaService;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.service.ContatoService;
import br.gov.mt.sejudh.sigepen.service.CredenciamentoService;
import br.gov.mt.sejudh.sigepen.service.FaseDoCredenciamentoService;
import br.gov.mt.sejudh.sigepen.service.PessoaService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.SolicitacaoVisitaService;
import br.gov.mt.sejudh.sigepen.service.TipoVisitaService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.service.UsuarioService;
import br.gov.mt.sejudh.sigepen.service.VisitanteResponsavelService;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;
import br.gov.mt.sejudh.sigepen.service.VwVisitanteService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.ModelUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
		@URLMapping(id = "ListarVisitante", pattern = "/visitante", viewId = "/WEB-INF/pages/visitante/ListarVisitante.xhtml"),
		@URLMapping(id = "CadastrarVisitante", pattern = "/visitante/cadastrar", viewId = "/WEB-INF/pages/visitante/CadastrarVisitante.xhtml"),
		@URLMapping(id = VisitanteBean.mappingRecuperando, pattern = "/visitante/alterar/recuperando/#{id}", viewId = "/WEB-INF/pages/visitante/CadastrarVisitante.xhtml"),
		@URLMapping(id = VisitanteBean.mappingPessoa, pattern = "/visitante/alterar/pessoa/#{id}", viewId = "/WEB-INF/pages/visitante/CadastrarVisitante.xhtml"),
		@URLMapping(id = "AlterarVisitante", pattern = "/visitante/alterar/#{id}", viewId = "/WEB-INF/pages/visitante/AlterarVisitante.xhtml"),
		@URLMapping(id = "VisualizarVisitante", pattern = "/visitante/visualizar/#{id}", viewId = "/WEB-INF/pages/visitante/AlterarVisitante.xhtml"),
		@URLMapping(id = "VisualizarHistoricoCredenciamento", pattern = "/visitante/VisualizarHistoricoCredenciamento/#{id}/#{tab}", viewId = "/WEB-INF/pages/visitante/AlterarVisitante.xhtml"),
		@URLMapping(id = "ListarCredenciamentoVisitante", pattern = "/visitante/credenciar/#{id}", viewId = "/WEB-INF/pages/visitante/CredenciamentosVisitante.xhtml") })
public class VisitanteBean extends MetodosDePaginaBean {

	private Boolean usuarioEstaCadastrando = false;

	private Boolean usuarioPossuiLeitorBiometricoAtivo = false;

	private Boolean visitanteMenorQueSete = false;

	public static final String mappingRecuperando = "CadastrarCredenciamento";
	public static final String mappingPessoa = "AlterarPessoaVisitante";
	private static final String FILHOS = "Filho(a)";

	@URLQueryParameter("id")
	private String idVisitante;

	private FiltroVwVisitante filtro = new FiltroVwVisitante();

	@SuppressWarnings("unused") // usado em CredenciamentosVisitante.xhtml
	private FiltroFaseDoCredenciamento filtroFaseDoCredenciamento;

	private Visitante visitante;

	private List<Visitante> visitantesResponsaveis = new ArrayList<>();


	private LazyDataModel<VwVisitante> lazyDataModel;

	@Inject
	private VisitanteService service;

	@Inject
	private VisitanteResponsavelService visitanteResponsavelService;

	@Inject
	private VwVisitanteService vwVisitanteService;

	@Inject
	private RecuperandoService recuperandoService;

	@Inject
	private SolicitacaoVisitaService solicitacaoVisitaService;

	@Inject
	private UnidadePrisionalService unidadeService;

	@Inject
	private BiometriaService bioService;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private ComboService comboService;

	@Inject
	private FaseDoCredenciamentoService faseDoCredenciamentoService;

	@Inject
	private SecurityUtil securityUtil;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	@Inject
	private ParenteDAO parenteDAO;

	private List<Credenciamento> credenciamentos;
	private Recuperando recuperandoSelecionado;
	private Credenciamento credenciamentoAtual;
	private TipoVisita tipoVisita;
	private UnidadePrisional unidadePrisional;
	private String profissao;

	private String loginUsuarioAutorizador;
	private String senhaUsuarioAutorizador;
	private String observacaoUsuarioAutorizador;
	private List<String> documentosDescricao = new ArrayList<String>();
	private List<String> certidoesDescricao = new ArrayList<String>();

	private Boolean cadastroTipoVisita = false;
	private Boolean cadastroDadosVisitante = false;

	private Estado estado;
	private List<Cidade> cidades;

	private List<UnidadePrisional> unidades;
	private List<TipoVisita> tipoVisitas;
	private Optional<Recuperando> recuperando;
	private List<Recuperando> exRecuperandos;
	private Recuperando exRecuperando;

	@Inject
	private PassagemDAO passagemDAO;

	private SortOrder sentidoOrdenacao;
	private String campoOrdenacao;

	@Inject
	private CredenciamentoService credenciamentoService;
	private Visitante filtroVisitante;
	private List<Recuperando> filtroRecuperando = new ArrayList<Recuperando>();

	@Inject
	private PessoaService pessoaService;

	private VwVisitante visitanteSelecionado;

	private Visitante visitanteDetalhe;

	private Boolean tornarVisitante = false;
	private Boolean ehNecessarioValidarBiometria1pra1 = false;

	@Inject
	private BiometriaSDKDao biometriaSDKDao;

	private List<CredencialDocumento> documentosAprovados;
	private List<CredencialCertidao> certidoesAprovadas;

	@Inject
	TipoVisitaService tipoVisitaService;

	@Inject
	CredenciamentoDAO credenciamentoDAO;

	private boolean salvarFasesAuto = false;
	private boolean alterarDados = false;
	private boolean clickVoltar = false;

	@Inject
	private ContatoService contatoService;

	private boolean mostrarPanelVisitante = false;

	private boolean mostrarPanelResponsavel = false;

	private boolean alterarDadosTela = false;

	private FaseCredenciamento[] fasesCredenciamento;

	private String tabIndex = "0";

	public Boolean isMenorQueSete() {
		return visitanteMenorQueSete;
	}

	public void setIsMenorQueSete(Date dataNascimento) {
		this.visitanteMenorQueSete = ModelUtil.isUnderSeven(dataNascimento);
	}

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			FiltroVwVisitante filtroDash = (FiltroVwVisitante) facesUtil.getFlashParameter("filtroVisitanteDash");

			if (filtroDash != null) {
				setFiltro(filtroDash);
			} else {
				super.carregarFiltroPaginacao(FiltroVwVisitante.class, this);
			}
		}
	}

	@URLAction(onPostback = false)
	public String init() {
		this.carregarFasesCredenciamento();
		this.salvarFasesAuto = false;
		this.alterarDados = false;
		this.clickVoltar = false;
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.iniciarDadosCadastrar();
				break;
			case CREDENCIAR:
			case ALTERAR:
				if (PrettyContext.getCurrentInstance().getCurrentMapping().getId().equals(mappingRecuperando)) {
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					if (this.getVisitantePreso(id)) {
						facesUtil.exibirMensagemErro("Visitante possui um cadastro de recuperando ativo.", true);
						return "pretty:ListarVisitante";
					}

					super.setTituloPagina("Novo Credenciamento");
					this.salvarFasesAuto = false;
				}

				this.alterarDadosTela = true;

			case VISUALIZAR:
				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
				if (!buscarVisitante(id)) {
					facesUtil.exibirMensagemErro("Visitante não encontrado.", true);
					return "pretty:ListarVisitante";
				}

				if (!bloqueiaAlterarVisitante(this.visitante.getStatus())) {
					facesUtil.exibirMensagemErro("Operação não permitida.", true);
					return "pretty:ListarVisitante";
				}

				this.iniciarDadosAlterar();
				break;
			case TAB:
				long idConsulta = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
				if (!buscarVisitante(idConsulta)) {
					facesUtil.exibirMensagemErro("Visitante não encontrado.", true);
					return "pretty:ListarVisitante";
				}

				if (!bloqueiaAlterarVisitante(this.visitante.getStatus())) {
					facesUtil.exibirMensagemErro("Operação não permitida.", true);
					return "pretty:ListarVisitante";
				}

				String tab = facesUtil.getQueryParameter("tab");
				if (!tab.isEmpty()) {
					this.tabIndex = tab;
				}

				this.iniciarDadosAlterar();
				break;

			case LISTAR:
				this.pesquisar();
				super.logDeAcesso(log, "Visitantes");
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

	private void iniciarDadosAlterar() {
		this.cadastroTipoVisita = true;
		this.credenciamentos = new ArrayList<>();
		if (this.visitante.getPessoa().getNaturalidade() != null) {
			this.estado = this.visitante.getPessoa().getNaturalidade().getEstado();
			this.listarCidades();
		}
		if (PrettyContext.getCurrentInstance().getCurrentMapping().getId().equals(mappingPessoa)) {
			this.cadastroTipoVisita = false;
			this.cadastroDadosVisitante = true;
			this.alterarDados = true;
		}

		if (!this.visitantesResponsaveis.isEmpty()) {
			this.mostrarPanelVisitante = false;
			this.mostrarPanelResponsavel = true;
		} else {
			this.mostrarPanelResponsavel = false;
			this.mostrarPanelVisitante = true;
		}

		if (!this.visitantesResponsaveis.isEmpty()) {
			this.mostrarPanelVisitante = false;
			this.mostrarPanelResponsavel = true;
		} else {

			this.mostrarPanelResponsavel = false;
			this.mostrarPanelVisitante = true;
		}
		
		logDeAcesso(log, this.visitante);
	}

	private void limparVisitante() {
		this.visitante = new Visitante();
		this.visitante.setPessoa(new Pessoa());
		this.visitante.getPessoa().setDocumentos(new ArrayList<>());
		this.visitante.getPessoa().setEnderecos(new ArrayList<>());
		this.visitante.getPessoa().setContatos(new ArrayList<>());
		this.visitante.setCertidoes(new ArrayList<>());
	}

	private void iniciarDadosCadastrar() {
		VwVisitante visit = (VwVisitante) facesUtil.getObjetoSessao("tornarVisitante");
		this.salvarFasesAuto = false;
		this.cadastroTipoVisita = true;
		this.cadastroDadosVisitante = false;
		this.setTornarVisitante(false);
		this.credenciamentos = new ArrayList<>();
		if (visit == null) {
			limparVisitante();
		} else {

			this.visitante = this.buscarRecuperandoVisitante(visit.getPessoa());

			this.estado = this.visitante.getPessoa().getNaturalidade().getEstado();
			this.listarCidades();
			this.setTornarVisitante(true);

			if (this.visitante.getPessoa().getBiometria() != null
					&& this.visitante.getPessoa().getBiometria().getDedos() != null) {
				this.ehNecessarioValidarBiometria1pra1 = (this.visitante.getPessoa().getBiometria().getDedos()
						.size() > 0);
				this.cadastroTipoVisita = !this.ehNecessarioValidarBiometria1pra1;
			}
			this.ehNecessarioValidarBiometria1pra1 = true;
			this.cadastroTipoVisita = !this.ehNecessarioValidarBiometria1pra1;

		}

		this.listarUnidades();
		this.listarTipoVisitas();
	}

	private boolean buscarVisitante(long id) {
		if (id <= 0) {
			return false;
		}

		this.visitante = service.buscarVisitanteDadosPessoa(id);
		this.visitante.setCredenciamentos(this.credenciamentoService.buscarCredenciamentoComFasesPorVisitanteId(id));

		List<VisitanteResponsavel> listaResponsaveis = visitanteResponsavelService
				.buscarListaVisitantesReponsaveis(this.visitante);

		if (!listaResponsaveis.isEmpty()) {
			for (VisitanteResponsavel vr : listaResponsaveis) {
				Visitante v = new Visitante();
				v = service.buscarVisitanteDadosPessoa(vr.getVisitanteResponsavel().getId());
				this.visitantesResponsaveis.add(v);
			}

			this.visitante.setVisitantesReponsaveis(this.visitantesResponsaveis);
		}
		
		if(this.visitante != null)this.setIsMenorQueSete(this.visitante.getPessoa().getDataNascimento());

		for (Credenciamento credenciamento : visitante.getCredenciamentos()) {
			if (credenciamento.getParente() != null){
				credenciamento.setParente(parenteDAO.buscarParenteVisita(credenciamento.getId()));
			}
		}

		return this.visitante != null;
	}

	public String salvar(Boolean redirecionarAlterar) {
		try {
			if (!redirecionarAlterar && this.cadastrarBiometria()) {
				return null;
			}

			this.visitante.setVisitantesReponsaveis(visitantesResponsaveis);

			this.visitante = this.service.incluir(this.visitante, this.recuperandoSelecionado, this.salvarFasesAuto,
					this.tornarVisitante);

			bioService.salvarBiometriaNServer(this.visitante.getPessoa());

			this.salvarFasesAuto = false;

			if (redirecionarAlterar && !this.alterarDados) {
				this.service.atribuirStatusVisitantePosCredenciamento(this.visitante);

			}
			Credenciamento novo = this.visitante.getCadastro();
			this.visitante = this.service.removeDocumentosDuplicados(this.visitante);
			this.visitante.setCadastro(novo);
			LoggerUtil.logInfo(Acao.SALVAR, this.visitante, log);

			if (redirecionarAlterar) {
				this.redirectCredenciamento();
				return null;
			}

			if (PrettyContext.getCurrentInstance().getCurrentMapping().getId().equals("AlterarPessoaVisitante")
					|| PrettyContext.getCurrentInstance().getCurrentMapping().getId().equals("CadastrarVisitante")) {
				return null;
			}

			facesUtil.exibirMensagemInfo("Visitante " + this.visitante.getPessoa().getNome() + " salvo com sucesso!",
					true);
			return "pretty:ListarVisitante";
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Erro",
					"Erro ao salvar Visitante (" + this.visitante.getPessoa().getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.visitante, e, log);
		}

		return "";
	}

	private void redirecionarAlterarPessoa() {
		try {
			String atualUrl = PrettyContext.getCurrentInstance().getContextPath()
					+ PrettyContext.getCurrentInstance().getRequestURL().toString();
			FacesContext.getCurrentInstance().getExternalContext().redirect(atualUrl.replace("/pessoa", ""));
		} catch (IOException ie) {
			LoggerUtil.logErro(Acao.SALVAR, this.visitante, ie, log);
		}
	}

	public void excluir(VwVisitante visitante) {
		try {
			boolean podeExcluir = true;
			Visitante visitanteExcluir = this.service.buscarPorId(visitante.getId());
			for (Credenciamento vr : visitanteExcluir.getCredenciamentos()) {
				if (vr.getFasesDoCredenciamento().size() > 0)
					podeExcluir = false;
			}
			if (podeExcluir) {
				this.service.excluir(visitanteExcluir);
				facesUtil.exibirMensagemInfo("Visitante " + visitante.getNome() + " excluído com sucesso!", false);
				LoggerUtil.logInfo(Acao.EXCLUIR, visitanteExcluir, log);
			} else
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
						"Visitante com credenciamento ativo! Não é possível excluir!");

		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
		} catch (Exception e) {
			if (e.getCause() != null && e.getCause().getCause() != null
					&& e.getCause().getCause() instanceof ConstraintViolationException) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
						"Visitante com credenciamento ativo! Não é possível excluir!");
				return;
			}
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Erro",
					"Erro ao excluir visitante (" + visitante.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, visitante, e, log);
		}
	}

	public void excluirCredenciamento(Credenciamento credenciamento) {
		for (FaseDoCredenciamento vc : credenciamento.getFasesDoCredenciamento()) {
			if (vc.getStatusCredenciamento() != StatusCredenciamento.PENDENTE) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
						"Não é permitido excluir credenciamentos já iniciados.");
				return;
			}
		}
		try {
			this.service.excluirCredenciamentoDoVisitante(credenciamento);

			this.visitante.getCredenciamentos().remove(credenciamento);

			this.service.atribuirStatusVisitantePosCredenciamento(visitante);

			facesUtil.exibirMensagemInfo("Credenciamento excluído com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, credenciamento, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Erro",
					"Erro ao excluir Credenciamento" + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, credenciamento, e, log);
		}
	}

	public void imprimir(VwVisitante vwVisitante) {
		this.setFiltroVisitante(this.service.buscarPorId(vwVisitante.getId()));
		facesUtil.setObjetoSessao("filtroVisitante", this.getFiltroVisitante());

		this.credenciamentos = credenciamentoService.listarCredenciamentoPorVisitante(this.getFiltroVisitante());
		for (Credenciamento vr : this.credenciamentos) {
			this.getFiltroRecuperando().add(vr.getRecuperando());
		}

		facesUtil.exibirPaginaDialog("pretty:FichaVisitante");
	}
	
	public void imprimirCredenciamentos() {
        try {
            this.imprimirRelatorioCredenciamento(this.visitante);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de Credenciamentos: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Credenciamentos", e, log);
        }    
     }
    

	public void imprimir() {
		try {
			FiltroVwVisitanteRelatorio f = new FiltroVwVisitanteRelatorio();

			if (this.campoOrdenacao.contains("nome")) {
				f.setPropriedadeOrdenacao("pessoa");

				if (Order.asc("pessoa").equals(this.filtro.getOrdenacao())) {
					f.setOrdenacao(Order.asc("pessoa"));
				} else {
					f.setOrdenacao(Order.desc("pessoa"));
				}
			}

			f.setNome(this.filtro.getNome());
			f.setNomeMae(this.filtro.getNomeMae());
			f.setCpf(this.filtro.getCpf());
			f.setRecuperando(this.filtro.getRecuperando());
			f.setDataNascimento(this.filtro.getDataNascimento());
			f.setStatus(this.filtro.getStatus());
			f.setUnidadePrisional(this.filtro.getUnidadePrisional());
			f.setFaseCredenciamento(this.filtro.getFaseCredenciamento());
			f.setSituacaoCredenciamento(this.filtro.getSituacaoCredenciamento());
			f.setTipoVisita(this.filtro.getTipoVisita());
			f.setResponsavel(this.filtro.getResponsavel());
			f.setPessoasIds(this.filtro.getPessoasIds());
			f.setIsPesquisaFonetica(this.filtro.getIsPesquisaFonetica());

			Set<Visitante> lista = new LinkedHashSet<>(service.listarRelatorio(f, true));

			List<Visitante> dataSource = new ArrayList<>(lista);

			if (this.campoOrdenacao.contains("nome")) {
				this.campoOrdenacao = "pessoa";
				Collections.sort(dataSource, new PessoaSorter(this.campoOrdenacao, this.sentidoOrdenacao));
			}

			this.imprimirRelatorio(dataSource);
			this.pesquisar();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro imprimir lista de Visitantes: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Visitantes", e, log);
		}
	}

	private void imprimirRelatorio(List<Visitante> lista) {
		try {
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
						"Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			Map<String, Object> params = new HashMap<>();
			params.put("SUBREPORT_DIR", RelatorioUtil.getSubReportPath(Relatorio.ListaVisitantes));

			lista.forEach(v -> v.getCredenciamentos().forEach(vr -> {
				if (vr.getRecuperando() == null)
					vr.setRecuperando(new Recuperando(new Pessoa()));
			}));

			if (lista.size() == 1) {
				Relatorio.ListarVisitante.setNome("RELATÓRIO DE VISITANTES");
			}
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarVisitante, params));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Visitantes", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro imprimir lista de Visitantes: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Visitantes", e, log);
		}
	}
	
	private void imprimirRelatorioCredenciamento(Visitante visitante) {
        try {
            if (visitante.getCredenciamentos().size() == 0) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
                return;
            }
            Map<String, Object> params = new HashMap<>();
			params.put("SUBREPORT_DIR", RelatorioUtil.getSubReportPath(Relatorio.HistoricoCredenciamentos));         
            
            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(visitante, Relatorio.HistoricoCredenciamentos, params));
            RequestContext.getCurrentInstance().execute("download()");
            LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Credenciamentos", log);
        } catch (Exception e) {
        	System.out.println(e.getCause());
        	facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de Credenciamentos: " + e);
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Credenciamentos", e, log);
        }
    }


	public void reativarVisitante(VwVisitante visitante) {
		try {
			service.reativarVisitante(this.service.buscarPorId(visitante.getId()));
			facesUtil.exibirMensagemInfo("Visitante " + visitante.getPessoa().getNome() + " reativado com sucesso!",
					false);
			LoggerUtil.logInfo(Acao.ALTERAR, visitante, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Erro",
					"Erro ao reativar visitante (" + visitante.getPessoa().getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, visitante, e, log);
		}
	}

	public void selecionarTipoVisita() {
		this.credenciamentos.clear();

		if (!this.tornarVisitante && (this.visitante.getId() == null || this.visitante.getId() == 0)) {
			limparVisitante();
		}

		this.credenciamentoAtual = null;
		this.recuperandoSelecionado = null;
	}

	public void selecionarEmancipado() {
		if (this.visitante.getEmancipado() == null || this.visitante.getEmancipado() == SimNao.S
				|| !exibirEmancipado()) {
			this.visitante.setResponsavel(null);
		}
		if (!exibirEmancipado()) {
			this.visitante.setEmancipado(null);
		}
	}

	public Boolean exibirEmancipado() {
		return this.service.exibirEmancipado(this.visitante);
	}

	public void selecionarRecuperando(Parente parente, boolean isRenovacao) {
		if (parente != null && AcaoDeTela.CADASTRAR.equals(this.getAcaoDeTela()) && this.visitante.getId() == null
				&& !this.tornarVisitante) {
			this.visitante.setCredenciamentos(new ArrayList<>());
			this.visitante.getPessoa().setNome(parente.getNome());
			this.visitante.getPessoa().setDataNascimento(parente.getDataNascimento());
		}
		int iValidaConjuge = this.validarConjugeCadastro(parente, this.recuperandoSelecionado, this.visitante);
		if (iValidaConjuge == 1) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
					"Recuperando já possui Conjugê cadastrado.");
			return;
		} else if (iValidaConjuge == 2) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
					"Visitante já é Conjugê de outro Recuperando.");
			return;
		}

		if (this.recuperandoSelecionado == null || this.recuperandoSelecionado.getId() == 0) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Selecionar um recuperando.");
			return;
		}

		
		if (this.visitante.getResponsavel() != null && !verificaVisitanteResponsavelPossuiCredenciamento(this.visitante, this.recuperandoSelecionado)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ATENÇÃO", "O responsável do visitante não possui credenciamento aprovado para este recuperando.");
			return;
		}

		this.credenciamentos = new ArrayList<>();
		Credenciamento credenciamento = new Credenciamento();
		credenciamento.setRecuperando(this.recuperandoSelecionado);
		credenciamento.setVisitante(this.visitante);
		credenciamento.setTipoVisita(this.tipoVisita);
		credenciamento.setParente(parente);
		credenciamento.setIsRenovacao(isRenovacao);
		if (this.recuperandoSelecionado.getUnidadeId() != null && this.recuperandoSelecionado.getUnidadeId() > 0) {
			credenciamento.setUnidadePrisional(new UnidadePrisional(this.recuperandoSelecionado.getUnidadeId()));
		}
		this.credenciamentoAtual = credenciamento;

		this.credenciamentos.add(credenciamento);
		if (parente != null && this.visitante != null && this.visitante.getPessoa() != null) {
			if (this.visitante.getPessoa().getNome() == null)
				this.visitante.getPessoa().setNome(parente.getNome());
			if (this.visitante.getPessoa().getDataNascimento() == null && parente.getDataNascimento() != null)
				this.visitante.getPessoa().setDataNascimento(parente.getDataNascimento());
		}
		RequestContext.getCurrentInstance().execute("PF('selecionarParentesco').hide()");
	}

	public void associarRecuperando(SelectEvent event) {
		RecuperandoView r = (RecuperandoView) event.getObject();
		if (r == null) {
			return;
		}

		try {
			this.recuperandoSelecionado = recuperandoService.buscarRecuperandoPorId(r.getId(), false);
			this.recuperandoSelecionado.setUnidadeAtual(r.getNomeUnidade());
			this.recuperandoSelecionado.setUnidadeId(r.getUnidadeId());
			if (recuperandoSelecionado == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Recuperando não encontrado.");
				return;
			}

			if (this.tipoVisita == null || this.tipoVisita.getTipoEntrada() == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Selecionar o Tipo de Visita.");
				return;
			}


			if (TipoEntrada.FAMILIA.equals(this.tipoVisita.getTipoEntrada())) {
				List<Parente> parentes = this.recuperandoSelecionado.getParentes();
				service.buscarRecuperandosDoVisitante(this.visitante);
				List<Credenciamento> credenciamentosDoVisitante = this.visitante.getCredenciamentos();


				System.out.print("Visitante getStatus " + this.visitante.getStatus());

				Optional<Credenciamento> optCredenciamento = credenciamentosDoVisitante.stream()
						.filter(re -> re.getRecuperando() != null
								&& re.getRecuperando().getId() == this.recuperandoSelecionado.getId())
						.findFirst();

				if (!optCredenciamento.isPresent()) {
					RequestContext.getCurrentInstance().execute("PF('selecionarParentesco').show()");
				} else {
					Optional<Credenciamento> optCredenciamentoMesmaUnidade = credenciamentosDoVisitante.stream()
							.filter(cred -> cred.getRecuperando() != null
									&& cred.getRecuperando().getId() == this.recuperandoSelecionado.getId()
									&& cred.getUnidadePrisional().getId() == SecurityUtil.getUnidadeSelecionada()
											.getId()
									&& cred.getStatusCredenciamento() == StatusCredenciamento.APROVADO.getId())
							.findFirst();

					boolean isRenovacao = optCredenciamentoMesmaUnidade.isPresent();
					Credenciamento credenciamento = isRenovacao ? optCredenciamentoMesmaUnidade.get()
							: optCredenciamento.get();
					Optional<Parente> optParent = parentes.stream()
							.filter(p -> p.getId() == credenciamento.getParente().getId()).findFirst();

					if (!ModelUtil.isMenorDeIdade(this.visitante.getPessoa().getDataNascimento())
							&& credenciamento.getParente().getStatus() == SgpStatusAlternativo.P) {

						facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
								"O visitante não está ATIVO no Rol de parentes no recuperando selecionado");
						return;
					}

					if (optParent.isPresent()) {
						selecionarRecuperando(optParent.get(), isRenovacao);
					} else {
						RequestContext.getCurrentInstance().execute("PF('selecionarParentesco').show()");
					}
				}

				return;
			}

			this.selecionarRecuperando(null, false);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao associar recuperando ao visitante: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, r, e, log);
		}
	}

	public void abrirDialogRecuperando(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarRecuperando");
	}

	private void pesquisarBiometria(List<Long> ids) {
		try {
			this.setFiltro(new FiltroVwVisitante());
			this.filtro.setPessoasIds(ids);
			RequestContext.getCurrentInstance().update("registrosForm");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao pesquisar por biometria: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	private void exibeConfirmacaoCadastro() {
		RequestContext.getCurrentInstance().execute("PF('confirmaCadastro').show()");
	}

	public void proximoCadastroTipoVisita() {
		this.tipoVisita = tipoVisitaService.buscarPorId(tipoVisita.getId());
		this.visitante.setTipoVisita(this.tipoVisita);
		this.setIsMenorQueSete(this.visitante.getPessoa().getDataNascimento());
		for (Credenciamento x : this.visitante.getCredenciamentos()) {
			if (x.getId() != null
					&& x.getTipoVisita().getTipoEntrada().getId().equals(this.tipoVisita.getTipoEntrada().getId())
					&& x.getRecuperando() != null && this.recuperandoSelecionado != null
					&& x.getRecuperando().getId() == this.recuperandoSelecionado.getId()
					&& !StatusCredenciamento.INATIVO.getId().equals(x.getStatusCredenciamento())
					&& !StatusCredenciamento.NAO_APROVADO.getId().equals(x.getStatusCredenciamento())) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
						"Visitante já credenciado ao recuperando selecionado!");
				return;
			}
		}

		for (Credenciamento vr : this.credenciamentos) {
			if (vr.getId() == null)
				this.visitante.getCredenciamentos().add(vr);
			break;

		}

		this.cadastroTipoVisita = false;
		if (this.visitante.getPessoa().getId() > 0) {
			try {
				this.visitante.setStatus(StatusVisitante.C);

				this.visitante.setTipoVisita(this.tipoVisita);

				this.cadastroDadosVisitante = true;
				Panel panelTornarVisitante = (Panel) FacesContext.getCurrentInstance().getViewRoot()
						.findComponent("formTornarVisitante:panelTornarVisitante");
				if (panelTornarVisitante != null) {
					panelTornarVisitante.setVisible(false);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			this.cadastroDadosVisitante = true;

		if (this.credenciamentoAtual == null && this.credenciamentos.isEmpty()) {
			this.credenciamentoAtual = new Credenciamento();
			this.credenciamentoAtual.setUnidadePrisional(this.unidadePrisional);
			this.credenciamentoAtual.setTipoVisita(this.tipoVisita);
			this.credenciamentoAtual.setVisitante(this.visitante);
			this.credenciamentoAtual.setProfissao(this.profissao);
			this.visitante.getCredenciamentos().add(this.credenciamentoAtual);
		}

		this.carregarDadosSolicitacaoVisita();
	}

	@Deprecated
	private void carregarDadosSolicitacaoVisita() {
		if (this.credenciamentoAtual == null || this.credenciamentoAtual.getParente() == null
				|| this.credenciamentoAtual.getRecuperando() == null) {
			return;
		}
		if (this.visitante.getPessoa() != null && this.visitante.getPessoa().getId() > 0) {
			return;
		}

		SolicitacaoVisita solicitacaoVisita = this.solicitacaoVisitaService
				.buscarUltimaSolicitacaoAtiva(this.credenciamentoAtual);
		if (solicitacaoVisita == null) {
			return;
		}

		this.visitante.setPessoa(solicitacaoVisita.getPessoa());

		if (this.visitante.getPessoa().getNomeMae() != null && this.visitante.getPessoa().getNomeMae().equals("ND")) {
			this.visitante.getPessoa().setNomeMae("");
		}

		if (!this.visitante.getPessoa().getDocumentos().isEmpty()) {
			for (Documento documento : this.visitante.getPessoa().getDocumentos()) {
				this.incluirDocumento(documento);
			}
		}
	}

	public void voltarCadastroTipoVisita() {
		this.cadastroTipoVisita = true;
		this.cadastroDadosVisitante = false;
		this.visitante.getCredenciamentos().remove(this.credenciamentoAtual);
	}

	public void setUsuarioPossuiLeitorBiometricoAtivo() {
		Map<String, String> map = facesUtil.getParameters();
		this.usuarioPossuiLeitorBiometricoAtivo = Boolean.parseBoolean(map.get("status"));
	}

	public void retornoOperacaoBiometrica() {
		switch (BiometriaRetorno.getOperacao(facesUtil)) {
		case Pesquisar:
			pesquisarBiometria(BiometriaRetorno.getValor());
			break;
		case Registrar:
			registrarBiometria(BiometriaRetorno.getValor());
			break;
		case Capturar:
			validarBiometria(BiometriaRetorno.getValor());
			break;
		default:
			break;
		}
	}

	private Boolean cadastrarBiometria() {
		if (!verificarSeBiometriaEhObrigatoria() || this.visitante.getAutorizacaoCadastroSemBiometria() != null) {
			return false;
		}
		if (this.usuarioPossuiLeitorBiometricoAtivo && this.visitante.getPessoa().getBiometria() == null) {
			RequestContext.getCurrentInstance().execute(
					"enviaComandoParaApp({acao: 'registrar', idPessoa: " + this.visitante.getPessoa().getId() + "})");
			return true;
		}
		if (!this.usuarioPossuiLeitorBiometricoAtivo && this.visitante.getAutorizacaoCadastroSemBiometria() == null
				&& this.visitante.getPessoa().getBiometria() == null && !this.isMenorQueSete()) {
			RequestContext.getCurrentInstance().execute("PF('confirmaCadBio').show()");
			return true;
		}

		return false;
	}

	private void registrarBiometria(Long idBiometriaTemp) {
		try {
			if (this.getIsVisualizacao()) {
				return;
			}

			if (idBiometriaTemp == null) {
				throw new Exception("Biometria inválida.");
			}

			Biometria bio = bioService.buscarBiometriaConvertida(idBiometriaTemp);
			bioService.vincularBiometriaAPessoa(bio, this.visitante.getPessoa());

			if (this.visitante != null && this.visitante.getPessoa() != null
					&& this.visitante.getPessoa().getBiometria() != null && this.visitante.getPessoa().getId() > 0) {
				bioService.salvarBiometriaNServer(this.visitante.getPessoa());
				this.pessoaService.salvarPessoaVisitante(this.visitante.getPessoa());

				if (this.visitante.getId() != null)
					buscarVisitante(this.visitante.getId());
			}

			facesUtil.exibirMensagemInfo("Biometria capturada!", false);
			RequestContext.getCurrentInstance().update("mensagens");
			RequestContext.getCurrentInstance().update("tabView:biometriaCapturada");
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATENÇÃO", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao capturar biometria: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public String autorizarCadastroSemBiometria() {
		try {
			AuthenticationInfo authenticationInfo = SecurityUtils.getSecurityManager().authenticate(
					new UsernamePasswordToken(this.loginUsuarioAutorizador, this.senhaUsuarioAutorizador));
			SecurityUtils.getSecurityManager().checkPermission(authenticationInfo.getPrincipals(),
					"AutorizarCadastroSemBiometria");
			Usuario usuario = usuarioService.buscarUsuarioPorLogin(this.loginUsuarioAutorizador);

			AutorizacaoCadastroSemBiometria autCad = new AutorizacaoCadastroSemBiometria();
			autCad.setObservacao(this.observacaoUsuarioAutorizador);
			autCad.setUsuario(usuario);
			autCad.setDataAutorizacao(Sigepen.getDataAtual());
			autCad.setVisitante(this.visitante);

			this.visitante.setAutorizacaoCadastroSemBiometria(autCad);

			RequestContext.getCurrentInstance().execute("PF('dlgAutBio').hide()");

			return salvar(false);

		} catch (UnauthorizedException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Usuário informado não possui permissão para autorizar cadastro.");
		} catch (UnknownAccountException | IncorrectCredentialsException e) {
			if (this.isMenorQueSete()) {
				return null;
			}
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Usuário ou senha inválidos.");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao autorizar cadastro sem biometria.");
			LoggerUtil.logErro(e, log);
		}

		return null;
	}

	public String onFlowProcess(FlowEvent event) {
		this.definirValorBotaoProximo(event);

		if (event.getOldStep().equals("tabDocumentos") && event.getNewStep().equals("tabDados")) {
			if (this.visitante.getId() != null)
				this.visitante = this.service.buscarPorId(this.visitante.getId());
			// this.clickVoltar = true;
		}
		if (event.getOldStep().equals("tabDados") && event.getNewStep().isEmpty()) {
			if (PrettyContext.getCurrentInstance().getCurrentMapping().getId().equals(mappingPessoa)) {
				redirecionarAlterarPessoa();
				return event.getOldStep();
			}
			this.voltarCadastroTipoVisita();
			RequestContext.getCurrentInstance().update("panelTipoVisita");
			return event.getOldStep();
		}

		if (event.getOldStep().equals("tabDados") && event.getNewStep().equals("tabDocumentos")) {
			if (this.tornarVisitante)
				this.salvar(false);
			if (this.alterarDados || this.clickVoltar)
				this.salvarDados(this.visitante);
			if (this.visitante.getPessoa().getMenorIdade() == false)
				this.visitante.setEmancipado(null);
			if (this.visitante.getEmancipado() == SimNao.N && this.visitante.getResponsavel() == null
					&& this.visitantesResponsaveis.isEmpty()) {
				confirmResponsavelSave();
				return event.getOldStep();
			}
			if (!this.visitante.possuiFotoFrenteAtual()) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATENÇÃO",
						"É necessário associar uma foto de frente atual do visitante.");
				return event.getOldStep();
			}
			if (this.cadastrarBiometria()) {
				return event.getOldStep();
			}
			if (!this.tornarVisitante) {
				// Gambiarra criada para redirecionar ao credenciamento correto após salvar, não
				// alterar
				List<Long> idsCredenciamentos = this.visitante.getCredenciamentos().stream()
						.filter(c -> c.getId() != null).map(c -> c.getId()).collect(Collectors.toList());

				salvar(false);

				// Gambiarra criada para redirecionar ao credenciamento correto após salvar, não
				// alterar
				Optional<Credenciamento> v = this.visitante.getCredenciamentos().stream()
						.filter(e -> !idsCredenciamentos.contains(e.getId())).findAny();

				if (v.isPresent()) {
					this.credenciamentoAtual.setId(v.get().getId());
				}
			}
		}

		if (event.getOldStep().equals("tabDocumentos") && event.getNewStep().equals("tabEnderecos")) {
			try {
				if (this.visitante != null && this.visitante.getPessoa() != null
						&& this.visitante.getPessoa().getId() == 0)
					this.salvar(false);
				this.service.salvarDocumentos(this.visitante);
			} catch (ValidacaoException e) {
				e.printStackTrace();
			}

			try {
				this.documentosDescricao = new ArrayList<String>();

				if (PrettyContext.getCurrentInstance().getCurrentMapping().getId()
						.equals(VisitanteBean.mappingRecuperando)
						|| PrettyContext.getCurrentInstance().getCurrentMapping().getId()
								.equals("CadastrarVisitante")) {
					if (service.validarDocumentosObrigatorios(this.documentosDescricao, this.tipoVisita,
							this.visitante)) {
						RequestContext.getCurrentInstance().execute("PF('confirmObrigatorioDocumento').show();");
						RequestContext.getCurrentInstance().update("confirmObrigatorioDocumento");

						return event.getOldStep();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return event.getOldStep();
			}

		}

		if (event.getOldStep().equals("tabEnderecos") && event.getNewStep().equals("tabContatos")) {
			try {
				this.service.salvarEnderecos(this.visitante);
			} catch (ValidacaoException e) {
				e.printStackTrace();
			}
			if (this.visitante.getPessoa().getEnderecos().isEmpty()) {
				confirmAoMenosUmEnderecoObrigatorio();
				return event.getOldStep();
			}
		}

		if (event.getOldStep().equals("tabContatos") && event.getNewStep().equals("tabCertidoes")) {
			try {
				this.service.salvarContatos(this.visitante);
			} catch (ValidacaoException e) {
				e.printStackTrace();
			}
		}

		if (event.getOldStep().equals("tabCertidoes") && event.getNewStep().isEmpty()) {
			try {
				this.service.salvarCertidoes(this.visitante);
			} catch (ValidacaoException e) {
				e.printStackTrace();
			}

			try {
				this.certidoesDescricao = new ArrayList<String>();

				if (PrettyContext.getCurrentInstance().getCurrentMapping().getId()
						.equals(VisitanteBean.mappingRecuperando)
						|| PrettyContext.getCurrentInstance().getCurrentMapping().getId()
								.equals("CadastrarVisitante")) {
					if (service.validarCertidoesObrigatorias(this.certidoesDescricao, this.tipoVisita,
							this.visitante)) {
						RequestContext.getCurrentInstance().execute("PF('confirmObrigatorioCertidao').show();");
						RequestContext.getCurrentInstance().update("confirmObrigatorioCertidao");

						return event.getOldStep();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return event.getOldStep();
			}
			this.salvarFasesAuto = !this.alterarDados;
			this.salvar(true);

			return event.getOldStep();
		}

		return event.getNewStep();
	}

	private void definirValorBotaoProximo(FlowEvent event) {
		CommandButton btnNext = (CommandButton) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent("tabView:btnNext");
		if (btnNext == null) {
			return;
		}

		if (event.getNewStep().equals("tabCertidoes")) {
			btnNext.setValue("Salvar");
			btnNext.setIcon("ui-icon-disk");
			RequestContext.getCurrentInstance().update("tabView:panelBotoes");
			return;
		}

		if (!btnNext.getValue().equals("Próximo")) {
			btnNext.setValue("Próximo");
			btnNext.setIcon("fa fa-arrow-right");
			RequestContext.getCurrentInstance().update("tabView:panelBotoes");
		}
	}

	public void novoResponsavelEditar() {
		Object matriculaObjVisitante = this.visitante.getCredenciamentos().get(0).getRecuperando().getId();
		if (matriculaObjVisitante != null) {
			facesUtil.setObjetoSessao("matricula", this.visitante.getCredenciamentos().get(0).getRecuperando().getId());
		}
		facesUtil.exibirPaginaDialog("pretty:AssociarResponsavel");
	}

	public void novoResponsavel() {
		Object matriculaObj = this.credenciamentoAtual.getRecuperando().getId();
		if (matriculaObj != null) {
			facesUtil.setObjetoSessao("matricula", this.credenciamentoAtual.getRecuperando().getId());
		}
		facesUtil.exibirPaginaDialog("pretty:AssociarResponsavel");
	}

	public void novoEndereco() {
		facesUtil.exibirPaginaDialog("pretty:AssociarEndereco");
	}

	public void novoContato() {
		facesUtil.exibirPaginaDialog("pretty:AssociarContato");
	}

	public void novoDocumento() {
		facesUtil.exibirPaginaDialog("pretty:AssociarDocumento");
	}

	public void novaCertidao() {
		facesUtil.exibirPaginaDialog("pretty:AssociarCertidao", 200, 1300);
	}

	public void abrirDialogFoto(ActionEvent event) {
		facesUtil.exibirPaginaDialog("AssociarFoto", 768, 1024);
	}

	public void incluirResponsavel(SelectEvent event) {
		Visitante visitante = (Visitante) event.getObject();
		if (visitante == null) {
			return;
		}
		visitante = this.service.buscarPorId(visitante.getId());
		if (this.visitante.getId() != null && visitante.getId().equals(this.visitante.getId())) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
					"Não é possível adicionar si próprio como Responsável.");
			return;
		}
		boolean mesmoRecuperando = false;
		for (Credenciamento vr1 : this.visitante.getCredenciamentos()) {
			for (Credenciamento vr2 : visitante.getCredenciamentos()) {
				if (vr1.getRecuperando() != null && vr2.getRecuperando() != null
						&& vr1.getRecuperando().getId() == vr2.getRecuperando().getId()) {
					mesmoRecuperando = true;
				}
			}
		}
		if (!mesmoRecuperando) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
					"O visitante e o responsável devem possuir credenciamento para o mesmo recuperando.");
			return;
		}
		if (this.visitante.getId() == null || !this.visitante.getId().equals(visitante.getId())) {
			this.visitantesResponsaveis.add(visitante);
		}
	}

	public void desativarVinculoVisitanteResponsavel(Visitante visitante, Visitante vr) {
		if (visitante == null) {
			return;
		}

		visitanteResponsavelService.atualizarVisitanteResponsavel(visitante, vr);
		this.getVisitantesResponsaveis().remove(vr);
	}

	public void confirmRemocaoResponsavel() {
		RequestContext.getCurrentInstance().execute("PF('confirmResponsavelDialog').show();");
	}

	public void confirmResponsavelSave() {
		RequestContext.getCurrentInstance().execute("PF('confirmResponsavelDialog').show();");
	}

	public void confirmAoMenosUmEnderecoObrigatorio() {
		RequestContext.getCurrentInstance().execute("PF('confirmAoMenosUmEnderecoObrigatorio').show();");
	}

	public void incluirDocumento(SelectEvent event) {
		Documento documento = (Documento) event.getObject();
		if (documento == null) {
			return;
		}
		this.incluirDocumento(documento);
	}

	public void incluirDocumento(Documento documento) {
		documento.setPessoa(this.visitante.getPessoa());
		if (!this.visitante.getPessoa().getDocumentos().contains(documento)) {
			this.visitante.getPessoa().getDocumentos().add(documento);
		}

		if (documento.getId() == 0) {
			VisitanteDocumento visitanteDocumento = new VisitanteDocumento();
			visitanteDocumento.setDocumento(documento);
			visitanteDocumento.setVisitante(this.visitante);
			this.visitante.getVisitanteDocumentos().add(visitanteDocumento);
		}

		try {
			this.visitante = this.service.salvar(visitante);
			if (this.visitante.getResponsavel() != null) {
				buscarVisitante(this.visitante.getId());
			}
		} catch (ValidacaoException e) {
			e.printStackTrace();
		}
	}

	public void alterarDocumento(VisitanteDocumento visitanteDocumento) {
		facesUtil.setObjetoSessao("documento", visitanteDocumento.getDocumento());
		facesUtil.exibirPaginaDialog("pretty:AssociarDocumento");
		LoggerUtil.logInfo(Acao.ALTERAR, visitanteDocumento, log);
	}

	public void excluirDocumento(VisitanteDocumento visitanteDocumento) throws ValidacaoException {
		if (this.service.existeCredencimento(visitanteDocumento)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
					"Não é possível excluir o documento! Credenciamento já iniciado!");
			return;
		}

		this.visitante.getVisitanteDocumentos().remove(visitanteDocumento);
		service.excluirDocumento(visitanteDocumento);
		buscarVisitante(this.visitante.getId());
	}

	public void incluirEndereco(SelectEvent event) {
		Endereco endereco = (Endereco) event.getObject();
		if (endereco == null) {
			return;
		}

		if (!this.visitante.getPessoa().getEnderecos().contains(endereco)) {
			endereco.setPessoa(this.visitante.getPessoa());
			this.visitante.getPessoa().getEnderecos().add(endereco);
		}

		try {
			this.visitante = this.service.salvar(visitante);
			buscarVisitante(this.visitante.getId());

		} catch (ValidacaoException e) {
			e.printStackTrace();
		}
	}

	public void alterarEndereco(Endereco endereco) {
		facesUtil.setObjetoSessao("endereco", endereco);
		facesUtil.exibirPaginaDialog("pretty:AssociarEndereco");
		LoggerUtil.logInfo(Acao.ALTERAR, endereco, log);
	}

	public void excluirEndereco(Endereco endereco) {
		this.visitante.getPessoa().getEnderecos().remove(endereco);
		try {
			this.pessoaService.salvarPessoaVisitante(this.visitante.getPessoa());
			this.service.salvar(this.visitante);
			contatoService.excluirEndereco(endereco);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void incluirContato(SelectEvent event) {
		Contato contato = (Contato) event.getObject();
		if (contato == null) {
			return;
		}

		if (!this.visitante.getPessoa().getContatos().contains(contato)) {
			contato.setPessoa(this.visitante.getPessoa());
			this.visitante.getPessoa().getContatos().add(contato);
		}

		try {
			this.visitante = this.service.salvar(this.visitante);
			buscarVisitante(this.visitante.getId());

		} catch (ValidacaoException e) {
			e.printStackTrace();
		}
	}

	public void alterarContato(Contato contato) {
		facesUtil.setObjetoSessao("contato", contato);
		facesUtil.exibirPaginaDialog("pretty:AssociarContato");
		LoggerUtil.logInfo(Acao.ALTERAR, contato, log);
	}

	public void excluirContato(Contato contato) {
		this.visitante.getPessoa().getContatos().remove(contato);
		try {
			this.pessoaService.salvarPessoaVisitante(this.visitante.getPessoa());
			this.service.salvar(this.visitante);
			contatoService.excluirContato(contato);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void incluirCertidao(SelectEvent event) {
		VisitanteCertidao visitanteCertidao = (VisitanteCertidao) event.getObject();
		if (visitanteCertidao == null) {
			return;
		}

		if (!this.visitante.getCertidoes().contains(visitanteCertidao)) {
			visitanteCertidao.setVisitante(this.visitante);
			this.visitante.getCertidoes().add(visitanteCertidao);
		}

		try {
			this.service.salvar(visitante);
			buscarVisitante(this.visitante.getId());
		} catch (ValidacaoException e) {
			e.printStackTrace();
		}
	}

	public void alterarCertidao(VisitanteCertidao visitanteCertidao) {
		facesUtil.setObjetoSessao("certidao", visitanteCertidao);
		facesUtil.exibirPaginaDialog("pretty:AssociarCertidao");
		LoggerUtil.logInfo(Acao.ALTERAR, visitanteCertidao, log);
	}

	public void excluirCertidao(VisitanteCertidao certidao) throws Exception {
		if (this.service.existeCredencimento(certidao)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
					"Não é possível excluir a certidão! Credenciamento já iniciado!");
			return;
		}

		this.visitante.getCertidoes().remove(certidao);

		try {
			this.pessoaService.salvarPessoaVisitante(this.visitante.getPessoa());
			this.visitante = this.service.salvar(this.visitante);
			contatoService.excluirCertidao(certidao);
			buscarVisitante(this.visitante.getId());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		}
	}

	public void associarFoto(SelectEvent event) {
		Object retorno = event.getObject();
		if (retorno == null) {
			return;
		}

		VisitanteFoto visitanteFoto = new VisitanteFoto();
		visitanteFoto.setVisitante(this.visitante);
		visitanteFoto.setTipo(TipoFoto.FRENTE);
		visitanteFoto.setFotoAtual(SimNao.S);
		visitanteFoto.setImagem((Imagem) retorno);

		this.visitante.adicionarFotoFrente(visitanteFoto);
		LoggerUtil.logInfo(Acao.ADICIONAR, visitanteFoto + " " + this.visitante, log);
	}

	public void configurarFotoAtual(VisitanteFoto foto) {
		try {
			if (foto.getTipo() != TipoFoto.FRENTE) {
				throw new ValidacaoException("É necessário ter pelo menos uma foto de frente atual.");
			}
			for (VisitanteFoto f : this.visitante.getFotos()) {
				f.setFotoAtual(SimNao.N);
			}

			foto.setFotoAtual(SimNao.S);
			LoggerUtil.logInfo(Acao.SALVAR, foto, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(null, this.visitante, e, log);
		}
	}

	public void excluirFoto(VisitanteFoto foto) {
		try {
			if (foto.getFotoAtual() == SimNao.S && foto.getTipo() == TipoFoto.FRENTE) {
				throw new ValidacaoException("Não é permitido excluir foto de frente atual.");
			}

			this.visitante.getFotos().remove(foto);
			LoggerUtil.logInfo(Acao.EXCLUIR, foto, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir foto: " + e.getMessage());
			LoggerUtil.logErro(null, this.visitante, e, log);
		}
	}

	public void alterarNacionalidade() {
		if (!this.pessoaEhEstrangeira()) {
			this.visitante.getPessoa().setPais(null);
			return;
		}
		this.setEstado(null);
		this.visitante.getPessoa().setNaturalidade(null);

	}

	public boolean pessoaEhEstrangeira() {
		if (this.visitante.getPessoa().getNacionalidade() == null) {
			return false;
		}

		// 1 é o ID de Nacionalidade brasileiro nato
		return (this.visitante.getPessoa().getNacionalidade().getId() != 1);
	}

	public void listarCidades() {
		try {
			cidades = comboService.listarCidade(estado);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar cidades: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public void listarUnidades() {
		try {
			this.unidades = this.unidadeService.listarUnidadesComParamVisitanteAtivo(true);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar unidades: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public void listarTipoVisitas() {
		try {
			this.tipoVisitas = this.comboService.getTipoVisitas();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar visitas: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public List<UnidadePrisional> getUnidades() {
		if (this.unidades == null) {
			this.listarUnidades();
		}
		return unidades;
	}

	public List<TipoVisita> getTipoVisitas() {
		if (this.tipoVisitas == null) {
			this.listarTipoVisitas();
		}
		return tipoVisitas;
	}

	public Boolean getDisableDadosVisitante() {
		if ((this.visitante.getId() == null || this.alterarDados || this.clickVoltar)
				&& (securityUtil.possuiPermissao("AlterarPessoaVisitante")
						|| securityUtil.possuiPermissao("CadastrarVisitante"))) {
			return false;
		}
		return true;

	}

	public String getIdVisitante() {
		return idVisitante;
	}

	public void setIdVisitante(String idVisitante) {
		this.idVisitante = idVisitante;
	}

	public List<Visitante> getVisitantesResponsaveis() {
		return visitantesResponsaveis;
	}

	public void setVisitantesResponsaveis(List<Visitante> visitantesResponsaveis) {
		this.visitantesResponsaveis = visitantesResponsaveis;

	}

	public void pesquisar() {
		this.getFiltro().setProjetarDados(true);

		this.lazyDataModel = new LazyDataModel<VwVisitante>() {

			private static final long serialVersionUID = -4946935171862855617L;

			@Override
			public Object getRowKey(VwVisitante object) {
				return object != null ? object.getId() : null;
			}

			@Override
			public VwVisitante getRowData(String rowKey) {
				if (rowKey == null || rowKey.isEmpty()) {
					return null;
				}

				for (VwVisitante rec : lazyDataModel) {
					if (rec.getId().equals(Long.valueOf(rowKey))) {
						return rec;
					}
				}

				return null;
			}

			public int loadQtde() {
				return vwVisitanteService.quantidadeVwVisitante(getFiltro());
			}

			public List<VwVisitante> loadData(int first, int pageSize, String sortField, SortOrder sortOrder) {
				
				return vwVisitanteService.buscarPorFiltroVW(getFiltro(), false, first, pageSize,  sortField,  sortOrder);
			}

			@Override
			public List<VwVisitante> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				int buscarQuantidadeFiltrada = loadQtde();
				setRowCount(buscarQuantidadeFiltrada);
				campoOrdenacao = sortField;
				sentidoOrdenacao = sortOrder;
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}
				exRecuperandos = null;
				List<VwVisitante> visitantes = loadData(first, pageSize,  sortField,  sortOrder);

				if (usuarioEstaCadastrando) {
					checkVisitanteEhRecuperando(filtro.getPessoasIds().get(0));
					if (visitantes.size() == 0 && exRecuperandos == null) {
						exibeConfirmacaoCadastro();
					}
				}

				usuarioEstaCadastrando = false;
				getFiltro().setPessoasIds(new ArrayList<Long>());
				return visitantes;
			}
		};
	}

	public String novoCredenciamento(VwVisitante vwVisitante) {
		if (checkVisitanteEhRecuperando(vwVisitante.getIdPessoa())) {
			RequestContext.getCurrentInstance().update("registrosForm:dataTable:dialogRecuperandosFotos");
			return null;
		}

		facesUtil.setQueryParameter("id", vwVisitante.getId().toString());
		return "pretty:CadastrarCredenciamento";
	}

	private boolean checkVisitanteEhRecuperando(long idPessoa) {
		Optional<Passagem> passagem = this.passagemDAO.buscaPassagemPorIdPessoa(idPessoa);
		if (passagem.isPresent()) {
			if (passagem.get().getDataSaida() != null) {
				if (this.service.isDataSaidaMenorSeisMeses(passagem.get().getDataSaida())) {
					return this.exibirPopUpExRecuperando(idPessoa);
				}
			}

			return false;
		}

		return false;
	}

	private boolean exibirPopUpExRecuperando(long idPessoa) {
		this.recuperando = this.service.buscarExRecuperando(idPessoa);
		if (recuperando.isPresent()) {
			this.exRecuperandos = new ArrayList<>();
			this.exRecuperando = this.recuperando.get();
			this.exRecuperandos.add(this.exRecuperando);

			RequestContext.getCurrentInstance().execute("PF('dialogRecuperandosFotos').show();");
			return true;
		}

		return false;
	}

	public String executarCadastro() {
		return "pretty:CadastrarVisitante";
	}

	public Boolean verificarSeBiometriaEhObrigatoria() {
		return unidadeService.verificarSeBiometriaEhObrigatoria(SecurityUtil.getUnidadeSelecionada().getId());
	}

	public List<String> getDocumentosDescricao() {
		return documentosDescricao;
	}

	public List<String> getCertidoesDescricao() {
		return certidoesDescricao;
	}

	public FiltroVwVisitante getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroVwVisitante filtro) {
		this.filtro = filtro;
	}

	public Visitante getVisitante() {
		return visitante;
	}

	public void setVisitante(Visitante visitante) {
		this.visitante = visitante;
	}

	public LazyDataModel<VwVisitante> getLazyDataModel() {
		return lazyDataModel;
	}

	public void setLazyDataModel(LazyDataModel<VwVisitante> lazyDataModel) {
		this.lazyDataModel = lazyDataModel;
	}

	public List<Credenciamento> getCredenciamentos() {
		return credenciamentos;
	}

	public Recuperando getRecuperandoSelecionado() {
		return recuperandoSelecionado;
	}

	public TipoVisita getTipoVisita() {
		return tipoVisita;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	public void setTipoVisita(TipoVisita tipoVisita) {
		this.tipoVisita = tipoVisita;
	}

	public Boolean getCadastroTipoVisita() {
		return cadastroTipoVisita;
	}

	public Boolean getCadastroDadosVisitante() {
		return cadastroDadosVisitante;
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

	public String getLoginUsuarioAutorizador() {
		return loginUsuarioAutorizador;
	}

	public void setLoginUsuarioAutorizador(String loginUsuarioAutorizador) {
		this.loginUsuarioAutorizador = loginUsuarioAutorizador;
	}

	public String getSenhaUsuarioAutorizador() {
		return senhaUsuarioAutorizador;
	}

	public void setSenhaUsuarioAutorizador(String senhaUsuarioAutorizador) {
		this.senhaUsuarioAutorizador = senhaUsuarioAutorizador;
	}

	public String getObservacaoUsuarioAutorizador() {
		return observacaoUsuarioAutorizador;
	}

	public void setObservacaoUsuarioAutorizador(String observacaoUsuarioAutorizador) {
		this.observacaoUsuarioAutorizador = observacaoUsuarioAutorizador;
	}

	public String getProfissao() {
		return profissao;
	}

	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}

	public Optional<Recuperando> getRecuperando() {
		return recuperando;
	}

	public List<Recuperando> getExRecuperandos() {
		return exRecuperandos;
	}

	public Recuperando getExRecuperando() {
		return exRecuperando;
	}

	public Boolean desabilitaSelecao(Parente parente) {
		if (parente != null && parente.getCredenciamentos() != null && parente.getCredenciamentos().size() > 0) {
			Optional<Credenciamento> recs = parente.getCredenciamentos().stream()
					.filter(x -> !x.getStatusCredenciamento().equals(StatusCredenciamento.INATIVO.getId())
							|| x.getParente().getId() == parente.getId())
					.findAny();

			if (recs.isPresent())
				return true;
		}

		if (parente.getParentesco().getDescricao().equalsIgnoreCase(FILHOS)) {
			Calendar data = Calendar.getInstance();
			data.add(Calendar.YEAR, -18);

			if (parente.getDataNascimento() != null) {
				if (parente.getDataNascimento().after(data.getTime())) {
					return false;
				}
			}
		}

		return parente.getStatus().equals(SgpStatusAlternativo.I) || parente.getStatus().equals(SgpStatusAlternativo.P);
	}

	public FaseDoCredenciamentoService getFaseDoCredenciamentoService() {
		return faseDoCredenciamentoService;
	}

	public void setFaseDoCredenciamentoService(FaseDoCredenciamentoService faseDoCredenciamentoService) {
		this.faseDoCredenciamentoService = faseDoCredenciamentoService;
	}

	public String atribuirProximaFase(Credenciamento recuperando) {
		return FaseCredenciamento.getValue(recuperando.getFaseCredenciamento()).getDescricao();
	}

	public Visitante getFiltroVisitante() {
		return filtroVisitante;
	}

	public void setFiltroVisitante(Visitante filtroVisitante) {
		this.filtroVisitante = filtroVisitante;
	}

	public List<Recuperando> getFiltroRecuperando() {
		return filtroRecuperando;
	}

	public void setFiltroRecuperando(List<Recuperando> filtroRecuperando) {
		this.filtroRecuperando = filtroRecuperando;
	}

	public String tornarVisitante(VwVisitante vwVisitante) throws ValidacaoException {
		Pessoa p = pessoaService.buscarPessoaPorId(vwVisitante.getIdPessoa());
		vwVisitante.setPessoa(p);
		facesUtil.setObjetoSessao("tornarVisitante", vwVisitante);
		return "pretty:CadastrarVisitante";
	}

	public Boolean getTornarVisitante() {
		return tornarVisitante;
	}

	public void setTornarVisitante(Boolean tornarVisitante) {
		this.tornarVisitante = tornarVisitante;
	}

	public String getTabIndex() {
		return tabIndex;
	}
	
	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}

	private Visitante buscarRecuperandoVisitante(Pessoa pessoa) {
		Recuperando r = recuperandoService.buscarPessoaRecuperando(pessoa);
		List<VisitanteFoto> visitanteFotos = new ArrayList<VisitanteFoto>();
		Visitante visitante = new Visitante();
		visitante.setPessoa(pessoa);
		for (Foto f : r.getFotos()) {
			VisitanteFoto vf = new VisitanteFoto();
			vf.setImagem(f.getImagem());
			vf.setTipo(f.getTipo());
			vf.setAtual(f.getIsAtual());
			vf.setVisitante(visitante);
			visitanteFotos.add(vf);
		}
		visitante.setFotos(visitanteFotos);
		return visitante;
	}

	public VwVisitante getVisitanteSelecionado() {
		return visitanteSelecionado;
	}

	public void setVisitanteSelecionado(VwVisitante visitanteSelecionado) {
		this.carregarDetalhesVisitante(visitanteSelecionado);
		this.visitanteSelecionado = visitanteSelecionado;
	}

	private void carregarDetalhesVisitante(VwVisitante visitanteSelecionado) {
		if (visitanteSelecionado != null && visitanteSelecionado.getId() > 0)
			this.setVisitanteDetalhe(service.buscarVisitante(visitanteSelecionado));
	}

	public Visitante getVisitanteDetalhe() {
		return visitanteDetalhe;
	}

	public void setVisitanteDetalhe(Visitante visitanteDetalhe) {
		this.visitanteDetalhe = visitanteDetalhe;
	}

	public String limparSelection() {
		this.setVisitanteSelecionado(null);
		return "";
	}

	public void excluirCredenciamento(Long id) {
		FaseDoCredenciamento faseDoCredeciamento = faseDoCredenciamentoService.buscarCredencialPorId(id);
		FaseDoCredenciamento ultimo = credenciamentoDAO
				.buscarUltimoCredenciamento(faseDoCredeciamento.getCredenciamento());
		if (faseDoCredeciamento.getFaseCredenciamento() == ultimo.getFaseCredenciamento()) {
			this.faseDoCredenciamentoService.excluirCredenciamento(faseDoCredeciamento);
			buscarVisitante(faseDoCredeciamento.getCredenciamento().getVisitante().getId());
			credenciamentoService.atualizarStatusCredenciamento(faseDoCredeciamento);
			this.service.atribuirStatusVisitantePosCredenciamento(this.visitante);
			LoggerUtil.logInfo(Acao.EXCLUIR, faseDoCredeciamento, log);

		} else {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Não é possível excluir!");
		}

	}

	public void validarBiometria(String templateCapturado) {

		try {
			byte[] templateBiometricoCapturado = Base64.getDecoder()
					.decode(new String(templateCapturado).getBytes("UTF-8"));
			byte[] templateBiometricoCadastrado = this.visitante.getPessoa().getBiometria().getTemplate();

			boolean asBiometriasSaoCorrespondentes = biometriaSDKDao.verificarPorTemplate(templateBiometricoCapturado,
					templateBiometricoCadastrado);

			if (asBiometriasSaoCorrespondentes) {
				Panel panelTornarVisitante = (Panel) FacesContext.getCurrentInstance().getViewRoot()
						.findComponent("formValidarBiometria:tipoVisitaBiometria");

				if (panelTornarVisitante != null) {
					panelTornarVisitante.setVisible(false);

					this.cadastroTipoVisita = true;
					this.ehNecessarioValidarBiometria1pra1 = false;

					RequestContext.getCurrentInstance().update("tipoVisitaTornarVisitante");
					RequestContext.getCurrentInstance().update("tipoVisitaBiometria");
					RequestContext.getCurrentInstance().update("panelTornarVisitante");
					RequestContext.getCurrentInstance().update("panelTipoVisita");
				}
			} else {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Biometria inválida!");
			}

		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao validar Biometria do Visitante!");
			LoggerUtil.logErro(e, log);
		}

	}

	public Boolean getEhNecessarioValidarBiometria1pra1() {
		return this.ehNecessarioValidarBiometria1pra1;
	}

	public String buscarStatus(Credenciamento vr) {
		return StatusCredenciamento.getValue(vr.getStatusCredenciamento()).getDescricao();
	}

	private void salvarDados(Visitante _visitante) {
		this.pessoaService.salvarPessoaVisitante(_visitante.getPessoa());
	}

	public void documentosAprovados(FaseDoCredenciamento c) {
		this.documentosAprovados = new ArrayList<CredencialDocumento>();
		this.certidoesAprovadas = new ArrayList<CredencialCertidao>();
		FaseDoCredenciamento credencial = this.faseDoCredenciamentoService.buscarDocumentosCertidoes(c);
		for (CredencialDocumento d : credencial.getCredencialDocumentos()) {
			if (SimNao.S.equals(d.getValidado()))
				this.documentosAprovados.add(d);
		}

		for (CredencialCertidao d : credencial.getCredencialCertidaos()) {
			if (SimNao.S.equals(d.getValidado()))
				this.certidoesAprovadas.add(d);
		}
		RequestContext.getCurrentInstance().execute("PF('dialogDocsAprov').show()");
	}

	public List<CredencialDocumento> getDocumentosAprovados() {
		return documentosAprovados;
	}

	public void setDocumentosAprovados(List<CredencialDocumento> documentosAprovados) {
		this.documentosAprovados = documentosAprovados;
	}

	public List<CredencialCertidao> getCertidoesAprovadas() {
		return certidoesAprovadas;
	}

	public void setCertidoesAprovadas(List<CredencialCertidao> certidoesAprovadas) {
		this.certidoesAprovadas = certidoesAprovadas;
	}

	private int validarConjugeCadastro(Parente parente, Recuperando _recuperando, Visitante _visitante) {
		try {
			if (parente != null && parente.getParentesco() != null && parente.getParentesco().getId() == 5)
				return this.service.validarConjugeCadastro(_recuperando, _visitante);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean saidaSeisMeses(Date dtSaida) {

		if (dtSaida != null) {
			Calendar data = Calendar.getInstance();
			data.add(Calendar.MONTH, -6);
			data.set(Calendar.HOUR_OF_DAY, 00);
			data.set(Calendar.SECOND, 00);
			data.set(Calendar.MINUTE, 00);
			data.set(Calendar.MILLISECOND, 00);

			if (dtSaida.after(data.getTime())) {
				return true;
			}
		}
		return false;
	}

	public String redirectCredenciamento() throws ValidacaoException, Exception {
		if (PrettyContext.getCurrentInstance().getCurrentMapping().getId().equals(VisitanteBean.mappingRecuperando)) {
			if (service.validarDocumentosObrigatorios(this.documentosDescricao, this.tipoVisita, this.visitante)) {
				return "";
			}

			if (service.validarCertidoesObrigatorias(this.certidoesDescricao, this.tipoVisita, this.visitante)) {
				return "";
			}
		}

		String url = Sigepen.getApplicationName() + "/visitante";
		if (securityUtil.possuiPermissao("CadastrarFasesCredenciamento")) {
			if (this.visitante.getCadastro() != null) {
				url = Sigepen.getApplicationName() + "/credenciar/" + this.visitante.getCadastro().getId();
			} else {
				if (this.credenciamentoAtual != null) {
					url = Sigepen.getApplicationName() + "/credenciar/" + this.credenciamentoAtual.getId();
				}
			}
		}
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "";
	}

	public void sincronizar() {
		this.service.sincronizar();
	}

	public Boolean getUsuarioEstaCadastrando() {
		return usuarioEstaCadastrando;
	}

	public void setUsuarioEstaCadastrando(Boolean usuarioEstaCadastrando) {
		this.usuarioEstaCadastrando = usuarioEstaCadastrando;
	}

	public boolean isMostrarPanelVisitante() {
		return mostrarPanelVisitante;
	}

	public void setMostrarPanelVisitante(boolean mostrarPanelVisitante) {
		this.mostrarPanelVisitante = mostrarPanelVisitante;
	}

	public boolean isMostrarPanelResponsavel() {
		return mostrarPanelResponsavel;
	}

	public void setMostrarPanelResponsavel(boolean mostrarPanelResponsavel) {
		this.mostrarPanelResponsavel = mostrarPanelResponsavel;
	}

	public boolean isAlterarDadosTela() {
		return alterarDadosTela;
	}

	public void setAlterarDadosTela(boolean alterarDadosTela) {
		this.alterarDadosTela = alterarDadosTela;
	}

	public FaseCredenciamento[] getFasesCredenciamento() {
		return fasesCredenciamento;
	}

	public void setFasesCredenciamento(FaseCredenciamento[] fasesCredenciamento) {
		this.fasesCredenciamento = fasesCredenciamento;
	}

	private void carregarFasesCredenciamento() {
		fasesCredenciamento = this.service.carregarFasesCredenciamento(SecurityUtil.getUnidadeSelecionada().getId(),
				securityUtil.possuiPermissao("ListarTodosRecuperandos"));
	}

	public void abrirDlgAutorizar() {
		RequestContext.getCurrentInstance().execute("PF('dlgAutBio2').show()");
	}

	public String validarCadastroSemBiometria() {
		try {
			AuthenticationInfo authenticationInfo = SecurityUtils.getSecurityManager().authenticate(
					new UsernamePasswordToken(this.loginUsuarioAutorizador, this.senhaUsuarioAutorizador));
			SecurityUtils.getSecurityManager().checkPermission(authenticationInfo.getPrincipals(),
					"AutorizarCadastroSemBiometria");
			Usuario usuario = usuarioService.buscarUsuarioPorLogin(this.loginUsuarioAutorizador);

			AutorizacaoCadastroSemBiometria autCad = new AutorizacaoCadastroSemBiometria();
			autCad.setObservacao(this.observacaoUsuarioAutorizador);
			autCad.setUsuario(usuario);
			autCad.setDataAutorizacao(Sigepen.getDataAtual());
			autCad.setVisitante(this.visitante);

			this.visitante.setAutorizacaoCadastroSemBiometria(autCad);
			this.cadastroTipoVisita = true;
			this.ehNecessarioValidarBiometria1pra1 = false;

			RequestContext.getCurrentInstance().execute("PF('dlgAutBio2').hide()");
			RequestContext.getCurrentInstance().update("tipoVisitaTornarVisitante");
			RequestContext.getCurrentInstance().update("tipoVisitaBiometria");
			RequestContext.getCurrentInstance().update("panelTornarVisitante");
			RequestContext.getCurrentInstance().update("panelTipoVisita");

		} catch (UnauthorizedException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Usuário informado não possui permissão para autorizar cadastro.");
		} catch (UnknownAccountException | IncorrectCredentialsException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Usuário ou senha inválidos.");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao autorizar cadastro sem biometria.");
			LoggerUtil.logErro(e, log);
		}

		return null;
	}

	public boolean desabilitarBotaoAutorizar() {
		return this.visitante.getPessoa().getBiometria() != null
				&& this.visitante.getPessoa().getBiometria().getTemplate() != null;
	}

	public boolean habilitarCredenciar(Credenciamento vr) {
		if (vr != null) {
			return SecurityUtil.getUnidadeSelecionada().getId() != vr.getUnidadePrisional().getId()
					|| (FaseCredenciamento.APROVACAO.getId().equals(vr.getFaseCredenciamento())
							&& StatusCredenciamento.APROVADO.getId().equals(vr.getStatusCredenciamento()))
					|| (StatusCredenciamento.INATIVO.getId().equals(vr.getStatusCredenciamento()));
		}
		return false;
	}

	public boolean habilitarExcluirCredenciamento(Credenciamento vr) {
		if (vr != null) {
			return SecurityUtil.getUnidadeSelecionada().getId() != vr.getUnidadePrisional().getId();
		}
		return false;
	}

	

	private boolean verificaVisitanteResponsavelPossuiCredenciamento(Visitante visitante, Recuperando recuperando) {
		return service.verificaVisitanteResponsavelPossuiCredenciamento(visitante, recuperando);
	}

	public boolean getVisitantePreso(long idVisitante) {
		StatusRecuperando status = this.recuperandoService.buscarStatusRecuperandoPorIdVisitante(idVisitante);

		if (status == null)
			return false;

		return status.equals(StatusRecuperando.ATIVO) || status.equals(StatusRecuperando.INATIVO_PARCIAL);
	}

	public AcaoDeTela getAcaoAtual() {
		return this.getAcaoDeTela();
	}

	public boolean bloqueiaAlterarVisitante(StatusVisitante status) {
		if (status == null)
			return true;

		boolean isInativo = status.equals(StatusVisitante.M) || status.equals(StatusVisitante.I);
		boolean possuiPermissao = securityUtil.possuiPermissao("AlterarVisitanteInativo");

		switch (getAcaoDeTela()) {
		case LISTAR:
			if (possuiPermissao)
				return false;
			else
				return isInativo;

		case ALTERAR:
			if (possuiPermissao)
				return true;
			else if (PrettyContext.getCurrentInstance().getCurrentMapping().getId()
					.equals(VisitanteBean.mappingRecuperando)) {
				return !status.equals(StatusVisitante.M);
			} else {
				return !isInativo;
			}
		case CREDENCIAR:
			return !isInativo;

		case VISUALIZAR:
			return true;

		default:
			return false;
		}
	}
}

class PessoaSorter implements Comparator<Visitante> {

	private String sortField;
	private SortOrder sortOrder;

	public PessoaSorter(String sortField, SortOrder sortOrder) {
		this.sortField = sortField;
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(Visitante p1, Visitante p2) {
		try {
			Field field = Visitante.class.getDeclaredField(this.sortField);
			field.setAccessible(true);
			Object value1 = field.get(p1);
			Object value2 = field.get(p2);
			field.setAccessible(false);
			int value = ((Pessoa) value1).getNome().compareTo(((Pessoa) value2).getNome());

			return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
