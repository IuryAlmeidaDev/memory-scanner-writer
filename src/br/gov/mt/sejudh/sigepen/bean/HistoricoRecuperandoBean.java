package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.exception.NaoEncontradoException;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
import br.gov.mt.sejudh.sigepen.service.HistoricoRecuperandoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@ManagedBean
@ViewScoped
public class HistoricoRecuperandoBean extends MetodosDePaginaBean{

	private HistoricoRecuperando historicoRecuperando;
	
	private List<HistoricoRecuperando> historicos;
	
	private Recuperando recuperando;
	
	private RecuperandoDTO rec;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	RecuperandoService recuperandoService;
	
	@Inject
	HistoricoRecuperandoService historicoRecuperandoService;
		
	public void salvar(){
		try {
			this.historicoRecuperando.setRecuperando(this.recuperando);
			this.historicoRecuperandoService.salvar(this.historicoRecuperando);
			
			listarHistoricoRecuperando();
			LoggerUtil.logInfo(Acao.SALVAR, this.historicoRecuperando, log);
			this.historicoRecuperando = new HistoricoRecuperando();
			facesUtil.exibirMensagemInfo("Histórico salvo com Sucesso!", false);
			RequestContext.getCurrentInstance().update("mensagens");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar histórico do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.historicoRecuperando, e, log);
		}
	}
	
	public void cadastrarHistoricoRecuperando(RecuperandoDTO recuperando) {
		try {
			this.historicoRecuperando = new HistoricoRecuperando();
			this.rec = recuperando;
			carregarRecuperando(recuperando.getId());
			listarHistoricoRecuperando();
			this.historicoRecuperando.setRecuperando(this.recuperando);
			LoggerUtil.logInfo(Acao.VISUALIZAR, this.historicoRecuperando, log);
		} catch (NaoEncontradoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(null, this.historicoRecuperando, e, log);
		}
	}
	
	public void listarHistoricoRecuperando(){
		try {
			historicos = historicoRecuperandoService.listarHistoricoRecuperando(this.recuperando.getId());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar histórico do recuperando: " + e.getMessage());
			LoggerUtil.logErro(null, this.historicoRecuperando, e, log);
		}
	}
	
	private void carregarRecuperando(long recuperandoId) throws NaoEncontradoException {
		this.recuperando = recuperandoService.buscarRecuperandoPorId(recuperandoId, false);
		
		if (this.recuperando == null) {
			throw new NaoEncontradoException("Não foi possível carregar o recuperando (Cód: " + recuperandoId + ").");
		}
	}
	
	
	public HistoricoRecuperando getHistoricoRecuperando() {
		return historicoRecuperando;
	}

	public void setHistoricoRecuperando(HistoricoRecuperando historicoRecuperando) {
		this.historicoRecuperando = historicoRecuperando;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public List<HistoricoRecuperando> getHistoricos() {
		return historicos;
	}

	public void setHistoricos(List<HistoricoRecuperando> historicos) {
		this.historicos = historicos;
	}

	public RecuperandoDTO getRec() {
		return rec;
	}

	public void setRec(RecuperandoDTO rec) {
		this.rec = rec;
	}
	
}
