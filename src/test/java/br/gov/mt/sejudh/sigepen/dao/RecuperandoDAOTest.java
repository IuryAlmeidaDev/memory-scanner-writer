package br.gov.mt.sejudh.sigepen.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.Recuperando;

public class RecuperandoDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<Recuperando> recuperandoTypedQuery;

    @Mock
    private Query query;

    @InjectMocks
    private RecuperandoDAO dao;

    @Test
    public void buscarRecuperandoSimplesPorId_quandoNaoEncontrar_deveRetornarNulo() {
        when(em.createQuery(anyString(), eq(Recuperando.class))).thenReturn(recuperandoTypedQuery);
        when(recuperandoTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        Recuperando resultado = dao.buscarRecuperandoSimplesPorId(10L);

        assertThat(resultado).isNull();
        verify(recuperandoTypedQuery).setParameter("id", 10L);
        verify(recuperandoTypedQuery).getSingleResult();
    }

    @Test
    public void buscarRecuperandoSimplesPorIdPessoa_quandoNaoEncontrar_deveLancarValidacaoException() {
        when(em.createQuery(anyString(), eq(Recuperando.class))).thenReturn(recuperandoTypedQuery);
        when(recuperandoTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        assertThatThrownBy(() -> dao.buscarRecuperandoSimplesPorIdPessoa(20L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Pessoa 20");

        verify(recuperandoTypedQuery).setParameter("id", 20L);
        verify(recuperandoTypedQuery).getSingleResult();
    }

    @Test
    public void buscarRecuperandoPorIdPessoa_quandoNaoEncontrar_deveRetornarOptionalVazio() {
        when(em.createQuery(anyString(), eq(Recuperando.class))).thenReturn(recuperandoTypedQuery);
        when(recuperandoTypedQuery.getSingleResult()).thenThrow(new NoResultException());

        Optional<Recuperando> resultado = dao.buscarRecuperandoPorIdPessoa(30L);

        assertThat(resultado).isEmpty();
        verify(recuperandoTypedQuery).setParameter("id", 30L);
        verify(recuperandoTypedQuery).getSingleResult();
    }

    @Test
    public void recuperandoJaExisteNoSistema_quandoNaoEncontrarDuplicidade_deveRetornarNulo() {
        Date dataNascimento = new Date();
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());

        Long resultado = dao.recuperandoJaExisteNoSistema("Maria", "Ana", dataNascimento, 10L);

        assertThat(resultado).isNull();
        verify(query).setParameter("nome", "MARIA");
        verify(query).setParameter("nomeMae", "ANA");
        verify(query).setParameter("dataNascimento", dataNascimento);
        verify(query).setParameter("id", 10L);
    }

    @Test
    public void recuperandoJaExisteNoSistema_comIdNulo_deveConsultarSemParametroId() {
        Date dataNascimento = new Date();
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(99L);

        Long resultado = dao.recuperandoJaExisteNoSistema("Maria", "Ana", dataNascimento, null);

        assertThat(resultado).isEqualTo(99L);
        verify(query).setParameter("nome", "MARIA");
        verify(query).setParameter("nomeMae", "ANA");
        verify(query).setParameter("dataNascimento", dataNascimento);
        verify(query, never()).setParameter(eq("id"), any());
    }

    @Test
    public void fotoVinculada_quandoHouverVinculo_deveRetornarVerdadeiro() {
        Foto foto = new Foto();
        foto.setId(7L);
        Query pedidoQuery = mock(Query.class);
        Query remanejamentoQuery = mock(Query.class);
        Query passagemQuery = mock(Query.class);
        when(em.createQuery(anyString())).thenReturn(pedidoQuery, remanejamentoQuery, passagemQuery);
        when(pedidoQuery.getSingleResult()).thenReturn(1L);
        when(remanejamentoQuery.getSingleResult()).thenReturn(0L);
        when(passagemQuery.getSingleResult()).thenReturn(0L);

        boolean resultado = dao.fotoVinculada(foto);

        assertThat(resultado).isTrue();
        verify(pedidoQuery).setParameter("idFoto", 7L);
        verify(remanejamentoQuery).setParameter("idFoto", 7L);
        verify(passagemQuery).setParameter("idFoto", 7L);
    }
}
