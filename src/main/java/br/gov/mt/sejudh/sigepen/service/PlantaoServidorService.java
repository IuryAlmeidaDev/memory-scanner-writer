package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.PlantaoDAO;
import br.gov.mt.sejudh.sigepen.dao.PlantaoServidorDAO;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.PlantaoServidor;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class PlantaoServidorService extends BaseService<PlantaoServidor, Long>{

	private static final long serialVersionUID = 1L;
	
	
	@Inject
	private PlantaoServidorDAO dao;
	
	
	@Inject
	public void setPlantaoServidorDAO(PlantaoServidorDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}


}
