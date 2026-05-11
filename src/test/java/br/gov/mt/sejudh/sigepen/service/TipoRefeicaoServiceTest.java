package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.TipoRefeicaoDAO;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class TipoRefeicaoServiceTest extends BaseUnitTest {

    @Mock
    private TipoRefeicaoDAO dao;

    @InjectMocks
    private TipoRefeicaoService service;

    @Test
    public void deveRetornarTrueQuandoListaTiverTodasAsRefeicoesMesmoComOrdemDiferente() {
        TipoRefeicao cafe = tipo(1L, "Cafe");
        TipoRefeicao almoco = tipo(2L, "Almoco");
        TipoRefeicao janta = tipo(3L, "Janta");

        when(dao.listarTodos()).thenReturn(Arrays.asList(cafe, almoco, janta));

        boolean resultado = service.temTodasRefeicoes(Arrays.asList(janta, cafe, almoco));

        assertThat(resultado).isTrue();
    }

    @Test
    public void deveRetornarFalseQuandoFaltarRefeicao() {
        TipoRefeicao cafe = tipo(1L, "Cafe");
        TipoRefeicao almoco = tipo(2L, "Almoco");
        TipoRefeicao janta = tipo(3L, "Janta");

        when(dao.listarTodos()).thenReturn(Arrays.asList(cafe, almoco, janta));

        boolean resultado = service.temTodasRefeicoes(Arrays.asList(cafe, almoco));

        assertThat(resultado).isFalse();
    }

    @Test
    public void deveRetornarFalseQuandoListaForNula() {
        when(dao.listarTodos()).thenReturn(Collections.singletonList(tipo(1L, "Cafe")));

        boolean resultado = service.temTodasRefeicoes(null);

        assertThat(resultado).isFalse();
    }

    private TipoRefeicao tipo(Long id, String descricao) {
        TipoRefeicao tipo = new TipoRefeicao();
        tipo.setId(id);
        tipo.setDescricao(descricao);
        return tipo;
    }
}
