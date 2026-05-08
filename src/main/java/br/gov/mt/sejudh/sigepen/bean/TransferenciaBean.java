package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.enumerator.*;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTransferencia;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Escolta;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.Transferencia;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.model.dto.TransferenciaPedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.service.TransferenciaService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarTransferencia", pattern = "/transferencia", viewId = "/WEB-INF/pages/movimentacao/ListarTransferencia.xhtml")
,@URLMapping(id = "CadastrarTransferencia", pattern = "/transferencia/cadastrar", viewId = "/WEB-INF/pages/movimentacao/CadastrarTransferencia.xhtml")
,@URLMapping(id = "AlterarTransferencia", pattern = "/transferencia/alterar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarTransferencia.xhtml")
,@URLMapping(id = "VisualizarTransferencia", pattern = "/transferencia/visualizar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarTransferencia.xhtml")
,@URLMapping(id = "IniciarTransferencia", pattern = "/transferencia/iniciar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarTransferencia.xhtml")
,@URLMapping(id = "FinalizarTransferencia", pattern = "/transferencia/finalizar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarTransferencia.xhtml")
})
public class TransferenciaBean extends MetodosDePaginaBean {

	private FiltroTransferencia filtro;

	private Transferencia transferencia;
	
	private PedidoTransito pedido;
	
	private Escolta escolta;
	
	private LazyDataModel<Transferencia> transferencias;
	
	private Date dataMinima;
	
	private Boolean isIniciar;
	
	private Boolean isFinalizar;

	private List<Recuperando> recuperandosComBiometriaDesatualizada;

	@Inject
	private TransferenciaService service;
	
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
	private FileDownloadHandlerBean fileDownload;
	
	@Inject
	private Logger log;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		this.setFiltro(new FiltroTransferencia());
		this.getFiltro().setUnidadeOrigem(SecurityUtil.getUnidadeSelecionada());
		
		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			FiltroTransferencia filtroDash = (FiltroTransferencia) facesUtil
					.getFlashParameter("filtroTransferenciaDash");

			if (filtroDash != null) {
				setFiltro(filtroDash);

			}
		}
		super.carregarFiltroPaginacao(FiltroTransferencia.class, this, getFiltro());
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			Long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
			
			switch(this.getAcaoDeTela()) {
				case CADASTRAR:
					this.transferencia = new Transferencia(StatusMovimentacao.PENDENTE, SecurityUtil.getUnidadeSelecionada());
					break;
					
				case VISUALIZAR:
				case ALTERAR:
					if (!buscarTransferencia(id)) {
						facesUtil.exibirMensagemErro("Transferência não encontrada.", true);
						return "pretty:ListarTransferencia";
					}
					
					if (!validarTransferencia()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarTransferencia";
					}
					
					logDeAcesso(log, this.transferencia);
					break;
					
				case INICIAR:
				case FINALIZAR:
					if (!buscarTransferencia(id)) {
						facesUtil.exibirMensagemErro("Transferência não encontrada.", true);
						return "pretty:ListarTransferencia";
					}
					
					setIsIniciar(this.getAcaoDeTela() == AcaoDeTela.INICIAR);
					setIsFinalizar(this.getAcaoDeTela() == AcaoDeTela.FINALIZAR);
					
					if (!validarTransferencia()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarTransferencia";
					}

					logDeAcesso(log, this.transferencia);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Transferências");
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

	private boolean validarTransferencia() {
		if (this.transferencia.getUnidadeOrigem().getId() != SecurityUtil.getUnidadeSelecionada().getId()) {
			return false;
		}
		
		if (this.isIniciar != null) {
			if (this.isIniciar && this.transferencia.getStatus() != StatusMovimentacao.PENDENTE) {
				return false;
			}
		}
		
		if (this.isFinalizar != null) {
			if (this.isFinalizar && this.transferencia.getStatus() != StatusMovimentacao.EM_ANDAMENTO && this.transferencia.getStatus() != StatusMovimentacao.FINALIZADO_PARCIAL && this.transferencia.getStatus() != StatusMovimentacao.AGUARDANDO_FINALIZAR) {
				return false;
			}
		}
		
		return true;
	}

	private boolean buscarTransferencia(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.transferencia = service.buscaTransferenciaPorId(id);
		
		if (this.transferencia == null) {
			return false;
		}
		
		return true;
	}
	
	public String salvar() {
		String retorno = null;
		
		try {	
			 
			if (this.transferencia.getListTransferenciaPedidoTransitoDTO().isEmpty()) {
				throw new ValidacaoException("É necessário adicionar um pedido!");
			}
			
			if(service.isTranferenciaEmAndamento(transferencia.getId())) {				
				throw new ValidacaoException("A Trânsferencia já está em andamento!");
			}
			
			for (TransferenciaPedidoTransitoDTO ped : this.transferencia.getListTransferenciaPedidoTransitoDTO()){
				   
				Recuperando recuperando = this.serviceRecuperando.buscarRecuperandoPorId(ped.getMatricula(), false);
				 
				if (recuperando.getStatus() != StatusRecuperando.ATIVO) {
					throw new ValidacaoException("Recuperando inativo,remova o pedido "+ped.getNr()+"!");
				}
				
				if(serviceRecuperando.lotacaoAtual(recuperando).getUnidadePrisional().getId() != ped.getUnidadeOrigemId()){
					throw new ValidacaoException("Recuperando lotado em outra unidade, remova o pedido "+ped.getNr()+"!");
				}
				
				if(ped.getStatus() != StatusPedido.AUTORIZADO && this.transferencia.getId() != ped.getIdTransferencia()){
					throw new ValidacaoException("Pedido "+ped.getNr() +" não pode ser vinculado, remova o pedido!");
				}
				
			}			
			
			service.salvar(this.transferencia);
			facesUtil.exibirMensagemInfo("Transferência " + this.transferencia.getId() + " salva com sucesso!", true);
			retorno = "pretty:ListarTransferencia";
			LoggerUtil.logInfo(Acao.SALVAR, this.transferencia, log);			
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar Transferência (" + this.transferencia.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.transferencia, e, log);			
		}
		
		return retorno;
	}

	public void excluir(Transferencia transferencia) {
		try {
			service.excluir(transferencia);
			facesUtil.exibirMensagemInfo("Transferência" + transferencia.getId() + " excluída com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, transferencia, log);
		} 
		catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Transferência (" + transferencia.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, transferencia, e, log);		
		}
	}

	/*
	 * Inicia efetivamente uma transferencia	
	*/
	public String confirmarInicioTransferencia() {
		String retorno = null;
		
		try {			
			if (this.transferencia.getListTransferenciaPedidoTransitoDTO().isEmpty()) {
				throw new ValidacaoException("É necessário adicionar um pedido!");
			}			
			
			if (this.transferencia.getEscolta().isEmpty()) {
				throw new ValidacaoException("É necessário adicionar uma escolta!");
			}			
			
			for (TransferenciaPedidoTransitoDTO pedido : this.transferencia.getListTransferenciaPedidoTransitoDTO()) {
				Recuperando recuperando = this.serviceRecuperando.buscarRecuperandoPorId(pedido.getMatricula(), false);

				if(servicePedido.recuperandoEmMovimentacao(recuperando)){
					throw new ValidacaoException("Recuperando em movimentação, remova o pedido "+pedido.getNr()+"!");
				}

				if (recuperando.getStatus() != StatusRecuperando.ATIVO) {
					throw new ValidacaoException("Recuperando inativo,remova o pedido "+pedido.getNr()+"!");
				}
				
				if(serviceRecuperando.lotacaoAtual(recuperando).getUnidadePrisional().getId() != pedido.getUnidadeOrigemId()){
					throw new ValidacaoException("Recuperando lotado em outra unidade, remova o pedido "+pedido.getNr()+"!");
				}
				
				if(pedido.getStatus() != StatusPedido.AUTORIZADO && this.transferencia.getId() != pedido.getIdTransferencia()){
					throw new ValidacaoException("Pedido "+pedido.getNr() +" não pode ser vinculado, remova o pedido!");
				}				
			}
			
			service.iniciarTransferencia(this.transferencia);
			LoggerUtil.logInfo(Acao.INICIAR, this.transferencia, log);
			facesUtil.exibirMensagemInfo("Transferência " + this.transferencia.getId() + " iniciada com sucesso!", true);
			retorno = "pretty:ListarTransferencia";			
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao iniciar Transferência (" + this.transferencia.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.INICIAR, this.transferencia, e, log);			
		}
		
		return retorno;
	}
	
	/*
	 * Finaliza uma movimentação
	*/
	public String confirmarFinalizacaoTransferencia() {
		String retorno = null;
		
		try {
			for (TransferenciaPedidoTransitoDTO ped : this.transferencia.getListTransferenciaPedidoTransitoDTO()) {				
				if (ped.getStatus() != StatusPedido.CANCELADO && 
					    ped.getStatus() != StatusPedido.FINALIZADO) {
					    throw new ValidacaoException("Finalize ou cancele o pedido " + ped.getNr() + "!");
					}		
			}			
			
			service.finalizarTransferencia(this.transferencia);
			LoggerUtil.logInfo(Acao.FINALIZAR, this.transferencia, log);
			facesUtil.exibirMensagemInfo("Transferência " + this.transferencia.getId() + " finalizada com sucesso!", true);
			retorno = "pretty:ListarTransferencia";
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao finalizar transferência (" + this.transferencia.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.FINALIZAR, this.transferencia, e, log);
		}
		
		return retorno;
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
	public void confirmarFinalizacaoPedido(){
		try {
			servicePedido.finalizarPedidoTransferencia(this.pedido);
			LoggerUtil.logInfo(Acao.FINALIZAR, this.pedido, log);
			RequestContext.getCurrentInstance().execute("PF('finalizarPedido').hide()");
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao finalizar pedido (" + this.pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.FINALIZAR, this.pedido, e, log);
		}		
	}
	
	/*
	 * Prepara um pedido para ser cancelado
	*/
	public void cancelarPedido(PedidoTransito pedido) {		
		this.pedido = pedido;		
	}

	public void cancelarPedido(TransferenciaPedidoTransitoDTO pedidoDto) {
		this.pedido = new PedidoTransito();
		pedido.setId(pedidoDto.getNr());

		Transferencia t = new Transferencia();
		t.setId(pedidoDto.getIdTransferencia());
		pedido.setTransferencia(t);
	}
	
	/*
	 * Cancela um pedido
	*/
	public void confirmarCancelamentoPedido() {
		try {
			servicePedido.cancelarPedidoTransferencia(this.pedido);	
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
	public void cancelarTransferencia(Transferencia transferencia) {
		try {
			this.transferencia = service.buscaTransferenciaPorId(transferencia.getId());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	/*
	 * Cancela uma movimentação	e todos os pedidos vinculados
	*/
	public void confirmarCancelamentoTransferencia() {
		try {
			service.cancelarTransferencia(this.transferencia);	
			LoggerUtil.logInfo(Acao.CANCELAR, transferencia, log);
			RequestContext.getCurrentInstance().execute("PF('cancelarTransferencia').hide()");
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao cancelar transferencia (" + this.transferencia.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.CANCELAR, transferencia, e, log);
		}	
	}
	
	public void pesquisar() {
		this.transferencias = new LazyDataModel<Transferencia>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<Transferencia> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					LoggerUtil.logErro(e, log);
				}

				return service.buscaPorFiltro(getFiltro(), false);
			}
		};
	}
	
	public void imprimir() {
		try {			
			List<Transferencia> lista = service.buscaPorFiltroRelatorio(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarTransferencia));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Transferências", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de transferencias: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Transferências", e, log);
		}
	}
	
	public void imprimirRegistro(Transferencia transferencia) {
		try {
			List<Transferencia> lista = new ArrayList<Transferencia>();
			Transferencia transf = service.buscaTransferenciaImprimirRegistro(transferencia.getId());
			lista.add(transf);
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.RegistroTransferencia));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, transferencia, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir registro da transferência (" + transferencia.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, transferencia, e, log);
		}
	}
	
	public void abrirDialogPedido(ActionEvent event) {
		facesUtil.setObjetoSessao("tipoTransito", TipoTransito.TRANSFERENCIA);
		facesUtil.exibirPaginaDialog("pretty:AssociarPedidoTransferencia");
	}
	
	private Boolean verificarSeBiometriaEhObrigatoria() {
		return unidadeService.verificarSeBiometriaEhObrigatoria(SecurityUtil.getUnidadeSelecionada().getId());
	}
	
	private void validarRecuperandoBiometria(Recuperando recuperando) {
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
			
			for (TransferenciaPedidoTransitoDTO p : this.transferencia.getListTransferenciaPedidoTransitoDTO()) {
				if (!this.serviceRecuperando.recuperandoPossuiBiometriaAtualizada(p.getMatricula())) {
					this.recuperandosComBiometriaDesatualizada.add(this.serviceRecuperando.buscarRecuperandoPorId(p.getMatricula(), false));
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
			PedidoTransito pedido = servicePedido.buscarPedidoPorId(p.getId(), TipoMovimentacao.TRANSFERENCIA);
			
			if (service.isRecuperandoPossuiPedidoIncluso(this.transferencia.getListTransferenciaPedidoTransitoDTO(), pedido)) {
				throw new ValidacaoException("Recuperando já possui Pedido adicionado!");
			}
			
			if (pedido.getRecuperando().getStatus() != StatusRecuperando.ATIVO) {
				throw new ValidacaoException("Recuperando inativo, remova o pedido!");
			}
			
			if(serviceRecuperando.PedidoTransitolotacaoAtual(pedido.getRecuperando()).getUnidadePrisional().getId() != pedido.getUnidadeOrigem().getId()){
				throw new ValidacaoException("Recuperando lotado em outra unidade, remova o pedido!");
			}
			
			if(service.isTranferenciaEmAndamento(transferencia.getId())) {				
				throw new ValidacaoException("A Trânsferencia já está em andamento!");
			}

			validarRecuperandoBiometria(pedido.getRecuperando());

//		   this.transferencia.getPedidos().add(pedido);	
		   this.transferencia.getListTransferenciaPedidoTransitoDTO().add(convertePedidoEmTransferenciaPedidoDTO(pedido));	
					
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar Pedido (" + p.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, p, e, log);
		}
	}
	
	public TransferenciaPedidoTransitoDTO convertePedidoEmTransferenciaPedidoDTO(PedidoTransito pedido) {
		
		TransferenciaPedidoTransitoDTO dto = new TransferenciaPedidoTransitoDTO();
		dto.setDataPrevista(pedido.getDataPrevista());
		dto.setMatricula(pedido.getRecuperando().getId());
		dto.setNome(pedido.getRecuperando().getPessoa().getNome());
		dto.setNr(pedido.getId());
		dto.setStatusStr(pedido.getStatus().toString());
		dto.setStatus(pedido.getStatus());
		dto.setUnidadeDestino(pedido.getUnidadeDestino().getNome());
		dto.setIdTransferencia(this.transferencia.getId());
		dto.setUnidadeOrigem(pedido.getUnidadeOrigem().getNome());
		dto.setUnidadeOrigemId(pedido.getUnidadeOrigem().getId());
		
		return dto;
	}
	
	public void excluirPedido(PedidoTransito pedido) {
		try {
			this.transferencia.getPedidos().remove(pedido);
			pedido.setStatus(StatusPedido.AUTORIZADO);
			pedido.setTransferencia(null);
			servicePedido.salvar(pedido);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Pedido (" + pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, pedido, e, log);
		}
	}	
	
	public void excluirPedidoTransferenciaDTO(TransferenciaPedidoTransitoDTO transferenciaPedidoTransitoDTO) {
		try {
			this.transferencia.getListTransferenciaPedidoTransitoDTO().remove(transferenciaPedidoTransitoDTO);
			PedidoTransito pedidoTransito = this.servicePedido.buscaPedidoPorId(transferenciaPedidoTransitoDTO.getNr());
			pedidoTransito.setStatus(StatusPedido.AUTORIZADO);
			pedidoTransito.setTransferencia(null);
			servicePedido.salvar(pedidoTransito);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Pedido (" + pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, pedido, e, log);
		}
	}
	
	
	public void abrirDialogServidor(ActionEvent event) {
		this.escolta = new Escolta();
		facesUtil.setObjetoSessao("associarServidorEscolta", Boolean.TRUE);
		facesUtil.exibirPaginaDialog("pretty:AssociarServidor");
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
			
			if (service.servidorInclusoEscolta(this.transferencia.getEscolta(), servidor)) {
				throw new ValidacaoException("Servidor já incluso na escolta!");
			}
			
			this.escolta.setServidor(servidor);
			this.escolta.setTransferencia(this.transferencia);
				
			this.transferencia.getEscolta().add(escolta);
		
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar Servidor (" + s.getId() + "): " + e.getMessage());			
			LoggerUtil.logErro(Acao.ASSOCIAR, s, e, log);				
		}
	}
	
	public void excluirServidor(Escolta escolta){
		this.transferencia.getEscolta().remove(escolta);
	}		

	public FiltroTransferencia getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroTransferencia filtro) {
		this.filtro = filtro;
	}

	public Transferencia getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(Transferencia transferencia) {
		this.transferencia = transferencia;
	}

	public LazyDataModel<Transferencia> getTransferencias() {
		return transferencias;
	}

	public void setTransferencias(LazyDataModel<Transferencia> transferencias) {
		this.transferencias = transferencias;
	}

	public void setDataMinima(Date dataMinima) {
		this.dataMinima = dataMinima;
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
