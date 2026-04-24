package br.gov.mt.sejudh.sigepen.ws;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioJumbo;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioJumboDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioJumboXlsxBuilder;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.RelatorioJumboService;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Permissoes;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import net.sf.jasperreports.engine.JRException;

@Path("/relatorio/jumbo")
@RequestScoped
public class RelatorioJumboWebService {

    @Inject
    private RelatorioJumboService service;

    @Inject
    private Logger log;

    @Context
    private ServletContext servletContext;

    static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @POST
    @Path("/gerar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ "application/pdf", MediaType.APPLICATION_JSON })
    public Response gerar(String json) {
        return montarResposta(processar(json));
    }

    @POST
    @Path("/gerar/xlsx")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ CONTENT_TYPE_XLSX, MediaType.APPLICATION_JSON })
    public Response gerarXlsx(String json) {
        return montarResposta(processarXlsx(json));
    }

    private Response montarResposta(Resultado r) {
        ResponseBuilder rb = Response.status(r.status).entity(r.body).type(r.contentType);
        if (r.filename != null) {
            rb.header("Content-Disposition", "attachment; filename=" + r.filename);
        }
        return rb.build();
    }

    Resultado processar(String json) {
        try {
            if (!usuarioAutorizado()) {
                return erro(Status.FORBIDDEN, "Acesso negado.");
            }

            FiltroRelatorioJumbo filtro = desserializar(json);
            List<RelatorioJumboDTO> lista = service.buscar(filtro);

            if (lista.isEmpty()) {
                JsonObject js = new JsonObject();
                js.addProperty("mensagem", "Nenhum registro encontrado.");
                return new Resultado(Status.OK, MediaType.APPLICATION_JSON, js.toString(), null);
            }

            byte[] pdf = gerarPdf(lista);
            auditar(filtro, lista.size());

            String filename = "RelatorioJumbo_" + timestamp() + ".pdf";
            return new Resultado(Status.OK, "application/pdf", pdf, filename);

        } catch (IllegalArgumentException e) {
            return erro(Status.BAD_REQUEST, e.getMessage());
        } catch (JsonSyntaxException e) {
            return erro(Status.BAD_REQUEST, "Corpo da requisição com JSON inválido.");
        } catch (Exception e) {
            logarErro(e);
            return erro(Status.INTERNAL_SERVER_ERROR, "Erro ao gerar relatório Jumbo.");
        }
    }

    Resultado processarXlsx(String json) {
        try {
            if (!usuarioAutorizado()) {
                return erro(Status.FORBIDDEN, "Acesso negado.");
            }

            FiltroRelatorioJumbo filtro = desserializar(json);
            List<RelatorioJumboDTO> lista = service.buscarRaw(filtro);

            if (lista.isEmpty()) {
                JsonObject js = new JsonObject();
                js.addProperty("mensagem", "Nenhum registro encontrado.");
                return new Resultado(Status.OK, MediaType.APPLICATION_JSON, js.toString(), null);
            }

            byte[] xlsx = gerarXlsxBytes(lista);
            auditarXlsx(filtro, lista.size());

            String filename = "RelatorioJumbo_" + timestamp() + ".xlsx";
            return new Resultado(Status.OK, CONTENT_TYPE_XLSX, xlsx, filename);

        } catch (IllegalArgumentException e) {
            return erro(Status.BAD_REQUEST, e.getMessage());
        } catch (JsonSyntaxException e) {
            return erro(Status.BAD_REQUEST, "Corpo da requisição com JSON inválido.");
        } catch (Exception e) {
            logarErro(e);
            return erro(Status.INTERNAL_SERVER_ERROR, "Erro ao gerar relatório Jumbo.");
        }
    }

    boolean usuarioAutorizado() {
        Subject subject = SecurityUtils.getSubject();
        return subject != null
                && subject.isAuthenticated()
                && subject.isPermitted(Permissoes.RELATORIO_JUMBO);
    }

    FiltroRelatorioJumbo desserializar(String json) {
        FiltroRelatorioJumbo filtro = new Gson().fromJson(json, FiltroRelatorioJumbo.class);
        if (filtro == null) {
            throw new IllegalArgumentException("Corpo da requisição inválido.");
        }
        return filtro;
    }

    byte[] gerarPdf(List<RelatorioJumboDTO> lista) throws JRException {
        Map<String, Object> parameters = new HashMap<>();
        Arquivo arquivo = RelatorioUtil.gerarRelatorioRest(lista, Relatorio.ListarRelatorioJumbo, parameters,
                servletContext, SecurityUtil.getNomeUnidadeUsuario(), emissorSeguro());
        return arquivo.getBytes();
    }

    byte[] gerarXlsxBytes(List<RelatorioJumboDTO> lista) throws IOException {
        return RelatorioJumboXlsxBuilder.gerar(lista);
    }

    String emissorSeguro() {
        try {
            return SecurityUtil.getMatriculaUsuario();
        } catch (Exception e) {
            return "";
        }
    }

    void auditar(FiltroRelatorioJumbo filtro, int qtdRegistros) {
        LoggerUtil.logInfo(Acao.IMPRIMIR,
                "Relatório Jumbo [WS, formato=PDF, unidades=" + filtro.getUnidadesSelecionadas()
                        + ", qtd=" + qtdRegistros + "]",
                log);
    }

    void auditarXlsx(FiltroRelatorioJumbo filtro, int qtdRegistros) {
        LoggerUtil.logInfo(Acao.IMPRIMIR,
                "Relatório Jumbo [WS, formato=XLSX, unidades=" + filtro.getUnidadesSelecionadas()
                        + ", qtd=" + qtdRegistros + "]",
                log);
    }

    void logarErro(Throwable e) {
        LoggerUtil.logWS(e, log);
    }

    String timestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    private Resultado erro(Status status, String mensagem) {
        JsonObject js = new JsonObject();
        js.addProperty("erro", mensagem);
        return new Resultado(status, MediaType.APPLICATION_JSON, js.toString(), null);
    }

    static class Resultado {
        final Status status;
        final String contentType;
        final Object body;
        final String filename;

        Resultado(Status status, String contentType, Object body, String filename) {
            this.status = status;
            this.contentType = contentType;
            this.body = body;
            this.filename = filename;
        }
    }
}
