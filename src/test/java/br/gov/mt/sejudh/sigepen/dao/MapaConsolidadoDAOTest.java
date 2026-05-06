package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.Mes;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioMapaAlimentacaoConsultaDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioMapaAlimentacaoRecuperandoDTO;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MapaConsolidadoDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private MapaConsolidadoDAO dao;

    @Test
    public void buscarTotaisPedidosAuditados_deveRetornarZerosQuandoConsultaLancarExcecao() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("falha no banco"));

        Map<String, Object> resultado = dao.buscarTotaisPedidosAuditados(10L, "01/04/2026", "30/04/2026");

        assertThat(resultado)
                .containsEntry("AUDIT_CAFE", 0)
                .containsEntry("AUDIT_ALMOCO", 0)
                .containsEntry("AUDIT_JANTA", 0)
                .containsEntry("AUDIT_CAFE_ESPECIAL", 0)
                .containsEntry("AUDIT_ALMOCO_ESPECIAL", 0)
                .containsEntry("AUDIT_JANTA_ESPECIAL", 0);
    }

    @Test
    public void buscarTotaisPedidosAuditados_deveMapearCafeAlmocoEJanta() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.<Object[]>asList(
                new Object[]{"CAFE DA MANHA", BigDecimal.valueOf(12), BigDecimal.valueOf(2)},
                new Object[]{"ALMOCO", BigDecimal.valueOf(20), BigDecimal.valueOf(3)},
                new Object[]{"JANTA", BigDecimal.valueOf(18), null}
        ));

        Map<String, Object> resultado = dao.buscarTotaisPedidosAuditados(10L, "01/04/2026", "30/04/2026");

        assertThat(resultado)
                .containsEntry("AUDIT_CAFE", 12)
                .containsEntry("AUDIT_CAFE_ESPECIAL", 2)
                .containsEntry("AUDIT_ALMOCO", 20)
                .containsEntry("AUDIT_ALMOCO_ESPECIAL", 3)
                .containsEntry("AUDIT_JANTA", 18)
                .containsEntry("AUDIT_JANTA_ESPECIAL", 0);
    }

    @Test
    public void buscarTotaisPedidosAuditados_deveManterPadraoQuandoLinhaVierInconsistente() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.<Object[]>singletonList(
                new Object[]{"CAFE DA MANHA", null, BigDecimal.ONE}
        ));

        Map<String, Object> resultado = dao.buscarTotaisPedidosAuditados(10L, "01/04/2026", "30/04/2026");

        assertThat(resultado)
                .containsEntry("AUDIT_CAFE", 0)
                .containsEntry("AUDIT_ALMOCO", 0)
                .containsEntry("AUDIT_JANTA", 0)
                .containsEntry("AUDIT_CAFE_ESPECIAL", 0)
                .containsEntry("AUDIT_ALMOCO_ESPECIAL", 0)
                .containsEntry("AUDIT_JANTA_ESPECIAL", 0);
    }

    @Test
    public void buscarRelatorioMapaAlimentacaoConsulta_deveRetornarListaVaziaQuandoQueryVierVazia() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        List<RelatorioMapaAlimentacaoConsultaDTO> resultado = dao.buscarRelatorioMapaAlimentacaoConsulta(10L, 99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    public void buscarRelatorioMapaAlimentacaoConsulta_deveMapearLinhaDaConsulta() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);

        Object[] linha = new Object[]{
                new Date(),
                new Date(),
                "CTR-10",
                "Fornecedor XPTO",
                "CAFE DA MANHA",
                BigDecimal.valueOf(4.5D),
                BigDecimal.valueOf(6.5D)
        };
        when(query.getResultList()).thenReturn(Collections.singletonList(linha));

        List<RelatorioMapaAlimentacaoConsultaDTO> resultado = dao.buscarRelatorioMapaAlimentacaoConsulta(10L, 99L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNumeroContrato()).isEqualTo("CTR-10");
        assertThat(resultado.get(0).getNomeFornecedor()).isEqualTo("Fornecedor XPTO");
        assertThat(resultado.get(0).getRefeicao()).isEqualTo("CAFE DA MANHA");
        assertThat(resultado.get(0).getValorNormal()).isEqualTo(4.5D);
        assertThat(resultado.get(0).getValorEspecial()).isEqualTo(6.5D);
    }

    @Test
    public void buscaRelatorioMapaAlimentacaoRecuperando_deveMapearLinhasDaConsulta() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);

        Object[] linha = new Object[]{
                "Recuperando",
                BigDecimal.valueOf(123),
                new Date(),
                null,
                BigDecimal.ONE,
                BigDecimal.ZERO,
                null,
                null,
                "Nome Social",
                "PROVISORIO"
        };
        when(query.getResultList()).thenReturn(Collections.singletonList(linha));

        List<RelatorioMapaAlimentacaoRecuperandoDTO> resultado = dao.buscaRelatorioMapaAlimentacaoRecuperando(
                20L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNomeRecuperando()).isEqualTo("Recuperando");
        assertThat(resultado.get(0).getMatriculaRecuperando()).isEqualTo("123");
        assertThat(resultado.get(0).getNomeSocialRecuperando()).isEqualTo("Nome Social");
        assertThat(resultado.get(0).getSituacaoRecuperando()).isEqualTo("Provisório");
    }

    @Test
    public void buscaRelatorioMapaAlimentacaoRecuperando_deveBuscarDietaQueSobrepoePeriodo() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());

        dao.buscaRelatorioMapaAlimentacaoRecuperando(20L, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1));

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createNativeQuery(sqlCaptor.capture());

        assertThat(sqlCaptor.getValue())
                .contains("dr.INICIO <= TO_DATE(:dataFim, 'DD/MM/YYYY')")
                .contains("dr.FIM >= TO_DATE(:dataInicio, 'DD/MM/YYYY') OR dr.fim IS null");
    }

    @Test
    public void buscaRelatorioMapaAlimentacaoRecuperandoPorMesEAno_deveMapearLinhasDaConsulta() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);

        Object[] linha = new Object[]{
                "Recuperando 2",
                BigDecimal.valueOf(456),
                new Date(),
                null,
                BigDecimal.ONE,
                BigDecimal.ONE,
                null,
                null,
                "Social 2",
                "ALVARA_DE_SOLTURA"
        };
        when(query.getResultList()).thenReturn(Collections.singletonList(linha));

        List<RelatorioMapaAlimentacaoRecuperandoDTO> resultado = dao.buscaRelatorioMapaAlimentacaoRecuperando(
                20L, Mes.ABRIL, "2026");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNomeRecuperando()).isEqualTo("Recuperando 2");
        assertThat(resultado.get(0).getMatriculaRecuperando()).isEqualTo("456");
        assertThat(resultado.get(0).getNomeSocialRecuperando()).isEqualTo("Social 2");
        assertThat(resultado.get(0).getSituacaoRecuperando()).isEqualTo("Alvará de Soltura");
    }
}
