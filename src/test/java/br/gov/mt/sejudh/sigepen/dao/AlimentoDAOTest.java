package br.gov.mt.sejudh.sigepen.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.Alimento;

public class AlimentoDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<Alimento> query;

    @InjectMocks
    private AlimentoDAO dao;

    @Test
    public void listarTodos_deveOrdenarPorDescricao() {
        List<Alimento> alimentos = Collections.singletonList(new Alimento());
        when(entityManager.createQuery(anyString(), eq(Alimento.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(alimentos);

        List<Alimento> resultado = dao.listarTodos();

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture(), eq(Alimento.class));
        assertThat(jpql.getValue()).isEqualTo("select a from Alimento a order by descricao asc");
        assertThat(resultado).isSameAs(alimentos);
    }
}
