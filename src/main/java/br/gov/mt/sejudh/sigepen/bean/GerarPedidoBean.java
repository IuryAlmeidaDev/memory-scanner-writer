package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroGerarPedido;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
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
import com.google.gson.Gson;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	private DefaultTreeNode root = new DefaultTreeNode();
	private Estrutura estrutura;
    private Long unidadeSelecionada;
    private GerarPedidoEstruturaDTO gerarPedidoEstruturaDTO;

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

	public Long getUnidadeSelecionada() {
		return unidadeSelecionada;
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
			this.setFiltro(new FiltroUnidadePrisional());
			this.filtro.setId(SecurityUtil.getUnidadeSelecionada().getId());

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
				this.pesquisar();
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

	private boolean buscarUnidade(long id) {
		if (id <= 0) {
			return false;
		}

		this.unidade = unidadeService.buscaPorId(id);

        return this.unidade != null;
    }

	private String montarFiltroData() {
		StringBuilder filtroDataHorario = new StringBuilder();
		if (this.filtroPedido != null) {
			String horarioRefeiracao = this.contratoService.buscarHorarioRefeiracao(this.unidade.getId(),
					this.filtroPedido.getTipoRefeicao().getId());
			StringBuilder horarioRefeiracaoFormatado = new StringBuilder(horarioRefeiracao);
			horarioRefeiracaoFormatado.insert(2, ":");
			String filtroData = filtroPedido.getDataFormadata();
			filtroDataHorario.append(filtroData);
			filtroDataHorario.append(" ");
			filtroDataHorario.append(horarioRefeiracaoFormatado);
		}
		this.dataFormatada = filtroDataHorario.toString();
		return filtroDataHorario.toString();
	}

	public void montarEstrutura() {
		Long tipoRefeicao = this.filtroPedido.getTipoRefeicao().getId();
		if (this.unidade != null) {
			if (this.unidade.getId() > 0) {
				root = new DefaultTreeNode(new GerarPedidoEstruturaDTO(), null);
				root.setExpanded(true);
				estruturaService.montarArvoreEstruturaGerarPedido(
						GerarPedidoEstruturaDTO.from(unidade.getEstrutura()),
						root,
						montarFiltroData(),
						tipoRefeicao
				);
				estruturaService.capacidadeTotalEstruturaGerarPedido(root);
				estruturaService.capacidadeTotalDietaPadrao(root);
				estruturaService.capacidadeTotalDietaEspecial(root);
				GerarPedidoEstruturaDTO e = (GerarPedidoEstruturaDTO) root.getData();
				this.unidade.setCapacidadeTotal(e.getCapacidadeTotal());
			}
		}

		estrutura = new Estrutura();
	}

	public void pesquisar() {
		this.unidades = new LazyDataModel<UnidadePrisionalDTO>() {
			private static final long serialVersionUID = 1L;

			public List<UnidadePrisionalDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				List<UnidadePrisionalDTO> UnidadePrisionalDTO = unidadeService
						.buscarPorFiltroUnidadesComContratosAtivos(getFiltro(), false);

				setRowCount(UnidadePrisionalDTO.size());

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					log.error(e);
				}

				return UnidadePrisionalDTO;
			}
		};
	}

	public void selecionarEstruturaChamada(UnidadePrisionalDTO unidade) {
		facesUtil.setObjetoSessao("unidade", unidade);
		facesUtil.setObjetoSessao("tipo_relatorio", "unidade");
	}

	public void selecionarEstruturaChamada(GerarPedidoEstruturaDTO estrutura) {
		facesUtil.setObjetoSessao("estrutura", estrutura);
		facesUtil.setObjetoSessao("tipo_relatorio", "estrutura");
	}
	
	public void selecionarEstruturaChamada(UnidadePrisionalDTO unidade,String data, FiltroGerarPedido tipoRefeicao) {
		facesUtil.setObjetoSessao("unidade", unidade);
		facesUtil.setObjetoSessao("tipo_relatorio", "unidade");
		this.imprimir(data, tipoRefeicao);
	}

	public void selecionarEstruturaChamada(GerarPedidoEstruturaDTO estrutura,String data, FiltroGerarPedido tipoRefeicao) {
		facesUtil.setObjetoSessao("estrutura", estrutura);
		facesUtil.setObjetoSessao("tipo_relatorio", "estrutura");
		this.imprimir(data, tipoRefeicao);
	}

	public void imprimir(final String data, final FiltroGerarPedido filtroGerarPedido) {
		try {
			configurarDadosRelatorio();
			final Contrato contrato = contratoService.buscarContratoVigenteEAtivoPorUnidade(this.unidade.getId(), filtroGerarPedido.getDataInicio());

			final long idEstrutura = gerarPedidoEstruturaDTO.getId();

			final List<PedidoEstruturaDTO> estruturaPedido = estruturaService.montarEstruturaPedido(data, idEstrutura);
			final List<PedidoEstruturaDTO> estruturaNivel2 = getEstruturasDeNivel2(estruturaPedido);
			final List<RefeicaoDetalhadaDTO> refeicoesDetalhadas = estruturaService.buscaPedidoConsulta(
					data, idEstrutura, filtroGerarPedido.getTipoRefeicao());

			final List<DetalheItemEstruturaCardapioDTO> itemCardapioPorEstrutura = DetalheItemEstruturaCardapioDTO.combinarPedidos(
					estruturaNivel2, refeicoesDetalhadas);
			final List<DietaEspecialEstruturaDTO> refeicoesEspeciais = estruturaService.buscarDietasEspeciais(idEstrutura, data);

			validarCardapio(itemCardapioPorEstrutura);

			String jsonDadosRelatorio = criarJsonRelatorio(itemCardapioPorEstrutura, refeicoesEspeciais);
			PedidoAlimento pedidoAlimento = PedidoAlimento.create(
					data,
					SecurityUtil.getUsuario().getId(),
					idEstrutura, contrato.getId(),
					filtroGerarPedido.getTipoRefeicao().getId(),
					jsonDadosRelatorio
			);

			pedidoAlimentoService.salvarRegistroRelatorio(pedidoAlimento);

			// Parâmetros do relatório
			final Map<String, Object> parameters = criarParametrosRelatorio(data, filtroGerarPedido, refeicoesEspeciais, contrato);

			// Geração do relatório
			gerarRelatorioUnificado(itemCardapioPorEstrutura, parameters);
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Gerar Pedido", log);
		} catch (Exception e) {
			tratarErroGeracaoRelatorio(e);
		}
	}

	private String criarJsonRelatorio(List<DetalheItemEstruturaCardapioDTO> itemCardapioPorEstrutura, List<DietaEspecialEstruturaDTO> refeicoesEspeciais) {
		final Map<String, Object> dadosRelatorio = new HashMap<>();
		dadosRelatorio.put("itensCardapio", itemCardapioPorEstrutura);
		dadosRelatorio.put("refeicoesEspeciais", refeicoesEspeciais);
		final Gson gson = new Gson();
		return gson.toJson(dadosRelatorio);
	}

	private List<PedidoEstruturaDTO> getEstruturasDeNivel2(List<PedidoEstruturaDTO> estruturaPedido) {
		return estruturaPedido.stream()
				.filter(estrutura -> estrutura.getNivel() == 2)
				.collect(Collectors.toList());
	}

	private <T> Map<String, Object> criarParametrosRelatorio(String data, FiltroGerarPedido filtroGerarPedido, List<T> refeicoesEspeciais, Contrato contrato) {
		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("DATA", data);
		parameters.put("MENSAGEM", getMensagemCabecalhoQuantitativo(filtroGerarPedido, data, contrato));
		parameters.put("DIETA_ESPECIAL_POR_ESTRUTURA", refeicoesEspeciais);

		return parameters;
	}

	private <T> void gerarRelatorioUnificado(List<T> estruturaAgrupada, Map<String, Object> parameters) throws JRException {
		Arquivo relatorioGerado = RelatorioUtil.gerarRelatorio(estruturaAgrupada, Relatorio.PedidoDetalhado, parameters);
		fileDownload.prepararArquivo(relatorioGerado);
		RequestContext.getCurrentInstance().execute("download()");
	}

	private void tratarErroGeracaoRelatorio(Exception e) {
		String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage().substring(e.getMessage().indexOf(":") + 1);
		facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", message);
		LoggerUtil.logErro(Acao.IMPRIMIR, "Gerar Pedido", e, log);
	}

	private <T> void validarCardapio(List<T> cardapios) throws Exception {
		if (cardapios.isEmpty()) {
			throw new Exception("Não existe um cardápio para esse tipo de refeição");
		}
	}

	private String getMensagemCabecalhoQuantitativo(FiltroGerarPedido filtroGerarPedido, String data, Contrato contrato) {
		String refeicao = filtroGerarPedido.getTipoRefeicao().getDescricao();
		return gerarMensagemCabecalho(refeicao, data, contrato);
	}

	private String gerarMensagemCabecalho(String refeicao, String data, Contrato contrato) {
		return "Solicito a empresa " + contrato.getFornecedor().getNomeFantasia() +
				", cadastrada no CNPJ nº " + contrato.getFornecedor().getCnpj() +
				", o fornecimento do " + refeicao +
				" para o dia " + data +
				", para a " + this.unidade.getNome() +
				" conforme cardápios relacionados abaixo:";
	}

	private void configurarDadosRelatorio() {
        this.gerarPedidoEstruturaDTO = (GerarPedidoEstruturaDTO) facesUtil.getObjetoSessao("estrutura");
	}

	public boolean isUnidadePrisional(GerarPedidoEstruturaDTO estrutura) {
		return this.unidade.getEstrutura().getId() == estrutura.getId();
	}
}
