package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.EntradaSaidaRecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.model.EntradaSaida;
import br.gov.mt.sejudh.sigepen.model.EntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;

@Stateless
public class EntradaSaidaRecuperandoService {

	@Inject
	private EntradaSaidaRecuperandoDAO dao;
	
	public int buscarQuantidadeFiltrada(FiltroEntradaSaidaRecuperando filtro) {
		return this.dao.buscarQuantidadeFiltrada(filtro);
	}
	
	public List<EntradaSaidaRecuperando> buscarEntradasPorFiltro(FiltroEntradaSaidaRecuperando filtro, boolean isRelatorio) {
		return this.dao.buscarEntradasPorFiltro(filtro, isRelatorio);
	}

	public List<TipoVisita> getTipoVisitas(TipoEntrada te) {
		return dao.listarTipoVisitas(te);
	}

	public EntradaSaida salvar(EntradaSaida entrada) {
		return this.dao.salvar(entrada);
		
	}

}
