package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.seguranca.report.service.ProcessarPDFService;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@ManagedBean
@ApplicationScoped
@URLMapping(id="download", pattern="/download", viewId = "/WEB-INF/pages/_download/download.xhtml")
public class FileDownloadHandlerBean {
	
	@Inject
	private FacesUtil facesUtil;
	
	public void download() throws IOException {
    	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    	Arquivo arquivo = (Arquivo) facesUtil.getObjetoSessao("arquivo", true);
    	
    	if (arquivo != null && arquivo.getNome() != null) {
	    	HttpServletResponse response = (HttpServletResponse) ec.getResponse();
	
	        response.setContentType(arquivo.getContentType());
	        response.setContentLength(arquivo.getBytes().length);
	        response.setHeader("Content-disposition", "attachment; filename=" + arquivo.getNome());
	
	        ServletOutputStream os = response.getOutputStream();
	        os.write(arquivo.getBytes());
	        os.flush();
	        os.close();
	        FacesContext.getCurrentInstance().responseComplete();
    	} else {
    		throw new RuntimeException("Arquivo ou nome do arquivo está nulo.");
    	}
    }

    public void prepararArquivo(Arquivo arquivo) {
        byte[] bytesNovo;
		String loginUser = String.valueOf(SecurityUtil.getUsuario().getId());

    	ProcessarPDFService service = new ProcessarPDFService();

    	String hashImpressao = service.geraHashImpressaoRelatorio(loginUser);


		bytesNovo = service.processarPdfByte(hashImpressao, arquivo.getBytes());
    	facesUtil.setObjetoSessao("hashImpressao", hashImpressao);
    	arquivo.setBytes(bytesNovo);
    	facesUtil.setObjetoSessao("arquivo", arquivo);
    }

    /**
     * Coloca o arquivo na sessao para download sem aplicar o processamento PDF
     * (que marcaria os bytes com hash de impressao e quebraria o zip do XLSX).
     * O hash de impressao continua sendo gerado e persistido na sessao para que
     * LoggerUtil.getHashImpressao() consiga rastrear a exportacao no audit log.
     */
    public void prepararArquivoXlsx(Arquivo arquivo) {
        String loginUser = String.valueOf(SecurityUtil.getUsuario().getId());
        String hashImpressao = new ProcessarPDFService().geraHashImpressaoRelatorio(loginUser);
        facesUtil.setObjetoSessao("hashImpressao", hashImpressao);
        facesUtil.setObjetoSessao("arquivo", arquivo);
    }


    public byte[] enviarPDFParaAdicionarMarcaDagua(byte[] inputPdfBytes, String watermarkText) throws IOException {
        HttpURLConnection connection = null;
        ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();
        Properties properties = new Properties();
        String requestUrl = "http://localhost:8080/ws-sesp-audit-report/api/v1/processar/add-watermark";

        try (InputStream inputStream = Sigepen.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(inputStream);
            requestUrl = properties.getProperty("url.ws.sesp.audit.report");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String boundary = "boundary"; // sem os traços `--`

        try {
            // Configuração da conexão HTTP
            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Accept", "application/pdf");

            // Construir o corpo da requisição com o array de bytes do PDF e o texto da marca d'água
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                // Adicionar o PDF
                outputStream.writeBytes("--" + boundary + "\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"input.pdf\"\r\n");
                outputStream.writeBytes("Content-Type: application/pdf\r\n\r\n");
                outputStream.write(inputPdfBytes);
                outputStream.writeBytes("\r\n");

                // Adicionar o texto da marca d'água
                outputStream.writeBytes("--" + boundary + "\r\n");
                outputStream.writeBytes("Content-Disposition: form-data; name=\"watermarkText\"\r\n\r\n");
                outputStream.writeBytes(watermarkText);
                outputStream.writeBytes("\r\n");

                // Finalizar o corpo da requisição com o fechamento do boundary
                outputStream.writeBytes("--" + boundary + "--\r\n");
                outputStream.flush();
            }

            // Receber a resposta (PDF com marca d'água)
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    responseOutputStream.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("PDF enviado e resposta com marca d'água recebida com sucesso!");
            return responseOutputStream.toByteArray();

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    public  String geraHashImpressaoRelatorio() {


    	String loginUser = String.valueOf(SecurityUtil.getUsuario().getId());

        // Captura a data e hora atual
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Concatena o ID do usuário com o timestamp para criar uma string única
        String dataToHash = loginUser + timestamp;

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] encodedHash = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

            // Converte o hash em uma string hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar o hash", e);
        }
    }


    public void prepararArquivo2(File zipFile) throws IOException {
        Arquivo arquivo = converterFileParaArquivo(zipFile);
        facesUtil.setObjetoSessao("arquivo", arquivo);
    }


    public Arquivo converterFileParaArquivo(File file) throws IOException {
        Arquivo arquivo = new Arquivo();
        arquivo.setNome(file.getName());
        arquivo.setContentType("application/zip"); // Ou o tipo de conteúdo apropriado
        arquivo.setBytes(Files.readAllBytes(file.toPath()));
        arquivo.setCaminho(file.getAbsolutePath());
        return arquivo;
    }

    public void prepararArquivo22(File zipFile) throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        externalContext.setResponseContentType("application/zip");
        externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + zipFile.getName() + "\"");

        try (InputStream inputStream = new FileInputStream(zipFile);
             OutputStream outputStream = externalContext.getResponseOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            Logger.getLogger(FileDownloadHandlerBean.class.getName()).log(Level.SEVERE, "Erro ao preparar o arquivo para download", e);
            throw e;
        } finally {
            facesContext.responseComplete();
        }
    }



}
