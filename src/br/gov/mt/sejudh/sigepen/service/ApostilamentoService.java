package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.ApostilamentoDAO;
import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroApostilamento;
import br.gov.mt.sejudh.sigepen.model.Apostilamento;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.dto.ApostilamentoDTO;

@Stateless
public class ApostilamentoService extends BaseService<Apostilamento, Long> {

	private static final long serialVersionUID = 1861062963140055779L;

	@Inject
	protected ApostilamentoDAO apostilamentoDAO;
	
	@Inject
	private AuditoriaDAO auditDao;

	@Inject
	public void setApostilamentoDAO(ApostilamentoDAO apostilamentoDAO) {
		this.apostilamentoDAO = apostilamentoDAO;
		this.setDao(apostilamentoDAO);
	}

	@Override
	public Apostilamento buscarPorId(Long id) {
		Apostilamento apostilamento = apostilamentoDAO.buscaPorId(id);
		
		if (apostilamento != null) {
			apostilamento.setAutorRegistro(auditDao.autorRegistro(apostilamento, apostilamento.getId()));
			apostilamento.setAutorUltimaAlteracao(auditDao.autorUltimaAlteracao(apostilamento, apostilamento.getId()));
		}
		return apostilamento;
	}

	public List<Apostilamento> buscarAtivos() {
		return this.apostilamentoDAO.buscarAtivos();
	}
	
	public List<Apostilamento> buscarAtivosUnidade(long unidadeId) {
		return this.apostilamentoDAO.buscarAtivosUnidade(unidadeId);
	}

	public boolean existeApostilamentoUnidade(long unidadeId) {
		return this.apostilamentoDAO.existeApostilamentoUnidade(unidadeId).isEmpty();
	}

	public List<ApostilamentoDTO> buscaPorFiltro(FiltroApostilamento filtro) {
		return apostilamentoDAO.buscaPorFiltro(filtro);
	}

	public Apostilamento buscarApostilamentoAnterior(Contrato contrato) {
		return apostilamentoDAO.buscarApostilamentoAnterior(contrato);
	}

}
