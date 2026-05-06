package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class UnidadePrisionalDAOTest extends BaseUnitTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UnidadePrisionalDAO dao;

    @Test
    public void salvar_quandoUnidadeNova_devePersistirMantendoQuantidadeVisitaPorRecuperando() {
        UnidadePrisional unidade = new UnidadePrisional();
        unidade.setQuantidadeVisitaPorRecuperando(2);

        UnidadePrisional retorno = dao.salvar(unidade);

        assertThat(retorno).isSameAs(unidade);
        assertThat(retorno.getQuantidadeVisitaPorRecuperando()).isEqualTo(2);
        verify(entityManager).persist(unidade);
        verify(entityManager, never()).merge(unidade);
    }

    @Test
    public void salvar_quandoUnidadeExistente_deveFazerMergeMantendoQuantidadeVisitaPorRecuperando() {
        UnidadePrisional unidade = new UnidadePrisional(1L);
        unidade.setQuantidadeVisitaPorRecuperando(3);

        UnidadePrisional retorno = dao.salvar(unidade);

        assertThat(retorno).isSameAs(unidade);
        assertThat(retorno.getQuantidadeVisitaPorRecuperando()).isEqualTo(3);
        verify(entityManager).merge(unidade);
        verify(entityManager, never()).persist(unidade);
    }
}
