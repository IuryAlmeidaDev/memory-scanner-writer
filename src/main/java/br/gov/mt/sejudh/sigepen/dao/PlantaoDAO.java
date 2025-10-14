package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPlantao;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@RequestScoped
public class PlantaoDAO extends BaseDAO<Plantao, Long>{
	private static final long serialVersionUID = -318366946850533179L;
	
	
	@Inject
	private EntityManager entityManager;
	
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public boolean existePlantaoNaDataPorUnidade(UnidadePrisional unidade) {
		String jpql = "SELECT COUNT(p) FROM Plantao p " +
				"WHERE p.unidadePrisional = :unidade " +
				"AND p.statusPlantao = :status";

		Long count = entityManager.createQuery(jpql, Long.class)
				.setParameter("unidade", unidade)
				.setParameter("status", StatusPlantao.ANDAMENTO)
				.getSingleResult();

		return count > 0;
	}

	public Plantao obterPlantaoEmAndamento(UnidadePrisional unidade) {
		String jpql = "SELECT p FROM Plantao p WHERE p.dataPlantaoEncerramento IS NULL AND p.unidadePrisional = :unidade ORDER BY p.dataPlantao DESC";
		List<Plantao> resultado = em.createQuery(jpql, Plantao.class)
				.setParameter("unidade", unidade)
				.setMaxResults(1)
				.getResultList();

		return resultado.isEmpty() ? null : resultado.get(0);
	}

	public Plantao obterPlantaoAnterior(Plantao plantaoAtual, UnidadePrisional unidade) {
		String jpql = "SELECT p FROM Plantao p WHERE p.dataPlantao < :dataAtual AND p.unidadePrisional = :unidade ORDER BY p.dataPlantao DESC";
		List<Plantao> resultado = em.createQuery(jpql, Plantao.class)
				.setParameter("dataAtual", plantaoAtual.getDataPlantao())
				.setParameter("unidade", unidade)
				.setMaxResults(1)
				.getResultList();

		return resultado.isEmpty() ? null : resultado.get(0);
	}

	public Plantao buscarPlantaoMaisAntigoEmAbertoPorUnidade(UnidadePrisional unidade) {
		String jpql = "SELECT p FROM Plantao p " +
				"WHERE p.dataPlantaoEncerramento IS NULL " +
				"AND p.unidadePrisional = :unidade " +
				"ORDER BY p.dataPlantao ASC";

		List<Plantao> resultado = em.createQuery(jpql, Plantao.class)
				.setParameter("unidade", unidade)
				.setMaxResults(1)
				.getResultList();

		return resultado.isEmpty() ? null : resultado.get(0);
	}

	public Plantao buscarUltimoPlantaoEmAberto() {
		String jpql = "SELECT p FROM Plantao p WHERE p.dataPlantaoEncerramento IS NULL ORDER BY p.dataPlantao DESC";
		List<Plantao> resultado = em.createQuery(jpql, Plantao.class)
				.setMaxResults(1)
				.getResultList();
		return resultado.isEmpty() ? null : resultado.get(0);
	}

	public Plantao buscarUltimoPlantao() {
		String jpql = "SELECT r FROM Plantao r ORDER BY r.id DESC";

		try {
			return entityManager.createQuery(jpql, Plantao.class)
					.setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public Plantao buscarPorId(Long id) {
        String jpql = "SELECT DISTINCT p FROM Plantao p " +
                "LEFT JOIN FETCH p.unidadePrisional up " +
                "LEFT JOIN FETCH p.usuarioInicioPlantao ui " +
                "LEFT JOIN FETCH ui.servidor uis " +
                "LEFT JOIN FETCH p.usuarioFimPlantao uf " +
                "LEFT JOIN FETCH uf.servidor ufs " +
                "WHERE p.id = :id";

        TypedQuery<Plantao> query = em.createQuery(jpql, Plantao.class);
		query.setParameter("id", id);

		try {
            return query.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}
}