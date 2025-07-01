package br.gov.mt.sejudh.sigepen.service;


import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.AtividadeDAO;
import br.gov.mt.sejudh.sigepen.model.Atividade;


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
	
	
	
	/* ------------------------  Métodos  ------------------------ */
	
	public List<Atividade> listarTodasAtividades() {
        return dao.listarTodasAtividades();
    }
	
	public List<Atividade> listarTodasAtividadesPorTipo(Long tipo) {	    
	    return buscaPorFiltro("tipo", tipo);	    	   
	}
	
	
}
