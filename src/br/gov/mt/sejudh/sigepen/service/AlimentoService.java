package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.AlimentoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroAlimento;
import br.gov.mt.sejudh.sigepen.model.Alimento;

public class AlimentoService extends BaseService<Alimento, Long> {

	private static final long serialVersionUID = -53621150481705661L;
	
	@Inject
	private AlimentoDAO alimentoDAO;

	@Inject
	public AlimentoService(AlimentoDAO alimentoDAO) {
		this.alimentoDAO = alimentoDAO;
		this.setDao(alimentoDAO);
	}

	public List<Alimento> buscarPeloFiltro(FiltroAlimento filtro) {
		return alimentoDAO.buscaPorFiltro(filtro);
	}
	
	public List<Alimento> listarAtivos(){
		return alimentoDAO.listarAtivos();
	}
	
	public Alimento buscarAlimentoPorNome(Alimento alimento) {
		return alimentoDAO.buscarAlimentoPorNome(alimento);
	}

}
