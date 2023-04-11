package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.ParametroVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.ParametroUnidade;
import br.gov.mt.sejudh.sigepen.model.ParametroVisita;
import br.gov.mt.sejudh.sigepen.model.ParametroVisitaBloqueio;
import br.gov.mt.sejudh.sigepen.model.ParametroVisitaEstrutura;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.ContatoService;
import br.gov.mt.sejudh.sigepen.service.EnderecoService;
import br.gov.mt.sejudh.sigepen.service.EstruturaService;
import br.gov.mt.sejudh.sigepen.service.ParametroUnidadeService;
import br.gov.mt.sejudh.sigepen.service.ParametroVisitaService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarFornecedor", pattern = "/fornecedor", viewId = "/WEB-INF/pages/fornecedor/ListarFornecedor.xhtml")
,@URLMapping(id = "CadastrarFornecedor", pattern = "/fornecedor/cadastrar", viewId = "/WEB-INF/pages/fornecedor/CadastrarFornecedor.xhtml")
//,@URLMapping(id = "AlterarUnidade", pattern = "/fornecedor/alterar/#{id}", viewId = "/WEB-INF/pages/fornecedor/CadastrarFornecedor.xhtml")
//,@URLMapping(id = "VisualizarUnidade", pattern = "/fornecedor/visualizar/#{id}", viewId = "/WEB-INF/pages/fornecedor/CadastrarFornecedor.xhtml")
})
public class FornecedorBean extends MetodosDePaginaBean {

	private FiltroUnidadePrisional filtro;
	
	private Boolean disabledAbaEstrutura;

	private UnidadePrisional unidade;

	private LazyDataModel<UnidadePrisionalDTO> unidades;
	
    private Estrutura estrutura;
	
	private TreeNode root;
	
	private Contato contato;

	private List<ParametroUnidade> parametrosVisitante;
	
	@Inject
	private ServidorService servidorService;
	
	@Inject
	private UnidadePrisionalService unidadeService;
	
	@Inject
	private EnderecoService enderecoService;
	
	@Inject
	private ContatoService contatoService;

	@Inject
	private ParametroUnidadeService parametroUnidadeService;

	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private SecurityUtil security;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	private ParametroVisita parametroVisita;
	
	private List<ParametroVisita> parametrosVisita;
	
	private Long unidadeSelecionada;
	
	@Inject
	private ParametroVisitaService parametroVisitaService;

	private ParametroVisitaBloqueio bloqueio;
	
	@Inject
	private EstruturaService estruturaService;
	
	private boolean visualizacaoParametroVisita = false;
	
	private List<Estrutura> estruturaFilhos;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		if (getAcaoDeTela().equals(AcaoDeTela.ALTERAR) || getAcaoDeTela().equals(AcaoDeTela.VISUALIZAR)) {
			if(facesUtil.getQueryParameter("id") != null) {
				this.unidadeSelecionada = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
				this.parametrosVisita = this.parametroVisitaService.buscarParametrosVisita(this.unidadeSelecionada);
			}
		}
		if (!security.possuiPermissao("ListarTodasUnidades")) {
			this.setFiltro(new FiltroUnidadePrisional());
			this.filtro.setId(SecurityUtil.getUnidadeSelecionada().getId());
			
			super.carregarFiltroPaginacao(FiltroUnidadePrisional.class, this, getFiltro());
		} else {
			super.carregarFiltroPaginacao(FiltroUnidadePrisional.class, this);
		}
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.unidade = new UnidadePrisional();
					this.setDisabledAbaEstrutura(true);	
					break;
					
				case VISUALIZAR:
				case ALTERAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarUnidade(id)) {
						facesUtil.exibirMensagemErro("Unidade prisional não encontrada.", true);
						return "pretty:ListarUnidade";
					}
					
					if (!validarUnidade()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarUnidade";
					}

					logDeAcesso(log, this.unidade);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Unidades Prisionais");
					break;
					
				default:
					break;
			}
            this.carregarParametrosVisitante();
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}

	private boolean buscarUnidade(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.unidade = unidadeService.buscaPorId(id);
		
		if (this.unidade == null) {
			return false;
		}
		
		return true;
	}
	
	private boolean validarUnidade() {
		if (!security.possuiPermissao("ListarTodasUnidades") && this.unidade.getId() != SecurityUtil.getUnidadeSelecionada().getId()) {
			return false;
		}
		
		return true;
	}
	
	public String salvar() {
		String retorno = null;
		
		try {
		    parametroUnidadeService.salvarParametrosVisitanteUnidade(this.parametrosVisitante, this.unidade);
			unidadeService.salvar(this.unidade);
			alterarUnidadeSessao();
			facesUtil.exibirMensagemInfo("Unidade Prisional " + this.unidade.getNome() + " salva com Sucesso!", true);
			retorno = "pretty:ListarUnidade";			
			LoggerUtil.logInfo(Acao.SALVAR, unidade, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar unidade prisional (" + this.unidade.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, unidade, e, log);	
		}

		return retorno;
	}

	public void excluir(UnidadePrisionalDTO unidade) {
		try{
			unidadeService.excluir(unidade.getId());
			LoggerUtil.logInfo(Acao.EXCLUIR, unidade, log);
			facesUtil.exibirMensagemInfo("Unidade " + unidade.getNome() + " excluída com sucesso!", false);		
		}catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir unidade (" + unidade.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, unidade, e, log);
		}
	}
	
	public boolean possuiServidorLotado() {
		boolean possuiServidorLotado = false;
		
		try {
			possuiServidorLotado = unidadeService.possuiServidorLotado(this.unidade.getId());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao validar servidor lotado: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return possuiServidorLotado;
	}

	public void pesquisar() {
		this.unidades = new LazyDataModel<UnidadePrisionalDTO>() {
			private static final long serialVersionUID = 1L;
			
			public List<UnidadePrisionalDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters){
				setRowCount(unidadeService.quantidadeFiltrados(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return unidadeService.buscarPorFiltro(getFiltro(), false);
			}
		};
	}
	
	public Boolean possuiServidor() {
		return (this.unidade.getResponsavel() != null);
	}
	
	public void associarServidor(SelectEvent event) {
		ServidorDTO s = (ServidorDTO) event.getObject();	
		
		try {
			if (s == null) {
				return;
			}
			
			Servidor servidor = servidorService.buscarServidorSimplesPorId(s.getId());
			
			if (servidor == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Servidor não encontrado.");
				return;
			}
				
			this.unidade.setResponsavel(servidor);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao associar servidor na unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, s, e, log);
		}
	}
	
	public void abrirDialogServidor(ActionEvent event) {
		facesUtil.setObjetoSessao("unidadeLotacao", this.unidade);
		facesUtil.exibirPaginaDialog("pretty:AssociarServidor");
	}

	public void abrirDialogContato(ActionEvent event) {
		this.contato = null;
		facesUtil.exibirPaginaDialog("pretty:AssociarContato");
	}	
	
	public void abrirDialogAlterarContato(Contato contato) {
		this.contato = contato;
		facesUtil.setObjetoSessao("contato", contato);		
		facesUtil.exibirPaginaDialog("pretty:AssociarContato");
		LoggerUtil.logInfo(Acao.ALTERAR, contato, log);
	}
	
	public void abrirDialogEndereco(ActionEvent event) {
		facesUtil.setObjetoSessao("endereco", this.unidade.getEndereco());
		facesUtil.exibirPaginaDialog("pretty:AssociarEndereco");
	}
	
	public void incluirEndereco(SelectEvent event) {
		Endereco endereco = (Endereco) event.getObject();
		
		try {
			if (endereco != null) {
				endereco.setUnidadePrisional(this.unidade);
				this.unidade.setEndereco(endereco);
				
				if (this.unidade.getId() > 0) {	
					enderecoService.salvarEndereco(endereco);
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir endereço na unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, endereco, e, log);
		}
	}
	
	public void incluirContato(SelectEvent event) {
		Contato contato = (Contato) event.getObject();
		
		try {
			if (contato != null) {			
				if (this.contato != null) {
					unidade.getContatos().remove(this.contato);
					unidade.getContatos().add(contato);
				} else {
					contato.setUnidadePrisional(this.unidade);
					unidade.getContatos().add(contato);
				}
				
				if (this.unidade.getId()>0) {
					contatoService.salvarContato(contato);
					LoggerUtil.logInfo(Acao.SALVAR, contato, log);
				}else {
					LoggerUtil.logInfo(Acao.ADICIONAR, contato, log);	
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir contato na unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, contato, e, log);
		}
	}
	
	public void excluirContato(Contato contato) {
		try {
			this.unidade.getContatos().remove(contato);
			
			if (this.unidade.getId()>0) {
				contatoService.excluirContato(contato);
				LoggerUtil.logInfo(Acao.EXCLUIR, contato, log);
			}else {
				LoggerUtil.logInfo(Acao.REMOVER, contato, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir contato da unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, contato, e, log);
		}
	}
	
	public void imprimir() {
		try {
			List<UnidadePrisionalDTO> lista = unidadeService.buscarPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarUnidadePrisional));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Unidades", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de unidades: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Unidades", e, log);
		}
	}
	
	public Boolean verificarSeBiometriaEhObrigatoria() {
		return unidadeService.verificarSeBiometriaEhObrigatoria(this.unidade.getId());
	}


    private void carregarParametrosVisitante() {
        this.parametrosVisitante = new ArrayList<>();

        for (ParametroVisitante parametroVisitante : ParametroVisitante.values()) {
            ParametroUnidade parametroUnidade = new ParametroUnidade();
            parametroUnidade.setNome(parametroVisitante);

            this.parametrosVisitante.add(parametroUnidade);
        }

        if (this.unidade != null && this.unidade.getId() > 0) {
            this.carregarParametrosVisitanteUnidade();
        }
    }

    private void carregarParametrosVisitanteUnidade() {
	    try {
	    	List<ParametroUnidade> parametroUnidades = parametroUnidadeService.listarPorUnidade(this.unidade);
            this.unidade.setParametrosUnidade(parametroUnidades);
            this.unidade.setAtivaModuloVisitante(this.unidade.getParametrosUnidade().size() > 0 && parametroUnidades.stream().anyMatch(parametroUnidade -> parametroUnidade.getValidar() == SimNao.S));
            for (ParametroUnidade parametroUnidade : this.unidade.getParametrosUnidade()) {
                this.parametrosVisitante.stream().filter(x -> x.getNome() == parametroUnidade.getNome()).findFirst().ifPresent(
                        param -> param.setValueValidar(parametroUnidade.getValidar() == SimNao.S)
                );
            }
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro buscar lista de parametros: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Parametros", e, log);
        }
    }

    public void atualizarParametrosUnidade(){
		if (this.unidade.getAtivaModuloVisitante()) {
			for (ParametroUnidade parametroUnidade : this.parametrosVisitante) {
				if (parametroUnidade.getNome() == ParametroVisitante.RECEBIMENTO_DOCUMENTACAO || parametroUnidade.getNome() == ParametroVisitante.APROVACAO_CADASTRO){
					parametroUnidade.setValidar(SimNao.S);
					parametroUnidade.setValueValidar(true);
				}
			}
		} else {
			for (ParametroUnidade parametroUnidade : this.parametrosVisitante) {
				parametroUnidade.setValidar(SimNao.N);
				parametroUnidade.setValueValidar(false);
			}
		}
	}

	public void selecionarEstruturaChamada(UnidadePrisionalDTO unidade) {
		facesUtil.setObjetoSessao("unidade", unidade);
		facesUtil.setObjetoSessao("tipo_relatorio", "unidade");
	}

	public void selecionarEstruturaChamada(Estrutura estrutura) {
		facesUtil.setObjetoSessao("estrutura", estrutura);
		facesUtil.setObjetoSessao("tipo_relatorio", "estrutura");
	}

	private void alterarUnidadeSessao() {
		if (getUnidade().getId() == SecurityUtil.getUnidadeSelecionada().getId()) {
			SecurityUtil.setUnidadeSelecionada(getUnidade());
		}
	}
	
	public FiltroUnidadePrisional getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroUnidadePrisional filtro) {
		this.filtro = filtro;
	}

	public UnidadePrisional getUnidade() {
		return unidade;
	}

	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public LazyDataModel<UnidadePrisionalDTO> getUnidades() {
		return unidades;
	}

	public void setUnidades(LazyDataModel<UnidadePrisionalDTO> unidades) {
		this.unidades = unidades;
	}

	public Estrutura getEstrutura() {
		return estrutura;
	}

	public void setEstrutura(Estrutura estrutura) {
		this.estrutura = estrutura;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public Boolean getDisabledAbaEstrutura() {
		return disabledAbaEstrutura;
	}

	public void setDisabledAbaEstrutura(Boolean disabledAbaEstrutura) {
		this.disabledAbaEstrutura = disabledAbaEstrutura;
	}

    public List<ParametroUnidade> getParametrosVisitante() {
        return parametrosVisitante;
    }

    public void abrirDialogEstrutura(ActionEvent event) {
    	facesUtil.setObjetoSessao("unidadeSelecionadaUsuario", this.unidadeSelecionada);
        facesUtil.exibirPaginaDialog("pretty:AssociarQualquerEstrutura");
    }
    
    private void incluirEstrutura(Estrutura est) throws ValidacaoException {
    	this.validarTipoEntrada();
		ParametroVisitaEstrutura n = new ParametroVisitaEstrutura();
		n.setEstrutura(est);
		n.setParametroVisita(this.parametroVisita);
		if(this.parametroVisita.getEstruturas() == null)
			this.parametroVisita.setEstruturas(new ArrayList<ParametroVisitaEstrutura>());
		boolean jaCad = false;
		for(ParametroVisitaEstrutura pe : this.parametroVisita.getEstruturas()) {
			if( est.getId() == pe.getEstrutura().getId())
				jaCad = true;
		}
		if(!jaCad) {
			this.parametroVisitaService.validarEstrutura(this.parametroVisita, est);
			this.parametroVisita.getEstruturas().add(n);
		}
    }
    
    public void associarEstrutura(SelectEvent event) {
        Estrutura est = (Estrutura) event.getObject();
    	try {
            if (est == null) return;
            if(this.parametroVisita != null) {
            	incluirEstrutura(est);
            }
			
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, est, e, log);
		}
    }

	public List<ParametroVisita> getParametrosVisita() {
		return parametrosVisita;
	}

	public void setParametrosVisita(List<ParametroVisita> parametrosVisita) {
		this.parametrosVisita = parametrosVisita;
	}

	public void incluirParametroVisita() {
		boolean alterar = false;
		if(this.parametroVisita.getId() != null)
			alterar = true;
		try {
			this.validarTipoEntrada();
			this.validarDias();
			this.validarHorarios();
			this.validarEstrutura();
			this.validarDiaSemana();
			this.validarSalas();
			this.parametroVisita = parametroVisitaService.salvar(this.parametroVisita);
			this.parametrosVisita = this.parametroVisitaService.buscarParametrosVisita(this.unidadeSelecionada);
			if(alterar) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "", "Parâmetro Entrada/Saída alterado com sucesso!");
				LoggerUtil.logInfo(Acao.ALTERAR, this.parametroVisita, log);	
			}else {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "", "Parâmetro Entrada/Saída salvo com sucesso!");
				LoggerUtil.logInfo(Acao.ADICIONAR, this.parametroVisita, log);	
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			if(alterar) 
				LoggerUtil.logErro(Acao.ALTERAR, this.parametroVisita, e, log);
			else
				LoggerUtil.logErro(Acao.ADICIONAR, this.parametroVisita, e, log);
		}
	}
	
	

	public void incluirParametroVisitaBloqueio() {
		try {
			this.validarHorariosBloqueio();
			this.parametroVisita.getBloqueios().add(this.bloqueio);
			this.parametroVisita = parametroVisitaService.salvar(this.parametroVisita);
			this.parametrosVisita = this.parametroVisitaService.buscarParametrosVisita(this.unidadeSelecionada);
			this.bloqueio = new ParametroVisitaBloqueio();
			this.bloqueio.setParametroVisita(this.parametroVisita);
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "", "Parâmetro Entrada/Saída bloqueada com sucesso!");
			LoggerUtil.logInfo(Acao.ADICIONAR, this.bloqueio, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, this.bloqueio, e, log);
		}
	}
	
	
	private void validarSalas()  throws ValidacaoException {

		if (this.parametroVisita.getQuantidadeSala() != null && this.parametroVisita.getQuantidadeSala() < 0) {
			throw new ValidacaoException("Quantidade de sala não pode ser negativa");
		}
		
	}
	
	
	private void validarHorariosBloqueio() throws ValidacaoException {

		Date dataInicio = this.bloqueio.getDataInicio();
		Date dataFim = this.bloqueio.getDataFim();
		String descricaoBloqueio = this.bloqueio.getDescricao();

		if (dataFim.before(dataInicio)) {
			throw new ValidacaoException("Data/Hora fim menor que data/hora início!");
		}
		
		 if(dataInicio.equals(dataFim)) {
			 throw new ValidacaoException("Data/Hora Início não pode ser igual a Data/Hora Fim! Favor Alterar!");
		 }
		
		for (ParametroVisitaBloqueio b : this.parametroVisita.getBloqueios()) {
			if ((descricaoBloqueio.equals(b.getDescricao()) && dataFim.equals(b.getDataFim()) && dataInicio.equals(b.getDataInicio()))) {
				throw new ValidacaoException("Cadastro não permitido! Já existe um cadastro de bloqueio com os mesmos dados!");
			}
		}

		for(ParametroVisitaBloqueio b : this.parametroVisita.getBloqueios()) {
			if (!(dataFim.before(b.getDataInicio()) || dataInicio.after(b.getDataFim()))) {
				throw new ValidacaoException("Cadastro não permitido! Já existe um cadastro de bloqueio com os mesmos dados!");
			}
		}
	}
	
	
	private void validarHorarios() throws ValidacaoException {
		if (this.parametroVisita.entrada() > 2359) {
			throw new ValidacaoException("Hora início inválida!");
		}
		
		if (this.parametroVisita.saida() > 2359) {
			throw new ValidacaoException("Hora fim inválida!");
		}
		
		if(this.parametroVisita.getTipoEntrada() != TipoEntrada.FAMILIA &&
				((this.parametroVisita.getHoraEntrada() == null) ||
				(this.parametroVisita.getHoraEntrada() != null && "".equals(this.parametroVisita.getHoraEntrada())))) {
			throw new ValidacaoException("Obrigatório informar hora início!");
		}
		if(this.parametroVisita.getTipoEntrada() != TipoEntrada.FAMILIA &&
				((this.parametroVisita.getHoraSaida() == null) ||
				(this.parametroVisita.getHoraSaida() != null && "".equals(this.parametroVisita.getHoraSaida())))) {
			throw new ValidacaoException("Obrigatório informar hora fim!");
		}
		if (((this.parametroVisita.getHoraEntrada() == null)
				|| (this.parametroVisita.getHoraEntrada() != null && "".equals(this.parametroVisita.getHoraEntrada())))
				&& this.parametroVisita.saida() > 0) {
			throw new ValidacaoException("Obrigatório informar hora início!");
		}
		if (((this.parametroVisita.getHoraSaida() == null)
				|| (this.parametroVisita.getHoraSaida() != null && "".equals(this.parametroVisita.getHoraSaida())))
				&& this.parametroVisita.entrada() > 0) {
			throw new ValidacaoException("Obrigatório informar hora fim!");
		}
		
		if(this.parametroVisita.saida() < this.parametroVisita.entrada())
			throw new ValidacaoException("Hora fim não pode ser menor que hora início!");
		
		if(this.parametroVisita.getTipoEntrada() != TipoEntrada.FAMILIA && 
				this.parametroVisita.entrada() == this.parametroVisita.saida()) {
			throw new ValidacaoException("Hora Início não pode ser igual a Hora Fim! Favor Alterar!");
		}
		
		if(this.parametroVisita.getTipoEntrada() != TipoEntrada.FAMILIA &&
				((this.parametroVisita.getIntervalo() == null) ||
				(this.parametroVisita.getIntervalo() != null && this.parametroVisita.getIntervalo() == 0))) {
			throw new ValidacaoException("Obrigatório informar o intervalo!");
		}
		if(this.parametroVisita.getTipoEntrada() != TipoEntrada.FAMILIA &&
				((this.parametroVisita.getQuantidadeSala() == null) ||
				(this.parametroVisita.getQuantidadeSala() != null && this.parametroVisita.getQuantidadeSala() == 0))) {
			throw new ValidacaoException("Quantidade de salas é obrigatório!");
		}
	}

	public void abrirDialogAdicionarParametroVisita() {
		this.parametroVisita = new ParametroVisita();
		this.parametroVisita.setTipoEntrada(TipoEntrada.FAMILIA);
		this.parametroVisita.setUnidade(this.unidade);
		this.parametroVisita.setBloqueios(new ArrayList<ParametroVisitaBloqueio>());
		this.parametroVisita.setEstruturas(new ArrayList<ParametroVisitaEstrutura>());
		this.visualizacaoParametroVisita = false;
	}
	
	public void abrirDialogAlterarParametroVisita(ParametroVisita p) {
		this.parametroVisita = p;
		this.visualizacaoParametroVisita = false;
		LoggerUtil.logInfo(Acao.ALTERAR, p, log);
	}

	public void abrirDialogBloquearParametroVisita(ParametroVisita p) {
		this.parametroVisita = p;
		this.bloqueio = new ParametroVisitaBloqueio();
		this.bloqueio.setParametroVisita(this.parametroVisita);
		LoggerUtil.logInfo(Acao.ALTERAR, p, log);
	}

	public void excluirParametroVisita(ParametroVisita p) {
		try {
			this.parametrosVisita.remove(p);
			this.unidade.getParametrosVisita().remove(p);
			
			if (this.unidade.getId()>0) {
				parametroVisitaService.excluirParametroVisita(p);
				this.parametrosVisita = this.parametroVisitaService.buscarParametrosVisita(this.unidadeSelecionada);
				LoggerUtil.logInfo(Acao.EXCLUIR, p, log);
			}else {
				LoggerUtil.logInfo(Acao.REMOVER, p, log);
			}
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "", "Parâmetro Visita excluído com sucesso!");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir contato da unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, p, e, log);
		}
	}

	public ParametroVisita getParametroVisita() {
		return parametroVisita;
	}

	public void setParametroVisita(ParametroVisita parametroVisita) {
		this.parametroVisita = parametroVisita;
	}

	public ParametroVisitaBloqueio getBloqueio() {
		return bloqueio;
	}

	public void setBloqueio(ParametroVisitaBloqueio bloqueio) {
		this.bloqueio = bloqueio;
	}
	
	public void excluirParametroVisitaEstrutura(ParametroVisitaEstrutura estr) {
		try {
			this.parametroVisita.getEstruturas().remove(estr);
			
			if (this.unidade.getId()>0) {
				this.parametroVisita = parametroVisitaService.salvar(this.parametroVisita);
				this.parametrosVisita = this.parametroVisitaService.buscarParametrosVisita(this.parametroVisita.getUnidade().getId());
				LoggerUtil.logInfo(Acao.EXCLUIR, estr, log);
			}else {
				LoggerUtil.logInfo(Acao.REMOVER, estr, log);
			}
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "", "Estrutura excluída com sucesso!");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir contato da unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, estr, e, log);
		}
	}
	
	private void validarDiaSemana() throws ValidacaoException {
		this.parametroVisitaService.parametroExistente(this.parametroVisita);
	}
	
	private void validarEstrutura() throws ValidacaoException {
		if(this.parametroVisita != null && (this.parametroVisita.getEstruturas() == null || (this.parametroVisita.getEstruturas() != null && this.parametroVisita.getEstruturas().size() == 0))) {
			throw new ValidacaoException("Obrigatório informar ao menos uma estrutura!");
		}
	}
	
	private void validarTipoEntrada() throws ValidacaoException {
		if(this.parametroVisita.getTipoEntrada() == null) {
			throw new ValidacaoException("Tipo de Entrada é obrigatório!");
		}
	}

	private void validarDias() throws ValidacaoException {
		if(!this.parametroVisita.isDomingo() &&
				!this.parametroVisita.isSegunda() &&
				!this.parametroVisita.isTerca() &&
				!this.parametroVisita.isQuarta() &&
				!this.parametroVisita.isQuinta() &&
				!this.parametroVisita.isSexta() &&
				!this.parametroVisita.isSabado())
			throw new ValidacaoException("Obrigatório selecionar um dia!");
	}

	public boolean isVisualizacaoParametroVisita() {
		return visualizacaoParametroVisita;
	}

	public void setVisualizacaoParametroVisita(boolean visualizacaoParametroVisita) {
		this.visualizacaoParametroVisita = visualizacaoParametroVisita;
	}
	
	public void abrirDialogVisualizarParametroVisita(ParametroVisita p) {
		this.parametroVisita = p;
		this.visualizacaoParametroVisita = true;
		LoggerUtil.logInfo(Acao.VISUALIZAR, p, log);
	}
	
	public void excluirParametroVisitaBloqueio(ParametroVisitaBloqueio bloqueio) {
		this.parametroVisita.getBloqueios().remove(bloqueio);
		this.parametroVisita = parametroVisitaService.salvar(this.parametroVisita);
		facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "", "Bloqueio excluído com sucesso!");
		LoggerUtil.logInfo(Acao.REMOVER, bloqueio, log);
	}
	
	public void abrirDialogEstruturaFilhos(ParametroVisitaEstrutura estr) throws ValidacaoException {
		this.estruturaFilhos = new ArrayList<Estrutura>();
    	estruturaService.buscarEstruturasFilhos(estr.getEstrutura(), this.estruturaFilhos);
	}

	public List<Estrutura> getEstruturaFilhos() {
		return estruturaFilhos;
	}

	public void setEstruturaFilhos(List<Estrutura> estruturaFilhos) {
		this.estruturaFilhos = estruturaFilhos;
	}
	
	
}
