package br.gov.mt.sejudh.sigepen.util.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;

/**
 * Custom Criterion para uso da funcao Fonetica no bando de dados. </p> 
 * 
 * @author ivanbatista
 * 
 */

public class ExpressaoFonetica implements Criterion {
	
	public enum MatchModeFonetica{
		EXACT {
			@Override
			public String toMatchString(String pattern) {
				return pattern;
			}
		},
		START {
			@Override
			public String toMatchString(String pattern) {
				return pattern + " || '%'";
			}
		},
		END {
			@Override
			public String toMatchString(String pattern) {
				return "'%' || " + pattern;
			}
		},
		ANYWHERE {
			@Override
			public String toMatchString(String pattern) {
				return "'%' || " + pattern + " || '%'";
			}
		};
		public abstract String toMatchString(String pattern);
	}

	private static final long serialVersionUID = -972083935965090747L;
	private String propertyName;
	private String value;
	private MatchModeFonetica matchMode;

	protected ExpressaoFonetica(String propertyName, String value) {
		this.propertyName = propertyName;
		this.value = value;
	}

	protected ExpressaoFonetica(String propertyName, String value, MatchModeFonetica matchMode) {
		this.propertyName = propertyName;
		this.value = value;
		this.matchMode = matchMode;

	}

	@Override
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		final String[] columns = criteriaQuery.getColumnsUsingProjection(criteria, this.propertyName);
		String nomeFuncao = " FONETIZAR(?) ";
		
		if(this.matchMode != null) {
			nomeFuncao = this.matchMode.toMatchString(nomeFuncao);
		}
		
		String queryFragment = columns[0] + " like " + nomeFuncao;
		return queryFragment;
	}

	@Override
	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		return new TypedValue[] { criteriaQuery.getTypedValue(criteria, this.propertyName, this.value.toLowerCase())};
	}

}