package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.model.Ocorrencia;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@RequestScoped
public class OcorrenciaDAO extends BaseDAO<Ocorrencia, Long> {


	private static final long serialVersionUID = 1635530084073694819L;
	

	@Inject
	private EntityManager entityManager;

	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	
	
	
	/* ------------------------  Métodos  ------------------------ */
	
	
	public List<Ocorrencia> listarTodasOcorrencias() {
	    String jpql = "SELECT o FROM Ocorrencia o ORDER BY o.id";
	    TypedQuery<Ocorrencia> query = entityManager.createQuery(jpql, Ocorrencia.class);
	    return query.getResultList();
	}

	
	public Ocorrencia buscarPorIdComTipo(Long id) {
	    TypedQuery<Ocorrencia> query = entityManager.createQuery(
	        "SELECT o FROM Ocorrencia o JOIN FETCH o.tipoOcorrencia WHERE o.id = :id", Ocorrencia.class);
	    query.setParameter("id", id);
	    return query.getSingleResult();
	}

	public List<Ocorrencia> listarPorDataPlantao(LocalDate dataPlantao) {
		ZoneId zone = ZoneId.of("America/Cuiaba");

		Date inicio = Date.from(dataPlantao.atStartOfDay(zone).toInstant());
		Date fim = Date.from(dataPlantao.plusDays(1).atStartOfDay(zone).toInstant());

		String jpql = "SELECT o FROM Ocorrencia o WHERE o.plantao.dataPlantao >= :inicio AND o.plantao.dataPlantao < :fim";

		return entityManager.createQuery(jpql, Ocorrencia.class)
				.setParameter("inicio", inicio)
				.setParameter("fim", fim)
				.getResultList();
	}

}