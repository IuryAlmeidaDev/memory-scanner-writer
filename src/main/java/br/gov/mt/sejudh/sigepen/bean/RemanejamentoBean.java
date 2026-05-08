package br.gov.mt.sejudh.sigepen.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;
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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRemanejamento;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Estrutura;
import br.gov.mt.sejudh.sigepen.model.Foto;
import br.gov.mt.sejudh.sigepen.model.Imagem;
import br.gov.mt.sejudh.sigepen.model.PedidoTransito;
import br.gov.mt.sejudh.sigepen.model.Remanejamento;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.PedidoTransitoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.RemanejamentoDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.service.PedidoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.service.RemanejamentoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarRemanejamento", pattern = "/remanejamento", viewId = "/WEB-INF/pages/movimentacao/ListarRemanejamento.xhtml")
,@URLMapping(id = "CadastrarRemanejamento", pattern = "/remanejamento/cadastrar", viewId = "/WEB-INF/pages/movimentacao/CadastrarRemanejamento.xhtml")
,@URLMapping(id = "VisualizarRemanejamento", pattern = "/remanejamento/visualizar/#{id}", viewId = "/WEB-INF/pages/movimentacao/CadastrarRemanejamento.xhtml")
})
public class RemanejamentoBean extends MetodosDePaginaBean {

	private FiltroRemanejamento filtro; 

	private Remanejamento remanejamento; 
	
	private LazyDataModel<RemanejamentoDTO> remanejamentos; 
	
	private Date dataMinima;
	
	private Date dataMaxima;
	
	private List<UnidadePrisional> unidadesPrisionais;
	
	@Inject
	private ComboService comboService;
	
	@Inject
	private RemanejamentoService service; 

	@Inject
	private PedidoService pedService;
	
	@Inject
	private FileDownloadHandlerBean fileDownload;
	
	@Inject
	private RecuperandoService recService;
		
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {

		this.setFiltro(new FiltroRemanejamento());

		this.getFiltro().setUnidadeRemanejada(SecurityUtil.getUnidadeSelecionada());

		if (getAcaoDeTela().equals(AcaoDeTela.LISTAR)) {
			FiltroRemanejamento filtroDash = (FiltroRemanejamento) facesUtil
					.getFlashParameter("filtroRemanejamentoDash");

			if (filtroDash != null) {
				setFiltro(filtroDash);
				pesquisar();
			}
		}

		super.carregarFiltroPaginacao(FiltroRemanejamento.class, this, getFiltro());
	}
	
	@URLAction(onPostback=false)
	public String init() { 
		try {
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.remanejamento = new Remanejamento(StatusMovimentacao.EM_ANDAMENTO, SecurityUtil.getUnidadeSelecionada());
					break;
					
				case VISUALIZAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarRemanejamento(id)) {
						facesUtil.exibirMensagemErro("Remanejamento não encontrado.", true);
						return "pretty:ListarRemanejamento";
					}
					
					if (!validarRemanejamento()) {
						facesUtil.exibirMensagemErro("Operação não permitida.", true);
						return "pretty:ListarRemanejamento";
					}
					
					logDeAcesso(log, this.remanejamento);
					break;
			
				case LISTAR:
					unidadesPrisionais = comboService.getUnidadesPrisionais();
					pesquisar();
					logDeAcesso(log, "Remanejamentos");
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

	private boolean validarRemanejamento() {
		if (this.remanejamento.getUnidadeRemanejada().getId() != SecurityUtil.getUnidadeSelecionada().getId()) {
			return false;
		}
		
		return true;
	}

	private boolean buscarRemanejamento(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.remanejamento = service.buscaRemanejamentoPorId(id);
		
		if (this.remanejamento == null) {
			return false;
		}
		
		return true;
	}

	public void abrirDialogPedido(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarPedidoRemanejamento");
	}
	
	public void associarPedido(SelectEvent event) {
		PedidoTransitoDTO p = (PedidoTransitoDTO) event.getObject();	
		
		if (p == null) {
			return;
		}
		
		try {
			PedidoTransito pedido = pedService.buscaPedidoRemanejamentoPorId(p.getId());
		
			if (pedido.getRecuperando().getStatus() != StatusRecuperando.ATIVO) {
				throw new ValidacaoException("Recuperando inativo, remova o pedido!");
			}					
		
		   this.remanejamento.setPedido(pedido);
		   this.remanejamento.setEstruturaOrigem(recService.lotacaoAtual(pedido.getRecuperando()));
					
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao adicionar Pedido (" + p.getId() + "): " + e.getMessage());			
			LoggerUtil.logErro(Acao.ASSOCIAR, p, e, log);			
		}		
	}
	
	public void abrirDialogEstrutura(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarEstrutura");
	}
	
	public void associarEstrutura(SelectEvent event) {
		Estrutura est = (Estrutura) event.getObject();
		if (est == null) return;
		this.remanejamento.setEstruturaDestino(est);
	}
	
	public void abrirDialogFoto(ActionEvent event) {
		facesUtil.exibirPaginaDialog("pretty:AssociarFoto", 500 , 900);
	}
	
	public void associarFoto(SelectEvent event) {
		Object retorno = event.getObject();
		
		if (retorno == null) {
			return;
		}	
		
		this.remanejamento.setFoto(new Foto());		
		this.remanejamento.getFoto().setTipo(TipoFoto.FRENTE);
		this.remanejamento.getFoto().setIsAtual(false);
		this.remanejamento.getFoto().setDataInclusao(Sigepen.getDataAtual());
		this.remanejamento.getFoto().setImagem((Imagem) retorno);
	}

	public String salvar() {
		String retorno = null;
		
		try {
			
			if (this.remanejamento.getDataInicio().after(this.getDataMaxima())) {
				throw new ValidacaoException("Data de entrada não pode ser maior que a atual");
			}
			
			if (this.remanejamento.getPedido() == null) {
				throw new ValidacaoException("É necessário adicionar um recuperando!");
			}
			
			if (this.remanejamento.getEstruturaDestino() == null) {
				throw new ValidacaoException("É necessário adicionar uma estrutura!");
			}
			
			if (this.remanejamento.getFoto() == null) {
				throw new ValidacaoException("É necessário adicionar uma foto!");
			}else if (this.remanejamento.getFoto().getImagem() == null){
				throw new ValidacaoException("É necessário adicionar uma foto!");
			}
			
			this.remanejamento.getFoto().setRecuperando(this.remanejamento.getPedido().getRecuperando());
			recService.salvarFoto(this.remanejamento.getFoto());
						
			service.salvar(this.remanejamento);
			facesUtil.exibirMensagemInfo("Remanejamento " + this.remanejamento.getId() + " iniciado com sucesso!", true);
			retorno = "pretty:ListarRemanejamento";
			LoggerUtil.logInfo(Acao.INICIAR, this.remanejamento, log);			
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao iniciar remanejamento (" + this.remanejamento.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.INICIAR, this.remanejamento, e, log);			
		}
		
		return retorno;
	}
		
	public void pesquisar() { 
		this.remanejamentos = new LazyDataModel<RemanejamentoDTO>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public List<RemanejamentoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					LoggerUtil.logErro(e, log);
				}

				return service.buscaPorFiltro(getFiltro());
			}
		};
	}		
	
	public void finalizarRemanejamento(RemanejamentoDTO remanejamwento) {
		try {
			this.remanejamento = service.buscaRemanejamentoPorId(remanejamwento.getId());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void confirmarFinalizacaoRemanejamento() {
		try {			
			service.finalizarRemanejamento(this.remanejamento);	
			RequestContext.getCurrentInstance().execute("PF('finalizarRemanejamento').hide()");
			facesUtil.exibirMensagemInfo("Remanejamento " + this.remanejamento.getId() + " finalizado com sucesso!", false);
			LoggerUtil.logInfo(Acao.FINALIZAR, this.remanejamento, log);			
		} catch(Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao finalizar remanejamento (" + this.remanejamento.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.FINALIZAR, this.remanejamento, e, log);		
		}
	}

	public FiltroRemanejamento getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroRemanejamento filtro) {
		this.filtro = filtro;
	}
	
	public Remanejamento getRemanejamento() {
		return remanejamento;
	}

	public void setRemanejamento(Remanejamento remanejamento) {
		this.remanejamento = remanejamento;
	}

	public LazyDataModel<RemanejamentoDTO> getRemanejamentos() {
		return remanejamentos;
	}

	public void setRemanejamentos(LazyDataModel<RemanejamentoDTO> remanejamentos) {
		this.remanejamentos = remanejamentos;
	}

	public Date getDataMinima() {
		dataMinima = new Date();		
		return dataMinima;
	}
	
	public Date getDataMaxima() {
		dataMaxima = new Date();		
		return dataMaxima;
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}
	
	
	public void imprimir() {
		try {			
			List<RemanejamentoDTO> lista = service.buscaPorFiltro(filtro);
			
			if (lista.size() == 0) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return;
			}
			
			lista.stream().forEach(t -> {
				t.setFiltro(filtro);
			});

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(lista, Relatorio.ListarRemanejamento));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, "Lista de Remanejamento", log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de Remanejamento: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Lista de Remanejamento", e, log);
		}
	}
	
		
	
	public void imprimirRegistro(RemanejamentoDTO remanejamento) {
		try {
		

			FiltroRemanejamento f = new FiltroRemanejamento();
			f.setId(remanejamento.getId());

			List<RemanejamentoDTO> recs = service.buscaPorFiltro(f);

			fileDownload.prepararArquivo(RelatorioUtil.gerarRelatorio(recs, Relatorio.RegistroRemanejamento));
			RequestContext.getCurrentInstance().execute("download()");
			LoggerUtil.logInfo(Acao.IMPRIMIR, remanejamento, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro imprimir registro de remanejamento (" + remanejamento.getRecuperando() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, remanejamento, e, log);
		}
	}

}
