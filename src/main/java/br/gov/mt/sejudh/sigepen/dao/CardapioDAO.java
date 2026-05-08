package br.gov.mt.sejudh.sigepen.dao;

import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCardapio;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroConsulta;
import br.gov.mt.sejudh.sigepen.enumerator.DiasSemana;
import br.gov.mt.sejudh.sigepen.model.Cardapio;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.CardapioDTO;
import br.gov.mt.sejudh.sigepen.util.CardapioUtil;

@RequestScoped
public class CardapioDAO extends BaseDAO<Cardapio, Long> {

    private static final Logger log = LogManager.getLogger(CardapioDAO.class);

    @Inject
    private EntityManager entityManager;

    private static final long serialVersionUID = -318366946850533179L;

    public Cardapio salvar(Cardapio cardapio) {
        if (cardapio.getId() == null || cardapio.getId() <= 0) {
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
        String jpql = "SELECT c FROM Cardapio c WHERE c.id = :id";
        Query query = entityManager.createQuery(jpql, Cardapio.class);
        query.setParameter("id", id);
        return (Cardapio) query.getSingleResult();
    }

    public Cardapio buscarPor(Long semana, DiasSemana diaSemana, Long tipoRefeicaoId,
                               Long contratoId, Long tipoDietaId, Long unidadePrisionalId) {
        try {
            String jpql = "SELECT c FROM Cardapio c "
                    + "WHERE c.semana = :semana "
                    + "AND c.diaSemana = :diaSemana "
                    + "AND c.tipoRefeicao.id = :tipoRefeicaoId "
                    + "AND c.contrato.id = :contratoId "
                    + "AND c.tipoDieta.id = :tipoDietaId "
                    + "AND c.unidadePrisional.id = :unidadePrisionalId";

            Query query = entityManager.createQuery(jpql, Cardapio.class);
            query.setParameter("semana", semana);
            query.setParameter("diaSemana", diaSemana);
            query.setParameter("tipoRefeicaoId", tipoRefeicaoId);
            query.setParameter("contratoId", contratoId);
            query.setParameter("tipoDietaId", tipoDietaId);
            query.setParameter("unidadePrisionalId", unidadePrisionalId);

            return (Cardapio) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            log.error("Erro em CardapioDAO.buscaCardapio", e);
            return null;
        }
    }

    public boolean existeCardapioPara(String data, Long tipoRefeicaoId, Long contratoId) {
        try {
            int numeroSemana = CardapioUtil.obterNumeroSemanaAno(data);
            int semana = CardapioUtil.obterSemana(numeroSemana);

            String jpql = "SELECT COUNT(c) FROM Cardapio c "
                    + "WHERE c.semana = :semana "
                    + "AND c.diaSemana = :diaSemana "
                    + "AND c.tipoRefeicao.id = :tipoRefeicaoId "
                    + "AND c.contrato.id = :contratoId";

            Query query = entityManager.createQuery(jpql, Long.class);
            query.setParameter("semana", (long) semana);

            String dataFormatada = CardapioUtil.formataData(data);
            LocalDate localDate = LocalDate.parse(dataFormatada);
            int diaSemanaInt = CardapioUtil.obterDiaSemana(localDate);
            DiasSemana diaSemana = DiasSemana.getValue(diaSemanaInt);

            query.setParameter("diaSemana", diaSemana);
            query.setParameter("tipoRefeicaoId", tipoRefeicaoId);
            query.setParameter("contratoId", contratoId);

            Long count = (Long) query.getSingleResult();
            return count > 0;
        } catch (NoResultException e) {
            return false;
        } catch (Exception e) {
            log.error("Erro em CardapioDAO (count)", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<UnidadePrisional> buscarUnidadesPorContrato(Long contratoId) {
        String jpql = "SELECT u FROM Contrato c JOIN c.unidades u "
                    + "WHERE c.id = :contratoId "
                    + "ORDER BY u.nome ASC";
        Query query = entityManager.createQuery(jpql, UnidadePrisional.class);
        query.setParameter("contratoId", contratoId);
        return query.getResultList();
    }
}