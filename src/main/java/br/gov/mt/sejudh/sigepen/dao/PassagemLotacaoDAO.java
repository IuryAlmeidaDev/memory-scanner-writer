package br.gov.mt.sejudh.sigepen.dao;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;

@RequestScoped
public class PassagemLotacaoDAO {

    @Inject
    private EntityManager entityManager;

    public Long countEntradasPorUnidadeNaData(Long unidadeId, Date dataInicio, Date dataFim) {
        String jpql = "SELECT COUNT(pl) FROM PassagemLotacao pl " +
                "WHERE pl.estrutura.unidadePrisional.id = :unidadeId " +
                "AND pl.dataEntrada BETWEEN :dataInicio AND :dataFim";
        try {
            return entityManager.createQuery(jpql, Long.class)
                    .setParameter("unidadeId", unidadeId)
                    .setParameter("dataInicio", dataInicio)
                    .setParameter("dataFim", dataFim)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }

    public Long countSaidasPorUnidadeNaData(Long unidadeId, Date dataInicio, Date dataFim) {
        String jpql = "SELECT COUNT(pl) FROM PassagemLotacao pl " +
                "WHERE pl.estrutura.unidadePrisional.id = :unidadeId " +
                "AND pl.dataSaida BETWEEN :dataInicio AND :dataFim";
        try {
            return entityManager.createQuery(jpql, Long.class)
                    .setParameter("unidadeId", unidadeId)
                    .setParameter("dataInicio", dataInicio)
                    .setParameter("dataFim", dataFim)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }
}