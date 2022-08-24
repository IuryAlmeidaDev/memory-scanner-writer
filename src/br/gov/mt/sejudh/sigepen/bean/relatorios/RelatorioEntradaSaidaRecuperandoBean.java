package br.gov.mt.sejudh.sigepen.bean.relatorios;

import java.util.Date;
import java.util.List;

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
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.CondicaoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoDesligamentoRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntradaRelatorio;
import br.gov.mt.sejudh.sigepen.enumerator.TipoRelatorioEntradaSaidaRec;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioEntradaSaidaRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.RelatorioEntradaSaidaRecuperandoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
		@URLMapping(id = "entrada_saida", pattern = "/relatorio/recuperandos/entrada_saida", viewId = "/WEB-INF/pages/relatorio/RelatorioEntradaSaidaRecuperando.xhtml"), })
public class RelatorioEntradaSaidaRecuperandoBean extends MetodosDialogBean {

	@Inject
	private RelatorioEntradaSaidaRecuperandoService service;

	private FiltroRelatorioEntradaSaidaRecuperando filtro = new FiltroRelatorioEntradaSaidaRecuperando();

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	@Inject
	private Logger log;

	private List<Regime> regimes;

	@URLAction(onPostback = false)
	public String init() {
		this.filtro.setUnidade(SecurityUtil.getUnidadeSelecionada().getId());
		this.filtro.setDataInicial(new Date());
		this.filtro.setDataFinal(new Date());
		this.regimes = this.service.buscarRegimes();
		this.filtro.setTipoRelatorioEntradaSaidaRec(TipoRelatorioEntradaSaidaRec.ENTRADA);
		RequestContext.getCurrentInstance().update("meuFormulario");
		return "Pretty:entrada_saida";
	}

	public FiltroRelatorioEntradaSaidaRecuperando getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroRelatorioEntradaSaidaRecuperando filtro) {
		this.filtro = filtro;
	}

	public void imprimir() {
		if (this.filtro.getDataFinal().before(this.filtro.getDataInicial())){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "A data final deve não pode ser anterior a data inicial!");
			return;
		}
		
		long diff = this.filtro.getDataFinal().getTime() - this.filtro.getDataInicial().getTime();
		final long ano = 31536000000L;
		if(diff > ano) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Informe um período inferior a um ano!");
			return;
		}
		
		try {
			List<RelatorioEntradaSaidaRecuperandoDTO> lista = this.service
					.buscarRelatorioEntradaSaidaPorFiltro(this.filtro);

			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarRelatorioEntradaSaidaRecuperando));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório de Entrada/Saída de Recuperandos", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro imprimir Relatório de Entrada/Saída de Recuperandos " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Relatório de Entrada/Saída de Recuperandos", e, log);
		}

	}

	public List<Regime> getRegimes() {
		return regimes;
	}

	public void setRegimes(List<Regime> regimes) {
		this.regimes = regimes;
	}
	
	public TipoEntradaRelatorio[] getTipoEntrada() {
		return TipoEntradaRelatorio.values();
	}
	
	public TipoDesligamentoRelatorio[] getTipoDesligamento() {
		return TipoDesligamentoRelatorio.values();
	}
	
	public CondicaoRelatorio[] getCondicao() {
		return CondicaoRelatorio.values();
	}
	
	public boolean mostrarTipoEntrada() {
		return TipoRelatorioEntradaSaidaRec.ENTRADA.equals(this.filtro.getTipoRelatorioEntradaSaidaRec())
				|| TipoRelatorioEntradaSaidaRec.ENTRADA_SAIDA.equals(this.filtro.getTipoRelatorioEntradaSaidaRec());
	}
	
	public boolean mostrarTipoDesligamento() {
		return TipoRelatorioEntradaSaidaRec.SAIDA.equals(this.filtro.getTipoRelatorioEntradaSaidaRec())
				|| TipoRelatorioEntradaSaidaRec.ENTRADA_SAIDA.equals(this.filtro.getTipoRelatorioEntradaSaidaRec());
	}
}
