package br.gov.mt.sejudh.sigepen.dao;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoFaltaDisciplinar;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.model.TipoFaltaDisciplinar;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;


@RequestScoped
public class TipoFaltaDisciplinarDAO {
	
	@Inject
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void salvar(TipoFaltaDisciplinar tipoFaltaDisciplinar) {
		if(tipoFaltaDisciplinar.getId()>0){
			tipoFaltaDisciplinar = entityManager.merge(tipoFaltaDisciplinar);
		}else{
			entityManager.persist(tipoFaltaDisciplinar);
		}
	}

	public void excluir(TipoFaltaDisciplinar tipoFaltaDisciplinar) {
		entityManager.remove(entityManager.merge(tipoFaltaDisciplinar));
	}	


	@SuppressWarnings("unchecked")
	public List<TipoFaltaDisciplinar> buscaPorFiltro(FiltroTipoFaltaDisciplinar filtro, boolean isRelatorio) {
		Criteria criteria = filtro.criarCriteriaParaBusca(TipoFaltaDisciplinar.class, entityManager, isRelatorio);
		return criteria.list();
	}	
	
	public int quantidadeFiltrados(FiltroTipoFaltaDisciplinar filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(TipoFaltaDisciplinar.class, entityManager);
	    criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	

	@SuppressWarnings("unchecked")
	public List<TipoFaltaDisciplinar> listarTipoFaltaDisciplinarAtivo() {
		String jpql="select tfd from TipoFaltaDisciplinar tfd where tfd.status = :status order by tfd.descricao";
		Query query = entityManager.createQuery(jpql, TipoFaltaDisciplinar.class);
		query.setParameter("status", Status.ATIVO);
		List<TipoFaltaDisciplinar> tipoFaltaDisciplinars = query.getResultList();
		return tipoFaltaDisciplinars;
	}

	public boolean jaExisteTipoFaltaDisciplinarComMesmaDescricao(TipoFaltaDisciplinar tipoFaltaDisciplinar) {
		Query query = entityManager.createQuery("select count(tfd) from TipoFaltaDisciplinar tfd where lower(tfd.descricao) = :descricao and tfd.id <> :tipoFaltaId");
		query.setParameter("descricao", tipoFaltaDisciplinar.getDescricao().toLowerCase());
		query.setParameter("tipoFaltaId", tipoFaltaDisciplinar.getId());
		return (long) query.getSingleResult() > 0;
	}


	public boolean isTipoFaltaDisciplinarAssociadoAUmaFaltaDisciplinar(TipoFaltaDisciplinar tipoFaltaDisciplinar) {
		Query query = entityManager.createQuery("select count(fd) from FaltaDisciplinar fd where fd.tipo = :tipoFaltaDisciplinar");
		query.setParameter("tipoFaltaDisciplinar", tipoFaltaDisciplinar);
		return (long) query.getSingleResult() > 0;
	}

	public TipoFaltaDisciplinar buscarPorId(long id) {
		return entityManager.find(TipoFaltaDisciplinar.class, id);
	}
}
