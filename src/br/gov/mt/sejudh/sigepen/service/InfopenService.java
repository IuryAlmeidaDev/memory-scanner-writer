package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.InfopenDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroInfopen;
import br.gov.mt.sejudh.sigepen.model.Escolaridade;
import br.gov.mt.sejudh.sigepen.model.EstadoCivil;
import br.gov.mt.sejudh.sigepen.model.Etnia;
import br.gov.mt.sejudh.sigepen.model.Impedimento;
import br.gov.mt.sejudh.sigepen.model.Nacionalidade;
import br.gov.mt.sejudh.sigepen.model.Raca;
import br.gov.mt.sejudh.sigepen.model.Regime;
import br.gov.mt.sejudh.sigepen.model.Religiao;
import br.gov.mt.sejudh.sigepen.model.dto.InfopenDTO;

@Stateless
public class InfopenService extends BaseService<Impedimento, Long> {

	private static final long serialVersionUID = 8125552944206428355L;

	@Inject
	private InfopenDAO dao;

	public Nacionalidade[] buscarNacionalidades(long id) {
		return this.dao.buscarNacionalidades(id);
	}

	public Regime[] buscarRegimes(long id) {
		return this.dao.buscarRegimes(id);
	}

	public EstadoCivil[] buscarEstadoCivil(long id) {
		return this.dao.buscarEstadoCivil(id);
	}

	public Religiao[] buscarReligiao(long id) {
		return this.dao.buscarReligiao(id);
	}

	public Escolaridade[] buscarEscolaridade(long id) {
		return this.dao.buscarEscolaridade(id);
	}

	public Raca[] buscarRaca(long id) {
		return this.dao.buscarRaca(id);
	}

	public List<InfopenDTO> buscarInfopenDetalhadoPorFiltro(FiltroInfopen filtro) {
		return this.dao.buscarInfopenDetalhadoPorFiltro(filtro);
	}

	public Etnia[] buscarEtnia(long id) {
		return this.dao.buscarEtnia(id);
	}

	public long buscarFeminino(long idUnidade) {
		return this.dao.buscarFeminino(idUnidade);
	}
}
