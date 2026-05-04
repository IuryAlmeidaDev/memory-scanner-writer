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
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.model.Transferencia;

public class TransferenciaDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Query query;

    @InjectMocks
    private TransferenciaDAO dao;

    @Test
    public void possuiPedidoEmAndamento_quandoExistePedido_retornaTrue() {
        Transferencia transferencia = new Transferencia();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        boolean resultado = dao.possuiPedidoEmAndamento(transferencia);

        assertThat(resultado).isTrue();
        verify(query).setParameter("transferencia", transferencia);
        verify(query).setParameter("status", StatusPedido.EM_ANDAMENTO);
    }

    @Test
    public void isTranferenciaEmAndamento_quandoStatusEmAndamento_retornaTrue() {
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        boolean resultado = dao.isTranferenciaEmAndamento(10L);

        assertThat(resultado).isTrue();
        verify(query).setParameter("transferenciaId", 10L);
        verify(query).setParameter("status", StatusMovimentacao.EM_ANDAMENTO);
    }
}
