package br.gov.mt.sejudh.sigepen.bean;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoVisitanteDTO;
import br.gov.mt.sejudh.sigepen.service.*;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.biometrics.BiometriaRetorno;
import br.gov.mt.sejudh.sigepen.biometrics.BiometriaSDKDao;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaida;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamentosVisita;
import br.gov.mt.sejudh.sigepen.enumerator.StatusVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAutorizacaoValidacaoSemBio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
		@URLMapping(id = "ListarEntradaSaida", pattern = "/listarentradasaida", viewId = "/WEB-INF/pages/entradasaida/ListarEntradaSaida.xhtml"),
		@URLMapping(id = "EntradaSaida", pattern = "/entradasaida", viewId = "/WEB-INF/pages/entradasaida/EntradaSaida.xhtml"),

})
public class EntradaSaidaBean extends MetodosDePaginaBean {

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	private FiltroEntradaVisitante filtro;

	private LazyDataModel<Visitante> entradas;
	private LazyDataModel<RecuperandoVisitanteDTO> recuperandoVisitanteEntradas;

	@Inject
	private EntradaSaidaService service;
	
	@Inject
	private CredenciamentoService credenciamentoService;

	private EntradaSaida entradaSelecionada;

	private EntradaSaida entrada;

	private Ocorrencia ocorrencia;

	private TipoOcorrencia tipoOcorrencia;
	private Long idTipoOcorrencia;

	private Visitante visitante;

	private Long idPessoa;

	@Inject
	VisitanteService visitanteService;

	@Inject
	TipoVisitaService tipoVisitaService;
	
	@Inject
	PedidoService pedidoService;

	@Inject
	private BiometriaSDKDao biometriaSDKDao;
	
	@Inject
	private FaltaDisciplinarService faltaDisciplinarService;

	@Inject
	private TipoOcorrenciaService tipoOcorrenciaService;

	@Inject
	private OcorrenciaService OcorrenciaService;

	@Inject
	private EstruturaService estruturaService;

	private List<TipoOcorrencia> tipoOcorrencias = new ArrayList<TipoOcorrencia>();

	private Long estruturaSelecionada;

	public FaltaDisciplinarService getFaltaDisciplinarService() {
		return faltaDisciplinarService;
	}

	public void setFaltaDisciplinarService(FaltaDisciplinarService faltaDisciplinarService) {
		this.faltaDisciplinarService = faltaDisciplinarService;
	}

	private List<ParametroVisita> parametros;

	private Boolean usuarioPossuiLeitorBiometricoAtivo = false;

	private Boolean visitanteTemBiometria = null;

	private List<Credenciamento> credenciamentos;

	private List<Estrutura> estruturas;

	private boolean visualizacao;

	private String loginUsuarioAutorizador;
	private String senhaUsuarioAutorizador;
	private String observacaoUsuarioAutorizador;

	@Inject
	private UsuarioService usuarioService;

	private boolean pesquisaBiometrica;

	private String autorizacoesVisita;

	private String autorizarSemBiometria;

	@Inject
	private SecurityUtil securityUtil;

	private boolean mostrarPanelVisitante = false;

	private boolean mostrarPanelResponsavel = false;

	private String acao;

	private Long visitanteResponsavel;

	private boolean processando = false;

	@URLAction(onPostback = false)
	public String init() throws Exception {
		this.pesquisaBiometrica = false;
		acao = facesUtil.getQueryParameter("acao");
		if (acao == null) {
			acao = (String) facesUtil.getObjetoSessao("acao");
		}
		FiltroEntradaVisitante filtroDash = (FiltroEntradaVisitante) facesUtil
				.getFlashParameter("filtroEntradaSaidaDash");

		if (filtroDash != null) {
			setFiltro(filtroDash);
		} else {
			super.carregarFiltroPaginacao(FiltroEntradaVisitante.class, this);
			getFiltro().setData(new Date());
			getFiltro().setSomenteEntradas(true);
		}

		try {
			carregarEstruturaDisponivel();
		} catch (ValidacaoException e1) {
			e1.printStackTrace();
		}

		if (acao == null) {
			try {
				switch (this.getAcaoDeTela()) {
				case LISTAR:
					this.buscarEstruturaDisponiveis();
					this.pesquisar();
					this.tipoOcorrencias = this.tipoOcorrenciaService.listar();
					logDeAcesso(log, "Entrada/Saida");
					break;

				default:
					break;

				}
			} catch (Exception e) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
				LoggerUtil.logErro(e, log);
			}
		} else if ("ENTRADA".equals(acao)) {
			String idPessoa = facesUtil.getQueryParameter("idPessoa");
			if (idPessoa != null) {
				buscarEntradaSaida(idPessoa);
				this.processando = false;
				return null;
			}

			List<Long> idsBiometria = (List<Long>) facesUtil.getObjetoSessao("idsBiometria");
			if (idsBiometria != null && idsBiometria.size() > 0) {
				Long idBiometria = idsBiometria.get(0);
				idPessoa = idBiometria.toString();
				if (idPessoa != null) {
					this.pesquisaBiometrica = true;
					buscarEntradaSaida(idPessoa);
					this.processando = false;
					return null;
				}
			}

			List<Long> idsPessoa = (List<Long>) facesUtil.getObjetoSessao("idsPessoa");
			if (idsPessoa != null && idsPessoa.size() > 0) {
				Long idBiometria = idsPessoa.get(0);
				idPessoa = idBiometria.toString();
				if (idPessoa != null) {
					this.pesquisaBiometrica = false;
					buscarEntradaSaida(idPessoa);
					this.processando = false;
					return null;
				}
			}
		} else if ("VISUALIZAR".equals(acao) || "ALTERAR".equals(acao) || "FINALIZAR".equals(acao)
				|| "CANCELAR".equals(acao)) {
			String idPessoa = facesUtil.getQueryParameter("idPessoa");
			if (idPessoa != null) {
				buscarEntradaSaida(idPessoa);
				this.processando = false;
				return null;
			}
		}
		this.processando = false;
		return null;
	}

	public void pesquisar() {
		this.recuperandoVisitanteEntradas = new LazyDataModel<RecuperandoVisitanteDTO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getRowKey(RecuperandoVisitanteDTO object) {
				if (object != null && object.getId() != null) {
					return object.getId();
				}
				return null;
			}

			@Override
			public RecuperandoVisitanteDTO getRowData(String rowKey) {
				if (rowKey == null || rowKey.isEmpty()) {
					return null;
				}

				for (RecuperandoVisitanteDTO rec : recuperandoVisitanteEntradas) {
					if (rec.getId().equals(Long.valueOf(rowKey))) {
						return rec;
					}
				}

				return null;
			}

			@Override
			public List<RecuperandoVisitanteDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				try {
					carregarEstruturaDisponivel();
				} catch (ValidacaoException e1) {
					e1.printStackTrace();
				}

				getFiltro().setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
				getFiltro().setData(new Date());
				getFiltro().setSomenteEntradas(true);
				setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				List<RecuperandoVisitanteDTO> lista = service.buscarEntradasPorFiltro(getFiltro(), false);
				RequestContext.getCurrentInstance().update("formStatus");

				return lista;
			}
		};
	}

	public void abrirDialogAssociarVisitante(ActionEvent event) {
		facesUtil.setObjetoSessao("matricula", null);
		facesUtil.exibirPaginaDialog("pretty:AssociarResponsavel", 400, 1000);
	}

	public void associarVisitante(SelectEvent event) {
		Visitante v = (Visitante) event.getObject();
		if (v == null)
			return;
		LoggerUtil.logInfo(Acao.ASSOCIAR, v, log);
		List<Long> ids = new ArrayList<Long>();
		ids.add(v.getPessoa().getId());
		redirectAposPesquisar(ids);
	}

	public void abrirDialogDetalhesVisitante(long id) {
		RequestContext.getCurrentInstance().execute("PF('dialogDetalhesVisitante').show()");
	}

	protected void carregarEstruturaDisponivel() throws ValidacaoException {
		this.setParametros(
				this.service.buscarParametrosVisita(SecurityUtil.getUnidadeSelecionada().getId(), new Date()));
	}

	public void retornoOperacaoBiometrica() {
		switch (BiometriaRetorno.getOperacao(facesUtil)) {
		case Pesquisar:
			redirectAposBiometria(BiometriaRetorno.getValor());
			break;
		case Registrar:
			break;
		case Capturar:
			validarBiometria(BiometriaRetorno.getValor());
			break;
		default:
			break;
		}
	}

	public void redirectAposBiometria(List<Long> ids) {
		boolean encontrou = false;
		for (Long i : ids) {
			if (i != null && i > 0)
				encontrou = true;
		}
		if (!encontrou) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Biometria não encontrada!");
			return;
		}
		Visitante v = service.buscarBiometriaVisitante(ids);
		encontrou = v != null;
		if (!encontrou) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Biometria não encontrada!");
			return;
		}
		String nome = validarEntrada(ids);
		if (nome != null) {
			String[] strs = nome.split(";");
			String unidade = strs[0];
			String visitante = strs[1];
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "ERRO",
					"Existe uma Visita em andamento em outra unidade prisional " + unidade + " para " + visitante
							+ ". Operação não permitida!");
			return;
		}
		facesUtil.setObjetoSessao("acao", "ENTRADA");
		facesUtil.setObjetoSessao("idsBiometria", ids);
		String url = Sigepen.getApplicationName() + "/entradasaida";
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void redirectAposPesquisar(List<Long> ids) {
		String nome = validarEntrada(ids);
		if (nome != null) {
			String[] strs = nome.split(";");
			String unidade = strs[0];
			String visitante = strs[1];
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "ERRO",
					"Existe uma Visita em andamento em outra unidade prisional " + unidade + " para " + visitante
							+ ". Operação não permitida!");
			return;
		}
		facesUtil.setObjetoSessao("acao", "ENTRADA");
		facesUtil.setObjetoSessao("idsPessoa", ids);
		String url = Sigepen.getApplicationName() + "/entradasaida";
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private String validarEntrada(List<Long> ids) {

		FiltroEntradaSaida f = new FiltroEntradaSaida();
		f.setPessoasIds(ids);
		f.setData(new Date());
		return service.validarEntrada(f, SecurityUtil.getUnidadeSelecionada().getId());
	}

	public void validarBiometria(String templateCapturado) {

		try {
			byte[] templateBiometricoCapturado = Base64.getDecoder()
					.decode(new String(templateCapturado).getBytes("UTF-8"));
			byte[] templateBiometricoCadastrado = this.entrada.getVisitante().getPessoa().getBiometria().getTemplate();

			boolean asBiometriasSaoCorrespondentes = biometriaSDKDao.verificarPorTemplate(templateBiometricoCapturado,
					templateBiometricoCadastrado);

			if (asBiometriasSaoCorrespondentes) {
				if (this.entrada.getId() != null && this.entrada.getSituacao().equals(SituacaoVisita.EM_ANDAMENTO)) {
					this.finalizarVisita();
				} else {
					this.iniciarVisita();
				}
			} else {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Biometria inválida!");
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao validar Biometria do Visitante!");
			LoggerUtil.logErro(e, log);
		}
	}

	public FiltroEntradaVisitante getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroEntradaVisitante filtro) {
		this.filtro = filtro;
	}

	public EntradaSaida getEntradaSelecionada() {
		return entradaSelecionada;
	}

	public void setEntradaSelecionada(EntradaSaida entradaSelecionada) {
		this.entradaSelecionada = entradaSelecionada;
	}

	public LazyDataModel<Visitante> getEntradas() {
		return entradas;
	}

	public void setEntradas(LazyDataModel<Visitante> entradas) {
		this.entradas = entradas;
	}

	public Long getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(Long idPessoa) {
		this.idPessoa = idPessoa;
	}

	public Boolean getUsuarioPossuiLeitorBiometricoAtivo() {
		return usuarioPossuiLeitorBiometricoAtivo;
	}

	public boolean verificaSeVisitanteTemBiometria() {
		if (this.entrada == null) {
			return false;
		}

		return (this.entrada.getVisitante().getPessoa().getBiometria() != null
				&& this.entrada.getVisitante().getPessoa().getBiometria().getTemplate() != null);
	}

	public void setUsuarioPossuiLeitorBiometricoAtivo() {
		Map<String, String> map = facesUtil.getParameters();
		this.usuarioPossuiLeitorBiometricoAtivo = Boolean.parseBoolean(map.get("status"));

		if (this.entrada != null) {
			visitanteTemBiometria = verificaSeVisitanteTemBiometria();
			Boolean habilitaBotaoAutorizacao = null;

			if (this.usuarioPossuiLeitorBiometricoAtivo && visitanteTemBiometria) {
				habilitaBotaoAutorizacao = false;
			} else if (this.usuarioPossuiLeitorBiometricoAtivo && !visitanteTemBiometria) {
				habilitaBotaoAutorizacao = true;
			} else if (!this.usuarioPossuiLeitorBiometricoAtivo) {
				habilitaBotaoAutorizacao = true;
			}

			RequestContext.getCurrentInstance().execute("setStatusBotaoAutorizacao(" + habilitaBotaoAutorizacao
					+ ", 'formInformacoesVisitante:btnAutorizacaoEntradaSaida')");
		}
	}

	public List<ParametroVisita> getParametros() {
		return parametros;
	}

	public void setParametros(List<ParametroVisita> parametros) {
		this.parametros = parametros;
	}

	public List<Estrutura> getEstruturaDisponivel(TipoEntrada te) {
		List<Estrutura> r = new ArrayList<Estrutura>();
		for (ParametroVisita p : this.getParametros()) {
			if (p.getTipoEntrada() == te) {
				for (Estrutura e : p.getCelas()) {
					boolean cad = true;
					for (Estrutura x : r) {
						if (x.getId() == e.getId()) {
							cad = false;
						}
					}
					if (cad) {
						r.add(e);
					}
				}
			}
		}
		return r;
	}

	public List<Estrutura> getEstruturaDisponivelEntradaSaida(TipoEntrada te) {
		List<Estrutura> r = new ArrayList<Estrutura>();
		for (ParametroVisita p : this.getParametros()) {
			if (p.getTipoEntrada() == te) {
				for (Estrutura e : p.getCelas()) {
					boolean cad = true;
					for (Estrutura x : r) {
						if (x.getId() == e.getId()) {
							cad = false;
						}
					}
					if (cad) {
						r.add(e);
					}
				}
			}
		}
		return r;
	}

	public EntradaSaida getEntrada() {
		return entrada;
	}

	public void setEntrada(EntradaSaida entrada) {
		this.entrada = entrada;
	}

	public void clickIniciarVisita() {
		try {
			validarIniciarVisita();
			if (this.pesquisaBiometrica) {
				this.iniciarVisita();
			} else if (!this.pesquisaBiometrica && this.usuarioPossuiLeitorBiometricoAtivo
					&& this.entrada.getVisitante().getPessoa().getBiometria() != null
					&& this.entrada.getVisitante().getPessoa().getBiometria().getTemplate() != null) {
				RequestContext.getCurrentInstance().execute("capturar();");
			} else {
				RequestContext.getCurrentInstance().execute("PF('dlgAutBio').show()");
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		}
	}

	private boolean validaOcorrenciaMesmoDia(){

		LocalDate dataOcorrencia = this.ocorrencia.getDataOcorrencia().toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();

		boolean existeOcorrenciaNoMesmoDia = this.visitante.getOcorrencias().stream()
				.map(o -> o.getDataOcorrencia().toInstant()
						.atZone(ZoneId.systemDefault())
						.toLocalDate())
				.anyMatch(data -> data.equals(dataOcorrencia));


		return existeOcorrenciaNoMesmoDia;

	}

	public void clickOcorrencia() throws ValidacaoException {
		if(!this.validaOcorrenciaMesmoDia()){
			this.ocorrencia.setVisitante(this.visitante);
			Ocorrencia ocorrencia = this.OcorrenciaService.salvar(this.ocorrencia);
			this.visitante.getOcorrencias().add(ocorrencia);

			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Descrição da ocorrencia Salvo com sucesso!");
		}else{
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Erro", "Ocorrência já registrada para o dia de hoje.");
			return;
		}
		RequestContext.getCurrentInstance().execute("PF('detalhes').hide()");
		this.idTipoOcorrencia = null;
	}

	public void descricaoOcorrenciaAtualizada() {
		if(this.ocorrencia.getDescricao().isEmpty()){
			facesUtil.exibirMensagemInfo(
					"Descrição da ocorrencia e obrigatoria ",
					false);
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Erro", "Descrição da ocorrencia e obrigatoria");
		}
	}

	public void addTipoOcorrencia() {
		this.ocorrencia.setTipoOcorrencia(new TipoOcorrencia(this.idTipoOcorrencia));
	}

	public void clickFinalizarVisita() {
		if (this.pesquisaBiometrica) {
			this.finalizarVisita();
		} else if (!this.pesquisaBiometrica && this.usuarioPossuiLeitorBiometricoAtivo
				&& this.entrada.getVisitante().getPessoa().getBiometria() != null
				&& this.entrada.getVisitante().getPessoa().getBiometria().getTemplate() != null) {
			RequestContext.getCurrentInstance().execute("capturar();");
		} else {
			RequestContext.getCurrentInstance().execute("PF('dlgAutBio').show()");
		}
	}

	private void validarIniciarVisita() throws ValidacaoException {
		Optional<Credenciamento> optVr = this.getCredenciamentos().stream().filter(
				vr -> vr.getSelecionado() && vr.getTipoVisita().getTipoEntrada().equals(TipoEntrada.ENTRADA_SAIDA))
				.findFirst();

		if (optVr.isPresent() && this.entrada.getMotivoEntrada() == null) {
			exibeDialogMotivoEntradaSaida();
			throw new ValidacaoException("Preencha o motivo da visita");
		}

		if (StatusVisitante.M.equals(this.entrada.getVisitante().getStatus())) {
			throw new ValidacaoException("Entrada não permitida! Visitante com impedimento ativo!");
		}
		boolean ativo = false;
		ativo = validaSeExisteCredenciamentoAtivoNoStatusDefinidoTempoExecucao(ativo);
		if (!ativo || (this.getCredenciamentos() == null || 
				(this.getCredenciamentos() != null && this.getCredenciamentos().size() == 0))) {
			throw new ValidacaoException("Entrada não permitida! Visitante sem credenciamento ativo!");
		}
		boolean selecionado = false;
		for (Credenciamento vr : this.getCredenciamentos()) {
			if (vr.getSelecionado()) {
				selecionado = true;
			}
		}
		if (!selecionado) {
			throw new ValidacaoException("Obrigatório selecionar ao menos um credenciamento!");
		}

		if (this.getEntrada() != null && this.getEntrada().getVisitante() != null
				&& this.getEntrada().getVisitante().getEmancipado() != null
				&& this.getEntrada().getVisitante().getEmancipado().equals(SimNao.N)
				&& verificarVisitaResponsavel(this.visitanteResponsavel)) {
			throw new ValidacaoException("Entrada não permitida! Visitante responsável sem visita em andamento!");
		}

	}

	private boolean validaSeExisteCredenciamentoAtivoNoStatusDefinidoTempoExecucao(boolean ativo) {
		for (Credenciamento vr : this.getCredenciamentos()) {
			if (StatusCredenciamentosVisita.DISPONIVEL.getId().equals(vr.getStatusVisita().getStatus().getId()))
				ativo = true;
		}
		return ativo;
	}

	public void iniciarVisita() {
		FiltroEntradaSaida f = new FiltroEntradaSaida();
		f.setData(new Date());
		f.getPessoasIds().add(this.getEntrada().getVisitante().getPessoa().getId());
		f.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		List<EntradaSaida> lista = service.buscarEntradasPorFiltro(f, true);
		if (lista != null && lista.size() > 0) {
			EntradaSaida eAux = lista.get(0);
			if (SituacaoVisita.EM_ANDAMENTO.equals(eAux.getSituacao())) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Visitação já iniciada!");
				return;
			}
		}

		entrada.setDataEntrada(new Date());
		entrada.setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());
		entrada.setVisitante(this.getEntrada().getVisitante());
		entrada.setRecuperandos(new ArrayList<EntradaSaidaRecuperando>());

		for (Credenciamento vr : this.getCredenciamentos()) {
			if (vr.getSelecionado()) {
				EntradaSaidaRecuperando e = new EntradaSaidaRecuperando();
				e.setEntrada(entrada);
				e.setCredenciamento(vr);
				entrada.getRecuperandos().add(e);
			}
		}
		entrada.setSituacao(SituacaoVisita.EM_ANDAMENTO);
		addVisitanteResponsavel(entrada);
		this.entrada = service.salvar(entrada);
		LoggerUtil.logInfo(Acao.INICIAR, this.entrada, log);
		facesUtil.exibirMensagemInfo(
				"Entrada do visitante " + this.entrada.getVisitante().getPessoa().getNome() + " realizada com sucesso!",
				true);
		String url = Sigepen.getApplicationName() + "/listarentradasaida";
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return;
	}

	private void addVisitanteResponsavel(EntradaSaida entrada) {
			Visitante entradaResponsavel = service.responsavelDeuEntrada(this.credenciamentos.get(0).getVisitante().getId());
			if (entradaResponsavel != null) {
				entrada.setVisitanteResponsavel(entradaResponsavel);
			}
	}

	public void finalizarVisita() {
		LoggerUtil.logInfo(Acao.FINALIZAR, this.entrada, log);

		this.entrada.setDataSaida(new Date());
		this.entrada.setSituacao(SituacaoVisita.FINALIZADA);
		service.salvar(this.entrada);
		facesUtil.exibirMensagemInfo(
				"Saída do visitante " + this.entrada.getVisitante().getPessoa().getNome() + " realizada com sucesso!",
				true);
		String url = Sigepen.getApplicationName() + "/listarentradasaida";
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public List<Credenciamento> getCredenciamentos() {
		return credenciamentos;
	}

	public void setCredenciamentos(List<Credenciamento> credenciamentos) {
		this.credenciamentos = credenciamentos;
	}

	public void selecaoStatusVisita() throws ValidacaoException {
				
		UnidadePrisional unidadeSelecionada =  SecurityUtil.getUnidadeSelecionada();
		this.credenciamentos = new ArrayList<Credenciamento>();
		if (this.entrada != null) {
			for (Credenciamento vr : credenciamentoService.buscarPorVisitanteId(this.entrada.getVisitante().getId())) {
				vr.setStatusVisita(this.service.buscarStatus(vr));
				vr.setSelecionado(false);
				if (this.entrada.getRecuperandos() != null) {
					for (EntradaSaidaRecuperando esr : this.entrada.getRecuperandos()) {
						if (esr.getCredenciamento().getId().equals(vr.getId())) {
							vr.setSelecionado(true);
						}
					}
				}
				if (vr.getUnidadePrisional() == null) {
					if (vr.getVisitante().getUnidadePrisional().getId() == unidadeSelecionada.getId()) {
						if (this.getFiltro().getStatusCredenciamento() == null) {
							this.credenciamentos.add(vr);
						} else {
							if (this.getFiltro().getStatusCredenciamento().equals(this.service.buscarStatus(vr).getStatus())) {
								this.credenciamentos.add(vr);
							}
						}
					}

				}else {
					if (vr.getRecuperando() != null) {
						UnidadePrisional unidadePrisionalAtual = this.faltaDisciplinarService.buscaUnidadePriosionalAtualRecuperandoId(vr.getRecuperando().getId());
						if (unidadePrisionalAtual != null && unidadePrisionalAtual.getId() == unidadeSelecionada.getId()) {
							if (this.getFiltro().getStatusCredenciamento() == null) {
								this.credenciamentos.add(vr);
							} else {
								if (this.getFiltro().getStatusCredenciamento().equals(this.service.buscarStatus(vr).getStatus())) {
									this.credenciamentos.add(vr);
								}
							}
						}  
					} else {
						//Visitante é prestador de serviço, não tem recuperando vinculado
						this.credenciamentos.add(vr);
					}
					
				}
			}
		}
	}

	public void changeSelecaoStatusVisita() throws ValidacaoException {
		this.selecaoStatusVisita();
		RequestContext.getCurrentInstance().update("formInformacoesVisitante:tableCredenciamentos");
	}

	public void innerSelecaoStatusVisita() throws ValidacaoException {
		this.selecaoStatusVisita();
		RequestContext.getCurrentInstance().update("formInformacoesVisitante");
	}

	public List<Estrutura> getEstruturas() {
		return estruturas;
	}

	public void setEstruturas(List<Estrutura> estruturas) {
		this.estruturas = estruturas;
	}

	public void abrirDialogEstruturaDisponiveis(ActionEvent event) {
		this.estruturas = new ArrayList<Estrutura>();
		for (ParametroVisita p : this.getParametros()) {
			for (ParametroVisitaEstrutura pe : p.getEstruturas()) {
				this.estruturas.add(pe.getEstrutura());
			}
		}

		List<Estrutura> sorted = this.estruturas.stream().sorted(Comparator.comparing(Estrutura::getId))
				.collect(Collectors.toList());

		List<Estrutura> result = new ArrayList<Estrutura>();
		long anterior = -1;

		for (Estrutura e : sorted) {
			if (anterior != e.getId()) {
				result.add(e);
				anterior = e.getId();
			}
		}

		this.estruturas = result;

		RequestContext.getCurrentInstance().execute("PF('dialogEstrutura').show()");
	}

	public void clickSelection(AjaxBehaviorEvent e) {
		boolean sel = ((SelectBooleanCheckbox) e.getSource()).isSelected();
		Long id = 0L;
		try {
			id = Long.parseLong(((SelectBooleanCheckbox) e.getSource()).getTitle());
		} catch (Exception e2) {
		}
		if (id > 0) {
			for (Credenciamento vr : this.getCredenciamentos()) {
				if (id.equals(vr.getId())) {
					vr.setSelecionado(sel);
				}
			}
		}

		validaExibicaoMotivo();
	}

	private void validaExibicaoMotivo() {
		if (this.getCredenciamentos().stream().filter(
				vr -> vr.getTipoVisita().getTipoEntrada().equals(TipoEntrada.ENTRADA_SAIDA) && vr.getSelecionado())
				.count() > 0) {
			if (this.entrada.getMotivoEntrada() == null) {
				exibeDialogMotivoEntradaSaida();
			}
		} else {
			this.entrada.setMotivoEntrada(null);
		}
	}

	public void clickResponsavel(AjaxBehaviorEvent e) {
		boolean sel = ((SelectBooleanCheckbox) e.getSource()).isSelected();
		Long id = 0L;
		try {
			id = Long.parseLong(((SelectBooleanCheckbox) e.getSource()).getTitle());
		} catch (Exception e2) {
		}
		if (id > 0) {
			exibeDialogAvisoResponsavel();
			for (Visitante vr : this.entrada.getVisitante().getVisitantesReponsaveis()) {
				if (id.equals(vr.getId())) {
					vr.setSelecionado(sel);
				} else {
					vr.setSelecionado(false);
				}
			}
			this.visitanteResponsavel = id;
		}
	}

	private void exibeDialogAvisoResponsavel() {
		RequestContext.getCurrentInstance().execute("PF('dlgAvisoResponsavel').show()");
	}

	private void exibeDialogMotivoEntradaSaida() {
		RequestContext.getCurrentInstance().execute("PF('dlgMotivoObs').show()");
	}

	public void fecharDialogMotivoEntradaSaida() {
		RequestContext.getCurrentInstance().execute("PF('dlgMotivoObs').hide()");
		validaExibicaoMotivo();
	}

	public boolean isVisualizacao() {
		return visualizacao;
	}

	public void setVisualizacao(boolean visualizacao) {
		this.visualizacao = visualizacao;
	}

	public Long emAndamento() {
		FiltroEntradaSaida f = new FiltroEntradaSaida();
		f.setData(new Date());
		f.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		return service.buscarQuantidadeEmAndamento(f);
	}

	public Long finalizadas() {
		FiltroEntradaSaida f = new FiltroEntradaSaida();
		f.setData(new Date());
		f.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		return service.buscarQuantidadeFinalizada(f);
	}

	public Boolean getStatus() {
		Map<String, String> params = facesUtil.getFacesContext().getExternalContext().getRequestParameterMap();
		String status = params.get("status");
		return "true".equals(status);
	}

	public String autorizarCadastroSemBiometria() {
		try {
			AuthenticationInfo authenticationInfo = SecurityUtils.getSecurityManager().authenticate(
					new UsernamePasswordToken(this.loginUsuarioAutorizador, this.senhaUsuarioAutorizador));
			SecurityUtils.getSecurityManager().checkPermission(authenticationInfo.getPrincipals(),
					"AutorizarCadastroSemBiometria");
			Usuario usuario = usuarioService.buscarUsuarioPorLogin(this.loginUsuarioAutorizador);

			AutorizacaoValidacaoSemBiometria autValid = new AutorizacaoValidacaoSemBiometria();

			autValid.setObservacao(this.observacaoUsuarioAutorizador);
			autValid.setUsuario(usuario);
			autValid.setDataAutorizacao(Sigepen.getDataAtual());
			autValid.setEntradaSaida(this.entrada);

			if ("FINALIZAR".equals(this.acao)) {
				RequestContext.getCurrentInstance().execute("PF('dialogInformacoesVisitante').hide()");
				autValid.setTipoAutorizacaoValidacaoSemBio(TipoAutorizacaoValidacaoSemBio.SAIDA_VISITANTE);
				this.entrada.getAutorizacaoValidacaoSemBiometria().add(autValid);
				finalizarVisita();
			} else {
				if (this.entrada.getId() != null && this.entrada.getSituacao().equals(SituacaoVisita.EM_ANDAMENTO)) {
					autValid.setTipoAutorizacaoValidacaoSemBio(TipoAutorizacaoValidacaoSemBio.SAIDA_VISITANTE);
					this.entrada.getAutorizacaoValidacaoSemBiometria().add(autValid);
					this.finalizarVisita();
				} else {
					autValid.setTipoAutorizacaoValidacaoSemBio(TipoAutorizacaoValidacaoSemBio.ENTRADA_VISITANTE);
					this.entrada.getAutorizacaoValidacaoSemBiometria().add(autValid);
					this.iniciarVisita();
				}
			}

			this.loginUsuarioAutorizador = "";
			this.senhaUsuarioAutorizador = "";
			this.observacaoUsuarioAutorizador = "";

			RequestContext.getCurrentInstance().execute("PF('dlgAutBio').hide()");
			LoggerUtil.logInfo(Acao.AUTORIZAR, this.entrada, log);

			RequestContext.getCurrentInstance().update("registrosForm");
			RequestContext.getCurrentInstance().update("formInformacoesVisitante");
		} catch (UnauthorizedException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Usuário informado não possui permissão para  realizar a autorização de Entrada/Saída sem Biometria.");
		} catch (UnknownAccountException | IncorrectCredentialsException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Usuário ou senha inválidos.");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao autorizar cadastro sem biometria.");
			LoggerUtil.logErro(e, log);
		}

		return null;
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

	public boolean isPesquisaBiometrica() {
		return pesquisaBiometrica;
	}

	public void setPesquisaBiometrica(boolean pesquisaBiometrica) {
		this.pesquisaBiometrica = pesquisaBiometrica;
	}

	public Boolean verificarVisitaResponsavel(Long visitanteResponsavel) throws ValidacaoException {
//		if (visitanteResponsavel == null) {
//			throw new ValidacaoException("É necessário selecionar um Visitante responsável!");
//		}

		Long entradaResponsavel = service.responsavelDeuEntrada(this.credenciamentos.get(0).getVisitante().getId()).getId();
		if(entradaResponsavel == null){
			throw new ValidacaoException("É necessário selecionar um Visitante responsável!");
		}

		FiltroEntradaSaida fr = new FiltroEntradaSaida();
		fr.setData(new Date());
		fr.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		fr.setResponsavel(entradaResponsavel);
		return service.buscarEntradaResponsavel(fr) == 0;
	}

	public void excluir(Visitante v) {
		Date di = Sigepen.getDataConsultaMaiorIgual(new Date());
		Date df = Sigepen.getDataConsultaMenorIgual(new Date());
		for (EntradaSaida e : v.getEntradas()) {
			if (e.getDataEntrada().after(di) && e.getDataEntrada().before(df)) {
				this.service.excluir(e);
				LoggerUtil.logInfo(Acao.REMOVER, e, log);
			}
		}
		facesUtil.exibirMensagemInfo("Visita excluída com sucesso!", true);
	}

	public void cancelarVisita() {
		if (this.entrada.getMotivoCancelamento().isEmpty()) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Motivo do cancelamento é obrigatório!");
			return;
		}
		this.entrada.setDataCancelamento(new Date());
		this.entrada.setSituacao(SituacaoVisita.CANCELADA);
		service.salvar(this.entrada);
		LoggerUtil.logInfo(Acao.CANCELAR, this.entrada, log);
		facesUtil.exibirMensagemInfo("Visitação cancelada com sucesso!", true);
		String url = Sigepen.getApplicationName() + "/listarentradasaida";
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void finalizacaoVisita() {
		if (this.entrada.getMotivoFinalizacao().isEmpty()) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Preenchimento do motivo é obrigatório");
			return;
		}
		this.entrada.setDataSaida(new Date());
		this.entrada.setSituacao(SituacaoVisita.FINALIZADA);
		service.salvar(this.entrada);
		LoggerUtil.logInfo(Acao.FINALIZAR, this.entrada, log);
		facesUtil.exibirMensagemInfo("Visitação finalizada com sucesso!", true);
		String url = Sigepen.getApplicationName() + "/listarentradasaida";
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void salvar(EntradaSaida e1) {
		if (this.processando)
			return;
		boolean selecionado = false;
		for (Credenciamento vr : this.getCredenciamentos()) {
			if (vr.getSelecionado()) {
				selecionado = true;
			}
		}
		if (selecionado) {
			this.processando = true;
			while (this.entrada.getRecuperandos() != null && this.entrada.getRecuperandos().size() > 0) {
				this.entrada.getRecuperandos().remove(0);
			}
			for (Credenciamento vr : this.getCredenciamentos()) {
				if (vr.getSelecionado()) {
					EntradaSaidaRecuperando e = new EntradaSaidaRecuperando();
					e.setEntrada(entrada);
					e.setCredenciamento(vr);
					this.entrada.getRecuperandos().add(e);
				}
			}
			service.salvar(entrada);
			LoggerUtil.logInfo(Acao.ALTERAR, this.entrada, log);
			facesUtil.exibirMensagemInfo("Visitação do visitante " + this.entrada.getVisitante().getPessoa().getNome()
					+ " alterada com sucesso!", true);
			String url = Sigepen.getApplicationName() + "/listarentradasaida";
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect(url);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
					"Obrigatório selecionar ao menos um credenciamento!");
		}
	}

	public String getAutorizacoesVisita() {
		return autorizacoesVisita;
	}

	public void setAutorizacoesVisita(String autorizacoesVisita) {
		this.autorizacoesVisita = autorizacoesVisita;
	}

	public String getAutorizarSemBiometria() {
		return autorizarSemBiometria;
	}

	public void setAutorizarSemBiometria(String autorizarSemBiometria) {
		this.autorizarSemBiometria = autorizarSemBiometria;
	}

	public void abrirDlgAutorizar(Acao acao) {
		if (acao.equals(Acao.INICIAR)
				&& this.entrada.getAutorizacaoValidacaoSemBiometria().stream()
						.filter(a -> a.getTipoAutorizacaoValidacaoSemBio()
								.equals(TipoAutorizacaoValidacaoSemBio.ENTRADA_VISITANTE))
						.count() == 0
				&& !securityUtil.possuiPermissao("AutorizarEntradaVisitanteSemBiometria")) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO",
					"Entrada não permitida! Usuário não autorizado para efetuar entrada!");
			return;
		}
		if (acao.equals(Acao.FINALIZAR)
				&& this.entrada.getAutorizacaoValidacaoSemBiometria().stream()
						.filter(a -> a.getTipoAutorizacaoValidacaoSemBio()
								.equals(TipoAutorizacaoValidacaoSemBio.SAIDA_VISITANTE))
						.count() == 0
				&& !securityUtil.possuiPermissao("AutorizarSaidaVisitanteSemBiomatria")) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO",
					"Saída não permitida! Usuário não autorizado para efetuar saída!");
			return;
		}
		RequestContext.getCurrentInstance().execute("PF('dlgAutBio').show()");
	}

	public Boolean isMenor(Date nasc) {
		if (nasc == null)
			return false;
		Calendar past = Calendar.getInstance();
		past.add(Calendar.YEAR, -18);
		return nasc.after(past.getTime());
	}

	public Boolean getVisitanteTemBiometria() {
		return visitanteTemBiometria;
	}

	public void setVisitanteTemBiometria(Boolean visitanteTemBiometria) {
		this.visitanteTemBiometria = visitanteTemBiometria;
	}

	public Boolean mostrarFinalizar() {
		return "SAIDA".equals(this.acao) && this.getEntrada().getSituacao().equals(SituacaoVisita.EM_ANDAMENTO);
	}

	public Boolean mostrarIniciar() {
		if (this.getEntrada() == null)
			return false;
		return "ENTRADA".equals(this.acao) && this.getEntrada().getDataEntrada() == null;
	}

	public Boolean desabilitarCredenciamento(Credenciamento vr) {
		return "VISUALIZAR".equals(this.acao) || "SAIDA".equals(this.acao)
				|| vr.getStatusVisita().getStatus() != StatusCredenciamentosVisita.DISPONIVEL
				|| !this.getEntrada().getSituacao().equals(SituacaoVisita.EM_ANDAMENTO);
	}

	public Date buscarData(Visitante v) {
		Date di = Sigepen.getDataConsultaMaiorIgual(new Date());
		Date df = Sigepen.getDataConsultaMenorIgual(new Date());
		for (EntradaSaida e : v.getEntradas()) {
			if (e.getDataEntrada().after(di) && e.getDataEntrada().before(df))
				return e.getDataEntrada();
		}
		return null;
	}

	public Date buscarDataEntrada(Visitante v) {
		Date di = Sigepen.getDataConsultaMaiorIgual(new Date());
		Date df = Sigepen.getDataConsultaMenorIgual(new Date());
		for (EntradaSaida e : v.getEntradas()) {
			if (e.getDataEntrada() != null && e.getDataEntrada().after(di) && e.getDataEntrada().before(df))
				return e.getDataEntrada();
		}
		return null;
	}

	public Date buscarDataSaida(Visitante v) {
		Date di = Sigepen.getDataConsultaMaiorIgual(new Date());
		Date df = Sigepen.getDataConsultaMenorIgual(new Date());
 		for (EntradaSaida e : v.getEntradas()) {
			if (e.getDataSaida() != null && e.getDataSaida().after(di) && e.getDataSaida().before(df))
				return e.getDataSaida();
		}
		return null;
	}

	public int buscarStatusVisita(Visitante v) {
		Date di = Sigepen.getDataConsultaMaiorIgual(new Date());
		Date df = Sigepen.getDataConsultaMenorIgual(new Date());
		long unidadeIdSelecionada = SecurityUtil.getUnidadeSelecionada().getId();
				
		for (EntradaSaida e : v.getEntradas()) {
			if (e.getUnidadePrisional().getId() == unidadeIdSelecionada
					&& e.getDataEntrada().after(di) && e.getDataEntrada().before(df)) {
				if (SituacaoVisita.EM_ANDAMENTO.equals(e.getSituacao()))
					return 1;
				else if (SituacaoVisita.FINALIZADA.equals(e.getSituacao()))
					return 2;
			}
		}
		return 0;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	private void buscarEntradaSaida(String idPessoa) throws ValidacaoException {
		Long id = Long.parseLong(idPessoa);
		List<Long> ids = new ArrayList<Long>();
		ids.add(id);

		FiltroEntradaSaida f = new FiltroEntradaSaida();
		f.setPessoasIds(ids);
		Visitante visantePorPessoa = service.buscarPessoaVisitante(ids);

		if (visantePorPessoa != null) {
//			try {
				f.setPessoasIds(ids);
				f.setData(new Date());
				f.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
				List<EntradaSaida> listaEntrada = service.buscarEntradasPorFiltro(f, false);

				if (listaEntrada != null && listaEntrada.size() == 0) {
					this.novaEntrada(visantePorPessoa);
				} else if (listaEntrada != null && listaEntrada.size() > 0) {
					EntradaSaida eAux = listaEntrada.get(0);
					for (EntradaSaida andamento : listaEntrada) {
						if (SituacaoVisita.EM_ANDAMENTO.equals(andamento.getSituacao())) {
							eAux = andamento;
						}
					}
					if (SituacaoVisita.EM_ANDAMENTO.equals(eAux.getSituacao())) {
						this.entrada = new EntradaSaida();
						this.entrada = service.buscarAuditoria(eAux);
						this.autorizacoesVisita = "Autorizar saída sem biometria";
						this.autorizarSemBiometria = "Autorização para saída SEM BIOMETRIA";
						if ("ENTRADA".equals(this.acao))
							this.acao = "SAIDA";
					} else {
						this.novaEntrada(visantePorPessoa);
					}
				}

				if (!this.entrada.getVisitante().getVisitantesReponsaveis().isEmpty()) {
					this.mostrarPanelVisitante = false;
					this.mostrarPanelResponsavel = true;
				}

				this.mostrarPanelVisitante = true;
				this.innerSelecaoStatusVisita();
//			} catch (Exception e) {
//				facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO",
//						"Erro ao pesquisar por biometria: " + e.getMessage());
//					System.out.println(e.getStackTrace());
//				LoggerUtil.logErro(e, log);
//			}
		} else {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Não encontrada Pessoa Visitante!");
		}
	}

	private void novaEntrada(Visitante visitante) {
		this.entrada = new EntradaSaida();
		this.entrada.setSituacao(SituacaoVisita.EM_ANDAMENTO);
		this.autorizacoesVisita = "Autorizar entrada sem biometria";
		this.autorizarSemBiometria = "Autorização para entrada SEM BIOMETRIA";
		this.entrada.setVisitante(visitante);
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

	public void abrirDialogOcorrencia(Visitante v) {

		this.mostrarPanelResponsavel = false;
		this.mostrarPanelVisitante = false;

		this.visitante = v;
		this.ocorrencia = new Ocorrencia();
	}

	public void abrirDialogDetalhes(Visitante v, String a) {
		this.setAcao(a);

		Date di = Sigepen.getDataConsultaMaiorIgual(new Date());
		Date df = Sigepen.getDataConsultaMenorIgual(new Date());
		for (EntradaSaida e : v.getEntradas()) {
			if (e.getDataEntrada().after(di) && e.getDataEntrada().before(df))
				this.entrada = e;
		}
		if (v.getResponsavel() != null) {
			this.mostrarPanelVisitante = false;
			this.mostrarPanelResponsavel = true;
		}

		this.mostrarPanelVisitante = true;

		if(a.equals("OCORRENCIA")){
			this.abrirDialogOcorrencia(v);
		}
	}

	public void buttonIniciarVisita(Long id) {
		List<Long> ids = new ArrayList<Long>();
		ids.add(id);
		redirectAposPesquisar(ids);
	}

	public boolean mostrarSalvar() {
		return "ALTERAR".equals(this.acao) && SituacaoVisita.EM_ANDAMENTO.equals(this.entrada.getSituacao());
	}

    public LazyDataModel<RecuperandoVisitanteDTO> getRecuperandoVisitanteEntradas() {
        return recuperandoVisitanteEntradas;
    }

    public void setRecuperandoVisitanteEntradas(LazyDataModel<RecuperandoVisitanteDTO> recuperandoVisitanteEntradas) {
		recuperandoVisitanteEntradas = recuperandoVisitanteEntradas;
    }

    public Ocorrencia getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(Ocorrencia ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

	public Visitante getVisitante() {
		return visitante;
	}

	public void setVisitante(Visitante visitante) {
		this.visitante = visitante;
	}

    public List<TipoOcorrencia> getTipoOcorrencias() {
        return tipoOcorrencias;
    }

    public void setTipoOcorrencias(List<TipoOcorrencia> tipoOcorrencias) {
        this.tipoOcorrencias = tipoOcorrencias;
    }

    public TipoOcorrencia getTipoOcorrencia() {
        return tipoOcorrencia;
    }

    public void setTipoOcorrencia(TipoOcorrencia tipoOcorrencia) {
        this.tipoOcorrencia = tipoOcorrencia;
    }

    public Long getIdTipoOcorrencia() {
        return idTipoOcorrencia;
    }

	public void setIdTipoOcorrencia(Long idTipoOcorrencia) {
		this.idTipoOcorrencia = idTipoOcorrencia;
	}

    public Long getEstruturaSelecionada() {
        return estruturaSelecionada;
    }

    public void setEstruturaSelecionada(Long estruturaSelecionada) {
        this.estruturaSelecionada = estruturaSelecionada;
    }

	public void estruturaSelecionada(){

		if(this.estruturaSelecionada == null ){
			return;
		}

		this.buscarEstruturaComFilho();

		if(this.filtro.getIdsEstruturas().isEmpty()){
			this.filtro.getIdsEstruturas().add(this.estruturaSelecionada);
		}

		this.filtro.setIdEstrutura(this.estruturaSelecionada);
	}

	public void buscarEstruturaDisponiveis() {
		this.estruturas = new ArrayList<Estrutura>();
		for (ParametroVisita p : this.getParametros()) {
			for (ParametroVisitaEstrutura pe : p.getEstruturas()) {
				if(pe.getEstrutura().getEstruturaPai() == null){
					this.estruturaSelecionada = pe.getEstrutura().getId();
				}
				this.estruturas.add(pe.getEstrutura());
			}
		}

		List<Estrutura> sorted = this.estruturas.stream().sorted(Comparator.comparing(Estrutura::getId))
				.collect(Collectors.toList());

		List<Estrutura> result = new ArrayList<Estrutura>();
		long anterior = -1;

		for (Estrutura e : sorted) {
			if (anterior != e.getId()) {
				result.add(e);
				anterior = e.getId();
			}
		}

		this.buscarEstruturaComFilho();

		this.estruturas = result;
	}

	private void buscarEstruturaComFilho(){

		List<Estrutura> estuturas = new ArrayList<>();
		Estrutura estrutura = new Estrutura();
		estrutura.setId(this.estruturaSelecionada);
		this.estruturaService.buscarEstruturasFilhos(estrutura,estuturas);

		this.filtro.getIdsEstruturas().clear();
		estuturas.forEach( e -> this.filtro.getIdsEstruturas().add(e.getId()));
	}

}
