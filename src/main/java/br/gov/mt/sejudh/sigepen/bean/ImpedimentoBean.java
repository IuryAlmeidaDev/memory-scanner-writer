package br.gov.mt.sejudh.sigepen.bean;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroImpedimento;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.TipoImpedimento;
import br.gov.mt.sejudh.sigepen.model.Visitante;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.service.ImpedimentoService;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "ListarImpedimento", pattern = "/impedimento", viewId = "/WEB-INF/pages/impedimento/ListarImpedimento.xhtml"),
        @URLMapping(id = "CadastrarImpedimento", pattern = "/impedimento/cadastrar", viewId = "/WEB-INF/pages/impedimento/CadastrarImpedimento.xhtml"),
        @URLMapping(id = "AlterarImpedimento", pattern = "/impedimento/alterar/#{id}", viewId = "/WEB-INF/pages/impedimento/CadastrarImpedimento.xhtml"),
        @URLMapping(id = "VisualizarImpedimento", pattern = "/impedimento/visualizar/#{id}", viewId = "/WEB-INF/pages/impedimento/CadastrarImpedimento.xhtml")
})
public class ImpedimentoBean extends MetodosDePaginaBean {

    private FiltroImpedimento filtro;

    private Impedimento impedimento;

    private LazyDataModel<Impedimento> lazyDataModel;

    @Inject
    private ImpedimentoService service;

    @Inject
    private VisitanteService visitanteService;

    @Inject
    private ComboService comboService;

    @Inject
    private FacesUtil facesUtil;

    @Inject
    private Logger log;

    @Inject
    private FileDownloadHandlerBean fileDownload;

    private List<TipoImpedimento> tipoImpedimentos;

    private Date dataAtual;

    @URLAction(onPostback = false)
    public String init() {
        try {
            super.carregarFiltroPaginacao(FiltroImpedimento.class, this);
            switch (this.getAcaoDeTela()) {
                case CADASTRAR:
                    this.iniciarDadosCadastro();
                    break;

                case ALTERAR:
                case VISUALIZAR:
                    long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

                    if (!buscarImpedimento(id)) {
                        facesUtil.exibirMensagemErro("Impedimento não encontrado.", true);
                        return "pretty:ListarImpedimento";
                    }

                    this.iniciarDadosAlteracao();
                    logDeAcesso(log, this.impedimento);
                    break;

                case LISTAR:
                    pesquisar();
                    logDeAcesso(log, "Impedimentos");
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
        this.impedimento = new Impedimento();
        this.impedimento.setStatus(SgpStatus.A);
        this.tipoImpedimentos = comboService.getTipoImpedimentos();
    }

    private void iniciarDadosAlteracao() {
        this.tipoImpedimentos = comboService.getTipoImpedimentos();
        Optional.ofNullable(this.impedimento.getVisitante()).ifPresent(
                visitante -> this.impedimento.setVisitante(this.visitanteService.buscarPorId(visitante.getId()))
        );
    }

    private boolean buscarImpedimento(long id) {
        if (id <= 0) {
            return false;
        }

        this.impedimento = service.buscarPorId(id, true);

        return this.impedimento != null;
    }

    public String salvar() {
        String retorno = null;

        try {
            if (this.impedimento.getVisitante() == null) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Selecione um Visitante.");
                return null;
            }

            this.service.salvar(this.impedimento);
            facesUtil.exibirMensagemInfo("Impedimento salvo com sucesso!", true);
            retorno = "pretty:ListarImpedimento";
            LoggerUtil.logInfo(Acao.SALVAR, this.impedimento, log);
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar Impedimento (" + this.impedimento.getVisitante().getPessoa().getNome() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.SALVAR, this.impedimento, e, log);
        }

        return retorno;
    }

    public void excluir(Impedimento impedimento) {
        try {
            service.excluir(impedimento);
            facesUtil.exibirMensagemInfo("Impedimento " + impedimento.getVisitante().getPessoa().getNome() + " excluído com sucesso!", false);
            LoggerUtil.logInfo(Acao.EXCLUIR, impedimento, log);
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof ConstraintViolationException) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Não é possível excluir! Impedimento do visitante está em período ativo!");
                return;
            }
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Impedimento (" + impedimento.getVisitante().getPessoa().getNome() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.EXCLUIR, impedimento, e, log);
        }
    }

    public void abrirDialogVisitante(ActionEvent event) {
        facesUtil.setObjetoSessao("matricula", null);
        facesUtil.exibirPaginaDialog("pretty:AssociarResponsavel", 400, 1000);
    }
    public void associarVisitante(SelectEvent event) {
        Visitante v = (Visitante) event.getObject();
        if (v == null) return;

        Visitante visitante = this.visitanteService.buscarPorId(v.getId());

        this.impedimento.setVisitante(visitante);
    }

    public void pesquisar() {
    	this.getFiltro().setProjetarDados(false);
    	this.lazyDataModel = new LazyDataModel<Impedimento>() {

			private static final long serialVersionUID = -4946935171862855618L;;

            @Override
            public List<Impedimento> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
            	setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));
            	
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}
            	
            	List<Impedimento> impedimentos = service.buscarImpedimentosPorFiltro(getFiltro(), false);
				return impedimentos;
            }
    	};
    }

    public void imprimir() {
        try {
            List<Impedimento> lista = service.buscaPorFiltro(filtro, true);

            if (lista.size() == 0) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
                return;
            }

            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarImpedimentoVisitante));
            RequestContext.getCurrentInstance().execute("download()");
            LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Impedimento Visitante", log);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de impedimento visitante: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Impedimento Visitante", e, log);
        }
    }

    public FiltroImpedimento getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroImpedimento filtro) {
        this.filtro = filtro;
    }

    public Impedimento getImpedimento() {
        return impedimento;
    }

    public void setImpedimento(Impedimento impedimento) {
        this.impedimento = impedimento;
    }

    public LazyDataModel<Impedimento> getLazyDataModel() {
        return lazyDataModel;
    }

    public void setLazyDataModel(LazyDataModel<Impedimento> lazyDataModel) {
        this.lazyDataModel = lazyDataModel;
    }

    public AcaoDeTela getAcao() {
        return super.getAcaoDeTela();
    }

    public List<TipoImpedimento> getTipoImpedimentos() {
        return tipoImpedimentos;
    }

    public void setDataAtual(Date dataAtual) {
        this.dataAtual = dataAtual;
    }

    public Date getDataAtual() {
        dataAtual = new Date();
        return dataAtual;
    }
}
