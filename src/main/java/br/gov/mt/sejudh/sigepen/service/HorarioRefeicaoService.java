package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.HorarioRefeicaoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroHorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.dto.HorarioRefeicaoDTO;

public class HorarioRefeicaoService extends BaseService<HorarioRefeicao, Long>{
	
	private static final long serialVersionUID = 8109172552719856343L;
	
	@Inject
	protected HorarioRefeicaoDAO horarioRefeicaoDAO;
	
	@Inject
	public void setApostilamentoDAO(HorarioRefeicaoDAO horarioRefeicaoDAO) {
		this.horarioRefeicaoDAO = horarioRefeicaoDAO;
		this.setDao(horarioRefeicaoDAO);
	}

	public List<HorarioRefeicao> buscarPorUnidade(long unidadeId) {
		return horarioRefeicaoDAO.buscarPorUnidade(unidadeId);
	}
	
	public List<HorarioRefeicao> buscarPorContrato(long contratoId) {
		return horarioRefeicaoDAO.buscarPorContrato(contratoId);
	}
	
	public List<HorarioRefeicaoDTO> buscaPorFiltro(FiltroHorarioRefeicao filtro) {
        return this.horarioRefeicaoDAO.buscaPorFiltro(filtro, false);
    }

	public List<HorarioRefeicao> buscarPorUnidadeDataInicio(long unidadeId, Date dataInicio) {
		return this.horarioRefeicaoDAO.buscarPorUnidadeDataInicio(unidadeId, dataInicio);
	}
	
	public boolean temHorarioRefeicao(Long id) {		
		return this.horarioRefeicaoDAO.temHorarioRefeicao(id);
	}
	
	public int contarFiltrados(FiltroHorarioRefeicao filtro) {
		return horarioRefeicaoDAO.contarFiltrados(filtro);
	}
	
}
