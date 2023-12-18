package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

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
		
        if (todas == null) {
            return lista == null;
        }
        return todas.equals(lista);
    }
}
