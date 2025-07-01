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

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
	}

	@URLAction(onPostback = false)
	public String init() {
		this.unidadesPrisionais = unidadeService.listarUnidadesComContratosAtivo();
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				break;
			case VISUALIZAR:
			case ALTERAR:

				break;

			case LISTAR:

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
	
	private boolean existeApostilamentoParaUniadade(Long idUnidade) {
		if(apostilamentoService.existeApostilamentoUnidade(idUnidade)){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "É nescessário criar um apostilamento!");
			return false;
		}
		
		return true;
	}
	
	private boolean validarAno() {
		if(!(Integer.parseInt(this.ano) >= 1923)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Informe um ano válido!");
			return false;
		}
		return true;
	}
	
	private boolean validacoes(Long idUnidade){
		return this.existeApostilamentoParaUniadade(idUnidade) && this.validarAno() && this.validarContratoVigente(idUnidade);
	}
	
	private boolean validarContratoVigente(long idUnidade){
		String dataInicio = obterDataInicio(this.mes.getId(), Integer.parseInt(this.ano));
		String dataFim = obterDataFim(this.mes.getId(), Integer.parseInt(this.ano));
		try {
			this.contratos = contratoService.buscarContratoVigente(idUnidade, dataInicio,dataFim);
			if(this.contratos.isEmpty()) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Atenção para o Mês/Ano selecionado, não há contrato vigente na Unidade Prisional");
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
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
	
	public void gerarMapa() {
		
		if(validacoes(this.unidade.getId())) {	
			List<MapaConsolidadoDTO> mapaConsolidados = new ArrayList<>();
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("ANO", this.ano);
			this.contratos.forEach(contrato -> {				
				MapaConsolidadoDTO mapaConsolidado =  mapaConsolidadoService.obterMapaConsolidado(this.unidade, this.mes, this.ano, contrato);
				mapaConsolidados.add(mapaConsolidado);				
			});
			
			try {

				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(mapaConsolidados,Relatorio.ListaConsolidadoMensal, parameters, this.gerenciarAssinaturaService));
				RequestContext.getCurrentInstance().execute("download()");
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Gerar Consolidado do Mês", log);
			} catch (Exception e) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
				LoggerUtil.logErro(Acao.IMPRIMIR, "Gerar Pedido", e, log);
			}
			
			
			
		}		
	}	
	
}
