package br.gov.mt.sejudh.sigepen.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.omg.CORBA.portable.ApplicationException;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroServidor;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAcessoLog;
import br.gov.mt.sejudh.sigepen.enumerator.TipoBloqueioLog;
import br.gov.mt.sejudh.sigepen.model.Acesso;
import br.gov.mt.sejudh.sigepen.model.LogAcesso;
import br.gov.mt.sejudh.sigepen.model.ParametroUnidade;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.model.dto.UsuarioResertSenhaDTO;
import br.gov.mt.sejudh.sigepen.realm.SigepenRealm;
import br.gov.mt.sejudh.sigepen.service.LogAcessoBO;
import br.gov.mt.sejudh.sigepen.service.LoginService;
import br.gov.mt.sejudh.sigepen.service.ParametroUnidadeService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.service.UsuarioService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@RequestScoped
@URLMappings(mappings = { @URLMapping(id = "login", pattern = "/login", viewId = "/WEB-INF/pages/login/Login.xhtml"),
		@URLMapping(id = "erro", pattern = "/erro", viewId = "/WEB-INF/pages/erro.xhtml"),
		@URLMapping(id = "ResetSenha", pattern = "/resetSenha", viewId = "/WEB-INF/pages/login/ResetSenha.xhtml"),
		@URLMapping(id = "SelecionarUnidade", pattern = "/login/unidade", viewId = "/WEB-INF/pages/login/Unidade.xhtml") })
public class LoginBean {

	private String usuario;
	private String senha;
	private Date nascimento;
	private UnidadePrisional unidadeSelecionada;
	private List<UnidadePrisional> unidades;
	private UsuarioResertSenhaDTO usuarioResertSenhaDTO;

	@Inject
	private FacesUtil faces;

	@Inject
	private LoginService service;

	@Inject
	private Logger log;

	@Inject
	private ParametroUnidadeService parametroUnidadeService;

	@Inject
	private LogAcessoBO logAcessoBO;

	@Inject
	private UsuarioService usuarioService;

	@Inject
	private ServidorService servidorService;

	@PostConstruct
	public void init() {
		UnidadePrisional uni = SecurityUtil.getUnidadeSelecionada();

		if (uni != null) {
			this.unidadeSelecionada = uni;
		}
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = Sigepen.formatarEspacos(usuario);
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public UnidadePrisional getUnidadeSelecionada() {
		return unidadeSelecionada;
	}

	public void setUnidadeSelecionada(UnidadePrisional unidadeSelecionada) {
		this.unidadeSelecionada = unidadeSelecionada;
	}

	public Date getNascimento() {
		return this.nascimento;
	}

	public void setNascimento(Date nascimento) {
		this.nascimento = nascimento;
	}

	public UsuarioResertSenhaDTO getUsuarioResertSenhaDTO() {
		return usuarioResertSenhaDTO;
	}

	public void setUsuarioResertSenhaDTO(UsuarioResertSenhaDTO usuarioResertSenhaDTO) {
		this.usuarioResertSenhaDTO = usuarioResertSenhaDTO;
	}

	public List<UnidadePrisional> getUnidades() {
		if (this.unidades == null) {
			this.unidades = new ArrayList<UnidadePrisional>();

			for (Acesso acesso : SecurityUtil.getUsuario().getAcessos()) {
				this.unidades.add(acesso.getUnidade());
			}
		}

		this.unidades.sort(Comparator.comparing(UnidadePrisional::getNome));

		return unidades;
	}

	public void setUnidades(List<UnidadePrisional> unidades) {
		this.unidades = unidades;
	}

	private void clearCache() {
		for (Realm realm : ((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms()) {
			if (realm.getName().equals("jdbcRealm")) {
				SigepenRealm realm2 = (SigepenRealm) realm;
				realm2.getAuthorizationCache().remove(SecurityUtils.getSubject().getPrincipals());
			}
		}
	}

	public void login() throws ApplicationException {
		try {
			SecurityUtils.getSubject().login(new UsernamePasswordToken(usuario, senha));
			clearCache();
			SecurityUtils.getSubject().getSession().setAttribute("usuario",
					service.buscarUsuario(SecurityUtil.getLoginUsuario()));
			LoggerUtil.logInfo(Acao.LOGAR, usuario, log);
			FacesContext.getCurrentInstance().getExternalContext().redirect(Sigepen.APPLICATION_DASHBOARD_URL);
			gravarLogAcesso(TipoAcessoLog.SUCESSO_LOGIN, TipoBloqueioLog.NENHUM, usuario);
		} catch (UnknownAccountException e) {
			faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar login", "Usuário ou senha inválidos.");
			gravarLogAcesso(TipoAcessoLog.FALHA_LOGIN, TipoBloqueioLog.NENHUM, usuario);
		} catch (IncorrectCredentialsException e) {
			faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar login", "Usuário ou senha inválidos.");
			gravarLogAcesso(TipoAcessoLog.TENTATIVA_LOGIN, TipoBloqueioLog.NENHUM, usuario);
		} catch (AuthenticationException e) {
			faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar login", e.getMessage());
			gravarLogAcesso(TipoAcessoLog.FALHA_LOGIN, TipoBloqueioLog.NENHUM, usuario);
		} catch (Exception e) {
			faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar login", e.getMessage());
			invalidarLogin();
			LoggerUtil.logErro(e, log);
			gravarLogAcesso(TipoAcessoLog.FALHA_LOGIN, TipoBloqueioLog.NENHUM, usuario);
		}
	}

	private void gravarLogAcesso(TipoAcessoLog tipoAcessoLog, TipoBloqueioLog tipoBloqueio,
			String loginUsuarioAutorizador) throws ApplicationException {

		LogAcesso logAcesso = new LogAcesso();

		String ipUsuario = Optional.ofNullable(FacesUtil.getRequest().getHeader("X-FORWARDED-FOR"))
				.filter(ip -> !ip.trim().isEmpty()).orElseGet(() -> FacesUtil.getRequest().getRemoteAddr());

		logAcesso.setDataHoraAcesso(new Date());
		logAcesso.setLogin(loginUsuarioAutorizador);
		logAcesso.setTipoLogAcesso(tipoAcessoLog.getName());
		logAcesso.setTipoBloqueio(tipoBloqueio.name());
		logAcesso.setIp(ipUsuario);

		Optional<Usuario> usuarioOpt = Optional
				.ofNullable(this.usuarioService.buscarUsuarioPorLogin(loginUsuarioAutorizador));
		logAcesso.setUsuario(usuarioOpt.orElse(null));

		this.logAcessoBO.salvarLogAcesso(logAcesso);
	}

	public void logout() throws IOException {
		try {
			invalidarLogin();
			FacesContext.getCurrentInstance().getExternalContext().redirect(Sigepen.APPLICATION_LOGIN_URL);
		} catch (Exception e) {

		}
	}

	private void invalidarLogin() throws ApplicationException {
		String usuarioAtual = (String) SecurityUtils.getSubject().getPrincipal();
		SecurityUtils.getSubject().logout();
		gravarLogAcesso(TipoAcessoLog.LOGOUT, TipoBloqueioLog.NENHUM, usuarioAtual);
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	}

	public void setarUnidade() throws IOException {
		ativarModuloVisitante();
		SecurityUtil.setUnidadeSelecionada(unidadeSelecionada);
		clearCache();
		LoggerUtil.logInfo(Acao.LOGAR, unidadeSelecionada, log);
		FacesContext.getCurrentInstance().getExternalContext().redirect(Sigepen.APPLICATION_DASHBOARD_URL);
	}

	private void ativarModuloVisitante() {
		List<ParametroUnidade> parametros = parametroUnidadeService.listarPorUnidade(unidadeSelecionada);
		unidadeSelecionada.setAtivaModuloVisitante(parametros.size() > 0
				&& parametros.stream().anyMatch(parametroUnidade -> parametroUnidade.getValidar() == SimNao.S));
	}

	public void resetSenha() {

		Usuario usuario = this.usuarioService.buscarUsuariosPorCpfAndNascimento(this.usuario, this.nascimento);

		if (usuario != null) {
			this.usuarioResertSenhaDTO.setUsuario(usuario);
			this.setListaMatriculaENomeDaMae(usuario.getServidor().getCpf(), usuario.getServidor().getMae());
		}

	}

	private void setListaMatriculaENomeDaMae(String cpf, String mae) {
		FiltroServidor filtroServidor = new FiltroServidor();

		filtroServidor.setCpf(this.pegarCincosPrimeirosCaracters(cpf));
		filtroServidor.setMae(this.pegarCincosPrimeirosCaracters(mae));

		List<ServidorDTO> servidorDTOs = servidorService.buscaPorFiltro(filtroServidor, false);

		this.listaRandomMatriculaENomeMae(servidorDTOs);

	}

	private String pegarCincosPrimeirosCaracters(String string) {

		if (string != null && string.length() >= 5) {
			return string.substring(0, string.length());
		}

		return string;
	}

	private void listaRandomMatriculaENomeMae(List<ServidorDTO> lista) {

		if (lista.size() < 3) {
			System.out.println("A lista deve conter pelo menos 3 elementos!");
			throw new IllegalArgumentException("A lista deve conter pelo menos 3 elementos!");
		}

		Random random = new Random();

		while (this.usuarioResertSenhaDTO.getMatriculaList().size() < 3 && this.usuarioResertSenhaDTO.getNomeMaeList().size() < 3) {
			int index = random.nextInt(lista.size());
			this.usuarioResertSenhaDTO.getMatriculaList().add(lista.get(index).getMatricula());
			this.usuarioResertSenhaDTO.getNomeMaeList().add(lista.get(index).getMae());
		}

	}


}
