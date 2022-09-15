package br.gov.mt.sejudh.sigepen.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean(eager=true)
@ApplicationScoped
public class VersionBean {
	
	String versao;
	String build;

	@PostConstruct
	public void init() {
		try (InputStream i = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/versao.properties")) {
			Properties p = new Properties();
			p.load(i);
			
			versao = p.getProperty("versao");
			build = p.getProperty("build");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getVersao() {
		return versao;
	}

	public String getBuild() {
		return build;
	}
}
