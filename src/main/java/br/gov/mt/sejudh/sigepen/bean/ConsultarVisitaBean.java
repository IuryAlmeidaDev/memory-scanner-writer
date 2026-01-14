package br.gov.mt.sejudh.sigepen.bean;

import java.io.File;
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

import br.gov.mt.sejudh.sigepen.model.Visitante;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroEntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoVisita;
import br.gov.mt.sejudh.sigepen.enumerator.TipoEntrada;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.EntradaSaidaRecuperando;
import br.gov.mt.sejudh.sigepen.model.TipoVisita;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.EntradaSaidaDTO;
import br.gov.mt.sejudh.sigepen.relatorio.Relatorio;
import br.gov.mt.sejudh.sigepen.relatorio.RelatorioUtil;
import br.gov.mt.sejudh.sigepen.service.EntradaSaidaRecuperandoService;
import br.gov.mt.sejudh.sigepen.service.EntradaSaidaService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.service.VisitanteService;
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
	
	private LazyDataModel<EntradaSaidaDTO> entradas;
	
	@Inject
	private EntradaSaidaRecuperandoService service;

	@Inject
    private SecurityUtil securityUtil;
	
	@Inject
	private EntradaSaidaService esService;
	
	@Inject
	private VisitanteService visitanteService;
	
	private EntradaSaidaDTO entrada;
	
	private boolean mostrarPanelVisitante = false;
	
	private boolean mostrarPanelResponsavel = false;
	
	private String loginUsuarioAutorizador;
	private String senhaUsuarioAutorizador;
	private String observacaoUsuarioAutorizador;
	
	@Inject
	private UnidadePrisionalService unidadeService;
	
	private List<UnidadePrisional> unidadesPrisionais;
	
	private String sortField;
	
	private SortOrder sortOrder;

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
		this.entradas = new LazyDataModel<EntradaSaidaDTO>() {
			
			private static final long serialVersionUID = 1L;
			
			
			@Override
			public Object getRowKey(EntradaSaidaDTO object) {
				if(object != null && object.getId() != null) {
					return object.getId();
				}
				return null;
			}

			@Override
			public EntradaSaidaDTO getRowData(String rowKey) {
				if (rowKey == null || rowKey.isEmpty()) {
					return null;
				}

				for (EntradaSaidaDTO rec : entradas) {
					if (rec.getId().equals(Long.valueOf(rowKey))) {
						return rec;
					}
				}

                return null;
			}
			
			@Override
			public List<EntradaSaidaDTO> load(int first, int pageSize,
					String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
			
				
				ConsultarVisitaBean.this.sortField = sortField;
				ConsultarVisitaBean.this.sortOrder = sortOrder;
	
//				setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));
				setRowCount(service.buscarEntradasPorFiltroCount(getFiltro(), sortField,sortOrder, first));
				
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				List<EntradaSaidaDTO> lista = service.buscarEntradasPorFiltro(getFiltro(), sortField,sortOrder, first);
				
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

	public LazyDataModel<EntradaSaidaDTO> getEntradas() {
		return entradas;
	}

	public void setEntradas(LazyDataModel<EntradaSaidaDTO> entradas) {
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
	
	public StreamedContent getImpressaoPdf() {
		if (!securityUtil.possuiPermissao("ImprimirListaVisitas")) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", "Para realizar a impressão é necessária a permissão!");
			return null;
		}
		try {
			
			List<EntradaSaidaDTO> lista = service.buscarEntradasPorFiltro(getFiltro(), sortField, sortOrder);

			if (lista.size() == 0 || lista.isEmpty()) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_INFO, "AVISO", "Relatório não impresso, nenhum registro encontrado!");
				return null;
			}
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			if (StringUtils.isNotBlank(this.filtro.getFiltro())) {
				parameters.put("FILTRO", "Campos Utilizados: " + this.filtro.getFiltro());
			}

			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String cabecalhoFiltro = ctx.getRealPath("WEB-INF" + File.separator + "relatorios" + File.separator + "cabecalhos" + File.separator + "Filtro.jasper");
			parameters.put("CABECALHO_FILTRO", cabecalhoFiltro);

			LoggerUtil.logInfo(Acao.IMPRIMIR, "Consultar visitas", log);
			
			return RelatorioUtil.downloadPdf(lista, Relatorio.ListarEntradaSaida, parameters);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro imprimir lista de visitas: " + e.getMessage());
			LoggerUtil.logErro(Acao.IMPRIMIR, "Consultar visitas", e, log);
		}
		
		return null;
	}

	public String buscarSituacao(EntradaSaidaRecuperando e) {
		Date di = Sigepen.getDataConsultaMaiorIgual(new Date());
		return e.getEntrada().getDataSaida() != null || e.getEntrada().getDataEntrada().before(di) ? "Finalizada" : "Em Andamento";
	}

	public void abrirFinalizarVisita(EntradaSaidaDTO e) {
		this.entrada = e;
	}
	
	public void abrirVisualizarVisita(EntradaSaidaDTO e) {
		this.entrada = e;
		this.entrada.setVisitante(e.getVisitante());
		this.entrada.setAutorizacoes(visitanteService.buscarAutorizacoesEntradasDTO(e.getId()));
		this.entrada.getVisitante().setImagemAtual(visitanteService.buscarImagemFrente(e.getVisitante().getId()));


//		Visitante entradaResponsavel = esService.responsavelDeuEntrada(this.entrada.getVisitante().getId());
		if(e.getResponsavel() != null){
			this.entrada.getVisitante().setResponsavel(e.getResponsavel());
			this.entrada.getVisitante().getResponsavel().setImagemAtual(visitanteService.buscarImagemFrente(e.getVisitante().getResponsavel().getId()));
			
			this.mostrarPanelVisitante = false;
			this.mostrarPanelResponsavel = true;
			return;
		}
	   
	    this.mostrarPanelVisitante = true;
		
	}
	
		
	public void finalizarVisita() {
		try {
			if (this.entrada.getMotivoFinalizacao().isEmpty()) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Preenchimento do motivo é obrigatório");
				return;
			}		
			LoggerUtil.logInfo(Acao.FINALIZAR, this.entrada, log);
			this.entrada.setDataSaida(new Date());
			this.entrada.setSituacao(SituacaoVisita.FINALIZADA);
			esService.salvar(this.entrada);
			RequestContext.getCurrentInstance().execute("PF('dialogFinalizarVisita').hide()");
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao finalizar visita: " + e.getMessage());
			LoggerUtil.logErro(Acao.FINALIZAR, "Finalizar visita", e, log);
		}
	}
	
	public Boolean isMenor(Date nasc){
		if(nasc == null)
			return false;
		Calendar past = Calendar.getInstance();
        past.add(Calendar.YEAR, -18);
        return nasc.after(past.getTime());
	}
	
	
	public EntradaSaidaDTO getEntrada() {
		return entrada;
	}

	public void setEntrada(EntradaSaidaDTO entrada) {
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

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	
}

