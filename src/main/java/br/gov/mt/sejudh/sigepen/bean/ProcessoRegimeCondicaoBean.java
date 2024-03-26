package br.gov.mt.sejudh.sigepen.bean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusProcesso;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.ProcessoRegimeCondicao;
import br.gov.mt.sejudh.sigepen.service.ProcessoRegimeCondicaoService;
import br.gov.mt.sejudh.sigepen.service.ProcessoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@ManagedBean
@ViewScoped
public class ProcessoRegimeCondicaoBean {
	
	private ProcessoRegimeCondicao processoRegimeCondicao;
	
	private Processo processo;
	
	@Inject
	private ProcessoService processoService;
	
	@Inject
	private ProcessoRegimeCondicaoService processoRegimeCondicaoService;
			
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@PostConstruct
	public void init(){
		if(this.processoRegimeCondicao == null){
			this.processoRegimeCondicao = new ProcessoRegimeCondicao();
		}
	}
	
	public void processoSelecionado(Processo processo){
		if(processo != null){
			this.processoRegimeCondicao.setProcesso(this.processo = processo);
			this.processoRegimeCondicao.setRegime(this.processo.getRegimeAtual());
			this.processoRegimeCondicao.setCondicao(this.processo.getCondicaoAtual());
		}
	}
	
	public void salvar(){
		try{
			
			if(this.processoRegimeCondicao.getCondicao() == Condicao.CONDENADO){
				this.processo.setStatusProcesso(StatusProcesso.EM_EXECUCAO);
			} else{
				this.processo.setStatusProcesso(StatusProcesso.EM_ANDAMENTO);
			}
			processoRegimeCondicaoService.salvar(this.processoRegimeCondicao);
			this.processo = this.processoService.salvar(processo);
			
			RequestContext.getCurrentInstance().update("tabView:formPassagem");
			RequestContext.getCurrentInstance().update("mensagens");
			RequestContext.getCurrentInstance().execute("PF('dialogProcesso').hide()");
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Não foi possível salvar o processo: " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.processoRegimeCondicao, e, log);
		}
	}

	public ProcessoRegimeCondicao getProcessoRegimeCondicao() {
		return processoRegimeCondicao;
	}

	public void setProcessoRegimeCondicao(
			ProcessoRegimeCondicao processoRegimeCondicao) {
		this.processoRegimeCondicao = processoRegimeCondicao;
	}
}
