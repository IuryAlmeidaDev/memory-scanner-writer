package br.gov.mt.sejudh.sigepen.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
import br.gov.mt.sejudh.sigepen.model.Contrato;

public class ContratoDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<Contrato> query;

    @InjectMocks
    private ContratoDAO dao;

    @Test
    public void listarParaFiltro_semPermissaoFiltraPorUnidade() {
        when(entityManager.createQuery(anyString(), eq(Contrato.class))).thenReturn(query);
        when(query.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.<Contrato>emptyList());

        List<Contrato> contratos = dao.listarParaFiltro(false, 11L);

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture(), eq(Contrato.class));
        assertThat(jpql.getValue()).contains("join c.unidades u");
        assertThat(jpql.getValue()).contains("u.id = :unidadeId");
        verify(query).setParameter("unidadeId", 11L);
        assertThat(contratos).isEmpty();
    }

    @Test
    public void listarParaFiltro_semPermissaoSemUnidadeNaoConsultaBanco() {
        List<Contrato> contratos = dao.listarParaFiltro(false, null);

        assertThat(contratos).isEmpty();
        verify(entityManager, never()).createQuery(anyString(), eq(Contrato.class));
    }

    @Test
    public void listarAtivosParaFiltro_semPermissaoFiltraContratosAtivosPorUnidade() {
        when(entityManager.createQuery(anyString(), eq(Contrato.class))).thenReturn(query);
        when(query.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.<Contrato>emptyList());

        List<Contrato> contratos = dao.listarAtivosParaFiltro(false, 22L);

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture(), eq(Contrato.class));
        assertThat(jpql.getValue()).contains("join c.unidades u");
        assertThat(jpql.getValue()).contains("where c.status = 1");
        assertThat(jpql.getValue()).contains("and u.id = :unidadeId");
        verify(query).setParameter("unidadeId", 22L);
        assertThat(contratos).isEmpty();
    }

    @Test
    public void listarAtivosParaFiltro_semPermissaoSemUnidadeNaoConsultaBanco() {
        List<Contrato> contratos = dao.listarAtivosParaFiltro(false, null);

        assertThat(contratos).isEmpty();
        verify(entityManager, never()).createQuery(anyString(), eq(Contrato.class));
    }
}
