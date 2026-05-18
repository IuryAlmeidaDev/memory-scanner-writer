package br.gov.mt.sejudh.sigepen.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;

public class PermissaoUtilTest extends BaseUnitTest {

    @Mock
    private FacesUtil facesUtil;
    @Mock
    private SecurityUtil security;

    @InjectMocks
    private PermissaoUtil permissaoUtil;

    @Test
    public void filtrarUnidadesComBaseNaPermissaoListarTodasRecuperandos_semPermissaoCriaFiltroRestrito() {
        when(security.possuiPermissao("ListarTodosRecuperandos")).thenReturn(false);

        permissaoUtil.filtrarUnidadesComBaseNaPermissaoListarTodasRecuperandos(true);

        ArgumentCaptor<Object> filtroCaptor = ArgumentCaptor.forClass(Object.class);
        verify(facesUtil).setObjetoSessao(eq("filtroRecuperando"), filtroCaptor.capture());
        FiltroRecuperandoView filtro = (FiltroRecuperandoView) filtroCaptor.getValue();
        assertThat(filtro.getListarTodasUnidades()).isFalse();
        assertThat(filtro.getIsDietaEspecial()).isTrue();
    }

    @Test
    public void filtrarUnidadesComBaseNaPermissaoListarTodasRecuperandos_comPermissaoCriaFiltroAmplo() {
        when(security.possuiPermissao("ListarTodosRecuperandos")).thenReturn(true);

        permissaoUtil.filtrarUnidadesComBaseNaPermissaoListarTodasRecuperandos(true);

        ArgumentCaptor<Object> filtroCaptor = ArgumentCaptor.forClass(Object.class);
        verify(facesUtil).setObjetoSessao(eq("filtroRecuperando"), filtroCaptor.capture());
        FiltroRecuperandoView filtro = (FiltroRecuperandoView) filtroCaptor.getValue();
        assertThat(filtro.getListarTodasUnidades()).isTrue();
        assertThat(filtro.getIsDietaEspecial()).isTrue();
    }
}
