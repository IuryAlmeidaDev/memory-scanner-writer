package br.gov.mt.sejudh.sigepen.bean.relatorios;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.bean.outros.MetodosDialogBean;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroInfopen;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.IdadeRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Lgbt;
import br.gov.mt.sejudh.sigepen.enumerator.TempoCondenacao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDeficienciaRelatorio;
import br.gov.mt.sejudh.sigepen.model.Escolaridade;
import br.gov.mt.sejudh.sigepen.model.EstadoCivil;
import br.gov.mt.sejudh.sigepen.model.Etnia;
import br.gov.mt.sejudh.sigepen.model.Nacionalidade;
import br.gov.mt.sejudh.sigepen.model.Raca;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.Religiao;
import br.gov.mt.sejudh.sigepen.model.dto.InfopenDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.InfopenService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "Infopen", pattern = "/relatorio/recuperandos/infopen", viewId = "/WEB-INF/pages/relatorio/Infopen.xhtml"),
})
public class InfopenBean extends MetodosDialogBean {

	@Inject
	private InfopenService service;
	
	private FiltroInfopen filtro = new FiltroInfopen();
	private Nacionalidade[] nacionalidades = {};
	private Regime[] regimes = {};
	private EstadoCivil[] estadoCivil = {};
	private Religiao[] religiao = {};
	private Escolaridade[] escolaridade = {};
	private Raca[] raca = {};
	private IdadeRecuperando idadeRecuperando;
	private TempoCondenacao tempoCondenacao;
	private Lgbt lbgt;
	private Etnia[] etnia = {};
	
	
	private String tipoRelatorio;
    private String periodo;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	@Inject
	private Logger log;
	
	@URLAction(onPostback=false)
	public String init() {		
		this.filtro.setDataInicial(new Date());
		this.filtro.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		this.nacionalidades = this.service.buscarNacionalidades(SecurityUtil.getUnidadeSelecionada().getId());
		this.regimes = this.service.buscarRegimes(SecurityUtil.getUnidadeSelecionada().getId());
		this.estadoCivil = this.service.buscarEstadoCivil(SecurityUtil.getUnidadeSelecionada().getId());
		this.religiao = this.service.buscarReligiao(SecurityUtil.getUnidadeSelecionada().getId());
		this.escolaridade = this.service.buscarEscolaridade(SecurityUtil.getUnidadeSelecionada().getId());
		this.raca = this.service.buscarRaca(SecurityUtil.getUnidadeSelecionada().getId());
		this.etnia = this.service.buscarEtnia(SecurityUtil.getUnidadeSelecionada().getId());
		RequestContext.getCurrentInstance().update("meuFormulario");
		return "Pretty:infopen";
	}

	public FiltroInfopen getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroInfopen filtro) {
		this.filtro = filtro;
	}

	public Nacionalidade[] getNacionalidades() {
		return nacionalidades;
	}

	public void setNacionalidades(Nacionalidade[] nacionalidades) {
		this.nacionalidades = nacionalidades;
	}

	public Regime[] getRegimes() {
		return regimes;
	}

	public void setRegimes(Regime[] regimes) {
		this.regimes = regimes;
	}

	public EstadoCivil[] getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(EstadoCivil[] estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public Religiao[] getReligiao() {
		return religiao;
	}

	public void setReligiao(Religiao[] religiao) {
		this.religiao = religiao;
	}

	public Escolaridade[] getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(Escolaridade[] escolaridade) {
		this.escolaridade = escolaridade;
	}

	public Raca[] getRaca() {
		return raca;
	}

	public void setRaca(Raca[] raca) {
		this.raca = raca;
	}

	public IdadeRecuperando getIdadeRecuperando() {
		return idadeRecuperando;
	}

	public void setIdadeRecuperando(IdadeRecuperando idadeRecuperando) {
		this.idadeRecuperando = idadeRecuperando;
	}

	public TempoCondenacao getTempoCondenacao() {
		return tempoCondenacao;
	}

	public void setTempoCondenacao(TempoCondenacao tempoCondenacao) {
		this.tempoCondenacao = tempoCondenacao;
	}
	
	public void imprimir() {
	    try {

	        if (this.filtro.getTipoRelatorio() == 0) {
	            long impFeminino = service.buscarFeminino(SecurityUtil.getUnidadeSelecionada().getId());
	            Map<String, Object> parameters = new HashMap<>();
	            parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
	            parameters.put("UNIDADE_ID", SecurityUtil.getUnidadeSelecionada().getId());
	            parameters.put("IMP_FEMININO", impFeminino);
	            
	            if (this.filtro.getDataInicial() != null) {
	            	
	            	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	                
	                String dataInicialFormatada = sdf.format(this.filtro.getDataInicial());
	                parameters.put("DATA_SAIDA", dataInicialFormatada);
	                
	               	                
	                fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.EstatisticoInfopenPorData, parameters));
	                LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório Estatístico Infopen por Data", log);
	            } 

	            else {
	                fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.EstatisticoInfopen, parameters));
	                LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório Estatístico Infopen", log);
	            }

	            RequestContext.getCurrentInstance().execute("download()");
	        } 
	        
	        else if (this.filtro.getTipoRelatorio() == 1) {
	            List<InfopenDTO> lista = this.service.buscarInfopenDetalhadoPorFiltro(this.filtro);
	            	        
	            if (lista.isEmpty() || lista.get(0).getRecuperandos().isEmpty()) {
	                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
	                return;
	            }
	            
	            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.InfopenDetalhado));
	            LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório de Recuperandos Infopen Detalhado", log);
	            RequestContext.getCurrentInstance().execute("download()");
	        }
	    } 

	    catch (Exception e) {
	        e.printStackTrace();
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao imprimir Relatório: " + e.getMessage());
	        LoggerUtil.logErro(Acao.IMPRIMIR, "Erro ao gerar relatório", e, log);
	    }
	}
	
	
	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	
	public Etnia[] getEtnia() {
		return etnia;
	}

	public void setEtnia(Etnia[] etnia) {
		this.etnia = etnia;
	}

	public TipoDeficienciaRelatorio[] getTiposDeficiencia() {
		return TipoDeficienciaRelatorio.values();
	}
    
}
