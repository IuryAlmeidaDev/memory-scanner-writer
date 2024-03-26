package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.DietaEspecialDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroDietaEspecial;
import br.gov.mt.sejudh.sigepen.model.DietaRecuperando;
import br.gov.mt.sejudh.sigepen.model.dto.DietaEspecialDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class DietaEspecialService {

	@Inject
	private DietaEspecialDAO dao;
	
	@Inject
	private AuditoriaDAO auditDao;

	@Interceptors(EJBExcInterceptor.class)
	public DietaRecuperando salvar(DietaRecuperando dietaEspecial) throws Exception {
		return dao.salvar(dietaEspecial);
	}

	public DietaRecuperando buscaPorId(long id) {
		DietaRecuperando dietaEspecial = dao.buscaPorId(id);
		if (dietaEspecial != null) {
			dietaEspecial.setAutorRegistro(auditDao.autorRegistro(dietaEspecial, dietaEspecial.getId()));
			dietaEspecial.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(dietaEspecial, dietaEspecial.getId()));
		}
		return dietaEspecial;
	}

	public List<DietaEspecialDTO> buscarPorFiltro(FiltroDietaEspecial filtro) {
		return dao.buscaPorFiltro(filtro);
	}

	public List<DietaRecuperando> buscarbuscarPorTipoDieta(Long tipoDietaId){
		return dao.buscarPorTipoDieta(tipoDietaId);
	}
	
}
