package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.PessoaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPessoa;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.dto.PessoaDTO;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;

@Stateless
public class PessoaService {

	@Inject
	private PessoaDAO dao;
	
	@Inject
	private AuditoriaDAO auditDAO;

	@Interceptors(EJBExcInterceptor.class)
	public void salvar(Pessoa pessoa) {
		dao.salvar(pessoa);
	}

	public void excluir(long pessoaId) throws ValidacaoException {
		if(dao.isPessoaAssociadaAUmRecuperando(pessoaId)){
			throw new ValidacaoException("Essa pessoa não pode ser excluída pois já está associada a um Recuperando.");
		}
		
		dao.excluir(pessoaId);
	}

	public List<PessoaDTO> buscaPorFiltro(FiltroPessoa filtro) {
		return dao.buscaPorFiltro(filtro);
	}

	public int quantidadeFiltrados(FiltroPessoa filtro) {
		return dao.quantidadeFiltrados(filtro);
	}
	
	public Pessoa buscarPessoaPorId(long pessoaId) {
		Pessoa pessoa = dao.buscarPessoaPorId(pessoaId);
		
		if (pessoa != null) {
			pessoa.setAutorRegistro(auditDAO.autorRegistro(pessoa, pessoa.getId()));
			pessoa.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(pessoa, pessoa.getId()));
		}
		
		return pessoa;
	}
	
	public Pessoa buscarDadosPessoaRecuperandoPorId(long pessoaId) {
		Pessoa pessoa = dao.buscarPessoaPorId(pessoaId);
		
		return pessoa;
	}
}
