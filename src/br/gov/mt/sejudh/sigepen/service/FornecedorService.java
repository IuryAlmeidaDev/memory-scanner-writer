package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.FornecedorDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFornecedor;
import br.gov.mt.sejudh.sigepen.model.Fornecedor;
import br.gov.mt.sejudh.sigepen.model.dto.FornecedorDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class FornecedorService {
	
	@Inject
	private FornecedorDAO dao;
	
	@Interceptors(EJBExcInterceptor.class)
	public Fornecedor salvar(Fornecedor fornecedor) throws Exception {
		
		return dao.salvar(fornecedor);
	}

	public Fornecedor buscaPorId(long id) {
		Fornecedor fornecedor = dao.buscaPorId(id);
		
		return fornecedor;
	}

	public int quantidadeFiltrados(FiltroFornecedor filtro) {
		return dao.quantidadeFiltrados(filtro);
	}

	public List<FornecedorDTO> buscarPorFiltro(FiltroFornecedor filtro) {
		return dao.buscaPorFiltro(filtro);
	}
	
}
