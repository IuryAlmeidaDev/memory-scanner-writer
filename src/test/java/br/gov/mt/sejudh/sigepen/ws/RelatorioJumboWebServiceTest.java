package br.gov.mt.sejudh.sigepen.ws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioJumbo;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioJumboDTO;
import br.gov.mt.sejudh.sigepen.service.RelatorioJumboService;
import br.gov.mt.sejudh.sigepen.ws.RelatorioJumboWebService.Resultado;

public class RelatorioJumboWebServiceTest extends BaseUnitTest {

    @Mock
    private RelatorioJumboService service;

    @Mock
    private Logger log;

    @Spy
    @InjectMocks
    private RelatorioJumboWebService ws;

    @Before
    public void isolarDependenciasEstaticas() throws Exception {
        doReturn(true).when(ws).usuarioAutorizado();
        doReturn(new byte[] { 1, 2, 3 }).when(ws).gerarPdf(any());
        doReturn(new byte[] { 'P', 'K', 3, 4 }).when(ws).gerarXlsxBytes(any());
        doNothing().when(ws).auditar(any(), anyInt());
        doNothing().when(ws).auditarXlsx(any(), anyInt());
        doNothing().when(ws).logarErro(any());
        doReturn("20260422_120000").when(ws).timestamp();
    }

    @Test
    public void processar_deveRetornar403QuandoUsuarioNaoAutorizado() {
        doReturn(false).when(ws).usuarioAutorizado();

        Resultado r = ws.processar("{\"unidadesSelecionadas\":[2,7]}");

        assertThat(r.status).isEqualTo(Status.FORBIDDEN);
        assertThat(r.body.toString()).contains("Acesso negado");
        verify(service, never()).buscar(any());
    }

    @Test
    public void processar_deveRetornar400QuandoJsonForMalformado() {
        Resultado r = ws.processar("{isso nao eh json");

        assertThat(r.status).isEqualTo(Status.BAD_REQUEST);
        assertThat(r.contentType).isEqualTo(MediaType.APPLICATION_JSON);
        verify(service, never()).buscar(any());
    }

    @Test
    public void processar_deveRetornar400QuandoServiceLancaIllegalArgument() {
        when(service.buscar(any())).thenThrow(
                new IllegalArgumentException("Unidade fora da lista de unidades com Jumbo."));

        Resultado r = ws.processar("{\"unidadesSelecionadas\":[2,999]}");

        assertThat(r.status).isEqualTo(Status.BAD_REQUEST);
        assertThat(r.body.toString()).contains("fora da lista");
    }

    @Test
    public void processar_deveRetornar200ComMensagemQuandoListaForVazia() {
        when(service.buscar(any())).thenReturn(Collections.<RelatorioJumboDTO>emptyList());

        Resultado r = ws.processar("{\"unidadesSelecionadas\":[2,7]}");

        assertThat(r.status).isEqualTo(Status.OK);
        assertThat(r.contentType).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(r.body.toString()).contains("Nenhum registro encontrado");
        assertThat(r.filename).isNull();
        verify(ws, never()).auditar(any(), anyInt());
    }

    @Test
    public void processar_caminhoFelizDeveRetornarPdfBytesEAuditar() throws Exception {
        List<RelatorioJumboDTO> lista = Arrays.asList(
                new RelatorioJumboDTO(1L, "Fulano", "Mae", "Unidade", "Cela 1"),
                new RelatorioJumboDTO(2L, "Beltrano", "Mae 2", "Unidade", "Cela 2"));
        when(service.buscar(any())).thenReturn(lista);

        Resultado r = ws.processar("{\"unidadesSelecionadas\":[2,7]}");

        assertThat(r.status).isEqualTo(Status.OK);
        assertThat(r.contentType).isEqualTo("application/pdf");
        assertThat(r.body).isInstanceOf(byte[].class);
        assertThat((byte[]) r.body).containsExactly(1, 2, 3);
        assertThat(r.filename).isEqualTo("RelatorioJumbo_20260422_120000.pdf");
        verify(ws).auditar(any(FiltroRelatorioJumbo.class), eq(2));
    }

    @Test
    public void processar_deveRetornar500EmErroInesperado() throws Exception {
        doThrow(new RuntimeException("boom")).when(service).buscar(any());

        Resultado r = ws.processar("{\"unidadesSelecionadas\":[2]}");

        assertThat(r.status).isEqualTo(Status.INTERNAL_SERVER_ERROR);
        assertThat(r.body.toString()).contains("Erro ao gerar");
        verify(ws).logarErro(any(Throwable.class));
    }

    @Test
    public void processarXlsx_deveRetornar403QuandoUsuarioNaoAutorizado() {
        doReturn(false).when(ws).usuarioAutorizado();

        Resultado r = ws.processarXlsx("{\"unidadesSelecionadas\":[2,7]}");

        assertThat(r.status).isEqualTo(Status.FORBIDDEN);
        assertThat(r.body.toString()).contains("Acesso negado");
        verify(service, never()).buscarRaw(any());
    }

    @Test
    public void processarXlsx_deveRetornar400QuandoJsonForMalformado() {
        Resultado r = ws.processarXlsx("{isso nao eh json");

        assertThat(r.status).isEqualTo(Status.BAD_REQUEST);
        assertThat(r.contentType).isEqualTo(MediaType.APPLICATION_JSON);
        verify(service, never()).buscarRaw(any());
    }

    @Test
    public void processarXlsx_deveRetornar400QuandoServiceLancaIllegalArgument() {
        when(service.buscarRaw(any())).thenThrow(
                new IllegalArgumentException("Unidade fora da lista de unidades com Jumbo."));

        Resultado r = ws.processarXlsx("{\"unidadesSelecionadas\":[2,999]}");

        assertThat(r.status).isEqualTo(Status.BAD_REQUEST);
        assertThat(r.body.toString()).contains("fora da lista");
    }

    @Test
    public void processarXlsx_deveRetornar200ComMensagemQuandoListaForVazia() {
        when(service.buscarRaw(any())).thenReturn(Collections.<RelatorioJumboDTO>emptyList());

        Resultado r = ws.processarXlsx("{\"unidadesSelecionadas\":[2,7]}");

        assertThat(r.status).isEqualTo(Status.OK);
        assertThat(r.contentType).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(r.body.toString()).contains("Nenhum registro encontrado");
        assertThat(r.filename).isNull();
        verify(ws, never()).auditarXlsx(any(), anyInt());
    }

    @Test
    public void processarXlsx_caminhoFelizDeveRetornarXlsxBytesEAuditar() throws Exception {
        List<RelatorioJumboDTO> lista = Arrays.asList(
                new RelatorioJumboDTO(1L, "FULANO", "MAE", "UNIDADE", "CELA 1"),
                new RelatorioJumboDTO(2L, "BELTRANO", "MAE 2", "UNIDADE", "CELA 2"));
        when(service.buscarRaw(any())).thenReturn(lista);

        Resultado r = ws.processarXlsx("{\"unidadesSelecionadas\":[2,7]}");

        assertThat(r.status).isEqualTo(Status.OK);
        assertThat(r.contentType).isEqualTo(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertThat(r.body).isInstanceOf(byte[].class);
        assertThat(r.filename).isEqualTo("RelatorioJumbo_20260422_120000.xlsx");
        verify(ws).auditarXlsx(any(FiltroRelatorioJumbo.class), eq(2));
    }

    @Test
    public void processarXlsx_deveRetornar500EmErroInesperado() throws Exception {
        doThrow(new RuntimeException("boom xlsx")).when(service).buscarRaw(any());

        Resultado r = ws.processarXlsx("{\"unidadesSelecionadas\":[2]}");

        assertThat(r.status).isEqualTo(Status.INTERNAL_SERVER_ERROR);
        assertThat(r.body.toString()).contains("Erro ao gerar");
        verify(ws).logarErro(any(Throwable.class));
    }
}
