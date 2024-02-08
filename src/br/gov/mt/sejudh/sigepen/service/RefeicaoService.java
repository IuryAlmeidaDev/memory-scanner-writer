package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.RefeicaoDAO;
import br.gov.mt.sejudh.sigepen.model.Apostilamento;
import br.gov.mt.sejudh.sigepen.model.Refeicao;

@Stateless
public class RefeicaoService extends BaseService<Refeicao, Long> {

	private static final long serialVersionUID = 6213157827033432631L;

	@Inject
	protected RefeicaoDAO refeicaoDAO;

	@Inject
	public void setApostilamentoDAO(RefeicaoDAO refeicaoDAO) {
		this.refeicaoDAO = refeicaoDAO;
		this.setDao(refeicaoDAO);
	}
	
	public List<Refeicao> listarRefeicoesApostilamento(Apostilamento apostilamento) {
		return refeicaoDAO.listarRefeicoesApostilamento(apostilamento.getId());
	}

}
