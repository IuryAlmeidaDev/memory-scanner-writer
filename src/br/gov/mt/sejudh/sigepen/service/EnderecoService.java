package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.EnderecoDAO;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class EnderecoService {

	@Inject
	private EnderecoDAO endDao;

	public EnderecoService() {
		
	}

	@Interceptors(EJBExcInterceptor.class)
	public Endereco salvarEndereco(Endereco endereco){
		return endDao.salvar(endereco);
	}
	
	public void excluirEndereco(Endereco endereco){
		endDao.excluir(endereco);
	}
}
