package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.DiasSemana;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardapioDAOTest extends BaseUnitTest {

    private final CardapioDAO dao = new CardapioDAO();

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Long> query;

    @Test
    public void existeCardapioPara_deveFiltrarPorUnidadePrisional() throws Exception {
        setEntityManager();
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        boolean resultado = dao.existeCardapioPara("24/04/2026 12:00:00", 30L, 20L, 10L);

        assertThat(resultado).isTrue();
        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture(), eq(Long.class));
        assertThat(jpql.getValue()).contains("c.unidadePrisional.id = :unidadePrisionalId");
        verify(query).setParameter("tipoRefeicaoId", 30L);
        verify(query).setParameter("contratoId", 20L);
        verify(query).setParameter("unidadePrisionalId", 10L);
    }

    @Test
    public void existeCardapioPara_deveRetornarFalseQuandoNaoEncontrarCardapioDaUnidade() throws Exception {
        setEntityManager();
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(0L);

        boolean resultado = dao.existeCardapioPara("24/04/2026 12:00:00", 30L, 20L, 10L);

        assertThat(resultado).isFalse();
        verify(query).setParameter(eq("diaSemana"), any(DiasSemana.class));
        verify(query).setParameter("unidadePrisionalId", 10L);
    }

    private void setEntityManager() throws Exception {
        Field field = CardapioDAO.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(dao, entityManager);
    }
}
