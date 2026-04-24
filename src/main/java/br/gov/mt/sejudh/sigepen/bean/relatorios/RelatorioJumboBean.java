package br.gov.mt.sejudh.sigepen.bean.relatorios;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.bean.outros.MetodosDialogBean;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioJumbo;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoArquivo;
import br.gov.mt.sejudh.sigepen.enumerator.UnidadeJumbo;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioJumboDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioJumboXlsxBuilder;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.RelatorioJumboService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Permissoes;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import net.sf.jasperreports.engine.JRException;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = Permissoes.RELATORIO_JUMBO, pattern = "/relatorio/recuperandos/jumbo", viewId = "/WEB-INF/pages/relatorio/RelatorioJumbo.xhtml"),
})
public class RelatorioJumboBean extends MetodosDialogBean {

    @Inject
    private RelatorioJumboService service;

    @Inject
    private UnidadePrisionalService unidadeService;

    @Inject
    private FacesUtil facesUtil;

    @Inject
    private FileDownloadHandlerBean fileDownload;

    @Inject
    private Logger log;

    private FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();

    private List<UnidadePrisional> unidadesDisponiveis = new ArrayList<>();

    @URLAction(onPostback = false)
    public String init() {
        this.unidadesDisponiveis = unidadeService.listarPorIds(UnidadeJumbo.todosIds());
        List<Long> idsDisponiveis = this.unidadesDisponiveis.stream()
                .map(UnidadePrisional::getId)
                .collect(Collectors.toList());
        this.filtro.setUnidadesSelecionadas(idsDisponiveis);
        return "Pretty:GerarRelatorioJumbo";
    }

    public void imprimir() {
        if (filtro.getUnidadesSelecionadas() == null || filtro.getUnidadesSelecionadas().isEmpty()) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Selecione ao menos uma unidade.");
            return;
        }

        try {
            List<RelatorioJumboDTO> lista = service.buscar(filtro);

            if (lista.isEmpty()) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
                        "Relatório não impresso, nenhum registro encontrado!");
                return;
            }

            fileDownload.prepararArquivo(gerarPdf(lista));
            disparaDownload();
            registrarSucessoDeImpressao(lista.size());

        } catch (IllegalArgumentException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", e.getMessage());
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
                    "Erro ao imprimir Relatório Jumbo: " + e.getMessage());
            registrarErroImpressao(e);
        }
    }

    public void exportarXlsx() {
        if (filtro.getUnidadesSelecionadas() == null || filtro.getUnidadesSelecionadas().isEmpty()) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Selecione ao menos uma unidade.");
            return;
        }

        try {
            List<RelatorioJumboDTO> lista = service.buscarRaw(filtro);

            if (lista.isEmpty()) {
                facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO",
                        "Relatório não exportado, nenhum registro encontrado!");
                return;
            }

            fileDownload.prepararArquivoXlsx(gerarXlsx(lista));
            disparaDownload();
            registrarSucessoDeExportacaoXlsx(lista.size());

        } catch (IllegalArgumentException e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", e.getMessage());
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
                    "Erro ao exportar Relatório Jumbo: " + e.getMessage());
            registrarErroExportacaoXlsx(e);
        }
    }

    void registrarErroImpressao(Throwable e) {
        LoggerUtil.logErro(Acao.IMPRIMIR, "Relatório Jumbo", e, log);
    }

    void registrarErroExportacaoXlsx(Throwable e) {
        LoggerUtil.logErro(Acao.IMPRIMIR, "Relatório Jumbo [formato=XLSX]", e, log);
    }

    Arquivo gerarPdf(List<RelatorioJumboDTO> lista) throws JRException {
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        return RelatorioUtil.gerarRelatorioRest(
                lista,
                Relatorio.ListarRelatorioJumbo,
                new HashMap<>(),
                ctx,
                SecurityUtil.getNomeUnidadeUsuario(),
                SecurityUtil.getMatriculaUsuario());
    }

    Arquivo gerarXlsx(List<RelatorioJumboDTO> lista) throws IOException {
        Arquivo arquivo = new Arquivo();
        arquivo.setBytes(RelatorioJumboXlsxBuilder.gerar(lista));
        arquivo.setNome("RelatorioJumbo_" + timestampArquivo());
        arquivo.setTipo(TipoArquivo.XLSX);
        arquivo.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return arquivo;
    }

    String timestampArquivo() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    void disparaDownload() {
        RequestContext.getCurrentInstance().execute("download()");
    }

    void registrarSucessoDeImpressao(int qtdRegistros) {
        LoggerUtil.logInfo(Acao.IMPRIMIR,
                "Relatório Jumbo [formato=PDF, unidades=" + filtro.getUnidadesSelecionadas() + ", qtd=" + qtdRegistros + "]",
                log);
    }

    void registrarSucessoDeExportacaoXlsx(int qtdRegistros) {
        LoggerUtil.logInfo(Acao.IMPRIMIR,
                "Relatório Jumbo [formato=XLSX, unidades=" + filtro.getUnidadesSelecionadas()
                        + ", qtd=" + qtdRegistros + "]",
                log);
    }

    public FiltroRelatorioJumbo getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioJumbo filtro) {
        this.filtro = filtro;
    }

    public List<UnidadePrisional> getUnidadesDisponiveis() {
        return unidadesDisponiveis;
    }

    public void setUnidadesDisponiveis(List<UnidadePrisional> unidadesDisponiveis) {
        this.unidadesDisponiveis = unidadesDisponiveis;
    }
}
