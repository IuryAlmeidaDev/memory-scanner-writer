package br.gov.mt.sejudh.sigepen.bean;

import java.text.ParseException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.service.*;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroHorarioRefeicao;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Mes;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.MapaConsolidadoDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "VisualizarMapaConsolidado", pattern = "/visualizar/mapaCosolidado/unidade/#{id}", viewId = "/WEB-INF/pages/mapaCosolidado/GerarMapaConsolidadoUnidade.xhtml"),
		@URLMapping(id = "ListarMapaConsolidado", pattern = "/mapaConsolidado", viewId = "/WEB-INF/pages/mapaCosolidado/GerarMapaConsolidado.xhtml") })
public class MapaConsolidadoBean extends MetodosDePaginaBean {

	private List<UnidadePrisional> unidadesPrisionais;

	private UnidadePrisional unidade;

	private Mes mes;

	private FiltroHorarioRefeicao filtro;

	private Mes[] meses;

	private String ano;

	private List<Contrato> contratos = new ArrayList<Contrato>();

	@Inject
	private ContratoService contratoService;

	@Inject
	private ApostilamentoService apostilamentoService;

	@Inject
	private UnidadePrisionalService unidadeService;

	@Inject
	private MapaConsolidadoService mapaConsolidadoService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	@Inject
	private GerenciarAssinaturaService gerenciarAssinaturaService;

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public Mes getMes() {
		return mes;
	}

	public void setMes(Mes mes) {
		this.mes = mes;
	}

	public String getAno() {
		return this.ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public FiltroHorarioRefeicao getFiltro() {
		return this.filtro;
	}

	public Mes[] getMeses() {
		return Mes.values();
	}

	public Mes[] setMeses(Mes[] meses) {
		return this.meses = meses;
	}

	public MapaConsolidadoService getMapaConsolidadoService() {
		return this.mapaConsolidadoService;
	}

	protected void registrarAcessoFornecedor() {
		logDeAcesso(log, "Fornecedor");
	}

	protected void registrarErro(Exception e) {
		LoggerUtil.logErro(e, log);
	}

	protected void registrarSucessoGeracao() {
		LoggerUtil.logInfo(Acao.IMPRIMIR, "Gerar Consolidado do MÃªs", log);
	}

	protected void registrarErroGeracao(Exception e) {
		LoggerUtil.logErro(Acao.IMPRIMIR, "Gerar Pedido", e, log);
	}

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
	}

	@URLAction(onPostback = false)
	public String init() {
		try {
			this.unidadesPrisionais = unidadeService.listarUnidadesComContratosAtivo();
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				break;
			case VISUALIZAR:
			case ALTERAR:

				break;

			case LISTAR:

				registrarAcessoFornecedor();
				break;

			default:
				break;
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			registrarErro(e);
		}

		return null;
	}

	private boolean existeApostilamentoParaUniadade(Long idUnidade) {
		if (apostilamentoService.existeApostilamentoUnidade(idUnidade)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Ã‰ nescessÃ¡rio criar um apostilamento!");
			return false;
		}

		return true;
	}

	private boolean validarAno() {
		if (this.ano == null || this.ano.trim().isEmpty()) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Informe um ano vÃ¡lido!");
			return false;
		}

		int anoInformado;
		try {
			anoInformado = Integer.parseInt(this.ano);
		} catch (NumberFormatException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Informe um ano vÃ¡lido!");
			return false;
		}

		if (!(anoInformado >= 1923)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Informe um ano vÃ¡lido!");
			return false;
		}
		return true;
	}

	private boolean validacoes(Long idUnidade) {
		if (this.unidade == null || idUnidade == null || this.mes == null) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Informe a unidade e o mÃªs de referÃªncia!");
			return false;
		}

		return this.existeApostilamentoParaUniadade(idUnidade) && this.validarAno() && this.validarContratoVigente(idUnidade);
	}

	private boolean validarContratoVigente(long idUnidade) {
		String dataInicio = obterDataInicio(this.mes.getId(), Integer.parseInt(this.ano));
		String dataFim = obterDataFim(this.mes.getId(), Integer.parseInt(this.ano));
		try {
			this.contratos = contratoService.buscarContratoVigente(idUnidade, dataInicio, dataFim);
			if (this.contratos == null) {
				this.contratos = new ArrayList<Contrato>();
			}

			List<Contrato> contratosDaUnidade = new ArrayList<Contrato>();
			for (Contrato contrato : this.contratos) {
				if (contratoPertenceAUnidade(contrato, idUnidade)) {
					contratosDaUnidade.add(contrato);
				}
			}

			this.contratos = contratosDaUnidade;
			if (this.contratos.isEmpty()) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "AtenÃ§Ã£o para o MÃªs/Ano selecionado, nÃ£o hÃ¡ contrato vigente na Unidade Prisional");
				return false;
			}
		} catch (ParseException e) {
			LoggerUtil.logErro(e, log);
			return false;
		}
		return true;
	}

	private String obterDataInicio(int mes, int ano) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return YearMonth.of(ano, mes).atDay(1).format(formatter);

	}

	private String obterDataFim(int mes, int ano) {
		YearMonth yearMonth = YearMonth.of(ano, mes);
		int ultimoDiaDoMes = yearMonth.lengthOfMonth();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return yearMonth.atDay(ultimoDiaDoMes).format(formatter);

	}

	private double calcularValorAuditoria(Integer quantidadeAuditada, Double valorTotal, Integer quantidadeTotal) {
		if (quantidadeAuditada == null || valorTotal == null || quantidadeTotal == null || quantidadeTotal <= 0) {
			return 0D;
		}

		return quantidadeAuditada.doubleValue() * (valorTotal / quantidadeTotal.doubleValue());
	}

	private int obterQuantidadeAuditoria(Map<String, Integer> audit, String chave) {
		if (audit == null || chave == null) {
			return 0;
		}

		Integer quantidade = audit.get(chave);
		return quantidade != null ? quantidade : 0;
	}

	private void inicializarParametrosAuditoria(Map<String, Object> parameters) {
		parameters.put("TOTAL_VALOR_CAFE_AUDIT", 0D);
		parameters.put("TOTAL_VALOR_ALMOCO_AUDIT", 0D);
		parameters.put("TOTAL_VALOR_JANTA_AUDIT", 0D);
		parameters.put("TOTAL_VALOR_CAFE_ESP_AUDIT", 0D);
		parameters.put("TOTAL_VALOR_ALMOCO_ESP_AUDIT", 0D);
		parameters.put("TOTAL_VALOR_JANTA_ESP_AUDIT", 0D);
		parameters.put("TOTAL_GERAL_AUDIT", 0D);
	}

	private double obterParametroDouble(Map<String, Object> parameters, String chave) {
		Object valor = parameters.get(chave);
		return valor instanceof Double ? (Double) valor : 0D;
	}

	private boolean contratoPertenceAUnidade(Contrato contrato, Long idUnidade) {
		if (contrato == null || idUnidade == null || contrato.getUnidades() == null) {
			return false;
		}

		for (UnidadePrisional unidadeContrato : contrato.getUnidades()) {
			if (unidadeContrato != null && idUnidade.equals(unidadeContrato.getId())) {
				return true;
			}
		}

		return false;
	}

	protected Arquivo gerarArquivoRelatorio(List<MapaConsolidadoDTO> mapaConsolidados, Map<String, Object> parameters) throws Exception {
		return RelatorioUtil.gerarRelatorio(mapaConsolidados, Relatorio.ListaConsolidadoMensal, parameters);
	}

	protected void executarDownload() {
		RequestContext.getCurrentInstance().execute("download()");
	}

	public void gerarMapa() {
		Long idUnidade = this.unidade != null ? this.unidade.getId() : null;
		if (validacoes(idUnidade)) {
			List<MapaConsolidadoDTO> mapaConsolidados = new ArrayList<>();
			Map<String, Object> parameters = new HashMap<>();

			parameters.put("ANO", this.ano);

			String dataInicioStr = obterDataInicio(this.mes.getId(), Integer.parseInt(this.ano));
			String dataFimStr = obterDataFim(this.mes.getId(), Integer.parseInt(this.ano));

			Map<String, Integer> audit = mapaConsolidadoService.obterDadosAuditoria(this.unidade.getId(), dataInicioStr, dataFimStr);
			if (audit == null) {
				audit = new HashMap<String, Integer>();
			}
			final Map<String, Integer> auditFinal = audit;
			parameters.putAll(audit);
			inicializarParametrosAuditoria(parameters);

			this.contratos.forEach(contrato -> {
				if (!contratoPertenceAUnidade(contrato, this.unidade.getId())) {
					return;
				}

				MapaConsolidadoDTO dto = mapaConsolidadoService.obterMapaConsolidado(this.unidade, this.mes, this.ano, contrato);
				if (dto == null) {
					return;
				}
				mapaConsolidados.add(dto);

				double vlrAuditCafe = calcularValorAuditoria(obterQuantidadeAuditoria(auditFinal, "AUDIT_CAFE"),
						dto.getTotalValorCafe(), dto.getTotalCafe());
				double vlrAuditAlmoco = calcularValorAuditoria(obterQuantidadeAuditoria(auditFinal, "AUDIT_ALMOCO"),
						dto.getTotalValorAlmoco(), dto.getTotalAlmoco());
				double vlrAuditJanta = calcularValorAuditoria(obterQuantidadeAuditoria(auditFinal, "AUDIT_JANTA"),
						dto.getTotalValorJanta(), dto.getTotalJanta());

				double vlrAuditCafeEsp = calcularValorAuditoria(obterQuantidadeAuditoria(auditFinal, "AUDIT_CAFE_ESP"),
						dto.getTotalValorCafeEspecial(), dto.getTotalCafeEspecial());
				double vlrAuditAlmocoEsp = calcularValorAuditoria(obterQuantidadeAuditoria(auditFinal, "AUDIT_ALMOCO_ESP"),
						dto.getTotalValorAlmocoEspecial(), dto.getTotalAlmocoEspecial());
				double vlrAuditJantaEsp = calcularValorAuditoria(obterQuantidadeAuditoria(auditFinal, "AUDIT_JANTA_ESP"),
						dto.getTotalValorJantaEspecial(), dto.getTotalJantaEspecial());

				parameters.put("TOTAL_VALOR_CAFE_AUDIT", obterParametroDouble(parameters, "TOTAL_VALOR_CAFE_AUDIT") + vlrAuditCafe);
				parameters.put("TOTAL_VALOR_ALMOCO_AUDIT", obterParametroDouble(parameters, "TOTAL_VALOR_ALMOCO_AUDIT") + vlrAuditAlmoco);
				parameters.put("TOTAL_VALOR_JANTA_AUDIT", obterParametroDouble(parameters, "TOTAL_VALOR_JANTA_AUDIT") + vlrAuditJanta);

				parameters.put("TOTAL_VALOR_CAFE_ESP_AUDIT", obterParametroDouble(parameters, "TOTAL_VALOR_CAFE_ESP_AUDIT") + vlrAuditCafeEsp);
				parameters.put("TOTAL_VALOR_ALMOCO_ESP_AUDIT", obterParametroDouble(parameters, "TOTAL_VALOR_ALMOCO_ESP_AUDIT") + vlrAuditAlmocoEsp);
				parameters.put("TOTAL_VALOR_JANTA_ESP_AUDIT", obterParametroDouble(parameters, "TOTAL_VALOR_JANTA_ESP_AUDIT") + vlrAuditJantaEsp);

				parameters.put("TOTAL_GERAL_AUDIT", obterParametroDouble(parameters, "TOTAL_GERAL_AUDIT") + vlrAuditCafe + vlrAuditAlmoco + vlrAuditJanta
						+ vlrAuditCafeEsp + vlrAuditAlmocoEsp + vlrAuditJantaEsp);
			});

			try {

				fileDownload.prepararArquivo(gerarArquivoRelatorio(mapaConsolidados, parameters));
				executarDownload();
				registrarSucessoGeracao();
			} catch (Exception e) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
				registrarErroGeracao(e);
			}
		}
	}

}
