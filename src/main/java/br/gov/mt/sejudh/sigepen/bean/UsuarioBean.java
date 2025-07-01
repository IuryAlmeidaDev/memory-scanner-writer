package br.gov.mt.sejudh.sigepen.bean;

import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.model.*;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUsuario;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.model.dto.UsuarioDTO;
import br.gov.mt.sejudh.sigepen.service.EmailService;
import br.gov.mt.sejudh.sigepen.service.PerfilService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.service.UsuarioService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings ={
@URLMapping(id = "ListarUsuario", pattern = "/usuario", viewId = "/WEB-INF/pages/usuario/ListarUsuarios.xhtml")
,@URLMapping(id = "ListarUsuarioResetSenha", pattern = "/usuario/reset/senha", viewId = "/WEB-INF/pages/usuario/ListarUsuariosResetSenha.xhtml")
,@URLMapping(id = "CadastrarUsuario", pattern="/usuario/cadastrar", viewId = "/WEB-INF/pages/usuario/CadastrarUsuario.xhtml")
,@URLMapping(id = "AlterarUsuario", pattern="/usuario/alterar/#{id}", viewId = "/WEB-INF/pages/usuario/CadastrarUsuario.xhtml")
,@URLMapping(id = "VisualizarUsuario", pattern="/usuario/visualizar/#{id}", viewId = "/WEB-INF/pages/usuario/CadastrarUsuario.xhtml")
})
public class UsuarioBean extends MetodosDePaginaBean {

	private FiltroUsuario filtroUsuario;
	
	private Usuario usuario;
	
	private LazyDataModel<UsuarioDTO> usuarios;
	
	private Acesso acesso;
	
	private UnidadePrisional unidadeDoUsuario;
	
	private DualListModel<Perfil> perfisModel;

	private List<UnidadePrisional> unidadesPrisionais;
	
	@Inject
	private PerfilService perfilService;
	
	@Inject
	private UsuarioService usuarioService;
	
	@Inject
	private ServidorService servidorService;
	
	@Inject
	private UnidadePrisionalService unidadeService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	
	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		super.carregarFiltroPaginacao(FiltroUsuario.class, this);
	}
	
	@URLAction(onPostback=false)
	public String init(){
		try {
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.usuario = new Usuario(Status.ATIVO);
					break;
					
				case VISUALIZAR:
				case ALTERAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarUsuario(id)) {
						facesUtil.exibirMensagemErro("Usuário não encontrado.", true);
						return "pretty:ListarUsuário";
					}

					logDeAcesso(log, this.usuario);
					break;
					
				case LISTAR:
					unidadesPrisionais = unidadeService.listarUnidadesNaoInativas();
					pesquisar();
					logDeAcesso(log, "Usuários");
					break;
					
				default:
					break;
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}
	
	private boolean buscarUsuario(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.usuario = usuarioService.buscarUsuarioPorId(id);
		
		if (this.usuario == null) {
			return false;
		}
		
		return true;
	}
	
	public void pesquisar() {
		this.setUsuarios(new LazyDataModel<UsuarioDTO>() {
			private static final long serialVersionUID = 1L;
			
			public List<UsuarioDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				if(PrettyContext.getCurrentInstance().getCurrentMapping().getPattern().contains("reset")) {
					getFiltro().setResetSenha(true);
				}
				setRowCount(usuarioService.buscarQuantidadeFiltrada(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return usuarioService.buscarUsuariosPorFiltro(getFiltro());
			}
		});
	}

	public void imprimir() {
		try {

			if (getFiltro().getUnidadePrisional() == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Para impressão é necessário selecionar uma unidade");
				return;
			}

			List<UsuarioDTO> lista = usuarioService.buscarUsuariosPorFiltro(getFiltro());

			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}

			Map<String, Object> parameters = new HashMap<>();

			parameters.put("UNIDADE_FILTRO", getFiltro().getUnidadePrisional().getNome());

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarPerfilUsuario,parameters));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Perfis", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de Perfis: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Perfis", e, log);
		}
	}

	public void salvar() {
		boolean resetSenha = this.usuario.getResetSenha();
		try {			
			if (this.usuario.getEmail() == null || this.usuario.getEmail().equals("")) {
				throw new ValidacaoException("Email do Usuário é obrigatório.");
			}
			
			if(resetSenha) {
				this.usuario.setResetSenha(false);
			}
			
			this.usuario = usuarioService.salvar(this.usuario, false);
			
			if(resetSenha) {
				EmailService.sendEmailResetSenha(usuario);
			}			
			
			facesUtil.exibirMensagemInfo("Usuário " + this.usuario.getLogin() + " salvo com sucesso!", false);
			LoggerUtil.logInfo(Acao.SALVAR, this.usuario, log);
		} catch (ValidacaoException e){
			this.usuario.setResetSenha(resetSenha);
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Erro ao salvar usuário (" + this.usuario.getLogin() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.usuario, e, log);
		}
	}

	public void excluir(UsuarioDTO usuario) {
		try {
			usuarioService.excluir(usuario.getId());
			facesUtil.exibirMensagemInfo("Usuário " + usuario.getLogin() + " excluído com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, usuario, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Erro ao excluir usuário (" + usuario.getLogin() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, usuario, e, log);
		}
	}

	
	/*
	 * Servidor
	 */	
	
	public void abrirDialogServidor(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarServidor");
	}
	
	public void associarServidor(SelectEvent event) {
		ServidorDTO s = (ServidorDTO) event.getObject();
		
		try {
			if (s == null) {
				return;
			}
			
			Servidor servidor = servidorService.buscarServidorSimplesPorId(s.getId());
			
			if (servidor == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Servidor não encontrado.");
				return;
			}
				
			if (this.usuarioService.isServidorJaAssociadoAUmUsuario(s.getId())) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "O Servidor " + s.getNome() + " já está associado a um usuário.");
				return;
			}
			
			if(servidor.getEmail() != null){
				this.usuario.setEmail(servidor.getEmail());
			}
			
			this.usuario.setServidor(servidor);	
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao associar servidor: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, s, e, log);
		}
	}
	
	
	
	/*
	 * Aba Acesso -> Unidade
	 */	
	
	public void abrirDialogUnidade(ActionEvent event) {
		this.acesso = new Acesso(this.usuario);
		facesUtil.exibirPaginaDialog("pretty:AssociarUnidade");
	}
	
	public void associarUnidade(SelectEvent event) {
		UnidadePrisionalDTO u = (UnidadePrisionalDTO) event.getObject();
		
		try {
			if (u == null) {
				return;
			}
			
			UnidadePrisional unidade = unidadeService.buscaSimplesPorId(u.getId());
			
			if (unidade == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Unidade não encontrada.");
				return;
			}
			
			for (Acesso acesso : this.usuario.getAcessos()) {
				if (acesso.getUnidade().equals(unidade)) {
					this.acesso = null;
					return;
				}
			}
			
			this.acesso.setUnidade(unidade);
			
			if(!this.usuario.getAcessos().contains(this.acesso)) {
				this.usuario.getAcessos().add(this.acesso);
			}
			
			this.acesso = null;
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao associar unidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, u, e, log);
		}
	}
	
	public void removerUnidadeDoUsuario(ActionEvent event) {
		Acesso acesso = (Acesso) event.getComponent().getAttributes().get("acesso");
		
		if (acesso != null) {
			this.usuario.getAcessos().remove(acesso);
		}
	}

	
	
	/*
	 * Aba Acesso -> Perfil
	 */
		
	public void configurarAcesso(ActionEvent event) {
		try {
			this.acesso = (Acesso) event.getComponent().getAttributes().get("acesso");
			configurarPerfis();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao configurar perfis: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	private void configurarPerfis() {
		perfisModel = perfilService.listarPerfisModel(this.acesso.getPerfis());
	}
	
	public void associarPerfis(ActionEvent event) {
		List<Perfil> perfis = this.perfisModel.getTarget();
		
		if (perfis != null) {
			this.usuario.getAcessos().get(this.usuario.getAcessos().indexOf(this.acesso)).setPerfis(perfis);
		}
	}
	
	
	
	/*
	 * Validações de Pagina
	 */
	
	public void dataExpiracaoListener(SelectEvent event) {
		Date date = (Date) event.getObject();
		
		if (date != null) {
			this.usuario.setDataExpiracao(date);
		}
	}
	
	public Boolean possuiServidor() {
		return (this.usuario.getServidor() != null);
	}
	
	public Boolean possuiUsuario() {
		return this.usuario.getId() > 0;
	}
	
	public Date getDataMinima() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}
	
	public void fecharDialog() {
		facesUtil.fecharPaginaDialog(null);
	}

	
	
	/*
	 * Getters e Setters
	 */	
	
	public LazyDataModel<UsuarioDTO> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(LazyDataModel<UsuarioDTO> usuarios) {
		this.usuarios = usuarios;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public FiltroUsuario getFiltro() {
		return filtroUsuario;
	}

	public void setFiltro(FiltroUsuario filtro) {
		this.filtroUsuario = filtro;
	}

	public UnidadePrisional getUnidadeDoUsuario() {
		return unidadeDoUsuario;
	}

	public void setUnidadeDoUsuario(UnidadePrisional unidadeDoUsuario) {
		this.unidadeDoUsuario = unidadeDoUsuario;
	}

	public Acesso getAcesso() {
		return acesso;
	}

	public void setAcesso(Acesso acesso) {
		this.acesso = acesso;
	}

	public DualListModel<Perfil> getPerfisModel() {
		return perfisModel;
	}

	public void setPerfisModel(DualListModel<Perfil> perfisModel) {
		this.perfisModel = perfisModel;
	}

    public List<UnidadePrisional> getUnidadesPrisionais() {
        return unidadesPrisionais;
    }

    public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
        this.unidadesPrisionais = unidadesPrisionais;
    }
}
