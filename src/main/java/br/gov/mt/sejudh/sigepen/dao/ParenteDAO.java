package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.SolicitacaoVisita;

@RequestScoped
public class ParenteDAO {

    @Inject
    private EntityManager entityManager;

    public List<Parente> listarPorRecuperando(Recuperando recuperando) {
        TypedQuery<Parente> query = entityManager.createQuery("SELECT r FROM Parente r WHERE r.recuperando = :recuperando", Parente.class);
        query.setParameter("recuperando", recuperando);
        List<Parente> r = query.getResultList();
		for(Parente p : r) {
			Hibernate.initialize(p.getCredenciamentos());
		}
        return r;
    }

    public Optional<Parente> obtemPorParentescoRecuperandoInativo(SolicitacaoVisita solicitacaoVisita) {
        TypedQuery<Parente> query = entityManager.createQuery("SELECT p FROM Parente p WHERE p.recuperando = :recuperando " +
                        "AND p.parentesco = :parentesco AND p.status = :status",  Parente.class);
        query.setParameter("recuperando", solicitacaoVisita.getRecuperando());
        query.setParameter("parentesco", solicitacaoVisita.getParentesco());
        query.setParameter( "status", SgpStatusAlternativo.I);
        try {
        	query.setMaxResults(1);
            return Optional.of(query.getSingleResult());
        }catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Parente buscarParenteVisita(Long id) {
        String jpql = "SELECT p FROM Parente p " +
                "LEFT JOIN FETCH p.credenciamentos c " +
                "WHERE c.id = :id";
        TypedQuery<Parente> query = entityManager.createQuery(jpql,  Parente.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }
}
