package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAtestado;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PassagemLotacao;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TextoDoRelatorio;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
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
	
	

    @URLAction(onPostback=false)
    public String init() {
    	RecuperandoDTO rec = (RecuperandoDTO) facesUtil.getObjetoSessao("recuperandoAtestado");
    	recuperando = atestadosService.buscarRecuperandoComPassagens(rec.getId());
    	
    	Optional<Passagem> optPassagem = recuperando.getPassagens().stream().filter(p -> 
    		p.getLotacao().stream().anyMatch(l -> l.getEstrutura().getUnidadePrisional().getId() == SecurityUtil.getUnidadeSelecionada().getId())).findFirst();
    	
    	if (optPassagem.isPresent()) {
    		Optional<PassagemLotacao> optLotacao = optPassagem.get().getLotacao().stream().filter(l ->
    				l.getEstrutura().getUnidadePrisional().getId() == SecurityUtil.getUnidadeSelecionada().getId()).findFirst();
    		
    		if (optLotacao.isPresent()) {
    			lotacao = optLotacao.get();
    		}
    	}
    	
        return null;
    }
    
    public boolean exibeBotaoAtestado(RecuperandoDTO rec) {
    	return atestadosService.verificaRecuperandoPossuiPassagemNaUnidadeLogada(rec.getId());
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
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.Atestado, getTextoAtestado(), parameters));
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
