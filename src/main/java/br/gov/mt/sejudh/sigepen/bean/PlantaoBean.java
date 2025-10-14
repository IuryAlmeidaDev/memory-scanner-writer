package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPlantao;
import br.gov.mt.sejudh.sigepen.enumerator.*;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.model.dto.RegistroPlantaoServidorDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.*;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "ListarPlantao", pattern = "/plantao/listarPlantao", viewId = "/WEB-INF/pages/plantao/ListarPlantao.xhtml")
        , @URLMapping(id = "CadastrarRegistroPlantao", pattern = "/plantao/cadastrar", viewId = "/WEB-INF/pages/plantao/CadastrarRegistroPlantao.xhtml")
        , @URLMapping(id = "VisualizarPlantao", pattern = "/plantao/visualizar/#{id}", viewId = "/WEB-INF/pages/plantao/CadastrarRegistroPlantao.xhtml")
        , @URLMapping(id = "AlterarPlantao", pattern = "/plantao/alterar/#{id}", viewId = "/WEB-INF/pages/plantao/CadastrarRegistroPlantao.xhtml")
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
    private List<Ocorrencia> listaOcorrencias;
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

    private RegistroPlantaoServidorDTO servidorSelecionado = new RegistroPlantaoServidorDTO();

    private StatusPlantao statusPlantao;

    private TipoPlantao tipoPlantao;

    private StatusOcorrencia statusOcorrencia;

    private LazyDataModel<Plantao> plantoes;

    private boolean exibirDetalhamento = false;

    private boolean modoVisualizacao;
    private boolean modoEdicao;
    private boolean modoNovo;

    private Plantao plantaoEmAberto;

    private PlantaoVisitaEstrutura plantaoVisitaEstruturaFamilia = new PlantaoVisitaEstrutura();
    private PlantaoVisitaEstrutura plantaoVisitaEstruturaAtendimento = new PlantaoVisitaEstrutura();
    private PlantaoVisitaEstrutura plantaoVisitaEstruturaEntradaSaida = new PlantaoVisitaEstrutura();

    private boolean camposDesabilitado = true;

    private PlantaoVisita plantaoVisita;

    private String classificacaoOcorrencia;

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

    private List<RegistroPlantaoServidorDTO> listaGestao = new ArrayList<>();
    private List<RegistroPlantaoServidorDTO> listaPlantao = new ArrayList<>();
    private List<RegistroPlantaoServidorDTO> listaAdministrativa = new ArrayList<>();

    @URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
    public void configurarFiltroPaginacao() throws Exception {
        super.carregarFiltroPaginacao(FiltroPlantao.class, this);
    }

    @URLAction(onPostback = false)
    public String init() {

        this.plantaoEmAberto = plantaoService.buscarUltimoPlantaoEmAberto();

        inicializarListas();
        this.ocorrencia = new Ocorrencia();

        try {
            switch (this.getAcaoDeTela()) {
                case ALTERAR:
                case VISUALIZAR:
                    long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

                    if (!buscarPlantao(id)) {
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

    private void inicializarListas() {
        this.tipoOcorrencias = tipoOcorrenciaService.listarAtivos();
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
        this.ocorrencia.setDetalhamento(null);
        this.exibirDetalhamento = !this.exibirDetalhamento;
    }

    public void atualizarPlantaoSelecionado() {
        if (plantaoSelecionado != null && !plantaoSelecionado.isEmpty()) {
            carregarDadosServidores();
        }
    }

    private void listarPlantao(List<ServidorDTO> servidoresPlantao) {
        listaPlantao.clear();
        Long tipoPlantao = 1L;
        List<Atividade> atividadesPlantao = atividadeService.listarTodasAtividadesPorTipo(tipoPlantao);

        listaPlantao = servidoresPlantao.stream().map(s -> {
            RegistroPlantaoServidorDTO dto = new RegistroPlantaoServidorDTO();
            dto.setServidorDTO(s);
            dto.setAtividades(new ArrayList<>(atividadesPlantao));
            return dto;
        }).collect(Collectors.toList());

    }

    private void listarGestao(List<ServidorDTO> servidoresGestao) {
        listaGestao.clear();
        Long tipoGestao = 2L;
        List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);

        listaGestao = servidoresGestao.stream().map(s -> {
            RegistroPlantaoServidorDTO dto = new RegistroPlantaoServidorDTO();
            dto.setServidorDTO(s);
            dto.setAtividades(new ArrayList<>(atividadesGestao));
            return dto;
        }).collect(Collectors.toList());
    }

    private void listarAdministrativa(List<ServidorDTO> servidoresAdmin) {
        listaAdministrativa.clear();
        Long tipoAdministrativo = 3L;
        List<Atividade> atividadesAdministrativo = atividadeService.listarTodasAtividadesPorTipo(tipoAdministrativo);

        listaAdministrativa = servidoresAdmin.stream().map(s -> {
            RegistroPlantaoServidorDTO dto = new RegistroPlantaoServidorDTO();
            dto.setServidorDTO(s);
            dto.setAtividades(new ArrayList<>(atividadesAdministrativo));
            return dto;
        }).collect(Collectors.toList());
    }

    private void carregarDadosServidores() {

        UnidadePrisional unidade = this.plantao.getUnidadePrisional();

        List<ServidorDTO> servidoresPlantao = servidorService.listarServidoresPorTipoPlantao(plantaoSelecionado, unidade);
        listarPlantao(servidoresPlantao);
        List<ServidorDTO> servidoresGestao = servidorService.listarServidoresPorTipoPlantao("GESTAO", unidade);
        listarGestao(servidoresGestao);
        List<ServidorDTO> servidoresAdmin = servidorService.listarServidoresPorTipoPlantao("ADMINISTRATIVO", unidade);
        listarAdministrativa(servidoresAdmin);
    }

    public void salvarTodasEquipes() throws ValidacaoException {

        if (listaGestao == null || listaGestao.isEmpty() ||
                listaPlantao == null || listaPlantao.isEmpty() ||
                listaAdministrativa == null || listaAdministrativa.isEmpty()) {

            facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Todas as equipes devem conter pelo menos um servidor.");
            return;
        }

        Plantao plantaoComEquipes = this.plantaoService.buscarPorId(this.plantao.getId());

        Equipe equipeGestao = montarEquipe(listaGestao, TipoEquipe.A);
        Equipe equipePlantao = montarEquipe(listaPlantao, TipoEquipe.B);
        Equipe equipeAdministrativa = montarEquipe(listaAdministrativa, TipoEquipe.C);

        equipeGestao.setPlantao(plantaoComEquipes);
        equipePlantao.setPlantao(plantaoComEquipes);
        equipeAdministrativa.setPlantao(plantaoComEquipes);

        if (plantaoComEquipes.getEquipes() != null) {
            plantaoComEquipes.getEquipes().clear();
        }
        plantaoComEquipes.getEquipes().addAll(Arrays.asList(equipeGestao, equipePlantao, equipeAdministrativa));

        this.plantaoService.salvar(plantaoComEquipes);

        String retorno = "/plantao/alterar/" + this.plantao.getId();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        facesUtil.exibirMensagemInfo("Registro Plantão " + this.plantao.getId() + " salvo com sucesso!", true);

        try {
            externalContext.redirect(externalContext.getRequestContextPath() + retorno);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao redirecionar para a listagem de plantões", e);
        }
    }

    public Equipe montarEquipe(List<RegistroPlantaoServidorDTO> itemLista, TipoEquipe tipoEquipe) {
        List<PlantaoServidor> plantaoServidores = new ArrayList<>();

        Equipe equipe = new Equipe();

        for (RegistroPlantaoServidorDTO dto : itemLista) {

            PlantaoServidor plantaoServidor = new PlantaoServidor();

            Servidor servidor = new Servidor();
            servidor.setId(dto.getServidorDTO().getId());

            plantaoServidor.setServidor(servidor);
            plantaoServidor.setAtividade(dto.getAtividadeSelecionada());
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

    public void changeClassificacaoOcorrencia() {
        this.tipoOcorrencias = this.tipoOcorrenciaService.listarAtivosPorClassificacao(this.classificacaoOcorrencia);
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
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Ocorrência registrada com sucesso!", "Ocorrência registrada com sucesso!"));

            novaOcorrencia();

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(
                    null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao salvar ocorrência."));
            e.printStackTrace();
        }

    }

    public void salvarPlantaoVisita() throws ValidacaoException {
        if (!this.plantaoVisitaAtendimento.isEmpty()) {
            this.plantaoVisitaService.salvar(new PlantaoVisita(TipoEntrada.ATENDIMENTO, this.plantao, this.plantaoVisitaAtendimento));
        }
        if (!this.plantaoVisitaFamilia.isEmpty()) {
            this.plantaoVisitaService.salvar(new PlantaoVisita(TipoEntrada.FAMILIA, this.plantao, this.plantaoVisitaFamilia));
        }
        if (!this.plantaoVisitaEntradaSaida.isEmpty()) {
            this.plantaoVisitaService.salvar(new PlantaoVisita(TipoEntrada.ENTRADA_SAIDA, this.plantao, this.plantaoVisitaEntradaSaida));
        }
    }

    public void encerrar() throws ValidacaoException {

        if (!plantaoService.isPlantaoDaUnidadeLogada(plantao)) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Permissão negada", "Você não pode encerrar plantões de outra unidade.");
            return;
        }

        if (plantaoService.isDataEncerramentoIgualDataAbertura(plantao)) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "O plantão não pode ser encerrado no mesmo dia em que foi aberto.");
            return;
        }

        if (plantaoService.isExigeJustificativaParaEncerramento(plantao)) {
            if (plantao.getJustificativaEncerramentoPlantao() == null || plantao.getJustificativaEncerramentoPlantao().trim().isEmpty()) {
                RequestContext.getCurrentInstance().execute("PF('modalJustificativa').show()");
                return;
            }
        }

        if (plantao.getEquipes() == null || plantao.getEquipes().isEmpty() ||
                plantao.getEquipes().stream().allMatch(eq -> eq.getPlantaoServidor() == null || eq.getPlantaoServidor().isEmpty())) {

            facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não é possível encerrar o plantão sem servidores registrados nas equipes.");
            return;
        }

        finalizarEncerramento();
    }

    public void confirmarEncerramento() throws ValidacaoException {
        if (plantao.getJustificativaEncerramentoPlantao() == null || plantao.getJustificativaEncerramentoPlantao().trim().isEmpty()) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "A justificativa é obrigatória.");
            return;
        }
        finalizarEncerramento();
    }

    private void finalizarEncerramento() throws ValidacaoException {
        UnidadePrisional unidade = SecurityUtil.getUnidadeSelecionada();
        Plantao plantaoMaisAntigoAberto = plantaoService.buscarPlantaoMaisAntigoEmAbertoPorUnidade(unidade);

        if (plantaoMaisAntigoAberto != null && !plantaoMaisAntigoAberto.getId().equals(this.plantao.getId())) {
            facesUtil.exibirMensagem(
                    FacesMessage.SEVERITY_WARN,
                    "Atenção",
                    "Feche o plantão anterior antes de encerrar este."
            );
            return;
        }

        if (unidade.getAtivaModuloVisitante()) {
            this.salvarPlantaoVisita();
        }

        if (this.plantao != null && this.plantao.getId() != null) {
            this.plantao.setDataPlantaoEncerramento(new Date());
            this.plantao.setStatusPlantao(StatusPlantao.CONCLUIDO);
            this.plantao.setUsuarioFimPlantao(SecurityUtil.getUsuario());
            this.plantao.setJustificativaEncerramentoPlantao(plantao.getJustificativaEncerramentoPlantao());
            this.plantaoService.salvar(this.plantao);

            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            try {
                externalContext.redirect(externalContext.getRequestContextPath() + "/plantao/listarPlantao");
            } catch (IOException e) {
                throw new RuntimeException("Erro ao redirecionar para a listagem de plantões", e);
            }
        } else {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Nenhum plantão em andamento para encerrar.");
        }
    }


    public String registrar() {
        String retorno = null;
        try {
            UnidadePrisional unidadeLogada = SecurityUtil.getUnidadeSelecionada();

            boolean existePlantao = plantaoService.existePlantaoNaDataPorUnidade(unidadeLogada);
            if (existePlantao) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "Já existe um plantão registrado para hoje nesta unidade.");
                return null;
            }

            Plantao plantaoAnterior = this.plantaoService.buscarUltimoPlantao();
            Plantao plantaoAtual = new Plantao();

            plantaoAtual.setTipoPlantao(TipoPlantao.valueOf(this.plantaoSelecionado));
            plantaoAtual.setUnidadePrisional(unidadeLogada);
            plantaoAtual.setStatusPlantao(StatusPlantao.ANDAMENTO);
            plantaoAtual.setDataPlantao(new Date());
            plantaoAtual.setUsuarioInicioPlantao(SecurityUtil.getUsuario());

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
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar plantão (" + (this.plantao != null ? this.plantao.getId() : "") + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.SALVAR, this.plantao, e, log);
        }

        return retorno;
    }

    public void imprimir() {
        try {
            List<Plantao> lista = plantaoService.buscaPorFiltro(filtro, true);

            if (lista.isEmpty()) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
                return;
            }

            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarPlantoes));
            RequestContext.getCurrentInstance().execute("download()");
            LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de PLantões", log);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de plantões: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Cargos", e, log);
        }
    }

    public void prepararEdicao(RegistroPlantaoServidorDTO servidor) {
        this.servidorSelecionado = servidor;
    }

    public String atualizarRegistroPlantaoServidor() {
        String retorno = null;

        try {

            PlantaoServidor plantaoServidor = this.servidorSelecionado.getPlantaoServidor();
            plantaoServidor.setOcorrencia(this.servidorSelecionado.getOcorrenciaSelecionada());
            plantaoServidor.setJustificativa(this.servidorSelecionado.getJustificativa());

            PlantaoServidor plantaoServidorSalvo = this.plantaoServidorService.salvar(plantaoServidor);

            this.servidorSelecionado.setOcorrenciaSelecionada(plantaoServidorSalvo.getOcorrencia());
            this.servidorSelecionado.setJustificativa(plantaoServidorSalvo.getJustificativa());

            facesUtil.exibirMensagemInfo("Registro atualizado com sucesso!", true);
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

                UnidadePrisional unidadeLogada = SecurityUtil.getUnidadeSelecionada();

                if (!SecurityUtil.possuiPermissaoNaUnidadeLogada("ListarTodosPlantoes")) {
                    getFiltro().setUnidadePrisional(unidadeLogada);
                }
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

    private boolean buscarPlantao(long id) {
        if (id <= 0) {
            return false;
        }

        this.plantao = plantaoService.buscarPorId(id);

        this.listaOcorrencias = this.plantao.getOcorrencias()
                .stream()
                .filter(Ocorrencia::isAtivo)
                .collect(Collectors.toList());

        buscarVisita();

        if (this.plantao == null) {
            return false;
        }

        this.plantaoSelecionado = this.plantao.getTipoPlantao().getDescricao();

        if (!this.plantao.getEquipes().isEmpty()) {
            this.plantao.getEquipes().forEach(e -> {
                buscarPlantao(e);
                buscarGestao(e);
                buscarAdministrativa(e);
            });
        } else {
            atualizarPlantaoSelecionado();
        }

        return true;
    }

    public void habilitarVisitaFamilia() {
        this.plantaoVisitaEstruturaFamilia = new PlantaoVisitaEstrutura();
        this.camposDesabilitado = false;
    }

    public void habilitarAtendimento() {
        this.plantaoVisitaEstruturaAtendimento = new PlantaoVisitaEstrutura();
        this.camposDesabilitado = false;
    }

    public void habilitarEntradaSaida() {
        this.plantaoVisitaEstruturaEntradaSaida = new PlantaoVisitaEstrutura();
        this.camposDesabilitado = false;
    }

    public void salvarVisitaFamilia() throws ValidacaoException {

        PlantaoVisita plantaoVisitaExistente = this.plantaoVisitaService.buscarPlantaoVisita(this.plantao.getId(), TipoEntrada.FAMILIA);

        PlantaoVisita plantaoVisita = new PlantaoVisita();

        if (plantaoVisitaExistente != null) {
            plantaoVisita = plantaoVisitaExistente;
        }

        plantaoVisita.setPlantao(this.plantao);
        plantaoVisita.setTipoEntrada(TipoEntrada.FAMILIA);
        plantaoVisita.getPlantaoVisitaEstruturas().add(this.plantaoVisitaEstruturaFamilia);
        this.plantaoVisitaEstruturaFamilia.setPlantaoVisita(plantaoVisita);

        this.plantaoVisitaEstruturaFamilia.validarCamposObrigatorios();
        this.plantaoVisitaService.salvar(plantaoVisita);

        this.plantaoVisitaFamilia.add(this.plantaoVisitaEstruturaFamilia);

        this.plantaoVisitaEstruturaFamilia = new PlantaoVisitaEstrutura();
        this.camposDesabilitado = true;


    }

    public void salvarVisitaAtendimento() throws ValidacaoException {

        PlantaoVisita plantaoVisitaExistente = this.plantaoVisitaService.buscarPlantaoVisita(this.plantao.getId(), TipoEntrada.ATENDIMENTO);

        PlantaoVisita plantaoVisita = new PlantaoVisita();

        if (plantaoVisitaExistente != null) {
            plantaoVisita = plantaoVisitaExistente;
        }

        plantaoVisita.setPlantao(this.plantao);
        plantaoVisita.setTipoEntrada(TipoEntrada.ATENDIMENTO);
        plantaoVisita.getPlantaoVisitaEstruturas().add(this.plantaoVisitaEstruturaAtendimento);
        this.plantaoVisitaEstruturaAtendimento.setPlantaoVisita(plantaoVisita);

        this.plantaoVisitaService.salvar(plantaoVisita);

        this.plantaoVisitaAtendimento.add(this.plantaoVisitaEstruturaAtendimento);

        this.plantaoVisitaEstruturaAtendimento = new PlantaoVisitaEstrutura();
        this.camposDesabilitado = true;

    }

    public void salvarVisitaEntradaSaida() throws ValidacaoException {

        PlantaoVisita plantaoVisitaExistente = this.plantaoVisitaService.buscarPlantaoVisita(this.plantao.getId(), TipoEntrada.ENTRADA_SAIDA);

        PlantaoVisita plantaoVisita = new PlantaoVisita();

        if (plantaoVisitaExistente != null) {
            plantaoVisita = plantaoVisitaExistente;
        }

        plantaoVisita.setPlantao(this.plantao);
        plantaoVisita.setTipoEntrada(TipoEntrada.ENTRADA_SAIDA);
        plantaoVisita.getPlantaoVisitaEstruturas().add(this.plantaoVisitaEstruturaEntradaSaida);
        this.plantaoVisitaEstruturaEntradaSaida.setPlantaoVisita(plantaoVisita);

        this.plantaoVisitaService.salvar(plantaoVisita);

        this.plantaoVisitaEntradaSaida.add(this.plantaoVisitaEstruturaEntradaSaida);

        this.plantaoVisitaEstruturaEntradaSaida = new PlantaoVisitaEstrutura();
        this.camposDesabilitado = true;

    }

    public void buscarVisita() {

        this.plantaoVisitaEntradaSaida = buscarPlantaoVisitaEstrutura(TipoEntrada.ENTRADA_SAIDA.getId());
        this.plantaoVisitaFamilia = buscarPlantaoVisitaEstrutura(TipoEntrada.FAMILIA.getId());
        this.plantaoVisitaAtendimento = buscarPlantaoVisitaEstrutura(TipoEntrada.ATENDIMENTO.getId());

    }

    public PlantaoVisita montarVisitaPorEntrada(Integer tipoEntrada) {
        PlantaoVisita plantaoVisita = new PlantaoVisita();

        plantaoVisita.setTipoEntrada(TipoEntrada.getValue(tipoEntrada));
        plantaoVisita.setPlantaoVisitaEstruturas(buscarPlantaoVisitaEstrutura(tipoEntrada));

        return plantaoVisita;
    }

    public List<PlantaoVisitaEstrutura> buscarPlantaoVisitaEstrutura(Integer tipoEntrada) {

        List<PlantaoVisitaEstrutura> plantaoVisitaEstruturas = new ArrayList<>();

        if (!this.plantao.getPlantaoVisitas().isEmpty()) {
            for (PlantaoVisita plantaoVisita : this.plantao.getPlantaoVisitas()) {
                if (plantaoVisita.getTipoEntrada().getId().equals(tipoEntrada) && !plantaoVisita.getPlantaoVisitaEstruturas().isEmpty()) {
                    plantaoVisitaEstruturas = plantaoVisita.getPlantaoVisitaEstruturas();
                } else if (plantaoVisita.getTipoEntrada().getId().equals(tipoEntrada) && plantaoVisita.getPlantaoVisitaEstruturas().isEmpty()) {
                    plantaoVisitaEstruturas = this.plantaoVisitaEstruturaService.buscarPlantaoVisitaEstruturaPorUnidadeAndtipoEntrada(tipoEntrada, this.plantao);
                }
            }
        } else {
            plantaoVisitaEstruturas = this.plantaoVisitaEstruturaService.buscarPlantaoVisitaEstruturaPorUnidadeAndtipoEntrada(tipoEntrada, this.plantao);
        }


        return plantaoVisitaEstruturas;
    }

    private void buscarGestao(Equipe e) {
        if (e.getTipoEquipe().equals(TipoEquipe.A)) {

            Long tipoGestao = 2L;
            List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);

            listaGestao.clear();

            e.getPlantaoServidor().forEach(s -> listaGestao.add(
                    new RegistroPlantaoServidorDTO(s, atividadesGestao)
            ));
        }
    }

    private void buscarAdministrativa(Equipe e) {
        if (e.getTipoEquipe().equals(TipoEquipe.C)) {

            Long tipoGestao = 3L;
            List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);

            listaAdministrativa.clear();

            e.getPlantaoServidor().forEach(s -> listaAdministrativa.add(
                    new RegistroPlantaoServidorDTO(s, atividadesGestao)
            ));

        }
    }

    private void buscarPlantao(Equipe e) {
        if (e.getTipoEquipe().equals(TipoEquipe.B)) {

            Long tipoGestao = 1L;
            List<Atividade> atividadesGestao = atividadeService.listarTodasAtividadesPorTipo(tipoGestao);

            listaPlantao.clear();

            e.getPlantaoServidor().forEach(s -> listaPlantao.add(new RegistroPlantaoServidorDTO(s, atividadesGestao)));
        }
    }

    public void imprimirId(Plantao plantaoParaImprimir) {
        try {
            if (plantaoParaImprimir == null || plantaoParaImprimir.getId() == null) {
                facesUtil.exibirMensagemErro("Plantão inválido para gerar o relatório.", false);
                return;
            }
            Arquivo arquivo = plantaoService.gerarRelatorioRegistroPlantao(plantaoParaImprimir.getId());
            fileDownload.prepararArquivo(arquivo);
            RequestContext.getCurrentInstance().execute("download()");
        } catch (IllegalArgumentException | IllegalStateException e) {
            facesUtil.exibirMensagemErro(e.getMessage(), false);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Não foi possível gerar o relatório: " + e.getMessage());
            LoggerUtil.logErro(e, log);
        }
    }

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

    public List<RegistroPlantaoServidorDTO> getListaGestao() {
        return listaGestao;
    }

    public void setListaGestao(List<RegistroPlantaoServidorDTO> listaGestao) {
        this.listaGestao = listaGestao;
    }

    public List<RegistroPlantaoServidorDTO> getListaPlantao() {
        return listaPlantao;
    }

    public void setListaPlantao(List<RegistroPlantaoServidorDTO> listaPlantao) {
        this.listaPlantao = listaPlantao;
    }

    public List<RegistroPlantaoServidorDTO> getListaAdministrativa() {
        return listaAdministrativa;
    }

    public void setListaAdministrativa(List<RegistroPlantaoServidorDTO> listaAdministrativa) {
        this.listaAdministrativa = listaAdministrativa;
    }

    public RegistroPlantaoServidorDTO getServidorSelecionado() {
        return servidorSelecionado;
    }

    public void setServidorSelecionado(RegistroPlantaoServidorDTO servidorSelecionado) {
        this.servidorSelecionado = servidorSelecionado;
    }

    public RegistroPlantaoServidor getRegistroPlantaoServidor() {
        return registroPlantaoServidor;
    }

    public void setRegistroPlantaoServidor(RegistroPlantaoServidor registroPlantaoServidor) {
        this.registroPlantaoServidor = registroPlantaoServidor;
    }

    public String getPlantaoSelecionado() {
        return plantaoSelecionado;
    }

    public void setPlantaoSelecionado(String plantaoSelecionado) {
        this.plantaoSelecionado = plantaoSelecionado;
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

    public Plantao getPlantao() {
        return plantao;
    }

    public void setPlantao(Plantao plantao) {
        this.plantao = plantao;
    }

    public LazyDataModel<Plantao> getPlantoes() {
        return plantoes;
    }

    public void setPlantoes(LazyDataModel<Plantao> plantoes) {
        this.plantoes = plantoes;
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

    public PlantaoVisitaEstrutura getPlantaoVisitaEstruturaFamilia() {
        return plantaoVisitaEstruturaFamilia;
    }

    public void setPlantaoVisitaEstruturaFamilia(PlantaoVisitaEstrutura plantaoVisitaEstruturaFamilia) {
        this.plantaoVisitaEstruturaFamilia = plantaoVisitaEstruturaFamilia;
    }

    public PlantaoVisitaEstrutura getPlantaoVisitaEstruturaAtendimento() {
        return plantaoVisitaEstruturaAtendimento;
    }

    public void setPlantaoVisitaEstruturaAtendimento(PlantaoVisitaEstrutura plantaoVisitaEstruturaAtendimento) {
        this.plantaoVisitaEstruturaAtendimento = plantaoVisitaEstruturaAtendimento;
    }

    public PlantaoVisitaEstrutura getPlantaoVisitaEstruturaEntradaSaida() {
        return plantaoVisitaEstruturaEntradaSaida;
    }

    public void setPlantaoVisitaEstruturaEntradaSaida(PlantaoVisitaEstrutura plantaoVisitaEstruturaEntradaSaida) {
        this.plantaoVisitaEstruturaEntradaSaida = plantaoVisitaEstruturaEntradaSaida;
    }

    public PlantaoVisita getPlantaoVisita() {
        return plantaoVisita;
    }

    public void setPlantaoVisita(PlantaoVisita plantaoVisita) {
        this.plantaoVisita = plantaoVisita;
    }

    public boolean getCamposDesabilitado() {
        return camposDesabilitado;
    }

    public void setCamposDesabilitado(boolean camposDesabilitado) {
        this.camposDesabilitado = camposDesabilitado;
    }

    public Plantao getPlantaoEmAberto() {
        return plantaoEmAberto;
    }

    public void setPlantaoEmAberto(Plantao plantaoEmAberto) {
        this.plantaoEmAberto = plantaoEmAberto;
    }

    public String getClassificacaoOcorrencia() {
        return classificacaoOcorrencia;
    }

    public void setClassificacaoOcorrencia(String classificacaoOcorrencia) {
        this.classificacaoOcorrencia = classificacaoOcorrencia;
    }

}

