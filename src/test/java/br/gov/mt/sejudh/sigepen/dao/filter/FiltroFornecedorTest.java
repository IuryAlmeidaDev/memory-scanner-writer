package br.gov.mt.sejudh.sigepen.dao.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class FiltroFornecedorTest {

    @Test
    public void setCnpj_deveNormalizarPontuacaoParaBuscaParcial() {
        FiltroFornecedor filtro = new FiltroFornecedor();

        filtro.setCnpj(" 12.345/ab ");

        assertThat(filtro.getCnpj()).isEqualTo("12345AB");
    }

    @Test
    public void setCnpj_devePreservarNulo() {
        FiltroFornecedor filtro = new FiltroFornecedor();

        filtro.setCnpj(null);

        assertThat(filtro.getCnpj()).isNull();
    }
}
