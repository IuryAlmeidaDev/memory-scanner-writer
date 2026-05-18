package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.dao.CardapioDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroGerarPedido;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.exception.MultiplosResultadosException;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.DietaEspecialPedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.ItemPedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.DetalheItemEstruturaCardapioDTO;
import br.gov.mt.sejudh.sigepen.model.dto.GerarPedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RefeicaoDetalhadaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.ContratoService;
import br.gov.mt.sejudh.sigepen.service.EstruturaService;
import br.gov.mt.sejudh.sigepen.service.PedidoAlimentoService;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.net.Severity;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarGerarPedido", pattern = "/gerarPedido", viewId = "/WEB-INF/pages/gerarPedido/ListarGerarPedido.xhtml"),
		@URLMapping(id = "VisualizarGerarPedido", pattern = "/visualizar/gerarPedido/unidade/#{id}", viewId = "/WEB-INF/pages/gerarPedido/ListarGerarPedidoEstrutura.xhtml") })
public class GerarPedidoBean extends MetodosDePaginaBean {

	private FiltroUnidadePrisional filtro;
	private String dataFormatada;
	private FiltroGerarPedido filtroPedido;
	private List<TipoRefeicao> tiposRefeicao;
	private UnidadePrisional unidade = new UnidadePrisional();
	private LazyDataModel<UnidadePrisionalDTO> unidades;
	private List<String> nomesUnidadesFiltro = new ArrayList<>();
	private List<String> siglasUnidadesFiltro = new ArrayList<>();
	private DefaultTreeNode root = new DefaultTreeNode();
	private Estrutura estrutura;
	private Long unidadeSelecionada;
	private GerarPedidoEstruturaDTO gerarPedidoEstruturaDTO;
	private Contrato contrato;
	private boolean paginacaoConfigurada = false;
	private HorarioRefeicao horarioRefeicao;
	private static final int LIMITE_OPCOES_FILTRO_UNIDADE = 100;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	@Inject
	private SecurityUtil security;

	@Inject
	private TipoRefeicaoService tipoRefeicaoService;

	@Inject
	private ContratoService contratoService;

	@Inject
	private EstruturaService estruturaService;

	@Inject
	private UnidadePrisionalService unidadeService;

	@Inject
	private PedidoAlimentoService pedidoAlimentoService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private CardapioDAO cardapioDAO;

	@Inject
	private Logger log;

	public String getDataFormatada() {
		return this.dataFormatada;
	}

	public void setDataFormatada(String dataFormatada) {
		this.dataFormatada = dataFormatada;
	}

	public FiltroUnidadePrisional getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroUnidadePrisional filtro) {
		this.filtro = filtro;
	}

	public FiltroGerarPedido getFiltroPedido() {
		return filtroPedido;
	}

	public void setFiltroPedido(FiltroGerarPedido filtroPedido) {
		this.filtroPedido = filtroPedido;
	}

	public UnidadePrisional getUnidade() {
		return this.unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public Estrutura getEstrutura() {
		return this.estrutura;
	}

	public DefaultTreeNode getRoot() {
		return this.root;
	}

	public LazyDataModel<UnidadePrisionalDTO> getUnidades() {
		return this.unidades;
	}

	public List<TipoRefeicao> getTiposRefeicao() {
		return this.tiposRefeicao;
	}

	public List<String> getNomesUnidadesFiltro() {
		return nomesUnidadesFiltro;
	}

	public List<String> getSiglasUnidadesFiltro() {
		return siglasUnidadesFiltro;
	}

	public Long getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public HorarioRefeicao getHorarioRefeicao() {
		return horarioRefeicao;
	}

	public void setHorarioRefeicao(HorarioRefeicao horarioRefeicao) {
		this.horarioRefeicao = horarioRefeicao;
	}

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		this.filtroPedido = new FiltroGerarPedido();
		if (getAcaoDeTela().equals(AcaoDeTela.ALTERAR) || getAcaoDeTela().equals(AcaoDeTela.VISUALIZAR)) {
			if (facesUtil.getQueryParameter("id") != null) {
				this.unidadeSelecionada = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
			}
		}
		if (!security.possuiPermissao("ListarTodasUnidades")) {
			UnidadePrisional unidadeSelecionadaSessao = SecurityUtil.getUnidadeSelecionada();
			this.setFiltro(new FiltroUnidadePrisional());
			if (unidadeSelecionadaSessao == null || unidadeSelecionadaSessao.getId() <= 0) {
				return;
			}
			this.filtro.setId(unidadeSelecionadaSessao.getId());

			super.carregarFiltroPaginacao(FiltroUnidadePrisional.class, this, getFiltro());
		} else {
			super.carregarFiltroPaginacao(FiltroUnidadePrisional.class, this);
		}
	}

	@URLAction(onPostback = false)
	public String init() {
		this.tiposRefeicao = tipoRefeicaoService.listarTodos();
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				break;
			case VISUALIZAR:
			case ALTERAR:

				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

				if (!buscarUnidade(id)) {
					facesUtil.exibirMensagemErro("Unidade prisional não encontrada.", true);
					return "pretty:ListarUnidade";
				}

				break;

			case LISTAR:
				carregarOpcoesFiltroUnidades();
				this.pesquisar();
				logDeAcesso(log, "Gerar Pedido");
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

		this.unidade = unidadeService.buscaPorId(id);

		return this.unidade != null;
	}

	private void carregarOpcoesFiltroUnidades() {
		FiltroUnidadePrisional filtroOpcoes = new FiltroUnidadePrisional();
		if (!security.possuiPermissao("ListarTodasUnidades")) {
			UnidadePrisional unidadeSelecionadaSessao = SecurityUtil.getUnidadeSelecionada();
			if (unidadeSelecionadaSessao == null || unidadeSelecionadaSessao.getId() <= 0) {
				this.nomesUnidadesFiltro = new ArrayList<>();
				this.siglasUnidadesFiltro = new ArrayList<>();
				return;
			}
			filtroOpcoes.setId(unidadeSelecionadaSessao.getId());
		}

		this.nomesUnidadesFiltro = unidadeService.listarNomesUnidadesComContratosAtivos(filtroOpcoes, "",
				LIMITE_OPCOES_FILTRO_UNIDADE);
		this.siglasUnidadesFiltro = unidadeService.listarSiglasUnidadesComContratosAtivos(filtroOpcoes, "",
				LIMITE_OPCOES_FILTRO_UNIDADE);
	}

	private String montarFiltroData() {
		StringBuilder filtroDataHorario = new StringBuilder();

		if (this.filtroPedido != null) {
			HorarioRefeicao horarioEntrega = contratoService.buscarHorarioEntregaVigentePorUnidade(
					this.unidade.getId(),
					this.filtroPedido.getTipoRefeicao().getId(),
					this.filtroPedido.getDataInicio());

			if (horarioEntrega == null || horarioEntrega.getHorarioInicio() == null) {
				return null;
			}

			LocalDate dataPedido = Instant.ofEpochMilli(this.filtroPedido.getDataInicio().getTime())
					.atZone(ZoneId.systemDefault())
					.toLocalDate();
			LocalTime horarioPedido = LocalTime.parse(horarioEntrega.getHorarioInicio(), DateTimeFormatter.ofPattern("HHmm"))
					.withSecond(0)
					.withNano(0);

			String filtroData = dataPedido.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

			filtroDataHorario.append(filtroData.trim());
			filtroDataHorario.append(" ");
			filtroDataHorario.append(horarioPedido.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
		}

		this.dataFormatada = filtroDataHorario.toString().trim();
		return this.dataFormatada;
	}

	public void montarEstrutura() {
		root = new DefaultTreeNode();
		Long tipoRefeicao = this.filtroPedido.getTipoRefeicao().getId();
		if (this.unidade != null) {
			if (this.unidade.getId() > 0) {
				
				this.horarioRefeicao = estruturaService.buscarConfiguracaoHorarioRefeicao(this.unidade.getId(), tipoRefeicao);				
				String dataFormatada = montarFiltroData();
				
				if (dataFormatada == null || dataFormatada.isEmpty()) {
	                facesUtil.exibirMensagem(SEVERITY_ERROR, "Erro",
	                        "Não existe cardápio cadastrado para esta data e tipo de refeição");
	                estrutura = new Estrutura();
	                return;
	            }
				
				try {
					validarHorarioRefeicao(tipoRefeicao);
					
					this.contrato = contratoService.buscarContratoVigenteEAtivoPorUnidade(this.unidade.getId(),
							this.filtroPedido.getDataInicio());

					boolean existeCardapio = cardapioDAO.existeCardapioPara(dataFormatada, tipoRefeicao,
							contrato.getId(), this.unidade.getId());

					if (existeCardapio) {
						root = new DefaultTreeNode(new GerarPedidoEstruturaDTO(), null);
						root.setExpanded(true);
						estruturaService.montarArvoreEstruturaGerarPedido(
								GerarPedidoEstruturaDTO.from(unidade.getEstrutura()), root, dataFormatada,
								tipoRefeicao);
						estruturaService.capacidadeTotalEstruturaGerarPedido(root);
						estruturaService.capacidadeTotalDietaPadrao(root);
						estruturaService.capacidadeTotalDietaEspecial(root);
						GerarPedidoEstruturaDTO e = (GerarPedidoEstruturaDTO) root.getData();
						this.unidade.setCapacidadeTotal(e.getCapacidadeTotal());
					} else {
						facesUtil.exibirMensagem(SEVERITY_ERROR, "Erro",
								"Não existe cardápio cadastrado para esta data e tipo de refeição");
						root = new DefaultTreeNode();
					}
				} catch (ValidacaoException e) {
				    facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Regra de Negócio", e.getMessage());
				    root = new DefaultTreeNode(); 
				} catch (Exception e) {
				    facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro Inesperado", e.getMessage());
				}
			}
		}
		estrutura = new Estrutura();
	}

	public void pesquisar() {
		paginacaoConfigurada = false;
		this.unidades = new LazyDataModel<UnidadePrisionalDTO>() {
			private static final long serialVersionUID = 1L;

			public List<UnidadePrisionalDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				try {
					if(!paginacaoConfigurada) {
						configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
						paginacaoConfigurada = true;
					}
					
				} catch (Exception e) {
					log.error(e);
				}
				
				setRowCount(unidadeService.quantidadeFiltradosUnidadesComContratosAtivos(getFiltro()));
				List<UnidadePrisionalDTO> UnidadePrisionalDTO = unidadeService
						.buscarPorFiltroUnidadesComContratosAtivos(getFiltro(), false);

				return UnidadePrisionalDTO;
			}
		};
	}

	public void selecionarEstruturaChamada(GerarPedidoEstruturaDTO estrutura, String data,
			FiltroGerarPedido filtroGerarPedido, boolean assinar) {

		facesUtil.setObjetoSessao("estrutura", estrutura);
		facesUtil.setObjetoSessao("tipo_relatorio", "estrutura");

		boolean validaOk = validaDataPedido(filtroGerarPedido.getDataInicio());
		if (!validaOk)
			return;

		if (assinar) {
			boolean pedidoAtivoExistente = pedidoAlimentoService.verificarExistenciaPedidoAtivo(data,
					filtroGerarPedido.getTipoRefeicao().getId(), this.unidade.getId(), contrato.getId());

			if (pedidoAtivoExistente) {
				RequestContext.getCurrentInstance().execute("PF('confirmSubstituirPedidoDialog').show()");
			} else {
				this.imprimir(data, filtroGerarPedido, contrato, true);
			}
		} else {
			this.imprimir(data, filtroGerarPedido, contrato, false);
		}
	}

	public void confirmarSubstituicaoPedido() {
		if (!validaDataPedido(this.filtroPedido != null ? this.filtroPedido.getDataInicio() : null)) {
			RequestContext.getCurrentInstance().execute("PF('confirmSubstituirPedidoDialog').hide()");
			return;
		}
		this.imprimir(this.dataFormatada, this.filtroPedido, this.contrato, true);
		RequestContext.getCurrentInstance().execute("PF('confirmSubstituirPedidoDialog').hide()");
	}

	@Transactional
	private void imprimir(final String data, final FiltroGerarPedido filtroGerarPedido, Contrato contrato,
			boolean assinado) {
		try {
			validarHorarioRefeicao(filtroGerarPedido.getTipoRefeicao().getId());
			configurarDadosRelatorio();
			log.info("Geracao manual disparada pelo bean. unidade=" + this.unidade.getId()
					+ ", refeicao=" + filtroGerarPedido.getTipoRefeicao().getId()
					+ ", dataPedido=" + data
					+ ", contrato=" + (contrato != null ? contrato.getId() : null)
					+ ", assinado=" + assinado);

			PedidoAlimentoService.PedidoGerado pedidoGerado = pedidoAlimentoService.gerarPedidoManual(
					this.unidade,
					contrato,
					filtroGerarPedido.getTipoRefeicao(),
					data,
					SecurityUtil.getUsuario().getId(),
					assinado);

			PedidoAlimentoService.PedidoMontado pedidoMontado = pedidoGerado.getPedidoMontado();

			final List<DetalheItemEstruturaCardapioDTO> itemCardapioPorEstrutura = pedidoMontado.getItens();
			final List<DietaEspecialEstruturaDTO> refeicoesEspeciais = pedidoMontado.getDietasEspeciais();

			int totalRecuperandoDieta = calcularTotalGeralRecuperando(itemCardapioPorEstrutura, refeicoesEspeciais);

			final Map<String, Object> parameters = criarParametrosRelatorio(data, filtroGerarPedido, refeicoesEspeciais,
					contrato);
			parameters.put("TOTAL_RECUPERANDO", totalRecuperandoDieta);

			if (assinado) {
				parameters.put("EMISSOR", SecurityUtil.getMatriculaUsuario());
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "Info", "Relatório assinado com sucesso!");
				gerarRelatorioUnificado(itemCardapioPorEstrutura, Relatorio.PedidoDetalhadoAssinado, parameters);
			} else {
				gerarRelatorioUnificado(itemCardapioPorEstrutura, Relatorio.PedidoDetalhado, parameters);
			}

			LoggerUtil.logInfo(Acao.IMPRIMIR, "Gerar Pedido", log);

		} catch (Exception e) {
			tratarErroGeracaoRelatorio(e);
		}
	}

	public void validarHorarioRefeicao(Long tipoRefeicaoId) throws ValidacaoException {
		if (this.unidade == null || this.unidade.getId() <= 0) {
			return;
		}

		if (this.filtroPedido == null || this.filtroPedido.getDataInicio() == null) {
			return;
		}

		HorarioRefeicao horarioEntrega = contratoService.buscarHorarioEntregaVigentePorUnidade(
				this.unidade.getId(),
				tipoRefeicaoId,
				this.filtroPedido.getDataInicio());

		if (horarioEntrega == null || horarioEntrega.getHorarioInicio() == null) {
			return;
		}

		this.horarioRefeicao = horarioEntrega;

		LocalDate dataPedidoAlvo = Instant.ofEpochMilli(this.filtroPedido.getDataInicio().getTime())
				.atZone(ZoneId.systemDefault())
				.toLocalDate();

		List<HorarioRefeicao> horariosVigentes = buscarHorariosVigentesParaData(this.unidade.getId(), this.filtroPedido.getDataInicio());
		HorarioRefeicao horarioAnterior = resolverHorarioAnterior(horariosVigentes, tipoRefeicaoId);

		LocalTime horaAnterior = LocalTime.parse(horarioAnterior.getHorarioInicio(), DateTimeFormatter.ofPattern("HHmm"));
		LocalTime horaAlvo = LocalTime.parse(horarioEntrega.getHorarioInicio(), DateTimeFormatter.ofPattern("HHmm"));
		LocalDate dataRefeicaoAnterior = horaAnterior.isBefore(horaAlvo) ? dataPedidoAlvo : dataPedidoAlvo.minusDays(1);
		java.time.LocalDateTime referencia = java.time.LocalDateTime.now(ZoneId.of("America/Cuiaba"));

		log.info("Validacao janela manual. unidade=" + this.unidade.getId()
				+ ", tipoAlvo=" + tipoRefeicaoId
				+ ", horarioAnterior=" + horarioAnterior.getHorarioInicio()
				+ ", horarioAlvo=" + horarioEntrega.getHorarioInicio()
				+ ", referencia=" + referencia);

		pedidoAlimentoService.validarGeracaoDentroDaJanelaOperacional(
				horarioAnterior,
				dataRefeicaoAnterior,
				horarioEntrega,
				dataPedidoAlvo,
				2,
				referencia);
	}

	private List<HorarioRefeicao> buscarHorariosVigentesParaData(Long unidadeId, Date dataReferencia) {
		List<HorarioRefeicao> horarios = new ArrayList<>();
		List<TipoRefeicao> tipos = this.tiposRefeicao != null ? this.tiposRefeicao : tipoRefeicaoService.listarTodos();
		for (TipoRefeicao tipo : tipos) {
			HorarioRefeicao horario = contratoService.buscarHorarioEntregaVigentePorUnidade(unidadeId, tipo.getId(), dataReferencia);
			if (horario != null && horario.getHorarioInicio() != null) {
				horarios.add(horario);
			}
		}

		horarios.sort(Comparator.comparing(h -> LocalTime.parse(h.getHorarioInicio(), DateTimeFormatter.ofPattern("HHmm"))));
		return horarios;
	}

	private HorarioRefeicao resolverHorarioAnterior(List<HorarioRefeicao> horarios, Long tipoRefeicaoId) throws ValidacaoException {
		if (horarios == null || horarios.isEmpty()) {
			throw new ValidacaoException("Nao existem horarios vigentes para calcular a janela operacional.");
		}

		int indiceAlvo = -1;
		for (int i = 0; i < horarios.size(); i++) {
			HorarioRefeicao horario = horarios.get(i);
			if (horario.getTipoRefeicao() != null && horario.getTipoRefeicao().getId() != null
					&& horario.getTipoRefeicao().getId().equals(tipoRefeicaoId)) {
				indiceAlvo = i;
				break;
			}
		}

		if (indiceAlvo < 0) {
			throw new ValidacaoException("Nao foi possivel localizar o horario vigente da refeicao selecionada.");
		}

		int indiceAnterior = indiceAlvo == 0 ? horarios.size() - 1 : indiceAlvo - 1;
		return horarios.get(indiceAnterior);
	}

	private int calcularTotalDietaComum(List<DetalheItemEstruturaCardapioDTO> itens) {
		if (itens == null)
			return 0;

		return itens.stream().mapToInt(DetalheItemEstruturaCardapioDTO::getQtdRecuperando).distinct().sum();
	}

	private int calcularTotalDietaEspecial(List<DietaEspecialEstruturaDTO> dietas) {
		if (dietas == null)
			return 0;

		return dietas.stream().mapToInt(DietaEspecialEstruturaDTO::getQtdRecuperando).sum();
	}

	private int calcularTotalGeralRecuperando(List<DetalheItemEstruturaCardapioDTO> itens,
			List<DietaEspecialEstruturaDTO> dietas) {
		return calcularTotalDietaComum(itens) + calcularTotalDietaEspecial(dietas);
	}

	private boolean validaDataPedido(Date dataPedido) {
		try {
			if (dataPedido == null) {
				facesUtil.exibirMensagem(SEVERITY_ERROR, "Erro", "Selecione a data para gerar o pedido.");
				return false;
			}

			LocalDate hoje = LocalDate.now();
			LocalDate amanha = hoje.plusDays(1);
			LocalDate dataSelecionada = Instant.ofEpochMilli(dataPedido.getTime()).atZone(ZoneId.systemDefault())
					.toLocalDate();

			if (dataSelecionada.isBefore(hoje)) {
				facesUtil.exibirMensagem(SEVERITY_ERROR, "Atenção",
						"Não é possível emitir/substituir um pedido retroativamente.");
				return false;
			}

			if (dataSelecionada.isAfter(amanha)) {
				facesUtil.exibirMensagem(SEVERITY_ERROR, "Atenção",
						"Não é possível gerar um pedido com uma antecedência superior a 1 dia.");
				return false;
			}

			return true;
		} catch (Exception e) {
			facesUtil.exibirMensagem(SEVERITY_ERROR, "Erro", "Ocorreu um erro ao validar a data informada.");
			return false;
		}
	}

	private List<PedidoEstruturaDTO> getEstruturasDeNivel2(List<PedidoEstruturaDTO> estruturaPedido) {
		return estruturaPedido.stream().filter(estrutura -> estrutura.getNivel() == 2).collect(Collectors.toList());
	}

	private <T> Map<String, Object> criarParametrosRelatorio(String data, FiltroGerarPedido filtroGerarPedido,
			List<T> refeicoesEspeciais, Contrato contrato) {
		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("DATA", data);
		parameters.put("MENSAGEM", getMensagemCabecalhoQuantitativo(filtroGerarPedido, data, contrato));
		parameters.put("DIETA_ESPECIAL_POR_ESTRUTURA", refeicoesEspeciais);

		return parameters;
	}

	private <T> void gerarRelatorioUnificado(List<T> estruturaAgrupada, Relatorio relatorio,
			Map<String, Object> parameters) throws JRException {
		Arquivo relatorioGerado = RelatorioUtil.gerarRelatorio(estruturaAgrupada, relatorio, parameters);
		fileDownload.prepararArquivo(relatorioGerado);
		RequestContext.getCurrentInstance().execute("download()");
	}

	private void tratarErroGeracaoRelatorio(Exception e) {
		String message = "Erro ao gerar relatorio.";
		if (e.getCause() != null && e.getCause().getMessage() != null && !e.getCause().getMessage().trim().isEmpty()) {
			message = e.getCause().getMessage();
		} else if (e.getMessage() != null && !e.getMessage().trim().isEmpty()) {
			message = e.getMessage();
			int separador = message.indexOf(":");
			if (separador >= 0 && separador + 1 < message.length()) {
				message = message.substring(separador + 1).trim();
			}
		}
		facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", message);
		LoggerUtil.logErro(Acao.IMPRIMIR, "Gerar Pedido", e, log);
	}

	private <T> void validarCardapio(List<T> cardapios) throws Exception {
		if (cardapios.isEmpty()) {
			throw new Exception("Não existe um cardápio para esse tipo de refeição");
		}
	}

	private String getMensagemCabecalhoQuantitativo(FiltroGerarPedido filtroGerarPedido, String data,
			Contrato contrato) {
		String refeicao = filtroGerarPedido.getTipoRefeicao().getDescricao();
		return gerarMensagemCabecalho(refeicao, data, contrato);
	}

	private String gerarMensagemCabecalho(String refeicao, String data, Contrato contrato) {
		return "Solicito a empresa " + contrato.getFornecedor().getNomeFantasia() + ", cadastrada no CNPJ nº "
				+ contrato.getFornecedor().getCnpj() + ", o fornecimento do " + refeicao + " para o dia " + data
				+ ", para a " + this.unidade.getNome() + " conforme cardápios relacionados abaixo:";
	}

	private void configurarDadosRelatorio() {
		this.gerarPedidoEstruturaDTO = (GerarPedidoEstruturaDTO) facesUtil.getObjetoSessao("estrutura");
	}

	public boolean isUnidadePrisional(GerarPedidoEstruturaDTO estrutura) {
		return this.unidade.getEstrutura().getId() == estrutura.getId();
	}

	public void substituicaoPedido() {
		imprimir(dataFormatada, filtroPedido, contrato, true);
	}

	public void naoSubstituirPedido() {
		imprimir(dataFormatada, filtroPedido, contrato, false);
	}

}

