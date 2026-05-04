package br.gov.mt.sejudh.sigepen.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.enumerator.Status;

public class DietaEspecialDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Query query;

    @InjectMocks
    private DietaEspecialDAO dao;

    @Before
    public void setUp() {
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(query);
    }

    @Test
    public void suspenderDietasAtivasDoRecuperando_alteraApenasAtivasVigentes() {
        dao.suspenderDietasAtivasDoRecuperando(99L);

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture());
        assertThat(jpql.getValue()).contains("d.status = :statusAtivo");
        assertThat(jpql.getValue()).contains("d.inicio <= :dataReferencia");
        assertThat(jpql.getValue()).contains("d.fim IS NULL OR d.fim > :dataReferencia");
        verify(query).setParameter("statusSuspenso", Status.SUSPENSO);
        verify(query).setParameter("statusAtivo", Status.ATIVO);
        verify(query).setParameter("idRecuperando", 99L);
        verify(query).executeUpdate();
    }

    @Test
    public void reativarDietasSuspensasDoRecuperando_alteraSuspensasVigentesMesmoComFim() {
        dao.reativarDietasSuspensasDoRecuperando(99L);

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture());
        assertThat(jpql.getValue()).contains("d.status = :statusSuspenso");
        assertThat(jpql.getValue()).contains("d.fim IS NULL OR d.fim > :dataReferencia");
        verify(query).setParameter("statusAtivo", Status.ATIVO);
        verify(query).setParameter("statusSuspenso", Status.SUSPENSO);
        verify(query).setParameter("idRecuperando", 99L);
        verify(query).executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void inativarDietasAtivasOuSuspensasDoRecuperando_sobrescreveFimExistente() {
        Date dataFim = new Date();

        dao.inativarDietasAtivasOuSuspensasDoRecuperando(99L, dataFim);

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List<Status>> statusCaptor = ArgumentCaptor.forClass(List.class);
        verify(entityManager).createQuery(jpql.capture());
        assertThat(jpql.getValue()).contains("d.status IN (:statusVigentes)");
        assertThat(jpql.getValue()).doesNotContain("d.inicio <= :dataFim");
        assertThat(jpql.getValue()).doesNotContain("d.fim IS NULL OR d.fim > :dataFim");
        verify(query).setParameter("statusInativo", Status.INATIVO);
        verify(query).setParameter(org.mockito.Mockito.eq("statusVigentes"), statusCaptor.capture());
        assertThat(statusCaptor.getValue()).containsExactly(Status.ATIVO, Status.SUSPENSO);
        verify(query).setParameter("dataFim", dataFim);
        verify(query).setParameter("idRecuperando", 99L);
        verify(query).executeUpdate();
    }
}
