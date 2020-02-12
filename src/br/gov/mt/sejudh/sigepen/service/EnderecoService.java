package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.EnderecoDAO;
import br.gov.mt.sejudh.sigepen.model.Endereco;

@Stateless
public class EnderecoService {

	@Inject
	private EnderecoDAO endDao;

	public EnderecoService() {
		
	}

	public Endereco salvarEndereco(Endereco endereco){
		return endDao.salvar(endereco);
	}
	
	public void excluirEndereco(Endereco endereco){
		endDao.excluir(endereco);
	}
}
