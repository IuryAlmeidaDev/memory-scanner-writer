package br.gov.mt.sejudh.sigepen.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class ValidacaoException extends Exception {

	private static final long serialVersionUID = 1948244380277610457L;

	public ValidacaoException(String message) {
		super(message);
	}
}
