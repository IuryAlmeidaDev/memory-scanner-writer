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
import br.gov.mt.sejudh.sigepen.service.FornecedorService;

public class FornecedorBeanTest extends BaseUnitTest {

    @Mock
    private FornecedorService fornecedorService;

    @InjectMocks
    private FornecedorBean bean;

    @Test
    public void completarRazoesSociaisFiltro_deveBuscarComConsultaELimite() {
        when(fornecedorService.listarRazoesSociaisParaFiltro("Raz", 20))
                .thenReturn(Arrays.asList("Razao A", "Razao B"));

        java.util.List<String> resultado = bean.completarRazoesSociaisFiltro(" Raz ");

        assertThat(resultado).containsExactly("Razao A", "Razao B");
        verify(fornecedorService).listarRazoesSociaisParaFiltro("Raz", 20);
    }

    @Test
    public void completarNomesFantasiaFiltro_deveBuscarComConsultaELimite() {
        when(fornecedorService.listarNomesFantasiaParaFiltro("Fan", 20))
                .thenReturn(Arrays.asList("Fantasia A", "Fantasia B"));

        java.util.List<String> resultado = bean.completarNomesFantasiaFiltro(" Fan ");

        assertThat(resultado).containsExactly("Fantasia A", "Fantasia B");
        verify(fornecedorService).listarNomesFantasiaParaFiltro("Fan", 20);
    }

    @Test
    public void completarFornecedoresFiltro_consultaCurtaNaoBuscaBanco() {
        assertThat(bean.completarRazoesSociaisFiltro("R")).isEmpty();
        assertThat(bean.completarNomesFantasiaFiltro("F")).isEmpty();
        verify(fornecedorService, never()).listarRazoesSociaisParaFiltro(
                org.mockito.Mockito.anyString(), org.mockito.Mockito.anyInt());
        verify(fornecedorService, never()).listarNomesFantasiaParaFiltro(
                org.mockito.Mockito.anyString(), org.mockito.Mockito.anyInt());
    }
}
