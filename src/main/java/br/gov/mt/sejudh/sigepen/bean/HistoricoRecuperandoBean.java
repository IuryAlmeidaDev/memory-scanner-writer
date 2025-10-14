package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.exception.NaoEncontradoException;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TipoHistorico;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.HistoricoRecuperandoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean
@ViewScoped
public class HistoricoRecuperandoBean extends MetodosDePaginaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String DATE_TIME_FORMAT = "dd/MM/yyyy";
	private static final String DIALOG_TEXT_EDITOR_ID = "dialogTextEditor";

	private HistoricoRecuperando historicoRecuperando;
	private List<HistoricoRecuperando> historicos;
	private Recuperando recuperando;
	private RecuperandoView recuperandoView;

	@Inject
	private FacesUtil facesUtil;
	@Inject
	private Logger log;
	@Inject
	private RecuperandoService recuperandoService;
	@Inject
	private HistoricoRecuperandoService historicoRecuperandoService;
	@Inject
	private FileDownloadHandlerBean fileDownload;

	private Date dataInicio;
	private Date dataFim;
	private int opcaoDetalhamento = 0;

	public HistoricoRecuperandoBean() {
		this.historicoRecuperando = new HistoricoRecuperando();
	}

	public void salvar(){
		try {
			this.historicoRecuperando.setRecuperando(this.recuperando);
			this.historicoRecuperando.setServidor(SecurityUtil.getUsuario().getServidor());
			this.historicoRecuperandoService.salvar(this.historicoRecuperando);

			listarHistoricoRecuperando();
			LoggerUtil.logInfo(Acao.SALVAR, this.historicoRecuperando, log);
			this.historicoRecuperando = new HistoricoRecuperando();
			facesUtil.exibirMensagemInfo("Histórico salvo com sucesso!", false);
			RequestContext.getCurrentInstance().update("mensagens");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar histórico do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.historicoRecuperando, e, log);
		}
	}

	public void cadastrarHistoricoRecuperando(RecuperandoView recuperandoView) {
		try {
			this.historicoRecuperando = new HistoricoRecuperando();
			this.recuperandoView = recuperandoView;
			carregarRecuperando(recuperandoView.getId());
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
			if (this.recuperando != null) {
				historicos = historicoRecuperandoService.listarHistoricoRecuperandoPermitidos(this.recuperando.getId());
			} else {
				historicos = new ArrayList<>();
			}
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

	public void visualizarDetalhamento(Long id) {
		this.opcaoDetalhamento = 1;
		Optional<HistoricoRecuperando> historicoOptional = historicoRecuperandoService.buscarHistoricoPorId(id);
		if (historicoOptional.isPresent()) {
			this.historicoRecuperando = historicoOptional.get();
			RequestContext.getCurrentInstance().execute("PF('dialogTextEditor').show()");
			RequestContext.getCurrentInstance().update(DIALOG_TEXT_EDITOR_ID);
		} else {
			facesUtil.exibirMensagemErro("Histórico não encontrado para visualização.", false);
		}
	}

	private String getFontName(String name) {
		String fontKeyword = "Font";
		String notAvailableKeyword = "is not available to the JVM";
		if(name != null && name.contains(fontKeyword)) {
			int i0 = name.indexOf(fontKeyword) + fontKeyword.length();
			int i1 = name.indexOf(notAvailableKeyword);
			if (i1 > i0) {
				return name.substring(i0, i1).trim();
			}
		}
		return "";
	}

	public void imprimirRegistro(HistoricoRecuperando historicoRecuperando) {
		Optional<HistoricoRecuperando> historicoOptional = historicoRecuperandoService.buscarHistoricoPorId(historicoRecuperando.getId());
		if (historicoOptional.isPresent()) {
			List<HistoricoRecuperando> lista = new ArrayList<>();
			lista.add(historicoOptional.get());
			imprimir(lista);
		} else {
			facesUtil.exibirMensagemErro("Histórico não encontrado para impressão.", false);
		}
	}

	public void imprimirTodosRegistros() {
		try {
			List<HistoricoRecuperando> lista = historicoRecuperandoService
					.listarHistoricoRecuperandoPermitidosPorPeriodo(recuperando.getId(), dataInicio, dataFim);
			imprimir(lista);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao buscar históricos: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, this.historicoRecuperando, e, log);
		}
	}

	private void imprimir(List<HistoricoRecuperando> lista) {
		try {
			if (lista == null || lista.isEmpty()) {
				facesUtil.exibirMensagemErro("Não há registros para serem impressos.", false);
				return;
			}

			String nomeRelatorio = verificarTipoHistorico(lista);

			// Configurar parâmetros do relatório
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("NOME_DO_RELATORIO", nomeRelatorio);
			parametros.put("RECUPERANDO_NOME", recuperando.getPessoa().getNome());
			parametros.put("RECUPERANDO_NOME_SOCIAL", recuperando.getPessoa().getNomeSocial());
			parametros.put("RECUPERANDO_ID", String.valueOf(recuperando.getId()));
			parametros.put("DATA_INICIAL_RELATORIO", dataInicio != null ? new SimpleDateFormat(DATE_TIME_FORMAT).format(dataInicio) : "Não informada");
			parametros.put("DATA_FINAL_RELATORIO", dataFim != null ? new SimpleDateFormat(DATE_TIME_FORMAT).format(dataFim) : "Não informada");

			if (recuperando.getFotoFrenteAtual() != null && recuperando.getFotoFrenteAtual().getImagem() != null) {
				parametros.put("RECUPERANDO_FOTO", recuperando.getFotoFrenteAtual().getImagem().getArquivo());
			} else {
				parametros.put("RECUPERANDO_FOTO", null);
			}

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.HistoricoRecuperando, parametros));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, this.historicoRecuperando, log);

			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Relatório gerado com sucesso!");

		} catch (Exception e) {
			String erro = e.getMessage();
			if (erro != null && erro.contains("Font")) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "A fonte de texto " + getFontName(erro) + " não foi encontrada.");
			} else {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Ocorreu um erro ao gerar o relatório: " + erro);
			}
			LoggerUtil.logErro(Acao.IMPRIMIR, this.historicoRecuperando, e, log);
		}
	}

	private String verificarTipoHistorico(List<HistoricoRecuperando> lista) {
		TipoHistorico tipoPadrao = lista.get(0).getTipoHistorico();

		boolean todosIguais = lista.stream()
				.allMatch(historico -> historico.getTipoHistorico() != null && historico.getTipoHistorico().equals(tipoPadrao));

		if (todosIguais && tipoPadrao != null && tipoPadrao.getDescricao() != null) {
			return "HISTÓRICO DE " + tipoPadrao.getDescricao().toUpperCase() + " DE RECUPERANDO";
		}

		return Relatorio.HistoricoRecuperando.getNome();
	}


	public void removerHistoricoSelecionado() {
		try {
			if (historicoRecuperando != null && historicoRecuperando.getRecuperando() != null) {
				historicoRecuperandoService.removerHistoricoRecuperando(historicoRecuperando.getRecuperando().getId(), historicoRecuperando.getId());

				historicos.remove(historicoRecuperando);
				this.historicoRecuperando = new HistoricoRecuperando();

				facesUtil.exibirMensagemInfo("Histórico removido com sucesso!", false);
			} else {
				facesUtil.exibirMensagemErro("Nenhum histórico selecionado para remover.", false);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagemErro("Erro ao remover histórico: " + e.getMessage(), false);
			LoggerUtil.logErro(Acao.REMOVER, this.historicoRecuperando, e, log);
		}
	}

	public void selecionarParaRemover(HistoricoRecuperando historico) {
		this.historicoRecuperando = historico;
	}

	public int getOpcaoDetalhamento() {
		return opcaoDetalhamento;
	}

	public void setOpcaoDetalhamento(int opcaoDetalhamento) {
		this.opcaoDetalhamento = opcaoDetalhamento;
	}

	public void adicionarDetalhamento() {
		this.opcaoDetalhamento = 0;
	}

	public void closeTextEditor() {
		if(this.opcaoDetalhamento == 1) {
			this.historicoRecuperando = new HistoricoRecuperando();
		}
	}

	public HistoricoRecuperando getHistoricoRecuperando() {
		if(this.historicoRecuperando == null) {
			this.historicoRecuperando = new HistoricoRecuperando();
		}
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

	public RecuperandoView getRecuperandoView() {
		return recuperandoView;
	}

	public void setRecuperandoView(RecuperandoView recuperandoView) {
		this.recuperandoView = recuperandoView;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
}
