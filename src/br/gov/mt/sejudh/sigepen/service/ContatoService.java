package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.ContatoDAO;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.VisitanteCertidao;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class ContatoService {

	@Inject
	private ContatoDAO contatoDao;

	public ContatoService() {
		
	}

	@Interceptors(EJBExcInterceptor.class)
	public Contato salvarContato(Contato contato){
		return contatoDao.salvar(contato);
	}
	
	public void excluirContato(Contato contato){
		contatoDao.excluir(contato);
	}

	public void excluirEndereco(Endereco endereco) {
		contatoDao.excluir(endereco);

	}

	public void excluirCertidao(VisitanteCertidao certidao) {
		contatoDao.excluir(certidao);
		
	}
	
	
}
