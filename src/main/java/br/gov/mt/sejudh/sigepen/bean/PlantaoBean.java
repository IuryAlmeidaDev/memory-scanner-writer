package br.gov.mt.sejudh.sigepen.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPlantao;
import br.gov.mt.sejudh.sigepen.enumerator.*;
import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.service.*;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;

import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.dto.RegistroPlantaoServidorDTO2;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;


@ManagedBean
@ViewScoped
@URLMappings(mappings ={
		@URLMapping(id = "ListarPlantao", pattern = "/plantao/listarPlantao", viewId = "/WEB-INF/pages/plantao/ListarPlantao.xhtml")
		,@URLMapping(id = "CadastrarRegistroPlantao",  pattern = "/plantao/cadastrar", viewId = "/WEB-INF/pages/plantao/CadastrarRegistroPlantao.xhtml")
		,@URLMapping(id = "VisualizarPlantao", pattern = "/plantao/visualizar/#{id}", viewId = "/WEB-INF/pages/plantao/CadastrarRegistroPlantao.xhtml")
		,@URLMapping(id = "AlterarPlantao", pattern = "/plantao/alterar/#{id}", viewId = "/WEB-INF/pages/plantao/CadastrarRegistroPlantao.xhtml")
})
public class PlantaoBean extends MetodosDePaginaBean {

	private FiltroPlantao filtro = new FiltroPlantao();

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;

    private String plantaoSelecionado;


    private List<TipoOcorrencia> tipoOcorrencias;
    private List<Ocorrencia> ocorrencias;
	private List<Ocorrencia> listaOcorrencias ;
    private List<Atividade> atividades;
	private List<ServidorDTO> servidores;
	private List<UnidadePrisional> unidadesPrisionais;

	private TipoOcorrencia tipoOcorrencia;
	private UnidadePrisional unidadePrisional;
	private RegistroPlantaoServidor registroPlantaoServidor;
	private Ocorrencia ocorrencia;
	private Plantao plantao = new Plantao();

	private List<PlantaoVisitaEstrutura> plantaoVisitaFamilia = new ArrayList<>();
	private List<PlantaoVisitaEstrutura> plantaoVisitaEntradaSaida = new ArrayList<>();
	private List<PlantaoVisitaEstrutura> plantaoVisitaAtendimento = new ArrayList<>();

	private SituacaoPlantao situacaoSelecionada;
	private Atividade atividade;
	private Atividade atividadeSelecionada;
		
	private RegistroPlantaoServidorDTO2 servidorSelecionado = new RegistroPlantaoServidorDTO2();

	private StatusPlantao statusPlantao;

	private TipoPlantao tipoPlantao;
	
	private StatusOcorrencia statusOcorrencia;

	private LazyDataModel<Plantao> plantoes;

	private boolean exibirDetalhamento = false;

	private boolean modoVisualizacao;
	private boolean modoEdicao;
	private boolean modoNovo;

	@Inject
	private TipoOcorrenciaService tipoOcorrenciaService;

	@Inject
	private PlantaoService plantaoService;
	
	@Inject
	private AtividadeService atividadeService;
	
	@Inject
	private ServidorService servidorService;

	@Inject
	private PlantaoServidorService plantaoServidorService;

	@Inject
	private OcorrenciaService ocorrenciaService;

	@Inject
	private PlantaoVisitaEstruturaService plantaoVisitaEstruturaService;

	@Inject
	private PlantaoVisitaService plantaoVisitaService;

	private List<RegistroPlantaoServidorDTO2> listaGestao = new ArrayList<RegistroPlantaoServidorDTO2>();
	private List<RegistroPlantaoServidorDTO2> listaPlantao = new ArrayList<RegistroPlantaoServidorDTO2>();
	private List<RegistroPlantaoServidorDTO2> listaAdministrativa = new ArrayList<RegistroPlantaoServidorDTO2>();

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		super.carregarFiltroPaginacao(FiltroPlantao.class, this);
	}


	@URLAction(onPostback = false)
	public String init() {

		inicializarListas();
		this.ocorrencia = new Ocorrencia();

		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				break;

			case ALTERAR:
			case VISUALIZAR:
				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

				if (!buscarPLantao(id)) {
					facesUtil.exibirMensagemErro("Plantão não encontrado.", true);
					return "pretty:ListarPlantao";
				}

				logDeAcesso(log, this.plantao);
				break;

			case LISTAR:
				pesquisar();
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




	/* ------------------------- Métodos ------------------------- */
	private void inicializarListas() {
		this.listaOcorrencias = ocorrenciaService.buscarOcorrenciasHoje();
		this.ocorrencias = ocorrenciaService.listarTodasOcorrencias();
		this.tipoOcorrencias = tipoOcorrenciaService.listar();
	}

	public void novaOcorrencia() {
	    this.ocorrencia = new Ocorrencia();
	    this.modoVisualizacao = false;
	    this.modoEdicao = true;
	    this.modoNovo = true;
	}

	public void buscarOcorrencia(Ocorrencia ocorrenciaSelecionada, boolean isVisualizar) {
	    this.ocorrencia = ocorrenciaService.buscarPorIdComTipo(ocorrenciaSelecionada.getId());
	    this.modoVisualizacao = isVisualizar;
	}

	public void selecionarOcorrencia(Ocorrencia ocorrenciaSelecionada) {
	    this.ocorrencia = ocorrenciaService.buscarPorIdComTipo(ocorrenciaSelecionada.getId());
	}

	public void mostrarDetalhamento() {
	    this.exibirDetalhamento = true;
	}

	public void atualizarPlantaoSelecionado() {
	    if (plantaoSelecionado != null && !plantaoSelecionado.isEmpty()) {
	        carregarDadosServidores();
	    }
	}

	private void listarPlantao(List<ServidorDTO> servidoresPlantao){
		listaPlantao.clear();

		Long tipoPlantao = 1L;
		List<Atividade> atividadesPlantao = atividadeService.listarTodasAtividadesPorTipo(tipoPlantao);

		listaPlantao = servidoresPlantao.stream().map(s -> {
			RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
			dto.setServidorDTO(s);
			dto.setAtividades(new ArrayList<>(atividadesPlantao));
			return dto;
		}).collect(Collectors.toList());

	}

	private void listarGestao(List<ServidorDTO> servidoresGestao){
		listaGestao.clear();
		// Carregar equipe de gestão
		Long tipoGestao = 2L;
		List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);

		listaGestao = servidoresGestao.stream().map(s -> {
			RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
			dto.setServidorDTO(s);
			dto.setAtividades(new ArrayList<>(atividadesGestao));
			return dto;
		}).collect(Collectors.toList());


	}

	private void listarAdministrativa(List<ServidorDTO> servidoresAdmin){
		listaAdministrativa.clear();

		// Carregar equipe administrativa
		Long tipoAdministrativo = 3L;
		List<Atividade> atividadesAdministrativo = atividadeService.listarTodasAtividadesPorTipo(tipoAdministrativo);

		listaAdministrativa = servidoresAdmin.stream().map(s -> {
			RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
			dto.setServidorDTO(s);
			dto.setAtividades(new ArrayList<>(atividadesAdministrativo));
			return dto;
		}).collect(Collectors.toList());

	}

	private void carregarDadosServidores(){

		UnidadePrisional unidade = this.plantao.getUnidadePrisional();

		List<ServidorDTO> servidoresPlantao = servidorService.listarServidoresPorTipoPlantao(plantaoSelecionado,unidade);
		listarPlantao(servidoresPlantao);
		List<ServidorDTO> servidoresGestao = servidorService.listarServidoresPorTipoPlantao("GESTAO",unidade);
		listarGestao(servidoresGestao);
		List<ServidorDTO> servidoresAdmin = servidorService.listarServidoresPorTipoPlantao("ADMINISTRATIVO",unidade);
		listarAdministrativa(servidoresAdmin);

	}

	public void salvarTodasEquipes() throws ValidacaoException {
		Plantao plantaoComEquipes = this.plantaoService.buscarPorId(this.plantao.getId());

		Equipe equipeGestao = montarEquipe(listaGestao, TipoEquipe.A);
		Equipe equipePlantao = montarEquipe(listaPlantao, TipoEquipe.B);
		Equipe equipeAdministrativa = montarEquipe(listaAdministrativa, TipoEquipe.C);

		plantaoComEquipes.getEquipes().addAll(Arrays.asList(equipeGestao, equipePlantao, equipeAdministrativa));

		this.plantaoService.salvar(plantaoComEquipes);

		// Redireciona após salvar
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		try {
			externalContext.redirect(externalContext.getRequestContextPath() + "/plantao/listarPlantao");
		} catch (IOException e) {
			throw new RuntimeException("Erro ao redirecionar para a listagem de plantões", e);
		}
	}

	public void salvarTodasEquipesAtual() throws ValidacaoException {

		Plantao plantaoComEquipes = this.plantaoService.buscarPorId(this.plantao.getId());
		Equipe equipeGestao = montarEquipe(listaGestao, TipoEquipe.A);
		Equipe equipePlantao = montarEquipe(listaPlantao, TipoEquipe.B);
		Equipe equipeAdministrativa = montarEquipe(listaAdministrativa, TipoEquipe.C);

		plantaoComEquipes.getEquipes().addAll(Arrays.asList(equipeGestao, equipePlantao, equipeAdministrativa));

		this.plantaoService.salvar(plantaoComEquipes);

	}

	public Equipe montarEquipe(List<RegistroPlantaoServidorDTO2> itemLista, TipoEquipe tipoEquipe) {
	    String retorno = null;

		List<PlantaoServidor> plantaoServidores = new ArrayList<>();

		Equipe equipe =  new Equipe();

	    for (RegistroPlantaoServidorDTO2 dto : itemLista) {

	        PlantaoServidor plantaoServidor = new PlantaoServidor();

			Servidor servidor = new Servidor();
			servidor.setId(dto.getServidorDTO().getId());

			plantaoServidor.setServidor(servidor);
			plantaoServidor.setAtividae(dto.getAtividadeSelecionada());
			plantaoServidor.setSituacao(dto.getSituacaoSelecionada());

			plantaoServidores.add(plantaoServidor);
			plantaoServidor.setEquipe(equipe);
	    }

		equipe.setTipoEquipe(tipoEquipe);
		equipe.setPlantaoServidor(plantaoServidores);
		equipe.setPlantao(this.plantao);

	    return equipe;
	}

	public void preencherDescricao() {
	    if (this.ocorrencia != null && this.ocorrencia.getTipoOcorrencia() != null) {
	        String texto = this.ocorrencia.getTipoOcorrencia().getDescricao();
	        this.ocorrencia.setDescricao(texto);
	    }
	}

	public void salvarOcorrencia() {

		try {

			if (ocorrencia.getVisitante() == null) {
	            Visitante visitantePadrao = new Visitante();
	            visitantePadrao.setId(1L);
	            ocorrencia.setVisitante(visitantePadrao);
	        }

			if (ocorrencia.getStatusOcorrencia() == null) {
				ocorrencia.setStatusOcorrencia(StatusOcorrencia.ANDAMENTO);
			}

			ocorrencia.setPlantao(this.plantao);
			this.listaOcorrencias.add(ocorrenciaService.salvar(ocorrencia));

			FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Ocorrência registrada com sucesso!"));

	        novaOcorrencia();

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao salvar ocorrência."));
			e.printStackTrace();
		}

	}

	public void salvarPlantaoVisita() throws ValidacaoException {
		if(!this.plantaoVisitaAtendimento.isEmpty()){
			this.plantaoVisitaService.salvar(new PlantaoVisita(TipoEntrada.ATENDIMENTO,this.plantao,this.plantaoVisitaAtendimento));
		}
		if(!this.plantaoVisitaFamilia.isEmpty()){
			this.plantaoVisitaService.salvar(new PlantaoVisita(TipoEntrada.FAMILIA,this.plantao,this.plantaoVisitaFamilia));
		}
		if(!this.plantaoVisitaEntradaSaida.isEmpty()){
			this.plantaoVisitaService.salvar(new PlantaoVisita(TipoEntrada.ENTRADA_SAIDA,this.plantao,this.plantaoVisitaEntradaSaida));
		}
	}

	public void encerrar() throws ValidacaoException {
		this.salvarPlantaoVisita();
	}

	public String registrar() {
		String retorno = null;
		try {
			Plantao plantaoAnterior = this.plantaoService.buscarUltimoPlantao();
			Plantao plantaoAtual = new Plantao();

			plantaoAtual.setTipoPlantao(TipoPlantao.valueOf(this.plantaoSelecionado));
			plantaoAtual.setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());
			plantaoAtual.setStatusPlantao(StatusPlantao.ANDAMENTO);
			plantaoAtual.setDataPlantao(new Date());

			if (plantaoAnterior != null) {
				plantaoAtual.setPlantaoAnterior(plantaoAnterior.getTipoPlantao().getDescricao());
			}

			this.plantao = this.plantaoService.salvar(plantaoAtual);

			retorno = "/sigepen/plantao/alterar/" + this.plantao.getId();
			FacesContext.getCurrentInstance().getExternalContext().redirect(retorno);
			facesUtil.exibirMensagemInfo("Registro Plantão " + this.plantao.getId() + " salvo com sucesso!", true);

			LoggerUtil.logInfo(Acao.SALVAR, this.plantao, log);

		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {

			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar plantao (" + (this.plantao != null ? this.plantao.getId() : "") + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.plantao, e, log);
		}

		return retorno;
	}

	public void prepararEdicao(RegistroPlantaoServidorDTO2 servidor) {
	    this.servidorSelecionado = servidor;
	}

	public String atualizarRegistroPlantaoServidor() {
	    String retorno = null;

	    try {

			PlantaoServidor plantaoServidor = this.servidorSelecionado.getPlantaoServidor();

			if(plantaoServidor != null){
				plantaoServidor.setOcorrencia(this.servidorSelecionado.getOcorrenciaSelecionada());
				plantaoServidor.setJustificativa(this.servidorSelecionado.getJustificativa());

				this.plantaoServidorService.salvar(plantaoServidor);

				facesUtil.exibirMensagemInfo("Registro atualizado com sucesso!", true);

			}

			retorno = "pretty:ListarRegistroPlantao";
	    } catch (Exception e) {
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Erro ao atualizar registro", e.getMessage());
	        LoggerUtil.logErro(e, log);
	    }

	    return retorno;
	}

	
	public void pesquisar() {
		this.plantoes = new LazyDataModel<Plantao>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<Plantao> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(plantaoService.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return plantaoService.buscaPorFiltro(getFiltro(), false);
			}
		};
	}
	

	private boolean buscarPLantao(long id) {
		if (id <= 0) {
			return false;
		}

		this.plantao = plantaoService.buscarPorId(id);
//		this.plantao.setPlantaoVisitas(buscarVisita());

		buscarVisita();

		if (this.plantao == null) {
			return false;
		}

		this.plantaoSelecionado = this.plantao.getTipoPlantao().getDescricao();

		if(!this.plantao.getEquipes().isEmpty()){
			this.plantao.getEquipes().forEach(e -> {
				buscarPlantao(e);
				buscarGetao(e);
				buscarAdministrativa(e);
			});
		}else {
			atualizarPlantaoSelecionado();
		}

		return true;
	}

	public void buscarVisita(){

		this.plantaoVisitaEntradaSaida = buscarPlantaoVisitaEstrutura(TipoEntrada.ENTRADA_SAIDA.getId());
		this.plantaoVisitaFamilia = buscarPlantaoVisitaEstrutura(TipoEntrada.FAMILIA.getId());
		this.plantaoVisitaAtendimento = buscarPlantaoVisitaEstrutura(TipoEntrada.ATENDIMENTO.getId());

	}

	public PlantaoVisita montarVisitaPorEntrada(Integer tipoEntrada){
		PlantaoVisita plantaoVisita = new PlantaoVisita();

		plantaoVisita.setTipoEntrada(TipoEntrada.getValue(tipoEntrada));
		plantaoVisita.setPlantaoVisitaEstruturas(buscarPlantaoVisitaEstrutura(tipoEntrada));

		return plantaoVisita;
	}

	public List<PlantaoVisitaEstrutura> buscarPlantaoVisitaEstrutura(Integer tipoEntrada){
		return this.plantaoVisitaEstruturaService.buscarPlantaoVisitaEstruturaPorUnidadeAndtipoEntrada(tipoEntrada, this.plantao);
	}


	private void buscarGetao(Equipe e){
		if(e.getTipoEquipe().equals(TipoEquipe.A)){

			// Carregar equipe de gestão
			Long tipoGestao = 2L;
			List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);

			listaGestao.clear();

			e.getPlantaoServidor().forEach(s -> {

				listaGestao.add(
						new RegistroPlantaoServidorDTO2(s,atividadesGestao)
				);

			});
		}
	}

	private void buscarAdministrativa(Equipe e){
		if(e.getTipoEquipe().equals(TipoEquipe.C)){

			// Carregar equipe de gestão
			Long tipoGestao = 3L;
			List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);

			listaAdministrativa.clear();

			e.getPlantaoServidor().forEach(s -> {

				listaAdministrativa.add(
						new RegistroPlantaoServidorDTO2(s,atividadesGestao)
				);

			});

		}
	}

	private void buscarPlantao(Equipe e){
		if(e.getTipoEquipe().equals(TipoEquipe.B)){
			// Carregar equipe de gestão
			Long tipoGestao = 1L;
			List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);

			listaPlantao.clear();

			e.getPlantaoServidor().forEach(s -> {

				listaPlantao.add(
						new RegistroPlantaoServidorDTO2(s,atividadesGestao)
				);

			});

		}
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


	public FiltroPlantao getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroPlantao filtro) {
		this.filtro = filtro;
	}

	public void setPlantoes(LazyDataModel<Plantao> plantoes) {
		this.plantoes = plantoes;
	}

    public Plantao getPlantao() {
        return plantao;
    }

    public void setPlantao(Plantao plantao) {
        this.plantao = plantao;
    }

	public LazyDataModel<Plantao> getPlantoes() {
		return plantoes;
	}

	public List<Ocorrencia> getOcorrencias() {
		return ocorrencias;
	}

	public void setOcorrencias(List<Ocorrencia> ocorrencias) {
		this.ocorrencias = ocorrencias;
	}


	public Ocorrencia getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(Ocorrencia ocorrencia) {
		this.ocorrencia = ocorrencia;
	}


	public List<TipoOcorrencia> getTipoOcorrencias() {
		return tipoOcorrencias;
	}


	public void setTipoOcorrencias(List<TipoOcorrencia> tipoOcorrencias) {
		this.tipoOcorrencias = tipoOcorrencias;
	}


	public TipoOcorrencia getTipoOcorrencia() {
		return tipoOcorrencia;
	}


	public void setTipoOcorrencia(TipoOcorrencia tipoOcorrencia) {
		this.tipoOcorrencia = tipoOcorrencia;
	}

	public boolean isExibirDetalhamento() {
	    return exibirDetalhamento;
	}


	public boolean isModoVisualizacao() {
		return modoVisualizacao;
	}


	public void setModoVisualizacao(boolean modoVisualizacao) {
		this.modoVisualizacao = modoVisualizacao;
	}


	public boolean isModoEdicao() {
		return modoEdicao;
	}


	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}


	public boolean isModoNovo() {
		return modoNovo;
	}


	public void setModoNovo(boolean modoNovo) {
		this.modoNovo = modoNovo;
	}


	public StatusOcorrencia getStatusOcorrencia() {
		return statusOcorrencia;
	}


	public void setStatusOcorrencia(StatusOcorrencia statusOcorrencia) {
		this.statusOcorrencia = statusOcorrencia;
	}

	public StatusOcorrencia[] getStatusOcorrencias() {
	    return StatusOcorrencia.values();
	}

	public List<PlantaoVisitaEstrutura> getPlantaoVisitaFamilia() {
		return plantaoVisitaFamilia;
	}

	public List<PlantaoVisitaEstrutura> getPlantaoVisitaEntradaSaida() {
		return plantaoVisitaEntradaSaida;
	}

	public List<PlantaoVisitaEstrutura> getPlantaoVisitaAtendimento() {
		return plantaoVisitaAtendimento;
	}

	public List<Ocorrencia> getListaOcorrencias() {
		return listaOcorrencias;
	}

	public void setListaOcorrencias(List<Ocorrencia> listaOcorrencias) {
		this.listaOcorrencias = listaOcorrencias;
	}

}
