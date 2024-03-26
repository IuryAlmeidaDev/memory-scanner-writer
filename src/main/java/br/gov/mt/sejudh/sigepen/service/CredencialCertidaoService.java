package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.CredencialCertidaoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.CredencialCertidao;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.VisitanteCertidao;

@Stateless
public class CredencialCertidaoService extends BaseService<CredencialCertidao, Long> {


	private static final long serialVersionUID = -687320532742086635L;
	
    private CredencialCertidaoDAO credencialCertidaoDAO;
    
    
    @Inject
    public void setCredencialCertidaoDAO(CredencialCertidaoDAO credencialCertidaoDAO) {
		this.credencialCertidaoDAO = credencialCertidaoDAO;
		this.setDao(credencialCertidaoDAO);
	}
   

    public void validarCertidoes(FaseDoCredenciamento faseDoCredenciamento) throws ValidacaoException {
        for (VisitanteCertidao certidao : faseDoCredenciamento.getCredenciamento().getVisitante().getCertidoes()) {
            CredencialCertidao credencialCertidao = new CredencialCertidao();
            credencialCertidao.setValidado(SimNao.getByBool(certidao.getValueValidado()));
            credencialCertidao.setFaseDoCredenciamento(faseDoCredenciamento);
            credencialCertidao.setVisitanteCertidao(certidao);

            super.salvar(credencialCertidao);
            
        }
    }
}
