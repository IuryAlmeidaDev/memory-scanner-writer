package br.gov.mt.sejudh.sigepen.enumerator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;

public class UnidadeJumboTest extends BaseUnitTest {

    @Test
    public void todosIds_deveRetornarOsNoveIdsFixosDasUnidadesComJumbo() {
        List<Long> ids = UnidadeJumbo.todosIds();

        assertThat(ids)
                .hasSize(9)
                .containsExactlyInAnyOrder(2L, 3L, 7L, 8L, 13L, 19L, 51L, 54L, 58L);
    }

    @Test
    public void contemId_deveRetornarTrueParaIdsPresentesNoEnum() {
        assertThat(UnidadeJumbo.contemId(2L)).isTrue();
        assertThat(UnidadeJumbo.contemId(58L)).isTrue();
    }

    @Test
    public void contemId_deveRetornarFalseParaIdsForaDoEnum() {
        assertThat(UnidadeJumbo.contemId(1L)).isFalse();
        assertThat(UnidadeJumbo.contemId(999L)).isFalse();
    }

    @Test
    public void contemId_deveRetornarFalseParaNull() {
        assertThat(UnidadeJumbo.contemId(null)).isFalse();
    }
}
