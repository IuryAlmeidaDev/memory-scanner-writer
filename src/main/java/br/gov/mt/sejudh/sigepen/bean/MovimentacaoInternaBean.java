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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroMovimentacaoExterna;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroMovimentacaoInterna;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.MovimentacaoPassos;
import br.gov.mt.sejudh.sigepen.enumerator.StatusMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.StatusPedido;
import br.gov.mt.sejudh.sigepen.enumerator.TipoMovimentacao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Escolta;
import br.gov.mt.sejudh.sigepen.model.MovimentacaoInterna;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Servidor;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.MovimentacaoInternaService;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarMovimentacaoInterna", pattern = "/movimentacao-interna", viewId = "/WEB-INF/pages/movimentacao/ListarMovimentacaoInterna.xhtml")
,@URLMapping(id = "CadastrarMovimentacaoInterna", pattern = "/movimentacao-interna/cadastrar", viewId = "/WEB-INF/pages/movimentacao/CadastrarMovimentacaoInterna.xhtml")
,@URLMapping(id = "AlterarMovimentacaoInterna", pattern = "/movimentacao-interna/alterar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarMovimentacaoInterna.xhtml")
,@URLMapping(id = "VisualizarMovimentacaoInterna", pattern = "/movimentacao-interna/visualizar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarMovimentacaoInterna.xhtml")
,@URLMapping(id = "IniciarMovimentacaoInterna", pattern = "/movimentacao-interna/iniciar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarMovimentacaoInterna.xhtml")
,@URLMapping(id = "FinalizarMovimentacaoInterna", pattern = "/movimentacao-interna/finalizar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarMovimentacaoInterna.xhtml")
})
public class MovimentacaoInternaBean extends MetodosDePaginaBean {

	private FiltroMovimentacaoInterna filtro;

	private MovimentacaoInterna movimentacao;
	
	private PedidoTransito pedido;
	
	private Escolta escolta;
	
	private LazyDataModel<MovimentacaoInterna> movimentacoes;
	
	private Date dataMinima;
	
	private Boolean isIniciar;
	
	private Boolean isFinalizar;

	@Inject
	private MovimentacaoInternaService service;
	
	@Inject
	private PedidoService servicePedido;
	
	@Inject
	private ServidorService servidorService;
		
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		this.setFiltro(new FiltroMovimentacaoInterna());
		this.getFiltro().setUnidadeOrigem(SecurityUtil.getUnidadeSelecionada());
		
		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			FiltroMovimentacaoInterna filtroDash = (FiltroMovimentacaoInterna) facesUtil
					.getFlashParameter("filtroMovimentacaoInternaDash");

			if (filtroDash != null) {
				setFiltro(filtroDash);

			}
		}

		super.carregarFiltroPaginacao(FiltroMovimentacaoInterna.class, this, getFiltro());
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			Long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
			
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.movimentacao = new MovimentacaoInterna(StatusMovimentacao.PENDENTE, SecurityUtil.getUnidadeSelecionada());
					break;
					
				case VISUALIZAR:
				case ALTERAR:					
					if (!buscarMovimentacao(id)) {
						facesUtil.exibirMensagemErro("Movimentação não encontrada.", true);
						return "pretty:ListarMovimentacaoInterna";
					}
					
					if (!validarMovimentacao()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarMovimentacaoInterna";
					}
					
					logDeAcesso(log, this.movimentacao);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Movimentações Internas");
					break;
					
				case INICIAR:
				case FINALIZAR:
					if (!buscarMovimentacao(id)) {
						facesUtil.exibirMensagemErro("Movimentação não encontrada.", true);
						return "pretty:ListarMovimentacaoInterna";
					}
					
					setIsIniciar(this.getAcaoDeTela() == AcaoDeTela.INICIAR);
					setIsFinalizar(this.getAcaoDeTela() == AcaoDeTela.FINALIZAR);
					
					if (!validarMovimentacao()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarMovimentacaoInterna";
					}

					logDeAcesso(log, this.movimentacao);
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
			if (this.isFinalizar != null && this.isFinalizar && this.movimentacao.getStatus() != StatusMovimentacao.EM_ANDAMENTO && this.movimentacao.getStatus() != StatusMovimentacao.FINALIZADO_PARCIAL) {
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
			
			for (PedidoTransito ped : this.movimentacao.getPedidos()) {
				service.validarMovimentacaoInterna(ped, movimentacao, MovimentacaoPassos.SALVAR, false);
			}			
			
			service.salvar(this.movimentacao);
			facesUtil.exibirMensagemInfo("Movimentação " + this.movimentacao.getId() + " salva com sucesso!", true);
			retorno = "pretty:ListarMovimentacaoInterna";
			LoggerUtil.logInfo(Acao.SALVAR, this.movimentacao, log);			
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());		
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar movimentação (" + this.movimentacao.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.movimentacao, e, log);			
		}
		
		return retorno;
	}
	
	public void excluir(MovimentacaoInterna movimentacao) {
		try {
			service.excluir(movimentacao);
			facesUtil.exibirMensagemInfo("Movimentação" + movimentacao.getId() + " excluída com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, movimentacao, log);
		} 
		catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Movimentação (" + movimentacao.getId() + "): " + e.getMessage());
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
			
			for (PedidoTransito pedido: this.movimentacao.getPedidos()) {
				service.validarMovimentacaoInterna(pedido, this.movimentacao, MovimentacaoPassos.INICIAR, false);			
			}			
			
			service.iniciarMovimentacao(this.movimentacao);
			LoggerUtil.logInfo(Acao.INICIAR, movimentacao, log);
			facesUtil.exibirMensagemInfo("Movimentação " + this.movimentacao.getId() + " iniciada com sucesso!", true);
			retorno = "pretty:ListarMovimentacaoInterna";				
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
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
			
			 List<PedidoTransito> peds = servicePedido.buscarPedidosPorMovimentacaoId(this.movimentacao.getId());
			for (PedidoTransito ped : peds) {
				service.validarMovimentacaoInterna(ped, this.movimentacao, MovimentacaoPassos.FINALIZAR, false);
			}

			service.finalizarMovimentacao(this.movimentacao);
			LoggerUtil.logInfo(Acao.FINALIZAR, movimentacao, log);
			facesUtil.exibirMensagemInfo("Movimentação " + this.movimentacao.getId() + " finalizada com sucesso!", true);
			retorno = "pretty:ListarMovimentacaoInterna";				
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao finalizar movimentação (" + this.movimentacao.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.FINALIZAR, this.movimentacao, e, log);
		}
		
		return retorno;
	}
	
	/*
	 * Prepara um pedido para ser finalizado
	*/
	public void finalizarPedido(PedidoTransito pedido) {		
		this.pedido = this.servicePedido.buscarPedidoPorIdComPassagens(pedido.getId(), TipoMovimentacao.TRANSITO_INTERNO);		
	
	}
	
	/*
	 * Finaliza um pedido
	*/	
	public void confirmarFinalizacaoPedido() {
		try {
			servicePedido.finalizarPedidoMovInt(this.pedido);	
			LoggerUtil.logInfo(Acao.FINALIZAR, this.pedido, log);
			RequestContext.getCurrentInstance().execute("PF('finalizarPedido').hide()");
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Erro ao confirmar finalização do pedido (" + this.pedido.getId() + "): " + e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao confirmar finalização do pedido (" + this.pedido.getId() + "): " + e.getMessage());
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
			servicePedido.cancelarPedidoMovInt(this.pedido);
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
	public void cancelarMovimentacao(MovimentacaoInterna movimentacao) {
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
	
	/*
	 * Consulta movimentação com base no FILTRO	
	*/
	public void pesquisar() {		
		this.movimentacoes = new LazyDataModel<MovimentacaoInterna>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<MovimentacaoInterna> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
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
			List<MovimentacaoInterna> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarMovimentacaoInterna));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Movimentações Interna", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de movimentações interna: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Movimentações Interna", e, log);
		}
	}
	
	public void imprimirRegistro(MovimentacaoInterna movimentacao) {
		try {
			List<MovimentacaoInterna> lista = new ArrayList<MovimentacaoInterna>();
			MovimentacaoInterna movimentacaoInterna = service.buscaMovimentacaoPorId(movimentacao.getId());
			lista.add(movimentacaoInterna);
						
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.RegistroMovimentacaoInterna));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, movimentacao, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir registro da movimentação interna (" + movimentacao.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, movimentacao, e, log);
		}
	}
	
		
	public void abrirDialogPedido(ActionEvent event) {
		facesUtil.setObjetoSessao("tipoTransito", TipoTransito.INTERNA);
		facesUtil.exibirPaginaDialog("pretty:AssociarPedidoMovimentacaoInterna");
	}
	
	public void associarPedido(SelectEvent event) {
		PedidoTransitoDTO p = (PedidoTransitoDTO) event.getObject();	
		
		if (p == null) {
			return;
		}

		try {
			PedidoTransito pedido = servicePedido.buscaPedidoPorId(p.getId());
			
			service.validarMovimentacaoInterna(pedido, this.movimentacao, MovimentacaoPassos.ASSOCIAR_PEDIDO, false);

			this.movimentacao.getPedidos().add(pedido);
		} catch (ValidacaoException e) {  
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao associar pedido (" + p.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, p, e, log);
		}
	}
	
	public void excluirPedido(PedidoTransito pedido) {
		try {
			this.movimentacao.getPedidos().remove(pedido);
			pedido.setStatus(StatusPedido.AUTORIZADO);
			pedido.setMovimentacaoInterna(null);
			servicePedido.salvar(pedido);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Pedido (" + pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, pedido, e, log);
		}
	}	
	
	
	public void abrirDialogServidor(ActionEvent event) {
		this.escolta = new Escolta();
		facesUtil.setObjetoSessao("associarServidorEscolta", Boolean.TRUE);
		facesUtil.setObjetoSessao("tipoTransito", TipoTransito.INTERNA);
		facesUtil.setObjetoSessao("unidadeLotacao", SecurityUtil.getUnidadeSelecionada());
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
			
			if (service.servidorInclusoEscolta(this.movimentacao.getEscolta(), servidor)) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Servidor já incluso na escolta!");
				return;
			}
			
			this.escolta.setServidor(servidor);
			this.escolta.setMovimentacaoInterna(this.movimentacao);			
			this.movimentacao.getEscolta().add(this.escolta);	
		
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar Servidor (" + s.getId() + "): " + e.getMessage());			
			LoggerUtil.logErro(Acao.ASSOCIAR, s, e, log);
		}
	}
	
	public void excluirServidor(Escolta escolta){
		this.movimentacao.getEscolta().remove(escolta);
	}
	
	
	public FiltroMovimentacaoInterna getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroMovimentacaoInterna filtro) {
		this.filtro = filtro;
	}

	public MovimentacaoInterna getMovimentacao() {
		return movimentacao;
	}

	public void setMovimentacao(MovimentacaoInterna movimentacao) {
		this.movimentacao = movimentacao;
	}

	public LazyDataModel<MovimentacaoInterna> getMovimentacoes() {
		return movimentacoes;
	}

	public void setMovimentacoes(LazyDataModel<MovimentacaoInterna> movimentacoes) {
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
	
}
