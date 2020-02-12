package br.gov.mt.sejudh.sigepen.dao;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUsuario;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.model.dto.UsuarioDTO;

@RequestScoped
public class UsuarioDAO {
	
	@Inject
	private EntityManager em; 
	
	public Usuario salvar(Usuario usuario) throws Exception {
		try {
			if (usuario.getId() > 0) {
				em.merge(usuario);
			} else {
				em.persist(usuario);
			}
		} catch (Exception e){
			throw e;
		}
		
		return usuario;
	}
	
	public void excluir(long usuarioId) throws Exception {
		try {
			Usuario usuario =  buscarUsuarioPorId(usuarioId);
			em.remove(em.merge(usuario));
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Usuario buscarUsuarioPorId(long id) {
		try {
			String jpql="select u from Usuario u where u.id = :id";
			TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
			query.setParameter("id", id);
			Usuario u = query.getSingleResult();
			Hibernate.initialize(u.getAcessos());
			return u;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<UsuarioDTO> buscarUsuariosPorFiltro(FiltroUsuario filtro) {
		Criteria criteria = filtro.criarCriteriaParaBusca(Usuario.class, em);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		criteria.setResultTransformer(new AliasToBeanResultTransformer(UsuarioDTO.class));
		return criteria.list();
	}

	public int buscarQuantidadeFiltrada(FiltroUsuario filtro) {
		Criteria criteria = filtro.criarCriteriaParaQuantidade(Usuario.class, em);	
		criteria.setProjection(Projections.rowCount());		
		return ((Number) criteria.uniqueResult()).intValue();
	}

	public boolean isServidorJaAssociadoAUmUsuario(long servidorId) {
		Query query = em.createQuery("select count(u) from Usuario u where u.servidor.id = :servidorId");
		query.setParameter("servidorId", servidorId);
		return (long) query.getSingleResult() > 0;
	}

	public Usuario buscarUsuarioPorLogin(String loginUsuarioAutorizador) {
		try {
			String jpql="select u from Usuario u where u.login = :login";
			TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class);
			query.setParameter("login", loginUsuarioAutorizador);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}