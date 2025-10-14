package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.AtividadeDAO;
import br.gov.mt.sejudh.sigepen.model.Atividade;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class AtividadeService extends BaseService<Atividade, Long>{

	private static final long serialVersionUID = 1L;

	@Inject
	private AtividadeDAO dao;
	
	@Inject
	public void setAtividadeDAO(AtividadeDAO dao) {
		this.dao = dao;
		super.setDao(dao);
	}

    public List<Atividade> listarTodasAtividades() {
        return dao.listarTodasAtividades();
    }
	
	public List<Atividade> listarTodasAtividadesPorTipo(Long tipo) {	    
	    return buscaPorFiltro("tipo", tipo);	    	   
	}
}
