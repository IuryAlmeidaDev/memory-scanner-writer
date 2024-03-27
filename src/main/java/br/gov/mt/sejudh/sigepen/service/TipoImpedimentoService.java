package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.TipoImpedimentoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoImpedimento;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.TipoImpedimento;

@Stateless
public class TipoImpedimentoService extends BaseService<TipoImpedimento, Long> {

	private static final long serialVersionUID = 3013745979433041710L;

	private TipoImpedimentoDAO tipoInImpedimentoDAO;

	@Inject
	public void setTipoInImpedimentoDAO(TipoImpedimentoDAO tipoInImpedimentoDAO) {
		this.tipoInImpedimentoDAO = tipoInImpedimentoDAO;
		this.setDao(tipoInImpedimentoDAO);
	}

	@Override
	protected void validaDuplicidade(TipoImpedimento tipoImpedimento) throws ValidacaoException {
		FiltroTipoImpedimento filtro = new FiltroTipoImpedimento();
		filtro.setDescricao(tipoImpedimento.getDescricao());

		List<TipoImpedimento> list = dao.buscaPorFiltro(filtro, false);
		if (list.stream()
				.anyMatch(x -> tipoImpedimento.getId() == null || !x.getId().equals(tipoImpedimento.getId()))) {
			throw new ValidacaoException("Tipo impedimento já cadastrada no sistema!");
		}
	}
}
