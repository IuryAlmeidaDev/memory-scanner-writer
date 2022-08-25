package br.gov.mt.sejudh.sigepen.bean;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoAutorizacaoValidacaoSemBio;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.model.AutorizacaoValidacaoSemBiometria;
import br.gov.mt.sejudh.sigepen.model.EntradaSaida;
import br.gov.mt.sejudh.sigepen.model.EntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.EntradaSaidaRecuperandoService;
import br.gov.mt.sejudh.sigepen.service.EntradaSaidaService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.service.UsuarioService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;


@ViewScoped
@ManagedBean
@URLMappings(mappings = {
        @URLMapping(id = "ConsultarListaVisitas", pattern = "/consultarlistavisitas", viewId = "/WEB-INF/pages/entradasaida/ConsultarVisita.xhtml"),
})
public class ConsultarVisitaBean extends MetodosDePaginaBean {
	
	@Inject
	private FacesUtil facesUtil;
	
    private List<TipoVisita> tipoVisitas;

	@Inject
	private Logger log;
	
	private FiltroEntradaSaidaRecuperando filtro;
	
	private LazyDataModel<EntradaSaidaRecuperando> entradas;
	
	@Inject
	private EntradaSaidaRecuperandoService service;
	
	@Inject
    private UsuarioService usuarioService;

	@Inject
    private SecurityUtil securityUtil;
    
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@Inject
	private EntradaSaidaService esService;
	
	private EntradaSaida entrada;
	
	private boolean mostrarPanelVisitante = false;
	
	private boolean mostrarPanelResponsavel = false;
	
	private String loginUsuarioAutorizador;
	private String senhaUsuarioAutorizador;
	private String observacaoUsuarioAutorizador;
	
	private String acao;
	
	@Inject
	private UnidadePrisionalService unidadeService;
	
	private List<UnidadePrisional> unidadesPrisionais;

	@URLAction(onPostback = false)
	public String init() throws Exception {
		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			FiltroEntradaSaidaRecuperando filtroDash = (FiltroEntradaSaidaRecuperando)facesUtil.getFlashParameter("filtroEntradaSaidaRecuperandoDash");
			
			if (filtroDash != null) {
				setFiltro(filtroDash);
			} else {
				super.carregarFiltroPaginacao(FiltroEntradaSaidaRecuperando.class, this);
			}
		}
		try {
			switch(this.getAcaoDeTela()) {
				case LISTAR:
					this.listarTipoVisitas(null);
					this.unidadesPrisionais = unidadeService.listarUnidadesNaoInativas();
					this.filtro.setUnidade(SecurityUtil.getUnidadeSelecionada());
					this.filtro.setExibeUnidadeNoFiltro(securityUtil.possuiPermissao("ConsultarVisitaPorUnidade"));
					this.pesquisar();
					logDeAcesso(log, "Entrada/Saida");
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
	
	public void pesquisar() {
		this.entradas = new LazyDataModel<EntradaSaidaRecuperando>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Object getRowKey(EntradaSaidaRecuperando object) {
				if(object != null && object.getId() != null) {
					return object.getId();
				}
				return null;
			}

			@Override
			public EntradaSaidaRecuperando getRowData(String rowKey) {
				if (rowKey == null || rowKey.isEmpty()) {
					return null;
				}

				for (EntradaSaidaRecuperando rec : entradas) {
					if (rec.getId().equals(Long.valueOf(rowKey))) {
						return rec;
					}
				}

                return null;
			}
			
			@Override
			public List<EntradaSaidaRecuperando> load(int first, int pageSize,
					String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {

				setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				List<EntradaSaidaRecuperando> lista = service.buscarEntradasPorFiltro(getFiltro(), false);		
				return lista;
			}
		};
	}

	public FiltroEntradaSaidaRecuperando getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroEntradaSaidaRecuperando filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<EntradaSaidaRecuperando> getEntradas() {
		return entradas;
	}

	public void setEntradas(LazyDataModel<EntradaSaidaRecuperando> entradas) {
		this.entradas = entradas;
	}
	
	public List<TipoVisita> getTipoVisitas() {
		if (this.tipoVisitas == null) {
            this.listarTipoVisitas(null);
        }
        return tipoVisitas;
	}

	public void setTipoVisitas(List<TipoVisita> tipoVisitas) {
		this.tipoVisitas = tipoVisitas;
	}
	
	public void listarTipoVisitas(TipoEntrada te) {
        try {
        	this.tipoVisitas = this.service.getTipoVisitas(te);	
        } catch (Exception e) {
            facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar visitas: " + e.getMessage());
            LoggerUtil.logErro(e, log);
        }
    }

	public void imprimir() {
		if(!securityUtil.possuiPermissao("ImprimirListaVisitas")) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Para realizar a impressão é necessária a permissão!");
			return;
		}
		try {
			List<EntradaSaidaRecuperando> lista = service.buscarEntradasPorFiltro(getFiltro(), true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FILTRO", "Campos Utilizados: " + this.filtro.getFiltro());

			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String cabecalhoFiltro = ctx.getRealPath("WEB-INF" + File.separator + "relatorios" + File.separator + "cabecalhos" + File.separator + "Filtro.jasper");
			parameters.put("CABECALHO_FILTRO", cabecalhoFiltro);
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarEntradaSaida, parameters));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Consultar visitas", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de visitas: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Consultar visitas", e, log);
		}
	}

	public String buscarSituacao(EntradaSaidaRecuperando e) {
		Date di = Sigepen.getDataConsultaMaiorIgual(new Date());
		return e.getEntrada().getDataSaida() != null || e.getEntrada().getDataEntrada().before(di) ? "Finalizada" : "Em Andamento";
	}

	public void abrirFinalizarVisita(EntradaSaidaRecuperando e) {
		this.entrada = e.getEntrada();
	}
	
	public void abrirVisualizarVisita(EntradaSaidaRecuperando e) {
		
		 this.entrada = e.getEntrada();
		
		if(this.entrada.getVisitante().getResponsavel() != null) {
			this.mostrarPanelVisitante = false;
			this.mostrarPanelResponsavel = true;
			return;
		}
	   
	    this.mostrarPanelVisitante = true;
		
	}
	
		
	public void finalizarVisita() {
		if (this.entrada.getMotivoFinalizacao().isEmpty()) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Preenchimento do motivo é obrigatório");
			return;
		}		
		LoggerUtil.logInfo(Acao.FINALIZAR, this.entrada, log);
		this.entrada.setDataSaida(new Date());
		this.entrada.setSituacao(SituacaoVisita.FINALIZADA);
		esService.salvar(this.entrada);
		RequestContext.getCurrentInstance().execute("PF('dialogFinalizarVisita').hide()");
	}
	
	public Boolean isMenor(Date nasc){
		if(nasc == null)
			return false;
		Calendar past = Calendar.getInstance();
        past.add(Calendar.YEAR, -18);
        return nasc.after(past.getTime());
	}
	
	
	public EntradaSaida getEntrada() {
		return entrada;
	}

	public void setEntrada(EntradaSaida entrada) {
		this.entrada = entrada;
	}
	
	public boolean isMostrarPanelVisitante() {
		return mostrarPanelVisitante;
	}

	public void setMostrarPanelVisitante(boolean mostrarPanelVisitante) {
		this.mostrarPanelVisitante = mostrarPanelVisitante;
	}

	public boolean isMostrarPanelResponsavel() {
		return mostrarPanelResponsavel;
	}

	public void setMostrarPanelResponsavel(boolean mostrarPanelResponsavel) {
		this.mostrarPanelResponsavel = mostrarPanelResponsavel;
	}
	
	public String getLoginUsuarioAutorizador() {
		return loginUsuarioAutorizador;
	}

	public void setLoginUsuarioAutorizador(String loginUsuarioAutorizador) {
		this.loginUsuarioAutorizador = loginUsuarioAutorizador;
	}

	public String getSenhaUsuarioAutorizador() {
		return senhaUsuarioAutorizador;
	}

	public void setSenhaUsuarioAutorizador(String senhaUsuarioAutorizador) {
		this.senhaUsuarioAutorizador = senhaUsuarioAutorizador;
	}

	public String getObservacaoUsuarioAutorizador() {
		return observacaoUsuarioAutorizador;
	}

	public void setObservacaoUsuarioAutorizador(String observacaoUsuarioAutorizador) {
		this.observacaoUsuarioAutorizador = observacaoUsuarioAutorizador;
	}

	public String autorizarCadastroSemBiometria() {
		try {
			AuthenticationInfo authenticationInfo = SecurityUtils.getSecurityManager().authenticate(
					new UsernamePasswordToken(this.loginUsuarioAutorizador, this.senhaUsuarioAutorizador));
			SecurityUtils.getSecurityManager().checkPermission(authenticationInfo.getPrincipals(),
					"AutorizarCadastroSemBiometria");
			Usuario usuario = usuarioService.buscarUsuarioPorLogin(this.loginUsuarioAutorizador);

			AutorizacaoValidacaoSemBiometria autValid = new AutorizacaoValidacaoSemBiometria();
			
			autValid.setObservacao(this.observacaoUsuarioAutorizador);
			autValid.setUsuario(usuario);
			autValid.setDataAutorizacao(Sigepen.getDataAtual());
			autValid.setTipoAutorizacaoValidacaoSemBio(TipoAutorizacaoValidacaoSemBio.SAIDA_VISITANTE);
			autValid.setEntradaSaida(this.entrada);

			this.loginUsuarioAutorizador = "";
			this.senhaUsuarioAutorizador = "";
			this.observacaoUsuarioAutorizador = "";

			RequestContext.getCurrentInstance().execute("PF('dlgAutBio').hide()");
			LoggerUtil.logInfo(Acao.AUTORIZAR, this.entrada, log);
			
			this.entrada.getAutorizacaoValidacaoSemBiometria().add(autValid);
			
			if("FINALIZAR".equals(this.acao)) {
				RequestContext.getCurrentInstance().execute("PF('dialogVisualizarEntradaSaida').hide()");
				this.entrada.getAutorizacaoValidacaoSemBiometria().add(autValid);
				finalizarVisitaManual();
			}else {
				if(this.entrada.getId() != null && this.entrada.getSituacao().equals(SituacaoVisita.EM_ANDAMENTO)){
					this.entrada.getAutorizacaoValidacaoSemBiometria().add(autValid);
					finalizarVisitaManual();
				}
						
			}
			RequestContext.getCurrentInstance().update("dataTable");

			RequestContext.getCurrentInstance().update("formDialogVisualizarEntradaSaida");
			
		} catch (UnauthorizedException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Usuário informado não possui permissão para autorizar cadastro.");
		} catch (UnknownAccountException | IncorrectCredentialsException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Usuário ou senha inválidos.");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao autorizar cadastro sem biometria.");
			LoggerUtil.logErro(e, log);
		}

		return null;
	}
	
	
	public void abrirDlgAutorizar() {
		if (!securityUtil.possuiPermissao("AutorizarEntradaVisitanteSemBiometria")) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Entrada não permitida! Usuário não autorizado para efetuar entrada!");
			return;
		}
		
		if (!securityUtil.possuiPermissao("AutorizarSaidaVisitanteSemBiomatria")) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Saída não permitida! Usuário não autorizado para efetuar saída!");
			return;
		}
		
		RequestContext.getCurrentInstance().execute("PF('dlgAutBio').show()");
	}
	
	public void finalizarVisitaManual() {
		LoggerUtil.logInfo(Acao.FINALIZAR, this.entrada, log);
		this.entrada.setDataSaida(new Date());
		this.entrada.setSituacao(SituacaoVisita.FINALIZADA);
		service.salvar(this.entrada);
		facesUtil.exibirMensagemInfo("Saída do visitante " + this.entrada.getVisitante().getPessoa().getNome() +  " realizada com sucesso!", true);
		String url = Sigepen.getApplicationName() + "/consultarlistavisitas";
        try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void selecionarTipoEntrada() {
		this.filtro.setTipoVisita(null);
		this.listarTipoVisitas(this.filtro.getTipoEntrada());
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}
}

