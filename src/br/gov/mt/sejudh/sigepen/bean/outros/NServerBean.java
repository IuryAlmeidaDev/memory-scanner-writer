package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import com.neurotec.biometrics.NBiometricStatus;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.service.NServerService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "NServer", pattern = "/admin/nserver", viewId = "/WEB-INF/pages/_admin/nserver/nserver.xhtml")
public class NServerBean {
	
	private Long idPessoa;
	
	private Boolean statusNServer;

	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private NServerService service;
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			if (!SecurityUtil.getLoginUsuario().equals("ADMIN")) {
				return "pretty:dashboard";
			}

		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
		}
		
		return null;
	}
	
	public void importar() {
		try {
			NBiometricStatus status = service.importarTemplate(idPessoa);
			
			if(status.equals(NBiometricStatus.OK)) {
				facesUtil.exibirMensagemInfo("Template importado!", false);
			} else {
				facesUtil.exibirMensagemErro("Erro ao importar template! " + status, false);
			}
			
			idPessoa = null;
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
		}
	}
	
	public void excluir() {
		try {		
			NBiometricStatus status = service.excluirTemplate(idPessoa);
			
			if(status.equals(NBiometricStatus.OK)) {
				facesUtil.exibirMensagemInfo("Template excluído!", false);
			} else {
				facesUtil.exibirMensagemErro("Erro ao excluir template! " + status, false);
			}
			
			idPessoa = null;
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
		}
	}
	
	public void checkStatus() {
		statusNServer = service.checkStatus();
	}

	public Long getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(Long idPessoa) {
		this.idPessoa = idPessoa;
	}

	public Boolean getStatusNServer() {
		return statusNServer;
	}

	public void setStatusNServer(Boolean statusNServer) {
		this.statusNServer = statusNServer;
	}
}
