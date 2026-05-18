package br.gov.mt.sejudh.sigepen.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class MonetaryFormatUtilTest {

	@Test
	public void formatBrl_comValorNulo_deveRetornarZeroMonetario() {
		assertThat(MonetaryFormatUtil.formatBrl(null)).isEqualTo("R$ 0.00");
	}

	@Test
	public void formatBrl_comValorDecimal_deveRetornarValorComDuasCasas() {
		assertThat(MonetaryFormatUtil.formatBrl(12.3)).isEqualTo("R$ 12.30");
	}

	@Test
	public void roundTwoDecimals_comTresCasas_deveArredondarHalfUp() {
		assertThat(MonetaryFormatUtil.roundTwoDecimals(10.235)).isEqualTo(10.24);
	}

	@Test
	public void roundTwoDecimals_comValorNulo_deveRetornarNulo() {
		assertThat(MonetaryFormatUtil.roundTwoDecimals(null)).isNull();
	}
}
