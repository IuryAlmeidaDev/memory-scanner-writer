package br.gov.mt.sejudh.sigepen.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroLog;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoArquivo;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.Log;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.LogService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "ListarLog", pattern = "/log", viewId = "/WEB-INF/pages/auditoria/ListarLog.xhtml")
public class LogBean {
	
	private LazyDataModel<Log> logs;
	
	private FiltroLog filtro;
	
	private ServidorDTO servidor;
	
	@Inject
	private LogService service;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;

	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@PostConstruct
	public void init() {
		try {	
			LoggerUtil.logInfo(Acao.ACESSAR, "Logs", log);
			pesquisar();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void pesquisar() {
		this.setFiltro(new FiltroLog());
		this.logs = new LazyDataModel<Log>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Log> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
				setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));		
				
                if (first >= getRowCount()) {
                    first = 0;                    
                    DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("registrosForm:dataTable");
                    dt.setFirst(first);
                }
				
				getFiltro().organizaFiltro(first, pageSize, sortField, sortOrder);
				
				return service.buscarLogsPorFiltro(getFiltro(), false);
			}
		};
	}
	
	public void abrirDialogServidor(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarServidor");
	}
	
	public void associarServidor(SelectEvent event) {
		ServidorDTO s = (ServidorDTO) event.getObject();
		
		if (s == null) {
			return;
		}
		
		this.servidor = s;
		
		this.filtro.setUsuarioId(String.valueOf(service.buscarIdUsuarioPorIdServidor(s.getId())));		
	}

	public void imprimir() {
		try {
			List<Log> lista = service.buscarLogsPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarLogs));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Logs", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de logs: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Logs", e, log);
		}
	}
	
	public void imprimirLog(Log l) {
		try {
			InputStream input = new ByteArrayInputStream(l.getTextoExcecao().getBytes(Charset.forName("UTF-8")));
			byte[] data = new byte[input.available()];
			input.read(data);

			OutputStream output = new ByteArrayOutputStream();
			output.write(data);
			
			Arquivo arquivo = new Arquivo();
			arquivo.setBytes(data);
			arquivo.setContentType("text/plain");
			arquivo.setNome(String.valueOf(l.getId()));
			arquivo.setTipo(TipoArquivo.LOG);
			
			fileDownload.prepararArquivo(arquivo);
			RequestContext.getCurrentInstance().execute("download()");
			//LoggerUtil.logInfo(Acao.IMPRIMIR, "Log de erro", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir log de erro: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Log de erro", e, log);
		}
	}
	
	public LazyDataModel<Log> getLogs() {
		return logs;
	}

	public void setLogs(LazyDataModel<Log> logs) {
		this.logs = logs;
	}

	public FiltroLog getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroLog filtro) {
		this.filtro = filtro;
	}

	public ServidorDTO getServidor() {
		return servidor;
	}

	public void setServidor(ServidorDTO servidor) {
		this.servidor = servidor;
	}
}
