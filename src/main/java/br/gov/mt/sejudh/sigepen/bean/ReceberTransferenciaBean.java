package br.gov.mt.sejudh.sigepen.bean;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.enumerator.TipoMovimentacao;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.TipoFoto;
import br.gov.mt.sejudh.sigepen.enumerator.TipoTransito;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarReceberTransferencia", pattern = "/recebimento", viewId = "/WEB-INF/pages/movimentacao/ListarReceberTransferencia.xhtml")
,@URLMapping(id = "ReceberTransferencia", pattern = "/recebimento/receber/#{id}", viewId = "/WEB-INF/pages/movimentacao/ReceberTransferencia.xhtml")
,@URLMapping(id = "VisualizarReceberTransferencia", pattern = "/recebimento/visualizar/#{id}", viewId = "/WEB-INF/pages/movimentacao/ReceberTransferencia.xhtml")
})
public class ReceberTransferenciaBean extends MetodosDePaginaBean {

	private FiltroPedTransito filtro;

	private PedidoTransito pedido;
	
	private LazyDataModel<PedidoTransitoDTO> pedidos;
	
	private Date dataMinima;

	@Inject
	private PedidoService service;
	
	@Inject
	private RecuperandoService recService;
		
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		this.setFiltro(new FiltroPedTransito());
		this.getFiltro().setUnidadeDestino(SecurityUtil.getUnidadeSelecionada());
		
		super.carregarFiltroPaginacao(FiltroPedTransito.class, this, getFiltro());
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
				case VISUALIZAR:
				case RECEBER:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarRecebimento(id)) {
						facesUtil.exibirMensagemErro("Pedido não encontrado.", true);
						return "pretty:ListarReceberTransferencia";
					}
					
					if (!validarRecebimento()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarReceberTransferencia";
					}

					logDeAcesso(log, this.pedido);
					break;
				
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Recebimento de Transferências");
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

	private boolean validarRecebimento() {
		if (this.pedido.getTipoTransito() != TipoTransito.TRANSFERENCIA) {
			return false;
		}
		
		if (this.pedido.getUnidadeDestino().getId() != SecurityUtil.getUnidadeSelecionada().getId()) {
			return false;
		}
		
		return true;
	}

	private boolean buscarRecebimento(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.pedido = service.buscarPedidoTransferenciaPorId(id);
		
		if (this.pedido == null) {
			return false;
		}
		
		return true;
	}

	public String salvar() {
		String retorno = null;
		
		try {
			if (this.pedido.getRecuperando() == null) {
				throw new ValidacaoException("É necessário adicionar um recuperando!");
			}
			
			if (this.pedido.getEstruturaDestino() == null) {
				throw new ValidacaoException("É necessário adicionar uma estrutura!");
			}
			
			if (this.pedido.getFoto() == null) {
				throw new ValidacaoException("É necessário adicionar uma foto!");
			}else if (this.pedido.getFoto().getImagem() == null){
				throw new ValidacaoException("É necessário adicionar uma foto!");
			}
			
			recService.salvarFoto(this.pedido.getFoto());
						
			service.finalizarPedidoTransferencia(this.pedido);
			facesUtil.exibirMensagemInfo("Transferência " + this.pedido.getId() + " recebida com sucesso!", true);
			retorno = "pretty:ListarReceberTransferencia";
			LoggerUtil.logInfo(Acao.FINALIZAR, this.pedido, log);			
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar pedido (" + this.pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.FINALIZAR, this.pedido, e, log);			
		}
		
		return retorno;
	}
		
	public void pesquisar() {
		this.pedidos = new LazyDataModel<PedidoTransitoDTO>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<PedidoTransitoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
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
	
	public void abrirDialogEstrutura(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarEstrutura");
	}
	
	public void associarEstrutura(SelectEvent event) {
		Estrutura est = (Estrutura) event.getObject();
		if (est == null) return;
		this.pedido.setEstruturaDestino(est);
	}
	
	public void cancelarPedido(PedidoTransitoDTO pedido) {
		try {
			this.pedido = service.buscaPedidoPorId(pedido.getId());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void confirmarCancelamentoPedido() {
		try {
			service.cancelarPedidoTransferencia(this.pedido);	
			LoggerUtil.logInfo(Acao.CANCELAR, pedido, log);	
			RequestContext.getCurrentInstance().execute("PF('cancelarPedido').hide()");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao confirmar cancelamento do pedido (" + this.pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.CANCELAR, this.pedido, e, log);
		}
	}	
	
	/*
	 * Associar Foto
	 */
	
	public void abrirDialogFoto(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarFoto", 500 , 900);
	}
	
	public void associarFoto(SelectEvent event) {
		Object retorno = event.getObject();
		
		if (retorno == null) {
			return;
		}
		
		this.pedido.setFoto(new Foto());
		this.pedido.getFoto().setRecuperando(this.pedido.getRecuperando());
		this.pedido.getFoto().setTipo(TipoFoto.FRENTE);
		this.pedido.getFoto().setIsAtual(false);
		this.pedido.getFoto().setDataInclusao(Sigepen.getDataAtual());
		this.pedido.getFoto().setImagem((Imagem) retorno);
	}
	

	public FiltroPedTransito getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroPedTransito filtro) {
		this.filtro = filtro;
	}

	public PedidoTransito getPedido() {
		return pedido;
	}

	public void setPedido(PedidoTransito pedido) {
		this.pedido = pedido;
	}

	public LazyDataModel<PedidoTransitoDTO> getPedidos() {
		return pedidos;
	}

	public void setPedidos(LazyDataModel<PedidoTransitoDTO> pedidos) {
		this.pedidos = pedidos;
	}	
	
	public Date getDataMinima() {
		dataMinima = new Date();		
		return dataMinima;
	}
	
	public void imprimir() {
		try {			
			List<PedidoTransitoDTO> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			lista.stream().forEach(t -> {
				t.setFiltro(filtro);
			});
			
			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarReceberTransferencia));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Recebimento de Transferências", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de recebimento de transferências: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Recebimento de Transferências", e, log);
		}
	}
	
	public void imprimirRegistro(PedidoTransitoDTO pedidoTransferencia) {
		try {
			PedidoTransito pedido = service.buscaPedidoPorId(pedidoTransferencia.getId());

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(pedido, Relatorio.RegistroRecebimentoTransferencia));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, pedidoTransferencia, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro imprimir registro de recebimento de transferencia (" + pedidoTransferencia.getRecuperando() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, pedidoTransferencia, e, log);
		}
	}

}
