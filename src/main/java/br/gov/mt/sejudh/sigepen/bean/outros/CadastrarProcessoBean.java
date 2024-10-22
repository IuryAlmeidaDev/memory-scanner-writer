package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.bean.MetodosDePaginaBean;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCrime;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.StatusProcesso;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Crime;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.ProcessoCrime;
import br.gov.mt.sejudh.sigepen.model.ProcessoRegimeCondicao;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.service.CrimeService;
import br.gov.mt.sejudh.sigepen.service.ProcessoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
	@URLMapping(id = "CadastrarProcesso", pattern = "/processo/cadastrar", viewId = "/WEB-INF/pages/_dialogs/CadastrarProcesso.xhtml"),
	@URLMapping(id = "AlterarProcesso", pattern = "/processo/alterar", viewId = "/WEB-INF/pages/_dialogs/CadastrarProcesso.xhtml"),
	@URLMapping(id = "VisualizarProcesso", pattern = "/processo/visualizar", viewId = "/WEB-INF/pages/_dialogs/CadastrarProcesso.xhtml")
})
public class CadastrarProcessoBean extends MetodosDePaginaBean {

	private FiltroCrime filtro;
	
	private LazyDataModel<Crime> crimes;
	
	private List<String> legislacoes;
	
	private List<String> artigos;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private ProcessoService processoService;
	
	@Inject
	private CrimeService crimeService;
	
	private Processo processo;
	
	private ProcessoRegimeCondicao processoRegimeCondicoes;
	
	private ProcessoCrime processoCrime;
	
	private Recuperando recuperando;
	
	private Boolean isPassagem;
	
	private ProcessoRegimeCondicao processoRegimeCondicaoSelecionado;
	
	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configuraGridDocumentos() {
		configuraFiltroDocumentos();
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			if (this.isDialogInvalida()) {
				return "pretty:dashboard";
			}
			
			this.processoRegimeCondicoes = new ProcessoRegimeCondicao();
			this.setLegislacoes(crimeService.listarLegislacoes());
		    this.artigos = new ArrayList<String>();
		    
		    pesquisar();
		    
		    this.processo = (Processo) facesUtil.getObjetoSessao("processo");
			this.recuperando = (Recuperando) facesUtil.getObjetoSessao("recuperandoProcesso");
			this.isPassagem = (Boolean) facesUtil.getObjetoSessao("isPassagem");
		    
			switch(this.getAcaoDeTela()) {
				case CADASTRAR:
					this.processo = new Processo();
					this.processo.setRecuperando(this.recuperando);
					break;
					
				case ALTERAR:
					if(this.processo.getId() > 0){
						this.processo = processoService.buscaProcessoPorId(this.processo);
						
						if (this.processo == null) {
							throw new Exception("Erro ao buscar processo!");
						}
					}
					break;
				case VISUALIZAR:
					if(this.processo.getId() > 0){
						this.processo = processoService.buscaProcessoPorId(this.processo);
						
						if (this.processo == null) {
							throw new Exception("Erro ao buscar processo!");
						}
					}
					break;
					
				default:
					break;
			}
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}

	public void pesquisar() {
		this.setFiltro(new FiltroCrime());
		this.setCrimes(new LazyDataModel<Crime>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Crime> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {	
				setRowCount(crimeService.quantidadeFiltrados(getFiltro()));
			
				if (first >= getRowCount()) {
					first = 0;
					DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("formCrimes:dataTable");
					dt.setFirst(first);
				}
			 
				getFiltro().organizaFiltro(first, pageSize, sortField, sortOrder);
				
				return crimeService.buscaPorFiltro(getFiltro(), false);
			}
		});
	}	
	
	public void listarArtigos(){
		try {
			this.setArtigos(crimeService.listarArtigos(this.filtro.getLegislacao()));
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	
	public void excluirCrime(ProcessoCrime crime){
		if(crime != null){
			this.processo.getCrimes().remove(crime);
		}
	}
	
	public void inativarCrime(ProcessoCrime crime){
		if(crime != null){
			this.processoCrime = crime;
		}
	}
	
	public void confirmainativacaoCrime(){
		if(this.processoCrime != null){
			processoCrime.setStatus(Status.INATIVO);
			RequestContext.getCurrentInstance().execute("PF('inativarCrime').hide()");
		}
	}
	
	public void selecionarCrime(Crime crime){
		if (crime != null) {
			ProcessoCrime processoCrime = new ProcessoCrime(Status.ATIVO);
			processoCrime.setCrime(crime);
			processoCrime.setDataInclusao(new Date());
			processoCrime.setProcesso(this.processo);
			this.processo.getCrimes().add(processoCrime);
		}
	}
	
	
	public void excluirProcessoRegimeCondicaoECrime(Processo processo) throws ValidacaoException {
		try {
			
			Long idProcesso = processoService.contarProcessosPorRecuperando(processo.getRecuperando().getId());
	        
	        if (idProcesso <= 1) {
	            throw new ValidacaoException("Não é permitido excluir o processo (" + processo.getNumeroProcesso() + ").");
	        }
			
			processo = processoService.buscaProcessoPorId(processo);
			
			processoService.excluirProcesso(processo);	
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "SUCESSO", "Processo " + processo.getNumeroProcesso() + " excluído(a) com sucesso!");						
			
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir processo (" + processo.getNumeroProcesso() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, recuperando, e, log);
		}
	}

	public void salvar() {
		try {
			if (processo.getCondicaoAtual().equals(Condicao.CONDENADO) && 
				 (processo.getAno() == null || processo.getMes() == null || processo.getDia() == null || processo.getAno() + processo.getMes() + processo.getDia() <= 0)) {
				throw new ValidacaoException("Insira valores válidos no tempo de condenação!");
			}
			
			if (processo.getDia() != null && processo.getDia() > 31) {
				throw new ValidacaoException("Não é permitido inserir quantidade maior que 31 dias.");
			}
			
			if (processo.getMes() != null && processo.getMes() > 12) {
				throw new ValidacaoException("Não é permitido inserir quantidade maior que 12 meses.");
			}
			
			if (processo.getProcessosRegimeCondicao().isEmpty()){
				throw new ValidacaoException("Insira ao menos um Regime e Condição no Processo!");
			}
			
			if (processo.getCrimes().isEmpty()){
				throw new ValidacaoException("Insira ao menos um Crime!");
			}
			
			if (!this.possuiCrimeAtivo(processo.getCrimes())){
				throw new ValidacaoException("Insira ao menos um Crime ATIVO!");	
			}
			
			if (processo.getCondicaoAtual().equals(Condicao.CONDENADO) && 
					 (processo.getAno() == null || processo.getMes() == null || processo.getDia() == null || processo.getAno() + processo.getMes() + processo.getDia() <= 0)) {
					throw new ValidacaoException("Insira valores válidos no tempo de condenação!");
			}
			
			if (processo.getDia() != null && processo.getDia() > 31) {
				throw new ValidacaoException("Não é permitido inserir quantidade maior que 31 dias.");
			}
			
			if (processo.getMes() != null && processo.getMes() > 12) {
				throw new ValidacaoException("Não é permitido inserir quantidade maior que 12 meses.");
			}
			
			if (this.processo.getCondicaoAtual() == Condicao.CONDENADO) {
				this.processo.setStatusProcesso(StatusProcesso.EM_EXECUCAO);
			} else {
				this.processo.setStatusProcesso(StatusProcesso.EM_ANDAMENTO);
			}
			
			validarTempoCondenacao(false);

			if (isPassagem != null && isPassagem) {
				facesUtil.fecharPaginaDialog(this.processo);
			} else {
				processoService.salvar(this.processo);
				LoggerUtil.logInfo(Acao.SALVAR, this.processo, log);
				facesUtil.fecharPaginaDialog(this.processo.getNumeroProcesso());
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public boolean possuiCrimeAtivo(List<ProcessoCrime> crimes){
		for(ProcessoCrime crime : crimes){
			if(crime.getStatus() == Status.ATIVO){
				return true;
			}
		}
		return false;
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}
	
	public void selecionarRegimeCondicao(ProcessoRegimeCondicao prc) {
		this.processoRegimeCondicaoSelecionado = prc;
	}
	
	public void adicionarRegimeCondicao(ProcessoRegimeCondicao regimeCondicao) {
		if (regimeCondicao == null) {
			this.processoRegimeCondicoes = new ProcessoRegimeCondicao();
			this.processoRegimeCondicoes.setCondicao(this.processo.getCondicaoAtual());
			this.processoRegimeCondicoes.setRegime(this.processo.getRegimeAtual());
		} else {
			this.processoRegimeCondicoes = regimeCondicao;
		}
	}
	
	private void validarTempoCondenacao(boolean isAssociarDoc) throws ValidacaoException {
		boolean tempoVazio = (this.processo.getAno() == null || this.processo.getMes() == null || this.processo.getDia() == null);
		boolean isCondenado = false;
		boolean isProcessoCondenado = (this.processo.getCondicaoAtual() != null) ? processo.getCondicaoAtual().equals(Condicao.CONDENADO) : false;
		String msg = "Favor preencher o tempo total da condenação.";
		
		if (isAssociarDoc) {
			isCondenado = this.processoRegimeCondicoes.getCondicao().equals(Condicao.CONDENADO);

			if (isCondenado && tempoVazio && !isProcessoCondenado) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", msg);
				RequestContext.getCurrentInstance().update("formPassCrime:anoMesDia");
			}
		} else {
			if (isProcessoCondenado && tempoVazio) {
				throw new ValidacaoException(msg);
			}
		}
	}
	
	public void associarRegimeCondicao() {
		try {
			
			if (this.processoRegimeCondicoes.getModalidade() != null && this.processoRegimeCondicoes.getModalidade().equals(Modalidade.TEMPORARIA)) {
				Optional<ProcessoRegimeCondicao> optProcPreventiva = this.processo.getProcessosRegimeCondicao().stream().filter(x -> x.getModalidade().equals(Modalidade.PREVENTIVA)).findAny();
				
				if (optProcPreventiva.isPresent()) {
					throw new ValidacaoException("Operação não permitida, já existe um Documento com a Modalidade Preventiva para este Processo.");
				}
			}
			
			if (this.processoRegimeCondicoes.getDataDocumento().after(new Date())) {
				throw new ValidacaoException("Data do documento não pode ser maior que data atual.");
			}
			
			if (this.processoRegimeCondicoes.getProcesso() != null && this.processoRegimeCondicoes.getProcesso().getId() > 0) {
				this.processo.getProcessosRegimeCondicao().remove(this.processoRegimeCondicoes);
			} else {
				this.processoRegimeCondicoes.setProcesso(this.processo);
			}
			
			if (this.processoRegimeCondicoes.getEstabelecimento() == null) {
				throw new ValidacaoException("É obrigatório associar um estabelecimento.");
			}
			
			validarTempoCondenacao(true);
			
			this.processo.getProcessosRegimeCondicao().add(this.processoRegimeCondicoes);
			RequestContext.getCurrentInstance().execute("PF('dialogRegimeCondicaoProcesso').hide()");
			
			processoService.configurarModalidadeProcesso(this.processo);
			configuraFiltroDocumentos();
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		}
	}
	
	private void configuraFiltroDocumentos() {
		RequestContext.getCurrentInstance().execute("PF('regCondDT').filter()");
	}
	
	public void excluirRegimeCondicao(ProcessoRegimeCondicao regimeCondicao){
		this.processo.getProcessosRegimeCondicao().remove(regimeCondicao);
		processoService.configurarModalidadeProcesso(this.processo);
		configuraFiltroDocumentos();
		validarTempoDeCondenacao();
	}
	
	private void validarTempoDeCondenacao() {
		List<ProcessoRegimeCondicao> documentos = this.processo.getProcessosRegimeCondicao().stream().filter(p-> p.getCondicao().equals(Condicao.CONDENADO)).collect(Collectors.toList());
		
		if (documentos.size() <= 0) {
			this.processo.setAno(null);
			this.processo.setMes(null);
			this.processo.setDia(null);
		}
		
		if (this.processo.getProcessosRegimeCondicao().size() == 0) {
			this.processo.setRegimeAtual(null);
			this.processo.setCondicaoAtual(null);
		}
		
		RequestContext.getCurrentInstance().update("formPassCrime:anoMesDia");
	}
	
	public Boolean exibeAlterarDocumento(ProcessoRegimeCondicao prc) {
		//Boolean isProcessoPreventivo = ((this.processo.getModalidade()) != null && this.processo.getModalidade().equals(Modalidade.PREVENTIVA));
		Boolean isTemporario = (prc.getModalidade() != null && prc.getModalidade().equals(Modalidade.TEMPORARIA));
		Boolean isVisualizacao = getIsVisualizacao();
		Boolean isVenceHojeOuVencido = (prc.validaVencimentoDocumento()!= null && prc.validaVencimentoDocumento() <= 0);
		
		return (!isVisualizacao && isTemporario && isVenceHojeOuVencido);  
	}
	
	public void abrirDialogEstabelecimento(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarEstabelecimento", null, 1000);
	}
	
	public void associarEstabelecimento(SelectEvent event) {
		Estabelecimento estabelecimento = (Estabelecimento) event.getObject();
		
		if (estabelecimento != null) {
			this.processoRegimeCondicoes.setEstabelecimento(estabelecimento);
		}
	}

	public FiltroCrime getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroCrime filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<Crime> getCrimes() {
		return crimes;
	}
	
	public void setCrimes(LazyDataModel<Crime> crimes) {
		this.crimes = crimes;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public ProcessoRegimeCondicao getProcessoRegimeCondicoes() {
		return processoRegimeCondicoes;
	}

	public void setProcessoRegimeCondicoes(ProcessoRegimeCondicao processoRegimeCondicoes) {
		this.processoRegimeCondicoes = processoRegimeCondicoes;
	}
	
	public Date getDataMaxima(){
		return new Date();
	}

	public List<String> getLegislacoes() {
		return legislacoes;
	}

	public void setLegislacoes(List<String> legislacoes) {
		this.legislacoes = legislacoes;
	}

	public List<String> getArtigos() {
		return artigos;
	}

	public void setArtigos(List<String> artigos) {
		this.artigos = artigos;
	}

	public ProcessoCrime getProcessoCrime() {
		return processoCrime;
	}

	public void setProcessoCrime(ProcessoCrime processoCrime) {
		this.processoCrime = processoCrime;
	}

	public ProcessoRegimeCondicao getProcessoRegimeCondicaoSelecionado() {
		return processoRegimeCondicaoSelecionado;
	}

	public void setProcessoRegimeCondicaoSelecionado(ProcessoRegimeCondicao processoRegimeCondicaoSelecionado) {
		this.processoRegimeCondicaoSelecionado = processoRegimeCondicaoSelecionado;
	}
}