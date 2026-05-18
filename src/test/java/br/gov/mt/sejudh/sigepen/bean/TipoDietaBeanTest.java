package br.gov.mt.sejudh.sigepen.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import br.gov.mt.sejudh.sigepen.BaseUnitTest;
import br.gov.mt.sejudh.sigepen.service.TipoDietaService;

public class TipoDietaBeanTest extends BaseUnitTest {

    @Mock
    private TipoDietaService service;

    @InjectMocks
    private TipoDietaBean bean;

    @Test
    public void completarDescricoesTiposDietaFiltro_deveBuscarComConsultaELimite() {
        when(service.listarDescricoesParaFiltro("Bra", 20)).thenReturn(Arrays.asList("Branda", "Branda especial"));

        java.util.List<String> resultado = bean.completarDescricoesTiposDietaFiltro(" Bra ");

        assertThat(resultado).containsExactly("Branda", "Branda especial");
        verify(service).listarDescricoesParaFiltro("Bra", 20);
    }

    @Test
    public void completarDescricoesTiposDietaFiltro_consultaCurtaNaoBuscaBanco() {
        java.util.List<String> resultado = bean.completarDescricoesTiposDietaFiltro("B");

        assertThat(resultado).isEmpty();
        verify(service, never()).listarDescricoesParaFiltro(
                org.mockito.Mockito.anyString(), org.mockito.Mockito.anyInt());
    }
}
