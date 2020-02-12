package br.gov.mt.sejudh.sigepen.util;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;

import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.realm.SigepenRealm;

@Named
@ApplicationScoped
public class SecurityUtil {

	public static String getLoginUsuario() {
		return SecurityUtils.getSubject().getPrincipal().toString();
	}
	
	public static String getNomeUsuario() {
		return (getUsuario().getServidor() == null) ? "Administrador" : getUsuario().getServidor().getNome();
	}
	
	public static String getMatriculaUsuario() {
		return (getUsuario().getServidor() == null) ? "0" : String.valueOf(getUsuario().getServidor().getMatricula());
	}
	
	public static String getNomeUnidadeUsuario() {
		return (getUnidadeSelecionada() == null) ? "SEJUDH" : getUnidadeSelecionada().getNome();
	}
	
	public static Usuario getUsuario() {
		Usuario usuario = (Usuario) SecurityUtils.getSubject().getSession().getAttribute("usuario");
		return usuario;
	}
	
	public static void setUsuario(Usuario usuario) {
		SecurityUtils.getSubject().getSession().setAttribute("usuario", usuario);
	}
	
	public static UnidadePrisional getUnidadeSelecionada() {
		UnidadePrisional unidade = (UnidadePrisional) SecurityUtils.getSubject().getSession().getAttribute("unidadeSelecionada");
		return unidade;
	}
	
	public static void setUnidadeSelecionada(UnidadePrisional unidade) {
		SecurityUtils.getSubject().getSession().setAttribute("unidadeSelecionada", unidade);
	}
	
	public boolean isAutenticado() {
		return SecurityUtils.getSubject().isAuthenticated();
	}
	
	public boolean possuiPerfil(String perfil) {
		return SecurityUtils.getSubject().hasRole(perfil);
	}
	
	public boolean possuiPermissao(String permissao) {
		return SecurityUtils.getSubject().isPermitted(permissao);
	}
	
	public boolean exibeCategoriaMenu(String categoria) {		
		boolean resultado = false;
		
		switch (categoria.toLowerCase()) {
			case "tabelasauxiliares":
				resultado =		this.possuiPermissao("ListarCategoria") ||			
								this.possuiPermissao("ListarCargo") ||
								this.possuiPermissao("ListarCrime") ||
								this.possuiPermissao("ListarFuncao") ||
								this.possuiPermissao("ListarEstabelecimento") ||
								this.possuiPermissao("ListarOrgao") ||
								this.possuiPermissao("ListarPessoa") ||
								this.possuiPermissao("ListarServidor") || 
								this.possuiPermissao("ListarUnidade");
				break;
				
			case "controledeacesso":
				resultado = this.possuiPermissao("ListarUsuario") || 
							this.possuiPermissao("ListarPerfil")  || 
							this.possuiPermissao("ListarPermissao");
				break;
				
			case "recuperando":
				resultado = this.possuiPermissao("ListarRecuperando");
				break;
				
			case "movimentacoes":
				resultado = this.possuiPermissao("ListarPedidoTransito") ||
							this.possuiPermissao("ListarMovimentacaoExterna") ||
							this.possuiPermissao("ListarMovimentacaoInterna") ||
							this.possuiPermissao("ListarTransferencia") ||
							this.possuiPermissao("ListarReceberTransferencia") ||
							this.possuiPermissao("ListarRemanejamento");
				break;
				
			case "auditoria":
				resultado = this.possuiPermissao("ListarLog");
				break;
		}
		
		return resultado;
	}

	public static void clearCache() {
		for (Realm realm : ((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms()) {
		    if (realm.getName().equals("jdbcRealm")) {
		    	SigepenRealm realm2 = (SigepenRealm)realm;
		    	realm2.getAuthorizationCache().remove(SecurityUtils.getSubject().getPrincipals());
		    }
		}
	}
}
