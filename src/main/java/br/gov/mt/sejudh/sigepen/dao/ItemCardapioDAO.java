package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroApostilamento;
import br.gov.mt.sejudh.sigepen.model.Cardapio;
import br.gov.mt.sejudh.sigepen.model.ItemCardapio;
import br.gov.mt.sejudh.sigepen.model.dto.ItemCardapioDTO;

@RequestScoped
public class ItemCardapioDAO extends BaseDAO<ItemCardapio, Long> {

	private static final long serialVersionUID = -5877005003461906704L;

	public ItemCardapio salvar(ItemCardapio itemCardapio) {
        if (itemCardapio.getId() == null || itemCardapio.getId() <= 0 ) {
            em.persist(itemCardapio);
            return itemCardapio;
        }

        return em.merge(itemCardapio);
    }
	
	@SuppressWarnings("unchecked")
	public List<ItemCardapioDTO> buscaPorFiltro(FiltroApostilamento filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(ItemCardapio.class, em);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(ItemCardapio.class));
		return criteria.list();
	}

}
