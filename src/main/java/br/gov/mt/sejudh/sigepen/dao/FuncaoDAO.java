package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroFuncao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Cargo;
import br.gov.mt.sejudh.sigepen.model.Funcao;


@RequestScoped
public class FuncaoDAO {
	
	@Inject
	private EntityManager entityManager;
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(Funcao funcao) {
		if(funcao.getId()>0){
			funcao = entityManager.merge(funcao);
		}else{
			entityManager.persist(funcao);	
		}
	}
	
	public void excluir(Funcao funcao) {
		entityManager.remove(entityManager.merge(funcao));		
	}

	@SuppressWarnings("unchecked")
	public List<Funcao> buscaPorFiltro(FiltroFuncao filtro) {		
		Criteria criteria = filtro.criarCriteriaParaBusca(Funcao.class, entityManager);
		return criteria.list();
	}		
	
	public int quantidadeFiltrados(FiltroFuncao filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Funcao.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
	    return ((Number) criteria.uniqueResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<Funcao> listarFuncaoAtiva() {
		String jpql="select f from Funcao f where f.status = :status order by f.nome";
		Query query = entityManager.createQuery(jpql, Funcao.class);
		query.setParameter("status", Status.ATIVO);
		List<Funcao> funcao = query.getResultList();
		return funcao;
	}

	public boolean jaExisteFuncaoComMesmoNomeEmCargo(Funcao funcao) {
		Query query = entityManager.createQuery("select count(f) from Funcao f where lower(f.nome) = :funcaoNome and f.id <> :funcaoId AND f.cargo = :cargo");
		query.setParameter("funcaoNome", funcao.getNome().toLowerCase());
		query.setParameter("funcaoId", funcao.getId());
		query.setParameter("cargo", funcao.getCargo());
		return (long) query.getSingleResult() > 0;
	}

	public boolean isFuncaoAssociadoAUmServidor(Funcao funcao) {
		Query query = entityManager.createQuery("select count(s) from Servidor s where s.funcao = :funcao");
		query.setParameter("funcao", funcao);
		return (long) query.getSingleResult() > 0;
	}

	@SuppressWarnings("unchecked")
	public List<Funcao> buscaPorFiltro(FiltroFuncao filtro, boolean b) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Funcao.class, entityManager, b);			
		return criteria.list();
	}

	public Funcao buscarPorId(long id) {
		return entityManager.find(Funcao.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Funcao> listarFuncoesPorCargo(Cargo cargo) {
		String jpql="select f from Funcao f where f.cargo = :cargo  and f.status = :status order by f.nome";
		Query query = entityManager.createQuery(jpql, Funcao.class);
		query.setParameter("cargo", cargo);
		query.setParameter("status", Status.ATIVO);
		List<Funcao> funcoes = query.getResultList();
		return funcoes;
	}
}
