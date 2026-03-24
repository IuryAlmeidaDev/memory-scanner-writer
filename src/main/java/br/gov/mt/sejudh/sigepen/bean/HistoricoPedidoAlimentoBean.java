package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroHistoricoPedidoAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoComboDTO;
import br.gov.mt.sejudh.sigepen.service.HistoricoPedidoAlimentoService;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.DietaEspecialPedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.dto.DetalheItemEstruturaCardapioDTO;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialEstruturaDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.ContratoService;
import org.primefaces.context.RequestContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarHistoricoPedidoAlimento", pattern = "/historicoPedidoAlimento", viewId = "/WEB-INF/pages/historicoPedidoAlimento/ListarHistoricoPedidoAlimento.xhtml"),
		@URLMapping(id = "VisualizarHistoricoPedidoAlimento", pattern = "/visualizar/historicoPedidoAlimento/#{id}", viewId = "/WEB-INF/pages/historicoPedidoAlimento/VisualizarHistoricoPedidoAlimento.xhtml") })
public class HistoricoPedidoAlimentoBean extends MetodosDePaginaBean {

	private FiltroHistoricoPedidoAlimento filtro;
	private LazyDataModel<PedidoAlimento> pedidos;
	private PedidoAlimento pedidoSelecionado;
	private Long idSelecionado;
	private List<ContratoComboDTO> contratos;
	private List<TipoRefeicao> tiposRefeicao;
	private Map<Long, String> descricoesRefeicao;
	private Map<Long, String> descricoesContrato;

	@Inject
	private HistoricoPedidoAlimentoService historicoPedidoAlimentoService;
	@Inject
	private TipoRefeicaoService tipoRefeicaoService;
	@Inject
	private FacesUtil facesUtil;
	@Inject
	private SecurityUtil security;
	@Inject
	private Logger log;
	@Inject
	private ContratoService contratoService;
	@Inject
	private FileDownloadHandlerBean fileDownload;

	public FiltroHistoricoPedidoAlimento getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroHistoricoPedidoAlimento filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<PedidoAlimento> getPedidos() {
		return pedidos;
	}

	public PedidoAlimento getPedidoSelecionado() {
		return pedidoSelecionado;
	}

	public void setPedidoSelecionado(PedidoAlimento pedidoSelecionado) {
		this.pedidoSelecionado = pedidoSelecionado;
	}

	public List<ContratoComboDTO> getContratos() {
		return contratos;
	}

	public List<TipoRefeicao> getTiposRefeicao() {
		return tiposRefeicao;
	}

	public Map<Long, String> getDescricoesRefeicao() {
		return descricoesRefeicao;
	}
	public Map<Long, String> getDescricoesContrato() {
	    return descricoesContrato;
	}

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		if (!security.possuiPermissao("ListarTodasUnidades")) {
			this.filtro = new FiltroHistoricoPedidoAlimento();
			this.filtro.setIdUnidadePrisional(SecurityUtil.getUnidadeSelecionada().getId());
			super.carregarFiltroPaginacao(FiltroHistoricoPedidoAlimento.class, this, getFiltro());
		} else {
			super.carregarFiltroPaginacao(FiltroHistoricoPedidoAlimento.class, this);
		}

		if (getAcaoDeTela().equals(AcaoDeTela.VISUALIZAR)) {
			String paramId = facesUtil.getQueryParameter("id");
			if (paramId != null) {
				this.idSelecionado = Sigepen.converteStringEmLong(paramId);
			}
		}
	}

	@URLAction(onPostback = false)
	public String init() {
		this.tiposRefeicao = tipoRefeicaoService.listarTodos();
		this.contratos = historicoPedidoAlimentoService.listarContratosParaCombo();
		this.descricoesRefeicao = this.tiposRefeicao.stream()
				.collect(Collectors.toMap(t -> t.getId(), t -> t.getDescricao()));
		this.descricoesContrato = this.contratos.stream()
	            .collect(Collectors.toMap(c -> c.getId(), c -> c.getNumContrato()));

		try {
			switch (getAcaoDeTela()) {
			case LISTAR:
				pesquisar();
				logDeAcesso(log, "HistoricoPedidoAlimento");
				break;

			case VISUALIZAR:
				if (idSelecionado != null && idSelecionado > 0) {
					this.pedidoSelecionado = historicoPedidoAlimentoService.buscarPorId(idSelecionado);
					if (this.pedidoSelecionado == null) {
						facesUtil.exibirMensagemErro("Pedido não encontrado.", true);
						return "pretty:ListarHistoricoPedidoAlimento";
					}
				}
				logDeAcesso(log, "HistoricoPedidoAlimento");
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

	public void pesquisar() {
		if (this.filtro == null) {
			this.filtro = new FiltroHistoricoPedidoAlimento();
		}
		this.pedidos = new LazyDataModel<PedidoAlimento>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<PedidoAlimento> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				try {
					configuraPaginacao(getRowCount() == 0 ? Integer.MAX_VALUE : getRowCount(), first, pageSize,
							sortField, sortOrder, filtro);
				} catch (Exception e) {
					log.error(e);
				}

				List<PedidoAlimento> resultado = historicoPedidoAlimentoService.buscarPorFiltroHistorico(filtro);

				setRowCount(historicoPedidoAlimentoService.contarPorFiltroHistorico(filtro));

				return resultado;
			}
		};
	}

	public void baixarRelatorio(Long idPedido, boolean ehAssinado) {
		try {
			PedidoAlimento pedido = historicoPedidoAlimentoService.buscarPorIdComItens(idPedido);

			if (pedido == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "Pedido não encontrado.");
				return;
			}

			List<DetalheItemEstruturaCardapioDTO> itensPedido = converterItens(pedido);

			if (itensPedido.isEmpty()) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "Não há itens para gerar o relatório.");
				return;
			}

			List<DietaEspecialEstruturaDTO> dietasEspeciais = converterDietas(pedido);
			Contrato contrato = contratoService.buscaPorId(pedido.getIdContrato());
			
			String nomeUnidadeHistorica = historicoPedidoAlimentoService.buscarNomeUnidadePorId(pedido.getIdUnidadePrisional());

			Map<String, Object> parameters = montarParametros(pedido, itensPedido, dietasEspeciais, contrato, nomeUnidadeHistorica);

			if (ehAssinado) {
				parameters.put("EMISSOR", SecurityUtil.getMatriculaUsuario());
				Arquivo relatorioGerado = RelatorioUtil.gerarRelatorio(itensPedido, Relatorio.PedidoDetalhadoAssinado,
						parameters);
				fileDownload.prepararArquivo(relatorioGerado);
				RequestContext.getCurrentInstance().execute("download()");
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Histórico Pedido Alimento", log);
			} else {
				Arquivo relatorioGerado = RelatorioUtil.gerarRelatorio(itensPedido, Relatorio.PedidoDetalhado,
						parameters);
				fileDownload.prepararArquivo(relatorioGerado);
				RequestContext.getCurrentInstance().execute("download()");
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Histórico Pedido Alimento", log);
			}

		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
					"Não foi possível gerar o relatório." + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	private List<DetalheItemEstruturaCardapioDTO> converterItens(PedidoAlimento pedido) {
		return pedido.getItensPedido().stream().filter(i -> i.getStatus() == Status.ATIVO)
				.map(i -> new DetalheItemEstruturaCardapioDTO(i.getItem(), i.getQtdPorItem(), i.getQtdRecuperando(),
						i.getSiglaUnidadeMedida(), i.getNomeEstrutura(), i.getNivelEstrutura(), i.getItemCardapioId(),
						i.getEstruturaId()))
				.collect(Collectors.toList());
	}

	private List<DietaEspecialEstruturaDTO> converterDietas(PedidoAlimento pedido) {
		return pedido.getDietasEspeciais().stream().filter(d -> d.getStatus() == Status.ATIVO).map(this::converterDieta)
				.collect(Collectors.toList());
	}

	private DietaEspecialEstruturaDTO converterDieta(DietaEspecialPedidoAlimento d) {
		Object[] row = new Object[] { null, BigDecimal.valueOf(d.getEstruturaId()), d.getNomeEstrutura(),
				d.getTipoDieta(), BigDecimal.valueOf(d.getQtdRecuperando()) };
		DietaEspecialEstruturaDTO dto = new DietaEspecialEstruturaDTO(row);
		dto.setNomesRecuperandos(d.getNomesRecuperandos() != null && !d.getNomesRecuperandos().isEmpty()
				? Arrays.asList(d.getNomesRecuperandos().split(","))
				: new ArrayList<>());
		return dto;
	}

	private Map<String, Object> montarParametros(PedidoAlimento pedido,
	        List<DetalheItemEstruturaCardapioDTO> itensPedido, 
	        List<DietaEspecialEstruturaDTO> dietasEspeciais,
	        Contrato contrato, String nomeUnidade) { // <--- Novo parâmetro

	    Map<String, Object> parameters = new HashMap<>();
	    parameters.put("DATA", pedido.getDataPedido());
	    parameters.put("DIETA_ESPECIAL_POR_ESTRUTURA", dietasEspeciais);
	    parameters.put("TOTAL_RECUPERANDO", calcularTotalRecuperando(itensPedido, dietasEspeciais));
	    parameters.put("MENSAGEM", montarMensagemCabecalho(pedido, contrato, nomeUnidade));
	    return parameters;
	}

	private String montarMensagemCabecalho(PedidoAlimento pedido, Contrato contrato, String nomeUnidade) {
	    return "Solicito a empresa " + contrato.getFornecedor().getNomeFantasia() + ", cadastrada no CNPJ nº "
	            + contrato.getFornecedor().getCnpj() + ", o fornecimento do "
	            + descricoesRefeicao.get(pedido.getIdTipoRefeicao()) + " para o dia " + pedido.getDataPedido()
	            + ", para a " + nomeUnidade 
	            + " conforme cardápios relacionados abaixo:";
	}

	private int calcularTotalRecuperando(List<DetalheItemEstruturaCardapioDTO> itensPedido,
			List<DietaEspecialEstruturaDTO> dietasEspeciais) {

		int totalDietaComum = itensPedido.stream().mapToInt(DetalheItemEstruturaCardapioDTO::getQtdRecuperando)
				.distinct().sum();

		int totalDietaEspecial = dietasEspeciais.stream().mapToInt(DietaEspecialEstruturaDTO::getQtdRecuperando).sum();

		return totalDietaComum + totalDietaEspecial;
	}

	public String visualizar(PedidoAlimento pedido) {
		return "pretty:VisualizarHistoricoPedidoAlimento?id=" + pedido.getId();
	}

	public void finalizar(Long idPedido) {
		try {
			historicoPedidoAlimentoService.finalizarPedido(idPedido);
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "Concluido", "Pedido finalizado com sucesso");
			pesquisar();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
					"Não foi possível finalizar o pedido." + e.getMessage());
		}
	}

}