package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.PedidoAlimento;
import org.junit.Test;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PedidoAlimentoDAOTest extends BaseUnitTest {

    private final PedidoAlimentoDAO dao = new PedidoAlimentoDAO();

    @Mock
    private EntityManager entityManager;

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

    private void setEntityManager() throws Exception {
        Field field = PedidoAlimentoDAO.class.getSuperclass().getDeclaredField("em");
        field.setAccessible(true);
        field.set(dao, entityManager);
    }
}
