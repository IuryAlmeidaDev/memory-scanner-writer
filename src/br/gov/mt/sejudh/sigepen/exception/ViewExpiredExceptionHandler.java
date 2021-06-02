package br.gov.mt.sejudh.sigepen.exception;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import br.gov.mt.sejudh.sigepen.util.Sigepen;

public class ViewExpiredExceptionHandler extends ExceptionHandlerWrapper {

	private ExceptionHandler wrapped;

    public ViewExpiredExceptionHandler (ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }
    
	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {
		Iterable<ExceptionQueuedEvent> events = getUnhandledExceptionQueuedEvents();
		
		for (Iterator<ExceptionQueuedEvent> iterator = events.iterator(); iterator.hasNext();) {
			ExceptionQueuedEvent event = iterator.next();
			ExceptionQueuedEventContext ctx = (ExceptionQueuedEventContext)event.getSource();
			Throwable e = ctx.getException();
			
			if (e instanceof ViewExpiredException) {
				FacesContext context = FacesContext.getCurrentInstance();

				try {
					context.getExternalContext().redirect(Sigepen.APPLICATION_LOGIN_URL);
					context.getPartialViewContext().setRenderAll(true);
					context.renderResponse();
				} catch (Exception exc) {
					
				} finally {
					iterator.remove();
				}
			}
		}
		
		getWrapped().handle();
	}
}
