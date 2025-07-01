package br.gov.mt.sejudh.sigepen.bean;

import java.text.DateFormatSymbols;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPLantao;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRegistroPLantao;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoPlantao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPlantao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Atividade;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantao;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantaoServidor;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.RegistroPlantaoServidorDTO2;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.AtividadeService;
import br.gov.mt.sejudh.sigepen.service.PlantaoService;
import br.gov.mt.sejudh.sigepen.service.RegistroPlantaoService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;


@ManagedBean
@ViewScoped
@URLMappings(mappings ={
		@URLMapping(id = "ListarPlantao", pattern = "/plantao/listarPlantao", viewId = "/WEB-INF/pages/plantao/ListarPlantao.xhtml")
		,@URLMapping(id = "CadastrarRegistroPlantao",  pattern = "/plantao/cadastrar", viewId = "/WEB-INF/pages/plantao/CadastrarRegistroPlantao.xhtml")
		,@URLMapping(id = "VisualizarPlantao", pattern = "/plantao/visualizar/#{id}", viewId = "/WEB-INF/pages/plantao/CadastrarRegistroPlantao.xhtml")
})
public class RegistroPlantaoBean extends MetodosDePaginaBean {
	
	private FiltroRegistroPLantao filtro = new FiltroRegistroPLantao();

	
	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
    private String plantaoSelecionado;
    
	private List<Atividade> atividades;	
	private List<Plantao> plantoes;	
	private List<ServidorDTO> servidores;	
	private List<UnidadePrisional> unidadesPrisionais;
    
	private List<RegistroPlantao> plantoesEmAndamento;
	private RegistroPlantao registroPlantaoSelecionado;
	 	
	private UnidadePrisional unidadePrisional;
	private RegistroPlantaoServidor registroPlantaoServidor;
	private RegistroPlantao registroPlantao;		
	private SituacaoPlantao situacaoSelecionada;
	
	private Atividade atividade;
	private Atividade atividadeSelecionada;
		
	private RegistroPlantaoServidorDTO2 servidorSelecionado = new RegistroPlantaoServidorDTO2();

	private StatusPlantao statusPlantao;
	private TipoPlantao tipoPlantao;		
	
	private LazyDataModel<RegistroPlantao> registroPlantoes;
	
	//private LazyDataModel<RegistroPlantao> registroPlantoesss;
	//private List<RegistroPlantao> registroPlantoess;
	
	
	@Inject
	private RegistroPlantaoService registroPlantaoService;
	
	@Inject
	private AtividadeService atividadeService;
	
	@Inject
	private ServidorService servidorService;
	
	@Inject
	private UnidadePrisionalService unidadePrisionalService;
	
	
	
	private List<RegistroPlantaoServidorDTO2> listaGestao = new ArrayList<RegistroPlantaoServidorDTO2>();
	private List<RegistroPlantaoServidorDTO2> listaPlantao = new ArrayList<RegistroPlantaoServidorDTO2>();
	private List<RegistroPlantaoServidorDTO2> listaAdministrativa = new ArrayList<RegistroPlantaoServidorDTO2>();
	
	
	
	@PostConstruct
	public void inicializar() {		
		
		if (registroPlantao == null || registroPlantao.getId() == null) {
		    registroPlantao = new RegistroPlantao();		    
		}
			    
	    plantoes = registroPlantaoService.listarPlantaoPorStatus();
	    unidadesPrisionais = unidadePrisionalService.listarUnidadesNaoInativas();	         
        carregarPlantoesEmAndamento();
	}
	
	public void carregarPlantoesEmAndamento() {
		plantoesEmAndamento = registroPlantaoService.listarRegistroPlantaoPorStatus();
    }
	
	public void definirPlantaoAtual() {
	    if (registroPlantaoSelecionado != null) {
	        System.out.println("Selecionado: " + registroPlantaoSelecionado.getTipoPlantao().getDescricao());

	        registroPlantao.setPlantaoAnterior(
	            registroPlantaoSelecionado.getTipoPlantao().getDescricao()
	            + " (ID: " + registroPlantaoSelecionado.getId() + ")"
	        );
	    }
	}


	

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		super.carregarFiltroPaginacao(FiltroRegistroPLantao.class, this);
	}
	
	@URLAction(onPostback = false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.registroPlantao = new RegistroPlantao();
				break;

			case ALTERAR:
			case VISUALIZAR:								
				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
				
				if (!buscarPLantao(id)) {
					facesUtil.exibirMensagemErro("Plantão não encontrado.", true);
					return "pretty:ListarPlantao";
				}

				logDeAcesso(log, this.registroPlantao);
				break;

			case LISTAR:
				pesquisar();				
				logDeAcesso(log, this.registroPlantao);
				
				//pesquisar2();
				//logDeAcesso(log, this.registroPlantao);
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
	
	public void imprimirId(RegistroPlantao registroPlantao) {
		try {
			
			List<RegistroPlantao> lista = Arrays.asList(registroPlantao);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
						"Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarPlantoes));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Registro PLantões", log);
			
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro imprimir lista de plantoes: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de PLantões", e, log);
		}
	}
	
	public void imprimir() {
		try {
			
			List<RegistroPlantao> lista = registroPlantaoService.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
						"Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarPlantoes));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Registro PLantões", log);
			
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro imprimir lista de plantoes: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de PLantões", e, log);
		}
	}
	
	
	/* ------------------------- Métodos ------------------------- */
	
	public void atualizarPlantaoSelecionado() {
	    if (plantaoSelecionado != null && !plantaoSelecionado.isEmpty()) {
	        carregarDadosPorPlantao();
	    }
	}
	
	
	private void carregarDadosPorPlantao() {
	    listaPlantao.clear();
	    listaAdministrativa.clear();
	    listaGestao.clear();

	    if (plantaoSelecionado != null && !plantaoSelecionado.isEmpty()) {
	        List<String> tiposPlantao = Arrays.asList("A", "B", "C", "D");

	        // Sempre que for um plantão A, B, C ou D, carrega os três tipos de equipe
	        if (tiposPlantao.contains(plantaoSelecionado)) {
	            // Carregar equipe de plantão
	            Long tipoPlantao = 1L;
	            List<Atividade> atividadesPlantao = atividadeService.listarTodasAtividadesPorTipo(tipoPlantao);
	            List<ServidorDTO> servidoresPlantao = servidorService.listarServidoresPorTipoPlantao(plantaoSelecionado);

	            listaPlantao = servidoresPlantao.stream().map(s -> {
	                RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	                dto.setNomeServidor(s.getNome());
	                dto.setAtividades(new ArrayList<>(atividadesPlantao));
	                return dto;
	            }).collect(Collectors.toList());

	            // Carregar equipe de gestão
	            Long tipoGestao = 2L;
	            List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);
	            List<ServidorDTO> servidoresGestao = servidorService.listarServidoresPorTipoPlantao("GESTAO");

	            listaGestao = servidoresGestao.stream().map(s -> {
	                RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	                dto.setNomeServidor(s.getNome());
	                dto.setAtividades(new ArrayList<>(atividadesGestao));
	                return dto;
	            }).collect(Collectors.toList());

	            // Carregar equipe administrativa
	            Long tipoAdministrativo = 3L;
	            List<Atividade> atividadesAdministrativo = atividadeService.listarTodasAtividadesPorTipo(tipoAdministrativo);
	            List<ServidorDTO> servidoresAdmin = servidorService.listarServidoresPorTipoPlantao("ADMINISTRATIVO");

	            listaAdministrativa = servidoresAdmin.stream().map(s -> {
	                RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	                dto.setNomeServidor(s.getNome());
	                dto.setAtividades(new ArrayList<>(atividadesAdministrativo));
	                return dto;
	            }).collect(Collectors.toList());
	        }

	        // Se selecionar diretamente gestão ou administrativo, continua funcionando como antes
	        else if (plantaoSelecionado.equals("GESTAO")) {
	            Long tipoGestao = 2L;
	            List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);
	            List<ServidorDTO> servidoresGestao = servidorService.listarServidoresPorTipoPlantao("GESTAO");

	            listaGestao = servidoresGestao.stream().map(s -> {
	                RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	                dto.setNomeServidor(s.getNome());
	                dto.setAtividades(new ArrayList<>(atividadesGestao));
	                return dto;
	            }).collect(Collectors.toList());
	        } else if (plantaoSelecionado.equals("ADMINISTRATIVO")) {
	            Long tipoAdministrativo = 3L;
	            List<Atividade> atividadesAdministrativo = atividadeService.listarTodasAtividadesPorTipo(tipoAdministrativo);
	            List<ServidorDTO> servidoresAdmin = servidorService.listarServidoresPorTipoPlantao("ADMINISTRATIVO");

	            listaAdministrativa = servidoresAdmin.stream().map(s -> {
	                RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	                dto.setNomeServidor(s.getNome());
	                dto.setAtividades(new ArrayList<>(atividadesAdministrativo));
	                return dto;
	            }).collect(Collectors.toList());
	        }
	    }
	}
	
	private void carregarDadosPorPlantaoOriginal() {
	    listaPlantao.clear();
	    listaAdministrativa.clear();
	    listaGestao.clear();

	    if (plantaoSelecionado != null && !plantaoSelecionado.isEmpty()) {

	        
	        List<String> tiposPlantao = Arrays.asList("A", "B", "C", "D");
	        
	        if (tiposPlantao.contains(plantaoSelecionado)) {
	            Long tipoPlantao = 1L;
	            List<Atividade> atividadesPlantao = atividadeService.listarTodasAtividadesPorTipo(tipoPlantao);
	            List<ServidorDTO> servidoresPlantao = servidorService.listarServidoresPorTipoPlantao(plantaoSelecionado);

	            listaPlantao = servidoresPlantao.stream().map(s -> {
	                RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	                dto.setNomeServidor(s.getNome());
	                dto.setAtividades(new ArrayList<>(atividadesPlantao));
	                return dto;
	            }).collect(Collectors.toList());
	        }
	        
	        if (plantaoSelecionado.equals("GESTAO")) {
	            Long tipoGestao = 2L;
	            List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);
	            List<ServidorDTO> servidoresGestao = servidorService.listarServidoresPorTipoPlantao("GESTAO");

	            listaGestao = servidoresGestao.stream().map(s -> {
	                RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	                dto.setNomeServidor(s.getNome());
	                dto.setAtividades(new ArrayList<>(atividadesGestao));
	                return dto;
	            }).collect(Collectors.toList());
	        }
	        
	        if (plantaoSelecionado.equals("ADMINISTRATIVO")) {
	            Long tipoAdministrativo = 3L;
	            List<Atividade> atividadesAdministrativo = atividadeService.listarTodasAtividadesPorTipo(tipoAdministrativo);
	            List<ServidorDTO> servidoresAdmin = servidorService.listarServidoresPorTipoPlantao("ADMINISTRATIVO");

	            listaAdministrativa = servidoresAdmin.stream().map(s -> {
	                RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	                dto.setNomeServidor(s.getNome());
	                dto.setAtividades(new ArrayList<>(atividadesAdministrativo));
	                return dto;
	            }).collect(Collectors.toList());
	        }
	    }
	}

	
	public String salvarTodasEquipes() {
	    String retornoGestao = salvarEquipe(listaGestao);
	    String retornoPlantao = salvarEquipe(listaPlantao);
	    String retornoAdministrativa = salvarEquipe(listaAdministrativa);

	    boolean todasSalvas = 
	        retornoGestao != null && 
	        retornoPlantao != null && 
	        retornoAdministrativa != null;

	    return todasSalvas ? "pretty:ListarPlantao" : null;
	}



	
	public String salvarEquipe(List<RegistroPlantaoServidorDTO2> itemLista) {
	    String retorno = null;
	    boolean algumSalvo = false;

	    RegistroPlantao ultimoRegistro = registroPlantaoService.buscarUltimoRegistro();
	    if (ultimoRegistro == null) {
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar equipe", "Nenhum registro encontrado.");
	        return null;
	    }

	    Long registroPlantaoId = ultimoRegistro.getId();

	    for (RegistroPlantaoServidorDTO2 dto : itemLista) {
	        if (dto.getNomeServidor() != null) {

	            boolean registroExistente = registroPlantaoService.existeRegistro(dto.getNomeServidor(), dto.getDataRegistroPlantaoServidor());

	            if (!registroExistente) {
	                RegistroPlantaoServidor equipe = new RegistroPlantaoServidor();

	                equipe.setNomeServidor(dto.getNomeServidor());
	                equipe.setAtividadeSelecionada(dto.getAtividadeSelecionada());
	                equipe.setSituacaoSelecionada(dto.getSituacaoSelecionada());

	                equipe.setDataRegistroPlantaoServidor(
	                    Date.from(dto.getDataRegistroPlantaoServidor()
	                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));

	                equipe.setRegistroPlantaoId(registroPlantaoId);
	                equipe.setOcorrenciaSelecionada(dto.getOcorrenciaSelecionada() != null ? dto.getOcorrenciaSelecionada() : null);
	                equipe.setJustificativa(dto.getJustificativa() != null ? dto.getJustificativa() : null);

	                registroPlantaoService.salvarEquipePlantao(equipe);
	                algumSalvo = true;

	            } else {
	                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "O servidor " + dto.getNomeServidor() + " já tem um registro para esta data.");
	            }

	        } else {
	            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "Dados incompletos para alguns servidores.");
	        }
	    }

	    if (algumSalvo) {
	        facesUtil.exibirMensagemInfo("Registro Plantão " + ultimoRegistro.getId() + " salvo com sucesso!", true);
	        retorno = "pretty:ListarPlantao";
	    } else {
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Nenhum registro foi salvo. Verifique se os dados não são duplicados ou incompletos.");
	    }

	    return retorno;
	}

	
		
	public String salvar() {
	    String retorno = null;

	    try {
	    	 
	    	 RegistroPlantao ultimoRegistro = registroPlantaoService.buscarUltimoRegistroPlantao();

	    	 if (ultimoRegistro != null 
	    	         && ultimoRegistro.getTipoPlantao() != null 
	    	         && ultimoRegistro.getStatusPlantao() == StatusPlantao.INICIADO) {
	    	     
	    	     ultimoRegistro.setStatusPlantao(StatusPlantao.ANDAMENTO);
	    	     registroPlantaoService.salvar(ultimoRegistro);
	    	 }	    	 
	    	 	    		    	 
	    	if (this.plantaoSelecionado != null) {
	    		this.registroPlantao.setTipoPlantao(TipoPlantao.valueOf(this.plantaoSelecionado));
	    	}
	    	
	    	
	    	registroPlantao.setStatusPlantao(StatusPlantao.INICIADO);
	    	
	    	registroPlantao.setDataPlantao(new Date());
	    	

	        registroPlantaoService.salvar(this.registroPlantao);
	        facesUtil.exibirMensagemInfo("Registro Plantão " + this.registroPlantao.getId() + " salvo com sucesso!", true);
	        //retorno = "pretty:ListarRegistroPlantao";
	        retorno = null;
	        LoggerUtil.logInfo(Acao.SALVAR, this.registroPlantao, log);

	    } catch (ValidacaoException e) {	        
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());

	    } catch (Exception e) {	        
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
	                "Erro ao salvar plantao (" + this.registroPlantao.getId() + "): " + e.getMessage());	        
	        LoggerUtil.logErro(Acao.SALVAR, this.registroPlantao, e, log);
	    }

	    return retorno;
	}
	
	
	/*
	 * 
	 	<p:commandButton value="Limpar" icon="ui-icon-refresh"
    	action="#{registroPlantaoBean.limparFormulario}"
    	update="@form"
    	process="@this"
    	style="margin-left: 10px;" />
    	
    	
    	public void limparFormulario() {
		    this.plantaoSelecionado = null;
		    this.dataPlantao = null;
		    this.horaInicio = null;
		    this.unidadePrisional = null;
		    this.statusPlantao = null;
		    this.equipePlantao = new ArrayList<>(); // limpa tabela se quiser
		}
	 
	 * 
	 * */

	
	
	public void prepararEdicao(RegistroPlantaoServidorDTO2 servidor) {
	    this.servidorSelecionado = servidor;
	}
	
	
	public String atualizarRegistroPlantaoServidor() {
	    String retorno = null;

	    try {
	        if (servidorSelecionado.getNomeServidor() != null) {
	            RegistroPlantaoServidor registroPlantaoServidor = 
	                registroPlantaoService.buscarPorNome(servidorSelecionado.getNomeServidor());

	            if (registroPlantaoServidor != null) {                   
	                registroPlantaoServidor.setOcorrenciaSelecionada(servidorSelecionado.getOcorrenciaSelecionada());
	                registroPlantaoServidor.setJustificativa(servidorSelecionado.getJustificativa());   

	                registroPlantaoService.atualizar(registroPlantaoServidor);
	                
	                facesUtil.exibirMensagemInfo("Registro atualizado com sucesso!", true);
	                
	                retorno = "pretty:ListarRegistroPlantao";
	            } else {
	                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Aviso", 
	                    "Registro não encontrado para o servidor: " + servidorSelecionado.getNomeServidor());
	            }
	        }
	    } catch (Exception e) {
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Erro ao atualizar registro", e.getMessage());
	        LoggerUtil.logErro(e, log);
	    }

	    return retorno;
	}

	
	public void pesquisar() {
		this.registroPlantoes = new LazyDataModel<RegistroPlantao>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<RegistroPlantao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(registroPlantaoService.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return registroPlantaoService.buscaPorFiltro(getFiltro(), false);
			}
		};
	}
	
	
	private boolean buscarPLantao(long id) {
		if (id <= 0) {
			return false;
		}

		this.registroPlantao = registroPlantaoService.buscarPorId(id);

		if (this.registroPlantao == null) {
			return false;
		}

		return true;
	}
	
	/* ------------------------- Get & Setts ------------------------- */

		
	public List<SituacaoPlantao> getSituacoes() {
        return Arrays.asList(SituacaoPlantao.values());
    }

	public SituacaoPlantao getSituacaoSelecionada() {
        return situacaoSelecionada;
    }

    public void setSituacaoSelecionada(SituacaoPlantao situacaoSelecionada) {
        this.situacaoSelecionada = situacaoSelecionada;
    }
    
    public Atividade getAtividadeSelecionada() {
		return atividadeSelecionada;
	}

	public void setAtividadeSelecionada(Atividade atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}
    
	public RegistroPlantao getRegistroPlantao() {
		return registroPlantao;
	}

	public void setRegistroPlantao(RegistroPlantao registroPlantao) {
		this.registroPlantao = registroPlantao;
	}

	
	public List<Plantao> getPlantoes() {
        return plantoes;
    }

    public void setPlantoes(List<Plantao> plantoes) {
        this.plantoes = plantoes;
    }

	public List<ServidorDTO> getServidores() {
		return servidores;
	}

	public void setServidores(List<ServidorDTO> servidores) {
		this.servidores = servidores;
	}

	public List<Atividade> getAtividades() {
		return atividades;
	}

	public void setAtividades(List<Atividade> atividades) {
		this.atividades = atividades;
	}

	public Atividade getAtividade() {
		return atividade;
	}

	public void setAtividade(Atividade atividade) {
		this.atividade = atividade;
	}

	public List<RegistroPlantaoServidorDTO2> getListaGestao() {
		return listaGestao;
	}

	public void setListaGestao(List<RegistroPlantaoServidorDTO2> listaGestao) {
		this.listaGestao = listaGestao;
	}

	public List<RegistroPlantaoServidorDTO2> getListaPlantao() {
		return listaPlantao;
	}

	public void setListaPlantao(List<RegistroPlantaoServidorDTO2> listaPlantao) {
		this.listaPlantao = listaPlantao;
	}

	public List<RegistroPlantaoServidorDTO2> getListaAdministrativa() {
		return listaAdministrativa;
	}

	public void setListaAdministrativa(List<RegistroPlantaoServidorDTO2> listaAdministrativa) {
		this.listaAdministrativa = listaAdministrativa;
	}
		
	public RegistroPlantaoServidorDTO2 getServidorSelecionado() {
	    return servidorSelecionado;
	}

	public void setServidorSelecionado(RegistroPlantaoServidorDTO2 servidorSelecionado) {
	    this.servidorSelecionado = servidorSelecionado;
	}

	public RegistroPlantaoServidor getRegistroPlantaoServidor() {
		return registroPlantaoServidor;
	}

	public void setRegistroPlantaoServidor(RegistroPlantaoServidor registroPlantaoServidor) {
		this.registroPlantaoServidor = registroPlantaoServidor;
	}
	
	public void setPlantaoSelecionado(String plantaoSelecionado) {
	    this.plantaoSelecionado = plantaoSelecionado;	    
	}

	public String getPlantaoSelecionado() {
		return plantaoSelecionado;
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}

	public UnidadePrisional getUnidadePrisional() {
		return unidadePrisional;
	}

	public void setUnidadePrisional(UnidadePrisional unidadePrisional) {
		this.unidadePrisional = unidadePrisional;
	}

	
	
	public StatusPlantao getStatusPlantao() {
		return statusPlantao;
	}

	public void setStatusPlantao(StatusPlantao statusPlantao) {
		this.statusPlantao = statusPlantao;
	}
	
	public StatusPlantao[] getStatusPlantoes() {
        return StatusPlantao.values();
    }
	
	
	
	
	public TipoPlantao getTipoPlantao() {
		return tipoPlantao;
	}

	public void setTipoPlantao(TipoPlantao tipoPlantao) {
		this.tipoPlantao = tipoPlantao;
	}

	public TipoPlantao[] getTiposPlantao() {
	    return TipoPlantao.values();
	}


	public FiltroRegistroPLantao getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroRegistroPLantao filtro) {
		this.filtro = filtro;
	}


	public LazyDataModel<RegistroPlantao> getRegistroPlantoes() {
		return registroPlantoes;
	}


	public void setRegistroPlantoes(LazyDataModel<RegistroPlantao> registroPlantoes) {
		this.registroPlantoes = registroPlantoes;
	}

	
	public List<RegistroPlantao> getPlantoesEmAndamento() {
		return plantoesEmAndamento;
	}

	public void setPlantoesEmAndamento(List<RegistroPlantao> plantoesEmAndamento) {
		this.plantoesEmAndamento = plantoesEmAndamento;
	}

	
	public RegistroPlantao getRegistroPlantaoSelecionado() {
		return registroPlantaoSelecionado;
	}

	public void setRegistroPlantaoSelecionado(RegistroPlantao registroPlantaoSelecionado) {
		this.registroPlantaoSelecionado = registroPlantaoSelecionado;
	}

}
