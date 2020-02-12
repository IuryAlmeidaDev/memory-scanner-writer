package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroOrgao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.Orgao;

@RequestScoped
public class OrgaoDAO {

	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(Orgao orgao) {
		if (orgao.getId() > 0) {
			orgao = entityManager.merge(orgao);
		} else {
			entityManager.persist(orgao);
		}
	}

	public void excluir(Orgao orgao) {
		entityManager.remove(entityManager.merge(orgao));
	}

	@SuppressWarnings("unchecked")
	public List<Orgao> buscaPorFiltro(FiltroOrgao filtro, boolean isBuscaRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Orgao.class, entityManager, isBuscaRelatorio);
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroOrgao filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Orgao.class, entityManager);		
	    criteria.setProjection(Projections.rowCount());		
	    return ((Number) criteria.uniqueResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<Orgao> listarOrgaoAtivo() {
		String jpql="select new br.gov.mt.sejudh.sigepen.model.Orgao(o.id, o.nome) from Orgao o where o.status = :status";
		Query query = entityManager.createQuery(jpql, Orgao.class);
		query.setParameter("status", Status.ATIVO);
		List<Orgao> orgaos = query.getResultList();
		return orgaos;
	}

	public boolean jaExisteOrgaoComMesmoNome(Orgao orgao) {
		Query query = entityManager.createQuery("select count(o) from Orgao o where lower(o.nome) = :orgaoNome and o.id <> :orgaoId");
		query.setParameter("orgaoNome", orgao.getNome().toLowerCase());
		query.setParameter("orgaoId", orgao.getId());
		return (long) query.getSingleResult() > 0;
	}

	public boolean isOrgaoAssociadoAUmServidor(Orgao orgao) {
		Query query = entityManager.createQuery("select count(s) from Servidor s where s.orgao = :orgao");
		query.setParameter("orgao", orgao);
		return (long) query.getSingleResult() > 0;
	}

	public Orgao buscarPorId(long id) {
		return entityManager.find(Orgao.class, id);
	}
}
