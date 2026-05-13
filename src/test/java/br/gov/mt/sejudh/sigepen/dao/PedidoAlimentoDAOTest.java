package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PedidoAlimentoDAOTest extends BaseUnitTest {

    private final PedidoAlimentoDAO dao = new PedidoAlimentoDAO();

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Boolean> booleanQuery;

    @Mock
    private TypedQuery<Long> longQuery;

    @Test
    public void salvarComFlush_devePersistirPedidoNovoEExecutarFlush() throws Exception {
        setEntityManager();
        PedidoAlimento pedido = PedidoAlimento.create("24/04/2026 12:00:00", 0L, 10L, 20L, 30L);

        PedidoAlimento resultado = dao.salvarComFlush(pedido);

        assertThat(resultado).isSameAs(pedido);
        verify(entityManager).persist(pedido);
        verify(entityManager).flush();
    }

    @Test
    public void salvarComFlush_deveFazerMergePedidoExistenteEExecutarFlush() throws Exception {
        setEntityManager();
        PedidoAlimento pedido = PedidoAlimento.create("24/04/2026 12:00:00", 0L, 10L, 20L, 30L);
        PedidoAlimento pedidoMergeado = PedidoAlimento.create("24/04/2026 12:00:00", 0L, 10L, 20L, 30L);
        pedido.setId(1L);
        pedidoMergeado.setId(1L);
        when(entityManager.merge(pedido)).thenReturn(pedidoMergeado);

        PedidoAlimento resultado = dao.salvarComFlush(pedido);

        assertThat(resultado).isSameAs(pedidoMergeado);
        verify(entityManager).merge(pedido);
        verify(entityManager).flush();
    }

    @Test
    public void existePedidoAtivoPorTipoRefeicaoEUnidade_deveConsultarPorDiaDoPedido() throws Exception {
        setEntityManager();
        when(entityManager.createQuery(anyString(), eq(Boolean.class))).thenReturn(booleanQuery);
        when(booleanQuery.getSingleResult()).thenReturn(true);

        boolean resultado = dao.existePedidoAtivoPorTipoRefeicaoEUnidade(
                "24/04/2026 11:10:00",
                30L,
                10L,
                20L);

        assertThat(resultado).isTrue();
        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture(), eq(Boolean.class));
        assertThat(jpql.getValue()).contains("p.dataPedido LIKE :dataPedido");
        verify(booleanQuery).setParameter("dataPedido", "24/04/2026%");
        verify(booleanQuery).setParameter("idTipoRefeicao", 30L);
        verify(booleanQuery).setParameter("idUnidadePrisional", 10L);
        verify(booleanQuery).setParameter("idContrato", 20L);
        verify(booleanQuery).setParameter("statusAtivo", Status.ATIVO);
    }

    @Test
    public void existePedidoAtivo_deveConsultarPorDiaDoPedido() throws Exception {
        setEntityManager();
        when(entityManager.createQuery(anyString(), eq(Boolean.class))).thenReturn(booleanQuery);
        when(booleanQuery.getSingleResult()).thenReturn(false);

        boolean resultado = dao.existePedidoAtivo("24/04/2026 17:00:00", 30L, 10L, 20L);

        assertThat(resultado).isFalse();
        verify(booleanQuery).setParameter("dataPedido", "24/04/2026%");
        verify(booleanQuery).setParameter("statusAtivo", Status.ATIVO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void existePedidoAtivo_deveRejeitarDataPedidoForaDoFormatoDiario() throws Exception {
        setEntityManager();
        when(entityManager.createQuery(anyString(), eq(Boolean.class))).thenReturn(booleanQuery);

        dao.existePedidoAtivo("2026-04-24 17:00:00", 30L, 10L, 20L);
    }

    @Test
    public void findPedidosPendentesPorTipoRefeicaoEUnidade_deveBuscarPendentesPorDiaDoPedido() throws Exception {
        setEntityManager();
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.getResultList()).thenReturn(Arrays.asList(101L, 102L));

        List<Long> resultado = dao.findPedidosPendentesPorTipoRefeicaoEUnidade(
                "24/04/2026 11:10:00",
                30L,
                10L,
                20L);

        assertThat(resultado).containsExactly(101L, 102L);
        verify(longQuery).setParameter("dataPedido", "24/04/2026%");
        verify(longQuery).setParameter("idTipoRefeicao", 30L);
        verify(longQuery).setParameter("idUnidadePrisional", 10L);
        verify(longQuery).setParameter("idContrato", 20L);
        verify(longQuery).setParameter("statusPendente", Status.PENDENTE);
    }

    @Test
    public void findPedidosEmAbertoPorTipoRefeicaoEUnidade_deveBuscarAtivosEPendentesPorDiaDoPedido()
            throws Exception {
        setEntityManager();
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.getResultList()).thenReturn(Arrays.asList(201L, 202L));

        List<Long> resultado = dao.findPedidosEmAbertoPorTipoRefeicaoEUnidade(
                "24/04/2026 19:00:00",
                30L,
                10L,
                20L);

        assertThat(resultado).containsExactly(201L, 202L);
        verify(longQuery).setParameter("dataPedido", "24/04/2026%");
        verify(longQuery).setParameter("statusesEmAberto", Arrays.asList(Status.PENDENTE, Status.ATIVO));
    }

    @Test
    public void findPedidosParaInativarAoAssinar_deveBuscarAtivosEPendentesPorDiaDoPedido() throws Exception {
        setEntityManager();
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.getResultList()).thenReturn(Arrays.asList(301L));

        List<Long> resultado = dao.findPedidosParaInativarAoAssinar("24/04/2026 12:00:00", 30L, 10L, 20L);

        assertThat(resultado).containsExactly(301L);
        verify(longQuery).setParameter("dataPedido", "24/04/2026%");
        verify(longQuery).setParameter("statuses", Arrays.asList(Status.PENDENTE, Status.ATIVO));
    }

    @Test
    public void findActivePedidoAlimentoIdsByCriteria_deveBuscarEmAbertoPorDiaDoPedido() throws Exception {
        setEntityManager();
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.getResultList()).thenReturn(Arrays.asList(401L));

        List<Long> resultado = dao.findActivePedidoAlimentoIdsByCriteria("24/04/2026 14:00:00", 30L, 10L, 20L);

        assertThat(resultado).containsExactly(401L);
        verify(longQuery).setParameter("dataPedido", "24/04/2026%");
        verify(longQuery).setParameter("statusesEmAberto", Arrays.asList(Status.PENDENTE, Status.ATIVO));
    }

    private void setEntityManager() throws Exception {
        Field field = PedidoAlimentoDAO.class.getSuperclass().getDeclaredField("em");
        field.setAccessible(true);
        field.set(dao, entityManager);
    }
}
