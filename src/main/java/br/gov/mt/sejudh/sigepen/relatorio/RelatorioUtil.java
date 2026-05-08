package br.gov.mt.sejudh.sigepen.relatorio;

import br.gov.mt.sejudh.sigepen.enumerator.TipoArquivo;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.GerenciarAssinatura;
import br.gov.mt.sejudh.sigepen.model.TextoDoRelatorio;
import br.gov.mt.sejudh.sigepen.service.GerenciarAssinaturaService;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static net.sf.jasperreports.engine.JasperCompileManager.compileReportToFile;

@RequestScoped
public class RelatorioUtil {

	private static final Logger log = LogManager.getLogger(RelatorioUtil.class);

	private static String dataSource;

	public static <T> Arquivo gerarRelatorio(Relatorio relatorio, Map<String, Object> parameters) throws JRException, NamingException, SQLException {
		
		Properties properties = new Properties();
        try (InputStream inputStream = Sigepen.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(inputStream);
            dataSource = properties.getProperty("sigepenJndiName");
        } catch (Exception e) {
            log.error("Erro ao carregar application.properties (RelatorioUtil)", e);
        }
		
		InitialContext initialContext = new InitialContext();
		DataSource ds = (DataSource) initialContext.lookup(dataSource);
		Connection conn = ds.getConnection();
        Arquivo arquivo;
		
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

		} finally {
			conn.close();
			ds = null;
			initialContext.close();
		}
		
		return arquivo;
	}

	public static <T> Arquivo gerarRelatorio(T valor, Relatorio relatorio) throws JRException {
        return gerarRelatorio(Collections.singletonList(valor), relatorio, new HashMap<>());
	}
	
	public static <T> Arquivo gerarRelatorio(T valor, Relatorio relatorio, Map<String, Object> parameters) throws JRException {
        return gerarRelatorio(Collections.singletonList(valor), relatorio, parameters);
	}

	public static void setAssinatura(Map<String, Object> parameters,Relatorio relatorio, GerenciarAssinaturaService gerenciarAssinaturaService){

		GerenciarAssinatura gerenciarAssinatura = gerenciarAssinaturaService.buscar(SecurityUtil.getUnidadeSelecionada(),relatorio);

		if(gerenciarAssinatura != null && !gerenciarAssinatura.getGerenciarAssinaturaServidores().isEmpty()){

			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			String pathAssinatura = ctx.getRealPath("WEB-INF" + File.separator + "relatorios" + File.separator + "cabecalhos" + File.separator + "Assinatura.jasper");

			if(relatorio.getRodape().equals(ModeloRodape.PADRAO_PAISAGEM)){

				pathAssinatura = ctx.getRealPath("WEB-INF" + File.separator + "relatorios" + File.separator + "cabecalhos" + File.separator + "AssinaturaPassagem.jasper");
			}

			parameters.put("REPORT_ASSINATURA", pathAssinatura);

			gerenciarAssinatura.getGerenciarAssinaturaServidores().forEach(  gerenciarAssinaturaServidor -> {
				if(gerenciarAssinaturaServidor.getOrdem().equals("1")){
					parameters.put("ASSINATURA_".concat("1"),gerenciarAssinaturaServidor.getServidor().getNome());
					parameters.put("CARGO_".concat("1"),gerenciarAssinaturaServidor.getServidor().getCargo().getNome());
					parameters.put("FUNCAO_".concat("1"),gerenciarAssinaturaServidor.getServidor().getFuncao().getNome());
				}
				if(gerenciarAssinaturaServidor.getOrdem().equals("2")){
					parameters.put("ASSINATURA_".concat("2"),gerenciarAssinaturaServidor.getServidor().getNome());
					parameters.put("CARGO_".concat("2"),gerenciarAssinaturaServidor.getServidor().getCargo().getNome());
					parameters.put("FUNCAO_".concat("2"),gerenciarAssinaturaServidor.getServidor().getFuncao().getNome());

				}
				if(gerenciarAssinaturaServidor.getOrdem().equals("3")){
					parameters.put("ASSINATURA_".concat("3"),gerenciarAssinaturaServidor.getServidor().getNome());
					parameters.put("CARGO_".concat("3"),gerenciarAssinaturaServidor.getServidor().getCargo().getNome());
					parameters.put("FUNCAO_".concat("3"),gerenciarAssinaturaServidor.getServidor().getFuncao().getNome());

				}
				if(gerenciarAssinaturaServidor.getOrdem().equals("4")){
					parameters.put("ASSINATURA_".concat("4"),gerenciarAssinaturaServidor.getServidor().getNome());
					parameters.put("CARGO_".concat("4"),gerenciarAssinaturaServidor.getServidor().getCargo().getNome());
					parameters.put("FUNCAO_".concat("4"),gerenciarAssinaturaServidor.getServidor().getFuncao().getNome());

				}
			});
		}

 	}
	
	public static <T> Arquivo gerarRelatorio(List<T> valores, Relatorio relatorio, Map<String, Object> parameters) throws JRException {
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		return renderizar(valores, relatorio, parameters, ctx,
				SecurityUtil.getNomeUnidadeUsuario(),
				SecurityUtil.getMatriculaUsuario());
	}

	/**
	 * Variante para execução fora do ciclo JSF (endpoints REST).
	 * Recebe ServletContext por parâmetro em vez de obter via FacesContext,
	 * e aceita unidadeLogada/emissor explícitos porque o caller pode
	 * optar por não resolvê-los via SecurityUtil.
	 */
	public static <T> Arquivo gerarRelatorioRest(List<T> valores, Relatorio relatorio, Map<String, Object> parameters,
												 ServletContext ctx, String unidadeLogada, String emissor) throws JRException {
		return renderizar(valores, relatorio, parameters, ctx,
				unidadeLogada != null ? unidadeLogada : "",
				emissor != null ? emissor : "");
	}

	private static <T> Arquivo renderizar(List<T> valores, Relatorio relatorio, Map<String, Object> parameters,
										  ServletContext ctx, String unidadeLogada, String emissor) throws JRException {
		parameters.put("CABECALHO", getCabecalhoPath(ctx, relatorio.getCabecalho()));
		parameters.put("RODAPE", getRodapePath(ctx, relatorio.getRodape()));
		parameters.put("UNIDADE_LOGADA", unidadeLogada);
		parameters.put("EMISSOR", emissor);

		if (!parameters.containsKey("NOME_DO_RELATORIO")) {
			parameters.put("NOME_DO_RELATORIO", relatorio.getNome());
		}

		JRDataSource datasource = new JRBeanCollectionDataSource(valores);
		JasperPrint jasperPrint = JasperFillManager.fillReport(getRelatorioPath(ctx, relatorio), parameters, datasource);

		byte[] pdf = JasperExportManager.exportReportToPdf(jasperPrint);

		Arquivo arquivo = new Arquivo();
		arquivo.setBytes(pdf);
		arquivo.setContentType("application/pdf");
		arquivo.setNome(relatorio.name());
		arquivo.setTipo(TipoArquivo.PDF);

		return arquivo;
	}

	public static <T> Arquivo gerarRelatorio(Relatorio relatorio, TextoDoRelatorio conteudo) throws JRException, NamingException, SQLException {
        return gerarRelatorio(relatorio, conteudo, new HashMap<>());
	}

	public static <T> Arquivo gerarRelatorio(Relatorio relatorio, TextoDoRelatorio conteudo, Map<String, Object> parameters,GerenciarAssinaturaService gerenciarAssinaturaService) throws JRException, NamingException, SQLException {
        List<TextoDoRelatorio> lista = new ArrayList<>();
		lista.add(conteudo);
		
		setAssinatura(parameters, relatorio,gerenciarAssinaturaService);

		return gerarRelatorio(lista, relatorio, parameters);
	}

	public static <T> Arquivo gerarRelatorio(List<T> valores, Relatorio relatorio, Map<String, Object> parameters,GerenciarAssinaturaService gerenciarAssinaturaService) throws JRException {
		setAssinatura(parameters, relatorio, gerenciarAssinaturaService);
		return gerarRelatorio(valores, relatorio, parameters);
	}
	
	public static <T> Arquivo gerarRelatorio(Relatorio relatorio, TextoDoRelatorio conteudo, Map<String, Object> parameters) throws JRException, NamingException, SQLException {
        List<TextoDoRelatorio> lista = new ArrayList<>();
		lista.add(conteudo);
	
		return gerarRelatorio(lista, relatorio, parameters);
	}
	
	public static <T> Arquivo gerarRelatorio(List<T> valores, Relatorio relatorio, GerenciarAssinaturaService gerenciarAssinaturaService) throws JRException {
        Map<String, Object> parameters = new HashMap<>();
		setAssinatura(parameters, relatorio,gerenciarAssinaturaService);
		return gerarRelatorio(valores, relatorio, parameters);
	}

	public static <T> Arquivo gerarRelatorio(List<T> valores, Relatorio relatorio) throws JRException {
        return gerarRelatorio(valores, relatorio, new HashMap<>());
	}
	
	public static <T> Arquivo gerarRelatorio(Relatorio relatorio) throws JRException, NamingException, SQLException {
        return gerarRelatorio(relatorio, new HashMap<>());
	}
	
	private static String getRelatorioPath(ServletContext ctx, Relatorio rel) {
        String dir = ctx.getRealPath("WEB-INF" + File.separator + "relatorios");
        String jasperPath = dir + File.separator + rel.name() + ".jasper";
        ensureCompilado(jasperPath);
        return jasperPath;
	}

	private static void ensureCompilado(String jasperPath) {
		File jasperFile = new File(jasperPath);
		if (jasperFile.exists()) {
			return;
		}
		String jrxmlPath = jasperPath.replaceAll("\\.jasper$", ".jrxml");
		File jrxmlFile = new File(jrxmlPath);
		if (!jrxmlFile.exists()) {
			return;
		}
		try {
			compileReportToFile(jrxmlPath, jasperPath);
		} catch (net.sf.jasperreports.engine.JRException e) {
			throw new RuntimeException("Falha ao compilar relatório JRXML: " + jrxmlPath + " -> " + jasperPath, e);
		}
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

		ensureCompilado(cabecalhoPath);
		return cabecalhoPath;
	}
	
	private static String getRodapePath(ServletContext ctx, ModeloRodape rodape) {
		String rodapePath = ctx.getRealPath("WEB-INF" + File.separator + "relatorios" + File.separator + "cabecalhos");
		
		switch(rodape) {
			case PADRAO:
				rodapePath = rodapePath + File.separator + "RodapePadrao.jasper";
				break;
			case PADRAO_ASSINADO:
				rodapePath = rodapePath + File.separator + "RodapePadraoAssinado.jasper";
				break;
			case PADRAO_PAISAGEM:
				rodapePath = rodapePath + File.separator + "RodapePadraoPaisagem.jasper";
				break;
		}

		ensureCompilado(rodapePath);
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
