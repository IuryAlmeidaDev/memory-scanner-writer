package br.gov.mt.sejudh.sigepen.util;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.OptimisticLockException;

import br.gov.mt.sejudh.sigepen.exception.RegistroJaAtualizadoException;

public class EJBExcInterceptor {

	//@AroundInvoke
	public Object intercept(InvocationContext context) throws Exception {
		Object result = null;
		
		try {
			result = context.proceed();
		} catch (OptimisticLockException e) {
			throw new RegistroJaAtualizadoException("Este registro foi alterado, atualize a página e repita a operação.");
		}
		
		return result;
	}
}
