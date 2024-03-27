package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.service.UsuarioService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "AlterarSenha", pattern = "/usuario/alterarsenha", viewId = "/WEB-INF/pages/usuario/AlterarSenha.xhtml")
public class AlterarSenhaBean {

	private Usuario usuario;
	
	@Inject
	private UsuarioService service;
	
	@Inject
	private FacesUtil facesUtil;
	
	@PostConstruct
	public void init() {
		if (this.usuario == null) {
			this.usuario = SecurityUtil.getUsuario();
		}
	}
	
	public String alterar() {
		String retorno = null;
		
		try {
			service.salvar(this.usuario, true);
			retorno = "pretty:dashboard";
			facesUtil.exibirMensagemInfo("Senha alterada com sucesso!", true);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao alterar senha: " + e.getMessage());
		}
		
		return retorno;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
