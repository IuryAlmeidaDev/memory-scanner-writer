package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.model.Recuperando;

@Stateless
public class AtestadosService {

	@Inject
	private RecuperandoService recuperandoService;

	public Recuperando buscarRecuperandoComPassagens(Long recuperandoId) {
		return recuperandoService.buscarRecuperandoComPassagens(recuperandoId);
	}
	
	public Boolean verificaRecuperandoPossuiPassagemNaUnidadeLogada(Long recuperandoId) {
		return recuperandoService.verificaRecuperandoPossuiPassagemNaUnidadeLogada(recuperandoId);
	}
}
