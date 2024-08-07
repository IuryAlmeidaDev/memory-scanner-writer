package br.gov.mt.sejudh.sigepen.bean.relatorios;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.bean.outros.MetodosDialogBean;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioRecuperandoJurisdicional;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.CondicaoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperandoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamentoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntradaRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoRelatorioEntradaSaidaRec;
import br.gov.mt.sejudh.sigepen.enumerator.UfRelatorio;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioRecuperandoJurisdicionalDTO.RecuperandoJurisdicionalDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.RelatorioRecuperandoJurisdicionalService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "recuperando_secao_jurisdicional", pattern = "/relatorio/recuperandos/recuperando_secao_jurisdicional", viewId = "/WEB-INF/pages/relatorio/RelRecSecaoJurisdicional.xhtml"),
})
public class RelatorioRecuperandoJurisdicionalBean extends MetodosDialogBean {


	@Inject
	private FacesUtil facesUtil;
	
	private FiltroRelatorioRecuperandoJurisdicional filtro = new FiltroRelatorioRecuperandoJurisdicional();
	
	@Inject
	private RelatorioRecuperandoJurisdicionalService service;
	
	
	private List<UnidadePrisional> unidadesPrisionais = new ArrayList<>();

	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	

	@Inject
	private Logger log;
	

	@URLAction(onPostback=false)
	public String init() {
//		this.filtro.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		this.unidadesPrisionais = unidadeService.listarUnidadesNaoInativas();
		this.filtro.setDataInicial(new Date());
		this.filtro.setDataFinal(new Date());
		this.filtro.setTipoRelatorioEntradaSaidaRec(TipoRelatorioEntradaSaidaRec.ENTRADA);
		RequestContext.getCurrentInstance().update("meuFormulario");
		return "Pretty:recuperando_secao_jurisdicional";
	}


	public void imprimir() {
		
		
		boolean possuiDataInicial = this.filtro.getDataInicial() != null;
		boolean possuiDataFinal = this.filtro.getDataFinal() != null;
		
		if ((possuiDataInicial && !possuiDataFinal) || (!possuiDataInicial && possuiDataFinal)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Preencha as duas datas!");
			return;
		}
		
		if (this.filtro.getDataFinal().before(this.filtro.getDataInicial())){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "A data final deve não pode ser anterior a data inicial!");
			return;
		}
		
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FILTRO", this.filtro.getDescricao());
			parameters.put("SUBREPORT_DIR", RelatorioUtil.getSubReportPath(Relatorio.ListarRelatorioSecaoJurisdicionalNovo));
			
			List<RecuperandoJurisdicionalDTO> lista = this.service.buscarRelatorioNovo(this.filtro);

			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarRelatorioSecaoJurisdicionalNovo, parameters));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório por Seção Jurisdicional do Processo", log);
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro imprimir Relatório por Seção Jurisdicional do Processo " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Relatório por Seção Jurisdicional do Processo", e, log);
		}
		
	}


	public FacesUtil getFacesUtil() {
		return facesUtil;
	}


	public void setFacesUtil(FacesUtil facesUtil) {
		this.facesUtil = facesUtil;
	}


	public FileDownloadHandlerBean getFileDownload() {
		return fileDownload;
	}


	public void setFileDownload(FileDownloadHandlerBean fileDownload) {
		this.fileDownload = fileDownload;
	}


	public FiltroRelatorioRecuperandoJurisdicional getFiltro() {
		return filtro;
	}


	public void setFiltro(FiltroRelatorioRecuperandoJurisdicional filtro) {
		this.filtro = filtro;
	}


	public Logger getLog() {
		return log;
	}


	public void setLog(Logger log) {
		this.log = log;
	}

	public void abrirDialogEstabelecimento(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarEstabelecimento", null, 1000);
	}
	
	public void associarEstabelecimento(SelectEvent event) {
		Estabelecimento estabelecimento = (Estabelecimento) event.getObject();
		
		if (estabelecimento != null) {
			this.filtro.setEstabelecimento(estabelecimento);
		}
	}

	public CondicaoRelatorio[] getCondicao() {
		return CondicaoRelatorio.values();
	}
	
	public StatusRecuperandoRelatorio[] getStatus() {
		return StatusRecuperandoRelatorio.values();
	}
	
	public TipoEntradaRelatorio[] getTipoEntrada() {
		return TipoEntradaRelatorio.values();
	}
	
	public TipoDesligamentoRelatorio[] getTipoDesligamento() {
		return TipoDesligamentoRelatorio.values();
	}
	
	public boolean mostrarTipoEntrada() {
		return TipoRelatorioEntradaSaidaRec.ENTRADA.equals(this.filtro.getTipoRelatorioEntradaSaidaRec())
				|| TipoRelatorioEntradaSaidaRec.ENTRADA_SAIDA.equals(this.filtro.getTipoRelatorioEntradaSaidaRec());
	}
	
	public boolean mostrarTipoDesligamento() {
		return TipoRelatorioEntradaSaidaRec.SAIDA.equals(this.filtro.getTipoRelatorioEntradaSaidaRec())
				|| TipoRelatorioEntradaSaidaRec.ENTRADA_SAIDA.equals(this.filtro.getTipoRelatorioEntradaSaidaRec());
	}
	
	public UfRelatorio[] getEstados() {
		return UfRelatorio.values();
	}


	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}


	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}
}
