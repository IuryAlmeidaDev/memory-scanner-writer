package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.TipoOcorrenciaDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.TipoOcorrencia;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class TipoOcorrenciaService extends BaseService<TipoOcorrencia, Long> {

	
	private static final long serialVersionUID = 7230046305068303409L;
	
	@Inject
    private TipoOcorrenciaDAO tipoOcorrenciaServiceDAO;

	@Inject
	public void setTipoOcorrenciaServiceDAO(TipoOcorrenciaDAO tipoOcorrenciaServiceDAO) {
		this.tipoOcorrenciaServiceDAO = tipoOcorrenciaServiceDAO;
		this.setDao(tipoOcorrenciaServiceDAO);
	}

    public List<TipoOcorrencia> listar() {
		return this.dao.listarTodos();
    }

	public List<TipoOcorrencia> listarAtivos() {
		return this.dao.buscaPorCampo("status", Status.ATIVO);
	}

	public List<TipoOcorrencia> listarAtivosPorClassificacao(String classificacao) {
		return this.tipoOcorrenciaServiceDAO.listarAtivosPorClassificacao(classificacao);
	}
}
