package br.gov.mt.sejudh.sigepen.dao;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPerfil;
import br.gov.mt.sejudh.sigepen.model.Perfil;
import br.gov.mt.sejudh.sigepen.model.Permissao;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@RequestScoped
public class PerfilDAO {

	@Inject
	private EntityManager em;
	
	public void salvar(Perfil perfil) throws Exception {
		try {
			if (perfil.getId() > 0) {
				em.merge(perfil);
			} else {
				em.persist(perfil);
			}
		} catch (Exception e){
			throw new Exception(e);
		}
	}
	
	public void excluir(Perfil perfil) throws Exception {
		try {
			perfil = em.find(Perfil.class, perfil.getId());
		
			if(perfil != null) {
				em.remove(perfil);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public List<Permissao> listarPermissoesNegadas(List<Permissao> permissoesConcedidas, long idFuncionalidade) {
		List<Long> listaIdsPermissoes = new ArrayList<Long>();
		TypedQuery<Permissao> query;
		
		if(permissoesConcedidas != null && permissoesConcedidas.size() > 0) {
			for (Permissao perm : permissoesConcedidas) {
				listaIdsPermissoes.add(perm.getId());
			}
			
			query = em.createQuery("select p from Permissao p where p.id not in (:permissoes) and p.funcionalidade.id = :idFuncionalidade order by p.nome asc", Permissao.class);
			query.setParameter("permissoes", listaIdsPermissoes);
		} else {
			query = em.createQuery("select p from Permissao p where p.funcionalidade.id = :idFuncionalidade order by p.nome asc", Permissao.class);
		}

		query.setParameter("idFuncionalidade", idFuncionalidade);
		return query.getResultList();
	}

	public boolean isPerfilAssociadoAUmUsuario(long id) {
		Query query = em.createNativeQuery("select count(a.perfil_id) count from " + Sigepen.SEJUDH_SCHEMA + ".acesso_perfil a where a.perfil_id = :idPerfil");
		query.setParameter("idPerfil", id);		
		return Integer.parseInt(query.getSingleResult().toString()) > 0;
	}
	
	public Perfil jaExistePerfilComMesmoNome(String perfilNome) {
		try {
			//id 0 pertence ao usuário inicial do sistema.
			TypedQuery<Perfil> query = em.createQuery("select p from Perfil p where lower(p.nome) = :perfilNome and p.id > 0", Perfil.class);
			query.setParameter("perfilNome", perfilNome.toLowerCase());
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Perfil> buscarPerfisPorFiltro(FiltroPerfil filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Perfil.class, em);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}
	
	public int buscarQuantidadeFiltrada(FiltroPerfil filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Perfil.class, em);	
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}
	
	public List<Permissao> buscarPermissoesDoPerfil(Perfil perfil) {
		TypedQuery<Permissao> query = em.createQuery("select p from Permissao p join fetch p.perfis pp where pp.id = :idPerfil", Permissao.class);
		query.setParameter("idPerfil", perfil.getId());
		return query.getResultList();
	}
	
	public List<Permissao> buscarPermissoesPorFuncionalidadeEPerfil(long idFuncionalidade, long idPerfil) {
		TypedQuery<Permissao> query = em.createQuery("select p from Permissao p join fetch p.perfis pf where p.funcionalidade.id = :idFuncionalidade and pf.id = :idPerfil", Permissao.class);
		query.setParameter("idFuncionalidade", idFuncionalidade);
		query.setParameter("idPerfil", idPerfil);
		return query.getResultList();
	}

	public List<Perfil> listarPerfisDisponiveis(List<Perfil> perfis) {
		TypedQuery<Perfil> query;
		
		if (perfis == null || perfis.isEmpty()) {
			query = em.createQuery("select p from Perfil p where p.id > 0 order by p.nome asc", Perfil.class);
		} else {
			List<Long> listaIdsPerfis = new ArrayList<Long>();
			
			for (Perfil perfil : perfis) {
				listaIdsPerfis.add(perfil.getId());
			}
			
			query = em.createQuery("select p from Perfil p where p.id > 0 and p.id not in (:perfis) order by p.nome asc", Perfil.class);
			query.setParameter("perfis", listaIdsPerfis);
		}
		
		return query.getResultList();
	}

	public Perfil buscarPorId(long id) {
		return em.find(Perfil.class, id);
	}
}