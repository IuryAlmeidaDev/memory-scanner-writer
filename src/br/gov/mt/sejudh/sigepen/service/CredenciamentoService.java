package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.CredenciamentoDAO;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Credenciamento;
import br.gov.mt.sejudh.sigepen.model.FaseDoCredenciamento;
import br.gov.mt.sejudh.sigepen.model.Visitante;

@Stateless
public class CredenciamentoService extends BaseService<Credenciamento, Long> {

	private static final long serialVersionUID = 6611700174660923936L;
	
    private CredenciamentoDAO credenciamentoDAO;

    @Inject
    public void setCredenciamentoDAO(CredenciamentoDAO credDAO) {
		this.credenciamentoDAO = credDAO;
		this.setDao(credDAO);
	}

    public List<Credenciamento> listarCredenciamentoPorVisitante(Visitante visitante) {
        return this.credenciamentoDAO.buscarPorVisitante(visitante);
    }
    
    public Credenciamento buscarCredenciamentos(Credenciamento cred) {
    	return this.credenciamentoDAO.buscarCredenciamentos(cred);
    }

    public void setDocumentosValidados(long id, long idCredenciamento, Visitante visitante) {
    	this.credenciamentoDAO.setDocumentosValidados(id, idCredenciamento, visitante);
    }

	public void atualizarStatusCredenciamento(FaseDoCredenciamento faseDoCredenciamento) {
		this.credenciamentoDAO.atualizarStatusCredenciamento(faseDoCredenciamento);
	}
	
	public void atualizarStatusAprovacao(Credenciamento credenciamento, StatusCredenciamento status) {
		this.credenciamentoDAO.atualizarStatusAprovacao(credenciamento, status);
	}
	
	public void atualizarStatusCredenciamento(Credenciamento credenciamento) {
		this.credenciamentoDAO.atualizarStatusCredenciamento(credenciamento);
	}
	
	public void atualizarStatusCredenciamento(Visitante visitante) {
		this.credenciamentoDAO.atualizarStatusCredenciamento(visitante);
	}
	
	public Credenciamento buscarUltimoCredenciamento(Visitante visitante) {
		return this.credenciamentoDAO.buscarUltimoCredenciamento(visitante);
	}
}
