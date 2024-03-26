package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.FaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TipoFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.model.dto.FaltaDisciplinarDTO;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.FaltaDisciplinarService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.TipoFaltaDisciplinarService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "ListarFaltaDisciplinar", pattern = "/falta-disciplinar", viewId = "/WEB-INF/pages/faltadisciplinar/ListarFaltaDisciplinar.xhtml")
        , @URLMapping(id = "CadastrarFaltaDisciplinar", pattern = "/falta-disciplinar/cadastrar", viewId = "/WEB-INF/pages/faltadisciplinar/CadastrarFaltaDisciplinar.xhtml")
        , @URLMapping(id = "IniciarFaltaDisciplinar", pattern = "/falta-disciplinar/iniciar/#{id}", viewId = "/WEB-INF/pages/faltadisciplinar/CadastrarFaltaDisciplinar.xhtml")
        , @URLMapping(id = "ConcluirFaltaDisciplinar", pattern = "/falta-disciplinar/concluir/#{id}", viewId = "/WEB-INF/pages/faltadisciplinar/CadastrarFaltaDisciplinar.xhtml")
        , @URLMapping(id = "AlterarFaltaDisciplinar", pattern = "/falta-disciplinar/alterar/#{id}", viewId = "/WEB-INF/pages/faltadisciplinar/CadastrarFaltaDisciplinar.xhtml")
        , @URLMapping(id = "VisualizarFaltaDisciplinar", pattern = "/falta-disciplinar/visualizar/#{id}", viewId = "/WEB-INF/pages/faltadisciplinar/CadastrarFaltaDisciplinar.xhtml")
})
public class FaltaDisciplinarBean extends MetodosDePaginaBean {

    private FiltroFaltaDisciplinar filtro;

    private FaltaDisciplinar faltaDisciplinar;

    private LazyDataModel<FaltaDisciplinarDTO> faltaDisciplinars;

    private List<TipoFaltaDisciplinar> tipoFaltaDisciplinars;

    @Inject
    private TipoFaltaDisciplinarService tipoFaltaDisciplinarService;

    @Inject
    private FaltaDisciplinarService service;

    @Inject
    private RecuperandoService recuperandoService;

    @Inject
    private UnidadePrisionalService unidadeService;

    @Inject
    private FacesUtil facesUtil;

    @Inject
    private SecurityUtil securityUtil;

    @Inject
    private Logger log;

    @Inject
    private FileDownloadHandlerBean fileDownload;

    @URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
    public void carregaFiltros() throws Exception {
        this.setFiltro(new FiltroFaltaDisciplinar());
        this.getFiltro().setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());

        super.carregarFiltroPaginacao(FiltroFaltaDisciplinar.class, this, getFiltro());
    }

    @URLAction(onPostback = false)
    public String init() {
        try {
            this.tipoFaltaDisciplinars = tipoFaltaDisciplinarService.listarTipoFaltaDisciplinarAtivo();

            ArrayList<AcaoDeTela> acaoBuscaDados = new ArrayList<>(Arrays.asList(
                    AcaoDeTela.ALTERAR,
                    AcaoDeTela.INICIAR,
                    AcaoDeTela.FINALIZAR,
                    AcaoDeTela.CONCLUIR,
                    AcaoDeTela.VISUALIZAR
            ));
            if (acaoBuscaDados.contains(this.getAcaoDeTela())) {
                long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

                if (!buscarFaltaDisciplinar(id)) {
                    facesUtil.exibirMensagemErro("Falta disciplinar não encontrada.", true);
                    return "pretty:ListarFaltaDisciplinar";
                }

                if (!validarAcessoFaltaDisciplinar()) {
                    facesUtil.exibirMensagemErro("Operação não permitida.", true);
                    return "pretty:ListarFaltaDisciplinar";
                }

                logDeAcesso(log, this.faltaDisciplinar);
            }

            switch (this.getAcaoDeTela()) {
                case CADASTRAR:
                    this.faltaDisciplinar = new FaltaDisciplinar(Status.ATIVO);
                    this.faltaDisciplinar.setNovaSituacao(SituacaoFaltaDisciplinar.PENDENTE);
                    break;

                case VISUALIZAR:
                case ALTERAR:
                    this.faltaDisciplinar.setNovaSituacao(this.getSituacaoAtual());
                    break;

                case INICIAR:
                    this.faltaDisciplinar.setNovaSituacao(SituacaoFaltaDisciplinar.EM_ANDAMENTO);
                    break;

                case CONCLUIR:
                    this.faltaDisciplinar.setNovaSituacao(SituacaoFaltaDisciplinar.CONCLUIDA);
                    break;

                case LISTAR:
                    pesquisar();
                    logDeAcesso(log, "Falta Disciplinar");
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

    private boolean validarAcessoFaltaDisciplinar() {
        if (SecurityUtil.getUnidadeSelecionada().getId() != this.faltaDisciplinar.getEstruturaOcorrencia().getUnidadePrisional().getId()) {
            return false;
        }

        if (this.getAcaoDeTela().equals(AcaoDeTela.ALTERAR) && !this.exibirAcaoAlterar(this.getSituacaoAtual())) {
            return false;
        }

        if (this.getAcaoDeTela().equals(AcaoDeTela.INICIAR) && !this.exibirAcaoIniciar(this.getSituacaoAtual())) {
            return false;
        }

        if (this.getAcaoDeTela().equals(AcaoDeTela.CONCLUIR) && !this.exibirAcaoConcluir(this.getSituacaoAtual())) {
            return false;
        }

        return true;
    }

    private Boolean verificarSeBiometriaEhObrigatoria() {
        return unidadeService.verificarSeBiometriaEhObrigatoria(SecurityUtil.getUnidadeSelecionada().getId());
    }



    private boolean buscarFaltaDisciplinar(long id) {
        if (id <= 0) {
            return false;
        }

        this.faltaDisciplinar = service.buscaFaltaDisciplinarPorId(id);
       /* Recuperando recuperando = buscarRecuperandoLotacaoAtual(this.faltaDisciplinar.getRecuperando().getId(), false);
        this.faltaDisciplinar.setRecuperando(recuperando);
        // Configura a lotação atual conforme a configurada na Falta disciplinar
        configurarLotacaoAtual(this.faltaDisciplinar.getRecuperando(),this.faltaDisciplinar.getEstruturaOcorrencia());
*/
        if (this.faltaDisciplinar == null) {
            return false;
        }

        return true;
    }

    public boolean getIsCadastro() {
        return getAcaoDeTela().equals(AcaoDeTela.CADASTRAR);
    }

    public boolean exibirAcaoAlterar(SituacaoFaltaDisciplinar situacao) {

        if ( ( securityUtil.possuiPermissao("AlterarFaltaDisciplinar") && situacao.equals(SituacaoFaltaDisciplinar.PENDENTE) ) ||
                        (securityUtil.possuiPermissao("AlterarTudoFaltaDisciplinar") && !situacao.equals(SituacaoFaltaDisciplinar.CANCELADA))
        ) {
            return true;
        }
        return false;
    }

    public boolean exibirAcaoIniciar(SituacaoFaltaDisciplinar situacao) {

        if (securityUtil.possuiPermissao("IniciarFaltaDisciplinar") && situacao.equals(SituacaoFaltaDisciplinar.PENDENTE)
        ) {
            return true;
        }
        return false;
    }

    public boolean exibirAcaoConcluir(SituacaoFaltaDisciplinar situacao) {

        if (securityUtil.possuiPermissao("ConcluirFaltaDisciplinar") && situacao.equals(SituacaoFaltaDisciplinar.EM_ANDAMENTO)
        ) {
            return true;
        }
        return false;
    }

    public boolean exibirAcaoCancelar(SituacaoFaltaDisciplinar situacao) {

        if (securityUtil.possuiPermissao("CancelarFaltaDisciplinar") && !situacao.equals(SituacaoFaltaDisciplinar.CANCELADA)
        ) {
            return true;
        }
        return false;
    }

    public boolean exibirAcaoExcluir(SituacaoFaltaDisciplinar situacao) {

        if (securityUtil.possuiPermissao("ExcluirFaltaDisciplinar") && situacao.equals(SituacaoFaltaDisciplinar.PENDENTE)
        ) {
            return true;
        }
        return false;
    }

    private boolean temAlgumaPermissaoQuePermitarAlterar(){
        if(securityUtil.possuiPermissao("CadastrarFaltaDisciplinar") ||
                securityUtil.possuiPermissao("AlterarFaltaDisciplinar") ||
                securityUtil.possuiPermissao("IniciarFaltaDisciplinar") ||
                securityUtil.possuiPermissao("ConcluirFaltaDisciplinar") ||
                securityUtil.possuiPermissao("AlterarTudoFaltaDisciplinar")){
            return true;
        }
        return false;
    }

    public boolean exibirMotivoCancelamento(){
        if(  this.getSituacaoAtual().equals(SituacaoFaltaDisciplinar.CANCELADA) ){
            return true;
        }
        return false;
    }

    public boolean exibirBotaoConfirmarSemConfirmacao() {
        if (
                temAlgumaPermissaoQuePermitarAlterar() &&
                        !this.getIsVisualizacao()
        ) {
            return true;
        }
        return false;
    }
    private SituacaoFaltaDisciplinar getSituacaoAtual(){
        SituacaoFaltaDisciplinar situacaoAtual = this.faltaDisciplinar.getSituacao();
        if(situacaoAtual == null){
            situacaoAtual = SituacaoFaltaDisciplinar.PENDENTE;
        }
        return situacaoAtual;
    }

    public boolean podeAlterarDadosIniciais() {
        if (!this.getIsVisualizacao() && (this.faltaDisciplinar.getNovaSituacao().equals(SituacaoFaltaDisciplinar.PENDENTE) || securityUtil.possuiPermissao("AlterarTudoFaltaDisciplinar"))) {
            return true;
        }

        if(this.getAcaoDeTela().equals(AcaoDeTela.ALTERAR) && this.getSituacaoAtual().getId() > SituacaoFaltaDisciplinar.PENDENTE.getId()){
            return true;
        }
        return false;
    }

    public boolean podeAlterarDadosPad() {
        if (!this.getIsVisualizacao() && (this.faltaDisciplinar.getNovaSituacao().equals(SituacaoFaltaDisciplinar.EM_ANDAMENTO)) || securityUtil.possuiPermissao("AlterarTudoFaltaDisciplinar")){
            return true;
        }
        return false;
    }

    public boolean podeAlterarDadosParecer() {
        if (!this.getIsVisualizacao() && (this.faltaDisciplinar.getNovaSituacao().equals(SituacaoFaltaDisciplinar.CONCLUIDA) || securityUtil.possuiPermissao("AlterarTudoFaltaDisciplinar"))) {
            return true;
        }
        return false;
    }

    public boolean exibirDadosFato(){
        SituacaoFaltaDisciplinar novaSituacao = this.faltaDisciplinar.getNovaSituacao();
        if(novaSituacao.equals(SituacaoFaltaDisciplinar.PENDENTE) || novaSituacao.equals(SituacaoFaltaDisciplinar.CANCELADA) ){
            return true;
        }
        if(this.getSituacaoAtual().getId() >= SituacaoFaltaDisciplinar.PENDENTE.getId()){
            return true;
        }

        return false;
    }

    public boolean exibirDivisaDadosPad(){
        if(this.getSituacaoAtual().equals(SituacaoFaltaDisciplinar.CANCELADA)){
            return true;
        }
        if(this.getSituacaoAtual().getId() >= SituacaoFaltaDisciplinar.EM_ANDAMENTO.getId()){
            return true;
        }
        if(this.faltaDisciplinar.getNovaSituacao().getId() >= SituacaoFaltaDisciplinar.EM_ANDAMENTO.getId()){
            return true;
        }
        return false;
    }
    public boolean exibirDadosPad(){
        SituacaoFaltaDisciplinar novaSituacao = this.faltaDisciplinar.getNovaSituacao();
        if(novaSituacao.equals(SituacaoFaltaDisciplinar.EM_ANDAMENTO) || novaSituacao.equals(SituacaoFaltaDisciplinar.CANCELADA) ){
            return true;
        }

        if(this.getSituacaoAtual().getId() > SituacaoFaltaDisciplinar.EM_ANDAMENTO.getId()){
            return true;
        }

        if(this.faltaDisciplinar.getNovaSituacao().getId() >= SituacaoFaltaDisciplinar.EM_ANDAMENTO.getId()){
            return true;
        }

        return false;
    }

    public boolean exibirDivisaDadosParecer(){
        if(this.getSituacaoAtual().equals(SituacaoFaltaDisciplinar.CANCELADA)){
            return true;
        }

        if( this.getSituacaoAtual().getId() >= SituacaoFaltaDisciplinar.CONCLUIDA.getId()){
            return true;
        }

        if( this.faltaDisciplinar.getNovaSituacao().getId() >= SituacaoFaltaDisciplinar.CONCLUIDA.getId()){
            return true;
        }
        return false;
    }

    public boolean exibirDadosParecer(){
        SituacaoFaltaDisciplinar novaSituacao = this.faltaDisciplinar.getNovaSituacao();
        if(novaSituacao.equals(SituacaoFaltaDisciplinar.CONCLUIDA) || novaSituacao.equals(SituacaoFaltaDisciplinar.CANCELADA) ){
            return true;
        }

        if( this.getSituacaoAtual().getId() >= SituacaoFaltaDisciplinar.FINALIZADA.getId()){
            return true;
        }

        return false;
    }

    public String salvar() {
        String retorno = null;

        try {
            service.salvar(this.faltaDisciplinar, this.faltaDisciplinar.getNovaSituacao());
            facesUtil.exibirMensagemInfo("Falta disciplinar "+getStringAcao()+" com sucesso!", true);
            retorno = "pretty:ListarFaltaDisciplinar";
            LoggerUtil.logInfo(Acao.SALVAR, this.faltaDisciplinar, log);
        } catch (ValidacaoException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATENÇÃO", e.getMessage());
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar falta disciplinar (" + this.faltaDisciplinar.getId() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.SALVAR, this.faltaDisciplinar, e, log);
        }

        return retorno;
    }


    private String getStringAcao() {
        String acao = "";
        if(this.getAcaoDeTela().equals(AcaoDeTela.CADASTRAR)){
            acao = "para o recuperando " + this.faltaDisciplinar.getRecuperando().getPessoa().getNome() + " cadastrada" ;
        }else if(this.getAcaoDeTela().equals(AcaoDeTela.ALTERAR)){
            acao = "alterada";
        }else if(this.getAcaoDeTela().equals(AcaoDeTela.INICIAR)){
            acao = "iniciada";
        }else if(this.getAcaoDeTela().equals(AcaoDeTela.CONCLUIR)){
            acao = "conclúida";
        }
        return acao;
    }

    public void excluir(FaltaDisciplinarDTO faltaDisciplinarDTO) {
        try {
            service.excluir(faltaDisciplinarDTO);
            facesUtil.exibirMensagemInfo("Falta disciplinar excluída com sucesso!", false);
            LoggerUtil.logInfo(Acao.EXCLUIR, faltaDisciplinarDTO, log);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir falta disciplinar (" + faltaDisciplinarDTO.getId() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.EXCLUIR, faltaDisciplinarDTO, e, log);
        }
    }

    public void pesquisar() {
        this.faltaDisciplinars = new LazyDataModel<FaltaDisciplinarDTO>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<FaltaDisciplinarDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                setRowCount(service.quantidadeFiltrados(getFiltro()));

                try {
                    configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return service.buscaPorFiltro(getFiltro(), false);
            }
        };
    }

    public void imprimir() {
        try {
            List<FaltaDisciplinarDTO> lista = service.buscaPorFiltro(filtro, true);

            if (lista.size() == 0) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
                return;
            }

            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarFaltaDisciplinar));
            RequestContext.getCurrentInstance().execute("download()");
            LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório Falta Disciplinar Recuperando", log);
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de Falta Disciplinar: " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, "Relatório Falta Disciplinar Recuperando", e, log);
        }
    }
    public void imprimirFaltaDisciplinar(Long id) {
        try {
            this.faltaDisciplinar = service.buscaFaltaDisciplinarPorId(id);

            ArrayList<FaltaDisciplinar> lista = new ArrayList<>();
            lista.add(this.faltaDisciplinar);

            LoggerUtil.logInfo(Acao.IMPRIMIR, this.faltaDisciplinar, log);
            fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.FaltaDisciplinarRecuperando));
            RequestContext.getCurrentInstance().execute("download()");
            LoggerUtil.logInfo(Acao.IMPRIMIR, this.faltaDisciplinar, log);
            lista.clear();
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir falta disciplinar do recuperando (" + this.faltaDisciplinar.getId() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.IMPRIMIR, this.faltaDisciplinar, e, log);
        }
    }

    public void abrirDialogRecuperando(ActionEvent event) {
        facesUtil.exibirPaginaDialog("pretty:AssociarRecuperando");
    }

    public void abrirJanelaIniciarFalta(FaltaDisciplinarDTO falta) {
        String atualUrl = PrettyContext.getCurrentInstance().getContextPath() + PrettyContext.getCurrentInstance().getRequestURL().toString();
         atualUrl = atualUrl+"/iniciar/"+falta.getId();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(atualUrl);

        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Erro", "Erro ao iniciar Falta Disciplinar (" + falta.getId() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.INICIAR, falta, e, log);
        }

    }
    public void abrirJanelaConcluirFalta(FaltaDisciplinarDTO falta) {
        String atualUrl = PrettyContext.getCurrentInstance().getContextPath() + PrettyContext.getCurrentInstance().getRequestURL().toString();
        atualUrl = atualUrl+"/concluir/"+falta.getId();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(atualUrl);

        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Erro", "Erro ao concluir Falta Disciplinar (" + falta.getId() + "): " + e.getMessage());
            LoggerUtil.logErro(Acao.INICIAR, falta, e, log);
        }

    }

    public void configurarTipoFaltaDisciplinar(AjaxBehaviorEvent event) throws AbortProcessingException {
        System.out.println(event.getSource());
    }

    public void associarRecuperando(SelectEvent event) {
    	RecuperandoView r = (RecuperandoView) event.getObject();

        try {
            if (r == null) {
                return;
            }

            Recuperando recuperando = this.buscarRecuperandoLotacaoAtual(r.getId(), false);

            if (recuperando == null) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Recuperando não encontrado.");
                return;
            }

            this.faltaDisciplinar.setRecuperando(recuperando);

        } catch (Exception e) {
            this.faltaDisciplinar.setRecuperando(null);

            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao associar recuperando a falta disciplinar: " + e.getMessage());
            LoggerUtil.logErro(Acao.ASSOCIAR, r, e, log);
        }
    }

    public Recuperando buscarRecuperandoLotacaoAtual(long id, boolean buscarAuditoria) {
        Recuperando recuperando = recuperandoService.buscarRecuperandoPorId(id, buscarAuditoria);
        return recuperando;
    }

    public void cancelarFaltaDisciplinar(FaltaDisciplinarDTO falta) {
        try {
            this.faltaDisciplinar = service.buscaFaltaDisciplinarPorId(falta.getId());
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao buscar falta disciplinar: " + e.getMessage());
            LoggerUtil.logErro(null, falta, e, log);
        }
    }

    public void confirmarCancelamentoFaltaDisciplinar() {
        try {
            service.cancelar(this.faltaDisciplinar);
            //facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO,"","Falta disciplinar cancelada com sucesso!");
            facesUtil.exibirMensagemInfo("Falta disciplinar cancelada com sucesso!", false);
            LoggerUtil.logInfo(Acao.CANCELAR, faltaDisciplinar, log);
            RequestContext.getCurrentInstance().execute("PF('cancelarFaltaDisciplinar').hide()");
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao cancelar falta disciplinar: " + e.getMessage());
            LoggerUtil.logErro(Acao.CANCELAR, faltaDisciplinar, e, log);
        }
    }

    public void abrirDialogEstrutura(ActionEvent event) {
    	facesUtil.setObjetoSessao("unidadeSelecionadaUsuario", SecurityUtil.getUnidadeSelecionada().getId());
        facesUtil.exibirPaginaDialog("pretty:AssociarQualquerEstrutura");
    }

    public void associarEstrutura(SelectEvent event) {
        Estrutura est = (Estrutura) event.getObject();
        if (est == null) return;

        this.faltaDisciplinar.setEstruturaOcorrencia(est);
    }

    public FiltroFaltaDisciplinar getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroFaltaDisciplinar filtro) {
        this.filtro = filtro;
    }

    public FaltaDisciplinar getFaltaDisciplinar() {
        return faltaDisciplinar;
    }

    public void setFaltaDisciplinar(FaltaDisciplinar faltaDisciplinar) {
        this.faltaDisciplinar = faltaDisciplinar;
    }

    public LazyDataModel<FaltaDisciplinarDTO> getFaltaDisciplinars() {
        return faltaDisciplinars;
    }

    public void setFaltaDisciplinars(LazyDataModel<FaltaDisciplinarDTO> faltaDisciplinars) {
        this.faltaDisciplinars = faltaDisciplinars;
    }

    public List<TipoFaltaDisciplinar> getTipoFaltaDisciplinars() {
        return tipoFaltaDisciplinars;
    }

    public void setTipoFaltaDisciplinars(List<TipoFaltaDisciplinar> tipoFaltaDisciplinars) {
        this.tipoFaltaDisciplinars = tipoFaltaDisciplinars;
    }

}
