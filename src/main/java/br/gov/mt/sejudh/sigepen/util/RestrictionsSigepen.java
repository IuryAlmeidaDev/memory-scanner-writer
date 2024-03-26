package br.gov.mt.sejudh.sigepen.util;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;

public class RestrictionsSigepen  {

	public static Criterion ignoraAcentoLike(String propertyName, String value, MatchMode mode) {
		return new IgnoraAcentoCriterion(propertyName, value, mode);
	}
}
