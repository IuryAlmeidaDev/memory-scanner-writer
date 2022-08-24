package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.CertidaoDAO;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Certidao;

@Stateless
public class CertidaoService extends BaseService<Certidao, Long> {


	private static final long serialVersionUID = 4073149482899792420L;

	private CertidaoDAO certidaoDAO;
	
	@Inject
	public void setCertidaoDAO(CertidaoDAO certidaoDAO) {
		this.certidaoDAO = certidaoDAO;
		this.setDao(certidaoDAO);
	}
	
	@Override
	public Certidao salvar(Certidao t) throws ValidacaoException {
		if(t.getId() == null) {
			List<Certidao> certidoes = this.certidaoDAO.buscarPorDescricao(t.getDescricao());
			if (certidoes.size() > 0) {
				throw new ValidacaoException("Certidão informada já cadastrada!");
			}
		}
		
		return super.salvar(t);
	}

}
