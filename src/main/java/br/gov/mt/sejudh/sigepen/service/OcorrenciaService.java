package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.OcorrenciaDAO;
import br.gov.mt.sejudh.sigepen.model.Ocorrencia;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class OcorrenciaService extends BaseService<Ocorrencia, Long> {

	
	private static final long serialVersionUID = 7230046305068303409L;
	
	@Inject
    private OcorrenciaDAO ocorrenciaDAO;

	@Inject
	public void setTipoOcorrenciaServiceDAO(OcorrenciaDAO ocorrenciaDAO) {
		this.ocorrenciaDAO = ocorrenciaDAO;
		this.setDao(ocorrenciaDAO);
	}
	
	
/* ------------------------- Métodos ------------------------- */
	
	public List<Ocorrencia> listarTodasOcorrencias() {
        return ocorrenciaDAO.listarTodasOcorrencias();
    }
	
	public Ocorrencia buscarPorIdComTipo(Long id)  {
		return ocorrenciaDAO.buscarPorIdComTipo(id);	
	}
	
	

}
