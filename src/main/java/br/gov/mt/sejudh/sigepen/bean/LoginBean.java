package br.gov.mt.sejudh.sigepen.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import br.gov.mt.sejudh.sigepen.model.dto.UsuarioResetSenhaDTO;
import br.gov.mt.sejudh.sigepen.realm.SigepenRealm;
import br.gov.mt.sejudh.sigepen.service.LogAcessoBO;
import br.gov.mt.sejudh.sigepen.service.LoginService;
import br.gov.mt.sejudh.sigepen.service.ParametroUnidadeService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
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
	private UnidadePrisional unidadeSelecionada;
	private List<UnidadePrisional> unidades;
	private List<UnidadePrisional> unidadesResetSenha;
	private UsuarioResetSenhaDTO usuarioResetSenhaDTO;
	private boolean resetSenha;
	private boolean unidadeErrada;
	private boolean isRedirectResetSenha;

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

	@Inject
	private UnidadePrisionalService unidadePrisionalService;

	@PostConstruct
	public void init() {
		UnidadePrisional uni = SecurityUtil.getUnidadeSelecionada();
		if(SecurityUtils.getSubject().getSession().getAttribute("unidadeErrada") != null) {
			this.unidadeErrada = (boolean) SecurityUtils.getSubject().getSession().getAttribute("unidadeErrada");
		}

		if (this.usuarioResetSenhaDTO == null) {
			this.usuarioResetSenhaDTO = new UsuarioResetSenhaDTO();
		}

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

	public UsuarioResetSenhaDTO getUsuarioResertSenhaDTO() {
		return usuarioResetSenhaDTO;
	}

	public void setUsuarioResertSenhaDTO(UsuarioResetSenhaDTO usuarioResertSenhaDTO) {
		this.usuarioResetSenhaDTO = usuarioResertSenhaDTO;
	}

	public boolean isResetSenha() {
		return resetSenha;
	}

	public void setUnidadesResetSenha(List<UnidadePrisional> unidadesResetSenha) {
		this.unidadesResetSenha = unidadesResetSenha;
	}

	public boolean getIsRedirectResetSenha(){
		return this.isRedirectResetSenha;
	}

	public void setResetSenha(boolean resetSenha) {
		this.resetSenha = resetSenha;
	}

	public boolean isUnidadeErrada() {
		return unidadeErrada;
	}

	public void setUnidadeErrada(boolean unidadeErrada) {
		SecurityUtils.getSubject().getSession().setAttribute("unidadeErrada", unidadeErrada);
		this.unidadeErrada = unidadeErrada;
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

	public List<UnidadePrisional> getUnidadesResetSenha() {

		this.unidadesResetSenha = this.unidadePrisionalService.listarUnidadesNaoInativas();
		
		if (this.unidadesResetSenha != null) {

			this.unidadesResetSenha.sort(Comparator.comparing(UnidadePrisional::getNome));

			return unidadesResetSenha;
		}

		return new ArrayList<UnidadePrisional>();
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
			// faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar login",
			// e.getMessage());
			this.resetSenha = true;
			gravarLogAcesso(TipoAcessoLog.FALHA_LOGIN, TipoBloqueioLog.NENHUM, usuario);
		} catch (Exception e) {
			faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar login", e.getMessage());
			invalidarLogin();
			LoggerUtil.logErro(e, log);
			gravarLogAcesso(TipoAcessoLog.FALHA_LOGIN, TipoBloqueioLog.NENHUM, usuario);
		}
	}

	public String redicetResetSenha() {
		return "ResetSenha?faces-redirect=true";
	}

	private void gravarLogAcesso(TipoAcessoLog tipoAcessoLog, TipoBloqueioLog tipoBloqueio,
			String loginUsuarioAutorizador) throws ApplicationException {

		LogAcesso logAcesso = new LogAcesso();

		String ipUsuario = Optional.ofNullable(FacesUtil.getRequest().getHeader("X-Real-IP-Port"))
                .filter(ipPort -> !ipPort.trim().isEmpty())
                .orElseGet(() -> FacesUtil.getRequest().getRemoteAddr());

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

	public void setarUnidadeResetSenha() throws IOException {
		this.usuarioResetSenhaDTO = (UsuarioResetSenhaDTO) SecurityUtils.getSubject().getSession()
				.getAttribute("usuarioResertSenhaDTO");
		this.usuarioResetSenhaDTO.getUsuario().setUnidadePerfilSolicitadaResetSenha(this.unidadeSelecionada.getNome());
		SecurityUtils.getSubject().getSession().setAttribute("usuarioResertSenhaDTO", this.usuarioResetSenhaDTO);
	}

	private void ativarModuloVisitante() {
		List<ParametroUnidade> parametros = parametroUnidadeService.listarPorUnidade(unidadeSelecionada);
		unidadeSelecionada.setAtivaModuloVisitante(parametros.size() > 0
				&& parametros.stream().anyMatch(parametroUnidade -> parametroUnidade.getValidar() == SimNao.S));
	}

	public void resetSenha() {

		Usuario usuario = this.usuarioService.buscarUsuariosPorCpfAndNascimento(this.usuarioResetSenhaDTO.getCpf(),
				this.usuarioResetSenhaDTO.getNascimento());

		if (usuario == null) {
			faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro na solicitação!",
					"Usuário, os dados informados não correspondem com a base de dados. Entre em contato com o adminstrador do sistema.");
			return;
		}

		usuario.setUnidadePerfilSolicitadaResetSenha(null);
		this.usuarioResetSenhaDTO.setUsuario(usuario);

		this.setListaNomeDaMae(usuario.getServidor().getMae());
		SecurityUtils.getSubject().getSession().setAttribute("usuarioResertSenhaDTO", this.usuarioResetSenhaDTO);

	}

	public void verificaNomeMae() {
		int index = this.usuarioResetSenhaDTO.getIndexNomeMaeSelecionada();
		
		this.usuarioResetSenhaDTO = (UsuarioResetSenhaDTO) SecurityUtils.getSubject().getSession()
				.getAttribute("usuarioResertSenhaDTO");
		
		String nomeMaeSelecionado = this.usuarioResetSenhaDTO.getNomeMaeList().get(index);
		
		if (nomeMaeSelecionado.equals(this.usuarioResetSenhaDTO.getUsuario().getServidor().getMae())) {
			this.setListaMatricula(String.valueOf(this.usuarioResetSenhaDTO.getUsuario().getServidor().getMatricula()));
			return;
		}

		this.limparResetSenha();
		
		faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro na solicitação!",
				"Usuário, os dados informados não correspondem com a base de dados. Entre em contato com o adminstrador do sistema.");

	}

	private void limparResetSenha() {

		if(this.usuarioResetSenhaDTO != null) {

			this.usuarioResetSenhaDTO.getMatriculaList().clear();
			this.usuarioResetSenhaDTO.getNomeMaeList().clear();
		}
		this.unidadeErrada = false;

		SecurityUtils.getSubject().getSession().setAttribute("usuarioResertSenhaDTO", null);
		SecurityUtils.getSubject().getSession().setAttribute("usuario", null);
		
		this.usuarioResetSenhaDTO = null;
	}

	public void verificaMatricula() throws Exception {

		int index = this.usuarioResetSenhaDTO.getIndexMatriculaSelecionada();
		
		this.usuarioResetSenhaDTO = (UsuarioResetSenhaDTO) SecurityUtils.getSubject().getSession()
				.getAttribute("usuarioResertSenhaDTO");
		
		Long MatriculaSelecionado = this.usuarioResetSenhaDTO.getMatriculaList().get(index);

		if (MatriculaSelecionado.equals(this.usuarioResetSenhaDTO.getUsuario().getServidor().getMatricula())) {

			this.enviarResetSenha();
			return;

		}

		this.limparResetSenha();
		
		faces.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro na solicitação!",
				"Usuário, os dados informados não correspondem com a base de dados. Entre em contato com o adminstrador do sistema.");
	}

	private void enviarResetSenha() throws Exception {
		Usuario usuario = this.usuarioResetSenhaDTO.getUsuario();
		usuario.setResetSenha(true);
		usuario.setDataSolicitacaoResetSenha(new Date());

		SecurityUtils.getSubject().getSession().setAttribute("usuario", usuario);

		if(!this.unidadeErrada){
			usuario.setUnidadePerfilSolicitadaResetSenha(null);
		}

		this.usuarioService.salvar(usuario);
		this.isRedirectResetSenha = true;

		SecurityUtils.getSubject().getSession().setAttribute("usuario", null);
		SecurityUtils.getSubject().getSession().setAttribute("usuarioResertSenhaDTO", null);

		this.limparResetSenha();

		return ;
	}

	public void redirectLogin() throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().redirect(Sigepen.APPLICATION_LOGIN_URL);
	}

	@SuppressWarnings("unlikely-arg-type")
	private void setListaMatricula(String matricula) {
		Set<Long> list = new HashSet<Long>();

		FiltroServidor filtroServidor = new FiltroServidor();

		filtroServidor.setMatriculaLike(this.pegarTresPrimeirosCaracters(matricula));

		List<ServidorDTO> servidorDTOs = servidorService.buscaPorFiltro(filtroServidor, false);

		if (servidorDTOs.size() < 3) {
			System.out.println("A lista deve conter pelo menos 3 elementos!");
			throw new IllegalArgumentException("A lista deve conter pelo menos 3 elementos!");
		}

		Random random = new Random();

		servidorDTOs.removeIf(servidorDTO -> servidorDTO.getMatricula() == this.usuarioResetSenhaDTO.getUsuario().getServidor().getMatricula());
		servidorDTOs.remove(this.usuarioResetSenhaDTO.getUsuario());
		
		this.usuarioResetSenhaDTO.getMatriculaList().clear();
		
		while (list.size() < 2) {
			int index = random.nextInt(servidorDTOs.size());
			list.add(servidorDTOs.get(index).getMatricula());
		}
		
		list.add(this.usuarioResetSenhaDTO.getMatricula());
		this.usuarioResetSenhaDTO.getMatriculaList().addAll(list);
		Collections.shuffle(this.usuarioResetSenhaDTO.getMatriculaList());

	}

	@SuppressWarnings("unlikely-arg-type")
	private void setListaNomeDaMae(String mae) {
		Set<String> list = new HashSet<String>();

		FiltroServidor filtroServidor = new FiltroServidor();

		filtroServidor.setMae(this.pegarTresPrimeirosCaracters(mae));

		List<ServidorDTO> servidorDTOs = servidorService.buscaPorFiltro(filtroServidor, false);

		if (servidorDTOs.size() < 3) {
			System.out.println("A lista deve conter pelo menos 3 elementos!");
			throw new IllegalArgumentException("A lista deve conter pelo menos 3 elementos!");
		}

		Random random = new Random();
		servidorDTOs.removeIf(servidorDTO -> servidorDTO.getMae().equals(this.usuarioResetSenhaDTO.getUsuario().getServidor().getMae()));
		
		this.usuarioResetSenhaDTO.getNomeMaeList().clear();
		
		while (list.size() < 2) {
			int index = random.nextInt(servidorDTOs.size());
			list.add(servidorDTOs.get(index).getMae());
		}
		
		list.add(this.usuarioResetSenhaDTO.getNomeMae());
		
		this.usuarioResetSenhaDTO.getNomeMaeList().addAll(list);
		Collections.shuffle(this.usuarioResetSenhaDTO.getNomeMaeList());

	}

	private String pegarTresPrimeirosCaracters(String string) {

		int quantCaracter = 3;

		if (string != null && string.length() >= quantCaracter) {
			return string.substring(0, quantCaracter);
		}

		return string;
	}

}
