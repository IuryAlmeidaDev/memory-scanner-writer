package br.gov.mt.sejudh.sigepen.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.model.Recuperando;

public class PedidoDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Query query;

    @InjectMocks
    private PedidoDAO dao;

    @Test
    public void recuperandoEmMovimentacao_quandoContadorMaiorQueZero_retornaTrue() {
        Recuperando recuperando = new Recuperando();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        boolean resultado = dao.recuperandoEmMovimentacao(recuperando);

        assertThat(resultado).isTrue();
        verify(query).setParameter("rec", recuperando);
        verify(query).setParameter("status", StatusPedido.EM_ANDAMENTO);
        verify(query).setParameter("statusSuspenso", StatusPedido.SUSPENSO);
    }

    @Test
    public void recuperandoEmMovimentacao_quandoContadorZero_retornaFalse() {
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);

        boolean resultado = dao.recuperandoEmMovimentacao(new Recuperando());

        assertThat(resultado).isFalse();
    }
}
