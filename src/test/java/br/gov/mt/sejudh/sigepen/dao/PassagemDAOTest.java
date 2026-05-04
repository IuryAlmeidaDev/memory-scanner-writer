package br.gov.mt.sejudh.sigepen.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.Recuperando;

public class PassagemDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager em;
    @Mock
    private Query query;
    @Mock
    private TypedQuery<Passagem> typedQuery;

    @InjectMocks
    private PassagemDAO dao;

    @Test
    public void possuiPassagemAberta_quandoContadorMaiorQueZero_retornaTrue() {
        Recuperando recuperando = new Recuperando();
        recuperando.setId(99L);
        when(em.createQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        boolean resultado = dao.possuiPassagemAberta(recuperando);

        assertThat(resultado).isTrue();
        verify(query).setParameter("idRec", 99L);
    }

    @Test
    public void buscarPrimeiraPassagem_semPassagens_retornaTrue() {
        when(em.createQuery(anyString(), org.mockito.Mockito.eq(Passagem.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        boolean resultado = dao.buscarPrimeiraPassagem(99L);

        assertThat(resultado).isTrue();
        verify(typedQuery).setParameter("id", 99L);
    }
}
