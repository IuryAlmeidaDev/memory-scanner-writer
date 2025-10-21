package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.service.GerenciarAssinaturaService;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAtestado;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TextoDoRelatorio;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.relatorio.textos.Atestados;
import br.gov.mt.sejudh.sigepen.service.AtestadosService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AtestadosRecuperando", pattern = "/recuperando/atestados", viewId = "/WEB-INF/pages/_dialogs/AtestadosDoRecuperando.xhtml")
public class AtestadosBean {
	
	private Recuperando recuperando;
	
	private PassagemLotacao lotacao;
	
	private TipoAtestado atestadoSelecionado;
	
	private String comportamento;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@Inject
	private Logger log;
	
	@Inject
	private AtestadosService atestadosService;

	@Inject
	private GerenciarAssinaturaService gerenciarAssinaturaService;

    @URLAction(onPostback=false)
    public String init() {
    	RecuperandoView rec = (RecuperandoView) facesUtil.getObjetoSessao("recuperandoAtestado");
    	recuperando = atestadosService.buscarRecuperandoComPassagens(rec.getId());
    	
    	Optional<Passagem> optPassagem = recuperando.getPassagens().stream()
    			.filter(p -> p.getLotacao().stream().anyMatch(l -> 
    				l.getEstrutura().getUnidadePrisional().getId() == SecurityUtil.getUnidadeSelecionada().getId()))
    			.max(Comparator.comparing(Passagem::getId));
    	
    	if (optPassagem.isPresent()) {
    		List<PassagemLotacao> lotacoes = optPassagem.get().getLotacao().stream()
    				.sorted(Comparator.comparing(PassagemLotacao::getId))
    				.filter(l -> l.getEstrutura().getUnidadePrisional().getId() == SecurityUtil.getUnidadeSelecionada().getId())
    				.collect(Collectors.toList());
	
    		List<PassagemLotacao> lotacaoAux = new ArrayList<>();
    		lotacoes.forEach(l -> {
    			lotacaoAux.add(l);
    			
    			if (l.getTipoTransito().equals(TipoTransito.TRANSFERENCIA)) {
    				lotacaoAux.clear();
    				lotacaoAux.add(l);
    			}
    			
    			if (l.getTipoTransito().equals(TipoTransito.REMANEJAMENTO) && l.getDataSaida() != null) {
    				lotacaoAux.add(l);
    			}
    		});

    		lotacao = new PassagemLotacao();
    		lotacao.setDataEntrada(lotacaoAux.stream().min(Comparator.comparing(PassagemLotacao::getId)).get().getDataEntrada());
    		lotacao.setEstrutura(lotacaoAux.get(0).getEstrutura());
    		
    		try {
    			lotacao.setDataSaida(lotacaoAux.stream().max(Comparator.comparing(PassagemLotacao::getId)).get().getDataSaida());
    		} catch (NoSuchElementException e) {
    			lotacao.setDataSaida(null);
    		}
    	}
    	
        return null;
    }
    
	public void imprimirAtestado() {
		try {
			UnidadePrisional unidadeAtual = lotacao.getEstrutura().getUnidadePrisional();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("NOME_DO_RELATORIO", Atestados.getNomeRelatorio(atestadoSelecionado));
			
			if (unidadeAtual.getResponsavel() != null) { 
				parameters.put("RESPONSAVEL", unidadeAtual.getResponsavel().getNome());
			}
			
			parameters.put("UNIDADE_CIDADE_UF", unidadeAtual.getEndereco().getCidade().getNome() + "-" + unidadeAtual.getEndereco().getCidade().getEstado().getUf());
			
			if(this.gerenciarAssinaturaService.buscar(unidadeAtual, Relatorio.Atestado) != null) {
				fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.Atestado, getTextoAtestado(), parameters, this.gerenciarAssinaturaService));
			} else {
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.AtestadoSemAssinatura, getTextoAtestado(), parameters));
		}
			
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Atestado de " + atestadoSelecionado.name(), log);
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Atestado de " + atestadoSelecionado.name(), e, log);
		}
	}
	
	private TextoDoRelatorio getTextoAtestado() {
		switch(atestadoSelecionado) {
			case Permanencia:
				return Atestados.getTexto(atestadoSelecionado, recuperando, lotacao);
			case Comportamento:
				return Atestados.getTexto(atestadoSelecionado, recuperando, lotacao, comportamento);
			default:
				return null;
		}
	}

	public TipoAtestado getAtestadoSelecionado() {
		return atestadoSelecionado;
	}

	public void setAtestadoSelecionado(TipoAtestado atestadoSelecionado) {
		this.atestadoSelecionado = atestadoSelecionado;
	}

	public String getComportamento() {
		return comportamento;
	}

	public void setComportamento(String comportamento) {
		this.comportamento = comportamento;
	}
}
