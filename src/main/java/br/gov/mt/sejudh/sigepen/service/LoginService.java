package br.gov.mt.sejudh.sigepen.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.LoginDAO;
import br.gov.mt.sejudh.sigepen.model.Usuario;

@Stateless
public class LoginService {

	@Inject
	private LoginDAO dao;

	public Usuario buscarUsuario(String loginUsuario) {
		return dao.buscarUsuario(loginUsuario);
	}
}
