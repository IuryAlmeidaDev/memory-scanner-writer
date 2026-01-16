package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCardapio;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;
import br.gov.mt.sejudh.sigepen.enumerator.DiasSemana;
import br.gov.mt.sejudh.sigepen.model.Cardapio;
import br.gov.mt.sejudh.sigepen.model.dto.CardapioDTO;
import br.gov.mt.sejudh.sigepen.util.CardapioUtil;

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
		criteria.setResultTransformer(new AliasToBeanResultTransformer(CardapioDTO.class));
		return criteria.list();
	}

	@Override
	public int quantidadeFiltrados(FiltroConsulta filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Cardapio.class, em);
		return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	public Cardapio buscarPorId(Long id) {

		String jpql = "select c from Cardapio c where c.id = :id";
		Query query = entityManager.createQuery(jpql, Cardapio.class);
		query.setParameter("id", id);

		return (Cardapio) query.getSingleResult();
	}

	/**
	 * Verifica se existe um cardápio para a data, tipo de refeição e contrato especificados
	 * 
	 * @param data Data no formato dd/MM/yyyy ou dd/MM/yyyy HH:mm:ss
	 * @param tipoRefeicaoId ID do tipo de refeição
	 * @param contratoId ID do contrato
	 * @return true se existe cardápio, false caso contrário
	 */
	public boolean existeCardapioPara(String data, Long tipoRefeicaoId, Long contratoId) {
		try {
			// Calcula a semana e o dia da semana para a data informada
			int numeroSemana = CardapioUtil.obterNumeroSemanaAno(data);
			int semana = CardapioUtil.obterSemana(numeroSemana);

			// Consulta para verificar se existe cardápio para a data, tipo de refeição e contrato
            String jpql = "SELECT COUNT(c) FROM Cardapio c " +
                    "WHERE c.semana = :semana " +
                    "AND c.diaSemana = :diaSemana " +
                    "AND c.tipoRefeicao.id = :tipoRefeicaoId " +
                    "AND c.contrato.id = :contratoId ";

			Query query = entityManager.createQuery(jpql, Long.class);
			query.setParameter("semana", (long) semana);

			// Obtém o dia da semana (0-6, onde 0 é domingo)
			// A função TO_CHAR(data, 'D') retorna 1-7, onde 1 é domingo, por isso subtraímos 1
			String jpqlDiaSemana = "SELECT TO_CHAR(TO_DATE(:data, 'DD/MM/YYYY HH24:MI:SS'), 'D') FROM dual";
			Query diaSemanaQuery = entityManager.createNativeQuery(jpqlDiaSemana);
			diaSemanaQuery.setParameter("data", data);
			int diaSemanaInt = Integer.parseInt((String) diaSemanaQuery.getSingleResult()) - 1;
			DiasSemana diaSemana = DiasSemana.getValue(diaSemanaInt);

			query.setParameter("diaSemana", diaSemana);
			query.setParameter("tipoRefeicaoId", tipoRefeicaoId);
			query.setParameter("contratoId", contratoId);

			Long count = (Long) query.getSingleResult();
			return count > 0;
		} catch (NoResultException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
