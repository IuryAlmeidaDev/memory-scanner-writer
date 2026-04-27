package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.Parametro;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class RolloutAlimentacaoServiceTest extends BaseUnitTest {

    @InjectMocks
    private RolloutAlimentacaoService rolloutAlimentacaoService;

    @Mock
    private ParametroService parametroService;

    @Test
    public void devePermitirTodasUnidadesQuandoParametroNaoExiste() {
        when(parametroService.getParametrosSistema()).thenReturn(Collections.emptyList());

        assertThat(rolloutAlimentacaoService.unidadeHabilitada(10L)).isTrue();
    }

    @Test
    public void devePermitirSomenteUnidadesDaWhitelist() {
        Parametro parametro = new Parametro(1L, RolloutAlimentacaoService.PARAM_ROLLOUT_UNIDADES, "10,20");
        when(parametroService.getParametrosSistema()).thenReturn(Arrays.asList(parametro));

        assertThat(rolloutAlimentacaoService.unidadeHabilitada(10L)).isTrue();
        assertThat(rolloutAlimentacaoService.unidadeHabilitada(99L)).isFalse();
    }
}
