package br.gov.mt.sejudh.sigepen.bean;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

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
    
    //public void prepararArquivo2(File zipFile) {
    //	facesUtil.setObjetoSessao("arquivo", zipFile);
    //}
    
    public void prepararArquivo(Arquivo arquivo) {
    	facesUtil.setObjetoSessao("arquivo", arquivo);
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
