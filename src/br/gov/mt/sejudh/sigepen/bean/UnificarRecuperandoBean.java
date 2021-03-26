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
import javax.faces.bean.SessionScoped;
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
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroUnificarRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.OrientacaoParticularidade;
import br.gov.mt.sejudh.sigepen.enumerator.RegiaoParticularidade;
import br.gov.mt.sejudh.sigepen.enumerator.Sexo;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
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
import br.gov.mt.sejudh.sigepen.model.Escolaridade;
import br.gov.mt.sejudh.sigepen.model.EstadoCivil;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.HistoricoRecuperando;
import br.gov.mt.sejudh.sigepen.model.IdentidadeDeGenero;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.OrientacaoSexual;
import br.gov.mt.sejudh.sigepen.model.OutroNome;
import br.gov.mt.sejudh.sigepen.model.Parente;
import br.gov.mt.sejudh.sigepen.model.Particularidade;
import br.gov.mt.sejudh.sigepen.model.Passagem;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.Processo;
import br.gov.mt.sejudh.sigepen.model.ProcessoRegimeCondicao;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Religiao;
import br.gov.mt.sejudh.sigepen.model.TipoDocumento;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.Usuario;
import br.gov.mt.sejudh.sigepen.model.dto.DadosRecuperandoUnificacaoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ExtratoRecuperandoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RecuperandoDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.BiometriaService;
import br.gov.mt.sejudh.sigepen.service.ContatoService;
import br.gov.mt.sejudh.sigepen.service.DocumentoService;
import br.gov.mt.sejudh.sigepen.service.EnderecoService;
import br.gov.mt.sejudh.sigepen.service.HistoricoRecuperandoService;
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
@SessionScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarUnificarRecuperando", pattern = "/recuperando/listar", viewId = "/WEB-INF/pages/recuperando/ListarUnificarRecuperando.xhtml"),
		@URLMapping(id = "UnificarRecuperando", pattern = "/recuperando/unificar", viewId = "/WEB-INF/pages/recuperando/UnificarRecuperando.xhtml")

})
public class UnificarRecuperandoBean extends MetodosDePaginaBean {

    private List<RecuperandoDTO> recuperandosSelecionados;
    
	private RecuperandoDTO recuperandoSelecionado1;
	
	private Pessoa pessoaRecuperandoSelecionado1;
	
	private Recuperando recuperando1;
	
	private List<Documento> documentosRecuperando1;
    
    private RecuperandoDTO recuperandoSelecionado2;
    
	private Pessoa pessoaRecuperandoSelecionado2;
	
	private Recuperando recuperando2;
	
	private List<Documento> documentosRecuperando2;
    
    //Dados do recuperando selecionados
	private DadosRecuperandoUnificacaoDTO dadosRecuperandoUnificacaoDTO;

	@Inject
	private PedidoService pedidoService;
	
	@Inject
	private HistoricoRecuperandoService historicoRecuperandoService;
	
	//******************************************************************************************************	
	
	
	private String biometria;
		
	private FiltroUnificarRecuperando filtro;
	
	private LazyDataModel<RecuperandoDTO> recuperandos;
	
	private List<RecuperandoDTO> recuperandoSimilares;
	
	private Recuperando recuperando;
	
	private Foto fotoDoFormulario;

	private Contato contato;

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
	
	private UnidadePrisional unidadeInfopenSelecionada;
	
	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			FiltroUnificarRecuperando filtroDash = (FiltroUnificarRecuperando)facesUtil.getFlashParameter("filtroRecuperandoDash");
			
			if (filtroDash != null) {
				setFiltro(filtroDash);
			} else {
				super.carregarFiltroPaginacao(FiltroUnificarRecuperando.class, this);
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
					//pesquisar();
					inicializarVariaveis();
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
	
	public String executarUnificacao() {
		
		//RN01 - O Sistema deve automaticamente selecionar o número de matrícula que for mais antigo (menor número de matrícula)
		this.dadosRecuperandoUnificacaoDTO = new DadosRecuperandoUnificacaoDTO();
		if(recuperandoSelecionado1.getId() < recuperandoSelecionado2.getId()) {
			dadosRecuperandoUnificacaoDTO.setIdSelecionado(recuperandoSelecionado1.getId());
		}else {
			dadosRecuperandoUnificacaoDTO.setIdSelecionado(recuperandoSelecionado2.getId());
		}
		
		pessoaRecuperandoSelecionado1 = pessoaService.buscarDadosPessoaRecuperandoPorId(recuperandoSelecionado1.getPessoaId());
		pessoaRecuperandoSelecionado2 = pessoaService.buscarDadosPessoaRecuperandoPorId(recuperandoSelecionado2.getPessoaId());
		
		this.recuperando1 = service.buscarRecuperandoPorId(recuperandoSelecionado1.getId(), false);
		this.recuperando2 = service.buscarRecuperandoPorId(recuperandoSelecionado2.getId(), false);
		
		this.documentosRecuperando1 = new ArrayList<Documento>();
		this.documentosRecuperando2 = new ArrayList<Documento>();
		
		//E03. Recuperando com passagens ativas
		if(recuperando1.getStatus().equals(StatusRecuperando.ATIVO) && recuperando2.getStatus().equals(StatusRecuperando.ATIVO)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é permitido unificar o cadastro de recuperando com passagens ativas! Favor verificar!");
			return "";
		}	
		
		//E05. Recuperando com passagens concomitantes
		if(!validaPassagensConcomitantes()) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é permitido unificar cadastros de recuperando que possuem passagens concomitantes!");
			return "";
		}
		
		//E06. Recuperando com pedido de trânsito em andamento (Recuperando 1)
		if(!validaPedidoTransitoAndamento(recuperando1.getId())) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é possível unificar os cadastros deste recuperando pois o recuperando "+recuperando1.getPessoa().getNome()+" possui pedido de trânsito em andamento!");
			return "";
		}
		//E06. Recuperando com pedido de trânsito em andamento (Recuperando 2)
		if(!validaPedidoTransitoAndamento(recuperando2.getId())) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é possível unificar os cadastros deste recuperando pois o recuperando "+recuperando2.getPessoa().getNome()+" possui pedido de trânsito em andamento!");
			return "";
		}
		
		return "pretty:UnificarRecuperando";
	}
	
	public boolean validaPedidoTransitoAndamento(Long recuperandoId) {
		FiltroPedTransito filtroPedTransito = new FiltroPedTransito();
		filtroPedTransito.setRecuperandoId(recuperandoId);
		filtroPedTransito.setStatus(StatusPedido.EM_ANDAMENTO);
		List<PedidoTransitoDTO> listPedidoTransito = pedidoService.buscaPorFiltro(filtroPedTransito, false);
		
		return !(listPedidoTransito != null && !listPedidoTransito.isEmpty() && listPedidoTransito.size() > 0);
	}

	
	/**
	 * Validação de passagens concomitantes
	 * 
	 * É considerado passagen concomitante quando a data de entrada ou saída de uma passagem de um cadastro de recuperando estiver em interseção com as datas de entrada e saída de uma passagem do outro cadastro (ou seja, uma data não pode estar contida na outra).
	 * 
	 * @return false se existem passagens concomitantes e true se não existem passagens concomitantes
	 */
	private boolean validaPassagensConcomitantes() {
		for(Passagem passagemRecuperando1 : recuperando1.getPassagens()) {
			for(Passagem passagemRecuperando2 : recuperando2.getPassagens()) {
				if (passagemRecuperando1.getDataSaida() != null && (passagemRecuperando2.getDataEntrada().after(passagemRecuperando1.getDataEntrada()) && passagemRecuperando2.getDataEntrada().before(passagemRecuperando1.getDataSaida()))) {
					return false;
				}else if (passagemRecuperando2.getDataSaida() != null && passagemRecuperando1.getDataSaida() != null && (passagemRecuperando2.getDataSaida().after(passagemRecuperando1.getDataEntrada()) && passagemRecuperando2.getDataSaida().before(passagemRecuperando1.getDataSaida()))) {
					return false;
				}
			}
		}
		for(Passagem passagemRecuperando2 : recuperando2.getPassagens()) {
			for(Passagem passagemRecuperando1 : recuperando1.getPassagens()) {
				if (passagemRecuperando2.getDataSaida() != null && (passagemRecuperando1.getDataEntrada().after(passagemRecuperando2.getDataEntrada()) && passagemRecuperando1.getDataEntrada().before(passagemRecuperando2.getDataSaida()))) {
					return false;
				}else if (passagemRecuperando1.getDataSaida() != null && passagemRecuperando2.getDataSaida() != null && (passagemRecuperando1.getDataSaida().after(passagemRecuperando2.getDataEntrada()) && passagemRecuperando1.getDataSaida().before(passagemRecuperando2.getDataSaida()))) {
					return false;
				}
			}
		}
		return true;
	}
	
	public List<Documento> getDocumentosRecuperando1() {
		if (this.recuperando1.getId() > 0) {
			this.documentosRecuperando1.clear();
			this.documentosRecuperando1.addAll(this.recuperando1.getPessoa().getDocumentos());
			this.documentosRecuperando1.addAll(this.recuperando1.getDocumentos());
		}

		return documentosRecuperando1;
	}
	
	public List<Documento> getDocumentosRecuperando2() {
		if (this.recuperando2.getId() > 0) {
			this.documentosRecuperando2.clear();
			this.documentosRecuperando2.addAll(this.recuperando2.getPessoa().getDocumentos());
			this.documentosRecuperando2.addAll(this.recuperando2.getDocumentos());
		}

		return documentosRecuperando2;
	}
	
	public String onFlowProcess(FlowEvent event) {
            return event.getNewStep();
    }
	
	public String salvarUnificacao() {        
        
		//RN01 - O Sistema deve automaticamente selecionar o número de matrícula que for mais antigo (menor número de matrícula)
		if (getRecuperando1().getId() < getRecuperando2().getId()) {
			unificarDadosPessoais(getRecuperando1());
			unificarPassagens(getRecuperando1());
			unificarVidaSocial(getRecuperando1());
			unificarFotos(getRecuperando1());
			unificarParticularidades(getRecuperando1());
			unificarAlcunha(getRecuperando1());
			unificarOutrosNomes(getRecuperando1());
			unificarDocumentos(getRecuperando1());
			unificarEnderecos(getRecuperando1());
			unificarContatos(getRecuperando1());
			unificarParentes(getRecuperando1());
			unificarPedidoTransito(getRecuperando1());
			unificarHistoricoRecuperando(getRecuperando1());
			unificarAutorizacaoCadastro(getRecuperando1());
			unificarStatusRecuperando(getRecuperando1());

			try {
				service.excluirRecuperandoUnificacao(getRecuperando2());
				service.salvarRecuperandoUnificacao(getRecuperando1());
				service.atualizarRegimeCondicaoAtualRecuperando(getRecuperando1());
				limparRecuperandosUnificacao();

				facesUtil.exibirMensagemInfo("Recuperando Unificado com Sucesso!", true);

				return "pretty:ListarUnificarRecuperando";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			unificarDadosPessoais(getRecuperando2());
			unificarPassagens(getRecuperando2());
			unificarVidaSocial(getRecuperando2());
			unificarFotos(getRecuperando2());
			unificarParticularidades(getRecuperando2());
			unificarAlcunha(getRecuperando2());
			unificarOutrosNomes(getRecuperando2());
			unificarDocumentos(getRecuperando2());
			unificarEnderecos(getRecuperando2());
			unificarContatos(getRecuperando2());
			unificarParentes(getRecuperando2());
			unificarPedidoTransito(getRecuperando2());
			unificarHistoricoRecuperando(getRecuperando2());
			unificarAutorizacaoCadastro(getRecuperando2());
			unificarStatusRecuperando(getRecuperando2());

			try {
				service.excluirRecuperandoUnificacao(getRecuperando1());
				service.salvarRecuperandoUnificacao(getRecuperando2());
				service.atualizarRegimeCondicaoAtualRecuperando(getRecuperando2());
				limparRecuperandosUnificacao();

				facesUtil.exibirMensagemInfo("Recuperando Unificado com Sucesso!", true);
				
				return "pretty:ListarUnificarRecuperando";

			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,"Erro", "Erro ao salvar unificação do Recuperando!");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		
		return "";
		
    }
	
	public void limparRecuperandosUnificacao() {
	    recuperandosSelecionados = new ArrayList<RecuperandoDTO>();
		recuperandoSelecionado1 = null;
		pessoaRecuperandoSelecionado1 = new Pessoa();
		recuperando1 = new Recuperando();
		documentosRecuperando1 = new ArrayList<Documento>();
		
	    recuperandoSelecionado2 = null;
	    pessoaRecuperandoSelecionado2 = new Pessoa();
		recuperando2 = new Recuperando();
		documentosRecuperando2 = new ArrayList<Documento>();
	}
	
	private void unificarStatusRecuperando(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId() && !recuperando.getStatus().equals(StatusRecuperando.ATIVO) && getRecuperando1().getStatus().equals(StatusRecuperando.ATIVO)) {
			recuperando.setStatus(StatusRecuperando.ATIVO);
		} else if (getRecuperando2().getId() != recuperando.getId() && !recuperando.getStatus().equals(StatusRecuperando.ATIVO) && getRecuperando2().getStatus().equals(StatusRecuperando.ATIVO)) {
			recuperando.setStatus(StatusRecuperando.ATIVO);
		}
	}
	private void unificarPedidoTransito(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			FiltroPedTransito filtroPedTransito = new FiltroPedTransito();
			filtroPedTransito.setRecuperandoId(getRecuperando1().getId());
			List<PedidoTransitoDTO> listPedidoTransito = pedidoService.buscaPorFiltro(filtroPedTransito, false);
			
			for(PedidoTransitoDTO pedidoTransitoDTO : listPedidoTransito) {
				PedidoTransito pedidoTransito = pedidoService.buscaPedidoPorId(pedidoTransitoDTO.getId());
				pedidoTransito.setRecuperando(recuperando);
				pedidoService.salvar(pedidoTransito);
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			FiltroPedTransito filtroPedTransito = new FiltroPedTransito();
			filtroPedTransito.setRecuperandoId(getRecuperando2().getId());
			List<PedidoTransitoDTO> listPedidoTransito = pedidoService.buscaPorFiltro(filtroPedTransito, false);
			
			for(PedidoTransitoDTO pedidoTransitoDTO : listPedidoTransito) {
				PedidoTransito pedidoTransito = pedidoService.buscaPedidoPorId(pedidoTransitoDTO.getId());
				pedidoTransito.setRecuperando(recuperando);
				pedidoService.salvar(pedidoTransito);
			}
		}
	}
	
	private void unificarHistoricoRecuperando(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			List<HistoricoRecuperando> listHistoricoRecuperandos = historicoRecuperandoService.listarHistoricoRecuperando(getRecuperando1().getId());
			
			for(HistoricoRecuperando historicoRecuperando : listHistoricoRecuperandos) {
				historicoRecuperando.setRecuperando(recuperando);
				historicoRecuperandoService.salvar(historicoRecuperando);
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			List<HistoricoRecuperando> listHistoricoRecuperandos = historicoRecuperandoService.listarHistoricoRecuperando(getRecuperando2().getId());
			
			for(HistoricoRecuperando historicoRecuperando : listHistoricoRecuperandos) {
				historicoRecuperando.setRecuperando(recuperando);
				historicoRecuperandoService.salvar(historicoRecuperando);
			}
		}
	}
	
	private void unificarAutorizacaoCadastro(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			if(recuperando.getPessoa().getAutorizacaoCadastro() == null) {
				recuperando.getPessoa().setAutorizacaoCadastro(getRecuperando1().getPessoa().getAutorizacaoCadastro());
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			if(recuperando.getPessoa().getAutorizacaoCadastro() == null) {
				recuperando.getPessoa().setAutorizacaoCadastro(getRecuperando2().getPessoa().getAutorizacaoCadastro());
			}
		}
	}
	
	private void unificarDadosPessoais(Recuperando recuperando) {
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNomeNomeMaeDataNascimento()) {
			recuperando.getPessoa().setNome(getRecuperando1().getPessoa().getNome());
			recuperando.getPessoa().setNomeMae(getRecuperando1().getPessoa().getNomeMae());
			recuperando.getPessoa().setDataNascimento(getRecuperando1().getPessoa().getDataNascimento());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNomeNomeMaeDataNascimento()) {
			recuperando.getPessoa().setNome(getRecuperando2().getPessoa().getNome());
			recuperando.getPessoa().setNomeMae(getRecuperando2().getPessoa().getNomeMae());
			recuperando.getPessoa().setDataNascimento(getRecuperando2().getPessoa().getDataNascimento());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdSexo()) {
			recuperando.getPessoa().setSexo(getRecuperando1().getPessoa().getSexo());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdSexo()) {
			recuperando.getPessoa().setSexo(getRecuperando2().getPessoa().getSexo());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNacionalidade()) {
			recuperando.getPessoa().setNacionalidade(getRecuperando1().getPessoa().getNacionalidade());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNacionalidade()) {
			recuperando.getPessoa().setNacionalidade(getRecuperando2().getPessoa().getNacionalidade());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdNaturalidade()) {
			recuperando.getPessoa().setNaturalidade(getRecuperando1().getPessoa().getNaturalidade());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdNaturalidade()) {
			recuperando.getPessoa().setNaturalidade(getRecuperando2().getPessoa().getNaturalidade());
		}

		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdSinalTatuagem()) {
			recuperando.setTemSinalOuTatuagem(getRecuperando1().getTemSinalOuTatuagem());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdSinalTatuagem()) {
			recuperando.setTemSinalOuTatuagem(getRecuperando2().getTemSinalOuTatuagem());
		}

		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdAltura()) {
			recuperando.setAltura(getRecuperando1().getAltura());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdAltura()) {
			recuperando.setAltura(getRecuperando2().getAltura());
		}

		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdPeso()) {
			recuperando.setPeso(getRecuperando1().getPeso());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdPeso()) {
			recuperando.setPeso(getRecuperando2().getPeso());
		}

		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdTipoSanguineo()) {
			recuperando.setTipoSanguineo(getRecuperando1().getTipoSanguineo());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdTipoSanguineo()) {
			recuperando.setTipoSanguineo(getRecuperando2().getTipoSanguineo());
		}

		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdRaca()) {
			recuperando.setRaca(getRecuperando1().getRaca());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdRaca()) {
			recuperando.setRaca(getRecuperando2().getRaca());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdCorOlhos()) {
			recuperando.setCorOlho(getRecuperando1().getCorOlho());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdCorOlhos()) {
			recuperando.setCorOlho(getRecuperando2().getCorOlho());
		}
		
		if(getRecuperando1().getId() == dadosRecuperandoUnificacaoDTO.getIdCorCabelo()) {
			recuperando.setCorCabelo(getRecuperando1().getCorCabelo());
		}else if(getRecuperando2().getId() == dadosRecuperandoUnificacaoDTO.getIdCorCabelo()) {
			recuperando.setCorCabelo(getRecuperando2().getCorCabelo());
		}

	}
	
	private void unificarFotos(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Foto foto : getRecuperando1().getFotos()) {
				foto.setRecuperando(recuperando);
				recuperando.getFotos().add(foto);
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Foto foto : getRecuperando2().getFotos()) {
				foto.setRecuperando(recuperando);
				recuperando.getFotos().add(foto);
			}
		}
	}
	
	private void unificarParticularidades(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Particularidade particularidade : getRecuperando1().getParticularidades()) {
				particularidade.setRecuperando(recuperando);
				recuperando.getParticularidades().add(particularidade);
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Particularidade particularidade : getRecuperando2().getParticularidades()) {
				particularidade.setRecuperando(recuperando);
				recuperando.getParticularidades().add(particularidade);
			}
		}
	}

	private void unificarPassagens(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Passagem passagem : getRecuperando1().getPassagens()) {
				passagem.setRecuperando(recuperando);
				recuperando.getPassagens().add(passagem);
			}
			
			for (Processo processo : getRecuperando1().getProcessos()) {
				processo.setRecuperando(recuperando);
				recuperando.getProcessos().add(processo);
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Passagem passagem : getRecuperando2().getPassagens()) {
				passagem.setRecuperando(recuperando);
				recuperando.getPassagens().add(passagem);
			}
			
			for (Processo processo : getRecuperando2().getProcessos()) {
				processo.setRecuperando(recuperando);
				recuperando.getProcessos().add(processo);
			}
		}
	}
	
	private void unificarVidaSocial(Recuperando recuperando) {
		if (dadosRecuperandoUnificacaoDTO.getIdOrientacaoSexual() != null) {
			OrientacaoSexual orientacaoSexual = new OrientacaoSexual();
			orientacaoSexual.setId(dadosRecuperandoUnificacaoDTO.getIdOrientacaoSexual());
			recuperando.setOrientacaoSexual(orientacaoSexual);
		}

		if (dadosRecuperandoUnificacaoDTO.getIdIdentidadeDeGenero() != null) {
			IdentidadeDeGenero identidadeDeGenero = new IdentidadeDeGenero();
			identidadeDeGenero.setId(dadosRecuperandoUnificacaoDTO.getIdIdentidadeDeGenero());
			recuperando.setIdentidadeDeGenero(identidadeDeGenero);
		}
		recuperando.getPessoa().setNomeSocial(dadosRecuperandoUnificacaoDTO.getNomeSocial());
		
		EstadoCivil estadoCivil = new EstadoCivil();
		estadoCivil.setId(dadosRecuperandoUnificacaoDTO.getIdEstadoCivil());
		recuperando.setEstadoCivil(estadoCivil);
		
		Escolaridade escolaridade = new Escolaridade();
		escolaridade.setId(dadosRecuperandoUnificacaoDTO.getIdEscolaridade());
		recuperando.setEscolaridade(escolaridade);
		
		recuperando.setProfissao(dadosRecuperandoUnificacaoDTO.getProfissao());
		
		Religiao religiao = new Religiao();
		religiao.setId(dadosRecuperandoUnificacaoDTO.getIdReligiao());
		recuperando.setReligiao(religiao);
				
		//recuperando.setPne(TipoDeficiencia.valueOf(dadosRecuperandoUnificacaoDTO.getPne()));
	}
	
	private void unificarAlcunha(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Alcunha alcunha1 : getRecuperando1().getAlcunhas()) {
				Boolean principal = false;
				Boolean duplicada = false;
				for(Alcunha alcunha2 : getRecuperando2().getAlcunhas()) {
					if(alcunha1.isPrincipal() && alcunha2.isPrincipal() && !principal) {
						principal = true;
					}
					if(alcunha1.getNome().equalsIgnoreCase(alcunha2.getNome()) && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					if(principal) {
						alcunha1.setRecuperando(recuperando);
						alcunha1.setPrincipal(false);
						recuperando.getAlcunhas().add(alcunha1);
					}else {
						alcunha1.setRecuperando(recuperando);
						recuperando.getAlcunhas().add(alcunha1);
					}
				}
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Alcunha alcunha2 : getRecuperando2().getAlcunhas()) {
				Boolean principal = false;
				Boolean duplicada = false;
				for(Alcunha alcunha1 : getRecuperando1().getAlcunhas()) {
					if(alcunha2.isPrincipal() && alcunha1.isPrincipal() && !principal) {
						principal = true;
					}
					if(alcunha2.getNome().equalsIgnoreCase(alcunha1.getNome()) && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					if(principal) {
						alcunha2.setRecuperando(recuperando);
						alcunha2.setPrincipal(false);
						recuperando.getAlcunhas().add(alcunha2);
					}else {
						alcunha2.setRecuperando(recuperando);
						recuperando.getAlcunhas().add(alcunha2);
					}
				}
			}
		}
	}
	
	private void unificarOutrosNomes(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (OutroNome outroNome1 : getRecuperando1().getOutrosNomes()) {
				Boolean duplicada = false;
				for(OutroNome outroNome2 : getRecuperando2().getOutrosNomes()) {
					if(outroNome1.getNome().equalsIgnoreCase(outroNome2.getNome()) && outroNome1.getNomeMae().equalsIgnoreCase(outroNome2.getNomeMae()) && outroNome1.getDataNascimento().compareTo(outroNome2.getDataNascimento()) == 0 && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					outroNome1.setRecuperando(recuperando);
					recuperando.getOutrosNomes().add(outroNome1);
				}
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (OutroNome outroNome2 : getRecuperando2().getOutrosNomes()) {
				Boolean duplicada = false;
				for(OutroNome outroNome1 : getRecuperando1().getOutrosNomes()) {
					if(outroNome2.getNome().equalsIgnoreCase(outroNome1.getNome()) && outroNome2.getNomeMae().equalsIgnoreCase(outroNome1.getNomeMae()) && outroNome2.getDataNascimento().compareTo(outroNome1.getDataNascimento()) == 0 && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					outroNome2.setRecuperando(recuperando);
					recuperando.getOutrosNomes().add(outroNome2);
				}
			}
		}
	}
	
	private void unificarDocumentos(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Documento documento1 : getRecuperando1().getDocumentos()) {
				Boolean duplicada = false;
				for(Documento documento2 : getRecuperando2().getDocumentos()) {
					if(documento1.getTipoDocumento().getId() == documento2.getTipoDocumento().getId() && documento1.getNumero().equalsIgnoreCase(documento2.getNumero()) && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					documento1.setRecuperando(recuperando);
					documento1.setPessoa(recuperando.getPessoa());
					recuperando.getDocumentos().add(documento1);
				}
			}
						
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Documento documento2 : getRecuperando2().getDocumentos()) {
				Boolean duplicada = false;
				for(Documento documento1 : getRecuperando1().getDocumentos()) {
					if(documento2.getTipoDocumento().getId() == documento1.getTipoDocumento().getId() && documento2.getNumero().equalsIgnoreCase(documento1.getNumero()) && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					documento2.setRecuperando(recuperando);
					documento2.setPessoa(recuperando.getPessoa());
					recuperando.getDocumentos().add(documento2);
				}
			}
		}
	}
	
	private void unificarEnderecos(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Endereco endereco1 : getRecuperando1().getPessoa().getEnderecos()) {
				Boolean duplicada = false;
				for(Endereco endereco2 : getRecuperando2().getPessoa().getEnderecos()) {
					if(endereco1.getLogradouro().equalsIgnoreCase(endereco2.getLogradouro()) && endereco1.getNumero().equalsIgnoreCase(endereco2.getNumero()) && endereco1.getComplemento().equalsIgnoreCase(endereco2.getComplemento()) && endereco1.getBairro().equalsIgnoreCase(endereco2.getBairro())
							&& endereco1.getCidade().getId() == endereco2.getCidade().getId()  && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					endereco1.setPessoa(recuperando.getPessoa());
					if(recuperando.getPessoa().getEnderecos() != null) {
						recuperando.getPessoa().getEnderecos().add(endereco1);						
					}else {
						recuperando.getPessoa().setEnderecos(new ArrayList<Endereco>());
						recuperando.getPessoa().getEnderecos().add(endereco1);						
					}
				}
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Endereco endereco2 : getRecuperando2().getPessoa().getEnderecos()) {
				Boolean duplicada = false;
				for(Endereco endereco1 : getRecuperando1().getPessoa().getEnderecos()) {
					if(endereco2.getLogradouro().equalsIgnoreCase(endereco1.getLogradouro()) && endereco2.getNumero().equalsIgnoreCase(endereco1.getNumero()) && endereco2.getComplemento().equalsIgnoreCase(endereco1.getComplemento()) && endereco2.getBairro().equalsIgnoreCase(endereco1.getBairro())
							&& endereco2.getCidade().getId() == endereco1.getCidade().getId()  && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					endereco2.setPessoa(recuperando.getPessoa());
					if(recuperando.getPessoa().getEnderecos() != null) {
						recuperando.getPessoa().getEnderecos().add(endereco2);						
					}else {
						recuperando.getPessoa().setEnderecos(new ArrayList<Endereco>());
						recuperando.getPessoa().getEnderecos().add(endereco2);						
					}
				}
			}
		}
	}
	
	private void unificarContatos(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Contato contato1 : getRecuperando1().getPessoa().getContatos()) {
				Boolean duplicada = false;
				for(Contato contato2 : getRecuperando2().getPessoa().getContatos()) {
					if(contato1.getNome().equalsIgnoreCase(contato2.getNome()) && contato1.getTipoContato().getDescricao().equalsIgnoreCase(contato2.getTipoContato().getDescricao()) && contato1.getValor().equalsIgnoreCase(contato2.getValor())  && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					contato1.setPessoa(recuperando.getPessoa());
					if(recuperando.getPessoa().getContatos() != null) {
						recuperando.getPessoa().getContatos().add(contato1);
					}else {
						recuperando.getPessoa().setContatos(new ArrayList<Contato>());
						recuperando.getPessoa().getContatos().add(contato1);
					}
				}
			}
			
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Contato contato2 : getRecuperando2().getPessoa().getContatos()) {
				Boolean duplicada = false;
				for(Contato contato1 : getRecuperando1().getPessoa().getContatos()) {
					if(contato2.getNome().equalsIgnoreCase(contato1.getNome()) && contato2.getTipoContato().getDescricao().equalsIgnoreCase(contato1.getTipoContato().getDescricao()) && contato2.getValor().equalsIgnoreCase(contato1.getValor())  && !duplicada) {
						duplicada = true;
					}
				}
				if(!duplicada) {
					contato2.setPessoa(recuperando.getPessoa());
					if(recuperando.getPessoa().getContatos() != null) {
						recuperando.getPessoa().getContatos().add(contato2);
					}else {
						recuperando.getPessoa().setContatos(new ArrayList<Contato>());
						recuperando.getPessoa().getContatos().add(contato2);
					}
				}
			}
		}
	}
	
	private void unificarParentes(Recuperando recuperando) {
		if (getRecuperando1().getId() != recuperando.getId()) {
			for (Parente parente : getRecuperando1().getParentes()) {
				parente.setRecuperando(recuperando);
				recuperando.getParentes().add(parente);
			}
			if (recuperando.getAlteracaoParentes().before(getRecuperando1().getAlteracaoParentes())) {
				recuperando.setAlteracaoParentes(getRecuperando1().getAlteracaoParentes());
			}
		} else if (getRecuperando2().getId() != recuperando.getId()) {
			for (Parente parente : getRecuperando2().getParentes()) {
				parente.setRecuperando(recuperando);
				recuperando.getParentes().add(parente);
			}
			if (recuperando.getAlteracaoParentes().before(getRecuperando2().getAlteracaoParentes())) {
				recuperando.setAlteracaoParentes(getRecuperando2().getAlteracaoParentes());
			}
		}
	}
	


	/*
	 * Métodos da Listagem
	 */
	public void pesquisar() {
		if(getFiltro() != null && (getFiltro().getNome() == null || getFiltro().getNome().isEmpty()) && (getFiltro().getNomeMae() == null || getFiltro().getNomeMae().isEmpty())  && (getFiltro().getDataNascimento() == null) ) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Na consulta de recuperando é obrigatório informar pelo menos um dos filtros de consulta (Nome do Recuperando ou Nome da mãe ou Data de Nascimento)");
		}else {
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
				List<RecuperandoDTO> recuperandos = new ArrayList<RecuperandoDTO>();
				
				if(getFiltro() != null && (getFiltro().getNome() == null || getFiltro().getNome().isEmpty()) && (getFiltro().getNomeMae() == null || getFiltro().getNomeMae().isEmpty())  && (getFiltro().getDataNascimento() == null) ) {
					setRowCount(0);

					try {
						configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}else {
					
				setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

					recuperandos = service.buscarRecuperandosPorFiltro(getFiltro(), false);
				}
				
				return recuperandos;
			}
		};
		}
	}
	
	/**
	 * seleciona recuperando para unificação 
	 * 
	 * @param recuperandoDTO
	 */
	public void selecionarRecuperandoUnificar(RecuperandoDTO recuperandoDTO) {
		if (recuperandoSelecionado1 != null && recuperandoSelecionado1.equals(recuperandoDTO)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é possível selecionar duas vezes o mesmo recuperando para unificação!");
		}else if (recuperandoSelecionado2 != null && recuperandoSelecionado2.equals(recuperandoDTO)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Não é possível selecionar duas vezes o mesmo recuperando para unificação!");
		}else if (recuperandoSelecionado1 != null && recuperandoSelecionado2 != null) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO","Selecione no máximo dois registros para unificação!");
		} else if (recuperandoSelecionado1 == null) {
			this.recuperandoSelecionado1 = recuperandoDTO;
		} else if (recuperandoSelecionado2 == null) {
			this.recuperandoSelecionado2 = recuperandoDTO;
		}
	}
	
	/**
	 * Limpa seleção de recuperando para unificação
	 * 
	 * @param recuperandoDTO
	 */
	public void limparSelecaoRecuperando(RecuperandoDTO recuperandoDTO) {
		if(recuperandoSelecionado1 != null && recuperandoSelecionado1.equals(recuperandoDTO)) {
			this.recuperandoSelecionado1 = null;
		}else if(recuperandoSelecionado2 != null && recuperandoSelecionado2.equals(recuperandoDTO)) {
			this.recuperandoSelecionado2 = null;
		}
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
	
	public void imprimirExtratoRecuperando(Recuperando recuperando) {
		gerarExtratoRecuperando(service.gerarEntidadeExtratoRecuperando(recuperando, null));
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

			if (this.recuperando.getId() > 0) {
				service.excluirParente(parente);
				LoggerUtil.logInfo(Acao.EXCLUIR, parente, log);
			}
            this.recuperando.getParentes().remove(parente);
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
	public FiltroUnificarRecuperando getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroUnificarRecuperando filtro) {
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

	public UnidadePrisional getUnidadeInfopenSelecionada() {
		return unidadeInfopenSelecionada;
	}

	public void setUnidadeInfopenSelecionada(UnidadePrisional unidadeInfopenSelecionada) {
		this.unidadeInfopenSelecionada = unidadeInfopenSelecionada;
	}

    public void setRecuperandosSelecionados(List<RecuperandoDTO> recuperandosSelecionados) {
		this.recuperandosSelecionados = recuperandosSelecionados;
	}

	public List<RecuperandoDTO> getRecuperandosSelecionados() {
		return recuperandosSelecionados;
	}


	public RecuperandoDTO getRecuperandoSelecionado1() {
		return recuperandoSelecionado1;
	}

	public void setRecuperandoSelecionado1(RecuperandoDTO recuperandoSelecionado1) {
		this.recuperandoSelecionado1 = recuperandoSelecionado1;
	}

	public RecuperandoDTO getRecuperandoSelecionado2() {
		return recuperandoSelecionado2;
	}

	public void setRecuperandoSelecionado2(RecuperandoDTO recuperandoSelecionado2) {
		this.recuperandoSelecionado2 = recuperandoSelecionado2;
	}
	

	public DadosRecuperandoUnificacaoDTO getDadosRecuperandoUnificacaoDTO() {
		return dadosRecuperandoUnificacaoDTO;
	}

	public void setDadosRecuperandoUnificacaoDTO(DadosRecuperandoUnificacaoDTO dadosRecuperandoUnificacaoDTO) {
		this.dadosRecuperandoUnificacaoDTO = dadosRecuperandoUnificacaoDTO;
	}

	public Pessoa getPessoaRecuperandoSelecionado1() {
		return pessoaRecuperandoSelecionado1;
	}

	public void setPessoaRecuperandoSelecionado1(Pessoa pessoaRecuperandoSelecionado1) {
		this.pessoaRecuperandoSelecionado1 = pessoaRecuperandoSelecionado1;
	}

	public Pessoa getPessoaRecuperandoSelecionado2() {
		return pessoaRecuperandoSelecionado2;
	}

	public void setPessoaRecuperandoSelecionado2(Pessoa pessoaRecuperandoSelecionado2) {
		this.pessoaRecuperandoSelecionado2 = pessoaRecuperandoSelecionado2;
	}

	public Recuperando getRecuperando1() {
		return recuperando1;
	}

	public void setRecuperando1(Recuperando recuperando1) {
		this.recuperando1 = recuperando1;
	}

	public Recuperando getRecuperando2() {
		return recuperando2;
	}

	public void setRecuperando2(Recuperando recuperando2) {
		this.recuperando2 = recuperando2;
	}
	
	public void setDocumentosRecuperando1(List<Documento> documentosRecuperando1) {
		this.documentosRecuperando1 = documentosRecuperando1;
	}

	public void setDocumentosRecuperando2(List<Documento> documentosRecuperando2) {
		this.documentosRecuperando2 = documentosRecuperando2;
	}

}
