package br.gov.mt.sejudh.sigepen.dao;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.gov.mt.sejudh.sigepen.base.dao.BaseDAO;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPlantao;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantao;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantaoServidor;

@RequestScoped
public class RegistroPlantaoDAO extends BaseDAO<RegistroPlantao, Long>{

	private static final long serialVersionUID = 1L;
	
	
	@Inject
	private EntityManager entityManager;

	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	
	
	/* ------------------------  Métodos  ------------------------ */
		
	
	
	public List<RegistroPlantao> listarRegistroPlantaoPorStatus(StatusPlantao statusPlantao) {
	    String jpql = "SELECT p FROM RegistroPlantao p WHERE p.statusPlantao = :statusPlantao"; 
	    Query query = entityManager.createQuery(jpql, RegistroPlantao.class);
	    query.setParameter("statusPlantao", StatusPlantao.ANDAMENTO);
	    return query.getResultList();
	}
	
	public List<Plantao> listarPlantaoPorStatus(Status status) {
	    String jpql = "SELECT p FROM Plantao p WHERE p.status = :status"; 
	    Query query = entityManager.createQuery(jpql, Plantao.class);
	    query.setParameter("status", Status.ATIVO);
	    return query.getResultList();
	}
	
	public RegistroPlantao buscarUltimoRegistro() {
	    return entityManager.createQuery(
	            "SELECT r FROM RegistroPlantao r ORDER BY r.id DESC", RegistroPlantao.class)
	        .setMaxResults(1)
	        .getSingleResult();
	}

	
	
	public RegistroPlantaoServidor salvarEquipePlantao(RegistroPlantaoServidor registroPlantaoServidor) {
        if (registroPlantaoServidor.getId() != null && registroPlantaoServidor.getId() > 0) {
            entityManager.merge(registroPlantaoServidor);
        } else {
            entityManager.persist(registroPlantaoServidor);
        }
		return registroPlantaoServidor;
    }
	
	
	public void atualizar(RegistroPlantaoServidor registroPlantaoServidor) {
	    if (registroPlantaoServidor.getId() != null && registroPlantaoServidor.getId() > 0) {
	        entityManager.merge(registroPlantaoServidor);
	        entityManager.flush(); 
	    }
	}
	
	public RegistroPlantaoServidor buscarPorNome(String nomeServidor) {
	    try {
	        return entityManager.createQuery(
	            "SELECT r FROM RegistroPlantaoServidor r WHERE r.nomeServidor = :nomeServidor", 
	            RegistroPlantaoServidor.class)
	            .setParameter("nomeServidor", nomeServidor)
	            .getSingleResult();
	    } catch (NoResultException e) {
	        return null; 
	    }
	}
	
	public boolean existeRegistro(String nomeServidor, LocalDate dataRegistro) {
	    try {	        
	        RegistroPlantaoServidor registro = entityManager.createQuery(
	                "SELECT r FROM RegistroPlantaoServidor r WHERE r.nomeServidor = :nomeServidor AND r.dataRegistroPlantaoServidor = :dataRegistro", 
	                RegistroPlantaoServidor.class)
	                .setParameter("nomeServidor", nomeServidor)
	                .setParameter("dataRegistro", Date.from(dataRegistro.atStartOfDay(ZoneId.systemDefault()).toInstant()))
	                .getSingleResult();

	        // Se encontrar um registro, retorna verdadeiro, indicando que o registro já existe
	        return registro != null;
	    } catch (NoResultException e) {
	        // Se não encontrar nenhum registro, retorna falso
	        return false;
	    }
	}

	
	
	public RegistroPlantao buscarUltimoRegistroPlantao() {
	    String jpql = "SELECT r FROM RegistroPlantao r ORDER BY r.id DESC";

	    try {
	        return entityManager.createQuery(jpql, RegistroPlantao.class)
	                            .setMaxResults(1)
	                            .getSingleResult();
	    } catch (NoResultException e) {
	        return null; 
	    }
	}

	
	//nao sera mas necessaqrio
	public RegistroPlantao buscarUltimoRegistroPlantaoComPlantao() {
	    String jpql = "SELECT r FROM RegistroPlantao r " +
	                  "JOIN FETCH r.plantao " +
	                  "WHERE r.plantao IS NOT NULL " +
	                  "ORDER BY r.id DESC";

	    try {
	        return entityManager.createQuery(jpql, RegistroPlantao.class)
	                            .setMaxResults(1)
	                            .getSingleResult();
	    } catch (NoResultException e) {
	        return null; // Caso não exista nenhum RegistroPlantao com plantao vinculado
	    }
	}


	
	public Plantao buscarPlantaoAnterior(Long idPlantaoAtual) {
	    try {
	        return entityManager.createQuery(
	                "SELECT p FROM RegistroPlantao rp JOIN FETCH rp.plantao p " +
	                "WHERE rp.id = :idPlantaoAtual ORDER BY rp.id DESC",
	                Plantao.class)
	            .setParameter("idPlantaoAtual", idPlantaoAtual)
	            .setMaxResults(1) // Garante que pega só o último
	            .getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

	/*
	public Plantao buscarPlantaoAnterior3(Long idPlantaoAtual) {
	    try {
	        RegistroPlantao registroPlantaoAnterior = entityManager.createQuery(
	                "SELECT rp FROM RegistroPlantao rp " +
	                "WHERE rp.id <= :idPlantaoAtual AND rp.plantao IS NOT NULL " +
	                "ORDER BY rp.id DESC",
	                RegistroPlantao.class)
	            .setParameter("idPlantaoAtual", idPlantaoAtual)
	            .setMaxResults(1) // Pegamos o último registro antes ou igual ao atual
	            .getSingleResult();

	        if (registroPlantaoAnterior != null) {
	            return registroPlantaoAnterior.getPlantao(); // Retorna o Plantao vinculado
	        } else {
	            return null;
	        }
	    } catch (NoResultException e) {
	        return null;
	    }
	}

	
	public Plantao buscarPlantaoAnterior2(Long idPlantaoAtual) {
	    try {
	        // Buscar o último RegistroPlantao anterior ao ID atual
	        RegistroPlantao registroPlantaoAnterior = entityManager.createQuery(
	                "SELECT rp FROM RegistroPlantao rp " +
	                "WHERE rp.id <= :idPlantaoAtual " +
	                "ORDER BY rp.id DESC",
	                RegistroPlantao.class)
	            .setParameter("idPlantaoAtual", idPlantaoAtual)
	            .setMaxResults(1) // Pegamos apenas o último RegistroPlantao
	            .getSingleResult();

	        // Verificar se encontramos um RegistroPlantao anterior
	        if (registroPlantaoAnterior != null && registroPlantaoAnterior.getPlantao() != null) {
	            return registroPlantaoAnterior.getPlantao(); // Retorna o Plantao vinculado
	        } else {
	            return null; // Nenhum plantão encontrado
	        }
	    } catch (NoResultException e) {
	        return null;
	    }
	}*/
	
	
		
}
