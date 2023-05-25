package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFornecedor;
import br.gov.mt.sejudh.sigepen.model.Fornecedor;
import br.gov.mt.sejudh.sigepen.model.dto.FornecedorDTO;

@RequestScoped
public class FornecedorDAO {
	
	@Inject
	private EntityManager entityManager;

	public Fornecedor salvar(Fornecedor fornecedor) {
		if(fornecedor.getId()>0){
			entityManager.merge(fornecedor);
		}else{			
			entityManager.persist(fornecedor);	
		}
		return fornecedor;
	}
	
	public Fornecedor buscaPorId(Long id) {
		
		String jpql="select f from Fornecedor f join fetch f.endereco join fetch f.contatos where f.id = :id";
		Query query = entityManager.createQuery(jpql, Fornecedor.class);
		query.setParameter("id", id);
		Fornecedor fornecedor = (Fornecedor) query.getSingleResult();
		Hibernate.initialize(fornecedor.getEndereco());
		Hibernate.initialize(fornecedor.getContatos());
		
		return fornecedor;
	}
	
	public int quantidadeFiltrados(FiltroFornecedor filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Fornecedor.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<FornecedorDTO> buscaPorFiltro(FiltroFornecedor filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Fornecedor.class, entityManager);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(FornecedorDTO.class));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Fornecedor> listar() {
		String jpql="select f from Fornecedor f";
		Query query = entityManager.createQuery(jpql, Fornecedor.class);
		
		return query.getResultList();
	}
}
