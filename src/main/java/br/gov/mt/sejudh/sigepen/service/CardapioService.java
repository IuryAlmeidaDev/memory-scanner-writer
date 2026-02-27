package br.gov.mt.sejudh.sigepen.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.base.service.BaseService;
import br.gov.mt.sejudh.sigepen.dao.CardapioDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCardapio;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cardapio;
import br.gov.mt.sejudh.sigepen.model.dto.CardapioDTO;

@Stateless
public class CardapioService extends BaseService<Cardapio, Long> {

	private static final long serialVersionUID = 7168310183626016049L;
	
	@Inject
	protected CardapioDAO cardapioDAO;

	@Inject
	public void setApostilamentoDAO(CardapioDAO cardapioDAO) {
		this.cardapioDAO = cardapioDAO;
		this.setDao(cardapioDAO);
	}
	
	public Cardapio salvar(Cardapio t) throws ValidacaoException {
        return this.cardapioDAO.salvar(t);
    }

	public Cardapio buscarExistente(Cardapio cardapio) {
		if (cardapio.getSemana() != null && cardapio.getDiaSemana() != null &&
				cardapio.getTipoRefeicao() != null && cardapio.getContrato() != null &&
				cardapio.getTipoDieta() != null) {
			return cardapioDAO.buscarPor(cardapio.getSemana(), cardapio.getDiaSemana(),
					cardapio.getTipoRefeicao().getId(), cardapio.getContrato().getId(),
					cardapio.getTipoDieta().getId());
		}
		return null;
	}
	
	public List<CardapioDTO> buscarDTOPorFiltro(FiltroCardapio filtro) {
		return cardapioDAO.buscaPorFiltro(filtro);
	}
	
	@Override
	public Cardapio buscarPorId(Long id) {
		return cardapioDAO.buscarPorId(id);
	}

}
