package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.TipoVisitaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVisitante;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;


@Stateless
public class TipoVisitaService extends BaseService<TipoVisita, Long> {

	private static final long serialVersionUID = 6060410458495656060L;

	@Inject
    private VisitanteService visitanteService;

    private TipoVisitaDAO tipoVisitaDAO;
    
    @Inject
    public void setTipoVisitaDAO(TipoVisitaDAO tipoVisitaDAO) {
		this.tipoVisitaDAO = tipoVisitaDAO;
		this.setDao(tipoVisitaDAO);
	}
    
    protected void validarExclusao(TipoVisita tipoVisita) throws ValidacaoException {
        FiltroVisitante filtroVisitante = new FiltroVisitante();
        filtroVisitante.setTipoVisita(tipoVisita);

        int visitantes = this.visitanteService.quantidadeFiltrados(filtroVisitante);

        if (visitantes > 0) {
            throw new ValidacaoException("Existe visitante vinculado ao tipo de visita!");
        }
    }

	public TipoVisita buscarPorId(Long tipoVisitaId) {
		return tipoVisitaDAO.buscarPorId(tipoVisitaId);
	}
}
