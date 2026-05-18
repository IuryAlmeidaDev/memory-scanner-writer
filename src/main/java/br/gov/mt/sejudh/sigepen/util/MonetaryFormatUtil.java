package br.gov.mt.sejudh.sigepen.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public final class MonetaryFormatUtil {

	private MonetaryFormatUtil() {
	}

	public static String formatBrl(Double value) {
		double normalized = value == null ? 0D : roundTwoDecimals(value);
		return String.format(Locale.US, "R$ %.2f", normalized);
	}

	public static Double roundTwoDecimals(Double value) {
		if (value == null) {
			return null;
		}

		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
}
