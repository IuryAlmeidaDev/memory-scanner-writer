package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EntradaSaidaDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager em;

    @Mock
    private Query query;

    @InjectMocks
    private EntradaSaidaDAO dao;

    @Test
    public void bloquearRecuperandoParaControleVisita_deveAplicarLockPessimistaNoRecuperando() {
        dao.bloquearRecuperandoParaControleVisita(10L);

        verify(em).find(Recuperando.class, 10L, LockModeType.PESSIMISTIC_WRITE);
    }

    @Test
    public void buscarUnidadePrisionalParaControleVisita_deveBuscarUnidadeAtualizadaPorId() {
        dao.buscarUnidadePrisionalParaControleVisita(1L);

        verify(em).find(UnidadePrisional.class, 1L);
    }

    @Test
    public void contarVisitantesAdultosPorRecuperandoNoDia_deveMontarQueryParametrizadaERetornarTotal() {
        Date inicioDia = new Date();
        Date fimDia = new Date();
        Date dataMaioridade = new Date();
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(BigDecimal.valueOf(3L));

        Long total = dao.contarVisitantesAdultosPorRecuperandoNoDia(1L, 10L, inicioDia, fimDia, dataMaioridade);

        assertThat(total).isEqualTo(3L);
        verify(query).setParameter("unidadeId", 1L);
        verify(query).setParameter("recuperandoId", 10L);
        verify(query).setParameter("inicioDia", inicioDia);
        verify(query).setParameter("fimDia", fimDia);
        verify(query).setParameter("situacaoEmAndamento", SituacaoVisita.EM_ANDAMENTO.getId());
        verify(query).setParameter("situacaoFinalizada", SituacaoVisita.FINALIZADA.getId());
        verify(query).setParameter("dataMaioridade", dataMaioridade);
    }

    @Test
    public void contarVisitantesAdultosPorRecuperandoNoDia_deveUsarDistinctSituacoesConfirmadasEDataNascimentoNaQuery() {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(BigDecimal.ZERO);

        dao.contarVisitantesAdultosPorRecuperandoNoDia(1L, 10L, new Date(), new Date(), new Date());

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(em).createNativeQuery(sqlCaptor.capture());

        assertThat(sqlCaptor.getValue())
                .contains("COUNT(DISTINCT E.ID)")
                .contains("E.SITUACAO IN (:situacaoEmAndamento, :situacaoFinalizada)")
                .contains("P.DATA_NASCIMENTO <= :dataMaioridade")
                .contains("C.ID_RECUPERANDO = :recuperandoId");
    }

    @Test
    public void contarVisitantesAdultosPorRecuperandoNoDia_quandoEntradaIgnoradaInformada_deveExcluirEntradaAtualDaContagem() {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(BigDecimal.ONE);

        dao.contarVisitantesAdultosPorRecuperandoNoDia(1L, 10L, new Date(), new Date(), new Date(), 99L);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(em).createNativeQuery(sqlCaptor.capture());

        assertThat(sqlCaptor.getValue()).contains("E.ID <> :entradaIgnoradaId");
        verify(query).setParameter("entradaIgnoradaId", 99L);
    }
}
