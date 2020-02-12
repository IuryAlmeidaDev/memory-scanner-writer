package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "SelecionarListaChamada", pattern = "/recuperando/lista-chamada", viewId = "/WEB-INF/pages/_dialogs/SelecionarListaChamada.xhtml")
public class SelecionarListaChamadaBean extends MetodosDialogBean {
	String tipoRelatorio;
	UnidadePrisionalDTO unidade;
	Estrutura estrutura;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@Inject
	private Logger log;
	
	@URLAction(onPostback=false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		unidade = (UnidadePrisionalDTO) facesUtil.getObjetoSessao("unidade");
		estrutura =  (Estrutura) facesUtil.getObjetoSessao("estrutura");
		tipoRelatorio = (String) facesUtil.getObjetoSessao("tipo_relatorio");
		
		return null;
	}
	
	public void imprimirListaDeChamada(Boolean comFoto) {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			
			switch (tipoRelatorio) {
				case "unidade":
					parameters.put("ID_ESTRUTURA", Integer.parseInt(String.valueOf(unidade.getIdEstrutura())));
					parameters.put("ESTRUTURA_NOME", unidade.getNome());
				break;
				
				case "estrutura":
					parameters.put("ID_ESTRUTURA", Integer.parseInt(String.valueOf(estrutura.getId())));
					int index = estrutura.getNomeCompleto().indexOf(">");
					
					if (index > -1) {
						parameters.put("ESTRUTURA_NOME", estrutura.getNomeCompleto().substring(0, index));
					} else {
						parameters.put("ESTRUTURA_NOME", estrutura.getNomeCompleto());
					}
					break;
			}
			
			if (comFoto) {
				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.ListaDeChamadaComFoto, parameters));
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Chamada com Foto", log);
			} else {
				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.ListaDeChamada, parameters));
				LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Chamada", log);
			}
			RequestContext.getCurrentInstance().execute("download()");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Chamada", e, log);
		}
	}
	
	public void imprimirListaSintetica() {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			
			switch (tipoRelatorio) {
				case "unidade":
					parameters.put("ID_ESTRUTURA", unidade.getIdEstrutura());
				break;
				
				case "estrutura":
					parameters.put("ID_ESTRUTURA", estrutura.getId());
					break;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.ListaDeChamadaSintetica, parameters));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Chamada Sintética", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Chamada Sintética", e, log);
		}
	}
}
