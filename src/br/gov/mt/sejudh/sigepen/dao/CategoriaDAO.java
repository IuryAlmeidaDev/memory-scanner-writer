package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCategoria;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Categoria;


@RequestScoped
public class CategoriaDAO {
	
	@Inject
	private EntityManager entityManager;

	public void salvar(Categoria categoria) {
		if(categoria.getId()>0){
			categoria = entityManager.merge(categoria);
		}else{
			entityManager.persist(categoria);	
		}
	}

	public void excluir(Categoria categoria) {
		entityManager.remove(entityManager.merge(categoria));
	}	
	
	@SuppressWarnings("unchecked")
	public List<Categoria> buscaPorFiltro(FiltroCategoria filtro, boolean isRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Categoria.class, entityManager, isRelatorio);			
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroCategoria filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Categoria.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> listarCategoriaAtiva() {
		String jpql="select c from Categoria c where c.status = :status";
		Query query = entityManager.createQuery(jpql, Categoria.class);
		query.setParameter("status", Status.ATIVO);
		List<Categoria> categorias = query.getResultList();
		return categorias;
	}

	public boolean jaExisteCategoriaComMesmoNome(Categoria categoria) {
		Query query = entityManager.createQuery("select count(c) from Categoria c where lower(c.nome) = :categoriaNome and c.id <> :categoriaId");
		query.setParameter("categoriaNome", categoria.getNome().toLowerCase());
		query.setParameter("categoriaId", categoria.getId());
		return (long) query.getSingleResult() > 0;
	}

	public boolean isCategoriaAssociadaAUmCargo(Categoria categoria) {
		Query query = entityManager.createQuery("select count(c) from Cargo c where c.categoria = :categoria");
		query.setParameter("categoria", categoria);
		return (long) query.getSingleResult() > 0;
	}

	public Categoria buscarPorId(long id) {
		return entityManager.find(Categoria.class, id);
	}
}
