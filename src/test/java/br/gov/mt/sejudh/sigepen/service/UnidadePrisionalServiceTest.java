package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.UnidadePrisionalDAO;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.TipoUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnidadePrisionalServiceTest extends BaseUnitTest {

    @InjectMocks
    private UnidadePrisionalService service;

    @Mock
    private UnidadePrisionalDAO dao;

    @Test
    public void salvar_quandoQuantidadeVisitaPorRecuperandoNegativa_deveLancarValidacaoException() {
        UnidadePrisional unidade = unidadeValida(-1);

        Throwable throwable = catchThrowable(() -> service.salvar(unidade));

        assertThat(throwable).isInstanceOf(ValidacaoException.class)
                .hasMessage("Quantidade de visita por recuperando nÃ£o pode ser negativa");
    }

    @Test
    public void salvar_quandoQuantidadeVisitaPorRecuperandoNula_deveSalvar() throws ValidacaoException {
        UnidadePrisional unidade = unidadeValida(null);
        when(dao.salvar(unidade)).thenReturn(unidade);

        UnidadePrisional retorno = service.salvar(unidade);

        assertThat(retorno).isSameAs(unidade);
        verify(dao).salvar(unidade);
    }

    @Test
    public void salvar_quandoQuantidadeVisitaPorRecuperandoZero_deveSalvar() throws ValidacaoException {
        UnidadePrisional unidade = unidadeValida(0);
        when(dao.salvar(unidade)).thenReturn(unidade);

        UnidadePrisional retorno = service.salvar(unidade);

        assertThat(retorno).isSameAs(unidade);
        verify(dao).salvar(unidade);
    }

    @Test
    public void salvar_quandoQuantidadeVisitaPorRecuperandoPositiva_deveSalvar() throws ValidacaoException {
        UnidadePrisional unidade = unidadeValida(3);
        when(dao.salvar(unidade)).thenReturn(unidade);

        UnidadePrisional retorno = service.salvar(unidade);

        assertThat(retorno).isSameAs(unidade);
        verify(dao).salvar(unidade);
    }

    private UnidadePrisional unidadeValida(Integer quantidadeVisitaPorRecuperando) {
        UnidadePrisional unidade = new UnidadePrisional();
        unidade.setNome("Unidade");
        unidade.setSigla("UND");
        unidade.setTipoUnidade(new TipoUnidade());
        unidade.setQuantidadeVisitaPorRecuperando(quantidadeVisitaPorRecuperando);
        return unidade;
    }
}
