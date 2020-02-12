package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;

import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.SituacaoEstruturaUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.service.EstruturaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@ManagedBean
@ViewScoped
public class EstruturaBean extends MetodosDePaginaBean{

	@ManagedProperty(value="#{unidadePrisionalBean.unidade}")
	private UnidadePrisional unidade;

	private Estrutura estrutura;
	
	private DefaultTreeNode root;
	
	private DefaultTreeNode noSelecionado;
	
	private SituacaoEstruturaUnidade situacaoEstruturaUnidade;
	
	private List<SituacaoEstruturaUnidade> situacoes;

	@Inject
	private EstruturaService estruturaService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;

	@PostConstruct
	public void init() {
		if(this.unidade != null){
			if(this.unidade.getId()>0){				
				root = new DefaultTreeNode( new Estrutura() , null);
				root.setExpanded(true);
				estruturaService.montarArvoreEstrutura(unidade.getEstrutura(), root);
				estruturaService.capacidadeTotalEstrutura(root);
				Estrutura e = (Estrutura) root.getData();
				this.unidade.setCapacidadeTotal(e.getCapacidadeTotal());
			}
		}
		
		estrutura = new Estrutura();
		situacaoEstruturaUnidade = new SituacaoEstruturaUnidade();
	}
	
	public String novo() {
		init();
		return "pretty:CadastrarUnidade";
	}
	
	public void incluirEstruturaPai(Estrutura estrutura){
		this.estrutura = new Estrutura();
		this.estrutura.setEstruturaPai(estrutura);
		this.estrutura.setSituacaoUnidade(SituacaoUnidade.ATIVO);
		this.estrutura.setUnidadePrisional(estrutura.getUnidadePrisional());
		this.noSelecionado = estruturaService.buscarNo(root, null, estrutura);
	}
	
	public void estruturaRelacionada(Estrutura estrutura){
		this.situacaoEstruturaUnidade = new SituacaoEstruturaUnidade();
		this.estrutura = estrutura;
		this.situacaoEstruturaUnidade.setEstrutura(estrutura);
		this.situacaoEstruturaUnidade.setUnidadePrisional(estrutura.getUnidadePrisional());
	}
	
	public void salvarSituacaoEstrutura() {
		try{			
			this.estruturaService.salvarSituacaoEstrutura(this.situacaoEstruturaUnidade, root);
			
			facesUtil.exibirMensagemInfo("Situação da Estrutura salva com Sucesso!", false);
			
			LoggerUtil.logInfo(Acao.SALVAR, this.situacaoEstruturaUnidade, log);
			RequestContext.getCurrentInstance().execute("PF('situacaoEstruturaUnidade').hide()");
			RequestContext.getCurrentInstance().update("unidadeForm:tabView:arvoreEstrutura");
			RequestContext.getCurrentInstance().update("mensagens");
			init();
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}		
	}
	
	public void resetDialogGerenciarSituacaoEstrutura(String target) {
        RequestContext.getCurrentInstance().reset(target);
    }
	
	public void listarPorUnidade(UnidadePrisionalDTO unidadePrisional){
		try {
			situacoes = estruturaService.listarPorUnidade(unidadePrisional.getId());
			LoggerUtil.logInfo(Acao.VISUALIZAR, "Histórico de situações " + unidadePrisional, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public void salvar() {
		try{
			if(this.estrutura.getId() > 0){
				noSelecionado.setData(estruturaService.salvar(estrutura));
			} else {
				new DefaultTreeNode(estruturaService.salvar(estrutura), noSelecionado).setExpanded(true);
			}
			estruturaService.capacidadeTotalEstrutura(root);
			this.unidade.setCapacidadeTotal(((Estrutura) root.getData()).getCapacidadeTotal());
			
			LoggerUtil.logInfo(Acao.SALVAR, this.estrutura, log);
			facesUtil.exibirMensagemInfo("Estrutura salva com Sucesso!", false);
			RequestContext.getCurrentInstance().execute("PF('novaEstrutura').hide()");
			RequestContext.getCurrentInstance().update("unidadeForm:tabView:arvoreEstrutura");
			RequestContext.getCurrentInstance().update("mensagens");
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Não foi possivel inserir a Estrutura: " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.estrutura, e, log);
		}
	}

	public void excluir(Estrutura estrutura) {
		try{
			estruturaService.excluir(estrutura);
			
			LoggerUtil.logInfo(Acao.EXCLUIR, estrutura, log);
			facesUtil.setFlashParameter("unidade", this.unidade);
			facesUtil.exibirMensagemInfo("Estrutura excluída com Sucesso!", false);
			init();
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Não foi possivel excluir a Estrutura: " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, estrutura, e, log);
		}
	}

	public void alterar(Estrutura estrutura) {
		this.estrutura = estrutura;
		this.noSelecionado = estruturaService.buscarNo(root, null, estrutura);
		LoggerUtil.logInfo(Acao.ALTERAR, this.estrutura, log);
	}
	
	public void selecionarListaDeChamada(Estrutura est) {
		facesUtil.setObjetoSessao("estrutura", est);
		facesUtil.setObjetoSessao("tipo_relatorio", "estrutura");
		facesUtil.exibirPaginaDialog("pretty:SelecionarListaChamada", 160, 180);
	}

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public Estrutura getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(Estrutura estrutura) {
		this.estrutura = estrutura;
	}

	public DefaultTreeNode getRoot() {
		return root;
	}

	public void setRoot(DefaultTreeNode root) {
		this.root = root;
	}

	public SituacaoEstruturaUnidade getSituacaoEstruturaUnidade() {
		return situacaoEstruturaUnidade;
	}

	public void setSituacaoEstruturaUnidade(SituacaoEstruturaUnidade situacaoEstruturaUnidade) {
		this.situacaoEstruturaUnidade = situacaoEstruturaUnidade;
	}

	public List<SituacaoEstruturaUnidade> getSituacoes() {
		return situacoes;
	}

	public void setSituacoes(List<SituacaoEstruturaUnidade> situacoes) {
		this.situacoes = situacoes;
	}
	
}
