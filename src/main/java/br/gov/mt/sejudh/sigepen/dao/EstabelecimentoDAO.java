package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEstabelecimento;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;


@RequestScoped
public class EstabelecimentoDAO {
	
	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(Estabelecimento estabelecimento) {
		if(estabelecimento.getId()>0){
			estabelecimento = entityManager.merge(estabelecimento);
		}else{
			entityManager.persist(estabelecimento);	
		}
	}

	public void excluir(Estabelecimento estabelecimento) {
		entityManager.remove(entityManager.merge(estabelecimento));
	}	
	
	@SuppressWarnings("unchecked")
	public List<Estabelecimento> buscaPorFiltro(FiltroEstabelecimento filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Estabelecimento.class, entityManager, isBuscaRelatorio);			
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroEstabelecimento filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Estabelecimento.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}


	public boolean jaExisteEstabelecimentoComMesmoNome(Estabelecimento estabelecimento) {
		Query query = entityManager.createQuery("select count(e) from Estabelecimento e where lower(e.nome) = :estabNome and e.id <> :estabId");
		query.setParameter("estabNome", estabelecimento.getNome().toLowerCase());
		query.setParameter("estabId", estabelecimento.getId());
		return (long) query.getSingleResult() > 0;
	}
	
	public boolean isProcedenciaAssociadaAUmaPassagemProcedencia(Estabelecimento estabelecimento) {
		Query query = entityManager.createQuery("SELECT COUNT(p) FROM PassagemProcedencia p WHERE p.procedencia = :procedencia");
		query.setParameter("procedencia", estabelecimento);
		return (long) query.getSingleResult() > 0;
	}

	public Estabelecimento buscarPorId(long id) {
		return entityManager.find(Estabelecimento.class, id);
	}
}
