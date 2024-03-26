package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.ProcessoRegimeCondicaoDAO;
import br.gov.mt.sejudh.sigepen.model.ProcessoRegimeCondicao;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class ProcessoRegimeCondicaoService {

	@Inject
	private ProcessoRegimeCondicaoDAO dao;
	
	@Inject
	private RecuperandoService recuperandoService;
	
	@Interceptors(EJBExcInterceptor.class)
	public void salvar(ProcessoRegimeCondicao processoRegimeCondicao){
		dao.salvar(processoRegimeCondicao);
		recuperandoService.atualizarRegimeCondicaoAtualRecuperando(processoRegimeCondicao.getProcesso().getRecuperando());
	}
}
