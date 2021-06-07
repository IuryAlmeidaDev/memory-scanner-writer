package br.gov.mt.sejudh.sigepen.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.model.Acesso;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.service.LoginService;
import br.gov.mt.sejudh.sigepen.realm.SigepenRealm;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@RequestScoped
@URLMappings(mappings = {
@URLMapping(id = "login", pattern = "/login", viewId = "/WEB-INF/pages/login/Login.xhtml"),
@URLMapping(id = "erro", pattern = "/erro", viewId = "/WEB-INF/pages/erro.xhtml"),
@URLMapping(id = "SelecionarUnidade", pattern = "/login/unidade", viewId = "/WEB-INF/pages/login/Unidade.xhtml") })
public class LoginBean {

	private String usuario;
	private String senha;	
	private UnidadePrisional unidadeSelecionada;
	private List<UnidadePrisional> unidades;

	@Inject
	private FacesUtil faces;
	
	@Inject
	private LoginService service;
	
	@Inject
	private Logger log;
	
	@PostConstruct
	public void init() {		
		UnidadePrisional uni = SecurityUtil.getUnidadeSelecionada();
		
		if (uni != null) {
			this.unidadeSelecionada = uni;
		}
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = Sigepen.formatarEspacos(usuario);
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public UnidadePrisional getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(UnidadePrisional unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public List<UnidadePrisional> getUnidades() {
		if (this.unidades == null) {
			this.unidades=  new ArrayList<UnidadePrisional>();

			for (Acesso acesso : SecurityUtil.getUsuario().getAcessos()) {
				this.unidades.add(acesso.getUnidade());
			}
		}
		
		this.unidades.sort(Comparator.comparing(UnidadePrisional::getNome));
		
		return unidades;
	}

	public void setUnidades(List<UnidadePrisional> unidades) {
		this.unidades = unidades;
	}
	
	private void clearCache() {
		for (Realm realm : ((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms()) {
		    if (realm.getName().equals("jdbcRealm")) {
		    	SigepenRealm realm2 = (SigepenRealm)realm;
		    	realm2.getAuthorizationCache().remove(SecurityUtils.getSubject().getPrincipals());
		    }
		}
	}

	public void login() {
		try {
			SecurityUtils.getSubject().login(new UsernamePasswordToken(usuario, senha));
			clearCache();
			SecurityUtils.getSubject().getSession().setAttribute("usuario", service.buscarUsuario(SecurityUtil.getLoginUsuario()));
			LoggerUtil.logInfo(Acao.LOGAR, usuario, log);
			FacesContext.getCurrentInstance().getExternalContext().redirect(Sigepen.APPLICATION_DASHBOARD_URL);
		} 
		catch (UnknownAccountException e) {
			faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar login", "Usuário ou senha inválidos.");
		}
		catch (IncorrectCredentialsException e) {
			faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar login", "Usuário ou senha inválidos.");
		}
		catch (AuthenticationException e) {
			faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar login", e.getMessage());
		} catch (Exception e) {
			faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar login", e.getMessage());
			invalidarLogin();
			LoggerUtil.logErro(e, log);
		}
	}

	public void logout() throws IOException {
		try {
		    invalidarLogin();
		    FacesContext.getCurrentInstance().getExternalContext().redirect(Sigepen.APPLICATION_LOGIN_URL);
		} catch (Exception e) {
			
		}
	}

	private void invalidarLogin() {
		SecurityUtils.getSubject().logout();
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	}
	
	public void setarUnidade() throws IOException {
		SecurityUtil.setUnidadeSelecionada(unidadeSelecionada);
		clearCache();
		LoggerUtil.logInfo(Acao.LOGAR, unidadeSelecionada, log);
		FacesContext.getCurrentInstance().getExternalContext().redirect(Sigepen.APPLICATION_DASHBOARD_URL);
	}
}
