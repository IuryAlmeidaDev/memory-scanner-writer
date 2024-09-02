package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.CrimeDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCrime;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Crime;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class CrimeService {

	@Inject
	private CrimeDAO dao;
	
	@Interceptors(EJBExcInterceptor.class)
	public void salvar(Crime crime) {		
		dao.salvar(crime);
	}

	public List<Crime> buscaPorFiltro(FiltroCrime filtro, boolean isBuscaRelatorio) {		 
		return dao.buscaPorFiltro(filtro, isBuscaRelatorio);
    }

	public int quantidadeFiltrados(FiltroCrime filtro) {
		return dao.quantidadeFiltrados(filtro);
	}
	
	public List<String> listarLegislacoes() {
		return dao.listarLegislacoes();
	}		
	
	public List<String> listarArtigos(String legislacao) {
		return dao.listarArtigos(legislacao);
	}

	public Crime buscarPorId(long id) {
		return dao.buscarPorId(id);
	}
	
	public void excluir(long crimeId) throws ValidacaoException {
		dao.excluir(crimeId);		
	}
		
}
