package br.gov.mt.sejudh.sigepen.util;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.engine.spi.TypedValue;

	public class IgnoraAcentoCriterion implements Criterion {

		private static final long serialVersionUID = 1L;
		
		private String propertyName;
		private String value;
		
		public IgnoraAcentoCriterion(String propertyName, String value, MatchMode mode) {
			this.propertyName = propertyName;
			this.value = mode.toMatchString(value);
		}
		
		@Override
		public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
			return new TypedValue[] { criteriaQuery.getTypedValue(criteria, this.propertyName, this.value.toLowerCase())};
		}

		@Override
		public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
			final String[] columns = criteriaQuery.findColumns(this.propertyName, criteria);
			return "translate(lower(" + columns[0] + "), 'àáâãäèéêëìíîïòóôõöùúûüç', 'aaaaaeeeeiiiiooooouuuuc') like translate(lower(?), 'ç', 'c')";
		}
	}