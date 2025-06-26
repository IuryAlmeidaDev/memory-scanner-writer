package br.gov.mt.sejudh.sigepen.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import br.gov.mt.sejudh.sigepen.dao.AuditoriaDAO;
import br.gov.mt.sejudh.sigepen.dao.UsuarioDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUsuario;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Acesso;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.model.dto.UsuarioDTO;
import br.gov.mt.sejudh.sigepen.security.HashedPassSaltGenerator;
import br.gov.mt.sejudh.sigepen.util.EJBExcInterceptor;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@Stateless
public class UsuarioService {
	
	@Inject
	private UsuarioDAO dao;
	
	@Inject
	private AuditoriaDAO auditDAO;

	@Inject
	private AcessoService acessoService;
	
	private Usuario configurarUsuario(Usuario usuario, boolean senhaMudou) {
		if (usuario.getId() <= 0) {
			usuario.setLogin(usuario.getServidor().getCpf());
			usuario.setDataCadastro(new Date());
			usuario.setPrimeiroAcesso(true);
			usuario = HashedPassSaltGenerator.gerarSenhaESalt(usuario, usuario.getServidor().getCpf());
		}
		
		if (senhaMudou) {
			usuario.setPrimeiroAcesso(false);
			usuario = HashedPassSaltGenerator.gerarSenhaESalt(usuario, usuario.getSenha());
		}
		
		if (usuario.getPrimeiroAcesso()) {
			usuario = HashedPassSaltGenerator.gerarSenhaESalt(usuario, usuario.getServidor().getCpf());
		}
		
		return usuario;
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public Usuario salvar(Usuario usuario, boolean senhaMudou) throws Exception {
		if (usuario.getServidor() == null) {
			throw new ValidacaoException("É necessário associar um servidor.");
		}
		
		if (usuario.getDataExpiracao() == null) {
			throw new ValidacaoException("É necessário preencher a data de expiração.");
		}
		
		if (usuario.getDataExpiracao().before(Sigepen.getDataAtual())) {
			throw new ValidacaoException("A data de expiração não pode ser menor ou igual que a data de hoje.");
		}
		
		if (usuarioNaoPossuiUnidadeDeAcesso(usuario)) {
			throw new ValidacaoException("É necessário adicionar pelo menos uma unidade de acesso.");
		}
		
		if (usuario.getId() > 0 && usuario.getSenha().length() < 6) {
			throw new ValidacaoException("A senha deve conter no mínimo 6 caracteres.");
		}
		
		validarSeUsuarioPossuiAcessoSemPerfil(usuario);
		
		usuario = dao.salvar(configurarUsuario(usuario, senhaMudou));
				
		if (usuario != null) {
			usuario.setAutorRegistro(auditDAO.autorRegistro(usuario, usuario.getId()));
			usuario.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(usuario, usuario.getId()));
		}
		
		return usuario;
	}
	
	@Interceptors(EJBExcInterceptor.class)
	public Usuario salvar(Usuario usuario) throws Exception {
		
		usuario = dao.salvar(usuario);
				
		if (usuario != null) {
			usuario.setAutorRegistro(auditDAO.autorRegistro(usuario, usuario.getId()));
			usuario.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(usuario, usuario.getId()));
		}
		
		return usuario;
	}
	
	private void validarSeUsuarioPossuiAcessoSemPerfil(Usuario usuario) throws ValidacaoException {
		List<Acesso> acessos = usuario.getAcessos();
		
		for (Acesso acesso : acessos) {
			if (acesso.getPerfis().isEmpty()) {
				throw new ValidacaoException("É necessário adicionar pelo menos um perfil a unidade de acesso " + acesso.getUnidade().getNome());
			}
		}
	}

	private boolean usuarioNaoPossuiUnidadeDeAcesso(Usuario usuario) {
		return usuario.getAcessos().isEmpty();
	}

	public Usuario buscarUsuarioPorId(long usuarioId) {
		Usuario usuario = dao.buscarUsuarioPorId(usuarioId);
		
		if (usuario != null) {
			usuario.setAutorRegistro(auditDAO.autorRegistro(usuario, usuario.getId()));
			usuario.setAutorUltimaAlteracao(auditDAO.autorUltimaAlteracao(usuario, usuario.getId()));
		}
		
		return usuario;
	}

	public int buscarQuantidadeFiltrada(FiltroUsuario filtro) {
		return dao.buscarQuantidadeFiltrada(filtro);
	}

	public List<UsuarioDTO> buscarUsuariosPorFiltro(FiltroUsuario filtro) {
		List<UsuarioDTO> usuarioDTOS = dao.buscarUsuariosPorFiltro(filtro);

		for (UsuarioDTO dto : usuarioDTOS) {
			if(filtro.getUnidadePrisional() != null ){
				dto.setAcessos(acessoService.buscarAcessosPorUsuarioIdAndUnidadeId(dto.getId(),filtro.getUnidadePrisional().getId()));
			}else{
				dto.setAcessos(acessoService.buscarAcessosPorUsuarioId(dto.getId()));
			}
		}

		return usuarioDTOS;
	}
	
	public Usuario buscarUsuariosPorCpfAndNascimento(String cpf, Date nascimento) {
		return dao.buscarUsuariosPorCpfAndNascimento(cpf, nascimento);
	}


	public void excluir(long usuarioId) throws Exception {		
		dao.excluir(usuarioId);
	}

	public boolean isServidorJaAssociadoAUmUsuario(long servidorId) {
		return dao.isServidorJaAssociadoAUmUsuario(servidorId);
	}

	public Usuario buscarUsuarioPorLogin(String loginUsuarioAutorizador) {
		return dao.buscarUsuarioPorLogin(loginUsuarioAutorizador);
	}

	public int conutSolicitacaoSenha(FiltroUsuario filtroUsuario){
		return dao.buscarQuantidadeFiltrada(filtroUsuario);
	}
}
