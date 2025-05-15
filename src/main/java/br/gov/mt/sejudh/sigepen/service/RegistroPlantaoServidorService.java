package br.gov.mt.sejudh.sigepen.service;


import javax.ejb.Stateless;
import javax.inject.Inject;
import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.RegistroPlantaoServidorDAO;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantaoServidor;


@Stateless
public class RegistroPlantaoServidorService extends BaseService<RegistroPlantaoServidor, Long>{

	private static final long serialVersionUID = 1L;

	@Inject
	private RegistroPlantaoServidorDAO dao;
	
	@Inject
	public void setRegistroPlantaoServidorDAO(RegistroPlantaoServidorDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}
	
	
	
	/* ------------------------  Métodos  ------------------------ */
	
	
	public RegistroPlantaoServidor salvar(RegistroPlantaoServidor registroPlantaoServidor) {
        return dao.salvar(registroPlantaoServidor);
    }
	
	
	
}
