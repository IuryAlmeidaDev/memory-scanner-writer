package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroCardapio;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.DiasSemana;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Apostilamento;
import br.gov.mt.sejudh.sigepen.model.Cardapio;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.ItemCardapio;
import br.gov.mt.sejudh.sigepen.model.TipoDieta;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.auxiliar.ObjetoSelectOne;
import br.gov.mt.sejudh.sigepen.model.dto.CardapioDTO;
import br.gov.mt.sejudh.sigepen.service.CardapioService;
import br.gov.mt.sejudh.sigepen.service.ContratoService;
import br.gov.mt.sejudh.sigepen.service.ItemCardapioService;
import br.gov.mt.sejudh.sigepen.service.TipoDietaService;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarCardapio", pattern = "/cardapio", viewId = "/WEB-INF/pages/cardapio/ListarCardapio.xhtml"),
		@URLMapping(id = "CadastrarCardapio", pattern = "/cardapio/cadastrar", viewId = "/WEB-INF/pages/cardapio/CadastrarCardapio.xhtml"),
		@URLMapping(id = "AlterarCardapio", pattern = "/cardapio/alterar/#{id}", viewId = "/WEB-INF/pages/cardapio/CadastrarCardapio.xhtml"),
		@URLMapping(id = "VisualizarCardapio", pattern = "/cardapio/visualizar/#{id}", viewId = "/WEB-INF/pages/cardapio/CadastrarCardapio.xhtml") })
public class CardapioBean extends MetodosDePaginaBean {
	
	private DiasSemana[] diasSemana;
	private List<ObjetoSelectOne> semanas;
	private List<TipoRefeicao> tiposRefeicao;
	private List<Contrato> contratos;
	private List<UnidadePrisional> unidadesPrisionais;
	private List<TipoDieta> tiposDieta;
	
	private Cardapio cardapio;
	private ItemCardapio itemCardapio;
	private ObjetoSelectOne semanaSelecionada;
	private FiltroCardapio filtro;
	private LazyDataModel<CardapioDTO> cardapios;
	
	private Apostilamento apostilamentoSelecionado;
	
	@Inject
	private CardapioService service;
	
	@Inject
	private TipoRefeicaoService tipoRefeicaoService;
	
	@Inject
	private TipoDietaService tipoDietaService;
	
	@Inject
	private ContratoService contratoService;
	
	@Inject 
	private ItemCardapioService itemCardapioService;
	
	@Inject
	private UnidadePrisionalService unidadeService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private SecurityUtil securityUtil;

	@Inject
	private Logger log;
	
	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		this.setFiltro(new FiltroCardapio());
		super.carregarFiltroPaginacao(FiltroCardapio.class, this, getFiltro());
	}
	
	@URLAction(onPostback = false)
	public String init() {
		try {	
			carregarListas();
			
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.cardapio = new Cardapio();
					break;
				case VISUALIZAR:
				case ALTERAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
	
					if (!buscarCardapio(id)) {
						facesUtil.exibirMensagemErro("Cardapio não encontrado.", true);
						return "pretty:ListarCardapio";
					}
					break;
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Cardapios");
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
	
	public String salvar() {
		String retorno = null;

		try {
			service.salvar(this.cardapio);
			facesUtil.exibirMensagemInfo("Cardápio de " + this.cardapio.getDiaSemana().getDescricao() + " salvo com Sucesso!", true);
			retorno = "pretty:ListarCardapio";
			LoggerUtil.logInfo(Acao.SALVAR, cardapio, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao salvar cardápio (" + this.cardapio.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, cardapio, e, log);
		}

		return retorno;
	}
	
	private boolean buscarCardapio(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.cardapio = service.buscarPorId(id);
		
		ObjetoSelectOne semana = new ObjetoSelectOne();
		semana.setId(this.cardapio.getSemana());
		this.setSemanaSelecionada(semana);

		return this.cardapio != null;
	}
	
	public void pesquisar() {
		this.setCardapios(new LazyDataModel<CardapioDTO>() {
			private static final long serialVersionUID = -6077221856355008621L;

			@Override
			public List<CardapioDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				getFiltro().setListarTodasUnidades(true);
				setRowCount(service.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return service.buscarDTOPorFiltro(getFiltro());
			}
		});
	}
	
	public void abrirDialogItem(ActionEvent event) {
		this.itemCardapio = null;
		facesUtil.exibirPaginaDialog("pretty:AssociarItemCardapio");
	}
	
	public void abrirDialogAlterarItem(ItemCardapio itemCardapio) {
		this.itemCardapio = itemCardapio;
		facesUtil.setObjetoSessao("itemCardapio", itemCardapio);
		facesUtil.exibirPaginaDialog("pretty:AssociarItemCardapio");
		LoggerUtil.logInfo(Acao.ALTERAR, itemCardapio, log);
	}
	
	public void incluirItemCardapio(SelectEvent event) {
		ItemCardapio itemCardapio = (ItemCardapio) event.getObject();

		try {
			if (itemCardapio != null) {
				if (itemCardapio.getMg_fem() < 0 || itemCardapio.getMg_masc() < 0 ) {
					facesUtil.exibirMensagemErro("Valor de gramatura de alimento não pode ser negativo", true);
					return;
				}

				if (this.itemCardapio != null) {
					cardapio.getItensCardapio().remove(this.itemCardapio);
				} else {
					itemCardapio.setCardapio(this.cardapio);
				}

				cardapio.getItensCardapio().add(itemCardapio);

				if (this.cardapio.getId() != null && this.cardapio.getId() > 0) {
					itemCardapioService.salvar(itemCardapio);
					LoggerUtil.logInfo(Acao.SALVAR, itemCardapio, log);
				} else {
					LoggerUtil.logInfo(Acao.ADICIONAR, itemCardapio, log);
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao incluir item ao cardápio: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, itemCardapio, e, log);
		}
	}

	public void excluirItemCardapio(ItemCardapio itemCardapio) {
		try {
			this.cardapio.getItensCardapio().remove(itemCardapio);

			if (this.cardapio.getId() > 0) {
				itemCardapioService.excluir(itemCardapio);
				LoggerUtil.logInfo(Acao.EXCLUIR, itemCardapio, log);
			} else {
				LoggerUtil.logInfo(Acao.REMOVER, itemCardapio, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao excluir item do Cardapio: " + e.getMessage());
			LoggerUtil.logErro(Acao.REMOVER, itemCardapio, e, log);
		}
	}
	
	private void carregarListas() {
		this.setUnidadesPrisionais(unidadeService.listarUnidadesNaoInativas());
		this.tiposRefeicao = tipoRefeicaoService.listarTodos();
		this.contratos = contratoService.listarAtivos();
		this.setTiposDieta(tipoDietaService.listarAtivas());
		this.setDiasSemana(this.listarDiasSemana());
		this.populaSemanas();
	}
	
	private void populaSemanas(){
		int total = 6;
		
		this.semanas = new ArrayList<ObjetoSelectOne>();
		
		for(int i = 1; i <= total; i++) {
			this.semanas.add(new ObjetoSelectOne(i, i + "º Semana"));
		}
	}
	
	public DiasSemana[] listarDiasSemana() {
		return DiasSemana.values(); 
	}

	public Cardapio getCardapio() {
		return cardapio;
	}

	public void setCardapio(Cardapio cardapio) {
		this.cardapio = cardapio;
	}

	public Apostilamento getApostilamentoSelecionado() {
		return apostilamentoSelecionado;
	}

	public void setApostilamentoSelecionado(Apostilamento apostilamentoSelecionado) {
		this.apostilamentoSelecionado = apostilamentoSelecionado;
	}
	
	public DiasSemana[] getDiasSemana() {
		return diasSemana;
	}

	public void setDiasSemana(DiasSemana[] diasSemana) {
		this.diasSemana = diasSemana;
	}

	public LazyDataModel<CardapioDTO> getCardapios() {
		return cardapios;
	}

	public void setCardapios(LazyDataModel<CardapioDTO> cardapios) {
		this.cardapios = cardapios;
	}

	public FiltroCardapio getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroCardapio filtro) {
		this.filtro = filtro;
	}

	public List<TipoRefeicao> getTiposRefeicao() {
		return tiposRefeicao;
	}

	public void setTiposRefeicao(List<TipoRefeicao> tiposRefeicao) {
		this.tiposRefeicao = tiposRefeicao;
	}

	public List<Contrato> getContratos() {
		return contratos;
	}

	public void setContratos(List<Contrato> contratos) {
		this.contratos = contratos;
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}

	public List<TipoDieta> getTiposDieta() {
		return tiposDieta;
	}

	public void setTiposDieta(List<TipoDieta> tiposDieta) {
		this.tiposDieta = tiposDieta;
	}

	public ItemCardapio getItemCardapio() {
		return itemCardapio;
	}

	public void setItemCardapio(ItemCardapio itemCardapio) {
		this.itemCardapio = itemCardapio;
	}

	public List<ObjetoSelectOne> getSemanas() {
		return semanas;
	}

	public void setSemanas(List<ObjetoSelectOne> semanas) {
		this.semanas = semanas;
	}
	
	public ObjetoSelectOne getSemanaSelecionada() {
		return semanaSelecionada;
	}

	public void setSemanaSelecionada(ObjetoSelectOne semanaSelecionada) {
		this.semanaSelecionada = semanaSelecionada;
		this.cardapio.setSemana(semanaSelecionada.getId());
	}
}
