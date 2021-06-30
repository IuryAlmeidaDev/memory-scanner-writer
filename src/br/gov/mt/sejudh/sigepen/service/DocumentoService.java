package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.DocumentoDAO;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class DocumentoService {

	@Inject
	private DocumentoDAO documentoDao;

	public DocumentoService() {
		
	}

	@Interceptors(EJBExcInterceptor.class)
	public Documento salvarDocumento(Documento documento){
		return documentoDao.salvar(documento);
	}
	
	public void excluirDocumento(Documento documento){
		documentoDao.excluir(documento);
	}
}
