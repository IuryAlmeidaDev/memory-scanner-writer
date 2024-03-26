package br.gov.mt.sejudh.sigepen.bean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

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

		if (this.unidade == null) {
			return false;
		}

		// this.montarEstrutura();

		return true;
	}

	private String montarFiltroData() {
		StringBuffer filtroDataHorario = new StringBuffer();
		if (this.filtroPedido != null) {
			String horarioRefeiracao = this.contratoService.buscarHorarioRefeiracao(this.unidade.getId(),
					this.filtroPedido.getTipoRefeicao().getId());
			StringBuffer horarioRefeiracaoFormatado = new StringBuffer(horarioRefeiracao);
			horarioRefeiracaoFormatado.insert(2, ":");
			String filtroData = filtroPedido.getDataFormadata();
			filtroDataHorario.append(filtroData);
			filtroDataHorario.append(" ");
			filtroDataHorario.append(horarioRefeiracaoFormatado.toString());
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

	public void imprimirPedidoUnificado(String data, FiltroGerarPedido tipoRefeicao) {
		try {
			configurarDadosRelatorio(true);

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			parameters.put("DATA", data);

			switch (tipoRelatorio) {
			case "unidade":
				parameters.put("ID_ESTRUTURA", this.unidadeDTO.getIdEstrutura());
				break;

			case "estrutura":
				parameters.put("ID_ESTRUTURA", gerarPedidoEstruturaDTO.getId());
				break;

			}
			List<PedidoDetalhadoDTO> lista = new ArrayList<>();
			PedidoDetalhadoDTO pedido = this.gerarPedidoDetalhado(data, tipoRefeicao);
			lista.add(pedido);
			parameters.put("PEDIDO_DETALHADO", lista);
			parameters.put("MENSAGEM_PEDIDO_DETALHADO",
					this.getMensagemCabecalhoDetalhado(pedido, filtroPedido.getDataFormadata()));

			if (this.validacao(this.unidade.getId())) {
				Contrato contrato = contratoService.buscarContratoPorUnidade(this.unidade.getId(), filtroPedido.getDataInicio());
				parameters.put("MENSAGEM",
						this.getMensagemCabecalhoQuantitativo(tipoRefeicao, filtroPedido.getDataFormadata(), contrato));

				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.GerarPedidoUnificado, parameters));
				RequestContext.getCurrentInstance().execute("download()");
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Gerar Pedido", log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Gerar Pedido", e, log);
		}
	}

	public void imprimirQuantitativo(String data, FiltroGerarPedido tipoRefeicao) {
		try {
			configurarDadosRelatorio(true);

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			parameters.put("DATA", data);

			switch (tipoRelatorio) {
			case "unidade":
				parameters.put("ID_ESTRUTURA", this.unidadeDTO.getIdEstrutura());
				break;

			case "estrutura":
				parameters.put("ID_ESTRUTURA", gerarPedidoEstruturaDTO.getId());
				break;

			}

			if (this.validacao(this.unidade.getId())) {
				Contrato contrato = contratoService.buscarContratoPorUnidade(this.unidade.getId(), filtroPedido.getDataInicio());
				parameters.put("MENSAGEM",
						this.getMensagemCabecalhoQuantitativo(tipoRefeicao, filtroPedido.getDataFormadata(), contrato));

				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.GerarPedido, parameters));
				RequestContext.getCurrentInstance().execute("download()");
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Gerar Pedido", log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Gerar Pedido", e, log);
		}
	}

	private boolean validacao(long id) throws ParseException {
		Contrato contrato = contratoService.buscarContratoPorUnidade(id, this.filtroPedido.getDataInicio());
		if (contrato == null) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"O contrato está fora do período de vigência!");
			return false;
		}
		
		return true;
	}

	private boolean validarCardapio(List<CardapioPedidoDTO> cardapios) throws Exception {
		if (cardapios.isEmpty()) {
//			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
//					"Não existe um cardápio para esse tipo de refeição!");
		throw new Exception("Não existe um cardápio para esse tipo de refeição!");
		}
		return true;
	}

	public void imprimirDetalhado(String data, FiltroGerarPedido tipoRefeicao) {
		try {
			configurarDadosRelatorio(true);

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			parameters.put("DATA", data);
			Long id = null;

			if (existeApostilamentoParaUniadade(this.unidade.getId()) && this.validacao(this.unidade.getId())) {
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

				PedidoDetalhadoDTO pedido = estruturaService.buscaPedidoDetalhado(data, id,
						tipoRefeicao.getTipoRefeicao());
				if (this.validarCardapio(pedido.getCardapio())) {
					pedido.setQtdPadrao(gerarPedidoEstruturaDTO.getTotalDietaPadrao());
					pedido.setQtdEspecial(gerarPedidoEstruturaDTO.getTotalDietaEspecial());

					parameters.put("MENSAGEM",
							this.getMensagemCabecalhoDetalhado(pedido, filtroPedido.getDataFormadata()));

					fileDownload.prepararArquivo(
							RelatorioUtil.gerarRelatorio(pedido, Relatorio.PedidoDetalhado, parameters));
					RequestContext.getCurrentInstance().execute("download()");
					LoggerUtil.logInfo(Acao.IMPRIMIR, "Gerar Pedido", log);
				}
			}

		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Gerar Pedido", e, log);
		}
	}

	public PedidoDetalhadoDTO gerarPedidoDetalhado(String data, FiltroGerarPedido tipoRefeicao) throws ParseException, Exception {
		PedidoDetalhadoDTO pedido = null;
		
		Long id = null;

		if (existeApostilamentoParaUniadade(this.unidade.getId()) && this.validacao(this.unidade.getId())) {
			switch (tipoRelatorio) {
			case "unidade":
				id = this.unidadeDTO.getIdEstrutura();
				break;

			case "estrutura":
				id = gerarPedidoEstruturaDTO.getId();
				break;
			}

			pedido = estruturaService.buscaPedidoDetalhado(data, id, tipoRefeicao.getTipoRefeicao());
			if (this.validarCardapio(pedido.getCardapio())) {
				pedido.setQtdPadrao(gerarPedidoEstruturaDTO.getTotalDietaPadrao());
				pedido.setQtdEspecial(gerarPedidoEstruturaDTO.getTotalDietaEspecial());
			}
		}
		return pedido;
	}

	private String getMensagemCabecalhoQuantitativo(FiltroGerarPedido filtroGerarPedido, String data,
			Contrato contrato) {
		StringBuilder msg = new StringBuilder("Solicito a empresa ");
		msg.append(contrato.getFornecedor().getNomeFantasia());
		msg.append(", cadastrada no CNPJ nº " + contrato.getFornecedor().getCnpj());
		msg.append(", o fornecimento do " + filtroGerarPedido.getTipoRefeicao().getDescricao());
		msg.append(" para o dia " + data);
		msg.append(", para a " + this.unidade.getNome());
		msg.append(" sendo assim relacionado.");
		return msg.toString();
	}

	private String getMensagemCabecalhoDetalhado(PedidoDetalhadoDTO pedido, String data) {
		Contrato contrato = contratoService.buscarContratoPorUnidade(this.unidade.getId(), filtroPedido.getDataInicio());
		StringBuilder msg = new StringBuilder("Solicito a empresa ");
		msg.append(contrato.getFornecedor().getNomeFantasia());
		msg.append(", cadastrada no CNPJ nº " + contrato.getFornecedor().getCnpj());
		msg.append(", o fornecimento do " + pedido.getRefeicao());
		msg.append(" para o dia " + data);
		msg.append(", para a " + this.unidade.getNome());
		msg.append(", com dieta Padrão " + pedido.getQtdPadrao());
		msg.append(" e dieta especial " + pedido.getQtdEspecial());
		msg.append(", sendo assim relacionado.");
		return msg.toString();
	}

	private void configurarDadosRelatorio(boolean gerarPedido) {
		this.unidadeDTO = (UnidadePrisionalDTO) facesUtil.getObjetoSessao("unidade");
		this.gerarPedidoEstruturaDTO = (GerarPedidoEstruturaDTO) facesUtil.getObjetoSessao("estrutura");
		this.tipoRelatorio = (String) facesUtil.getObjetoSessao("tipo_relatorio");
	}

	private boolean existeApostilamentoParaUniadade(Long idUnidade) {
		if (apostilamentoService.existeApostilamentoUnidade(idUnidade)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "É nescessário criar um apostilamento!");
			return false;
		}

		return true;
	}
}
