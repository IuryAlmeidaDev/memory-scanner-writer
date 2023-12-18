package br.gov.mt.sejudh.sigepen.service;

import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.ItemCardapioDAO;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cardapio;
import br.gov.mt.sejudh.sigepen.model.ItemCardapio;

public class ItemCardapioService extends BaseService<ItemCardapio, Long> {

	private static final long serialVersionUID = -8578382017589842907L;

	@Inject
	private ItemCardapioDAO itemCardapioDAO;

	public ItemCardapio salvar(ItemCardapio t) throws ValidacaoException {
        this.validaDuplicidade(t);
        return this.itemCardapioDAO.salvar(t);
    }
	
	@Inject
	public ItemCardapioService(ItemCardapioDAO itemCardapioDAO) {
		this.itemCardapioDAO = itemCardapioDAO;
		this.setDao(itemCardapioDAO);
	}

}
