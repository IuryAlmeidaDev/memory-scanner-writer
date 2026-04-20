package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.DietaEspecialDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroDietaEspecial;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
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

	    if (dietaEspecial == null) {
	        throw new ValidacaoException("Erro interno: dieta especial não inicializada.");
	    }

	    if (dietaEspecial.getRecuperando() == null || dietaEspecial.getRecuperando().getId() <= 0) {
	        throw new ValidacaoException("É necessário associar um recuperando antes de concluir o cadastro.");
	    }

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
	
	public int quantidadeFiltrados(FiltroDietaEspecial filtro) {
	    return dao.quantidadeFiltrados(filtro);
	}
	
	public void encerrarDietasAtivasDoRecuperando(long idRecuperando, Date dataFim) {
	    dao.encerrarDietasAtivasDoRecuperando(idRecuperando, dataFim);
	}
	
}
