package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

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
import br.gov.mt.sejudh.sigepen.model.Certidao;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.CertidaoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "ListarCertidao", pattern = "/certidao", viewId = "/WEB-INF/pages/certidao/ListarCertidao.xhtml"),
        @URLMapping(id = "CadastrarCertidao", pattern = "/certidao/cadastrar", viewId = "/WEB-INF/pages/certidao/CadastrarCertidao.xhtml"),
        @URLMapping(id = "AlterarCertidao", pattern = "/certidao/alterar/#{id}", viewId = "/WEB-INF/pages/certidao/CadastrarCertidao.xhtml"),
        @URLMapping(id = "VisualizarCertidao", pattern = "/certidao/visualizar/#{id}", viewId = "/WEB-INF/pages/certidao/CadastrarCertidao.xhtml")
})
public class CertidaoBean extends MetodosDePaginaBean {

    private FiltroTipoImpedimento filtro;

    private Certidao certidao;

    private LazyDataModel<Certidao> lazyDataModel;

    @Inject
    private CertidaoService service;

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
                    this.certidao = new Certidao();
                    break;

                case ALTERAR:
                case VISUALIZAR:
                    long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

                    if (!buscarCertidao(id)) {
                        facesUtil.exibirMensagemErro("Certidao não encontrado.", true);
                        return "pretty:ListarCertidao";
                    }

                    logDeAcesso(log, this.certidao);
                    break;

                case LISTAR:
                    pesquisar();
                    logDeAcesso(log, "Certidaos");
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

    private boolean buscarCertidao(long id) {
        if (id <= 0) {
            return false;
        }

        this.certidao = service.buscarPorId(id, true);

        if (this.certidao == null) {
            return false;
        }

        return true;
    }

    public String salvar() {
        String retorno = null;

        try {
            service.salvar(this.certidao);
            facesUtil.exibirMensagemInfo("Certidão " + this.certidao.getDescricao() + " salvo com sucesso!", true);
            retorno = "pretty:ListarCertidao";
            LoggerUtil.logInfo(Acao.SALVAR, this.certidao, log);
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar Certidao (" + this.certidao.getDescricao() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.SALVAR, this.certidao, e, log);
        }

        return retorno;
    }

    public void excluir(Certidao certidao) {
        try {
            service.excluir(certidao);
            facesUtil.exibirMensagemInfo("Certidao " + certidao.getDescricao() + " excluído com sucesso!", false);
            LoggerUtil.logInfo(Acao.EXCLUIR, certidao, log);
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause().getCause() instanceof ConstraintViolationException) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Certidão esta vinculada a um tipo de visita ativo!");
                return;
            }
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Certidao (" + certidao.getDescricao() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.EXCLUIR, certidao, e, log);
        }
    }

    public void pesquisar() {
        this.lazyDataModel = new AbstractDataModel<>(this.service, this.getFiltro(), this);
    }

    public void imprimir() {
        try {
            List<Certidao> lista = service.buscaPorFiltro(filtro, true);

            if (lista.size() == 0) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
                return;
            }

            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarCertidao));
            RequestContext.getCurrentInstance().execute("download()");
            LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Certidão", log);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de certidão: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Certidão", e, log);
        }
    }

    public FiltroTipoImpedimento getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroTipoImpedimento filtro) {
        this.filtro = filtro;
    }

    public Certidao getCertidao() {
        return certidao;
    }

    public void setCertidao(Certidao Certidao) {
        this.certidao = Certidao;
    }

    public LazyDataModel<Certidao> getLazyDataModel() {
        return lazyDataModel;
    }

    public void setLazyDataModel(LazyDataModel<Certidao> lazyDataModel) {
        this.lazyDataModel = lazyDataModel;
    }
}
