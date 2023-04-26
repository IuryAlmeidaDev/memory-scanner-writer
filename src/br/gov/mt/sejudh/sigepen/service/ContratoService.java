package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.ContratoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroContrato;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class ContratoService {
	
	@Inject
	private ContratoDAO dao;
	
	@Interceptors(EJBExcInterceptor.class)
	public Contrato salvar(Contrato contrato) throws Exception {
		
		return dao.salvar(contrato);
	}

	public Contrato buscaPorId(long id) {
		Contrato contrato = dao.buscaPorId(id);
		
		return contrato;
	}

	public int quantidadeFiltrados(FiltroContrato filtro) {
		return dao.quantidadeFiltrados(filtro);
	}

	public List<ContratoDTO> buscarPorFiltro(FiltroContrato filtro) {
		return dao.buscaPorFiltro(filtro);
	}
	
}
