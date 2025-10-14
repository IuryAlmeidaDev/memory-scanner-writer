package br.gov.mt.sejudh.sigepen.service;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.RelatorioEntradaSaidaRecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRelatorioEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.dto.RelatorioEntradaSaidaRecuperandoDTO;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class RelatorioEntradaSaidaRecuperandoService extends BaseService<Impedimento, Long> {

	private static final long serialVersionUID = 8125552944206428355L;

	@Inject
	private RelatorioEntradaSaidaRecuperandoDAO dao;

	public List<RelatorioEntradaSaidaRecuperandoDTO> buscarRelatorioEntradaSaidaPorFiltro(FiltroRelatorioEntradaSaidaRecuperando filtro) {
		return this.dao.buscarRelatorioEntradaSaidaPorFiltro(filtro);
	}

	public List<Regime> buscarRegimes() {
		return this.dao.buscarRegimes();
	}
}
