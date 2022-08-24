package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
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
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
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
	
	private RecuperandoView rec;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	RecuperandoService recuperandoService;
	
	@Inject
	HistoricoRecuperandoService historicoRecuperandoService;
		
	@Inject
	private FileDownloadHandlerBean fileDownload;

	private int opcaoDetalhamento = 0;
	
	public void salvar(){
		if(this.historicoRecuperando.getId() > 0)
			this.historicoRecuperando.setId(0);
		try {
			this.historicoRecuperando.setRecuperando(this.recuperando);
			this.historicoRecuperandoService.salvar(this.historicoRecuperando);
			
			listarHistoricoRecuperando();
			LoggerUtil.logInfo(Acao.SALVAR, this.historicoRecuperando, log);
			this.historicoRecuperando = new HistoricoRecuperando();
			facesUtil.exibirMensagemInfo("Histórico salvo com Sucesso!", false);
			RequestContext.getCurrentInstance().update("mensagens");
			RequestContext.getCurrentInstance().update("dialogTextEditor");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar histórico do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.historicoRecuperando, e, log);
		}
	}
	
	public void cadastrarHistoricoRecuperando(RecuperandoView recuperando) {
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

	public RecuperandoView getRec() {
		return rec;
	}

	public void setRec(RecuperandoView rec) {
		this.rec = rec;
	}
	
	public void visualizarDetalhamento(Long id) {
		this.opcaoDetalhamento  = 1;
		this.historicoRecuperando = historicoRecuperandoService.buscarHistoricoPorId(id);
		RequestContext.getCurrentInstance().execute("PF('dialogTextEditor').show()");
		RequestContext.getCurrentInstance().update("dialogTextEditor");
	}

	private String getFontName(String name) {
		String t0 = "Font";
		String t1 = "is not available to the JVM";
		if(name != null && name.contains(t0)) {
			int i0 = name.indexOf(t0) + t0.length();
			int i1 = name.indexOf(t1);
			return name.substring(i0, i1);
		}
		return "";
	}
	
	public void imprimirRegistro(HistoricoRecuperando h) {
		this.historicoRecuperando = historicoRecuperandoService.buscarHistoricoPorId(h.getId());
		List<HistoricoRecuperando> lista = new ArrayList<HistoricoRecuperando>();
		lista.add(this.historicoRecuperando);
		try {
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.HistoricoRecuperando));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, this.historicoRecuperando, log);
		} catch (Exception e) {
			String erro = e.getMessage();
			if(erro.contains("Font")) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "A font de texto " + getFontName(erro) + " não foi encontrada. Entre em contato com o Administrador.");
			} else { 
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Ocorreu um erro ao imprimir: " + erro);
			}
		}
	}

	public int getOpcaoDetalhamento() {
		return opcaoDetalhamento;
	}

	public void setOpcaoDetalhamento(int opcaoDetalhamento) {
		this.opcaoDetalhamento = opcaoDetalhamento;
	}
	
	public void adicionarDetalhamento() {
		this.opcaoDetalhamento = 0;
		RequestContext.getCurrentInstance().update("dialogTextEditor");
	}
	
	public void closeTextEditor() {
		if(this.opcaoDetalhamento == 1) {
			this.historicoRecuperando = new HistoricoRecuperando();
			RequestContext.getCurrentInstance().update("dialogTextEditor");
		}
	}
}
