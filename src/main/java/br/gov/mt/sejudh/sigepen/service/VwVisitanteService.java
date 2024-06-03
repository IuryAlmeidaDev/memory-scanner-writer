package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.primefaces.model.SortOrder;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.VwVisitanteDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitante;
import br.gov.mt.sejudh.sigepen.model.view.VwVisitante;

@Stateless
public class VwVisitanteService extends BaseService<VwVisitante, Long> {


	private static final long serialVersionUID = -4345131784900856674L;
	
    private VwVisitanteDAO vwVisitanteDAO;
    
    @Inject
    public void setVwVisitanteDAO(VwVisitanteDAO vwVisitanteDAO) {
		this.vwVisitanteDAO = vwVisitanteDAO;
		this.setDao(vwVisitanteDAO);
	}
    
	public int buscarQuantidadeFiltrada(FiltroVwVisitante filtro) {
		return vwVisitanteDAO.quantidadeFiltrados(filtro);
	}

	public int quantidadeVwVisitante(FiltroVwVisitante filtro) {
		return vwVisitanteDAO.quantidadeVwVisitante(filtro);
	}

	public List<VwVisitante> buscarPorFiltro(FiltroVwVisitante filtro, boolean isBuscaRelatorio) {
		return vwVisitanteDAO.buscarPorFiltro(filtro, isBuscaRelatorio);
	}
	
	public List<VwVisitante> buscarPorFiltroVW(FiltroVwVisitante filtro, boolean isBuscaRelatorio, int first, int pageSize, String sortField, SortOrder sortOrder) {
				
		return vwVisitanteDAO.buscarPorFiltroVW(filtro, isBuscaRelatorio,  first,  pageSize, sortField, sortOrder);
	}
	
}
