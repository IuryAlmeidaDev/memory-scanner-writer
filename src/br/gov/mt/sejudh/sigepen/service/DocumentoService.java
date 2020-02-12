package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.DocumentoDAO;
import br.gov.mt.sejudh.sigepen.model.Documento;

@Stateless
public class DocumentoService {

	@Inject
	private DocumentoDAO documentoDao;

	public DocumentoService() {
		
	}

	public Documento salvarDocumento(Documento documento){
		return documentoDao.salvar(documento);
	}
	
	public void excluirDocumento(Documento documento){
		documentoDao.excluir(documento);
	}
}
