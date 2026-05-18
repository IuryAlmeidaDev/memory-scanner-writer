package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.UnidadePrisionalDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnidadePrisional;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.TipoUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

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

    @Test
    public void buscarPorFiltroUnidadesComContratosAtivos_deveDelegarParaDaoSemAlterarPaginacao() {
        FiltroUnidadePrisional filtro = new FiltroUnidadePrisional();
        filtro.setPrimeiroRegistro(20);
        filtro.setQuantidadeRegistros(10);
        List<UnidadePrisionalDTO> unidades = Collections.singletonList(new UnidadePrisionalDTO());
        when(dao.buscarPorFiltroUnidadesComContratosAtivos(filtro, false)).thenReturn(unidades);

        List<UnidadePrisionalDTO> resultado = service.buscarPorFiltroUnidadesComContratosAtivos(filtro, false);

        assertThat(resultado).isSameAs(unidades);
        assertThat(filtro.getPrimeiroRegistro()).isEqualTo(20);
        assertThat(filtro.getQuantidadeRegistros()).isEqualTo(10);
        verify(dao).buscarPorFiltroUnidadesComContratosAtivos(filtro, false);
    }

    @Test
    public void quantidadeFiltradosUnidadesComContratosAtivos_deveDelegarParaDao() {
        FiltroUnidadePrisional filtro = new FiltroUnidadePrisional();
        when(dao.quantidadeFiltradosUnidadesComContratosAtivos(filtro)).thenReturn(7);

        int resultado = service.quantidadeFiltradosUnidadesComContratosAtivos(filtro);

        assertThat(resultado).isEqualTo(7);
        verify(dao).quantidadeFiltradosUnidadesComContratosAtivos(filtro);
    }

    @Test
    public void listarNomesUnidadesComContratosAtivos_deveDelegarParametrosParaDao() {
        FiltroUnidadePrisional filtro = new FiltroUnidadePrisional();
        List<String> nomes = Collections.singletonList("Unidade A");
        when(dao.listarNomesUnidadesComContratosAtivos(filtro, "Uni", 20)).thenReturn(nomes);

        List<String> resultado = service.listarNomesUnidadesComContratosAtivos(filtro, "Uni", 20);

        assertThat(resultado).isSameAs(nomes);
        verify(dao).listarNomesUnidadesComContratosAtivos(filtro, "Uni", 20);
    }

    @Test
    public void listarSiglasUnidadesComContratosAtivos_deveDelegarParametrosParaDao() {
        FiltroUnidadePrisional filtro = new FiltroUnidadePrisional();
        List<String> siglas = Collections.singletonList("UP");
        when(dao.listarSiglasUnidadesComContratosAtivos(filtro, "UP", 20)).thenReturn(siglas);

        List<String> resultado = service.listarSiglasUnidadesComContratosAtivos(filtro, "UP", 20);

        assertThat(resultado).isSameAs(siglas);
        verify(dao).listarSiglasUnidadesComContratosAtivos(filtro, "UP", 20);
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
