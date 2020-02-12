package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCargo;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Cargo;
import br.gov.mt.sejudh.sigepen.model.Categoria;


@RequestScoped
public class CargoDAO {
	
	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(Cargo cargo) {
		if(cargo.getId()>0){
			cargo = entityManager.merge(cargo);
		}else{
			entityManager.persist(cargo);	
		}
	}

	public void excluir(Cargo cargo) {
		entityManager.remove(entityManager.merge(cargo));
	}	
		
	@SuppressWarnings("unchecked")
	public List<Cargo> buscaPorFiltro(FiltroCargo filtro, boolean isRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Cargo.class, entityManager, isRelatorio);			
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroCargo filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Cargo.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<Cargo> listarCargosPorCategoria(Categoria categoria) {
		String jpql="select c from Cargo c where c.categoria = :categoria  and c.status = :status order by c.nome";
		Query query = entityManager.createQuery(jpql, Cargo.class);
		query.setParameter("categoria", categoria);
		query.setParameter("status", Status.ATIVO);
		List<Cargo> cargos = query.getResultList();
		return cargos;
	}
	
	@SuppressWarnings("unchecked")
	public List<Cargo> listarCargosAtivo() {
		String jpql="select c from Cargo c where c.status = :status order by c.nome";
		Query query = entityManager.createQuery(jpql, Cargo.class);
		query.setParameter("status", Status.ATIVO);
		List<Cargo> cargos = query.getResultList();
		return cargos;
	}

	public boolean jaExisteCargoComMesmoNome(Cargo cargo) {
		Query query = entityManager.createQuery("select count(c) from Cargo c where lower(c.nome) = :cargoNome and c.id <> :cargoId");
		query.setParameter("cargoNome", cargo.getNome().toLowerCase());
		query.setParameter("cargoId", cargo.getId());
		return (long) query.getSingleResult() > 0;
	}

	public boolean isCargoAssociadoAUmServidor(Cargo cargo) {
		Query query = entityManager.createQuery("select count(s) from Servidor s where s.cargo = :cargo");
		query.setParameter("cargo", cargo);
		return (long) query.getSingleResult() > 0;
	}
	
	public Cargo buscarPorId(long id) {
		return entityManager.find(Cargo.class, id);
	}
}
