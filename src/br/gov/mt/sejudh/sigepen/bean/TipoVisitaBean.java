package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.base.dataModel.AbstractDataModel;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Certidao;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.TipoVisitaCertidao;
import br.gov.mt.sejudh.sigepen.model.TipoVisitaDocumento;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.TipoVisitaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "ListarTipoVisita", pattern = "/visita", viewId = "/WEB-INF/pages/visita/ListarTipoVisita.xhtml"),
        @URLMapping(id = "CadastrarTipoVisita", pattern = "/visita/cadastrar", viewId = "/WEB-INF/pages/visita/CadastrarTipoVisita.xhtml"),
        @URLMapping(id = "VisualizarTipoVisita", pattern = "/visita/visualizar/#{id}", viewId = "/WEB-INF/pages/visita/CadastrarTipoVisita.xhtml"),
        @URLMapping(id = "AlterarTipoVisita", pattern = "/visita/alterar/#{id}", viewId = "/WEB-INF/pages/visita/CadastrarTipoVisita.xhtml")
})
public class TipoVisitaBean extends MetodosDePaginaBean {

    private FiltroTipoVisita filtro;

    private TipoVisita tipoVisita;

    private TipoVisitaDocumento tipoVisitaDocumentoAux;

    private TipoVisitaCertidao tipoVisitaCertidaoAux;

    private LazyDataModel<TipoVisita> tipoVisitas;

    @Inject
    private TipoVisitaService service;

    @Inject
    private FacesUtil facesUtil;

    @Inject
    private FileDownloadHandlerBean fileDownload;

    @Inject
    private Logger log;

    public void pesquisar() {
        this.tipoVisitas = new AbstractDataModel<>(this.service, this.getFiltro(), this);
    }

    @URLAction(onPostback = false)
    public String init() {
        try {
            super.carregarFiltroPaginacao(FiltroTipoVisita.class, this);
            switch (this.getAcaoDeTela()) {
                case CADASTRAR:
                    this.tipoVisita = new TipoVisita();
                    this.tipoVisita.setTipoVisitaDocumento(new ArrayList<>());
                    this.tipoVisita.setTipoVisitaCertidao(new ArrayList<>());
                    break;

                case ALTERAR:
                case VISUALIZAR:
                    long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
                    
                    if (id == 0) {
                    	id = (long) facesUtil.getObjetoSessao("idTipoVisita");
                    }
                    
                    if(id > 0) {   	
                    	facesUtil.setObjetoSessao("idTipoVisita", id);
	                    if (!buscarTipoVisita(id)) {
	                        facesUtil.exibirMensagemErro("Tipo Visita não encontrado.", true);
	                        return "pretty:ListarTipoVisita";
	                    }
	                    logDeAcesso(log, this.tipoVisita);
                    }

                    break;

                case LISTAR:
                    pesquisar();
                    logDeAcesso(log, "Tipo Visitas");
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

    private boolean buscarTipoVisita(long id) {
        if (id <= 0) {
            return false;
        }

        this.tipoVisita = service.buscarPorId(id, true);

        if (this.tipoVisita == null) {
            return false;
        }

        return true;
    }

    public String salvar() {
        String retorno = null;

        try {
            service.salvar(this.tipoVisita);
            facesUtil.exibirMensagemInfo("Tipo Visita " + this.tipoVisita.getDescricao() + " salvo com sucesso!", true);
            retorno = "pretty:ListarTipoVisita";
            LoggerUtil.logInfo(Acao.SALVAR, this.tipoVisita, log);
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar Certidao (" + this.tipoVisita.getDescricao() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.SALVAR, this.tipoVisita, e, log);
        }

        return retorno;
    }

    public void excluir(TipoVisita tipoVisita) {
        try {
            service.excluir(tipoVisita);
            facesUtil.exibirMensagemInfo("Tipo Visita " + tipoVisita.getDescricao() + " excluído com sucesso!", false);
            LoggerUtil.logInfo(Acao.EXCLUIR, tipoVisita, log);
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof ConstraintViolationException) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Existe visitante vinculado ao tipo de visita!");
                return;
            }
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Tipo Visita (" + tipoVisita.getDescricao() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.EXCLUIR, tipoVisita, e, log);
        }
    }

    public void imprimir() {
        try {
            List<TipoVisita> lista = service.buscaPorFiltro(filtro, true);

            if (lista.size() == 0) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
                return;
            }

            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarTipoVisita));
            RequestContext.getCurrentInstance().execute("download()");
            LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Tipo Visita", log);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de tipo visita: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Tipo Visita", e, log);
        }
    }

    public void criaDocumento() {
        this.tipoVisitaDocumentoAux = new TipoVisitaDocumento();
    }

    private boolean validaDocumentoJaExiste() {
    	int indice = tipoVisita.getTipoVisitaDocumento().indexOf(this.tipoVisitaDocumentoAux);    	
    	return tipoVisita.getTipoVisitaDocumento().contains(this.tipoVisitaDocumentoAux) && (indice != this.tipoVisitaDocumentoAux.getIndexAlteracao());
    }
    
    public void adicionaDocumento() {
    	if (validaDocumentoJaExiste()) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Documento já incluído");
			return;
		}
    	
    	if (this.tipoVisitaDocumentoAux.getIndexAlteracao() >= 0) {
    		tipoVisita.getTipoVisitaDocumento().set(this.tipoVisitaDocumentoAux.getIndexAlteracao(), this.tipoVisitaDocumentoAux);
    	} else {    		
    		this.tipoVisitaDocumentoAux.setTipoVisita(tipoVisita);
    		tipoVisita.getTipoVisitaDocumento().add(this.tipoVisitaDocumentoAux);
    	}
    	
    	RequestContext.getCurrentInstance().execute("PF('dialogDocumento').hide()");
    }

    public void abrirDialogAlterarDocumento(TipoVisitaDocumento tipoVisitaDocumento) {
    	this.tipoVisitaDocumentoAux = SerializationUtils.clone(tipoVisitaDocumento);
    	this.tipoVisitaDocumentoAux.setIndexAlteracao(this.tipoVisita.getTipoVisitaDocumento().indexOf(tipoVisitaDocumento));
    }

    public void excluirDocumento(TipoVisitaDocumento tipoVisitaDocumento) {
    	this.tipoVisita.getTipoVisitaDocumento().remove(tipoVisitaDocumento);
    }

    public void criaCertidao() {
        this.tipoVisitaCertidaoAux = new TipoVisitaCertidao();
    }
    
    private boolean validaCertidaoJaExiste() {
    	int indice = tipoVisita.getTipoVisitaCertidao().indexOf(this.tipoVisitaCertidaoAux);    	
    	return tipoVisita.getTipoVisitaCertidao().contains(this.tipoVisitaCertidaoAux) && (indice != this.tipoVisitaCertidaoAux.getIndexAlteracao());
    }

    public void adicionaCertidao() {
    	if (validaCertidaoJaExiste()) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Certidão já incluída");
			return;
		}
    	
    	if (this.tipoVisitaCertidaoAux.getIndexAlteracao() >= 0) {
    		tipoVisita.getTipoVisitaCertidao().set(this.tipoVisitaCertidaoAux.getIndexAlteracao(), this.tipoVisitaCertidaoAux);
    	} else {    		
    		this.tipoVisitaCertidaoAux.setTipoVisita(tipoVisita);
    		tipoVisita.getTipoVisitaCertidao().add(this.tipoVisitaCertidaoAux);
    	}
    	
    	RequestContext.getCurrentInstance().execute("PF('dialogCertidao').hide()");
    }


    public void excluirCertidao(TipoVisitaCertidao tipoVisitaCertidao) {
    	tipoVisita.getTipoVisitaCertidao().remove(tipoVisitaCertidao);
    }

    public void abrirDialogAlterarCertidao(TipoVisitaCertidao tipoVisitaCertidao) {
    	this.tipoVisitaCertidaoAux = SerializationUtils.clone(tipoVisitaCertidao);
    	this.tipoVisitaCertidaoAux.setIndexAlteracao(this.tipoVisita.getTipoVisitaCertidao().indexOf(tipoVisitaCertidao));
    }

    public void incluirCertidao(SelectEvent event) {
        Certidao certidao = (Certidao) event.getObject();
        
        if (certidao == null) {
            return;
        }
        if (this.atribuirCertidao(certidao)) {
            this.tipoVisitaCertidaoAux.setCertidao(certidao);
        }
    }

    private Boolean atribuirCertidao(Certidao certidao) {
        return this.tipoVisitaCertidaoAux.getCertidao() == null || this.tipoVisitaCertidaoAux.getCertidao().getId() == null ||
                !this.tipoVisitaCertidaoAux.getCertidao().getId().equals(certidao.getId());
    }

    public void fecharDialog(ActionEvent event) {
        facesUtil.fecharPaginaDialog(null);
    }

    public void abrirDialogTipoCertidao(ActionEvent event) {
        facesUtil.exibirPaginaDialog("pretty:AssociarTipoCertidao", null, 1000);
    }

    public FiltroTipoVisita getFiltro() {
        return this.filtro;
    }

    public void setFiltro(FiltroTipoVisita filtro) {
        this.filtro = filtro;
    }

    public LazyDataModel<TipoVisita> getTipoVisitas() {
        return this.tipoVisitas;
    }

    public TipoVisita getTipoVisita() {
        return tipoVisita;
    }

    public TipoVisitaDocumento getTipoVisitaDocumentoAux() {
        return tipoVisitaDocumentoAux;
    }

    public void setTipoVisitaDocumentoAux(TipoVisitaDocumento tipoVisitaDocumento) {
        this.tipoVisitaDocumentoAux = tipoVisitaDocumento;
    }

    public TipoVisitaCertidao getTipoVisitaCertidaoAux() {
        return tipoVisitaCertidaoAux;
    }

    public void setTipoVisitaCertidaoAux(TipoVisitaCertidao tipoVisitaCertidao) {
        this.tipoVisitaCertidaoAux = tipoVisitaCertidao;
    }
}
