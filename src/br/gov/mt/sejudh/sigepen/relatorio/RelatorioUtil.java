package br.gov.mt.sejudh.sigepen.relatorio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mt.sejudh.sigepen.enumerator.TipoArquivo;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.TextoDoRelatorio;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RequestScoped
public class RelatorioUtil {
	
	public static <T> Arquivo gerarRelatorio(Relatorio relatorio, Map<String, Object> parameters) throws JRException, NamingException, SQLException {
		InitialContext initialContext = new InitialContext();
		DataSource ds = (DataSource) initialContext.lookup("java:/sigepen-dev");
		Connection conn = ds.getConnection();
		Arquivo arquivo = null;
		
		try {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			
			parameters.put("CABECALHO", getCabecalhoPath(ctx, relatorio.getCabecalho()));
			parameters.put("RODAPE", getRodapePath(ctx, relatorio.getRodape()));
			parameters.put("UNIDADE_LOGADA", SecurityUtil.getUnidadeSelecionada().getNome());
			parameters.put("EMISSOR", SecurityUtil.getMatriculaUsuario());
			
			if (!parameters.containsKey("NOME_DO_RELATORIO")) {
				parameters.put("NOME_DO_RELATORIO", relatorio.getNome());
			}
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(getRelatorioPath(ctx, relatorio), parameters, conn);
			
			byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
			
			arquivo = new Arquivo();
			arquivo.setBytes(pdf);
			arquivo.setContentType("application/pdf");
			arquivo.setNome(relatorio.name());
			arquivo.setTipo(TipoArquivo.PDF);

		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
			ds = null;
			initialContext.close();
		}
		
		return arquivo;
	}

	public static <T> Arquivo gerarRelatorio(T valor, Relatorio relatorio) throws JRException {
		return gerarRelatorio(Arrays.asList(valor), relatorio, new HashMap<String, Object>());
	}
	
	public static <T> Arquivo gerarRelatorio(T valor, Relatorio relatorio, Map<String, Object> parameters) throws JRException {
		return gerarRelatorio(Arrays.asList(valor), relatorio, parameters);
	}
	
	public static <T> Arquivo gerarRelatorio(List<T> valores, Relatorio relatorio, Map<String, Object> parameters) throws JRException {
		Arquivo arquivo = null;
		
		try {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			
			parameters.put("CABECALHO", getCabecalhoPath(ctx, relatorio.getCabecalho()));
			parameters.put("RODAPE", getRodapePath(ctx, relatorio.getRodape()));
			parameters.put("UNIDADE_LOGADA", SecurityUtil.getUnidadeSelecionada().getNome());
			parameters.put("EMISSOR", SecurityUtil.getMatriculaUsuario());
			
			if (!parameters.containsKey("NOME_DO_RELATORIO")) {
				parameters.put("NOME_DO_RELATORIO", relatorio.getNome());
			}
			
			JRDataSource datasource = new JRBeanCollectionDataSource(valores);
			JasperPrint jasperPrint = JasperFillManager.fillReport(getRelatorioPath(ctx, relatorio), parameters, datasource);
			
			byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);
			
			arquivo = new Arquivo();
			arquivo.setBytes(pdf);
			arquivo.setContentType("application/pdf");
			arquivo.setNome(relatorio.name());
			arquivo.setTipo(TipoArquivo.PDF);
			
		} catch (Exception e) {
			throw e;
		}
		
		return arquivo;
	}

	public static <T> Arquivo gerarRelatorio(Relatorio relatorio, TextoDoRelatorio conteudo) throws JRException, NamingException, SQLException {
		return gerarRelatorio(relatorio, conteudo, new HashMap<String, Object>());
	}
	
	public static <T> Arquivo gerarRelatorio(Relatorio relatorio, TextoDoRelatorio conteudo, Map<String, Object> parameters) throws JRException, NamingException, SQLException {		
		List<TextoDoRelatorio> lista = new ArrayList<TextoDoRelatorio>();
		lista.add(conteudo);
	
		return gerarRelatorio(lista, relatorio, parameters);
	}
	
	public static <T> Arquivo gerarRelatorio(List<T> valores, Relatorio relatorio) throws JRException {
		return gerarRelatorio(valores, relatorio, new HashMap<String, Object>());
	}
	
	public static <T> Arquivo gerarRelatorio(Relatorio relatorio) throws JRException, NamingException, SQLException {
		return gerarRelatorio(relatorio, new HashMap<String, Object>());
	}
	
	private static String getRelatorioPath(ServletContext ctx, Relatorio rel) {
		return ctx.getRealPath("WEB-INF" + File.separator + "relatorios" + File.separator + rel.name() + ".jasper");
	}
	
	public static String getSubReportPath(Relatorio relatorio) {
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		return ctx.getRealPath("WEB-INF" + File.separator + "relatorios" + File.separator + "subrelatorios" + File.separator + relatorio.name()) + File.separator; 
	}
	
	private static String getCabecalhoPath(ServletContext ctx, ModeloCabecalho cabecalho) {
		String cabecalhoPath = ctx.getRealPath("WEB-INF" + File.separator + "relatorios" + File.separator + "cabecalhos");
		
		switch(cabecalho) {
			case PADRAO:
				cabecalhoPath = cabecalhoPath + File.separator + "CabecalhoPadrao.jasper";
				break;
			case PADRAO_PAISAGEM:
				cabecalhoPath = cabecalhoPath + File.separator + "CabecalhoPadraoPaisagem.jasper";
				break;
			case PADRAO_SEM_UNIDADE_LOGADA:
				cabecalhoPath = cabecalhoPath + File.separator + "CabecalhoPadraoSemUnidadeLogada.jasper";
				break;
			case PADRAO_PAISAGEM_SEM_UNIDADE_LOGADA:
				cabecalhoPath = cabecalhoPath + File.separator + "CabecalhoPadraoPaisagemSemUnidadeLogada.jasper";
				break;
		}
		
		return cabecalhoPath;
	}
	
	private static String getRodapePath(ServletContext ctx, ModeloRodape rodape) {
		String rodapePath = ctx.getRealPath("WEB-INF" + File.separator + "relatorios" + File.separator + "cabecalhos");
		
		switch(rodape) {
			case PADRAO:
				rodapePath = rodapePath + File.separator + "RodapePadrao.jasper";
				break;
			case PADRAO_PAISAGEM:
				rodapePath = rodapePath + File.separator + "RodapePadraoPaisagem.jasper";
				break;
		}
		
		return rodapePath;
	}
	
	public static <T> StreamedContent downloadPdf(List<T> valores, Relatorio relatorio, Map<String, Object> parameters) throws Exception {
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        
        parameters.put("CABECALHO", getCabecalhoPath(ctx, relatorio.getCabecalho()));
        parameters.put("RODAPE", getRodapePath(ctx, relatorio.getRodape()));
        parameters.put("UNIDADE_LOGADA", SecurityUtil.getUnidadeSelecionada().getNome());
        parameters.put("EMISSOR", SecurityUtil.getMatriculaUsuario());
        
        if (!parameters.containsKey("NOME_DO_RELATORIO")) {
            parameters.put("NOME_DO_RELATORIO", relatorio.getNome());
        }
        
        JRDataSource datasource = new JRBeanCollectionDataSource(valores);
        JasperPrint jasperPrint = JasperFillManager.fillReport(getRelatorioPath(ctx, relatorio), parameters, datasource);

		return new DefaultStreamedContent(new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jasperPrint)), "application/pdf", Relatorio.ListarEntradaSaida.toString()+".pdf", "application/pdf");
	}
}
