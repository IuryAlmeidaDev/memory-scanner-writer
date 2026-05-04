package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoUnidade;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.SituacaoEstruturaUnidade;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.EstruturaDto;
import br.gov.mt.sejudh.sigepen.model.dto.GerarPedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoEstruturaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RefeicaoDetalhadaDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EstruturaDAOTest extends BaseUnitTest {

    private static final String DATA_HORA = "01/04/2026 12:00:00";

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @Mock
    private TypedQuery<Estrutura> estruturaTypedQuery;

    @Mock
    private TypedQuery<SituacaoEstruturaUnidade> situacaoTypedQuery;

    @Mock
    private TypedQuery<HorarioRefeicao> horarioTypedQuery;

    @InjectMocks
    private EstruturaDAO dao;

    @Before
    public void setUp() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());
    }

    @Test
    public void salvar_estruturaNova_persisteERetornaMesmaInstancia() {
        Estrutura estrutura = estrutura(0L, "Raiz");

        Estrutura resultado = dao.salvar(estrutura);

        assertThat(resultado).isSameAs(estrutura);
        verify(entityManager).persist(estrutura);
        verify(entityManager, never()).merge(any(Estrutura.class));
    }

    @Test
    public void salvar_estruturaExistente_mesclaERetornaInstanciaGerenciada() {
        Estrutura estrutura = estrutura(10L, "Raiz");
        Estrutura gerenciada = estrutura(10L, "Raiz gerenciada");
        when(entityManager.merge(estrutura)).thenReturn(gerenciada);

        Estrutura resultado = dao.salvar(estrutura);

        assertThat(resultado).isSameAs(gerenciada);
        verify(entityManager).merge(estrutura);
        verify(entityManager, never()).persist(any(Estrutura.class));
    }

    @Test
    public void excluir_estruturaExistente_removeInstanciaGerenciada() {
        Estrutura estrutura = estrutura(10L, "Raiz");
        Estrutura gerenciada = estrutura(10L, "Raiz gerenciada");
        when(entityManager.merge(estrutura)).thenReturn(gerenciada);

        dao.excluir(estrutura);

        verify(entityManager).merge(estrutura);
        verify(entityManager).remove(gerenciada);
    }

    @Test
    public void buscaPorId_idValido_consultaPorIdERetornaEstrutura() {
        Estrutura estrutura = estrutura(10L, "Raiz");
        when(entityManager.createQuery(anyString(), eq(Estrutura.class))).thenReturn(estruturaTypedQuery);
        when(estruturaTypedQuery.setParameter(anyString(), any())).thenReturn(estruturaTypedQuery);
        when(estruturaTypedQuery.getSingleResult()).thenReturn(estrutura);

        Estrutura resultado = dao.buscaPorId(10L);

        ArgumentCaptor<String> jpqlCaptor = ArgumentCaptor.forClass(String.class);
        assertThat(resultado).isSameAs(estrutura);
        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(Estrutura.class));
        verify(estruturaTypedQuery).setParameter("id", 10L);
        assertThat(jpqlCaptor.getValue()).contains("where e.id = :id");
    }

    @Test
    public void buscaEstruturaFilho_paiInformado_consultaFilhosOrdenadosPorId() {
        Estrutura pai = estrutura(1L, "Raiz");
        List<Estrutura> filhos = Collections.singletonList(estrutura(2L, "Cela"));
        when(entityManager.createQuery(anyString(), eq(Estrutura.class))).thenReturn(estruturaTypedQuery);
        when(estruturaTypedQuery.setParameter(anyString(), any())).thenReturn(estruturaTypedQuery);
        when(estruturaTypedQuery.getResultList()).thenReturn(filhos);

        List<Estrutura> resultado = dao.buscaEstruturaFilho(pai);

        ArgumentCaptor<String> jpqlCaptor = ArgumentCaptor.forClass(String.class);
        assertThat(resultado).isSameAs(filhos);
        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(Estrutura.class));
        verify(estruturaTypedQuery).setParameter("id", pai);
        assertThat(jpqlCaptor.getValue()).contains("where e.estruturaPai = :id ORDER BY e.id");
    }

    @Test
    public void listarPorUnidade_idInformado_filtraOrigemAlteracaoERetornaHistorico() {
        List<SituacaoEstruturaUnidade> historico = Collections.singletonList(new SituacaoEstruturaUnidade());
        when(entityManager.createQuery(anyString(), eq(SituacaoEstruturaUnidade.class))).thenReturn(situacaoTypedQuery);
        when(situacaoTypedQuery.setParameter(anyString(), any())).thenReturn(situacaoTypedQuery);
        when(situacaoTypedQuery.getResultList()).thenReturn(historico);

        List<SituacaoEstruturaUnidade> resultado = dao.listarPorUnidade(10L);

        ArgumentCaptor<String> jpqlCaptor = ArgumentCaptor.forClass(String.class);
        assertThat(resultado).isSameAs(historico);
        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(SituacaoEstruturaUnidade.class));
        verify(situacaoTypedQuery).setParameter("id", 10L);
        assertThat(jpqlCaptor.getValue()).contains("s.origemAlteracao = true");
    }

    @Test
    public void quantidadeRecuperandosLotados_estruturaInformada_retornaTotalDaConsulta() {
        Estrutura estrutura = estrutura(10L, "Cela");
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(7L);

        long resultado = dao.quantidadeRecuperandosLotados(estrutura);

        assertThat(resultado).isEqualTo(7L);
        verify(query).setParameter("estrutura", estrutura);
    }

    @Test
    public void buscarEstruturasUnidade_idInformado_criaNativeQueryComDtoERetornaResultado() {
        List<EstruturaDto> esperado = Collections.singletonList(new EstruturaDto());
        when(entityManager.createNativeQuery(anyString(), eq(EstruturaDto.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(esperado);

        List<EstruturaDto> resultado = dao.buscarEstruturasUnidade(10L);

        assertThat(resultado).isSameAs(esperado);
        verify(query).setParameter("idUnidade", 10L);
    }

    @Test
    public void buscarEstruturas_consultaRetornaLinhas_mapeiaIdENome() {
        when(query.getResultList()).thenReturn(Arrays.<Object[]>asList(
                new Object[]{BigDecimal.valueOf(1L), "Raiz"},
                new Object[]{BigDecimal.valueOf(2L), "Raiz > Cela"}));

        List<Estrutura> resultado = dao.buscarEstruturas(10L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getNome()).isEqualTo("RAIZ");
        assertThat(resultado.get(1).getId()).isEqualTo(2L);
        assertThat(resultado.get(1).getNome()).isEqualTo("RAIZ > CELA");
        verify(query).setParameter("idUnidade", 10L);
    }

    @Test
    public void buscarConfiguracaoHorarioRefeicao_semResultado_retornaNull() {
        when(entityManager.createQuery(anyString(), eq(HorarioRefeicao.class))).thenReturn(horarioTypedQuery);
        when(horarioTypedQuery.setParameter(anyString(), any())).thenReturn(horarioTypedQuery);
        when(horarioTypedQuery.getResultList()).thenReturn(Collections.<HorarioRefeicao>emptyList());

        HorarioRefeicao resultado = dao.buscarConfiguracaoHorarioRefeicao(10L, 1L);

        assertThat(resultado).isNull();
        verify(horarioTypedQuery).setParameter("unidadeId", 10L);
        verify(horarioTypedQuery).setParameter("tipoRefeicaoId", 1L);
    }

    @Test
    public void buscarConfiguracaoHorarioRefeicao_comResultado_retornaPrimeiroItem() {
        HorarioRefeicao primeiro = new HorarioRefeicao();
        HorarioRefeicao segundo = new HorarioRefeicao();
        when(entityManager.createQuery(anyString(), eq(HorarioRefeicao.class))).thenReturn(horarioTypedQuery);
        when(horarioTypedQuery.setParameter(anyString(), any())).thenReturn(horarioTypedQuery);
        when(horarioTypedQuery.getResultList()).thenReturn(Arrays.asList(primeiro, segundo));

        HorarioRefeicao resultado = dao.buscarConfiguracaoHorarioRefeicao(10L, 1L);

        assertThat(resultado).isSameAs(primeiro);
    }

    @Test
    public void buscarNomeEstrutura_idInformado_retornaNomeDaConsulta() {
        when(query.getSingleResult()).thenReturn("Raiz > Cela 1");

        String resultado = dao.buscarNomeEstrutura(10L);

        assertThat(resultado).isEqualTo("Raiz > Cela 1");
        verify(query).setParameter("idEstrutura", 10L);
    }

    @Test
    public void buscaEstruturaFilhoGerarPedido_linhaConsulta_mapeiaDto() {
        GerarPedidoEstruturaDTO estrutura = new GerarPedidoEstruturaDTO();
        estrutura.setId(10L);
        when(query.getResultList()).thenReturn(Collections.singletonList(new Object[]{
                BigDecimal.valueOf(20L),
                BigDecimal.valueOf(12),
                BigDecimal.ONE,
                "Cela 1",
                BigDecimal.ONE,
                BigDecimal.valueOf(3L),
                BigDecimal.valueOf(2L)}));

        List<GerarPedidoEstruturaDTO> resultado = dao.buscaEstruturaFilhoGerarPedido(estrutura, DATA_HORA, 1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(20L);
        assertThat(resultado.get(0).getNome()).isEqualTo("Cela 1");
        assertThat(resultado.get(0).getCela()).isTrue();
        assertThat(resultado.get(0).getDietaPadrao()).isEqualTo(3L);
        assertThat(resultado.get(0).getDietaEspecial()).isEqualTo(2L);
        assertThat(resultado.get(0).getSituacaoUnidade()).isEqualTo(SituacaoUnidade.ATIVO);
        verify(query).setParameter("id", 10L);
        verify(query).setParameter("data", DATA_HORA);
    }

    @Test
    public void buscaPedidoDetalhado_linhaConsulta_mapeiaDto() {
        when(query.getResultList()).thenReturn(Collections.singletonList(new Object[]{
                BigDecimal.valueOf(4L),
                "Arroz",
                BigDecimal.valueOf(100L),
                BigDecimal.valueOf(90L),
                "ALMOCO",
                "M",
                BigDecimal.ONE,
                BigDecimal.valueOf(99L)}));

        List<RefeicaoDetalhadaDTO> resultado = dao.buscaPedidoDetalhado(DATA_HORA, 10L, 1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRecuperandoTotal()).isEqualTo(4L);
        assertThat(resultado.get(0).getItem()).isEqualTo("Arroz");
        assertThat(resultado.get(0).getRefeicao()).isEqualTo("ALMOCO");
        assertThat(resultado.get(0).getItemCardapioId()).isEqualTo(99L);
        verify(query).setParameter("id", 10L);
        verify(query).setParameter("data", DATA_HORA);
        verify(query).setParameter("tipoRefeicao", 1L);
    }

    @Test
    public void buscarDietasEspeciaisPorEstrutura_linhaComNomes_mapeiaDtoENomes() {
        when(query.getResultList()).thenReturn(Collections.singletonList(new Object[]{
                BigDecimal.valueOf(2L),
                BigDecimal.valueOf(10L),
                "Ala A",
                "Hipossodica",
                BigDecimal.valueOf(2L),
                "Ana,Bia"}));

        List<DietaEspecialEstruturaDTO> resultado = dao.buscarDietasEspeciaisPorEstrutura(10L, DATA_HORA);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstruturaId()).isEqualTo(10L);
        assertThat(resultado.get(0).getNomeEstrutura()).isEqualTo("Ala A");
        assertThat(resultado.get(0).getTipoDieta()).isEqualTo("Hipossodica");
        assertThat(resultado.get(0).getQtdRecuperando()).isEqualTo(2);
        assertThat(resultado.get(0).getNomesRecuperandos()).containsExactly("Ana", "Bia");
        verify(query).setParameter("idEstrutura", 10L);
        verify(query).setParameter("DATA", DATA_HORA);
    }

    @Test
    public void montarEstruturaPedido_linhaConsulta_mapeiaPedidoEstrutura() {
        when(query.getResultList()).thenReturn(Collections.singletonList(new Object[]{
                BigDecimal.ONE,
                BigDecimal.valueOf(10L),
                "Raiz",
                BigDecimal.valueOf(7L),
                BigDecimal.valueOf(2L)}));

        List<PedidoEstruturaDTO> resultado = dao.montarEstruturaPedido(DATA_HORA, 10L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNivel()).isEqualTo(1);
        assertThat(resultado.get(0).getEstruturaId()).isEqualTo(10);
        assertThat(resultado.get(0).getEstruturaNome()).isEqualTo("Raiz");
        assertThat(resultado.get(0).getQtdPadrao()).isEqualTo(7);
        verify(query).setParameter("data", DATA_HORA);
        verify(query).setParameter("idEstrutura", 10L);
    }

    @Test
    public void buscaEstruturaFilhoGerarPedido_transferenciaEmAndamento_aplicaFiltroPorRecuperando() {
        GerarPedidoEstruturaDTO estrutura = new GerarPedidoEstruturaDTO();
        estrutura.setId(10L);

        dao.buscaEstruturaFilhoGerarPedido(estrutura, DATA_HORA, 1L);

        String sql = sqlCapturado();
        assertFiltroAplicadoAposAgruparRamosDaEstrutura(sql);
        assertFiltroTransferenciaEmAndamento(sql, "r.ID");
    }

    @Test
    public void montarEstruturaPedido_transferenciaEmAndamento_aplicaFiltroPorPassagem() {
        long idEstrutura = 10L;

        dao.montarEstruturaPedido(DATA_HORA, idEstrutura);

        assertFiltroTransferenciaEmAndamento(sqlCapturado(), "pas.RECUPERANDO_ID");
    }

    @Test
    public void buscaPedidoDetalhado_transferenciaEmAndamento_aplicaFiltroPorPassagem() {
        long idEstrutura = 10L;
        long tipoRefeicao = 1L;

        dao.buscaPedidoDetalhado(DATA_HORA, idEstrutura, tipoRefeicao);

        assertFiltroTransferenciaEmAndamento(sqlCapturado(), "pas.RECUPERANDO_ID");
    }

    @Test
    public void buscarDietasEspeciaisPorEstrutura_transferenciaEmAndamento_aplicaFiltroPorPassagem() {
        long idEstrutura = 10L;

        dao.buscarDietasEspeciaisPorEstrutura(idEstrutura, DATA_HORA);

        assertFiltroTransferenciaEmAndamento(sqlCapturado(), "pas.RECUPERANDO_ID");
    }

    private Estrutura estrutura(Long id, String nome) {
        Estrutura estrutura = new Estrutura();
        estrutura.setId(id);
        estrutura.setNome(nome);
        return estrutura;
    }

    private String sqlCapturado() {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createNativeQuery(sqlCaptor.capture());
        return sqlCaptor.getValue();
    }

    private void assertFiltroTransferenciaEmAndamento(String sql, String recuperandoExpression) {
        String sqlNormalizado = sql.replaceAll("\\s+", " ");

        assertThat(sqlNormalizado).contains("NOT EXISTS");
        assertThat(sqlNormalizado).contains("PEDIDO_TRANSITO pt");
        assertThat(sqlNormalizado).contains("pt.RECUPERANDO_ID = " + recuperandoExpression);
        assertThat(sqlNormalizado).contains("pt.STATUS = 'EM_ANDAMENTO'");
        assertThat(sqlNormalizado).contains("pt.TIPO_TRANSITO = 'TRANSFERENCIA'");
        assertThat(sqlNormalizado).contains("pt.DATA_CONCLUSAO IS NULL");
        assertThat(sqlNormalizado).doesNotContain("pt.STATUS IN");
        assertThat(sqlNormalizado).doesNotContain("SUSPENSO");
        assertThat(sqlNormalizado).doesNotContain("VINCULADO");
        assertThat(sqlNormalizado).doesNotContain("MOVIMENTACAO_INTERNA");
        assertThat(sqlNormalizado).doesNotContain("MOVIMENTACAO_EXTERNA");
    }

    private void assertFiltroAplicadoAposAgruparRamosDaEstrutura(String sql) {
        String sqlNormalizado = sql.replaceAll("\\s+", " ");

        assertThat(sqlNormalizado)
                .contains("WHERE ((e.ESTRUTURAPAI_ID = :id AND e.CELA = 0) OR")
                .contains("plo.DATA_SAIDA IS NULL))) AND NOT EXISTS");
    }
}
