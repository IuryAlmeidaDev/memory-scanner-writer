package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.PlantaoDAO;
import br.gov.mt.sejudh.sigepen.model.Plantao;

import java.util.List;

@Stateless
public class PlantaoService extends BaseService<Plantao, Long>{

	private static final long serialVersionUID = 1L;
	
	
	@Inject
	private PlantaoDAO dao;
	
	
	@Inject
	public void setPlantaoDao(PlantaoDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

	public Plantao buscarUltimoPlantao() {
		return dao.buscarUltimoPlantao();
	}

	@Override
	public Plantao buscarPorId(Long id) {
		return dao.buscarPorId(id);
	}

}
