package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.ProcessoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Condicao;
import br.gov.mt.sejudh.sigepen.enumerator.Modalidade;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class ProcessoService {

	@Inject
	private ProcessoDAO dao;

	@Interceptors(EJBExcInterceptor.class)
	public Processo salvar(Processo processo){
		dao.salvar(processo);
		return processo;
	}
	
	public void configurarModalidadeProcesso(Processo processo) {
		if (processo.getCondicaoAtual().equals(Condicao.PROVISORIO)) {
			long prevCount = processo.getProcessosRegimeCondicao().stream().filter(p -> (p.getModalidade()!= null && p.getModalidade().equals(Modalidade.PREVENTIVA))).count();
			long tempCount = processo.getProcessosRegimeCondicao().stream().filter(p -> (p.getModalidade()!= null && p.getModalidade().equals(Modalidade.TEMPORARIA))).count();
			
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
	
	public List<Processo> processosPorRecuperando(final long idRecuperando) {
		return dao.processosPorRecuperando(idRecuperando);
	}
}
