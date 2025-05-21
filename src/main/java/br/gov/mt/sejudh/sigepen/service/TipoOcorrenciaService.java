package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.GerenciarAssinaturaDAO;
import br.gov.mt.sejudh.sigepen.dao.TipoOcorrenciaDAO;
import br.gov.mt.sejudh.sigepen.model.GerenciarAssinatura;
import br.gov.mt.sejudh.sigepen.model.TipoOcorrencia;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;

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
}
