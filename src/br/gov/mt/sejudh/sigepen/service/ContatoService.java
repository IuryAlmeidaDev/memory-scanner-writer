package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.ContatoDAO;
import br.gov.mt.sejudh.sigepen.model.Contato;

@Stateless
public class ContatoService {

	@Inject
	private ContatoDAO contatoDao;

	public ContatoService() {
		
	}

	public Contato salvarContato(Contato contato){
		return contatoDao.salvar(contato);
	}
	
	public void excluirContato(Contato contato){
		contatoDao.excluir(contato);
	}
}
