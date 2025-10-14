package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.PlantaoVisitaDAO;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.model.*;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class PlantaoVisitaService extends BaseService<PlantaoVisita, Long>{

	private static final long serialVersionUID = 1L;
	
	
	@Inject
	private PlantaoVisitaDAO dao;

	@Inject
	public void setPlantaoVisitaDAO(PlantaoVisitaDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	public PlantaoVisita buscarPlantaoVisita(Long idPlantao , TipoEntrada tipoEntrada) {
		return dao.buscarPlantaoVisita(idPlantao,tipoEntrada);
	}

}
