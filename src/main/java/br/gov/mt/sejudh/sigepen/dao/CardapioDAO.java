package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCardapio;
import br.gov.mt.sejudh.sigepen.model.Cardapio;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.dto.CardapioDTO;

@RequestScoped
public class CardapioDAO extends BaseDAO<Cardapio, Long> {
	
	@Inject
	private EntityManager entityManager;

	private static final long serialVersionUID = -318366946850533179L;	
	
	 public Cardapio salvar(Cardapio cardapio) {
        if (cardapio.getId() == null || cardapio.getId() <= 0 ) {
            em.persist(cardapio);
            return cardapio;
        }

        return em.merge(cardapio);
    }
	
	@SuppressWarnings("unchecked")
	public List<CardapioDTO> buscaPorFiltro(FiltroCardapio filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Cardapio.class, em);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(CardapioDTO.class));
		return criteria.list();
	}
	
	public Cardapio buscarPorId(Long id) {

		String jpql = "select c from Cardapio c where c.id = :id";
		Query query = entityManager.createQuery(jpql, Cardapio.class);
		query.setParameter("id", id);
		Cardapio cardapio = (Cardapio) query.getSingleResult();
//		Hibernate.initialize(contrato.getFornecedor());
//		Hibernate.initialize(contrato.getUnidades());
//		Hibernate.initialize(contrato.getHorariosRefeicao());
//		Hibernate.initialize(contrato.getJustificativa());
		
		return cardapio;
	}

}