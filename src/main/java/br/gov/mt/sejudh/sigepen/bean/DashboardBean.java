package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.component.panel.Panel;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroSolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.Dashboard;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.service.DashboardService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "dashboard", pattern = "/dashboard", viewId = "/WEB-INF/pages/dashboard/Dashboard.xhtml")
public class DashboardBean {
	
	private static final int NUM_COLUNAS_DASHBOARD = 3;
	
	private DashboardModel dashModel;
	private Long totalRecuperandosAtivos;
	private Long totalRecuperandosAtivosNaUnidade;
	private Long totalRecuperandosComCadastroIncompleto;
	private Long temporariosNaUnidadeAVencer;
	private Long temporariosNaUnidadeVencido;
	private Long temporariosNaUnidadeVenceHoje;
	private Long temporariosNoSistemaAVencer;
	private Long temporariosNoSistemaVencido;
	private Long temporariosNoSistemaVenceHoje;
	
	private List<Long> listaTemporariosNaUnidadeAVencer = new ArrayList<Long>();
	private List<Long> listaTemporariosNaUnidadeVencido = new ArrayList<Long>();
	private List<Long> listaTemporariosNaUnidadeVenceHoje = new ArrayList<Long>();
	private List<Long> listaTemporariosNoSistemaAVencer = new ArrayList<Long>();
	private List<Long> listaTemporariosNoSistemaVencido = new ArrayList<Long>();
	private List<Long> listaTemporariosNoSistemaVenceHoje = new ArrayList<Long>();
	
	private Long solicitacaoVisitaPendente;
	private Long totalCredenciamentoRecebimentoPendente;
	private Long totalCredenciamentoChecagemPendente;
	private Long totalCredenciamentoConferenciaPendente;
	private Long totalCredenciamentoAnalisePendente;
	private Long totalCredenciamentoAprovacaoPendente;
	
	@Inject
	private DashboardService service;
	
	@Inject
	private SecurityUtil securityUtil;
	
	@Inject
	private FacesUtil facesUtil;

	@PostConstruct
	public void init() {
		if(facesUtil.getObjetoSessao("permissao", false) != null) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Atenção", String.valueOf(facesUtil.getObjetoSessao("permissao")));
		}
		
		dashModel = new DefaultDashboardModel();
		
		if(SecurityUtil.getLoginUsuario().equals("ADMIN")){
			return;
		}
		
		for (int i=1; i<=NUM_COLUNAS_DASHBOARD;i++) {
			dashModel.addColumn(new DefaultDashboardColumn());
		}
		
		try {
			configurarDashboard();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void atribuirValores(Dashboard valor) {
		switch (valor) {
			case RECUPERANDOS_ATIVOS_SISTEMA:
				this.totalRecuperandosAtivos = service.totalRecuperandosAtivos();
				break;
				
			case RECUPERANDOS_ATIVOS_UNIDADE:
				this.totalRecuperandosAtivosNaUnidade = service.totalRecuperandosAtivosNaUnidade(SecurityUtil.getUnidadeSelecionada().getId());
				break;
				
			case RECUPERANDOS_ATIVOS_CADASTRO_INCOMPLETO_UNIDADE:
				this.totalRecuperandosComCadastroIncompleto = service.totalRecuperandosComCadastroIncompleto(SecurityUtil.getUnidadeSelecionada().getId());
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_A_VENCER:
				this.listaTemporariosNaUnidadeAVencer = service.recuperandosTemporariosNaUnidadeDataSolturaAVencer(SecurityUtil.getUnidadeSelecionada().getId());
				this.temporariosNaUnidadeAVencer = Long.parseLong(String.valueOf(this.listaTemporariosNaUnidadeAVencer.size()));
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_VENCEHOJE:
				this.listaTemporariosNaUnidadeVenceHoje = service.recuperandosTemporariosNaUnidadeDataSolturaVenceHoje(SecurityUtil.getUnidadeSelecionada().getId());				
				this.temporariosNaUnidadeVenceHoje = Long.parseLong(String.valueOf(this.listaTemporariosNaUnidadeVenceHoje.size()));
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_VENCIDA:
				this.listaTemporariosNaUnidadeVencido = service.recuperandosTemporariosNaUnidadeDataSolturaVencida(SecurityUtil.getUnidadeSelecionada().getId());
				this.temporariosNaUnidadeVencido = Long.parseLong(String.valueOf(this.listaTemporariosNaUnidadeVencido.size()));
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_A_VENCER:
				this.listaTemporariosNoSistemaAVencer = service.recuperandosTemporariosNoSistemaDataSolturaAVencer();
				this.temporariosNoSistemaAVencer = Long.parseLong(String.valueOf(this.listaTemporariosNoSistemaAVencer.size()));
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_VENCEHOJE:
				this.listaTemporariosNoSistemaVenceHoje = service.recuperandosTemporariosNoSistemaDataSolturaVenceHoje();
				this.temporariosNoSistemaVenceHoje = Long.parseLong(String.valueOf(this.listaTemporariosNoSistemaVenceHoje.size()));
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_VENCIDA:
				this.listaTemporariosNoSistemaVencido = service.recuperandosTemporariosNoSistemaDataSolturaVencida();
				this.temporariosNoSistemaVencido = Long.parseLong(String.valueOf(this.listaTemporariosNoSistemaVencido.size()));
				break;
				
			case SOLICITACAO_VISITA_PENDENTE:
				this.solicitacaoVisitaPendente = service.solicitacaoVisitaPendente(SecurityUtil.getUnidadeSelecionada().getId());
				break;
				
			case TOTAL_CREDENCIAMENTO_RECEBIMENTO_PENDENTE:
				this.totalCredenciamentoRecebimentoPendente = service.totalCredenciamentoRecebimentoPendente(SecurityUtil.getUnidadeSelecionada().getId());
				break;
			case TOTAL_CREDENCIAMENTO_CHECAGEM_PENDENTE:
				this.totalCredenciamentoChecagemPendente = service.totalCredenciamentoChecagemPendente(SecurityUtil.getUnidadeSelecionada().getId());
				break;
			case TOTAL_CREDENCIAMENTO_CONFERENCIA_PENDENTE:
				this.totalCredenciamentoConferenciaPendente = service.totalCredenciamentoConferenciaPendente(SecurityUtil.getUnidadeSelecionada().getId());
				break;
			case TOTAL_CREDENCIAMENTO_ANALISE_PENDENTE:
				this.totalCredenciamentoAnalisePendente = service.totalCredenciamentoAnalisePendente(SecurityUtil.getUnidadeSelecionada().getId());
				break;
			case TOTAL_CREDENCIAMENTO_APROVACAO_PENDENTE:
				this.totalCredenciamentoAprovacaoPendente = service.totalCredenciamentoAprovacaoPendente(SecurityUtil.getUnidadeSelecionada().getId());
				break;
				
			default:
				break;	
		}
	}

	private Date calculaDataLimiteMaiorDeIdade() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -18);

		return calendar.getTime();
	}
	
	private void configurarDashboard() {
		org.primefaces.component.dashboard.Dashboard das = (org.primefaces.component.dashboard.Dashboard) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:dash");
		List<Dashboard> lista = new ArrayList<Dashboard>();
		
		if (das != null) {
			for (UIComponent componente : das.getChildren()) {
				if (componente instanceof Panel) {
					Dashboard painel = Dashboard.getValue(componente.getId());
					lista.add(painel);
				}
			}
			
			int aux = 0;
			double result = (double)lista.size() / NUM_COLUNAS_DASHBOARD;
			double rowsceil = Math.ceil(result);
			
			for(int j=0; j<rowsceil; j++) {
				loopColuna:
				for (DashboardColumn coluna : dashModel.getColumns()) {
					for (int i=aux;i<lista.size();) {
						if(!securityUtil.possuiPermissao(lista.get(i).getDescricao())) {
							i++;
							aux = i;
							continue;
						}
						
						atribuirValores(lista.get(i));
						coluna.addWidget(lista.get(i).getDescricao());
						
						i++;
						aux = i;
						continue loopColuna;
					}
				}
			}
		}
	}

	public String executarAcao(Dashboard dash) {
		FiltroRecuperandoView filtro = new FiltroRecuperandoView();
		FiltroSolicitacaoVisita filtroSolicitacaoVisita = new FiltroSolicitacaoVisita();
		FiltroVwVisitante filtroVisitante = new FiltroVwVisitante();
		
		switch (dash) {
			case RECUPERANDOS_ATIVOS_SISTEMA:
				filtro.setStatus(StatusRecuperando.ATIVO);
				facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
				break;
				
			case RECUPERANDOS_ATIVOS_UNIDADE:
				filtro.setStatus(StatusRecuperando.ATIVO);
				filtro.setUnidade(SecurityUtil.getUnidadeSelecionada());
				facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
				break;
				
			case RECUPERANDOS_ATIVOS_CADASTRO_INCOMPLETO_UNIDADE:
				filtro.setStatus(StatusRecuperando.ATIVO);
				filtro.setUnidade(SecurityUtil.getUnidadeSelecionada());
				filtro.setIsCadastroCompleto(false);
				facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_A_VENCER:
				if (this.listaTemporariosNaUnidadeAVencer.size()==0)
					this.listaTemporariosNaUnidadeAVencer.add(-1L);
				
				filtro.setRecuperandoIds(this.listaTemporariosNaUnidadeAVencer);
				facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_VENCEHOJE:
				if (this.listaTemporariosNaUnidadeVenceHoje.size()==0)
					this.listaTemporariosNaUnidadeVenceHoje.add(-1L);
				
				filtro.setRecuperandoIds(this.listaTemporariosNaUnidadeVenceHoje);
				facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NA_UNIDADE_SOLTURA_VENCIDA:
				if (this.listaTemporariosNaUnidadeVencido.size()==0)
					this.listaTemporariosNaUnidadeVencido.add(-1L);
				
				filtro.setRecuperandoIds(this.listaTemporariosNaUnidadeVencido);
				facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_A_VENCER:
				if (this.listaTemporariosNoSistemaAVencer.size()==0)
					this.listaTemporariosNoSistemaAVencer.add(-1L);
				
				filtro.setRecuperandoIds(this.listaTemporariosNoSistemaAVencer);
				facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_VENCEHOJE:
				if (this.listaTemporariosNoSistemaVenceHoje.size()==0)
					this.listaTemporariosNoSistemaVenceHoje.add(-1L);
				
				filtro.setRecuperandoIds(this.listaTemporariosNoSistemaVenceHoje);
				facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
				break;
				
			case RECUPERANDOS_TEMPORARIOS_NO_SISTEMA_SOLTURA_VENCIDA:
				if (this.listaTemporariosNoSistemaVencido.size()==0)
					this.listaTemporariosNoSistemaVencido.add(-1L);
				
				filtro.setRecuperandoIds(this.listaTemporariosNoSistemaVencido);
				facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
				break;

			case SOLICITACAO_VISITA_PENDENTE:
				filtroSolicitacaoVisita.setStatus(SgpStatusAlternativo.P);
				filtroSolicitacaoVisita.setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());
				filtroSolicitacaoVisita.setDataNascimento(calculaDataLimiteMaiorDeIdade());
				facesUtil.setFlashParameter("filtroSolicitacaoVisitaDash", filtroSolicitacaoVisita);
				return "pretty:ListarSolicitacaoVisita";
				
			case TOTAL_CREDENCIAMENTO_RECEBIMENTO_PENDENTE:
				filtroVisitante.setFaseCredenciamento(FaseCredenciamento.RECEBIMENTO);
				filtroVisitante.setSituacaoCredenciamento(StatusCredenciamento.PENDENTE);
				filtroVisitante.setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());
				facesUtil.setFlashParameter("filtroVisitanteDash", filtroVisitante);
				return "pretty:ListarVisitante";
				
			case TOTAL_CREDENCIAMENTO_CHECAGEM_PENDENTE:
				filtroVisitante.setFaseCredenciamento(FaseCredenciamento.CHECAGEM);
				filtroVisitante.setSituacaoCredenciamento(StatusCredenciamento.PENDENTE);
				filtroVisitante.setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());
				facesUtil.setFlashParameter("filtroVisitanteDash", filtroVisitante);
				return "pretty:ListarVisitante";
				
			case TOTAL_CREDENCIAMENTO_CONFERENCIA_PENDENTE:
				filtroVisitante.setFaseCredenciamento(FaseCredenciamento.CONFERENCIA);
				filtroVisitante.setSituacaoCredenciamento(StatusCredenciamento.PENDENTE);
				filtroVisitante.setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());
				facesUtil.setFlashParameter("filtroVisitanteDash", filtroVisitante);
				return "pretty:ListarVisitante";
				
			case TOTAL_CREDENCIAMENTO_ANALISE_PENDENTE:
				filtroVisitante.setFaseCredenciamento(FaseCredenciamento.ANALISE);
				filtroVisitante.setSituacaoCredenciamento(StatusCredenciamento.PENDENTE);
				filtroVisitante.setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());
				facesUtil.setFlashParameter("filtroVisitanteDash", filtroVisitante);
				return "pretty:ListarVisitante";
				
			case TOTAL_CREDENCIAMENTO_APROVACAO_PENDENTE:
				filtroVisitante.setFaseCredenciamento(FaseCredenciamento.APROVACAO);
				filtroVisitante.setSituacaoCredenciamento(StatusCredenciamento.PENDENTE);
				filtroVisitante.setUnidadePrisional(SecurityUtil.getUnidadeSelecionada());
				facesUtil.setFlashParameter("filtroVisitanteDash", filtroVisitante);
				return "pretty:ListarVisitante";
				
		}
		
		facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
		return "pretty:ListarRecuperando";
	}
	
	public DashboardModel getDashModel() {
		return dashModel;
	}

	public Long getTotalRecuperandosAtivos() {
		return totalRecuperandosAtivos;
	}

	public Long getTotalRecuperandosAtivosNaUnidade() {
		return totalRecuperandosAtivosNaUnidade;
	}
	
	public Long getTotalRecuperandosComCadastroIncompleto() {
		return totalRecuperandosComCadastroIncompleto;
	}

	public Long getTemporariosNaUnidadeAVencer() {
		return temporariosNaUnidadeAVencer;
	}

	public Long getTemporariosNaUnidadeVencido() {
		return temporariosNaUnidadeVencido;
	}

	public Long getTemporariosNaUnidadeVenceHoje() {
		return temporariosNaUnidadeVenceHoje;
	}

	public Long getTemporariosNoSistemaAVencer() {
		return temporariosNoSistemaAVencer;
	}

	public Long getTemporariosNoSistemaVencido() {
		return temporariosNoSistemaVencido;
	}

	public Long getTemporariosNoSistemaVenceHoje() {
		return temporariosNoSistemaVenceHoje;
	}
	
	public Long getSolicitacaoVisitaPendente() {
		return solicitacaoVisitaPendente;
	}

	public Long getTotalCredenciamentoRecebimentoPendente() {
		return totalCredenciamentoRecebimentoPendente;
	}

	public Long getTotalCredenciamentoChecagemPendente() {
		return totalCredenciamentoChecagemPendente;
	}

	public Long getTotalCredenciamentoConferenciaPendente() {
		return totalCredenciamentoConferenciaPendente;
	}

	public Long getTotalCredenciamentoAnalisePendente() {
		return totalCredenciamentoAnalisePendente;
	}

	public Long getTotalCredenciamentoAprovacaoPendente() {
		return totalCredenciamentoAprovacaoPendente;
	}
	
	
}
