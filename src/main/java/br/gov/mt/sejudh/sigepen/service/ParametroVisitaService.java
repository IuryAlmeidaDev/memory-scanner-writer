package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.ParametroVisitaDAO;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatus;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.ParametroVisita;
import br.gov.mt.sejudh.sigepen.model.ParametroVisitaEstrutura;


@Stateless
public class ParametroVisitaService {
	
	@Inject
	private ParametroVisitaDAO dao;	

	public List<ParametroVisita> buscarParametrosVisita(long id) {
		return dao.buscarParametrosVisita(id);
	}

	public ParametroVisita salvar(ParametroVisita parametro) {
		return dao.salvar(parametro);
	}

	public void excluirParametroVisita(ParametroVisita p) {
		dao.excluirParametroVisita(p);
	}

	public void parametroExistente(ParametroVisita parametro) throws ValidacaoException {
		if(SgpStatus.A == parametro.getStatus() && this.dao.parametroExistente(parametro))
			throw new ValidacaoException("Cadastro não permitido! já existe cadastro do mesmo tipo ativo com a mesma estrutura e dia!");
	}
	
	public void validarEstrutura(ParametroVisita parametro, Estrutura est)  throws ValidacaoException {
		if(SgpStatus.A == parametro.getStatus() && this.dao.estruturaExistente(parametro, est))
			throw new ValidacaoException("Cadastro não permitido! já existe cadastro do mesmo tipo ativo com a mesma estrutura e dia!");
	}

	public List<ParametroVisitaEstrutura> listarEstruturas(ParametroVisita p) {
		return dao.listarEstruturas(p);
		
	}
}
