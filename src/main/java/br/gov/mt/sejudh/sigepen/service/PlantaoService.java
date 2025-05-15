package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import org.hibernate.metamodel.domain.Superclass;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.OrgaoDAO;
import br.gov.mt.sejudh.sigepen.dao.PlantaoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroOrgao;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPLantao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Orgao;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

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
	
		

	public List<Plantao> listarPlantaoAtivo() {
		return dao.listarPlantaoAtivo();
	}

	

}
