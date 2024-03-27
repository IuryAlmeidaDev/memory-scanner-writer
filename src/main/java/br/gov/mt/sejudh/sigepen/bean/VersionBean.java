package br.gov.mt.sejudh.sigepen.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(eager=true)
@ApplicationScoped
public class VersionBean {
	
	String versao;

	@PostConstruct
	public void init() {
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("META-INF/maven/br.gov.mt.sejudh.sigepen/sigepen/pom.properties")) {
			Properties p = new Properties();
			p.load(inputStream);
			
			versao = p.getProperty("version");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getVersao() {
		return versao;
	}

}
