package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.shiro.SecurityUtils;
import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.model.Permissao;

@RequestScoped
public class HistoricoRecuperandoDAO {

	@Inject
	private EntityManager entityManager;

	public void salvar(HistoricoRecuperando historicoRecuperando){
		if (historicoRecuperando.getId() > 0) {
			entityManager.merge(historicoRecuperando);
		} else {
			entityManager.persist(historicoRecuperando);
		}
	}

	public Optional<HistoricoRecuperando> buscarHistoricoPorId(long id) {
		String jpql = "SELECT h FROM HistoricoRecuperando h " +
				"JOIN FETCH h.recuperando rec " +
				"JOIN FETCH rec.pessoa pessoa " +
				"WHERE h.id = :id";

		TypedQuery<HistoricoRecuperando> query = entityManager.createQuery(jpql, HistoricoRecuperando.class);
		query.setParameter("id", id);

		try {
			return Optional.of(query.getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	public List<HistoricoRecuperando> listarHistoricoRecuperandoPorPeriodoPermissao(Long recuperandoId, Date dataInicial, Date dataFinal) {
		List<HistoricoRecuperando> lista = listarHistoricoRecuperandoPorPeriodo(recuperandoId, dataInicial, dataFinal);
		List<HistoricoRecuperando> listaFiltrada = new ArrayList<>();

		for (HistoricoRecuperando h : lista) {
			Hibernate.initialize(h.getRecuperando());

			if (h.getTipoHistorico() != null && h.getTipoHistorico().getFuncionalidade() != null) {
				boolean temPermissao = h.getTipoHistorico().getFuncionalidade().getPermissoes().stream()
						.filter(permissao -> permissao.getNome().startsWith("VisualizarHistoricoRecuperando"))
						.anyMatch(permissao -> SecurityUtils.getSubject().isPermitted(permissao.getNome()));

				if (temPermissao) {
					listaFiltrada.add(h);
				}
			}
		}
		return listaFiltrada;
	}

	public List<HistoricoRecuperando> listarHistoricoRecuperandoPorPeriodo(Long recuperandoId, Date dataInicial, Date dataFinal) {
		StringBuilder jpql = new StringBuilder("SELECT h FROM HistoricoRecuperando h ")
				.append("JOIN FETCH h.recuperando rec ")
				.append("JOIN FETCH rec.pessoa pessoa ")
				.append("WHERE h.recuperando.id = :id");

		if (dataInicial != null) {
			jpql.append(" AND h.dataRegistro >= TRUNC(:dataInicial)");
		}
		if (dataFinal != null) {
			jpql.append(" AND h.dataRegistro <= TRUNC(:dataFinal) + 0.99999");
		}

		jpql.append(" ORDER BY h.dataRegistro ASC");

		TypedQuery<HistoricoRecuperando> query = entityManager.createQuery(jpql.toString(), HistoricoRecuperando.class);
		query.setParameter("id", recuperandoId);

		if (dataInicial != null) {
			query.setParameter("dataInicial", dataInicial);
		}
		if (dataFinal != null) {
			query.setParameter("dataFinal", dataFinal);
		}

		return query.getResultList();
	}

	public void removerHistoricoRecuperando(Long recuperandoId, Long historicoId) {
		String jpql = "DELETE FROM HistoricoRecuperando h WHERE h.recuperando.id = :recuperandoId AND h.id = :historicoId";
		Query query = entityManager.createQuery(jpql);
		query.setParameter("recuperandoId", recuperandoId);
		query.setParameter("historicoId", historicoId);
		query.executeUpdate();
		entityManager.flush();
	}
}
