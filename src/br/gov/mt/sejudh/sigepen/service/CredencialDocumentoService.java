package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.CredencialDocumentoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.CredencialDocumento;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.VisitanteDocumento;

@Stateless
public class CredencialDocumentoService extends BaseService<CredencialDocumento, Long> {

	private static final long serialVersionUID = -4536521509748631563L;

	private CredencialDocumentoDAO credencialDocumentoDAO;

	@Inject
	public void setCredencialDocumentoDAO(CredencialDocumentoDAO credencialDocumentoDAO) {
		this.credencialDocumentoDAO = credencialDocumentoDAO;
		this.setDao(credencialDocumentoDAO);
	}

	public void validarDocumentos(FaseDoCredenciamento faseDoCredenciamento) throws ValidacaoException {
		for (VisitanteDocumento documento : faseDoCredenciamento.getCredenciamento().getVisitante()
				.getVisitanteDocumentos()) {
			CredencialDocumento credencialDocumento = new CredencialDocumento();
			credencialDocumento.setValidado(SimNao.getByBool(documento.getValueValidado()));
			credencialDocumento.setFaseDoCredenciamento(faseDoCredenciamento);
			credencialDocumento.setVisitanteDocumento(documento);

			super.salvar(credencialDocumento);
			
		}
	}
}
