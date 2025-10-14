package br.gov.mt.sejudh.sigepen.bean;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRemanejamento;
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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPedTransito;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estabelecimento;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Recuperando;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.model.view.RecuperandoView;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarPedidoTransito", pattern = "/pedido-de-transito", viewId = "/WEB-INF/pages/movimentacao/ListarPedidoTransito.xhtml")
,@URLMapping(id = "CadastrarPedidoTransito", pattern = "/pedido-de-transito/cadastrar", viewId = "/WEB-INF/pages/movimentacao/CadastrarPedidoTransito.xhtml")
,@URLMapping(id = "AlterarPedidoTransito", pattern = "/pedido-de-transito/alterar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarPedidoTransito.xhtml")
,@URLMapping(id = "VisualizarPedidoTransito", pattern = "/pedido-de-transito/visualizar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarPedidoTransito.xhtml")
})
public class PedidoTransitoBean extends MetodosDePaginaBean {

	private FiltroPedTransito filtro;

	private PedidoTransito pedido;
	
	private Boolean autorizado;

	private LazyDataModel<PedidoTransitoDTO> pedidos;
	
	private List<UnidadePrisional> unidadesPrisionais;
	
	private Date dataMinima;

	@Inject
	private PedidoService service;
	
	@Inject
	private RecuperandoService recuperandoService;
	
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
		this.setFiltro(new FiltroPedTransito());
		this.getFiltro().setUnidadeOrigem(SecurityUtil.getUnidadeSelecionada());

		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			FiltroPedTransito filtroDash = (FiltroPedTransito) facesUtil
					.getFlashParameter("filtroPedTransitoDash");

			if (filtroDash != null) {
				setFiltro(filtroDash);

			}
		}

		super.carregarFiltroPaginacao(FiltroPedTransito.class, this, getFiltro());
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			this.unidadesPrisionais = unidadeService.listarUnidadesNaoInativas();
			
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.pedido = new PedidoTransito(StatusPedido.SOLICITADO, SecurityUtil.getUnidadeSelecionada());
					break;
					
				case VISUALIZAR:
				case ALTERAR:					
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarPedidoTransito(id)) {
						facesUtil.exibirMensagemErro("Pedido de trânsito não encontrado.", true);
						return "pretty:ListarPedidoTransito";
					}			
					
					if (!validarPedidoTransito()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarPedidoTransito";
					}
					
					validarBiometriaAtualizadaDoRecuperando();
					logDeAcesso(log, this.pedido);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Pedidos de Trânsito");
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

	private boolean validarPedidoTransito() {
		if (SecurityUtil.getUnidadeSelecionada().getId() != this.pedido.getUnidadeOrigem().getId()) {
			return false;
		}
		
		return true;
	}
	
	private Boolean verificarSeBiometriaEhObrigatoria() {
		return unidadeService.verificarSeBiometriaEhObrigatoria(SecurityUtil.getUnidadeSelecionada().getId());
	}
	
	private void validarBiometriaAtualizadaDoRecuperando() {
		if (verificarSeBiometriaEhObrigatoria()) {
			if (!this.recuperandoService.recuperandoPossuiBiometriaAtualizada(this.pedido.getRecuperando().getId())) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATUALIZAÇÃO CADASTRAL NECESSÁRIA", "A biometria deste recuperando está DESATUALIZADA.");
			}
		}
	}

	private boolean buscarPedidoTransito(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.pedido = service.buscaPedidoTransitoPorId(id);
		
		if (this.pedido == null) {
			return false;
		}
		
		return true;
	}

	public String salvar() {
		String retorno = null;
		
		try {
			service.validarPedido(this.pedido, MovimentacaoPassos.SALVAR);
			service.salvar(this.pedido);
			facesUtil.exibirMensagemInfo("Pedido " + this.pedido.getId() + " salvo com sucesso!", true);
			retorno = "pretty:ListarPedidoTransito";
			LoggerUtil.logInfo(Acao.SALVAR, this.pedido, log);			
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATENÇÃO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar pedido (" + this.pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.pedido, e, log);		
		}
		
		return retorno;
	}

	public void excluir(PedidoTransitoDTO pedido) {
		try {
			service.excluir(pedido);
			facesUtil.exibirMensagemInfo("Pedido" + pedido.getId() + " excluído com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, pedido, log);
		} 
		catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir Pedido (" + pedido.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, pedido, e, log);
		}
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
	
	public void imprimir() {
		try {
			List<PedidoTransitoDTO> lista = service.buscaPorFiltro(filtro, true);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarPedido));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Pedidos de Trânsito", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de pedidos: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Pedidos de Trânsito", e, log);
		}
	}
	
	public void imprimirRelatorio(PedidoTransitoDTO pedidoTransito) {
		facesUtil.setObjetoSessao("pedidoTransito", pedidoTransito);
		facesUtil.exibirPaginaDialog("pretty:ImprimirPedidoTransitoTipoDocumento", 160, 180);
	}
	
	public void abrirDialogRecuperando(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarRecuperando");
	}
	
	public void associarRecuperando(SelectEvent event) {
		RecuperandoView r = (RecuperandoView) event.getObject();
		
		try {
			if (r == null) {
				return;
			}
			
			Recuperando recuperando = recuperandoService.buscarRecuperandoSimplesPorId(r.getId());
			
			if (recuperando == null) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Recuperando não encontrado.");
				return;
			}
				
			this.pedido.setRecuperando(recuperando);
			this.pedido.setEstruturaOrigem(recuperandoService.lotacaoAtual(recuperando));
			
			validarBiometriaAtualizadaDoRecuperando();
		} catch (Exception e) {
			this.pedido.setRecuperando(null);
			this.pedido.setEstruturaOrigem(null);
			
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao associar recuperando ao pedido: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, r, e, log);
		}
	}
	
	public void abrirDialogEstrutura(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarEstrutura");
	}
	
	public void associarEstrutura(SelectEvent event) {
		Estrutura est = (Estrutura) event.getObject();
		if (est == null) return;
		
		try {
			this.pedido.setEstruturaDestino(est);
			service.validarPedido(pedido, MovimentacaoPassos.ASSOCIAR_ESTRUTURA);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATENÇÃO", e.getMessage());
		}
	}
	
	public void associarEstruturaFiltro(SelectEvent event) {
		Estrutura est = (Estrutura) event.getObject();
		if (est == null) return;
		this.filtro.setEstrutura(est);
	}
	
	public void abrirDialogEstabelecimento(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarEstabelecimento", null, 1000);
	}
	
	public void associarEstabelecimento(SelectEvent event) {
		Estabelecimento estabelecimento = (Estabelecimento) event.getObject();
		
		if(estabelecimento != null){
			this.pedido.setEstabelecimento(estabelecimento);
			this.pedido.setResponsavelDestino(null);
		}		
	}
	
	public void associarEstabelecimentoFiltro(SelectEvent event) {
		Estabelecimento estabelecimento = (Estabelecimento) event.getObject();
		
		if(estabelecimento != null){
			this.filtro.setEstabelecimento(estabelecimento);
		}		
	}
	
	public void autorizarPedido(PedidoTransitoDTO pedido) {
		try {
			this.pedido = service.buscaPedidoPorId(pedido.getId());
			service.validarPedido(this.pedido, MovimentacaoPassos.AUTORIZAR);
			RequestContext.getCurrentInstance().execute("PF('autorizarPedido').show()");
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATENÇÃO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao buscar pedido: " + e.getMessage());
			LoggerUtil.logErro(null, pedido, e, log);
		}
	}
	
	public void confirmarAutorizacaoPedido() {
		try {
			service.validarPedido(pedido, MovimentacaoPassos.AUTORIZAR);

			pedido.setAutorizador(SecurityUtil.getUsuario());
			service.autorizarPedido(pedido, autorizado);
			LoggerUtil.logInfo(Acao.AUTORIZAR, pedido, log);	
			this.autorizado = null;
			RequestContext.getCurrentInstance().execute("PF('autorizarPedido').hide()");
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ATENÇÃO", "Não foi possível autorizar o pedido: " + e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao autorizar pedido: " + e.getMessage());
			LoggerUtil.logErro(Acao.AUTORIZAR, pedido, e, log);
		}
	}
	
	public void cancelarPedido(PedidoTransitoDTO pedido) {	
		try {
			this.pedido = service.buscaPedidoPorId(pedido.getId());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao buscar pedido: " + e.getMessage());
			LoggerUtil.logErro(null, pedido, e, log);
		}
	}
	
	public void confirmarCancelamentoPedido() {		
		try {
			service.cancelarPedido(this.pedido);	
			LoggerUtil.logInfo(Acao.CANCELAR, pedido, log);
			RequestContext.getCurrentInstance().execute("PF('cancelarPedido').hide()");
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao cancelar pedido: " + e.getMessage());
			LoggerUtil.logErro(Acao.CANCELAR, pedido, e, log);
		}
	}
	
	public void alterarTipoTransito(){
		if(this.pedido.getTipoTransito() == TipoTransito.INTERNA){
			this.pedido.setUnidadeDestino(null);
			this.pedido.setEstabelecimento(null);
			this.pedido.setTipoMovimentacao(null);
			this.pedido.setResponsavelDestino(null);
		}else if(this.pedido.getTipoTransito() == TipoTransito.EXTERNA){
			this.pedido.setUnidadeDestino(null);
			this.pedido.setEstruturaDestino(null);
			this.pedido.setResponsavelDestino(null);
		}else if(this.pedido.getTipoTransito() == TipoTransito.TRANSFERENCIA){
			this.pedido.setEstabelecimento(null);
			this.pedido.setTipoMovimentacao(null);
			this.pedido.setEstruturaDestino(null);
		}
	}
	
	public void alterarTipoTransitoFiltro(){
		if(this.filtro.getTipoTransito() == TipoTransito.INTERNA){
			this.filtro.setUnidadeDestino(null);
			this.filtro.setEstabelecimento(null);
		}else if(this.filtro.getTipoTransito() == TipoTransito.EXTERNA){
			this.filtro.setUnidadeDestino(null);
			this.filtro.setEstrutura(null);
		}else if(this.filtro.getTipoTransito() == TipoTransito.TRANSFERENCIA){
			this.filtro.setEstabelecimento(null);
			this.filtro.setEstrutura(null);
		}else {
			this.filtro.setUnidadeDestino(null);
			this.filtro.setEstabelecimento(null);
			this.filtro.setEstrutura(null);
		}
	}
	
	public void getResponsavelDestino(){
		this.pedido.setUnidadeDestino(unidadeService.buscaPorId(this.pedido.getUnidadeDestino().getId()));
		if(this.pedido.getUnidadeDestino().getResponsavel() != null){
			this.pedido.setResponsavelDestino(this.pedido.getUnidadeDestino().getResponsavel().getNome());
		} else{
			this.pedido.setResponsavelDestino(null);
		}
	}
	
	public boolean transferenciaInterna(){
		return this.pedido.getTipoTransito() == TipoTransito.INTERNA;
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

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}

	public Boolean getAutorizado() {
		return autorizado;
	}

	public void setAutorizado(Boolean autorizado) {
		this.autorizado = autorizado;
	}
	
	public Date getDataMinima() {
		dataMinima = new Date();		
		return dataMinima;
	}
}
