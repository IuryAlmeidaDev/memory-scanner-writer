package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.dao.CardapioDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroGerarPedido;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.exception.MultiplosResultadosException;
import br.gov.mt.sejudh.sigepen.exception.NaoEncontradoException;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.DietaEspecialPedidoAlimento;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
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
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
	private DefaultTreeNode root = new DefaultTreeNode();
	private Estrutura estrutura;
    private Long unidadeSelecionada;
    private GerarPedidoEstruturaDTO gerarPedidoEstruturaDTO;
	private Contrato contrato;

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

	public Long getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
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
	        // 1. Busca o horário (ex: "1405")
	        String horarioRefeiracao = this.contratoService.buscarHorarioRefeiracao(
	                this.unidade.getId(),
	                this.filtroPedido.getTipoRefeicao().getId());
	        
	        // 2. Garante que pegamos apenas os números e apenas os 4 primeiros dígitos (HHmm)
	        String horaLimpa = horarioRefeiracao.replaceAll("[^0-9]", "");
	        if (horaLimpa.length() > 4) {
	            horaLimpa = horaLimpa.substring(0, 4);
	        }

	        // 3. Formata para HH:mm
	        StringBuilder horarioFormatado = new StringBuilder(horaLimpa);
	        if (horarioFormatado.length() >= 2) {
	            horarioFormatado.insert(2, ":");
	        }
	        
	        // 4. Monta a string final: Data + Hora + Segundos fixos
	        String filtroData = filtroPedido.getDataFormadata(); // Esperado: "DD/MM/YYYY"
	        
	        filtroDataHorario.append(filtroData.trim());
	        filtroDataHorario.append(" ");
	        filtroDataHorario.append(horarioFormatado.toString().trim());
	        filtroDataHorario.append(":00");
	    }
	    
	    this.dataFormatada = filtroDataHorario.toString().trim();
	    return this.dataFormatada;
	}

	public void montarEstrutura() {
		root = new DefaultTreeNode();
		Long tipoRefeicao = this.filtroPedido.getTipoRefeicao().getId();
		if (this.unidade != null) {
			if (this.unidade.getId() > 0) {
				String dataFormatada = montarFiltroData();
				try {
					this.contrato = contratoService.buscarContratoVigenteEAtivoPorUnidade(
							this.unidade.getId(),
							this.filtroPedido.getDataInicio()
					);

					boolean existeCardapio = cardapioDAO.existeCardapioPara(
							dataFormatada,
							tipoRefeicao,
							contrato.getId()
					);

					if (existeCardapio) {
						// Se existe contrato e cardápio, montar a estrutura
						root = new DefaultTreeNode(new GerarPedidoEstruturaDTO(), null);
						root.setExpanded(true);
						estruturaService.montarArvoreEstruturaGerarPedido(
								GerarPedidoEstruturaDTO.from(unidade.getEstrutura()),
								root,
								dataFormatada,
								tipoRefeicao
						);
						estruturaService.capacidadeTotalEstruturaGerarPedido(root);
						estruturaService.capacidadeTotalDietaPadrao(root);
						estruturaService.capacidadeTotalDietaEspecial(root);
						GerarPedidoEstruturaDTO e = (GerarPedidoEstruturaDTO) root.getData();
						this.unidade.setCapacidadeTotal(e.getCapacidadeTotal());
					} else {
						facesUtil.exibirMensagem(SEVERITY_ERROR, "Erro", "Não existe cardápio cadastrado para esta data e tipo de refeição");
						root = new DefaultTreeNode();
					}
				} catch (EJBException e) {
					Throwable cause = e.getCause();
					if (cause instanceof NaoEncontradoException || cause instanceof MultiplosResultadosException) {
						facesUtil.exibirMensagem(SEVERITY_ERROR, "Erro", e.getMessage().substring(e.getMessage().indexOf(":") + 1));
					} else {
						throw e;
					}
					root = new DefaultTreeNode();
				}
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

 public void selecionarEstruturaChamada(GerarPedidoEstruturaDTO estrutura, String data, FiltroGerarPedido filtroGerarPedido) {
     facesUtil.setObjetoSessao("estrutura", estrutura);
     facesUtil.setObjetoSessao("tipo_relatorio", "estrutura");

     validaDataPedido(filtroGerarPedido.getDataInicio());

     boolean pedidoExistente = pedidoAlimentoService.verificarExistenciaPedido(
             data,
             filtroGerarPedido.getTipoRefeicao().getId(),
             this.unidade.getId(),
             contrato.getId()
     );

		if (pedidoExistente) {
			RequestContext.getCurrentInstance().execute("PF('confirmSubstituirPedidoDialog').show()");
		} else {
			this.imprimir(data, filtroGerarPedido, contrato);
		}
	}

    public void confirmarSubstituicaoPedido() {
		this.imprimir(this.dataFormatada, this.filtroPedido, this.contrato);
		RequestContext.getCurrentInstance().execute("PF('confirmSubstituirPedidoDialog').hide()");

	}

	@Transactional
	private void imprimir(final String data, final FiltroGerarPedido filtroGerarPedido, Contrato contrato) {
		try {
			configurarDadosRelatorio();

			final long idEstrutura = gerarPedidoEstruturaDTO.getId();

			final List<PedidoEstruturaDTO> estruturaPedido = estruturaService.montarEstruturaPedido(data, idEstrutura);
			final List<PedidoEstruturaDTO> estruturaNivel2 = getEstruturasDeNivel2(estruturaPedido);
			final List<RefeicaoDetalhadaDTO> refeicoesDetalhadas = estruturaService.buscaPedidoConsulta(
					data, idEstrutura, filtroGerarPedido.getTipoRefeicao());

			final List<DetalheItemEstruturaCardapioDTO> itemCardapioPorEstrutura = DetalheItemEstruturaCardapioDTO.combinarPedidos(
					estruturaNivel2, refeicoesDetalhadas);
			final List<DietaEspecialEstruturaDTO> refeicoesEspeciais = estruturaService.buscarDietasEspeciais(idEstrutura, data);

			validarCardapio(itemCardapioPorEstrutura);

			PedidoAlimento pedidoAlimento = PedidoAlimento.create(
					data,
					SecurityUtil.getUsuario().getId(),
					this.unidade.getId(), contrato.getId(),
					filtroGerarPedido.getTipoRefeicao().getId()
			);

			for (DetalheItemEstruturaCardapioDTO itemDTO : itemCardapioPorEstrutura) {
   				pedidoAlimento.getItensPedido().add(ItemPedidoAlimento.newInstance(pedidoAlimento, itemDTO));
			}

			for (DietaEspecialEstruturaDTO dietaDTO : refeicoesEspeciais) {
				pedidoAlimento.getDietasEspeciais().add(DietaEspecialPedidoAlimento.newInstance(pedidoAlimento, dietaDTO));
			}

			pedidoAlimentoService.salvarPedidoAlimento(pedidoAlimento);

			// Parâmetros do relatório
			final Map<String, Object> parameters = criarParametrosRelatorio(data, filtroGerarPedido, refeicoesEspeciais, contrato);

			// Geração do relatório
			gerarRelatorioUnificado(itemCardapioPorEstrutura, parameters);
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Gerar Pedido", log);
		} catch (Exception e) {
			tratarErroGeracaoRelatorio(e);
		}
	}

    private void validaDataPedido(Date dataPedido) {
        try {
            if (dataPedido == null) {
                facesUtil.exibirMensagem(SEVERITY_ERROR, "Erro", "Selecione a data para gerar o pedido.");
                return;
            }

            LocalDate hoje = LocalDate.now();
            LocalDate amanha = hoje.plusDays(1);
            LocalDate dataSelecionada = Instant.ofEpochMilli(dataPedido.getTime())
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            if (dataSelecionada.isBefore(hoje)) {
                facesUtil.exibirMensagem(SEVERITY_ERROR, "Atenção", "Não é possível emitir um pedido retroativo.");
                return;
            }

            if (dataSelecionada.isAfter(amanha)) {
                facesUtil.exibirMensagem(SEVERITY_ERROR, "Atenção", "Não é possível gerar um pedido com uma antecedência superior a 1 dia.");
            }
        } catch (Exception e) {
            facesUtil.exibirMensagem(SEVERITY_ERROR, "Erro", "Ocorreu um erro ao validar a data informada.");
        }
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
