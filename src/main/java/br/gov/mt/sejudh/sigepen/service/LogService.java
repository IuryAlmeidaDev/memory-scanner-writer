package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.LogDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroLog;
import br.gov.mt.sejudh.sigepen.model.Log;

@Stateless
public class LogService {

	@Inject
	private LogDAO dao;

	public List<Log> buscarLogsPorFiltro(FiltroLog filtro, boolean isRelatorio) {
		return dao.buscarLogsPorFiltro(filtro, isRelatorio);
	}

	public int buscarQuantidadeFiltrada(FiltroLog filtro) {
		return dao.buscarQuantidadeFiltrada(filtro);
	}

	public long buscarIdUsuarioPorIdServidor(long id) {
		return dao.buscarIdUsuarioPorServidorId(id);
	}
}


