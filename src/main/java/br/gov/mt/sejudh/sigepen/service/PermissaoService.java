package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.PermissaoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPermissao;
import br.gov.mt.sejudh.sigepen.model.Permissao;

@Stateless
public class PermissaoService {

	@Inject
	private PermissaoDAO dao;
	
	public PermissaoService() {
		
	}
	
	public List<Permissao> buscarPermissoesPorFiltro(FiltroPermissao filtro) {
		return dao.buscarPermissoesPorFiltro(filtro);
	}

	public int buscarQuantidadeFiltrada(FiltroPermissao filtro) {
		return dao.buscarQuantidadeFiltrada(filtro);
	}
}
