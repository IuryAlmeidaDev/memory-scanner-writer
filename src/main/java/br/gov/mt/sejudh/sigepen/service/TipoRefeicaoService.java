package br.gov.mt.sejudh.sigepen.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.TipoRefeicaoDAO;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;

public class TipoRefeicaoService {

	@Inject
	protected TipoRefeicaoDAO DAO;
	
	public List<TipoRefeicao> listarTodos() {
		return DAO.listarTodos();
	}
	
	public boolean temTodasRefeicoes(List<TipoRefeicao> lista) {
		List<TipoRefeicao> todas = this.listarTodos();

		if (todas == null || todas.isEmpty()) {
			return lista == null || lista.isEmpty();
		}

		if (lista == null || lista.isEmpty()) {
			return false;
		}

		Set<Long> idsEsperados = todas.stream().map(TipoRefeicao::getId).collect(Collectors.toSet());
		Set<Long> idsInformados = lista.stream().map(TipoRefeicao::getId).collect(Collectors.toSet());

		return idsInformados.containsAll(idsEsperados);
    }
}
