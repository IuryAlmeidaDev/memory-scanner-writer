package br.gov.mt.sejudh.sigepen.bean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.OrientacaoParticularidade;
import br.gov.mt.sejudh.sigepen.enumerator.RegiaoParticularidade;
import br.gov.mt.sejudh.sigepen.enumerator.Sexo;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.StatusProcesso;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Alcunha;
import br.gov.mt.sejudh.sigepen.model.AutorizacaoCadastroSemBiometria;
import br.gov.mt.sejudh.sigepen.model.Biometria;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Particularidade;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.ProcessoRegimeCondicao;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.TipoDocumento;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.model.dto.ExtratoRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.BiometriaService;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.service.ContatoService;
import br.gov.mt.sejudh.sigepen.service.DocumentoService;
import br.gov.mt.sejudh.sigepen.service.EnderecoService;
import br.gov.mt.sejudh.sigepen.service.PassagemService;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.service.PessoaService;
import br.gov.mt.sejudh.sigepen.service.ProcessoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.service.UsuarioService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarRecuperando", pattern = "/recuperando", viewId = "/WEB-INF/pages/recuperando/ListarRecuperando.xhtml"),
		@URLMapping(id = "CadastrarRecuperando", pattern = "/recuperando/cadastrar", viewId = "/WEB-INF/pages/recuperando/CadastrarRecuperando.xhtml"),
		@URLMapping(id = "AlterarRecuperando", pattern = "/recuperando/alterar/#{id}", viewId = "/WEB-INF/pages/recuperando/CadastrarRecuperando.xhtml"),
		@URLMapping(id = "VisualizarRecuperando", pattern = "/recuperando/visualizar/#{id}", viewId = "/WEB-INF/pages/recuperando/CadastrarRecuperando.xhtml"),
		@URLMapping(id = "RelatorioInfopen", pattern = "/recuperando/infopen", viewId = "/WEB-INF/pages/recuperando/RelatorioInfopen.xhtml")
})
public class RecuperandoBean extends MetodosDePaginaBean {

	private String biometria;
	
	private Boolean isCadastro = false;
	
	private FiltroRecuperando filtro;
	
	private LazyDataModel<RecuperandoDTO> recuperandos;
	
	private List<RecuperandoDTO> recuperandoSimilares;
	
	private Recuperando recuperando;
	
	private Foto fotoDoFormulario;

	private Contato contato;

	private List<Documento> documentos;

	private Documento doc;

	private Endereco endereco;

	private Alcunha alcunha;

	private Alcunha alcunhaAux;

	private OutroNome outroNome;

	private OutroNome nomeAux;

	private Parente parente;

	private Parente parenteAux;

	private Passagem passagemSelecionada;
	
	private String loginUsuarioAutorizador;
	
	private String senhaUsuarioAutorizador;
	
	private String observacaoUsuarioAutorizador;

	@SuppressWarnings("unused")
	private String corPericulosidade;

	private Processo processo;

	private Particularidade particularidade;
	
	private Boolean temBiometria = true;

	@Inject
	private SecurityUtil securityUtil;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@Inject
	private RecuperandoService service;

	@Inject
	private PessoaService pessoaService;

	@Inject
	private ContatoService contatoService;

	@Inject
	private DocumentoService documentoService;

	@Inject
	private EnderecoService enderecoService;

	@Inject
	private ProcessoService processoService;
	
	@Inject
	private BiometriaService bioService;

	@Inject
	private PedidoService pedido;
	
	@Inject
	private UsuarioService usuarioService;
	
	@Inject
	private UnidadePrisionalService unidadeService;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;

	private String movimentacaoInterna;

	private String movimentacaoExterna;

	private String emTransferencia;
	
	private RecuperandoDTO recuperandoSelecionado;
	
	@Inject
	private PassagemService passagemService;
	
	private List<UnidadePrisional> unidadesPrisionais;
	
	@Inject
	private ComboService comboService;
	
	private UnidadePrisional unidadeInfopenSelecionada;
	
	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			FiltroRecuperando filtroDash = (FiltroRecuperando)facesUtil.getFlashParameter("filtroRecuperandoDash");
			
			if (filtroDash != null) {
				setFiltro(filtroDash);
			} else {
				super.carregarFiltroPaginacao(FiltroRecuperando.class, this);
			}
		}
	}

	@URLAction(onPostback=false)
	public String init() {
		try {	
			switch(this.getAcaoDeTela()) {
				case CADASTRAR:
					long pessoaId = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					Pessoa p = null;
					
					//A pessoa pode já existir no sistema como visitante ou outros...
					if (pessoaId > 0) {
						p = pessoaService.buscarPessoaPorId(pessoaId);
						
						if (p == null) {
							facesUtil.exibirMensagemErro("Pessoa não encontrada.", true);
							return "pretty:ListarRecuperando";
						}
					} else {
						p = new Pessoa();
						p.setNaturalidade(new Cidade());
					}
					
					this.recuperando = new Recuperando(StatusRecuperando.INATIVO_PARCIAL);
					this.recuperando.setPessoa(p);
					inicializarVariaveis();
					break;
					
				case ALTERAR:
				case VISUALIZAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarRecuperando(id)) {
						facesUtil.exibirMensagemErro("Recuperando não encontrado.", true);
						return "pretty:ListarRecuperando";
					}
					
					if (!validarRecuperando()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarRecuperando";
					}

					inicializarVariaveis();
					carregarStatusMovimentacao();
					logDeAcesso(log, this.recuperando);
					
					if(verificarSeBiometriaEhObrigatoria()) {
						validarBiometriaAtualizada();
					}
					break;
					
				case LISTAR:
					unidadesPrisionais   = comboService.getUnidadesPrisionais();
					pesquisar();
					logDeAcesso(log, "Recuperandos");
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
	
	public Boolean verificarSeBiometriaEhObrigatoria() {
		return unidadeService.verificarSeBiometriaEhObrigatoria(SecurityUtil.getUnidadeSelecionada().getId());
	}
	
	public String executarCadastro() {
		return "pretty:CadastrarRecuperando";
	}
	
	private void validarBiometriaAtualizada() {
		if (this.recuperando.getPessoa().getBiometria() != null && recuperando.getPessoa().getBiometria().getTemplate() == null
				&& this.getAcaoDeTela() == AcaoDeTela.ALTERAR) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATUALIZAÇÃO CADASTRAL NECESSÁRIA", "A biometria deste recuperando está DESATUALIZADA.");
		}
	}

	private boolean buscarRecuperando(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.recuperando = service.buscarRecuperandoPorId(id, true);
		
		if (this.recuperando == null) {
			return false;
		}
		
		return true;
	}
	
	private boolean validarRecuperando() {
		return true;
	}
	
	private void inicializarVariaveis() {
		this.fotoDoFormulario = new Foto();
		this.particularidade = new Particularidade();
		
		this.parente = new Parente();
		this.documentos = new ArrayList<Documento>();
		this.alcunha = new Alcunha();
		
		this.passagemSelecionada = new Passagem();
	}
	
	public void carregarStatusMovimentacao() {
		this.movimentacaoInterna = (pedido.recuperandoEmMovimentacaoInterna(recuperando)) ? "SIM" : "NÃO";
		this.movimentacaoExterna = (pedido.recuperandoEmMovimentacaoExterna(recuperando)) ? "SIM" : "NÃO";
		this.emTransferencia = (pedido.recuperandoEmTransferencia(recuperando)) ? "SIM" : "NÃO";
	}

	public String recuperandoAuditoriaRodape() {
		if (this.recuperando.getAutorRegistro() == null) {
			return "";
		}
		String rodape = "Cadastrado por: " + this.recuperando.getAutorRegistro().getUsuario() + " em " + this.recuperando.getAutorRegistro().getData();
		if (!this.recuperando.getAutorUltimaAlteracao().getData() .equals(this.recuperando.getAutorRegistro().getData())) {
			return rodape + "| Alterado por: " + this.recuperando.getAutorUltimaAlteracao().getUsuario() + " em " + this.recuperando.getAutorUltimaAlteracao().getData();
		} else {
			return rodape;
		}
	}

	/*
	 * Métodos da Listagem
	 */
	private void pesquisar() {
		this.recuperandos = new LazyDataModel<RecuperandoDTO>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Object getRowKey(RecuperandoDTO object) {
				return object != null ? object.getId() : null;
			}

			@Override
			public RecuperandoDTO getRowData(String rowKey) {
				if (rowKey == null || rowKey.isEmpty()) {
					return null;
				}

				for (RecuperandoDTO rec : recuperandos) {
					if (rec.getId().equals(Long.valueOf(rowKey))) {
						service.buscarAlcunhasEOutrosNomes(rec);
						return rec;
					}
				}
				
				return null;
			}
			
			@Override
			public List<RecuperandoDTO> load(int first, int pageSize,
					String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				List<RecuperandoDTO> recuperandos = service.buscarRecuperandosPorFiltro(getFiltro(), false);

				if (isCadastro) {
					if (recuperandos.size() > 0) {
						recuperandoSimilares = service.buscarRecuperandosPorFiltro(getFiltro(), true);
						
						RequestContext.getCurrentInstance().execute("PF('dlgRecupFotos').show()");
					} else {
						RequestContext.getCurrentInstance().execute("PF('confirmaCadastro').show()");
					}
				}

				isCadastro = false;
				
				return recuperandos;
			}
		};
	}
	
	public void imprimir() {
		try {
			List<RecuperandoDTO> lista = service.buscarRecuperandosPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarRecuperando));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Recuperandos", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de recuperandos: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Recuperandos", e, log);
		}
	}
	
	public void imprimirExtratoRecuperandoPorPassagem(long passagemId) {
		gerarExtratoRecuperando(service.gerarEntidadeExtratoRecuperando(this.recuperando, passagemId));
	}
	
	public void imprimirExtratoRecuperando() {
		gerarExtratoRecuperando(service.gerarEntidadeExtratoRecuperando(this.recuperando, null));
	}
	
	private void gerarExtratoRecuperando(List<ExtratoRecuperandoDTO> lista) {
		try {
			List<ExtratoRecuperandoDTO> listaRec = lista;
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(listaRec, Relatorio.ExtratoRecuperando));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Recuperandos", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir extrato do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Extrato do Recuperando", e, log);
		}
	}

	/*
	 * Métodos de Bean
	 */

	public String salvar() {
		try {
			if (!this.recuperando.possuiFotoFrenteAtual()) {
				throw new ValidacaoException("É necessário associar uma foto de frente atual do recuperando.");
			}
			
			if (verificarSeBiometriaEhObrigatoria()) {
				if (this.temBiometria && this.recuperando.getPessoa().getBiometria() == null) {
					RequestContext.getCurrentInstance().execute("enviaComandoWs('registrar')");
					return null;
				} 
				else if (!this.temBiometria && this.recuperando.getPessoa().getAutorizacaoCadastro() == null && this.recuperando.getPessoa().getBiometria() == null) {
					RequestContext.getCurrentInstance().execute("PF('confirmaCadBio').show()");
					return null;
				}
			}

			boolean isNovoRecuperando = this.recuperando.getId() <= 0;
			this.recuperando = service.salvar(this.recuperando);
			LoggerUtil.logInfo(Acao.SALVAR, this.recuperando, log);
			String mensagemSucesso = "Recuperando(a) " + this.recuperando.getPessoa().getNome() + " salvo com sucesso!";

			if (isNovoRecuperando) {
				facesUtil.exibirMensagemInfo(mensagemSucesso, true);
				
				if (!securityUtil.possuiPermissao("CadastrarNovaPassagem")) {
					return "pretty:ListarRecuperando";
				}
				
				facesUtil.setQueryParameter("id", String.valueOf(this.recuperando.getId()));				
				return "pretty:CadastrarNovaPassagem";
			} else {
				facesUtil.exibirMensagemInfo(mensagemSucesso, false);
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Erro ao dar entrada em recuperando (" + this.recuperando.getPessoa().getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.recuperando, e, log);
		}

		return null;
	}
	
	public String autorizarCadastroSemBiometria() {
		try {
			AuthenticationInfo ai = SecurityUtils.getSecurityManager().authenticate(new UsernamePasswordToken(loginUsuarioAutorizador, senhaUsuarioAutorizador));
			SecurityUtils.getSecurityManager().checkPermission(ai.getPrincipals(), "AutorizarCadastroSemBiometria");
			Usuario u = usuarioService.buscarUsuarioPorLogin(loginUsuarioAutorizador);
		
			AutorizacaoCadastroSemBiometria autCad = new AutorizacaoCadastroSemBiometria();
			autCad.setObservacao(this.observacaoUsuarioAutorizador);
			autCad.setUsuario(u);
			autCad.setDataAutorizacao(Sigepen.getDataAtual());
		
			this.recuperando.getPessoa().setAutorizacaoCadastro(autCad);
		
			RequestContext.getCurrentInstance().execute("PF('dlgAutBio').hide()");
			
			return salvar();

		} catch (UnauthorizedException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Usuário informado não possui permissão para autorizar cadastro.");
		} catch (UnknownAccountException | IncorrectCredentialsException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Usuário ou senha inválidos.");
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao autorizar cadastro sem biometria.");
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}

	public void excluir(RecuperandoDTO recuperando) {
		try {
			service.excluir(recuperando);
			facesUtil.exibirMensagemInfo("Recuperando(a) " + recuperando.getNome() + " excluído(a) com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, recuperando, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir recuperando (" + recuperando.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, recuperando, e, log);
		}
	}

	public void configurarNomeSocial() {
		String nomeSocial = 
				(this.recuperando.getIdentidadeDeGenero() != null &&
					this.recuperando.getPessoa().getNomeSocial() != null) ? this.recuperando.getPessoa().getNomeSocial() : null;
		this.recuperando.getPessoa().setNomeSocial(nomeSocial);
	}

	public void limpaBiometria() {
		if (this.filtro != null && this.filtro.getPessoasIds() != null && this.filtro.getPessoasIds().size() > 0) {
			this.filtro.setPessoasIds(new ArrayList<Long>());
		}
	}

	public void pesquisarBiometria() {
		try {
			this.setFiltro(new FiltroRecuperando());
			this.filtro.setPessoasIds(new Gson().fromJson(this.biometria, new TypeToken<ArrayList<Long>>(){}.getType()));
			this.biometria = null;
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao pesquisar por biometria: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public void registrarBiometria() {
		try {
			if (this.getIsVisualizacao()) {
				return;
			}

			if (this.biometria == null) {
				throw new Exception("Biometria inválida.");
			}
			
			Biometria bio = bioService.buscarBiometriaConvertida(Long.parseLong(this.biometria));
			
			/*if (bio != null) {
				List<Long> pessoas = bioService.pesquisarBiometriaPorTemplateByte(bio.getTemplate());

				if (pessoas.size() > 0 && pessoas.get(0).longValue() != recuperando.getPessoa().getId() && pessoas.get(0).longValue() > 0) {
					Recuperando rec = service.buscarRecuperandoSimplesPorIdPessoa(pessoas.get(0).longValue());
					throw new ValidacaoException("A biometria informada já existe na matrícula " + rec.getId() + " com o nome de " + rec.getPessoa().getNome());
				}
			}*/
			
			bioService.vincularBiometriaAPessoa(bio, this.recuperando.getPessoa());

			facesUtil.exibirMensagemInfo("Biometria capturada!", false);
			RequestContext.getCurrentInstance().update("mensagens");
			RequestContext.getCurrentInstance().update("tabView:biometriaCapturada");
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATENÇÃO", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao capturar biometria: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public void visualizarLotacao(Passagem passagem) {
		LoggerUtil.logInfo(Acao.VISUALIZAR, passagem, log);
		this.passagemSelecionada = passagemService.buscaPassagemPorId(passagem);
	}

	/*
	 * Associar Foto
	 */

	public void abrirDialogFoto(ActionEvent event) {
		facesUtil.exibirPaginaDialog("AssociarFoto", 768, 1024);
	}

	public void associarFoto(SelectEvent event) {
		Object retorno = event.getObject();

		if (retorno == null) {
			return;
		}

		Foto foto = new Foto();
		foto.setRecuperando(this.recuperando);
		foto.setTipo(TipoFoto.FRENTE);
		foto.setIsAtual(true);
		foto.setDataInclusao(Sigepen.getDataAtual());
		foto.setImagem((Imagem) retorno);

		this.recuperando.adicionarFotoFrente(foto);
		LoggerUtil.logInfo(Acao.ADICIONAR, foto + " " + this.recuperando, log);
	}


	/*
	 * Associar Processo
	 */
	
	private String acaoDialogProcesso(Processo proc, boolean isVisualizar) {
		if (proc == null) {
			return "pretty:CadastrarProcesso";
		}
		
		if (isVisualizar) {
			return "pretty:VisualizarProcesso";
		} 
		
		return "pretty:AlterarProcesso";
	}

	public void abrirDialogProcesso(Processo processo, boolean isVisualizar) {
		facesUtil.setObjetoSessao("recuperandoProcesso", this.recuperando);
		
		if (processo != null) {
			facesUtil.setObjetoSessao("processo", processo);
		}
		
		facesUtil.exibirPaginaDialog(acaoDialogProcesso(processo, isVisualizar), null, 1300);
	}

	public void atualizarProcessos(SelectEvent event) {
		String numProc = (String) event.getObject();
		
		try {
			if (numProc != null && numProc != "") {
				this.recuperando = this.service.atualizarRegimeCondicaoAtualRecuperando(recuperando);
				facesUtil.exibirMensagemInfo("Processo " + numProc + " salvo com sucesso!", false);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro atualizar processos do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, numProc, e, log);
		}
	}

	public void processoSelecionado(Processo processo) {
		if (processo != null) {
			this.processo = processo;
		}
	}

	public void salvarProcesso() {
		try {
			if (processo.getDataFinalizado() != null) {
				this.processo.setStatusProcesso(StatusProcesso.FINALIZADO);
			}
			
			this.recuperando.getProcessos().remove(processo);
			this.processo = this.processoService.salvar(processo);

			LoggerUtil.logInfo(Acao.FINALIZAR, processo, log);

			this.recuperando.getProcessos().add(processo);
			RequestContext.getCurrentInstance().update("tabView:formPassagem");
			RequestContext.getCurrentInstance().update("mensagens");
			RequestContext.getCurrentInstance().execute("PF('dialogFinalizarProcesso').hide()");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar processo." + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, processo, e, log);
		}
	}

	/*
	 * Aba Fotos
	 */

	public void associarNovaFoto(SelectEvent event) {
		Object retorno = event.getObject();

		if (retorno == null) {
			return;
		}

		this.fotoDoFormulario = new Foto();
		this.fotoDoFormulario.setRecuperando(this.recuperando);
		this.fotoDoFormulario.setImagem((Imagem) retorno);
	}

	public void adicionarNovaFoto() {
		if (this.fotoDoFormulario == null || this.fotoDoFormulario.getImagem() == null) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Adicionar foto", "Preencha corretamente os campos e associe uma foto.");
			return;
		}

		this.fotoDoFormulario.getImagem().setDataInclusao(Sigepen.getDataAtual());
		this.fotoDoFormulario.setDataInclusao(Sigepen.getDataAtual());

		try {
			this.recuperando.setFotos(service.salvarFoto(this.fotoDoFormulario));
			LoggerUtil.logInfo(Acao.SALVAR, this.fotoDoFormulario, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(null, this.recuperando, e, log);
		}

		this.fotoDoFormulario = new Foto();
	}

	public void configurarFotoAtual(Foto foto) {
		try {
			if (!foto.getIsAtual()) {
				for (Foto f : this.recuperando.getFotos()) {
					if (f.getTipo().equals(TipoFoto.FRENTE) && f.getId() == foto.getId()) {
						foto.setIsAtual(!foto.getIsAtual());
						
						throw new ValidacaoException("É necessário ter pelo menos uma foto de frente atual.");
					}
				}
			}

			this.recuperando.setFotos(service.salvarFoto(foto));
			LoggerUtil.logInfo(Acao.SALVAR, foto, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(null, this.recuperando, e, log);
		}
	}

	public void excluirFoto(Foto f) {
		try {
			if (f.getIsAtual() && f.getTipo() == TipoFoto.FRENTE) {
				throw new ValidacaoException("Não é permitido excluir foto de frente atual.");
			}

			this.recuperando.getFotos().remove(f);
			service.excluirFoto(f);
			LoggerUtil.logInfo(Acao.EXCLUIR, f, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir foto: " + e.getMessage());
			LoggerUtil.logErro(null, this.recuperando, e, log);
		}
	}

	/*
	 * Aba Particularidades
	 */

	public void abrirDialogParticularidade(ActionEvent event) {
		Object regiao = event.getComponent().getAttributes().get("regiao");
		Object orientacao = event.getComponent().getAttributes().get("orientacao");
		facesUtil.getObjetoSessao("regiao");
		facesUtil.getObjetoSessao("orientacao");
		facesUtil.setObjetoSessao("regiao", regiao);
		facesUtil.setObjetoSessao("orientacao", orientacao);
		abrirDialogFoto(event);
	}

	public void associarParticularidade(SelectEvent event) {
		Object retorno = event.getObject();

		if (retorno == null) {
			return;
		}

		this.particularidade.setRecuperando(this.recuperando);
		this.particularidade.setImagem((Imagem) retorno);
		this.particularidade.setRegiao((RegiaoParticularidade) this.facesUtil.getObjetoSessao("regiao"));
		this.particularidade.setOrientacao((OrientacaoParticularidade) this.facesUtil.getObjetoSessao("orientacao"));
	}

	public void adicionarParticularidade() {
		if (this.particularidade == null || this.particularidade.getImagem() == null) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Adicionar Particularidade", "Preencha corretamente os campos e associe uma foto a uma região do corpo humano.");
			return;
		}

		this.particularidade.getImagem().setDataInclusao(Sigepen.getDataAtual());
		this.particularidade.setDataInclusao(Sigepen.getDataAtual());

		try {
			this.particularidade = service.salvarParticularidade(this.particularidade);
			LoggerUtil.logInfo(Acao.SALVAR, this.particularidade, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar particularidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.recuperando, e, log);
		}

		this.recuperando.getParticularidades().add(this.particularidade);
		this.particularidade = new Particularidade();
	}

	public void excluirParticularidade(Particularidade p) {
		this.recuperando.getParticularidades().remove(p);

		try {
			service.excluirParticularidade(p);
			LoggerUtil.logInfo(Acao.EXCLUIR, p, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir particularidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, this.recuperando, e, log);
		}
	}

	/*
	 * PreCadastroPessoa
	 */

	public String executarPreCadastroPessoaRemoteCommand() {
		if (this.getAcaoDeTela() == AcaoDeTela.CADASTRAR) {
			return "$('#cb').click();";
		} else {
			return "return false;";
		}
	}

	public void abrirDialogPreCadastroPessoa(ActionEvent event) {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", true);
		options.put("draggable", false);
		options.put("resizable", false);
		options.put("closable", false);

		facesUtil.setObjetoSessao("pessoa", this.recuperando.getPessoa());
		facesUtil.exibirPaginaDialog(acaoDialogPreCadastroPessoa(), options, 400, 800);
	}
	
	private String acaoDialogPreCadastroPessoa() {	
		if (this.getIsVisualizacao()) {
			return "pretty:PreCadastroPessoaVisualizar";
		} 
		
		if (this.recuperando.getId() > 0) {
			return "pretty:PreCadastroPessoaAlterar";
		} else {
			return "pretty:PreCadastroPessoaCadastrar";
		}
	}

	public void associarPreCadastroPessoa(SelectEvent event) throws IOException {
		Pessoa pessoa = (Pessoa) event.getObject();

		if (pessoa == null) {
			if (this.recuperando.getId() <= 0) {
				FacesContext.getCurrentInstance().getExternalContext().redirect(Sigepen.APPLICATION_RECUPERANDO_URL);
			}
		} else {
			if (pessoa.getSexo() == Sexo.M) {
				this.recuperando.setGestante(false);
			}
			
			this.recuperando.setPessoa(pessoa);
		}
	}

	public boolean bloquearAbas() {
		return (this.recuperando.getId() <= 0);
	}
	

	/*
	 * Perículosidade
	 */

	public void alterarPericulosidade() {
		try {
			RequestContext.getCurrentInstance().execute("PF('altPericulosidade').hide()");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao alterar periculosidade: " + e.getMessage());
			LoggerUtil.logErro(Acao.ALTERAR, this.recuperando, e, log);
		}
	}

	/*
	 * Contatos
	 */
	public void abrirDialogContato(ActionEvent event) {
		this.contato = null;
		facesUtil.exibirPaginaDialog("AssociarContato");
	}

	public void abrirDialogAlterarContato(Contato contato) {
		this.contato = contato;
		facesUtil.setObjetoSessao("contato", contato);
		facesUtil.exibirPaginaDialog("AssociarContato");
		LoggerUtil.logInfo(Acao.ALTERAR, contato, log);
	}

	public void incluirContato(SelectEvent event) {
		Contato contato = (Contato) event.getObject();
		
		try {
			if (contato != null) {	
				if (this.contato != null) {
					this.recuperando.getPessoa().getContatos().remove(this.contato);
					this.recuperando.getPessoa().getContatos().add(contato);
				} else {
					contato.setPessoa(this.recuperando.getPessoa());
					this.recuperando.getPessoa().getContatos().add(contato);
				}
				if (this.recuperando.getPessoa().getId() > 0) {
					contatoService.salvarContato(contato);
					LoggerUtil.logInfo(Acao.SALVAR, contato, log);
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir contato no recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, contato, e, log);
		}
	}

	public void excluirContato(Contato contato) {
		try {
			this.recuperando.getPessoa().getContatos().remove(contato);
			
			if (this.recuperando.getPessoa().getId() > 0) {
				contatoService.excluirContato(contato);
				LoggerUtil.logInfo(Acao.EXCLUIR, contato, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir contato do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, contato, e, log);
		}
	}

	/*
	 * Documentos
	 */
	public void abrirDialogDocumento(ActionEvent event) {
		this.doc = null;
		facesUtil.exibirPaginaDialog("AssociarDocumento");
	}

	public void abrirDialogAlterarDocumento(Documento documento) {
		this.doc = new Documento(documento);
		facesUtil.setObjetoSessao("documento", documento);
		facesUtil.exibirPaginaDialog("AssociarDocumento");
		LoggerUtil.logInfo(Acao.ALTERAR, documento, log);
	}

	public void incluirDocumento(SelectEvent event) {
		Documento documento = (Documento) event.getObject();
		
		try {
			if (documento != null) {
				if (this.doc == null) {
					if (jaPossuiTipoDocumento(this.recuperando.getPessoa().getDocumentos(), documento.getTipoDocumento()) != null) {
						documento.setRecuperando(this.recuperando);
						this.recuperando.getDocumentos().add(documento);
					} else {
						documento.setPessoa(this.recuperando.getPessoa());
						this.recuperando.getPessoa().getDocumentos().add(documento);
					}
				} else {
					this.recuperando.getDocumentos().remove(documento);
					this.recuperando.getPessoa().getDocumentos().remove(documento);
	
					if (jaPossuiTipoDocumento(this.recuperando.getPessoa().getDocumentos(), documento.getTipoDocumento()) != null) {
						documento.setPessoa(null);
						documento.setRecuperando(this.recuperando);
						this.recuperando.getDocumentos().add(documento);
					} else {
						documento.setPessoa(this.recuperando.getPessoa());
						documento.setRecuperando(null);
						this.recuperando.getPessoa().getDocumentos().add(documento);
					}
				}
				if ((documento.getRecuperando() != null && documento.getRecuperando().getId() > 0) || (documento.getPessoa() != null && documento.getPessoa().getId() > 0)) {
					documentoService.salvarDocumento(documento);
					LoggerUtil.logInfo(Acao.SALVAR, documento, log);
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar documento ao recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, documento, e, log);
		}
	}

	public void tornarDocumentoPrincipal(Documento documento) {
		try {
			Documento doc = jaPossuiTipoDocumento(this.recuperando.getPessoa().getDocumentos(), documento.getTipoDocumento());
	
			if (doc != null) {
				this.recuperando.getPessoa().getDocumentos().remove(doc);
				doc.setPessoa(null);
				doc.setRecuperando(this.recuperando);
				this.recuperando.getDocumentos().add(doc);
				
				if ((doc.getRecuperando() != null && doc.getRecuperando().getId() > 0) || (doc.getPessoa() != null && doc.getPessoa().getId() > 0)) {
					documentoService.salvarDocumento(doc);
				}
			}
	
			this.recuperando.getDocumentos().remove(documento);
			documento.setPessoa(this.recuperando.getPessoa());
			documento.setRecuperando(null);
			this.recuperando.getPessoa().getDocumentos().add(documento);
			
			if ((documento.getRecuperando() != null && documento.getRecuperando().getId() > 0) || (documento.getPessoa() != null && documento.getPessoa().getId() > 0)) {
				documentoService.salvarDocumento(documento);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao alterar documento principal do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.ALTERAR, documento, e, log);
		}
	}

	public void excluirDocumento(Documento documento) {
		try {
			this.recuperando.getDocumentos().remove(documento);
			this.recuperando.getPessoa().getDocumentos().remove(documento);
	
			if ((documento.getRecuperando() != null && documento.getRecuperando().getId() > 0) || (documento.getPessoa() != null && documento.getPessoa().getId() > 0)) {
				documentoService.excluirDocumento(documento);
				LoggerUtil.logInfo(Acao.EXCLUIR, documento, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir documento do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, documento, e, log);
		}
	}

	public Documento jaPossuiTipoDocumento(List<Documento> documentos,
			TipoDocumento tipoDoc) {
		for (Documento doc : documentos) {
			if (doc.getTipoDocumento().equals(tipoDoc)) {
				return doc;
			}
		}
		return null;
	}

	/*
	 * Endereços
	 */

	public void abrirDialogEndereco(ActionEvent event) {
		this.endereco = null;
		facesUtil.exibirPaginaDialog("AssociarEndereco");
	}

	public void abrirDialogAlterarEndereco(Endereco endereco) {
		this.endereco = endereco;
		facesUtil.setObjetoSessao("endereco", endereco);
		facesUtil.exibirPaginaDialog("AssociarEndereco");
		LoggerUtil.logInfo(Acao.SALVAR, endereco, log);
	}

	public void excluirEndereco(Endereco endereco) {
		try {
			this.recuperando.getPessoa().getEnderecos().remove(endereco);
			
			if (this.recuperando.getPessoa().getId() > 0) {
				enderecoService.excluirEndereco(endereco);
				LoggerUtil.logInfo(Acao.EXCLUIR, endereco, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir endereço do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, endereco, e, log);
		}
	}

	public void incluirEndereco(SelectEvent event) {
		Endereco endereco = (Endereco) event.getObject();
		
		try {
			if (endereco != null) {
	
				if (this.endereco != null) {
					this.recuperando.getPessoa().getEnderecos().remove(this.endereco);
					this.recuperando.getPessoa().getEnderecos().add(endereco);
				} else {
					endereco.setPessoa(this.recuperando.getPessoa());
					this.recuperando.getPessoa().getEnderecos().add(endereco);
				}
				
				if (this.recuperando.getPessoa().getId() > 0) {
					enderecoService.salvarEndereco(endereco);
					LoggerUtil.logInfo(Acao.SALVAR, endereco, log);
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar endereço ao recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, endereco, e, log);
		}
	}

	/*
	 * Alcunhas
	 */
	public void novaAlcunha() {
		this.alcunha = new Alcunha();
		this.alcunhaAux = null;
	}

	public void alterarAlcunha(Alcunha alcunha) {
		this.alcunha = new Alcunha(alcunha);
		this.alcunhaAux = alcunha;
		LoggerUtil.logInfo(Acao.ALTERAR, alcunha, log);
	}

	public void excluirAlcunha(Alcunha alcunha) {
		try {
			this.recuperando.getAlcunhas().remove(alcunha);
			
			if (this.recuperando.getId() > 0) {
				service.excluirAlcunha(alcunha);
				LoggerUtil.logInfo(Acao.EXCLUIR, alcunha, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir alcunha do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, alcunha, e, log);
		}
	}

	public void incluirAlcunha() {
		try {
			if (this.alcunha != null) {
	
				if (this.alcunha.isPrincipal()) {
					for (Alcunha alcunha : this.recuperando.getAlcunhas()) {
						alcunha.setPrincipal(false);
					}
				}
				if (this.alcunhaAux == null) {
					this.alcunha.setRecuperando(this.recuperando);
					this.recuperando.getAlcunhas().add(this.alcunha);
				} else {
					this.recuperando.getAlcunhas().remove(this.alcunha);
					this.recuperando.getAlcunhas().add(this.alcunha);
				}
				if (this.recuperando.getId() > 0) {
					service.salvarAlcunha(this.alcunha);
					LoggerUtil.logInfo(Acao.SALVAR, this.alcunha, log);
				}
				
				RequestContext.getCurrentInstance().execute("PF('novaAlcunha').hide()");
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar alcunha ao recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, this.alcunha, e, log);
		}
	}

	/*
	 * Outros Nomes
	 */
	public void novoNome() {
		this.outroNome = new OutroNome();
		this.nomeAux = null;
	}

	public void alterarNome(OutroNome outroNome) {
		this.outroNome = new OutroNome(outroNome);
		this.nomeAux = outroNome;
		LoggerUtil.logInfo(Acao.ALTERAR, outroNome, log);
	}

	public void excluirNome(OutroNome outroNome) {
		try {
			this.recuperando.getOutrosNomes().remove(outroNome);
			
			if (this.recuperando.getId() > 0) {
				service.excluirOutroNome(outroNome);
				LoggerUtil.logInfo(Acao.EXCLUIR, outroNome, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir outro nome do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, outroNome, e, log);
		}
	}

	public void incluirNome() {	
		try {
			if (this.outroNome != null) {
				if (this.outroNome.getDataNascimento().after(this.getDataMaioridade())) {
					throw new ValidacaoException("Recuperando deve possuir maioridade.");
				}
				
				if (this.nomeAux == null) {
					this.outroNome.setRecuperando(this.recuperando);
					this.recuperando.getOutrosNomes().add(this.outroNome);
				} else {
					this.recuperando.getOutrosNomes().remove(this.outroNome);
					this.recuperando.getOutrosNomes().add(this.outroNome);
				}
				
				if (this.recuperando.getId()>0) {
					service.salvarOutroNome(this.outroNome);
					LoggerUtil.logInfo(Acao.SALVAR, this.outroNome, log);
				}
				
				RequestContext.getCurrentInstance().execute("PF('outroNome').hide()");
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Erro ao adicionar outro nome ao recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.outroNome, e, log);
		}
	}

	public void tornarNomePrincipal(OutroNome outroNome) {
		service.tornarNomePrincipal(this.recuperando, outroNome);
		LoggerUtil.logInfo(Acao.ALTERAR, this.recuperando, log);
	}

	/*
	 * Parentes
	 */

	public void adicionarParente() {
		this.parente = new Parente();
		this.parenteAux = null;
	}

	public void alterarParente(Parente parente) {
		this.parenteAux = new Parente(parente);
		this.parente = parente;
		LoggerUtil.logInfo(Acao.ALTERAR, parente, log);
	}

	public void excluirParente(Parente parente) {
		try {
			this.recuperando.getParentes().remove(parente);
			
			if (this.recuperando.getId() > 0) {
				service.excluirParente(parente);
				LoggerUtil.logInfo(Acao.EXCLUIR, parente, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir parente do recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, parente, e, log);
		}
	}

	public void incluirParente() {
		try {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, 4);
			c.set(Calendar.HOUR_OF_DAY, 00);
			c.set(Calendar.SECOND, 00);
			c.set(Calendar.MINUTE, 00);
			c.set(Calendar.MILLISECOND, 00);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			if (service.habilitaAlteracaoParente(this.recuperando)) {

				if (this.parente != null) {
					if (this.parenteAux == null) {
						if (service.limiteParentes(this.recuperando)) {
							this.parente.setRecuperando(this.recuperando);
							this.recuperando.getParentes().add(this.parente);
						} else {
							throw new ValidacaoException("Limite de parentes Ativos não pode ser excedido!");
						}
					} else if (this.parente.getStatus() == Status.ATIVO) {
						this.recuperando.getParentes().remove(this.parenteAux);

						if (service.limiteParentes(this.recuperando)) {
							this.recuperando.getParentes().add(this.parente);
						} else {
							this.recuperando.getParentes().add(this.parenteAux);
							throw new ValidacaoException("Limite de parentes Ativos não pode ser excedido!");
						}
					}
					
					if (this.parente.getDataNascimento() != null) {
						if (this.parente.getDataNascimento().after(new Date())) {
							throw new ValidacaoException("Data de nascimento não pode ser maior que a data atual.");
						}
					}
					
					this.recuperando.setAlteracaoParentes(c.getTime());
					
					if (this.recuperando.getId() > 0) {
						service.salvarParente(this.parente);
						LoggerUtil.logInfo(Acao.SALVAR, this.parente, log);
					}
					
					RequestContext.getCurrentInstance().execute("PF('novoParente').hide()");
				}
			} else {
				throw new ValidacaoException("Alterações só serão permitidas a partir de " + sdf.format(this.recuperando.getAlteracaoParentes()));
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar parente ao recuperando: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, this.parente, e, log);
		}
	}
	
	public void abrirDialogAtestados(RecuperandoDTO recuperando) {
		facesUtil.setObjetoSessao("recuperandoAtestado", recuperando);
		facesUtil.exibirPaginaDialog("AtestadosRecuperando", 170, 370);
	}
	
	public void imprimirRelatorioInfopen() {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			parameters.put("SCHEMA", Sigepen.SEJUDH_SCHEMA);
			parameters.put("UNIDADE_ID", this.unidadeInfopenSelecionada.getId());
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(Relatorio.EstatisticoInfopen, parameters));
			
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Relatório Estatístico Infopen", log);
		}
		catch (Exception e) {
			e.printStackTrace();
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir Relatório Estatístico Infopen: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Relatório Estatístico Infopen", e, log);
		}
	}
	
	public void imprimirRegistro(RecuperandoDTO recuperando) {
		try {
			
			this.recuperando = service.buscarRecuperandoPorId(recuperando.getId(), false);
			List<Recuperando> recs = new ArrayList<Recuperando>();
			this.recuperando.setRegimeAtual(this.recuperando.getRegimeAtual());
			this.recuperando.setCondicaoAtual(this.recuperando.getCondicaoAtual());
			this.recuperando.setLotacaoAtual(recuperando.getLotacaoAtualNomeEstrutura());
			this.recuperando.setUnidadeAtual(recuperando.getUnidade());
			
			List<Processo> processos = new ArrayList<Processo>();
			//Iteração para setar regime e condição atual de cada processo
			for(Processo p : this.recuperando.getProcessos()) {
				Processo proc = processoService.buscaProcessoPorId(p);
				
				List<ProcessoRegimeCondicao> collectProc = proc.getProcessosRegimeCondicao().stream()
					.sorted(Comparator.comparing(ProcessoRegimeCondicao::getId).reversed())
					.collect(Collectors.toList());

				for(ProcessoRegimeCondicao prc : collectProc) {					
					Processo pAux = new Processo();
					pAux.setId(proc.getId());
					pAux.setNumeroProcesso(proc.getNumeroProcesso());
					pAux.setNumeroInquerito(proc.getNumeroInquerito());
					pAux.setNumeroOficio(proc.getNumeroOficio());
					pAux.setRegimeAtual(proc.getRegimeAtual());
					pAux.setCondicaoAtual(proc.getCondicaoAtual());
					pAux.setCrimes(proc.getCrimes());
					pAux.setProcessosRegimeCondicao(Arrays.asList(prc));
					pAux.setStatusProcesso(proc.getStatusProcesso());
					pAux.setUltimoAdicionado(prc);
					processos.add(pAux);
				}
			}
			this.recuperando.getProcessos().clear();
			this.recuperando.setProcessos(processos);
			
			List<Foto> fotos = new ArrayList<Foto>();
			for(Foto f : this.recuperando.getFotos()){
				if(f.getIsAtual()){
					fotos.add(f);
				}
			}
			this.recuperando.getFotos().clear();
			this.recuperando.setFotos(fotos);
			
			for(Alcunha a : this.recuperando.getAlcunhas()){
				if(a.isPrincipal()){
					this.recuperando.setAlcunhaPrincipal(a.getNome());
				}
			}
			
			for(Parente p : this.recuperando.getParentes()){
				if(p.getParentesco().getDescricao().equals("Conjugê")){
					this.recuperando.setConjuge(p.getNome());
				}
			}
			
			int i = 0;
			for(Parente p : this.recuperando.getParentes()){
				if(p.getParentesco().getDescricao().equals("Filho(a)")){
					i++;
				}
			}
			this.recuperando.setQuantFilhos(i);
			recs.add(this.recuperando);
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(recs, Relatorio.RegistroRecuperando));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, recuperando, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir registro do recuperando (" + recuperando.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, recuperando, e, log);
		}
	}

	public Date getDataMaioridade(){
		Calendar now = Calendar.getInstance();
        now.add(Calendar.YEAR, -18);
        return now.getTime();
	}

	/*
	 * Getter e Setters
	 */
	public FiltroRecuperando getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroRecuperando filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<RecuperandoDTO> getRecuperandos() {
		return recuperandos;
	}

	public void setRecuperandos(LazyDataModel<RecuperandoDTO> recuperandos) {
		this.recuperandos = recuperandos;
	}

	public Recuperando getRecuperando() {
		return recuperando;
	}

	public void setRecuperando(Recuperando recuperando) {
		this.recuperando = recuperando;
	}

	public List<RecuperandoDTO> getRecuperandoSimilares() {
		return recuperandoSimilares;
	}

	public Alcunha getAlcunha() {
		return alcunha;
	}

	public void setAlcunha(Alcunha alcunha) {
		this.alcunha = alcunha;
	}

	public OutroNome getOutroNome() {
		return outroNome;
	}

	public void setOutroNome(OutroNome outroNome) {
		this.outroNome = outroNome;
	}

	public Parente getParente() {
		return parente;
	}

	public void setParente(Parente parente) {
		this.parente = parente;
	}

	public Date getDataMinima() {
		return new Date();
	}

	public List<Documento> getDocumentos() {
		if (this.recuperando.getId() > 0) {
			this.documentos.clear();
			this.documentos.addAll(this.recuperando.getPessoa().getDocumentos());
			this.documentos.addAll(this.recuperando.getDocumentos());
		}

		return documentos;
	}

	public String corPeric(String peric) {
		if (peric != null) {
			switch (peric) {

			case "BAIXA":
				return "#008000";

			case "MÉDIA":
				return "#fbf514";

			case "ALTA":
				return "#ffa31a";

			case "ALTÍSSIMA":
				return "#e50000";

			default:
				return "";
			}
		}
		return "";
	}

	public String getCorPericulosidade() {
		if (this.recuperando.getId() > 0
				&& recuperando.getPericulosidade() != null) {
			switch (recuperando.getPericulosidade()) {

			case BAIXA:
				return "#008000";

			case MÉDIA:
				return "#fbf514";

			case ALTA:
				return "#ffa31a";

			case ALTÍSSIMA:
				return "#e50000";

			default:
				return "";
			}
		}
		return "";
	}

	public void setCorPericulosidade(String corPericulosidade) {
		this.corPericulosidade = corPericulosidade;
	}

	public boolean alterarParentes() {
		return service.habilitaAlteracaoParente(this.recuperando);
	}

	public Foto getFotoDoFormulario() {
		return fotoDoFormulario;
	}

	public void setFotoDoFormulario(Foto fotoDoFormulario) {
		this.fotoDoFormulario = fotoDoFormulario;
	}

	public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public Particularidade getParticularidade() {
		return particularidade;
	}

	public void setParticularidade(Particularidade particularidade) {
		this.particularidade = particularidade;
	}

	public String getBiometria() {
		return biometria;
	}

	public void setBiometria(String biometria) {
		this.biometria = biometria;
	}

	public Passagem getPassagemSelecionada() {
		return passagemSelecionada;
	}

	public void setPassagemSelecionada(Passagem passagemSelecionada) {
		this.passagemSelecionada = passagemSelecionada;
	}

	public String getMovimentacaoInterna() {
		return movimentacaoInterna;
	}

	public void setMovimentacaoInterna(String movimentacaoInterna) {
		this.movimentacaoInterna = movimentacaoInterna;
	}

	public String getMovimentacaoExterna() {
		return movimentacaoExterna;
	}

	public void setMovimentacaoExterna(String movimentacaoExterna) {
		this.movimentacaoExterna = movimentacaoExterna;
	}

	public String getEmTransferencia() {
		return emTransferencia;
	}

	public void setEmTransferencia(String emTransferencia) {
		this.emTransferencia = emTransferencia;
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

	public RecuperandoDTO getRecuperandoSelecionado() {
		return recuperandoSelecionado;
	}

	public void setRecuperandoSelecionado(RecuperandoDTO recuperandoSelecionado) {
		this.recuperandoSelecionado = recuperandoSelecionado;
	}

	public Boolean getTemBiometria() {
		return temBiometria;
	}

	public void setTemBiometria(Boolean temBiometria) {
		this.temBiometria = temBiometria;
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}

	public Boolean getIsCadastro() {
		return isCadastro;
	}

	public void setIsCadastro(Boolean isCadastro) {
		this.isCadastro = isCadastro;
	}

	public UnidadePrisional getUnidadeInfopenSelecionada() {
		return unidadeInfopenSelecionada;
	}

	public void setUnidadeInfopenSelecionada(UnidadePrisional unidadeInfopenSelecionada) {
		this.unidadeInfopenSelecionada = unidadeInfopenSelecionada;
	}
}
