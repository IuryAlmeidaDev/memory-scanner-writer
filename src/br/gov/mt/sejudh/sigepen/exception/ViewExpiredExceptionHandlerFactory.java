package br.gov.mt.sejudh.sigepen.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class ViewExpiredExceptionHandlerFactory extends ExceptionHandlerFactory {

	private ExceptionHandlerFactory factory;
	 
	public ViewExpiredExceptionHandlerFactory (ExceptionHandlerFactory factory) {
		this.factory = factory;
	}
 
	@Override
	public ExceptionHandler getExceptionHandler() {
		ExceptionHandler result = new ViewExpiredExceptionHandler(factory.getExceptionHandler());
		return result;
	}
}
