package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.ProcessoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.model.Processo;

@Stateless
public class ProcessoService {

	@Inject
	private ProcessoDAO dao;

	public Processo salvar(Processo processo){
		dao.salvar(processo);
		return processo;
	}
	
	public void configurarModalidadeProcesso(Processo processo) {
		if (processo.getCondicaoAtual().equals(Condicao.PROVISORIO)) {
			long prevCount = processo.getProcessosRegimeCondicao().stream().filter(p -> p.getModalidade().equals(Modalidade.PREVENTIVA)).count();
			long tempCount = processo.getProcessosRegimeCondicao().stream().filter(p -> p.getModalidade().equals(Modalidade.TEMPORARIA)).count();
			
			if (prevCount > 0) {
				processo.setModalidade(Modalidade.PREVENTIVA);
			} else if (tempCount > 0) {
				processo.setModalidade(Modalidade.TEMPORARIA);
			}
		} else {
			processo.setModalidade(null);
		}
	}
	
	public Processo buscaProcessoPorId(Processo processo){
		return dao.buscaProcessoPorId(processo);
	}
}
