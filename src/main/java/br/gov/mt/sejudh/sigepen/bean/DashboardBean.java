package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.gov.mt.sejudh.sigepen.dao.RecuperandoDAO;
import br.gov.mt.sejudh.sigepen.dao.filter.*;
import br.gov.mt.sejudh.sigepen.enumerator.*;
import br.gov.mt.sejudh.sigepen.service.*;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroRecuperandoView;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroSolicitacaoVisita;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroVwVisitante;
import br.gov.mt.sejudh.sigepen.enumerator.Dashboard;
import br.gov.mt.sejudh.sigepen.enumerator.FaseCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.enumerator.StatusCredenciamento;
import br.gov.mt.sejudh.sigepen.enumerator.StatusRecuperando;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "dashboard", pattern = "/dashboard", viewId = "/WEB-INF/pages/dashboard/Dashboard.xhtml")
public class DashboardBean {

	private static final Logger log = LogManager.getLogger(DashboardBean.class);

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
	
	//private List<Long> listaProvisorios90a180Dias = new ArrayList<>();
	//private List<Long> listaProvisoriosMais180Dias = new ArrayList<>();

	
	private Long solicitacaoVisitaPendente;
	private Long totalCredenciamentoRecebimentoPendente;
	private Long totalCredenciamentoChecagemPendente;
	private Long totalCredenciamentoConferenciaPendente;
	private Long totalCredenciamentoAnalisePendente;
	private Long totalCredenciamentoAprovacaoPendente;

	private Integer totalMovimentacaoInternaPendente;
	private Integer totalMovimentacaoInternaEmAndamento;
	private Integer totalMovimentacaoInternaAguardandoFinalizar;
	private Integer totalMovimentacaoInternaFinalizadoParcial;

	private Integer totalMovimentacaoExternaPendente;
	private Integer totalMovimentacaoExternaEmAndamento;
	private Integer totalMovimentacaoExternaAguardandoFinalizar;
	private Integer totalMovimentacaoExternaFinalizadoParcial;

	private Integer totalTransferenciaPendente;
	private Integer totalTransferenciaEmAndamento;
	private Integer totalTransferenciaFinalizadoParcial;

	private Integer totalReceberTransferenciaEmAndamento;
	private Integer totalReceberTransferenciaViculada;

	private Integer totalRemanejamentoEmAndamento;
	
	private Long totalProvisorios90a180Dias;
	private Long totalProvisoriosMais180Dias;

	
	@Inject
	private DashboardService service;
	
	@Inject
	private SecurityUtil securityUtil;
	
	@Inject
	private FacesUtil facesUtil;

	@Inject
	private MovimentacaoInternaService movimentacaoInternaService;

	@Inject
	private MovimentacaoExternaService movimentacaoExternaService;

	@Inject
	private TransferenciaService transferenciaService;

	@Inject
	private PedidoService pedidoService;

	@Inject
	private RemanejamentoService remanejamentoService;

	@Inject
	private UsuarioService usuarioService;
	
	@Inject
	private RecuperandoDAO recuperandoDAO;


	@PostConstruct
	public void init() {
		if(facesUtil.getObjetoSessao("permissao", false) != null) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Atenção", String.valueOf(facesUtil.getObjetoSessao("permissao")));
		}

		if(SecurityUtil.getLoginUsuario().equals("ADMIN")){
			return;
		}

		for (Dashboard d : Dashboard.values()) {
			if (!securityUtil.possuiPermissao(d.getDescricao())) {
				continue;
			}
			try {
				atribuirValores(d);
			} catch (Exception e) {
				LoggerUtil.logErro(e, log);
			}
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
				
			case RECUPERANDOS_PROVISORIOS_90_180_DIAS:
			    this.totalProvisorios90a180Dias = service.countRecuperandosProvisorios90a180Dias(
			        SecurityUtil.getUnidadeSelecionada().getId()
			    );
			    break;

			case RECUPERANDOS_PROVISORIOS_MAIS_180_DIAS:
			    this.totalProvisoriosMais180Dias = service.countRecuperandosProvisoriosMais180Dias(
			        SecurityUtil.getUnidadeSelecionada().getId()
			    );
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

			case TOTAL_MOVIMENTACAO_INTERNA_PENDENTE:
				this.totalMovimentacaoInternaPendente = getMovimentacaoInternaPendente();
				break;

			case TOTAL_MOVIMENTACAO_INTERNA_EM_ANDAMENTO:
				this.totalMovimentacaoInternaEmAndamento = getMovimentacaoInternaEmAndamento();
				break;
			case TOTAL_MOVIMENTACAO_INTERNA_AGUARDANDO_FINALIZAR:
				this.totalMovimentacaoInternaAguardandoFinalizar = getMovimentacaoInternaAguardandoFinalizar();
				break;

			case TOTAL_MOVIMENTACAO_INTERNA_FINALIZADO_PARCIAL:
				this.totalMovimentacaoInternaFinalizadoParcial = getMovimentacaoInternaFinalizadoParcial();
				break;

			case TOTAL_MOVIMENTACAO_EXTERNA_PENDENTE:
				this.totalMovimentacaoExternaPendente = getMovimentacaoExternaPendente();
				break;

			case TOTAL_MOVIMENTACAO_EXTERNA_EM_ANDAMENTO:
				this.totalMovimentacaoExternaEmAndamento = getMovimentacaoExternaEmAndamento();
				break;

			case TOTAL_MOVIMENTACAO_EXTERNA_FINALIZADO_PARCIAL:
				this.totalMovimentacaoExternaFinalizadoParcial = getMovimentacaoExternaFinalizadoParcial();
				break;

			case TOTAL_MOVIMENTACAO_EXTERNA_AGUARDANDO_FINALIZAR:
				this.totalMovimentacaoExternaAguardandoFinalizar = getMovimentacaoExternaAguardandoFinalizar();
				break;

			case TOTAL_TRANSFERENCIA_PENDENTE:
				this.totalTransferenciaPendente = getTransferenciaPendente();
				break;

			case TOTAL_TRANSFERENCIA_EM_ANDAMENTO:
				this.totalTransferenciaEmAndamento = getTransferenciaEmAndamento();
				break;

			case TOTAL_TRANSFERENCIA_FINALIZADO_PARCIAL:
				this.totalTransferenciaFinalizadoParcial = getTransferenciaFinalizadoParcial();
				break;

			case TOTAL_RECEBER_TRANSFERENCIA_VINCULADO:
				this.totalReceberTransferenciaViculada = getRecebimentoTransferenciaVinculado();
				break;

			case TOTAL_RECEBER_TRANSFERENCIA_EM_ANDAMENTO:
				this.totalReceberTransferenciaEmAndamento = getRecebimentoTransferenciaEmAndamento();
				break;

			case TOTAL_REMANEJAMENTO_EM_ANDAMENTO:
				this.totalRemanejamentoEmAndamento = getRemanejamentoEmAndamento();
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

	public String executarAcao(Dashboard dash) {
		FiltroRecuperandoView filtro = new FiltroRecuperandoView();
		FiltroSolicitacaoVisita filtroSolicitacaoVisita = new FiltroSolicitacaoVisita();
		FiltroVwVisitante filtroVisitante = new FiltroVwVisitante();
		FiltroRemanejamento filtroRemanejamento = new FiltroRemanejamento();
		FiltroPedTransito filtroPedTransito = new FiltroPedTransito();
		FiltroMovimentacaoExterna filtroMovimentacaoExterna = new FiltroMovimentacaoExterna();
		FiltroMovimentacaoInterna filtroMovimentacaoInterna = new FiltroMovimentacaoInterna();
		FiltroTransferencia filtroTransferencia = new FiltroTransferencia();
		
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
				
			case RECUPERANDOS_PROVISORIOS_90_180_DIAS:
			    filtro.setStatus(StatusRecuperando.ATIVO);
			    filtro.setCondicaoAtual(Condicao.PROVISORIO);
			    filtro.setModalidade(Modalidade.PREVENTIVA);
			    filtro.setUnidade(SecurityUtil.getUnidadeSelecionada());
			    filtro.setDiasProvisorioMin(90);
			    filtro.setDiasProvisorioMax(180);
			    facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
			    break;

			case RECUPERANDOS_PROVISORIOS_MAIS_180_DIAS:
			    filtro.setStatus(StatusRecuperando.ATIVO);
			    filtro.setCondicaoAtual(Condicao.PROVISORIO);
			    filtro.setModalidade(Modalidade.PREVENTIVA);
			    filtro.setUnidade(SecurityUtil.getUnidadeSelecionada());
			    filtro.setDiasProvisorioMin(181);
			    filtro.setDiasProvisorioMax(null);
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

			case TOTAL_MOVIMENTACAO_INTERNA_PENDENTE:
				filtroMovimentacaoInterna.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroMovimentacaoInterna.setStatus(StatusMovimentacao.PENDENTE);
				facesUtil.setFlashParameter("filtroMovimentacaoInternaDash", filtroMovimentacaoInterna);
				return "pretty:ListarMovimentacaoInterna";

			case TOTAL_MOVIMENTACAO_INTERNA_EM_ANDAMENTO:
				filtroMovimentacaoInterna.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroMovimentacaoInterna.setStatus(StatusMovimentacao.EM_ANDAMENTO);
				facesUtil.setFlashParameter("filtroMovimentacaoInternaDash", filtroMovimentacaoInterna);
				return "pretty:ListarMovimentacaoInterna";

			case TOTAL_MOVIMENTACAO_INTERNA_AGUARDANDO_FINALIZAR:
				filtroMovimentacaoInterna.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroMovimentacaoInterna.setStatus(StatusMovimentacao.AGUARDANDO_FINALIZAR);
				facesUtil.setFlashParameter("filtroMovimentacaoInternaDash", filtroMovimentacaoInterna);
				return "pretty:ListarMovimentacaoInterna";
				
			case TOTAL_MOVIMENTACAO_INTERNA_FINALIZADO_PARCIAL:
				filtroMovimentacaoInterna.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroMovimentacaoInterna.setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);
				facesUtil.setFlashParameter("filtroMovimentacaoInternaDash", filtroMovimentacaoInterna);
				return "pretty:ListarMovimentacaoInterna";

			case TOTAL_MOVIMENTACAO_EXTERNA_PENDENTE:
				filtroMovimentacaoExterna.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroMovimentacaoExterna.setStatus(StatusMovimentacao.PENDENTE);
				facesUtil.setFlashParameter("filtroMovimentacaoExternaDash", filtroMovimentacaoExterna);
				return "pretty:ListarMovimentacaoExterna";

			case TOTAL_MOVIMENTACAO_EXTERNA_EM_ANDAMENTO:
				filtroMovimentacaoExterna.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroMovimentacaoExterna.setStatus(StatusMovimentacao.EM_ANDAMENTO);
				facesUtil.setFlashParameter("filtroMovimentacaoExternaDash", filtroMovimentacaoExterna);
				return "pretty:ListarMovimentacaoExterna";

			case TOTAL_MOVIMENTACAO_EXTERNA_AGUARDANDO_FINALIZAR:
				filtroMovimentacaoExterna.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroMovimentacaoExterna.setStatus(StatusMovimentacao.AGUARDANDO_FINALIZAR);
				facesUtil.setFlashParameter("filtroMovimentacaoExternaDash", filtroMovimentacaoExterna);
				return "pretty:ListarMovimentacaoExterna";

			case TOTAL_MOVIMENTACAO_EXTERNA_FINALIZADO_PARCIAL:
				filtroMovimentacaoExterna.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroMovimentacaoExterna.setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);
				facesUtil.setFlashParameter("filtroMovimentacaoExternaDash", filtroMovimentacaoExterna);
				return "pretty:ListarMovimentacaoExterna";

			case TOTAL_TRANSFERENCIA_PENDENTE:
				filtroTransferencia.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroTransferencia.setStatus(StatusMovimentacao.PENDENTE);
				facesUtil.setFlashParameter("filtroTransferenciaDash", filtroTransferencia);
				return "pretty:ListarTransferencia";

			case TOTAL_TRANSFERENCIA_EM_ANDAMENTO:
				filtroTransferencia.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroTransferencia.setStatus(StatusMovimentacao.EM_ANDAMENTO);
				facesUtil.setFlashParameter("filtroTransferenciaDash", filtroTransferencia);
				return "pretty:ListarTransferencia";

			case TOTAL_TRANSFERENCIA_FINALIZADO_PARCIAL:
				filtroTransferencia.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
				filtroTransferencia.setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);
				facesUtil.setFlashParameter("filtroTransferenciaDash", filtroTransferencia);
				return "pretty:ListarTransferencia";

			case TOTAL_RECEBER_TRANSFERENCIA_EM_ANDAMENTO:
				filtroPedTransito.setUnidadeDestino(securityUtil.getUnidadeSelecionada());
				filtroPedTransito.setStatus(StatusPedido.EM_ANDAMENTO);
				facesUtil.setFlashParameter("filtroPedTransitoDash", filtroPedTransito);
				return "pretty:ListarReceberTransferencia";

			case TOTAL_RECEBER_TRANSFERENCIA_VINCULADO:
				filtroPedTransito.setUnidadeDestino(securityUtil.getUnidadeSelecionada());
				filtroPedTransito.setStatus(StatusPedido.VINCULADO);
				facesUtil.setFlashParameter("filtroPedTransitoDash", filtroPedTransito);
				return "pretty:ListarReceberTransferencia";

			case TOTAL_REMANEJAMENTO_EM_ANDAMENTO:
				filtroRemanejamento.setUnidadeRemanejada(securityUtil.getUnidadeSelecionada());
				filtroRemanejamento.setStatus(StatusPedido.EM_ANDAMENTO);
				facesUtil.setFlashParameter("filtroRemanejamentoDash", filtroRemanejamento);
				return "pretty:ListarRemanejamento";

				
		}
		
		facesUtil.setFlashParameter("filtroRecuperandoDash", filtro);
		return "pretty:ListarRecuperando";
	}

	public int getSolicitacaoResetSenha(){
		if(!securityUtil.possuiPermissao("ListarUsuarioResetSenha")){
			return 0;
		}

		FiltroUsuario filtroUsuario = new FiltroUsuario();

		filtroUsuario.setResetSenha(true);
		filtroUsuario.setUnidadePrisional(securityUtil.getUnidadeSelecionada());

		return this.usuarioService.conutSolicitacaoSenha(filtroUsuario);
	}

	public int getMovimentacaoInternaPendente(){

		if(!securityUtil.possuiPermissao("VisualizarMovimentacaoInterna")){
			return 0;
		}

		FiltroMovimentacaoInterna filtro = new FiltroMovimentacaoInterna();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusMovimentacao.PENDENTE);

		return this.movimentacaoInternaService.countMovimentacaoInterna(filtro);

	}

	public int getMovimentacaoInternaEmAndamento(){

		if(!securityUtil.possuiPermissao("VisualizarMovimentacaoInterna")){
			return 0;
		}
		FiltroMovimentacaoInterna filtro = new FiltroMovimentacaoInterna();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusMovimentacao.EM_ANDAMENTO);

		return this.movimentacaoInternaService.countMovimentacaoInterna(filtro);
	}

	public int getMovimentacaoInternaFinalizadoParcial(){

		if(!securityUtil.possuiPermissao("VisualizarMovimentacaoInterna")){
			return 0;
		}

		FiltroMovimentacaoInterna filtro = new FiltroMovimentacaoInterna();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);

		return this.movimentacaoInternaService.countMovimentacaoInterna(filtro);
	}

	public int getMovimentacaoInternaAguardandoFinalizar(){

		if(!securityUtil.possuiPermissao("VisualizarMovimentacaoInterna")){
			return 0;
		}

		FiltroMovimentacaoInterna filtro = new FiltroMovimentacaoInterna();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusMovimentacao.AGUARDANDO_FINALIZAR);

		return this.movimentacaoInternaService.countMovimentacaoInterna(filtro);
	}

	public int getMovimentacaoExternaPendente(){

		if(!securityUtil.possuiPermissao("VisualizarMovimentacaoExterna")){
			return 0;
		}
		FiltroMovimentacaoExterna filtro = new FiltroMovimentacaoExterna();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());

		filtro.setStatus(StatusMovimentacao.PENDENTE);

		return this.movimentacaoExternaService.countMovimentacaoExterna(filtro);
	}

	public int getMovimentacaoExternaEmAndamento(){

		if(!securityUtil.possuiPermissao("VisualizarMovimentacaoExterna")){
			return 0;
		}

		FiltroMovimentacaoExterna filtro = new FiltroMovimentacaoExterna();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusMovimentacao.EM_ANDAMENTO);

		return this.movimentacaoExternaService.countMovimentacaoExterna(filtro);
	}

	public int getMovimentacaoExternaFinalizadoParcial(){

		if(!securityUtil.possuiPermissao("VisualizarMovimentacaoExterna")){
			return 0;
		}

		FiltroMovimentacaoExterna filtro = new FiltroMovimentacaoExterna();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);

		return this.movimentacaoExternaService.countMovimentacaoExterna(filtro);
	}

	public int getMovimentacaoExternaAguardandoFinalizar(){

		if(!securityUtil.possuiPermissao("VisualizarMovimentacaoExterna")){
			return 0;
		}

		FiltroMovimentacaoExterna filtro = new FiltroMovimentacaoExterna();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusMovimentacao.AGUARDANDO_FINALIZAR);

		return this.movimentacaoExternaService.countMovimentacaoExterna(filtro);
	}
	public int getTransferenciaPendente(){

		if(!securityUtil.possuiPermissao("VisualizarTransferencia")){
			return 0;
		}

		FiltroTransferencia filtro = new FiltroTransferencia();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusMovimentacao.PENDENTE);

		return this.transferenciaService.countMovimentacaoExterna(filtro);
	}
	public int getTransferenciaFinalizadoParcial(){

		if(!securityUtil.possuiPermissao("VisualizarTransferencia")){
			return 0;
		}

		FiltroTransferencia filtro = new FiltroTransferencia();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusMovimentacao.FINALIZADO_PARCIAL);

		return this.transferenciaService.countMovimentacaoExterna(filtro);
	}
	public int getTransferenciaEmAndamento(){

		if(!securityUtil.possuiPermissao("VisualizarTransferencia")){
			return 0;
		}

		FiltroTransferencia filtro = new FiltroTransferencia();

		filtro.setUnidadeOrigem(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusMovimentacao.EM_ANDAMENTO);

		return this.transferenciaService.countMovimentacaoExterna(filtro);
	}
	public int getRecebimentoTransferenciaVinculado(){

		if(!securityUtil.possuiPermissao("VisualizarReceberTransferencia")){
			return 0;
		}

		FiltroPedTransito filtro = new FiltroPedTransito();

		filtro.setUnidadeDestino(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusPedido.VINCULADO);

		return this.pedidoService.countRecebimentoTransferencia(filtro);
	}
	public int getRecebimentoTransferenciaEmAndamento(){

		if(!securityUtil.possuiPermissao("VisualizarReceberTransferencia")){
			return 0;
		}

		FiltroPedTransito filtro = new FiltroPedTransito();

		filtro.setUnidadeDestino(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusPedido.EM_ANDAMENTO);

		return this.pedidoService.countRecebimentoTransferencia(filtro);
	}

	public int getRemanejamentoEmAndamento(){

		if(!securityUtil.possuiPermissao("VisualizarRemanejamento")){
			return 0;
		}

		FiltroRemanejamento filtro = new FiltroRemanejamento();

		filtro.setUnidadeRemanejada(securityUtil.getUnidadeSelecionada());
		filtro.setStatus(StatusPedido.EM_ANDAMENTO);

		return this.remanejamentoService.countRecebimentoTransferencia(filtro);
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

    public Integer getTotalMovimentacaoInternaPendente() {
        return totalMovimentacaoInternaPendente;
    }

	public Integer getTotalMovimentacaoInternaEmAndamento() {
		return totalMovimentacaoInternaEmAndamento;
	}

	public Integer getTotalMovimentacaoInternaAguardandoFinalizar() {
		return totalMovimentacaoInternaAguardandoFinalizar;
	}

	public Integer getTotalMovimentacaoExternaPendente() {
		return totalMovimentacaoExternaPendente;
	}

	public Integer getTotalMovimentacaoExternaEmAndamento() {
		return totalMovimentacaoExternaEmAndamento;
	}

	public Integer getTotalMovimentacaoExternaAguardandoFinalizar() {
		return totalMovimentacaoExternaAguardandoFinalizar;
	}

    public Integer getTotalMovimentacaoInternaFinalizadoParcial() {
        return totalMovimentacaoInternaFinalizadoParcial;
    }
    public Integer getTotalMovimentacaoExternaFinalizadoParcial() {
        return totalMovimentacaoExternaFinalizadoParcial;
    }

    public Integer getTotalTransferenciaPendente() {
        return totalTransferenciaPendente;
    }

    public Integer getTotalTransferenciaEmAndamento() {
        return totalTransferenciaEmAndamento;
    }

    public Integer getTotalTransferenciaFinalizadoParcial() {
        return totalTransferenciaFinalizadoParcial;
    }

    public Integer getTotalReceberTransferenciaEmAndamento() {
        return totalReceberTransferenciaEmAndamento;
    }

    public Integer getTotalReceberTransferenciaViculada() {
        return totalReceberTransferenciaViculada;
    }

    public Integer getTotalRemanejamentoEmAndamento() {
        return totalRemanejamentoEmAndamento;
    }
    
    public Long getTotalProvisorios90a180Dias() {
        return totalProvisorios90a180Dias;
    }

    public Long getTotalProvisoriosMais180Dias() {
        return totalProvisoriosMais180Dias;
    }

}
