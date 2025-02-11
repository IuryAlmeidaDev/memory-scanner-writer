package br.gov.mt.sejudh.sigepen.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import br.gov.mt.sejudh.sigepen.model.LogAcesso;

public class LogAcessoDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void salvar(LogAcesso logAcesso) {
        if (logAcesso.getIdLogAcesso() == null || logAcesso.getIdLogAcesso() <= 0) {
            entityManager.persist(logAcesso);
        } else {
            entityManager.merge(logAcesso);
        }
    }
}