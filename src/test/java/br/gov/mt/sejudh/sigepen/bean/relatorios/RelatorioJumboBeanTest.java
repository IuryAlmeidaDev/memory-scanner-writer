package br.gov.mt.sejudh.sigepen.bean.relatorios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.bean.FileDownloadHandlerBean;
import br.gov.mt.sejudh.sigepen.enumerator.UnidadeJumbo;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioJumboDTO;
import br.gov.mt.sejudh.sigepen.service.RelatorioJumboService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

public class RelatorioJumboBeanTest extends BaseUnitTest {

    @Mock
    private RelatorioJumboService service;

    @Mock
    private UnidadePrisionalService unidadeService;

    @Mock
    private FacesUtil facesUtil;

    @Mock
    private FileDownloadHandlerBean fileDownload;

    @Mock
    private Logger log;

    @Spy
    @InjectMocks
    private RelatorioJumboBean bean;

    @Before
    public void isolarDependenciasEstaticas() throws Exception {
        doReturn(new Arquivo()).when(bean).gerarPdf(any());
        doReturn(new Arquivo()).when(bean).gerarXlsx(any());
        doNothing().when(bean).disparaDownload();
        doNothing().when(bean).registrarSucessoDeImpressao(anyInt());
        doNothing().when(bean).registrarSucessoDeExportacaoXlsx(anyInt());
        doNothing().when(bean).registrarErroImpressao(any(Throwable.class));
        doNothing().when(bean).registrarErroExportacaoXlsx(any(Throwable.class));
    }

    @Test
    public void init_devePopularUnidadesEPreSelecionarApenasOsIdsQueVieramDoBanco() {
        List<UnidadePrisional> unidadesDoBanco = Arrays.asList(
                new UnidadePrisional(2L, "Unidade A"),
                new UnidadePrisional(3L, "Unidade B"));
        when(unidadeService.listarPorIds(UnidadeJumbo.todosIds())).thenReturn(unidadesDoBanco);

        bean.init();

        assertThat(bean.getUnidadesDisponiveis()).isSameAs(unidadesDoBanco);
        assertThat(bean.getFiltro().getUnidadesSelecionadas())
                .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    public void imprimir_deveExibirMensagemQuandoNenhumaUnidadeSelecionada() {
        bean.getFiltro().setUnidadesSelecionadas(Collections.emptyList());

        bean.imprimir();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_INFO), eq("AVISO"),
                eq("Selecione ao menos uma unidade."));
        verify(service, never()).buscar(any());
        verify(fileDownload, never()).prepararArquivo(any());
    }

    @Test
    public void imprimir_deveExibirMensagemQuandoServiceRetornaListaVazia() {
        bean.getFiltro().setUnidadesSelecionadas(Arrays.asList(2L, 7L));
        when(service.buscar(bean.getFiltro())).thenReturn(Collections.emptyList());

        bean.imprimir();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_INFO), eq("AVISO"),
                eq("Relatório não impresso, nenhum registro encontrado!"));
        verify(fileDownload, never()).prepararArquivo(any());
    }

    @Test
    public void imprimir_caminhoFelizDeveDelegarAoFileDownloadEAuditar() throws Exception {
        bean.getFiltro().setUnidadesSelecionadas(Arrays.asList(2L, 7L));
        List<RelatorioJumboDTO> lista = Arrays.asList(
                new RelatorioJumboDTO(1L, "Fulano", "Mae", "Unidade", "Cela 1"),
                new RelatorioJumboDTO(2L, "Beltrano", "Mae 2", "Unidade", "Cela 2"));
        when(service.buscar(bean.getFiltro())).thenReturn(lista);

        bean.imprimir();

        verify(bean).gerarPdf(lista);
        verify(fileDownload).prepararArquivo(any());
        verify(bean).disparaDownload();
        verify(bean).registrarSucessoDeImpressao(2);
    }

    @Test
    public void imprimir_deveTratarIllegalArgumentExceptionDoService() {
        bean.getFiltro().setUnidadesSelecionadas(Arrays.asList(2L, 999L));
        when(service.buscar(bean.getFiltro()))
                .thenThrow(new IllegalArgumentException("Unidade fora da lista de unidades com Jumbo."));

        bean.imprimir();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_INFO), eq("AVISO"),
                eq("Unidade fora da lista de unidades com Jumbo."));
        verify(fileDownload, never()).prepararArquivo(any());
    }

    @Test
    public void imprimir_deveTratarExcecaoGenericaDoService() {
        bean.getFiltro().setUnidadesSelecionadas(Arrays.asList(2L, 7L));
        when(service.buscar(bean.getFiltro()))
                .thenThrow(new RuntimeException("falha inesperada no DAO"));

        bean.imprimir();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_FATAL), eq("ERRO"),
                org.mockito.ArgumentMatchers.contains("Erro ao imprimir Relatório Jumbo"));
        verify(fileDownload, never()).prepararArquivo(any());
    }

    @Test
    public void init_deveLidarComBancoDevolvendoListaVazia() {
        when(unidadeService.listarPorIds(UnidadeJumbo.todosIds()))
                .thenReturn(java.util.Collections.<UnidadePrisional>emptyList());

        bean.init();

        assertThat(bean.getUnidadesDisponiveis()).isEmpty();
        assertThat(bean.getFiltro().getUnidadesSelecionadas()).isEmpty();
    }

    @Test
    public void exportarXlsx_deveExibirMensagemQuandoNenhumaUnidadeSelecionada() {
        bean.getFiltro().setUnidadesSelecionadas(Collections.emptyList());

        bean.exportarXlsx();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_INFO), eq("AVISO"),
                eq("Selecione ao menos uma unidade."));
        verify(service, never()).buscarRaw(any());
        verify(fileDownload, never()).prepararArquivoXlsx(any());
    }

    @Test
    public void exportarXlsx_deveExibirMensagemQuandoServiceRetornaListaVazia() {
        bean.getFiltro().setUnidadesSelecionadas(Arrays.asList(2L, 7L));
        when(service.buscarRaw(bean.getFiltro())).thenReturn(Collections.<RelatorioJumboDTO>emptyList());

        bean.exportarXlsx();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_INFO), eq("AVISO"),
                eq("Relatório não exportado, nenhum registro encontrado!"));
        verify(fileDownload, never()).prepararArquivoXlsx(any());
    }

    @Test
    public void exportarXlsx_caminhoFelizDeveDelegarAoFileDownloadEAuditar() throws Exception {
        bean.getFiltro().setUnidadesSelecionadas(Arrays.asList(2L, 7L));
        List<RelatorioJumboDTO> lista = Arrays.asList(
                new RelatorioJumboDTO(1L, "Fulano", "Mae", "Unidade", "Cela 1"),
                new RelatorioJumboDTO(2L, "Beltrano", "Mae 2", "Unidade", "Cela 2"));
        when(service.buscarRaw(bean.getFiltro())).thenReturn(lista);

        bean.exportarXlsx();

        verify(bean).gerarXlsx(lista);
        verify(fileDownload).prepararArquivoXlsx(any());
        verify(bean).disparaDownload();
        verify(bean).registrarSucessoDeExportacaoXlsx(2);
        verify(fileDownload, never()).prepararArquivo(any());
    }

    @Test
    public void exportarXlsx_deveTratarIllegalArgumentExceptionDoService() {
        bean.getFiltro().setUnidadesSelecionadas(Arrays.asList(2L, 999L));
        when(service.buscarRaw(bean.getFiltro()))
                .thenThrow(new IllegalArgumentException("Unidade fora da lista de unidades com Jumbo."));

        bean.exportarXlsx();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_INFO), eq("AVISO"),
                eq("Unidade fora da lista de unidades com Jumbo."));
        verify(fileDownload, never()).prepararArquivoXlsx(any());
    }

    @Test
    public void exportarXlsx_deveTratarExcecaoGenericaDoService() {
        bean.getFiltro().setUnidadesSelecionadas(Arrays.asList(2L, 7L));
        when(service.buscarRaw(bean.getFiltro()))
                .thenThrow(new RuntimeException("falha no builder xlsx"));

        bean.exportarXlsx();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_FATAL), eq("ERRO"),
                org.mockito.ArgumentMatchers.contains("Erro ao exportar Relatório Jumbo"));
        verify(fileDownload, never()).prepararArquivoXlsx(any());
        verify(bean).registrarErroExportacaoXlsx(any(Throwable.class));
    }
}
