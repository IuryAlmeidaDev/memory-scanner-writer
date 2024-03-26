package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.TipoVisitaDAO;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;


@Stateless
public class TipoVisitaService extends BaseService<TipoVisita, Long> {

	private static final long serialVersionUID = 6060410458495656060L;

    private TipoVisitaDAO tipoVisitaDAO;
    
    @Inject
    public void setTipoVisitaDAO(TipoVisitaDAO tipoVisitaDAO) {
		this.tipoVisitaDAO = tipoVisitaDAO;
		this.setDao(tipoVisitaDAO);
	}
    
    public void excluir(TipoVisita tipoVisita) throws ValidacaoException {
    	this.validarExclusao(tipoVisita);    	
    	tipoVisitaDAO.excluir(tipoVisita);
    }
    
    protected void validarExclusao(TipoVisita tipoVisita) throws ValidacaoException {
        if (tipoVisitaDAO.tipoVisitaEstaVinculada(tipoVisita)) {
            throw new ValidacaoException("Existe visitante vinculado ao tipo de visita!");
        }
    }

	public TipoVisita buscarPorId(Long tipoVisitaId) {
		return tipoVisitaDAO.buscarPorId(tipoVisitaId);
	}
	
	public TipoVisita buscaPorIdCredenciamento(Long idCredenciamento) {
		return tipoVisitaDAO.buscarPorIdCredenciamento(idCredenciamento);
	}
}
