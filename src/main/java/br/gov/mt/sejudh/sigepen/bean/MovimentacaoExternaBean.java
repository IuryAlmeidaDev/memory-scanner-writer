package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTransferencia;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroMovimentacaoExterna;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Escolta;
import br.gov.mt.sejudh.sigepen.model.MovimentacaoExterna;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.MovimentacaoExternaService;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarMovimentacaoExterna", pattern = "/movimentacao-externa", viewId = "/WEB-INF/pages/movimentacao/ListarMovimentacaoExterna.xhtml")
,@URLMapping(id = "CadastrarMovimentacaoExterna", pattern = "/movimentacao-externa/cadastrar", viewId = "/WEB-INF/pages/movimentacao/CadastrarMovimentacaoExterna.xhtml")
,@URLMapping(id = "AlterarMovimentacaoExterna", pattern = "/movimentacao-externa/alterar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarMovimentacaoExterna.xhtml")
,@URLMapping(id = "VisualizarMovimentacaoExterna", pattern = "/movimentacao-externa/visualizar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarMovimentacaoExterna.xhtml")
,@URLMapping(id = "IniciarMovimentacaoExterna", pattern = "/movimentacao-externa/iniciar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarMovimentacaoExterna.xhtml")
,@URLMapping(id = "FinalizarMovimentacaoExterna", pattern = "/movimentacao-externa/finalizar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarMovimentacaoExterna.xhtml")
})
public class MovimentacaoExternaBean extends MetodosDePaginaBean {

	private FiltroMovimentacaoExterna filtro;

	private MovimentacaoExterna movimentacao;
	
	private PedidoTransito pedido;
	
	private Escolta escolta;
	
	private LazyDataModel<MovimentacaoExterna> movimentacoes;
	
	private Date dataMinima;
	
	private Boolean isIniciar;
	
	private Boolean isFinalizar;
	
	private List<Recuperando> recuperandosComBiometriaDesatualizada;

	@Inject
	private MovimentacaoExternaService service;
	
	@Inject
	private PedidoService servicePedido;
	
	@Inject
	private RecuperandoService serviceRecuperando;
	
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
	public void carregaFiltros() throws Exception {
		this.setFiltro(new FiltroMovimentacaoExterna());
		this.getFiltro().setUnidadeOrigem(SecurityUtil.getUnidadeSelecionada());		
		
		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			FiltroMovimentacaoExterna filtroDash = (FiltroMovimentacaoExterna) facesUtil
					.getFlashParameter("filtroMovimentacaoExternaDash");

			if (filtroDash != null) {
				this.setFiltro(filtroDash);

			}
		}
		super.carregarFiltroPaginacao(FiltroMovimentacaoExterna.class, this, getFiltro());
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			Long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
			
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.movimentacao = new MovimentacaoExterna(StatusMovimentacao.PENDENTE, SecurityUtil.getUnidadeSelecionada());
					break;
					
				case VISUALIZAR:
				case ALTERAR:
					if (!buscarMovimentacao(id)) {
						facesUtil.exibirMensagemErro("Movimentação não encontrada.", true);
						return "pretty:ListarMovimentacaoExterna";
					}
					
					if (!validarMovimentacao()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarMovimentacaoExterna";
					}

					logDeAcesso(log, this.movimentacao);
					break;
					
				case INICIAR:
				case FINALIZAR:
					if (!buscarMovimentacao(id)) {
						facesUtil.exibirMensagemErro("Movimentação não encontrada.", true);
						return "pretty:ListarMovimentacaoExterna";
					}
					
					setIsIniciar(this.getAcaoDeTela() == AcaoDeTela.INICIAR);
					setIsFinalizar(this.getAcaoDeTela() == AcaoDeTela.FINALIZAR);
					
					if (!validarMovimentacao()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarMovimentacaoExterna";
					}

					logDeAcesso(log, this.movimentacao);
					break;
			
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Movimentações Externas");
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

	private boolean buscarMovimentacao(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.movimentacao = service.buscaMovimentacaoPorId(id);
		
		if (this.movimentacao == null) {
			return false;
		}
		
		return true;
	}
	
	private boolean validarMovimentacao() {
		if (this.movimentacao.getUnidadeOrigem().getId() != SecurityUtil.getUnidadeSelecionada().getId()) {
			return false;
		}
		
		if (this.isIniciar != null) {
			if (this.isIniciar && this.movimentacao.getStatus() != StatusMovimentacao.PENDENTE) {
				return false;
			}
		}
		
		if (this.isFinalizar != null) {
			if (this.isFinalizar && this.movimentacao.getStatus() != StatusMovimentacao.EM_ANDAMENTO && this.movimentacao.getStatus() != StatusMovimentacao.FINALIZADO_PARCIAL) {
				return false;
			}
		}
		
		return true;
	}
	
	public String salvar() {
		String retorno = null;	
		
		try {
			if (this.movimentacao.getPedidos().isEmpty()) {
				throw new ValidacaoException("É necessário adicionar um pedido!");
			}			
			
			for (PedidoTransito ped : this.movimentacao.getPedidos()){
				if (ped.getRecuperando().getStatus() != StatusRecuperando.ATIVO) {
					throw new ValidacaoException("Recuperando inativo,remova o pedido "+ped.getId()+"!");
				}
				
				if(serviceRecuperando.lotacaoAtual(ped.getRecuperando()).getUnidadePrisional().getId() != ped.getUnidadeOrigem().getId()){
					throw new ValidacaoException("Recuperando lotado em outra unidade, remova o pedido "+ped.getId()+"!");
				}
				
				if(ped.getStatus() != StatusPedido.AUTORIZADO && this.movimentacao.getId() != ped.getMovimentacaoExterna().getId()){
					throw new ValidacaoException("Pedido "+ped.getId() +" não pode ser vinculado, remova o pedido!");
				}
			}			
			
			service.salvar(this.movimentacao);
			facesUtil.exibirMensagemInfo("Movimentação " + this.movimentacao.getId() + " salva com sucesso!", true);
			retorno = "pretty:ListarMovimentacaoExterna";
			LoggerUtil.logInfo(Acao.SALVAR, this.movimentacao, log);			
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar movimentação (" + this.movimentacao.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.movimentacao, e, log);		
		}
		
		return retorno;
	}

	public void excluir(MovimentacaoExterna movimentacao) {
		try {
			service.excluir(movimentacao);
			facesUtil.exibirMensagemInfo("Movimentação" + movimentacao.getId() + " excluída com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, movimentacao, log);
		} 
		catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Erro ao excluir Movimentação (" + movimentacao.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, movimentacao, e, log);		
		}
	}
	
	/*
	 * Inicia efetivamente uma movimentação	
	*/
	public String confirmarInicioMovimentacao() {
		String retorno = null;
		
		try {		
			
			if (this.movimentacao.getPedidos().isEmpty()) {
				throw new ValidacaoException("É necessário adicionar um pedido!");
			}			
						
			if (this.movimentacao.getEscolta().isEmpty()) {
				throw new ValidacaoException("É necessário adicionar uma escolta!");
			}			
			
			for (PedidoTransito pedido : this.movimentacao.getPedidos()) {				
				if(servicePedido.recuperandoEmMovimentacao(pedido.getRecuperando())){
					throw new ValidacaoException("Recuperando em movimentação, remova o pedido "+pedido.getId()+"!");
				}

				if (pedido.getRecuperando().getStatus() != StatusRecuperando.ATIVO) {
					throw new ValidacaoException("Recuperando inativo,remova o pedido "+pedido.getId()+"!");
				}
				
				if(serviceRecuperando.lotacaoAtual(pedido.getRecuperando()).getUnidadePrisional().getId() != pedido.getUnidadeOrigem().getId()){
					throw new ValidacaoException("Recuperando lotado em outra unidade, remova o pedido "+pedido.getId()+"!");
				}
				
				if(pedido.getStatus() != StatusPedido.AUTORIZADO && this.movimentacao.getId() != pedido.getMovimentacaoExterna().getId()){
					throw new ValidacaoException("Pedido "+pedido.getId() +" não pode ser vinculado, remova o pedido!");
				}				
				
				validarBiometriaAtualizadaDoRecuperando(pedido.getRecuperando());
			}			
			
			service.iniciarMovimentacao(this.movimentacao);
			LoggerUtil.logInfo(Acao.INICIAR, this.movimentacao, log);
			facesUtil.exibirMensagemInfo("Movimentação " + this.movimentacao.getId() + " iniciada com sucesso!", true);
			retorno = "pretty:ListarMovimentacaoExterna";				
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao iniciar movimentação (" + this.movimentacao.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.INICIAR, this.movimentacao, e, log);
		}
		
		return retorno;
	}

	/*
	 * Finaliza uma movimentação
	*/
	public String confirmarFinalizacaoMovimentacao() {
		String retorno = null;		
		try {			
			for (PedidoTransito ped : this.movimentacao.getPedidos()) {				
				if(ped.getStatus() != StatusPedido.CANCELADO && ped.getStatus() != StatusPedido.FINALIZADO){
					throw new ValidacaoException("Finalize ou cancele o pedido "+ped.getId()+"!");
				}			
			}			
			
			service.finalizarMovimentacao(this.movimentacao);
			LoggerUtil.logInfo(Acao.FINALIZAR, movimentacao, log);
			facesUtil.exibirMensagemInfo("Movimentação " + this.movimentacao.getId() + " finalizada com sucesso!", true);
			retorno = "pretty:ListarMovimentacaoExterna";				
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao finalizar movimentação (" + this.movimentacao.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.FINALIZAR, this.movimentacao, e, log);
		}
		
		return retorno;
	}

	public void visualizarPedido(PedidoTransito pedido) {		
		this.pedido = pedido;		
	}
	
	/*
	 * Prepara um pedido para ser finalizado
	*/
	public void finalizarPedido(PedidoTransito pedido) {		
		this.pedido = pedido;		
	}
	
	/*
	 * Finaliza um pedido
	*/	
	public void confirmarFinalizacaoPedido() {
		try {
			servicePedido.finalizarPedidoMovExt(this.pedido);	
			LoggerUtil.logInfo(Acao.FINALIZAR, this.pedido, log);
			RequestContext.getCurrentInstance().execute("PF('finalizarPedido').hide()");
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Erro ao confirmar finalização do pedido (" + this.pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.FINALIZAR, this.pedido, e, log);
		}
	}
	
	/*
	 * Prepara um pedido para ser cancelado
	*/
	public void cancelarPedido(PedidoTransito pedido) {		
		this.pedido = pedido;		
	}
	
	/*
	 * Cancela um pedido
	*/
	public void confirmarCancelamentoPedido() {
		try {
			servicePedido.cancelarPedidoMovExt(this.pedido);	
			LoggerUtil.logInfo(Acao.CANCELAR, this.pedido, log);
			RequestContext.getCurrentInstance().execute("PF('cancelarPedido').hide()");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao confirmar cancelamento do pedido (" + this.pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.CANCELAR, this.pedido, e, log);
		}
	}
	
	/*
	 * Prepara uma movimentação	para ser cancelada
	*/
	public void cancelarMovimentacao(MovimentacaoExterna movimentacao) {
		try {
			this.movimentacao = service.buscaMovimentacaoPorId(movimentacao.getId());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao cancelar Movimentação (" + movimentacao.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.CANCELAR, movimentacao, e, log);
		}
	}
	
	/*
	 * Cancela uma movimentação	e todos os pedidos vinculados
	*/
	public void confirmarCancelamentoMovimentacao() {
		try {
			service.cancelarMovimentacao(this.movimentacao);	
			LoggerUtil.logInfo(Acao.CANCELAR, this.movimentacao, log);
			RequestContext.getCurrentInstance().execute("PF('cancelarMovimentacao').hide()");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao confirmar o cancelamento da movimentação (" + this.movimentacao.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.CANCELAR, this.movimentacao, e, log);
		}
	}

	public void pesquisar() {
		this.movimentacoes = new LazyDataModel<MovimentacaoExterna>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<MovimentacaoExterna> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return service.buscaPorFiltro(getFiltro(), false);
			}
		};
	}
	
	public void imprimir() {
		try {
			List<MovimentacaoExterna> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarMovimentacaoExterna));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Movimentações Externa", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de movimentações externa: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Movimentações Externa", e, log);
		}
	}
	
	public void imprimirRegistro(MovimentacaoExterna movimentacao) {
		try {
			List<MovimentacaoExterna> lista = new ArrayList<MovimentacaoExterna>();
			MovimentacaoExterna movimentacaoExterna = service.buscaMovimentacaoPorId(movimentacao.getId());
			lista.add(movimentacaoExterna);
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.RegistroMovimentacaoExterna));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, movimentacao, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir registro da movimentação externa (" + movimentacao.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, movimentacao, e, log);
		}
	}
	
	public void abrirDialogPedido(ActionEvent event) {
		facesUtil.setObjetoSessao("tipoTransito", TipoTransito.EXTERNA);
		facesUtil.exibirPaginaDialog("pretty:AssociarPedidoMovimentacaoExterna");
	}
	
	private Boolean verificarSeBiometriaEhObrigatoria() {
		return unidadeService.verificarSeBiometriaEhObrigatoria(SecurityUtil.getUnidadeSelecionada().getId());
	}
	
	private void validarBiometriaAtualizadaDoRecuperando(Recuperando recuperando) {
		if (verificarSeBiometriaEhObrigatoria()) {
			if (!this.serviceRecuperando.recuperandoPossuiBiometriaAtualizada(recuperando.getId())) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATUALIZAÇÃO CADASTRAL NECESSÁRIA", "A biometria deste recuperando está DESATUALIZADA.");
			}
		}
	}
	
	public void validarBiometriasDosRecuperandosDoPedido() {
		if (verificarSeBiometriaEhObrigatoria()) {
			if (this.recuperandosComBiometriaDesatualizada == null) {
				this.recuperandosComBiometriaDesatualizada = new ArrayList<>();
			}
			
			for (PedidoTransito p : this.movimentacao.getPedidos()) {
				if (!this.serviceRecuperando.recuperandoPossuiBiometriaAtualizada(p.getRecuperando().getId())) {
					this.recuperandosComBiometriaDesatualizada.add(p.getRecuperando());
				}
			}
			
			if (this.recuperandosComBiometriaDesatualizada.size() > 0) {
				RequestContext.getCurrentInstance().execute("PF('dialogAtualizarBiometria').show()");
			}
		}
	}
	
	public void associarPedido(SelectEvent event) {
		PedidoTransitoDTO p = (PedidoTransitoDTO) event.getObject();	
		
		if (p == null) {
			return;
		}
		
		try {
			PedidoTransito pedido = servicePedido.buscaPedidoPorId(p.getId());
					
			if (service.recuperandoPossuiPedidoIncluso(this.movimentacao.getPedidos(), pedido)) {
				throw new ValidacaoException("Recuperando já possui Pedido adicionado!");
			}
			
			if (pedido.getRecuperando().getStatus() != StatusRecuperando.ATIVO) {
				throw new ValidacaoException("Recuperando inativo, remova o pedido!");
			}
			
			if(serviceRecuperando.lotacaoAtual(pedido.getRecuperando()).getUnidadePrisional().getId() != pedido.getUnidadeOrigem().getId()){
				throw new ValidacaoException("Recuperando lotado em outra unidade, remova o pedido!");
			}			
			
			validarBiometriaAtualizadaDoRecuperando(pedido.getRecuperando());
		
		   this.movimentacao.getPedidos().add(pedido);		
					
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao associar pedido (" + p.getId() + "): " + e.getMessage());			
			LoggerUtil.logErro(Acao.ASSOCIAR, p, e, log);
		}		
		
	}
	
	public void excluirPedido(PedidoTransito pedido) {
		try {
			this.movimentacao.getPedidos().remove(pedido);
			pedido.setStatus(StatusPedido.AUTORIZADO);
			pedido.setMovimentacaoExterna(null);
			servicePedido.salvar(pedido);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Pedido (" + pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, pedido, e, log);
		}
	}	
	
	//Escolta
	public void abrirDialogServidor(ActionEvent event) {
		this.escolta = new Escolta();
		facesUtil.setObjetoSessao("associarServidorEscolta", Boolean.TRUE);
		facesUtil.setObjetoSessao("tipoTransito", TipoTransito.EXTERNA);
		facesUtil.setObjetoSessao("unidadeLotacao", SecurityUtil.getUnidadeSelecionada());
		facesUtil.exibirPaginaDialog("pretty:AssociarServidor");
		RequestContext.getCurrentInstance().execute("PF('adicionarEscolta').hide()");
	}
	
	public void associarServidor(SelectEvent event) {
		ServidorDTO s = (ServidorDTO) event.getObject();	
		
		if (s == null) {
			return;
		}
		
		try {
			Servidor servidor = servidorService.buscarServidorSimplesPorId(s.getId());
			
			if (servidor == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Servidor não encontrado.");
				return;
			}
			
			if (service.servidorInclusoEscolta(this.movimentacao.getEscolta(), servidor)) {
				throw new ValidacaoException("Servidor já incluso na escolta!");
			}
			
			this.escolta.setServidor(servidor);
			this.escolta.setDataInclusao(new Date());
			this.escolta.setMovimentacaoExterna(this.movimentacao);
				
			this.movimentacao.getEscolta().add(escolta);
			
			if(this.movimentacao.getStatus() == StatusMovimentacao.EM_ANDAMENTO || this.movimentacao.getStatus() == StatusMovimentacao.FINALIZADO_PARCIAL){			
				service.salvarMovimentacao(this.movimentacao);
				LoggerUtil.logInfo(Acao.ADICIONAR, this.escolta + ", " + this.movimentacao, log);
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar Servidor (" + s.getId() + "): " + e.getMessage());			
			LoggerUtil.logErro(Acao.ASSOCIAR, s, e, log);		
		}
	}
	
	public void escoltaExterna(){
		this.escolta = new Escolta();
	}
	
	public void adicionarEscoltaExterna() {
		try {
			this.escolta.setMovimentacaoExterna(this.movimentacao);
			this.escolta.setDataInclusao(new Date());
			this.movimentacao.getEscolta().add(this.escolta);
			
			if(this.movimentacao.getStatus() == StatusMovimentacao.EM_ANDAMENTO || this.movimentacao.getStatus() == StatusMovimentacao.FINALIZADO_PARCIAL){
				service.salvarMovimentacao(this.movimentacao);
				LoggerUtil.logInfo(Acao.ADICIONAR, this.escolta + ", " + this.movimentacao, log);
			}
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar escolta externa: " + e.getMessage());			
			LoggerUtil.logErro(Acao.ADICIONAR, this.escolta, e, log);
		}
	}
	
	public void excluirEscolta(Escolta escolta){
		this.movimentacao.getEscolta().remove(escolta);
	}
	
	public void acrescentarEscolta(MovimentacaoExterna movimentacao){
		try {
			this.movimentacao = service.buscaMovimentacaoPorId(movimentacao.getId());
			this.escolta = new Escolta();
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public FiltroMovimentacaoExterna getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroMovimentacaoExterna filtro) {
		this.filtro = filtro;
	}

	public MovimentacaoExterna getMovimentacao() {
		return movimentacao;
	}

	public void setMovimentacao(MovimentacaoExterna movimentacao) {
		this.movimentacao = movimentacao;
	}

	public LazyDataModel<MovimentacaoExterna> getMovimentacoes() {
		return movimentacoes;
	}

	public void setMovimentacoes(LazyDataModel<MovimentacaoExterna> movimentacoes) {
		this.movimentacoes = movimentacoes;
	}

	public PedidoTransito getPedido() {
		return pedido;
	}

	public void setPedido(PedidoTransito pedido) {
		this.pedido = pedido;
	}

	public Date getDataMinima() {
		dataMinima = new Date();		
		return dataMinima;
	}

	public Boolean getIsIniciar() {
		return isIniciar;
	}

	public void setIsIniciar(Boolean isIniciar) {
		this.isIniciar = isIniciar;
	}

	public Boolean getIsFinalizar() {
		return isFinalizar;
	}

	public void setIsFinalizar(Boolean isFinalizar) {
		this.isFinalizar = isFinalizar;
	}

	public Escolta getEscolta() {
		return escolta;
	}

	public void setEscolta(Escolta escolta) {
		this.escolta = escolta;
	}

	public List<Recuperando> getRecuperandosComBiometriaDesatualizada() {
		return recuperandosComBiometriaDesatualizada;
	}

	public void setRecuperandosComBiometriaDesatualizada(List<Recuperando> recuperandosComBiometriaDesatualizada) {
		this.recuperandosComBiometriaDesatualizada = recuperandosComBiometriaDesatualizada;
	}

}
