package br.gov.mt.sejudh.sigepen.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.RelatorioRecuperandoJurisdicionalDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioRecuperandoJurisdicional;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioRecuperandoJurisdicionalDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioRecuperandoJurisdicionalDTO.RecuperandoJurisdicionalDTO;

@Stateless
public class RelatorioRecuperandoJurisdicionalService extends BaseService<Impedimento, Long> {

	private static final long serialVersionUID = 8125552944206428355L;

	@Inject
	private RelatorioRecuperandoJurisdicionalDAO dao;
	
	public List<RecuperandoJurisdicionalDTO> buscarRelatorioNovo(FiltroRelatorioRecuperandoJurisdicional filtro) {
		List<RelatorioRecuperandoJurisdicionalDTO> lista = this.dao.buscarRelatorioNovo(filtro);
		List<RecuperandoJurisdicionalDTO> recuperandos = new ArrayList<RelatorioRecuperandoJurisdicionalDTO.RecuperandoJurisdicionalDTO>();
		
		if (lista.size() > 0) {
			RelatorioRecuperandoJurisdicionalDTO.prepararLista(lista, recuperandos);
			lista = null;
		}
		
		return recuperandos;
	}
	
	public List<RelatorioRecuperandoJurisdicionalDTO> buscarRelatorio(FiltroRelatorioRecuperandoJurisdicional filtro) {
		return this.dao.buscarRelatorio(filtro);
	}

}
