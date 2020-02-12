package br.gov.mt.sejudh.sigepen.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.mt.sejudh.sigepen.enumerator.StatusProcesso;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.service.ProcessoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ViewScoped
@ManagedBean
public class ProcessoBean {

	private Processo processo;
	
	@Inject
	private ProcessoService processoService;
	
	@Inject
	private FacesUtil facesUtil;

	public void processoSelecionado(Processo processo){
		if(processo != null){
			this.processo = processo;
		}
	}
	
	public void salvar(){
		try{
			if(processo.getDataFinalizado() != null){
				this.processo.setStatusProcesso(StatusProcesso.FINALIZADO);
			}
			this.processoService.salvar(processo);
			
			RequestContext.getCurrentInstance().update(":tabView:formPassagem");
			RequestContext.getCurrentInstance().update("mensagens");
			RequestContext.getCurrentInstance().execute("PF('dialogFinalizarProcesso').hide()");
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
		}
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}
	
	
}
