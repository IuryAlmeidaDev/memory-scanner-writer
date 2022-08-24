package br.gov.mt.sejudh.sigepen.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.base.dataModel.AbstractDataModel;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoImpedimento;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.TipoImpedimento;
import br.gov.mt.sejudh.sigepen.service.TipoImpedimentoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "ListarTipoImpedimento", pattern = "/tipoImpedimento", viewId = "/WEB-INF/pages/tipoImpedimento/ListarTipoImpedimento.xhtml"),
        @URLMapping(id = "CadastrarTipoImpedimento", pattern = "/tipoImpedimento/cadastrar", viewId = "/WEB-INF/pages/tipoImpedimento/CadastrarTipoImpedimento.xhtml"),
        @URLMapping(id = "AlterarTipoImpedimento", pattern = "/tipoImpedimento/alterar/#{id}", viewId = "/WEB-INF/pages/tipoImpedimento/CadastrarTipoImpedimento.xhtml"),
        @URLMapping(id = "VisualizarTipoImpedimento", pattern = "/tipoImpedimento/visualizar/#{id}", viewId = "/WEB-INF/pages/tipoImpedimento/CadastrarTipoImpedimento.xhtml")
})
public class TipoImpedimentoBean extends MetodosDePaginaBean {

    private FiltroTipoImpedimento filtro;

    private TipoImpedimento tipoImpedimento;

    private LazyDataModel<TipoImpedimento> lazyDataModel;

    @Inject
    private TipoImpedimentoService service;

    @Inject
    private FacesUtil facesUtil;

    @Inject
    private Logger log;

    @Inject
    private FileDownloadHandlerBean fileDownload;

    @URLAction(onPostback = false)
    public String init() {
        try {
            super.carregarFiltroPaginacao(FiltroTipoImpedimento.class, this);
            switch (this.getAcaoDeTela()) {
                case CADASTRAR:
                    this.tipoImpedimento = new TipoImpedimento();
                    break;

                case ALTERAR:
                case VISUALIZAR:
                    long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

                    if (!buscarTipoImpedimento(id)) {
                        facesUtil.exibirMensagemErro("Tipo Impedimento não encontrado.", true);
                        return "pretty:ListarTipoImpedimento";
                    }

                    logDeAcesso(log, this.tipoImpedimento);
                    break;

                case LISTAR:
                    pesquisar();
                    logDeAcesso(log, "Impedimentos Visitante");
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

    private boolean buscarTipoImpedimento(long id) {
        if (id <= 0) {
            return false;
        }

        this.tipoImpedimento = service.buscarPorId(id, true);

        if (this.tipoImpedimento == null) {
            return false;
        }

        return true;
    }

    public String salvar() {
        String retorno = null;

        try {
            service.salvar(this.tipoImpedimento);
            facesUtil.exibirMensagemInfo("Tipo Impedimento " + this.tipoImpedimento.getDescricao() + " salvo com sucesso!", true);
            retorno = "pretty:ListarTipoImpedimento";
            LoggerUtil.logInfo(Acao.SALVAR, this.tipoImpedimento, log);
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar Tipo Impedimento (" + this.tipoImpedimento.getDescricao() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.SALVAR, this.tipoImpedimento, e, log);
        }

        return retorno;
    }

    public void excluir(TipoImpedimento tipoImpedimento) {
        try {
            service.excluir(tipoImpedimento);
            facesUtil.exibirMensagemInfo("Tipo Impedimento " + tipoImpedimento.getDescricao() + " excluído com sucesso!", false);
            LoggerUtil.logInfo(Acao.EXCLUIR, tipoImpedimento, log);
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getCause() instanceof ConstraintViolationException) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Operação não permitida! Tipo Impedimento associado a um Impedimento do Visitante.");
                return;
            }
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir tipoImpedimento (" + tipoImpedimento.getDescricao() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.EXCLUIR, tipoImpedimento, e, log);
        }
    }

    public void pesquisar() {
        this.lazyDataModel = new AbstractDataModel<>(this.service, this.getFiltro(), this);
    }

    public void imprimir() {
        try {
            List<TipoImpedimento> lista = service.buscaPorFiltro(filtro, true);

            if (lista.size() == 0) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
                return;
            }

            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarTipoImpedimento));
            RequestContext.getCurrentInstance().execute("download()");
            LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Tipo Impedimento", log);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de tipo impedimento: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Tipo Impedimento", e, log);
        }
    }

    public FiltroTipoImpedimento getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroTipoImpedimento filtro) {
        this.filtro = filtro;
    }

    public TipoImpedimento getTipoImpedimento() {
        return tipoImpedimento;
    }

    public void setTipoImpedimento(TipoImpedimento tipoImpedimento) {
        this.tipoImpedimento = tipoImpedimento;
    }

    public LazyDataModel<TipoImpedimento> getLazyDataModel() {
        return lazyDataModel;
    }

    public void setLazyDataModel(LazyDataModel<TipoImpedimento> lazyDataModel) {
        this.lazyDataModel = lazyDataModel;
    }
}
