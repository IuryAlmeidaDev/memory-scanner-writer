package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.service.ContratoService;
import br.gov.mt.sejudh.sigepen.service.TipoDietaService;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

public class CardapioBeanTest extends BaseUnitTest {

    @Mock
    private TipoRefeicaoService tipoRefeicaoService;
    @Mock
    private TipoDietaService tipoDietaService;
    @Mock
    private ContratoService contratoService;
    @Mock
    private SecurityUtil securityUtil;

    @Spy
    @InjectMocks
    private CardapioBean bean;

    @Test
    public void carregarListas_semPermissaoListaContratosAtivosDaUnidadeAtual() throws Exception {
        UnidadePrisional unidade = new UnidadePrisional(22L);
        Contrato contrato = new Contrato();
        doReturn(unidade).when(bean).getUnidadeSelecionadaAtual();
        when(securityUtil.possuiPermissao("ListarTodasUnidades")).thenReturn(false);
        when(tipoRefeicaoService.listarTodos()).thenReturn(Collections.emptyList());
        when(tipoDietaService.listarAtivas()).thenReturn(Collections.emptyList());
        when(contratoService.listarAtivosParaFiltro(false, 22L)).thenReturn(Arrays.asList(contrato));

        invocarCarregarListas();

        verify(contratoService).listarAtivosParaFiltro(false, 22L);
        assertThat(bean.getContratos()).containsExactly(contrato);
        assertThat(bean.getUnidadesPrisionais()).containsExactly(unidade);
    }

    @Test
    public void carregarListas_comPermissaoListaTodosContratosAtivos() throws Exception {
        Contrato contrato = new Contrato();
        doReturn(null).when(bean).getUnidadeSelecionadaAtual();
        when(securityUtil.possuiPermissao("ListarTodasUnidades")).thenReturn(true);
        when(tipoRefeicaoService.listarTodos()).thenReturn(Collections.emptyList());
        when(tipoDietaService.listarAtivas()).thenReturn(Collections.emptyList());
        when(contratoService.listarAtivosParaFiltro(true, null)).thenReturn(Arrays.asList(contrato));

        invocarCarregarListas();

        verify(contratoService).listarAtivosParaFiltro(true, null);
        assertThat(bean.getContratos()).containsExactly(contrato);
        assertThat(bean.getUnidadesPrisionais()).isEmpty();
    }

    private void invocarCarregarListas() throws Exception {
        Method method = CardapioBean.class.getDeclaredMethod("carregarListas");
        method.setAccessible(true);
        method.invoke(bean);
    }
}
