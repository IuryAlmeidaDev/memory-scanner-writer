package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.HistoricoRecuperandoDAO;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class HistoricoRecuperandoService {

	@Inject
	private HistoricoRecuperandoDAO dao;
	
	public HistoricoRecuperandoService(){
		
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public void salvar(HistoricoRecuperando historicoRecuperando){
		dao.salvar(historicoRecuperando);
	}
	
	public List<HistoricoRecuperando> listarHistoricoRecuperando(Long recuperandoId){
		return dao.listarHistoricoRecuperando(recuperandoId);
	}
}
