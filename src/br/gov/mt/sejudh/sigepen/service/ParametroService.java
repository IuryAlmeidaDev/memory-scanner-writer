package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.ParametroDao;
import br.gov.mt.sejudh.sigepen.model.Parametro;

@Stateless
public class ParametroService {

	@Inject
	private ParametroDao dao;
	
	public void salvar(Parametro param) {
		dao.salvar(param);
	}
	
	public void excluir(Parametro param) {
		dao.excluir(param);
	}
	
	public List<Parametro> getParametrosSistema() {
		return dao.listarParametrosSistema();
	}
}
