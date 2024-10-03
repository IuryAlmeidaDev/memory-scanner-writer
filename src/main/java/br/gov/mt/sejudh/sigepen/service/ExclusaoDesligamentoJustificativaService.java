package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.ExclusaoDesligamentoJustificativaDAO;
import br.gov.mt.sejudh.sigepen.model.ExclusaoDesligamentoJustificativa;

@Stateless
public class ExclusaoDesligamentoJustificativaService extends BaseService<ExclusaoDesligamentoJustificativa, Long> {

	@Inject
	private ExclusaoDesligamentoJustificativaDAO dao;
	
	@Override
	public ExclusaoDesligamentoJustificativa salvar(ExclusaoDesligamentoJustificativa desligamentoJustificativa) {
		return this.dao.salvar(desligamentoJustificativa);
	}

}
