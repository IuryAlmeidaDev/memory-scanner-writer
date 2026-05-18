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
import br.gov.mt.sejudh.sigepen.service.AlimentoService;

public class AlimentoBeanTest extends BaseUnitTest {

    @Mock
    private AlimentoService service;

    @InjectMocks
    private AlimentoBean bean;

    @Test
    public void completarDescricoesAlimentosFiltro_deveBuscarComConsultaELimite() {
        when(service.listarDescricoesParaFiltro("Ar", 20)).thenReturn(Arrays.asList("Arroz", "Arroz integral"));

        java.util.List<String> resultado = bean.completarDescricoesAlimentosFiltro(" Ar ");

        assertThat(resultado).containsExactly("Arroz", "Arroz integral");
        verify(service).listarDescricoesParaFiltro("Ar", 20);
    }

    @Test
    public void completarDescricoesAlimentosFiltro_consultaCurtaNaoBuscaBanco() {
        java.util.List<String> resultado = bean.completarDescricoesAlimentosFiltro("A");

        assertThat(resultado).isEmpty();
        verify(service, never()).listarDescricoesParaFiltro(
                org.mockito.Mockito.anyString(), org.mockito.Mockito.anyInt());
    }
}
