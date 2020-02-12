package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.ProcessoRegimeCondicaoDAO;
import br.gov.mt.sejudh.sigepen.model.ProcessoRegimeCondicao;

@Stateless
public class ProcessoRegimeCondicaoService {

	@Inject
	private ProcessoRegimeCondicaoDAO dao;
	
	@Inject
	private RecuperandoService recuperandoService;
	
	public void salvar(ProcessoRegimeCondicao processoRegimeCondicao){
		dao.salvar(processoRegimeCondicao);
		recuperandoService.atualizarRegimeCondicaoAtualRecuperando(processoRegimeCondicao.getProcesso().getRecuperando());
	}
}
