package br.gov.mt.sejudh.sigepen.util.criterion;

import org.hibernate.criterion.Criterion;

import br.gov.mt.sejudh.sigepen.util.criterion.ExpressaoFonetica.MatchModeFonetica;

/**
 * Classe para chamada de Hibernate Criterion customizadas.
 * 
 * @author ivanbatista
 * 
 */
public class Restricao {

	Restricao() {
		// nao pode ser instanciada
	}

	/**
	 * Custom Criterion para uso da funcao Fonetica no bando de dados. 
	 * 
	 * @param propertyName
	 *            , propriedade a ser fonetizada
	 * @param value
	 *            , valor a ser pesquisado
	 * @return Criterion
	 */
	public static Criterion fonetiza(String propertyName, String value) {
		return new ExpressaoFonetica(propertyName, value);
	}

	/**
	 * Custom Criterion para uso da funcao Fonetica no bando de dados. 
	 * 
	 * @param propertyName
	 *            , propriedade a ser fonetizada
	 * @param value
	 *            , valor a ser pesquisado
	 * @param matchMode
	 * 
	 * @return Criterion
	 */
	public static Criterion fonetiza(String propertyName, String value, MatchModeFonetica matchMode) {
		return new ExpressaoFonetica(propertyName, value, matchMode);
	}

}