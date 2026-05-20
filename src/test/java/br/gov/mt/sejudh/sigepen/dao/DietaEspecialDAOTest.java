package br.gov.mt.sejudh.sigepen.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroDietaEspecial;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.DietaRecuperando;

public class DietaEspecialDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Query query;
    @Mock
    private TypedQuery<String> stringQuery;
    @Mock
    private Session session;
    @Mock
    private Criteria criteria;

    @InjectMocks
    private DietaEspecialDAO dao;

    @Before
    public void setUp() {
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(entityManager.createQuery(anyString(), eq(String.class))).thenReturn(stringQuery);
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        when(session.createCriteria(DietaRecuperando.class)).thenReturn(criteria);
        when(query.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(query);
        when(stringQuery.setParameter(anyString(), org.mockito.ArgumentMatchers.any())).thenReturn(stringQuery);
        when(criteria.createAlias(anyString(), anyString(), org.mockito.ArgumentMatchers.any(org.hibernate.sql.JoinType.class))).thenReturn(criteria);
        when(criteria.setProjection(org.mockito.ArgumentMatchers.any(Projection.class))).thenReturn(criteria);
        when(criteria.add(org.mockito.ArgumentMatchers.any(Criterion.class))).thenReturn(criteria);
        when(criteria.addOrder(org.mockito.ArgumentMatchers.any(Order.class))).thenReturn(criteria);
        when(criteria.setResultTransformer(org.mockito.ArgumentMatchers.any())).thenReturn(criteria);
        when(criteria.list()).thenReturn(Collections.emptyList());
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

    @Test
    public void listarNomesRecuperandosParaFiltro_semConsultaListaParaCombo() {
        when(stringQuery.getResultList()).thenReturn(Collections.singletonList("Maria"));

        List<String> nomes = dao.listarNomesRecuperandosParaFiltro(false, 7L);

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture(), eq(String.class));
        assertThat(jpql.getValue()).doesNotContain("lower(pessoa.nome) like :consulta");
        verify(stringQuery).setParameter("unidadeId", 7L);
        verify(stringQuery, never()).setParameter(eq("consulta"), org.mockito.ArgumentMatchers.any());
        verify(stringQuery, never()).setMaxResults(org.mockito.ArgumentMatchers.anyInt());
        assertThat(nomes).containsExactly("Maria");
    }

    @Test
    public void listarNomesRecuperandosParaFiltro_comConsultaAplicaLikeELimite() {
        when(stringQuery.getResultList()).thenReturn(Collections.singletonList("Maria"));

        List<String> nomes = dao.listarNomesRecuperandosParaFiltro(false, 7L, " Maria ", 20);

        ArgumentCaptor<String> jpql = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpql.capture(), eq(String.class));
        assertThat(jpql.getValue()).contains("lower(pessoa.nome) like :consulta");
        verify(stringQuery).setParameter("unidadeId", 7L);
        verify(stringQuery).setParameter("consulta", "%maria%");
        verify(stringQuery).setMaxResults(20);
        assertThat(nomes).containsExactly("Maria");
    }

    @Test
    public void listarNomesRecuperandosParaFiltro_semPermissaoSemUnidadeNaoConsultaBanco() {
        List<String> nomes = dao.listarNomesRecuperandosParaFiltro(false, null);

        assertThat(nomes).isEmpty();
        verify(entityManager, never()).createQuery(anyString(), eq(String.class));
    }

    @Test
    public void buscaPorFiltroRelatorio_deveBuscarSemPaginarEOrdenarPorCodigo() {
        FiltroDietaEspecial filtro = new FiltroDietaEspecial();
        filtro.setListarTodasUnidades(true);

        dao.buscaPorFiltroRelatorio(filtro);

        assertThat(filtro.getPrimeiroRegistro()).isZero();
        assertThat(filtro.getQuantidadeRegistros()).isEqualTo(Integer.MAX_VALUE);
        assertThat(filtro.getPropriedadeOrdenacao()).isEqualTo("id");
        assertThat(filtro.isAscendente()).isTrue();
        verify(criteria, never()).setFirstResult(org.mockito.ArgumentMatchers.anyInt());
        verify(criteria, never()).setMaxResults(org.mockito.ArgumentMatchers.anyInt());
        verify(criteria).addOrder(org.mockito.ArgumentMatchers.any(Order.class));
        verify(criteria).list();
    }

    @Test
    public void buscaPorFiltroRelatorio_devePreservarOrdenacaoDescendenteDaGrid() {
        FiltroDietaEspecial filtro = new FiltroDietaEspecial();
        filtro.setListarTodasUnidades(true);
        filtro.organizaFiltro(10, 10, "id", org.primefaces.model.SortOrder.DESCENDING);
        filtro.setPaginacaoIndex(10);

        dao.buscaPorFiltroRelatorio(filtro);

        assertThat(filtro.getPrimeiroRegistro()).isEqualTo(10);
        assertThat(filtro.getQuantidadeRegistros()).isEqualTo(Integer.MAX_VALUE);
        assertThat(filtro.getPropriedadeOrdenacao()).isEqualTo("id");
        assertThat(filtro.getSortOrder()).isEqualTo(org.primefaces.model.SortOrder.DESCENDING);
        assertThat(filtro.isAscendente()).isFalse();
        verify(criteria).setFirstResult(10);
        verify(criteria, never()).setMaxResults(org.mockito.ArgumentMatchers.anyInt());
        verify(criteria).addOrder(org.mockito.ArgumentMatchers.any(Order.class));
        verify(criteria).list();
    }
}
