package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.MapaConsolidadoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Mes;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.MapaConsolidadoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioMapaAlimentacaoConsultaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioMapaAlimentacaoRecuperandoDTO;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class MapaConsolidadoServiceTest extends BaseUnitTest {

    private static final String CAFE = "CAF\u00c9 DA MANH\u00c3";
    private static final String ALMOCO = "ALMO\u00c7O";
    private static final String JANTA = "JANTA";

    @Mock
    private MapaConsolidadoDAO dao;

    @Mock
    private ContratoService contratoService;

    @InjectMocks
    private MapaConsolidadoService service;

    @Test
    public void deveMapearDadosAuditoriaPreenchendoZerosQuandoChavesNaoExistirem() {
        Map<String, Object> dadosBrutos = new HashMap<String, Object>();
        dadosBrutos.put("AUDIT_CAFE", 4);
        dadosBrutos.put("AUDIT_JANTA_ESPECIAL", 2);

        when(dao.buscarTotaisPedidosAuditados(10L, "01/04/2026", "30/04/2026")).thenReturn(dadosBrutos);

        Map<String, Integer> resultado = service.obterDadosAuditoria(10L, "01/04/2026", "30/04/2026");

        assertThat(resultado)
                .containsEntry("AUDIT_CAFE", 4)
                .containsEntry("AUDIT_ALMOCO", 0)
                .containsEntry("AUDIT_JANTA", 0)
                .containsEntry("AUDIT_CAFE_ESP", 0)
                .containsEntry("AUDIT_ALMOCO_ESP", 0)
                .containsEntry("AUDIT_JANTA_ESP", 2);
    }

    @Test
    public void deveRetornarZerosQuandoDaoDeAuditoriaVierNulo() {
        when(dao.buscarTotaisPedidosAuditados(10L, "01/04/2026", "30/04/2026")).thenReturn(null);

        Map<String, Integer> resultado = service.obterDadosAuditoria(10L, "01/04/2026", "30/04/2026");

        assertThat(resultado)
                .containsEntry("AUDIT_CAFE", 0)
                .containsEntry("AUDIT_ALMOCO", 0)
                .containsEntry("AUDIT_JANTA", 0)
                .containsEntry("AUDIT_CAFE_ESP", 0)
                .containsEntry("AUDIT_ALMOCO_ESP", 0)
                .containsEntry("AUDIT_JANTA_ESP", 0);
    }

    @Test
    public void deveRetornarListaVaziaQuandoDaoDeConsultaVierNulo() {
        when(dao.buscarRelatorioMapaAlimentacaoConsulta(10L, 99L)).thenReturn(null);

        List<RelatorioMapaAlimentacaoConsultaDTO> resultado = service.obterMapaAlimentacaoConsulta(10L, 99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    public void deveRetornarListaVaziaQuandoDaoDeRecuperandosVierNulo() {
        when(dao.buscaRelatorioMapaAlimentacaoRecuperando(20L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30)))
                .thenReturn(null);

        List<RelatorioMapaAlimentacaoRecuperandoDTO> resultado = service.obterMapaAlimentacaoRecuperando(
                20L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));

        assertThat(resultado).isEmpty();
    }

    @Test
    public void deveCalcularTotaisEspeciaisQuandoRecuperandoPossuirDietaEspecial() throws Exception {
        UnidadePrisional unidade = unidade(10L, 20L, "Unidade A");
        Contrato contrato = contrato(99L, "2026-04-01 00:00", "2026-04-30 23:59");
        List<RelatorioMapaAlimentacaoConsultaDTO> consultas = refeicoes(2D, 5D, 9D, 3D, 6D, 10D);
        List<RelatorioMapaAlimentacaoRecuperandoDTO> recuperandos = Collections.singletonList(
                recuperando("2026-04-10 08:00", "2026-04-10 18:00", "2026-04-10 08:00", "2026-04-10 18:00"));

        when(contratoService.buscarHorarioRefeiracao(10L)).thenReturn(horariosCompletos());
        when(dao.buscaRelatorioMapaAlimentacaoRecuperando(eq(20L), any(LocalDate.class), any(LocalDate.class))).thenReturn(recuperandos);
        when(dao.buscarRelatorioMapaAlimentacaoConsulta(10L, 99L)).thenReturn(consultas);

        MapaConsolidadoDTO resultado = service.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato);

        assertThat(resultado.getTotalCafe()).isEqualTo(0);
        assertThat(resultado.getTotalAlmoco()).isEqualTo(0);
        assertThat(resultado.getTotalJanta()).isEqualTo(0);
        assertThat(resultado.getTotalCafeEspecial()).isEqualTo(1);
        assertThat(resultado.getTotalAlmocoEspecial()).isEqualTo(1);
        assertThat(resultado.getTotalJantaEspecial()).isEqualTo(1);
        assertThat(resultado.getTotalValorCafeEspecial()).isEqualTo(3D);
        assertThat(resultado.getTotalValorAlmocoEspecial()).isEqualTo(6D);
        assertThat(resultado.getTotalValorJantaEspecial()).isEqualTo(10D);
    }

    @Test
    public void deveContabilizarDietaEspecialNoDiaSeguinteQuandoInicioForAposHorarioDaRefeicao() throws Exception {
        UnidadePrisional unidade = unidade(10L, 20L, "Unidade A");
        Contrato contrato = contrato(99L, "2026-04-01 00:00", "2026-04-30 23:59");
        List<RelatorioMapaAlimentacaoConsultaDTO> consultas = refeicoes(2D, 5D, 9D, 3D, 6D, 10D);
        List<RelatorioMapaAlimentacaoRecuperandoDTO> recuperandos = Collections.singletonList(
                recuperando("2026-04-05 14:00", null, "2026-04-05 14:00", null));

        when(contratoService.buscarHorarioRefeiracao(10L)).thenReturn(horariosCompletos());
        when(dao.buscaRelatorioMapaAlimentacaoRecuperando(eq(20L), any(LocalDate.class), any(LocalDate.class))).thenReturn(recuperandos);
        when(dao.buscarRelatorioMapaAlimentacaoConsulta(10L, 99L)).thenReturn(consultas);

        MapaConsolidadoDTO resultado = service.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato);

        assertThat(resultado.getConsolidadoRecuperandoDTOs().get(0).getSaidaRecuperando()).isEmpty();
        assertThat(resultado.getTotalCafe()).isEqualTo(0);
        assertThat(resultado.getTotalAlmoco()).isEqualTo(0);
        assertThat(resultado.getTotalJanta()).isEqualTo(0);
        assertThat(resultado.getTotalCafeEspecial()).isEqualTo(25);
        assertThat(resultado.getTotalAlmocoEspecial()).isEqualTo(25);
        assertThat(resultado.getTotalJantaEspecial()).isEqualTo(26);
    }

    @Test
    public void deveLimparHorariosEntreExecucoesQuandoNovaConsultaNaoTrouxerHorarios() throws Exception {
        UnidadePrisional unidade = unidade(10L, 20L, "Unidade A");
        Contrato contrato = contrato(99L, "2026-04-01 00:00", "2026-04-30 23:59");
        List<RelatorioMapaAlimentacaoConsultaDTO> consultas = refeicoes(2D, 5D, 9D, 3D, 6D, 10D);
        List<RelatorioMapaAlimentacaoRecuperandoDTO> recuperandos = Collections.singletonList(
                recuperando("2026-04-10 08:00", "2026-04-10 18:00", null, null));

        when(contratoService.buscarHorarioRefeiracao(10L)).thenReturn(horariosCompletos(), null);
        when(dao.buscaRelatorioMapaAlimentacaoRecuperando(eq(20L), any(LocalDate.class), any(LocalDate.class))).thenReturn(recuperandos);
        when(dao.buscarRelatorioMapaAlimentacaoConsulta(10L, 99L)).thenReturn(consultas);

        MapaConsolidadoDTO primeiraExecucao = service.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato);
        MapaConsolidadoDTO segundaExecucao = service.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato);

        assertThat(primeiraExecucao.getTotalCafe()).isEqualTo(1);
        assertThat(primeiraExecucao.getTotalAlmoco()).isEqualTo(1);
        assertThat(primeiraExecucao.getTotalJanta()).isEqualTo(1);
        assertThat(primeiraExecucao.getTotalValorCafe()).isEqualTo(2D);
        assertThat(primeiraExecucao.getTotalValorAlmoco()).isEqualTo(5D);
        assertThat(primeiraExecucao.getTotalValorJanta()).isEqualTo(9D);

        assertThat(segundaExecucao.getTotalCafe()).isEqualTo(0);
        assertThat(segundaExecucao.getTotalAlmoco()).isEqualTo(0);
        assertThat(segundaExecucao.getTotalJanta()).isEqualTo(0);
        assertThat(segundaExecucao.getTotalValorCafe()).isEqualTo(0D);
        assertThat(segundaExecucao.getTotalValorAlmoco()).isEqualTo(0D);
        assertThat(segundaExecucao.getTotalValorJanta()).isEqualTo(0D);
    }

    @Test
    public void deveRetornarMapaComTotaisZeradosQuandoDaosDoConsolidadoVieremNulos() throws Exception {
        UnidadePrisional unidade = unidade(10L, 20L, "Unidade A");
        Contrato contrato = contrato(99L, "2026-04-01 00:00", "2026-04-30 23:59");

        when(contratoService.buscarHorarioRefeiracao(10L)).thenReturn(null);
        when(dao.buscaRelatorioMapaAlimentacaoRecuperando(eq(20L), any(LocalDate.class), any(LocalDate.class))).thenReturn(null);
        when(dao.buscarRelatorioMapaAlimentacaoConsulta(10L, 99L)).thenReturn(null);

        MapaConsolidadoDTO resultado = service.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato);

        assertThat(resultado.getNomeUnidade()).isEqualTo("UNIDADE A");
        assertThat(resultado.getConsolidadoRecuperandoDTOs()).isEmpty();
        assertThat(resultado.getTotalCafe()).isEqualTo(0);
        assertThat(resultado.getTotalAlmoco()).isEqualTo(0);
        assertThat(resultado.getTotalJanta()).isEqualTo(0);
        assertThat(resultado.getTotalCafeEspecial()).isEqualTo(0);
        assertThat(resultado.getTotalAlmocoEspecial()).isEqualTo(0);
        assertThat(resultado.getTotalJantaEspecial()).isEqualTo(0);
    }

    @Test
    public void deveIgnorarItensNulosNasListasDeConsultaEHorarios() throws Exception {
        UnidadePrisional unidade = unidade(10L, 20L, "Unidade A");
        Contrato contrato = contrato(99L, "2026-04-01 00:00", "2026-04-30 23:59");

        List<RelatorioMapaAlimentacaoConsultaDTO> consultas = Arrays.asList(
                null,
                refeicao(CAFE, 2D, 3D),
                refeicao(null, 5D, 6D),
                refeicao(JANTA, 9D, 10D)
        );
        List<RelatorioMapaAlimentacaoRecuperandoDTO> recuperandos = Collections.singletonList(
                recuperando("2026-04-10 08:00", "2026-04-10 18:00", null, null));
        List<HorarioRefeicao> horarios = Arrays.asList(
                null,
                horarioInvalido(1L, null, "0900"),
                horario(1L, "0700", "0900"),
                horario(3L, "1700", "1900")
        );

        when(contratoService.buscarHorarioRefeiracao(10L)).thenReturn(horarios);
        when(dao.buscaRelatorioMapaAlimentacaoRecuperando(eq(20L), any(LocalDate.class), any(LocalDate.class))).thenReturn(recuperandos);
        when(dao.buscarRelatorioMapaAlimentacaoConsulta(10L, 99L)).thenReturn(consultas);

        MapaConsolidadoDTO resultado = service.obterMapaConsolidado(unidade, Mes.ABRIL, "2026", contrato);

        assertThat(resultado.getTotalCafe()).isEqualTo(1);
        assertThat(resultado.getTotalAlmoco()).isEqualTo(0);
        assertThat(resultado.getTotalJanta()).isEqualTo(1);
        assertThat(resultado.getTotalValorCafe()).isEqualTo(2D);
        assertThat(resultado.getTotalValorAlmoco()).isEqualTo(0D);
        assertThat(resultado.getTotalValorJanta()).isEqualTo(9D);
    }

    @Test
    public void deveRetornarFalsoQuandoValidacaoDeHorarioReceberNulos() throws Exception {
        Method metodo = MapaConsolidadoService.class.getDeclaredMethod(
                "validaHorarioPedidoPorRefeicao", Long.class, Long.class, Long.class, Long.class);
        metodo.setAccessible(true);

        Boolean resultado = (Boolean) metodo.invoke(service, null, 1800L, 700L, 900L);

        assertThat(resultado).isFalse();
    }

    @Test
    public void deveRetornarHoraMaximaQuandoObterHorarioReceberDataNula() throws Exception {
        setField("vigenciaInicio", LocalDate.of(2026, 4, 1));
        setField("vigenciaFim", LocalDate.of(2026, 5, 1));

        Method metodo = MapaConsolidadoService.class.getDeclaredMethod("obterHoraio", Date.class, int.class, int.class);
        metodo.setAccessible(true);

        Long resultado = (Long) metodo.invoke(service, null, 4, 2026);

        assertThat(resultado).isEqualTo(2359L);
    }

    @Test
    public void deveRetornarZeroDiasQuandoDatasForemNulas() throws Exception {
        setField("vigenciaInicio", LocalDate.of(2026, 4, 1));
        setField("vigenciaFim", LocalDate.of(2026, 5, 1));

        Method metodo = MapaConsolidadoService.class.getDeclaredMethod("obterDias", Date.class, Date.class);
        metodo.setAccessible(true);

        Integer resultado = (Integer) metodo.invoke(service, null, null);

        assertThat(resultado).isEqualTo(0);
    }

    @Test
    public void deveCalcularDiasQuandoDataFinalForNula() throws Exception {
        setField("vigenciaInicio", LocalDate.of(2026, 4, 1));
        setField("vigenciaFim", LocalDate.of(2026, 5, 1));

        Method metodo = MapaConsolidadoService.class.getDeclaredMethod("obterDias", Date.class, Date.class);
        metodo.setAccessible(true);

        Integer resultado = (Integer) metodo.invoke(service, dataHora("2026-04-10 08:00"), null);

        assertThat(resultado).isEqualTo(21);
    }

    @Test
    public void deveRetornarUmDiaEspecialQuandoEspecialEstiverAtivoEQuantidadeForZero() throws Exception {
        Method metodo = MapaConsolidadoService.class.getDeclaredMethod("validarQtdDiasPeloHorario", int.class, boolean.class, boolean.class);
        metodo.setAccessible(true);

        Integer resultado = (Integer) metodo.invoke(service, 0, false, true);

        assertThat(resultado).isEqualTo(1);
    }

    private UnidadePrisional unidade(Long id, Long estruturaId, String nome) {
        UnidadePrisional unidade = new UnidadePrisional();
        unidade.setId(id);
        unidade.setNome(nome);

        Estrutura estrutura = new Estrutura();
        estrutura.setId(estruturaId);
        unidade.setEstrutura(estrutura);

        return unidade;
    }

    private Contrato contrato(Long id, String inicio, String fim) throws Exception {
        Contrato contrato = new Contrato();
        contrato.setId(id);
        contrato.setVigenciaContratoInicio(dataHora(inicio));
        contrato.setVigenciaContratoFim(dataHora(fim));
        return contrato;
    }

    private RelatorioMapaAlimentacaoRecuperandoDTO recuperando(String entrada, String saida, String inicioEspecial, String fimEspecial) throws Exception {
        RelatorioMapaAlimentacaoRecuperandoDTO dto = new RelatorioMapaAlimentacaoRecuperandoDTO();
        dto.setNomeRecuperando("Recuperando");
        dto.setNomeSocialRecuperando("Social");
        dto.setMatriculaRecuperando("123");
        dto.setEntradaRecuperando(dataHora(entrada));
        dto.setSaidaRecuperando(dataHora(saida));
        dto.setInicioEspecial(dataHora(inicioEspecial));
        dto.setFimEspecial(dataHora(fimEspecial));
        return dto;
    }

    private List<RelatorioMapaAlimentacaoConsultaDTO> refeicoes(Double cafeNormal, Double almocoNormal, Double jantaNormal,
                                                                Double cafeEspecial, Double almocoEspecial, Double jantaEspecial) {
        return Arrays.asList(
                refeicao(CAFE, cafeNormal, cafeEspecial),
                refeicao(ALMOCO, almocoNormal, almocoEspecial),
                refeicao(JANTA, jantaNormal, jantaEspecial)
        );
    }

    private RelatorioMapaAlimentacaoConsultaDTO refeicao(String descricao, Double valorNormal, Double valorEspecial) {
        RelatorioMapaAlimentacaoConsultaDTO dto = new RelatorioMapaAlimentacaoConsultaDTO();
        dto.setNomeFornecedor("Fornecedor");
        dto.setNumeroContrato("CTR-1");
        dto.setRefeicao(descricao);
        dto.setValorNormal(valorNormal);
        dto.setValorEspecial(valorEspecial);
        return dto;
    }

    private List<HorarioRefeicao> horariosCompletos() {
        return Arrays.asList(
                horario(1L, "0700", "0900"),
                horario(2L, "1100", "1300"),
                horario(3L, "1700", "1900")
        );
    }

    private HorarioRefeicao horario(Long tipoRefeicaoId, String inicio, String fim) {
        HorarioRefeicao horario = new HorarioRefeicao();
        TipoRefeicao tipoRefeicao = new TipoRefeicao();
        tipoRefeicao.setId(tipoRefeicaoId);
        horario.setTipoRefeicao(tipoRefeicao);
        horario.setHorarioInicio(inicio);
        horario.setHorarioFim(fim);
        return horario;
    }

    private HorarioRefeicao horarioInvalido(Long tipoRefeicaoId, String inicio, String fim) {
        HorarioRefeicao horario = new HorarioRefeicao();
        TipoRefeicao tipoRefeicao = new TipoRefeicao();
        tipoRefeicao.setId(tipoRefeicaoId);
        horario.setTipoRefeicao(tipoRefeicao);
        horario.setHorarioInicio(inicio);
        horario.setHorarioFim(fim);
        return horario;
    }

    private Date dataHora(String valor) throws Exception {
        if (valor == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(valor);
    }

    private void setField(String nomeCampo, Object valor) throws Exception {
        Field field = MapaConsolidadoService.class.getDeclaredField(nomeCampo);
        field.setAccessible(true);
        field.set(service, valor);
    }
}
