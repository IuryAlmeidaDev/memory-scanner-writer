package br.gov.mt.sejudh.sigepen.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.model.dto.PedidoConsultaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoEstruturaDetalhadoDTO;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.CardapioPedidoDTO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroGerarPedido;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.SituacaoEstruturaUnidade;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.GerarPedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoDetalhadoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.ApostilamentoService;
import br.gov.mt.sejudh.sigepen.service.ContratoService;
import br.gov.mt.sejudh.sigepen.service.EstruturaService;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

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
	private SituacaoEstruturaUnidade situacaoEstruturaUnidade;
	private Long unidadeSelecionada;
	private String tipoRelatorio;
	private UnidadePrisionalDTO unidadeDTO;
	private GerarPedidoEstruturaDTO gerarPedidoEstruturaDTO;

	@Inject
	private ApostilamentoService apostilamentoService;

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

	public SituacaoEstruturaUnidade getSituacaoEstruturaUnidade() {
		return this.situacaoEstruturaUnidade;
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
				estruturaService.montarArvoreEstruturaGerarPedido(this.toDto(unidade.getEstrutura()), root,
						montarFiltroData(), tipoRefeicao);
				estruturaService.capacidadeTotalEstruturaGerarPedido(root);
				estruturaService.capacidadeTotalDietaPadrao(root);
				estruturaService.capacidadeTotalDietaEspecial(root);
				GerarPedidoEstruturaDTO e = (GerarPedidoEstruturaDTO) root.getData();
				this.unidade.setCapacidadeTotal(e.getCapacidadeTotal());
			}
		}

		estrutura = new Estrutura();
		situacaoEstruturaUnidade = new SituacaoEstruturaUnidade();
	}

	private GerarPedidoEstruturaDTO toDto(Estrutura estrutura) {
		GerarPedidoEstruturaDTO dto = new GerarPedidoEstruturaDTO();
		dto.setNome(estrutura.getNome());
		dto.setCela(estrutura.getCela());
		dto.setId(estrutura.getId());
		return dto;
	}

	public void estruturaRelacionada(Estrutura estrutura) {
		this.situacaoEstruturaUnidade = new SituacaoEstruturaUnidade();
		this.estrutura = estrutura;
		this.situacaoEstruturaUnidade.setEstrutura(estrutura);
		this.situacaoEstruturaUnidade.setUnidadePrisional(estrutura.getUnidadePrisional());
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
					e.printStackTrace();
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
		this.imprimirPedidoUnificado(data, tipoRefeicao);
	}

	public void selecionarEstruturaChamada(GerarPedidoEstruturaDTO estrutura,String data, FiltroGerarPedido tipoRefeicao) {
		facesUtil.setObjetoSessao("estrutura", estrutura);
		facesUtil.setObjetoSessao("tipo_relatorio", "estrutura");
		this.imprimirPedidoUnificado(data, tipoRefeicao);
	}

	public void imprimirPedidoUnificado(final String data, final FiltroGerarPedido filtroGerarPedido) {
		try {
			configurarDadosRelatorio();
			final long idEstrutura = gerarPedidoEstruturaDTO.getId();
			final String unidadePrisionalSigla = this.unidade.getSigla();

			final Map<String, Object> parameters = new HashMap<>();
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			parameters.put("DATA", data);
			parameters.put("ID_ESTRUTURA", idEstrutura);

			// Obter a estrutura e filtrar níveis
			final List<PedidoEstruturaDTO> estruturaPedido = estruturaService.montarEstruturaPedido(data, idEstrutura);
			final List<PedidoEstruturaDTO> estruturaAteNivel2 = estruturaPedido.stream()
					.filter(obj -> obj.getNivel() <= 2)
					.collect(Collectors.toList());

			// Calcular totais e preparar a lista detalhada de pedidos
			final Integer totalDietasPadrao = calcularTotalDietaPadrao(estruturaPedido);
			final List<PedidoConsultaDTO> pedidosConsulta = estruturaService.buscaPedidoConsulta(data, idEstrutura, filtroGerarPedido.getTipoRefeicao());
			final List<PedidoEstruturaDetalhadoDTO> pedidosPorEstrutura = PedidoEstruturaDetalhadoDTO.combinarPedidos(estruturaAteNivel2, pedidosConsulta, unidadePrisionalSigla);
			this.validarCardapio(pedidosPorEstrutura);

			// Definir parâmetros para o relatório
			parameters.put("NOME_RELATORIO_DETALHADO", "Pedido detalhado de Alimentação");
			parameters.put("PEDIDO_ESTRUTURA_DETALHADO", pedidosPorEstrutura);
			parameters.put("TOTAL_DIETA_PADRAO", totalDietasPadrao);

			// Validar unidade e gerar o relatório
			if (validacao(this.unidade.getId())) {
				final Contrato contrato = contratoService.buscarContratoPorUnidade(this.unidade.getId(), filtroPedido.getDataInicio());
				parameters.put("MENSAGEM", this.getMensagemCabecalhoQuantitativo(filtroGerarPedido, filtroPedido.getDataFormadata(), contrato));

				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(estruturaPedido, Relatorio.GerarPedidoUnificado, parameters));
				RequestContext.getCurrentInstance().execute("download()");
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Gerar Pedido", log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Gerar Pedido", e, log);
		}
	}


	private boolean validacao(long id) {
		Contrato contrato = contratoService.buscarContratoPorUnidade(id, this.filtroPedido.getDataInicio());
		if (contrato == null) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"O contrato está fora do período de vigência!");
			return false;
		}
		
		return true;
	}

	private <T> void validarCardapio(List<T> cardapios) throws Exception {
		if (cardapios.isEmpty()) {
			throw new Exception("Não existe um cardápio para esse tipo de refeição!");
		}
	}

	public void imprimirDetalhado(String data, FiltroGerarPedido tipoRefeicao) {
		try {
			configurarDadosRelatorio();

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			parameters.put("DATA", data);
			Long id = null;

			if (existeApostilamentoParaUnidade(this.unidade.getId()) && this.validacao(this.unidade.getId())) {
				switch (tipoRelatorio) {
				case "unidade":
					parameters.put("ID_ESTRUTURA", this.unidadeDTO.getIdEstrutura());
					id = this.unidadeDTO.getIdEstrutura();
					break;

				case "estrutura":
					parameters.put("ID_ESTRUTURA", gerarPedidoEstruturaDTO.getId());
					id = gerarPedidoEstruturaDTO.getId();
					break;
				}

				PedidoDetalhadoDTO pedido = estruturaService.buscaPedidoDetalhado(data, id, tipoRefeicao.getTipoRefeicao());
				this.validarCardapio(pedido.getCardapio());
				pedido.setQtdPadrao(gerarPedidoEstruturaDTO.getTotalDietaPadrao());
				pedido.setQtdEspecial(gerarPedidoEstruturaDTO.getTotalDietaEspecial());

				parameters.put("MENSAGEM",
						this.getMensagemCabecalhoDetalhado(pedido.getRefeicao(), filtroPedido.getDataFormadata()));

				fileDownload.prepararArquivo(
						RelatorioUtil.gerarRelatorio(pedido, Relatorio.PedidoDetalhado, parameters));
				RequestContext.getCurrentInstance().execute("download()");
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Gerar Pedido", log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Gerar Pedido", e, log);
		}
	}

	private String getMensagemCabecalhoQuantitativo(FiltroGerarPedido filtroGerarPedido, String data, Contrato contrato) {
		String refeicao = filtroGerarPedido.getTipoRefeicao().getDescricao();
		return gerarMensagemCabecalho(refeicao, data, contrato);
	}

	private String getMensagemCabecalhoDetalhado(String refeicao, String data) {
		Contrato contrato = contratoService.buscarContratoPorUnidade(this.unidade.getId(), filtroPedido.getDataInicio());
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
		this.unidadeDTO = (UnidadePrisionalDTO) facesUtil.getObjetoSessao("unidade");
		this.gerarPedidoEstruturaDTO = (GerarPedidoEstruturaDTO) facesUtil.getObjetoSessao("estrutura");
		this.tipoRelatorio = (String) facesUtil.getObjetoSessao("tipo_relatorio");
	}

	private boolean existeApostilamentoParaUnidade(Long idUnidade) {
		if (apostilamentoService.existeApostilamentoUnidade(idUnidade)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "É nescessário criar um apostilamento!");
			return false;
		}

		return true;
	}

	public Integer calcularTotalDietaPadrao(List<PedidoEstruturaDTO> pedidosEstrutura) {
		OptionalInt menorNivelOpt = pedidosEstrutura.stream()
				.mapToInt(PedidoEstruturaDTO::getNivel)
				.min();

		return menorNivelOpt.isPresent()
				? pedidosEstrutura.stream()
				.filter(pedidoEstrutura -> pedidoEstrutura.getNivel() == menorNivelOpt.getAsInt())
				.mapToInt(PedidoEstruturaDTO::getQtdPadrao)
				.sum()
				: 0;
	}

	public boolean isUnidadePrisional(GerarPedidoEstruturaDTO estrutura) {
		return this.unidade.getEstrutura().getId() == estrutura.getId();
	}
}
