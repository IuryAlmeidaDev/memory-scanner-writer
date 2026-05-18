package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.TipoDietaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoDieta;
import br.gov.mt.sejudh.sigepen.model.TipoDieta;

public class TipoDietaService extends BaseService<TipoDieta, Long> {

	private static final long serialVersionUID = -5580365638715259890L;

	@Inject
	protected TipoDietaDAO tipoDietaDAO;

	@Inject
	public void setApostilamentoDAO(TipoDietaDAO tipoDietaDAO) {
		this.tipoDietaDAO = tipoDietaDAO;
		this.setDao(tipoDietaDAO);
	}

	public List<TipoDieta> buscarPorFiltro(FiltroTipoDieta filtro) {
		return tipoDietaDAO.buscaPorFiltro(filtro);
	}
	
	public List<TipoDieta> listarTodos(){
		return tipoDietaDAO.listarTodos();
	}

	public List<String> listarDescricoesParaFiltro(String consulta, int limite) {
		return tipoDietaDAO.listarDescricoesParaFiltro(consulta, limite);
	}

	public List<TipoDieta> listarAtivas() {
		return tipoDietaDAO.listarAtivos();
	}

	public List<TipoDieta> listarEspecialAtivas() {
		return tipoDietaDAO.listarEspecialAtivas();
	}

	public boolean existeDietaPadrao() {
		return tipoDietaDAO.existeDietaPadrao();
	}

}
