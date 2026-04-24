package br.gov.mt.sejudh.sigepen.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.RelatorioJumboDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioJumbo;
import br.gov.mt.sejudh.sigepen.enumerator.UnidadeJumbo;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioJumboDTO;

public class RelatorioJumboServiceTest extends BaseUnitTest {

    @Mock
    private RelatorioJumboDAO dao;

    @Mock
    private UnidadePrisionalService unidadeService;

    @InjectMocks
    private RelatorioJumboService service;

    @Before
    public void stubUnidadeServiceParaTodasAtivas() {
        when(unidadeService.listarPorIds(UnidadeJumbo.todosIds())).thenReturn(todasAsUnidadesJumbo());
    }

    private List<UnidadePrisional> todasAsUnidadesJumbo() {
        return Arrays.asList(
                new UnidadePrisional(2L, "Unidade 2"),
                new UnidadePrisional(3L, "Unidade 3"),
                new UnidadePrisional(7L, "Unidade 7"),
                new UnidadePrisional(8L, "Unidade 8"),
                new UnidadePrisional(13L, "Unidade 13"),
                new UnidadePrisional(19L, "Unidade 19"),
                new UnidadePrisional(51L, "Unidade 51"),
                new UnidadePrisional(54L, "Unidade 54"),
                new UnidadePrisional(58L, "Unidade 58"));
    }

    @Test
    public void buscar_deveLancarExcecaoQuandoFiltroForNull() {
        assertThatThrownBy(() -> service.buscar(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Filtro");

        verify(dao, never()).buscar(any());
    }

    @Test
    public void buscar_deveLancarExcecaoQuandoUnidadesForNull() {
        FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();
        filtro.setUnidadesSelecionadas(null);

        assertThatThrownBy(() -> service.buscar(filtro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ao menos uma unidade");

        verify(dao, never()).buscar(any());
    }

    @Test
    public void buscar_deveLancarExcecaoQuandoUnidadesForVazia() {
        FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();
        filtro.setUnidadesSelecionadas(Collections.emptyList());

        assertThatThrownBy(() -> service.buscar(filtro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ao menos uma unidade");

        verify(dao, never()).buscar(any());
    }

    @Test
    public void buscar_deveLancarExcecaoQuandoHouverIdForaDoEnum() {
        FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();
        filtro.setUnidadesSelecionadas(Arrays.asList(2L, 999L));

        assertThatThrownBy(() -> service.buscar(filtro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fora da lista");

        verify(dao, never()).buscar(any());
    }

    @Test
    public void buscar_deveLancarExcecaoQuandoHouverIdNullNaLista() {
        FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();
        filtro.setUnidadesSelecionadas(Arrays.asList(2L, null));

        assertThatThrownBy(() -> service.buscar(filtro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fora da lista");

        verify(dao, never()).buscar(any());
    }

    @Test
    public void buscar_deveLancarExcecaoQuandoUnidadeNaoEstaCadastradaNoBanco() {
        when(unidadeService.listarPorIds(UnidadeJumbo.todosIds())).thenReturn(Arrays.asList(
                new UnidadePrisional(2L, "Unidade 2"),
                new UnidadePrisional(7L, "Unidade 7")));

        FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();
        filtro.setUnidadesSelecionadas(Arrays.asList(2L, 3L));

        assertThatThrownBy(() -> service.buscar(filtro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não está cadastrada");

        verify(dao, never()).buscar(any());
    }

    @Test
    public void buscar_deveDelegarAoDaoQuandoFiltroForSubconjuntoValido() {
        FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();
        filtro.setUnidadesSelecionadas(Arrays.asList(2L, 7L));

        List<RelatorioJumboDTO> esperado = Collections.singletonList(
                new RelatorioJumboDTO(123L, "Fulano", "Mae", "Unidade X", "Cela 1"));
        when(dao.buscar(filtro)).thenReturn(esperado);

        List<RelatorioJumboDTO> resultado = service.buscar(filtro);

        assertThat(resultado).isSameAs(esperado);
        verify(dao).buscar(filtro);
    }

    @Test
    public void buscar_deveDelegarAoDaoQuandoFiltroTiverTodasAsNoveUnidades() {
        FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();
        filtro.setUnidadesSelecionadas(Arrays.asList(2L, 3L, 7L, 8L, 13L, 19L, 51L, 54L, 58L));

        when(dao.buscar(filtro)).thenReturn(Collections.emptyList());

        List<RelatorioJumboDTO> resultado = service.buscar(filtro);

        assertThat(resultado).isEmpty();
        verify(dao).buscar(filtro);
    }

    @Test
    public void buscarRaw_deveLancarExcecaoQuandoFiltroForNull() {
        assertThatThrownBy(() -> service.buscarRaw(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Filtro");

        verify(dao, never()).buscarRaw(any());
    }

    @Test
    public void buscarRaw_deveLancarExcecaoQuandoUnidadesForVazia() {
        FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();
        filtro.setUnidadesSelecionadas(Collections.emptyList());

        assertThatThrownBy(() -> service.buscarRaw(filtro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ao menos uma unidade");

        verify(dao, never()).buscarRaw(any());
    }

    @Test
    public void buscarRaw_deveLancarExcecaoQuandoHouverIdForaDoEnum() {
        FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();
        filtro.setUnidadesSelecionadas(Arrays.asList(2L, 999L));

        assertThatThrownBy(() -> service.buscarRaw(filtro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fora da lista");

        verify(dao, never()).buscarRaw(any());
    }

    @Test
    public void buscarRaw_deveDelegarAoDaoQuandoFiltroForValido() {
        FiltroRelatorioJumbo filtro = new FiltroRelatorioJumbo();
        filtro.setUnidadesSelecionadas(Arrays.asList(2L, 7L));

        List<RelatorioJumboDTO> esperado = Collections.singletonList(
                new RelatorioJumboDTO(123L, "FULANO", "MAE", "UNIDADE X", "CELA 1"));
        when(dao.buscarRaw(filtro)).thenReturn(esperado);

        List<RelatorioJumboDTO> resultado = service.buscarRaw(filtro);

        assertThat(resultado).isSameAs(esperado);
        verify(dao).buscarRaw(filtro);
        verify(dao, never()).buscar(any());
    }
}
