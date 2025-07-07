package br.gov.mt.sejudh.sigepen.dao;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.apache.shiro.authc.UnknownAccountException;
import org.hibernate.Hibernate;

import br.gov.mt.sejudh.sigepen.model.Usuario;

@RequestScoped
public class LoginDAO {

	@Inject
	private EntityManager em;

	public Usuario buscarUsuario(String loginUsuario) {
		try {
			TypedQuery<Usuario> query = em.createQuery("select u from Usuario u where u.login = :login", Usuario.class);
			query.setParameter("login", loginUsuario);
			Usuario u = query.getSingleResult();
			Hibernate.initialize(u.getAcessos());
			
			return u;
		} catch (NoResultException e) {
			throw new UnknownAccountException();
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional
	public Usuario buscarUsuarioPorLoginComPermissoes(String login) {
		TypedQuery<Usuario> query = em.createQuery(
				"SELECT u FROM Usuario u " +
						"JOIN FETCH u.acessos a " +
						"WHERE u.login = :login", Usuario.class
		);
		query.setParameter("login", login);
		Usuario usuario = query.getSingleResult();

		usuario.getAcessos().forEach(acesso -> {
			acesso.getPerfis().size();
			acesso.getPerfis().forEach(perfil -> perfil.getPermissoes().size());
		});

		return usuario;
	}
}