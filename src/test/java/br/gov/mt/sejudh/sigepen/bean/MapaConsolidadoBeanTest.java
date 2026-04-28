package br.gov.mt.sejudh.sigepen.bean;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.Mes;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.MapaConsolidadoDTO;
import br.gov.mt.sejudh.sigepen.service.ApostilamentoService;
import br.gov.mt.sejudh.sigepen.service.ContratoService;
import br.gov.mt.sejudh.sigepen.service.GerenciarAssinaturaService;
import br.gov.mt.sejudh.sigepen.service.MapaConsolidadoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.ArgumentCaptor;

import javax.faces.application.FacesMessage;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MapaConsolidadoBeanTest extends BaseUnitTest {

    @Mock
    private ContratoService contratoService;

    @Mock
    private ApostilamentoService apostilamentoService;

    @Mock
    private UnidadePrisionalService unidadeService;

    @Mock
    private MapaConsolidadoService mapaConsolidadoService;

    @Mock
    private FacesUtil facesUtil;

    @Mock
    private Logger log;

    @Mock
    private FileDownloadHandlerBean fileDownload;

    @Mock
    private GerenciarAssinaturaService gerenciarAssinaturaService;

    @Spy
    @InjectMocks
    private MapaConsolidadoBean bean;

    @Before
    public void configurarBean() throws Exception {
        doReturn(AcaoDeTela.LISTAR).when(bean).getAcaoDeTela();
        doNothing().when(bean).executarDownload();
        doReturn(new Arquivo()).when(bean).gerarArquivoRelatorio(any(), any());
        doNothing().when(bean).registrarAcessoFornecedor();
        doNothing().when(bean).registrarErro(any(Exception.class));
        doNothing().when(bean).registrarSucessoGeracao();
        doNothing().when(bean).registrarErroGeracao(any(Exception.class));
    }

    @Test
    public void init_devePopularListaDeUnidadesMesmoQuandoVierVazia() {
        when(unidadeService.listarUnidadesComContratosAtivo()).thenReturn(Collections.<UnidadePrisional>emptyList());

        bean.init();

        assertThat(bean.getUnidadesPrisionais()).isEmpty();
    }

    @Test
    public void init_deveTratarExcecaoNoFluxoInterno() throws Exception {
        when(unidadeService.listarUnidadesComContratosAtivo()).thenReturn(Collections.<UnidadePrisional>emptyList());
        doThrow(new RuntimeException("falha no init")).when(bean).getAcaoDeTela();

        bean.init();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_FATAL), eq("ERRO"), eq("falha no init"));
    }

    @Test
    public void validarAno_deveRetornarFalsoQuandoAnoForNuloOuInvalido() throws Exception {
        bean.setAno(null);
        assertThat(invokePrivate("validarAno")).isEqualTo(false);

        bean.setAno("abcd");
        assertThat(invokePrivate("validarAno")).isEqualTo(false);

        bean.setAno("1900");
        assertThat(invokePrivate("validarAno")).isEqualTo(false);
    }

    @Test
    public void validarAno_deveRetornarVerdadeiroQuandoAnoForValido() throws Exception {
        bean.setAno("2026");

        assertThat(invokePrivate("validarAno")).isEqualTo(true);
    }

    @Test
    public void validarContratoVigente_deveRetornarFalsoQuandoListaVierNulaOuVazia() throws Exception {
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026")).thenReturn(null);

        assertThat(invokePrivate("validarContratoVigente", new Class<?>[]{long.class}, 10L)).isEqualTo(false);

        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026")).thenReturn(Collections.<Contrato>emptyList());

        assertThat(invokePrivate("validarContratoVigente", new Class<?>[]{long.class}, 10L)).isEqualTo(false);
    }

    @Test
    public void validarContratoVigente_deveTratarParseException() throws Exception {
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026")).thenThrow(new ParseException("erro", 0));

        assertThat(invokePrivate("validarContratoVigente", new Class<?>[]{long.class}, 10L)).isEqualTo(false);
    }

    @Test
    public void validarContratoVigente_deveManterSomenteContratosDaUnidade() throws Exception {
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        Contrato valido = contrato(1L, unidade);
        Contrato invalido = contrato(2L, new UnidadePrisional(99L, "Outra"));

        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026")).thenReturn(Arrays.asList(valido, invalido));

        assertThat(invokePrivate("validarContratoVigente", new Class<?>[]{long.class}, 10L)).isEqualTo(true);
        assertThat(getContratos()).containsExactly(valido);
    }

    @Test
    public void validarContratoVigente_deveIgnorarContratosNulosOuSemListaDeUnidades() throws Exception {
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        Contrato valido = contrato(1L, unidade);
        Contrato semUnidades = new Contrato();
        semUnidades.setId(2L);

        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026"))
                .thenReturn(Arrays.asList(null, semUnidades, contrato(3L, (UnidadePrisional) null), valido));

        assertThat(invokePrivate("validarContratoVigente", new Class<?>[]{long.class}, 10L)).isEqualTo(true);
        assertThat(getContratos()).containsExactly(valido);
    }

    @Test
    public void calcularValorAuditoria_deveRetornarZeroQuandoParametrosForemNulosOuQuantidadeZero() throws Exception {
        assertThat(invokePrivate("calcularValorAuditoria",
                new Class<?>[]{Integer.class, Double.class, Integer.class}, null, 10D, 2)).isEqualTo(0D);
        assertThat(invokePrivate("calcularValorAuditoria",
                new Class<?>[]{Integer.class, Double.class, Integer.class}, 2, null, 2)).isEqualTo(0D);
        assertThat(invokePrivate("calcularValorAuditoria",
                new Class<?>[]{Integer.class, Double.class, Integer.class}, 2, 10D, 0)).isEqualTo(0D);
    }

    @Test
    public void gerarMapa_devePararNaValidacaoQuandoUnidadeOuMesForemNulos() {
        bean.setUnidade(null);
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        bean.gerarMapa();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_FATAL), eq("ERRO"), eq("Informe a unidade e o mÃªs de referÃªncia!"));
        verify(mapaConsolidadoService, never()).obterDadosAuditoria(anyLong(), any(), any());
    }

    @Test
    public void gerarMapa_devePararQuandoNaoHouverContratoVigente() throws Exception {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        bean.setUnidade(unidade);
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        when(apostilamentoService.existeApostilamentoUnidade(10L)).thenReturn(false);
        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026")).thenReturn(Collections.<Contrato>emptyList());

        bean.gerarMapa();

        verify(mapaConsolidadoService, never()).obterDadosAuditoria(anyLong(), any(), any());
        verify(fileDownload, never()).prepararArquivo(any());
    }

    @Test
    public void gerarMapa_devePararQuandoExistirApostilamentoNaUnidade() throws Exception {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        bean.setUnidade(unidade);
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        when(apostilamentoService.existeApostilamentoUnidade(10L)).thenReturn(true);

        bean.gerarMapa();

        verify(contratoService, never()).buscarContratoVigente(anyLong(), any(), any());
        verify(mapaConsolidadoService, never()).obterDadosAuditoria(anyLong(), any(), any());
        verify(bean, never()).gerarArquivoRelatorio(any(), any());
    }

    @Test
    public void gerarMapa_caminhoFelizDevePrepararArquivoEExecutarDownload() throws Exception {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        bean.setUnidade(unidade);
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        Contrato contrato = contrato(1L, unidade);
        MapaConsolidadoDTO dto = dto(2, 10D, 1, 6D, 1, 7D, 0, 0D, 0, 0D, 0, 0D);
        Map<String, Integer> audit = auditoria(1, 1, 1, 0, 0, 0);

        when(apostilamentoService.existeApostilamentoUnidade(10L)).thenReturn(false);
        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026")).thenReturn(Collections.singletonList(contrato));
        when(mapaConsolidadoService.obterDadosAuditoria(10L, "01/04/2026", "30/04/2026")).thenReturn(audit);
        when(mapaConsolidadoService.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato)).thenReturn(dto);

        bean.gerarMapa();

        verify(mapaConsolidadoService).obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato);
        verify(bean).gerarArquivoRelatorio(any(), any());
        verify(fileDownload).prepararArquivo(any());
        verify(bean).executarDownload();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gerarMapa_deveSomarAuditoriaDeMultiplosContratosDaUnidade() throws Exception {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        bean.setUnidade(unidade);
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        Contrato contratoUm = contrato(1L, unidade);
        Contrato contratoDois = contrato(2L, unidade);
        Contrato contratoOutraUnidade = contrato(3L, new UnidadePrisional(99L, "Outra"));

        when(apostilamentoService.existeApostilamentoUnidade(10L)).thenReturn(false);
        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026"))
                .thenReturn(Arrays.asList(contratoUm, contratoDois, contratoOutraUnidade));
        when(mapaConsolidadoService.obterDadosAuditoria(10L, "01/04/2026", "30/04/2026"))
                .thenReturn(auditoria(1, 1, 1, 1, 0, 0));
        when(mapaConsolidadoService.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contratoUm))
                .thenReturn(dto(2, 10D, 1, 6D, 1, 7D, 1, 4D, 0, 0D, 0, 0D));
        when(mapaConsolidadoService.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contratoDois))
                .thenReturn(dto(1, 8D, 2, 10D, 1, 9D, 0, 0D, 0, 0D, 0, 0D));

        bean.gerarMapa();

        ArgumentCaptor<List> listaCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Map> parametrosCaptor = ArgumentCaptor.forClass(Map.class);
        verify(bean).gerarArquivoRelatorio(listaCaptor.capture(), parametrosCaptor.capture());

        List<MapaConsolidadoDTO> relatorio = listaCaptor.getValue();
        Map<String, Object> parametros = parametrosCaptor.getValue();

        assertThat(relatorio).hasSize(2);
        assertThat(parametros.get("TOTAL_VALOR_CAFE_AUDIT")).isEqualTo(13D);
        assertThat(parametros.get("TOTAL_VALOR_ALMOCO_AUDIT")).isEqualTo(11D);
        assertThat(parametros.get("TOTAL_VALOR_JANTA_AUDIT")).isEqualTo(16D);
        assertThat(parametros.get("TOTAL_VALOR_CAFE_ESP_AUDIT")).isEqualTo(4D);
        assertThat(parametros.get("TOTAL_VALOR_ALMOCO_ESP_AUDIT")).isEqualTo(0D);
        assertThat(parametros.get("TOTAL_VALOR_JANTA_ESP_AUDIT")).isEqualTo(0D);
        assertThat(parametros.get("TOTAL_GERAL_AUDIT")).isEqualTo(44D);
    }

    @Test
    public void gerarMapa_deveContinuarQuandoAuditoriaOuDtoVieremNulos() throws Exception {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        bean.setUnidade(unidade);
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        Contrato contrato = contrato(1L, unidade);

        when(apostilamentoService.existeApostilamentoUnidade(10L)).thenReturn(false);
        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026")).thenReturn(Collections.singletonList(contrato));
        when(mapaConsolidadoService.obterDadosAuditoria(10L, "01/04/2026", "30/04/2026")).thenReturn(null);
        when(mapaConsolidadoService.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato)).thenReturn(null);

        bean.gerarMapa();

        verify(bean).gerarArquivoRelatorio(any(), any());
        verify(fileDownload).prepararArquivo(any());
        verify(bean).executarDownload();
    }

    @Test
    public void gerarMapa_deveTratarExcecaoAoPrepararArquivo() throws Exception {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        bean.setUnidade(unidade);
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        Contrato contrato = contrato(1L, unidade);
        MapaConsolidadoDTO dto = dto(1, 5D, 1, 6D, 1, 7D, 0, 0D, 0, 0D, 0, 0D);

        when(apostilamentoService.existeApostilamentoUnidade(10L)).thenReturn(false);
        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026")).thenReturn(Collections.singletonList(contrato));
        when(mapaConsolidadoService.obterDadosAuditoria(10L, "01/04/2026", "30/04/2026")).thenReturn(auditoria(1, 1, 1, 0, 0, 0));
        when(mapaConsolidadoService.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato)).thenReturn(dto);
        doThrow(new RuntimeException("falha download")).when(fileDownload).prepararArquivo(any());

        bean.gerarMapa();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_FATAL), eq("ERRO"), eq("falha download"));
        verify(bean, never()).executarDownload();
    }

    @Test
    public void gerarMapa_deveTratarExcecaoAoGerarRelatorio() throws Exception {
        UnidadePrisional unidade = new UnidadePrisional(10L, "Unidade A");
        bean.setUnidade(unidade);
        bean.setMes(Mes.ABRIL);
        bean.setAno("2026");

        Contrato contrato = contrato(1L, unidade);
        MapaConsolidadoDTO dto = dto(1, 5D, 1, 6D, 1, 7D, 0, 0D, 0, 0D, 0, 0D);

        when(apostilamentoService.existeApostilamentoUnidade(10L)).thenReturn(false);
        when(contratoService.buscarContratoVigente(10L, "01/04/2026", "30/04/2026")).thenReturn(Collections.singletonList(contrato));
        when(mapaConsolidadoService.obterDadosAuditoria(10L, "01/04/2026", "30/04/2026")).thenReturn(auditoria(1, 1, 1, 0, 0, 0));
        when(mapaConsolidadoService.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato)).thenReturn(dto);
        doThrow(new RuntimeException("falha relatorio")).when(bean).gerarArquivoRelatorio(any(), any());

        bean.gerarMapa();

        verify(facesUtil).exibirMensagem(eq(FacesMessage.SEVERITY_FATAL), eq("ERRO"), eq("falha relatorio"));
        verify(fileDownload, never()).prepararArquivo(any());
        verify(bean, never()).executarDownload();
    }

    private Contrato contrato(Long id, UnidadePrisional... unidades) {
        Contrato contrato = new Contrato();
        contrato.setId(id);
        contrato.setUnidades(Arrays.asList(unidades));
        return contrato;
    }

    private MapaConsolidadoDTO dto(int totalCafe, double valorCafe, int totalAlmoco, double valorAlmoco, int totalJanta, double valorJanta,
                                   int totalCafeEsp, double valorCafeEsp, int totalAlmocoEsp, double valorAlmocoEsp, int totalJantaEsp, double valorJantaEsp) {
        MapaConsolidadoDTO dto = new MapaConsolidadoDTO();
        dto.setTotalCafe(totalCafe);
        dto.setTotalValorCafe(valorCafe);
        dto.setTotalAlmoco(totalAlmoco);
        dto.setTotalValorAlmoco(valorAlmoco);
        dto.setTotalJanta(totalJanta);
        dto.setTotalValorJanta(valorJanta);
        dto.setTotalCafeEspecial(totalCafeEsp);
        dto.setTotalValorCafeEspecial(valorCafeEsp);
        dto.setTotalAlmocoEspecial(totalAlmocoEsp);
        dto.setTotalValorAlmocoEspecial(valorAlmocoEsp);
        dto.setTotalJantaEspecial(totalJantaEsp);
        dto.setTotalValorJantaEspecial(valorJantaEsp);
        return dto;
    }

    private Map<String, Integer> auditoria(int cafe, int almoco, int janta, int cafeEsp, int almocoEsp, int jantaEsp) {
        Map<String, Integer> audit = new HashMap<String, Integer>();
        audit.put("AUDIT_CAFE", cafe);
        audit.put("AUDIT_ALMOCO", almoco);
        audit.put("AUDIT_JANTA", janta);
        audit.put("AUDIT_CAFE_ESP", cafeEsp);
        audit.put("AUDIT_ALMOCO_ESP", almocoEsp);
        audit.put("AUDIT_JANTA_ESP", jantaEsp);
        return audit;
    }

    @SuppressWarnings("unchecked")
    private List<Contrato> getContratos() throws Exception {
        java.lang.reflect.Field field = MapaConsolidadoBean.class.getDeclaredField("contratos");
        field.setAccessible(true);
        return (List<Contrato>) field.get(bean);
    }

    private Object invokePrivate(String nomeMetodo) throws Exception {
        Method method = MapaConsolidadoBean.class.getDeclaredMethod(nomeMetodo);
        method.setAccessible(true);
        return method.invoke(bean);
    }

    private Object invokePrivate(String nomeMetodo, Class<?>[] tipos, Object... argumentos) throws Exception {
        Method method = MapaConsolidadoBean.class.getDeclaredMethod(nomeMetodo, tipos);
        method.setAccessible(true);
        return method.invoke(bean, argumentos);
    }
}
