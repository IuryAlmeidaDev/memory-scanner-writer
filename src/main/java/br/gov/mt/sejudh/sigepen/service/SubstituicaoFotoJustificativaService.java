package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.SubstituicaoFotoJustificativaDAO;
import br.gov.mt.sejudh.sigepen.model.SubstituicaoFotoJustificativa;

@Stateless
public class SubstituicaoFotoJustificativaService extends BaseService<SubstituicaoFotoJustificativa, Long> {

	@Inject
	private SubstituicaoFotoJustificativaDAO dao;

	public void substituirFoto(SubstituicaoFotoJustificativa fotoJustificativa){
		dao.salvar(fotoJustificativa);
	}
}
