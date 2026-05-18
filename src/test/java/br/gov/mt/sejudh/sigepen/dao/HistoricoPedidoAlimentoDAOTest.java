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
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoComboDTO;

public class HistoricoPedidoAlimentoDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private TypedQuery<ContratoComboDTO> query;

    @InjectMocks
    private HistoricoPedidoAlimentoDAO dao;

    @Test
    public void listarContratosParaCombo_semPermissaoFiltraPorUnidade() {
        List<ContratoComboDTO> contratos = Collections.singletonList(new ContratoComboDTO(1L, "CT-001"));
        when(entityManager.createQuery(anyString(), eq(ContratoComboDTO.class))).thenReturn(query);
        when(query.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(query);
        when(query.getResultList()).thenReturn(contratos);

        List<ContratoComboDTO> resultado = dao.listarContratosParaCombo(false, 7L);

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture(), eq(ContratoComboDTO.class));
        assertThat(jpql.getValue()).contains("LEFT JOIN c.unidades u");
        assertThat(jpql.getValue()).contains("AND u.id = :unidadeId");
        verify(query).setParameter("status", Status.ATIVO);
        verify(query).setParameter("unidadeId", 7L);
        assertThat(resultado).isSameAs(contratos);
    }

    @Test
    public void listarContratosParaCombo_semPermissaoSemUnidadeNaoConsultaBanco() {
        List<ContratoComboDTO> resultado = dao.listarContratosParaCombo(false, null);

        assertThat(resultado).isEmpty();
        verify(entityManager, never()).createQuery(anyString(), eq(ContratoComboDTO.class));
    }

    @Test
    public void listarContratosParaCombo_comPermissaoNaoFiltraPorUnidade() {
        when(entityManager.createQuery(anyString(), eq(ContratoComboDTO.class))).thenReturn(query);
        when(query.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.<ContratoComboDTO>emptyList());

        List<ContratoComboDTO> resultado = dao.listarContratosParaCombo(true, null);

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture(), eq(ContratoComboDTO.class));
        assertThat(jpql.getValue()).doesNotContain(":unidadeId");
        verify(query).setParameter("status", Status.ATIVO);
        verify(query, never()).setParameter("unidadeId", null);
        assertThat(resultado).isEmpty();
    }
}
