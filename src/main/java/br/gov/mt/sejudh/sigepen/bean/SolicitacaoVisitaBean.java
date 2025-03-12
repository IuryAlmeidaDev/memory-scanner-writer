package br.gov.mt.sejudh.sigepen.bean;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroSolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.PessoaSolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.SolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.SolicitacaoVisitaDTO;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.SolicitacaoVisitaService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.PermissaoUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "ListarSolicitacaoVisita", pattern = "/solicitacaoVisita", viewId = "/WEB-INF/pages/solicitacaoVisita/ListarSolicitacaoVisita.xhtml"),
        @URLMapping(id = "CadastrarSolicitacaoVisita", pattern = "/solicitacaoVisita/cadastrar", viewId = "/WEB-INF/pages/solicitacaoVisita/CadastrarSolicitacaoVisita.xhtml"),
        @URLMapping(id = "AlterarSolicitacaoVisita", pattern = "/solicitacaoVisita/alterar/#{id}", viewId = "/WEB-INF/pages/solicitacaoVisita/CadastrarSolicitacaoVisita.xhtml"),
        @URLMapping(id = "VisualizarSolicitacaoVisita", pattern = "/solicitacaoVisita/visualizar/#{id}", viewId = "/WEB-INF/pages/solicitacaoVisita/CadastrarSolicitacaoVisita.xhtml")
})
public class SolicitacaoVisitaBean extends MetodosDePaginaBean {

    private FiltroSolicitacaoVisita filtro;

    private SolicitacaoVisita solicitacaoVisita;

    private LazyDataModel<SolicitacaoVisitaDTO> lazyDataModel;

    @Inject
    private SolicitacaoVisitaService service;

    @Inject
    private RecuperandoService recuperandoService;

    @Inject
    private FacesUtil facesUtil;

    @Inject
    private Logger log;

    @Inject
    private PermissaoUtil permissaoUtil;

    @Inject
    private UnidadePrisionalService unidadeService;
    
    @Inject
    private FileDownloadHandlerBean fileDownload;

    
    
    private Recuperando recuperandoSelecionado;
    
    private static final String FILHOS = "Filho(a)";

    @Inject
	private SecurityUtil securityUtil;
    
    @URLAction(onPostback = false)
    public String init() {
    	
        try {
        	
        	FiltroSolicitacaoVisita filtroDash = (FiltroSolicitacaoVisita)facesUtil.getFlashParameter("filtroSolicitacaoVisitaDash");
			
			if (filtroDash != null) {
				setFiltro(filtroDash);
			} else {
				FiltroSolicitacaoVisita filtroAux = new FiltroSolicitacaoVisita();

            	if(getFiltro() == null) {
            		setFiltro(filtroAux);
            	}else {
            		super.carregarFiltroPaginacao(FiltroSolicitacaoVisita.class, this);
            	}
				
			}
        	
            switch (this.getAcaoDeTela()) {
                case CADASTRAR:
                    this.iniciarDadosCadastro();
                    break;

                case ALTERAR:
                case VISUALIZAR:
                    long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

                    if (!buscarSolicitacaoVisita(id)) {
                        facesUtil.exibirMensagemErro("SolicitacaoVisita não encontrado.", true);
                        return "pretty:ListarSolicitacaoVisita";
                    }

                    this.iniciarDadosAlteracao();
                    logDeAcesso(log, this.solicitacaoVisita);
                    break;

                case LISTAR:
                    pesquisar();
                    logDeAcesso(log, "SolicitacaoVisitas");
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
            LoggerUtil.logErro(e, log);
        }

        return null;
    }

    private void iniciarDadosCadastro() {
        this.solicitacaoVisita = new SolicitacaoVisita();
        this.solicitacaoVisita.setPessoaSolicitacao(new PessoaSolicitacaoVisita());
    }

    private void iniciarDadosAlteracao() {
    }

    private boolean buscarSolicitacaoVisita(long id) {
        if (id <= 0) {
            return false;
        }

//        this.solicitacaoVisita = service.buscarPorId(id, true);
        this.solicitacaoVisita = service.buscarSolicitacaoVisitaPorId(id, true);
        
        if(this.solicitacaoVisita.getRecuperando() != null){
        	Hibernate.initialize(this.solicitacaoVisita.getRecuperando());
            FiltroRecuperandoView filtroRecuperando = new FiltroRecuperandoView();
            filtroRecuperando.setId(this.solicitacaoVisita.getRecuperando().getId());
            
            List<RecuperandoView> recuperandov = recuperandoService.buscarRecuperandosPorFiltro(filtroRecuperando, false);

            if(!recuperandov.isEmpty()){
                this.solicitacaoVisita.getRecuperando().setLotacaoAtual(recuperandov.get(0).getNomeUnidade());
            }
        }

        return this.solicitacaoVisita != null;
    }

    public String salvar() {
        String retorno = null;

        try {
            service.salvar(this.solicitacaoVisita);
            facesUtil.exibirMensagemInfo("Solicitacao de Visita salvo com sucesso!", true);
            retorno = "pretty:ListarSolicitacaoVisita";
            LoggerUtil.logInfo(Acao.SALVAR, this.solicitacaoVisita, log);
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar Solicitacao de Visita (" + this.solicitacaoVisita.getPessoaSolicitacao().getNome() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.SALVAR, this.solicitacaoVisita, e, log);
        }

        return retorno;
    }

    public void excluir(SolicitacaoVisitaDTO solicitacao) {
    	try {
    		service.excluir(solicitacao.getId());
            facesUtil.exibirMensagemInfo("SolicitacaoVisita " + solicitacao.getNomeSolicitante() + " excluído com sucesso!", false);
            LoggerUtil.logInfo(Acao.EXCLUIR, solicitacao, log);
    	} catch (ValidacaoException e) {
    		facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
    		LoggerUtil.logErro(Acao.EXCLUIR, solicitacao, e, log);
    	}
    }

    public void associarRecuperando(SelectEvent event) {
        RecuperandoView r = (RecuperandoView) event.getObject();
        if (r == null) return;

        try {
            Recuperando recuperando = this.recuperandoService.buscarRecuperandoSimplesPorId(r.getId());
            if (recuperando == null) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Recuperando não encontrado.");
                return;
            }
            recuperando.setLotacaoAtual(r.getNomeUnidade());

            this.solicitacaoVisita.setRecuperando(recuperando);
            
            Recuperando recaux = new Recuperando();
            List<Parente> parentes = recuperandoService.listarPorRecuperando(recuperando);
            recaux.setParentes(parentes);
            this.setRecuperandoSelecionado(recaux);
            RequestContext.getCurrentInstance().execute("PF('selecionarParenteSolicitacaoVisita').show()");
            this.solicitacaoVisita.setParente(null);
        } catch (Exception e) {
            this.solicitacaoVisita.setRecuperando(null);
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao associar recuperando ao pedido: " + e.getMessage());
            LoggerUtil.logErro(Acao.ASSOCIAR, r, e, log);
        }
    }

    public String getCpfPessoa(Pessoa pessoa) {
        StringBuilder cpf = new StringBuilder();
        try {
            if (pessoa == null || CollectionUtils.isEmpty(pessoa.getDocumentos())) {
                return cpf.toString();
            }

            pessoa.getDocumentos().stream().filter(
                    documento ->  documento.getTipoDocumento() != null
                            && (documento.getTipoDocumento().getDescricao().equalsIgnoreCase("cpf")
                            || documento.getDescricao().equalsIgnoreCase("cpf") )
            ).findFirst().ifPresent(documento -> cpf.append(documento.getNumero()));

            return cpf.toString();
        } catch (Exception e) {
            return cpf.toString();
        }
    }

    public void abrirDialogRecuperando(ActionEvent event) {
        this.permissaoUtil.filtrarUnidadesComBaseNaPermissaoListarTodasRecuperandos();
        facesUtil.exibirPaginaDialog("pretty:AssociarRecuperando");
    }

    public void pesquisar() {
    	this.getFiltro().setProjetarDados(true);
    	this.lazyDataModel = new LazyDataModel<SolicitacaoVisitaDTO>() {

			private static final long serialVersionUID = -4946935171862855618L;;

            @Override
            public List<SolicitacaoVisitaDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
            	getFiltro().setListarTodasUnidades(securityUtil.possuiPermissao("ListarSolicitacaoVisitaTodasUnidades"));
            	setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));
            	
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

                return service.buscarSolicitacoesPorFiltro(getFiltro(), false);
            }
    	};
    }

    public List<UnidadePrisional> getUnidades() {
        if (this.filtro.getUnidades() == null) {
            this.listarUnidades();
        }
        return this.filtro.getUnidades();
    }

    private void listarUnidades() {
        try {
        	this.filtro.setUnidades(this.unidadeService.listarUnidadesComParamVisitanteAtivo(false));
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar cidades: " + e.getMessage());
            LoggerUtil.logErro(e, log);
        }
        
    }
    
    public String atualizaStatus(SolicitacaoVisita solVisita) {
    	SolicitacaoVisita solicitacao = this.service.buscarPorId(solVisita.getId());
    	Optional<Parente> parenteOptional = solicitacao.getRecuperando().getParentes().stream().filter(p-> p.equals(solVisita.getParente())).findFirst();
    	if(parenteOptional.isPresent()) {
    		if(parenteOptional.get().getStatus().equals(SgpStatusAlternativo.I) || solicitacao.getRecuperando().getStatus().equals(StatusRecuperando.INATIVO)) {
    			solVisita.setStatus(SgpStatusAlternativo.I);
    			return solVisita.getStatus().getDescricao();
    		}
    	}
    	
    	return solVisita.getStatus().getDescricao();
    }
    
    public void imprimir() {
        try {
        	List<SolicitacaoVisitaDTO> lista = service.buscarSolicitacoesPorFiltro(getFiltro(), true);

            if (lista.size() == 0) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
                return;
            }

            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarSolicitacaoVisita));
            RequestContext.getCurrentInstance().execute("download()");
            LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Solicitação de Visita", log);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de solicitação de visita: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Solicitação de Visita", e, log);
        }
    }

    public FiltroSolicitacaoVisita getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroSolicitacaoVisita filtro) {
        this.filtro = filtro;
    }

    public SolicitacaoVisita getSolicitacaoVisita() {
        return solicitacaoVisita;
    }

    public void setSolicitacaoVisita(SolicitacaoVisita SolicitacaoVisita) {
        this.solicitacaoVisita = SolicitacaoVisita;
    }

    public LazyDataModel<SolicitacaoVisitaDTO> getLazyDataModel() {
        return lazyDataModel;
    }

    public void setLazyDataModel(LazyDataModel<SolicitacaoVisitaDTO> lazyDataModel) {
        this.lazyDataModel = lazyDataModel;
    }

    public AcaoDeTela getAcaoDeTela() {
        return super.getAcaoDeTela();
    }

	public Recuperando getRecuperandoSelecionado() {
		return recuperandoSelecionado;
	}

	public void setRecuperandoSelecionado(Recuperando recuperandoSelecionado) {
		this.recuperandoSelecionado = recuperandoSelecionado;
	}
    
    public Boolean desabilitaSelecao(Parente parente) {
        if (parente.getParentesco().getDescricao().equalsIgnoreCase(FILHOS)) {
            Calendar data = Calendar.getInstance();
            data.add(Calendar.YEAR, -18);
            if (parente.getDataNascimento() != null) {
                if (parente.getDataNascimento().after(data.getTime())) {
                    return false;
                }
            }
        }
        return parente.getStatus().equals(SgpStatusAlternativo.I) ||
                parente.getStatus().equals(SgpStatusAlternativo.P);
    }

    public void fecharRolParentes(ActionEvent event){
    	RequestContext.getCurrentInstance().execute("PF('selecionarParenteSolicitacaoVisita').hide()");
    }

}
