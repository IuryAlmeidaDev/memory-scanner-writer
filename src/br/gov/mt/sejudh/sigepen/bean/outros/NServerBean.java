package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import com.neurotec.biometrics.NBiometricStatus;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.model.UserTbl;
import br.gov.mt.sejudh.sigepen.model.dto.PessoaNServerDTO;
import br.gov.mt.sejudh.sigepen.service.NServerService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "NServer", pattern = "/admin/nserver", viewId = "/WEB-INF/pages/_admin/nserver/nserver.xhtml")
public class NServerBean {
	
	private Long idPessoa;
	
	private Boolean statusNServer;
	
	private Long countMemoria;
	
	private Long countPessoa;
	
	private Long countUserTbl;
	
	private List<UserTbl> userTblList;
	
	private List<PessoaNServerDTO> pessoaList;

	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private NServerService service;
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			countMemoria = 0L;
			countPessoa = 0L;
			countUserTbl = 0L;
			
			if (!SecurityUtil.getLoginUsuario().equals("ADMIN")) {
				return "pretty:dashboard";
			}
			
			carregarListas();

		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
		}
		
		return null;
	}
	
	public void carregarListas() {
		this.userTblList = service.listarUserTbl();
		this.pessoaList = service.listarPessoas();
	}
	
	public void importar() {
		try {
			NBiometricStatus status = service.importarTemplate(idPessoa);
			
			if(status.equals(NBiometricStatus.OK)) {
				facesUtil.exibirMensagemInfo("Template importado!", false);
				carregarListas();
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
				carregarListas();
			} else {
				facesUtil.exibirMensagemErro("Erro ao excluir template! " + status, false);
			}
			
			idPessoa = null;
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
		}
	}
	
	public void checkStatus() {
		countMemoria = service.checkStatus();
		countUserTbl = service.checkCountUserTbl();
		countPessoa = service.checkCountPessoa();
		statusNServer = (countMemoria >= 0);
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

	public Long getCountMemoria() {
		return countMemoria;
	}

	public void setCountMemoria(Long countMemoria) {
		this.countMemoria = countMemoria;
	}

	public Long getCountPessoa() {
		return countPessoa;
	}

	public void setCountPessoa(Long countPessoa) {
		this.countPessoa = countPessoa;
	}

	public Long getCountUserTbl() {
		return countUserTbl;
	}

	public void setCountUserTbl(Long countUserTbl) {
		this.countUserTbl = countUserTbl;
	}

	public List<UserTbl> getUserTblList() {
		return userTblList;
	}

	public void setUserTblList(List<UserTbl> userTblList) {
		this.userTblList = userTblList;
	}

	public List<PessoaNServerDTO> getPessoaList() {
		return pessoaList;
	}

	public void setPessoaList(List<PessoaNServerDTO> pessoaList) {
		this.pessoaList = pessoaList;
	}
}
