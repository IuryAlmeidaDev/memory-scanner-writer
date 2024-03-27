package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroHorarioRefeicao;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.HorarioRefeicaoDTO;
import br.gov.mt.sejudh.sigepen.service.HorarioRefeicaoService;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarHorarioRefeicao", pattern = "/horarioRefeicao", viewId = "/WEB-INF/pages/horarioRefeicao/ListarHorarioRefeicao.xhtml"),
		@URLMapping(id = "CadastrarHorarioRefeicao", pattern = "/horarioRefeicao/cadastrar", viewId = "/WEB-INF/pages/horarioRefeicao/CadastrarHorarioRefeicao.xhtml"),
		@URLMapping(id = "AlterarHorarioRefeicao", pattern = "/horarioRefeicao/alterar/#{id}", viewId = "/WEB-INF/pages/horarioRefeicao/CadastrarHorarioRefeicao.xhtml"),
		@URLMapping(id = "VisualizarHorarioRefeicao", pattern = "/horarioRefeicao/visualizar/#{id}", viewId = "/WEB-INF/pages/horarioRefeicao/CadastrarHorarioRefeicao.xhtml") })
public class HorarioRefeicaoBean extends MetodosDePaginaBean {

	private LazyDataModel<HorarioRefeicaoDTO> horariosRefeicao;
	private List<UnidadePrisional> unidadesPrisionais;
	private List<HorarioRefeicao> listHorarios;

	private Date dataInicio;
	private UnidadePrisional unidade;
	private HorarioRefeicao horarioRefeicao;
	private FiltroHorarioRefeicao filtro;

	@Inject
	private HorarioRefeicaoService service;

	@Inject
	private UnidadePrisionalService unidadeService;
	
	@Inject
	private TipoRefeicaoService tipoRefeicaoService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private SecurityUtil securityUtil;

	@Inject
	private Logger log;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		this.setFiltro(new FiltroHorarioRefeicao());
		super.carregarFiltroPaginacao(FiltroHorarioRefeicao.class, this, getFiltro());
	}

	@URLAction(onPostback = false)
	public String init() {
		try {
			this.unidadesPrisionais = unidadeService.listarUnidadesComContratosAtivo();

			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.listHorarios = new ArrayList<HorarioRefeicao>();
				break;
			case VISUALIZAR:
			case ALTERAR:
				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

				if (!buscar(id)) {
					facesUtil.exibirMensagemErro("Horário de Refeição não encontrado.", true);
					return "pretty:ListarHorarioRefeicao";
				}

				logDeAcesso(log, this.listHorarios);
				break;
			case LISTAR:
				pesquisar();
				logDeAcesso(log, "Horário de refeição");
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
//		List<HorarioRefeicao> horariosAntigos = new ArrayList<HorarioRefeicao>();
		
		if(this.unidade == null) {
			this.unidade = SecurityUtil.getUnidadeSelecionada();
		}
		

		try {
			if(validarTemTodosHorarios()) {
//				if(this.listHorarios.get(0).getId() == null || this.listHorarios.get(0).getId() <= 0) {
//					horariosAntigos = service.buscarPorUnidadeDataInicio(this.unidade.getId(), this.dataInicio);
//				}
				
				
				for(HorarioRefeicao hora : this.listHorarios) {
					hora.setUnidadePrisional(this.unidade);
					hora.setDataInicio(this.dataInicio);
					service.salvar(hora);
				}
				
//				this.inativarHorariosRefeicao(horariosAntigos, dataInicio);
				
				facesUtil.exibirMensagemInfo("Horários de refeição da unidade" + this.unidade.getNome() + " salvo com Sucesso!",
						true);
				retorno = "pretty:ListarHorarioRefeicao";
				LoggerUtil.logInfo(Acao.SALVAR, listHorarios, log);
				
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao salvar horário de refeição (" + this.listHorarios + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, listHorarios, e, log);
		}

		return retorno;
	}

	public void pesquisar() {
		this.horariosRefeicao = new LazyDataModel<HorarioRefeicaoDTO>() {
			private static final long serialVersionUID = 9125627329863531857L;

			@Override
			public List<HorarioRefeicaoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				getFiltro().setListarTodasUnidades(securityUtil.possuiPermissao("ListarTodasUnidades"));
				setRowCount(service.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return service.buscaPorFiltro(getFiltro());
			}
		};
	}
	
	public void abrirDialogHorario(ActionEvent event) {
		this.horarioRefeicao = null;
		facesUtil.exibirPaginaDialog("pretty:AssociarHorarioRefeicaoUnidade");
	}

	public void abrirDialogAlterarHorario(HorarioRefeicao horarioRefeicao) {
		this.horarioRefeicao = horarioRefeicao;
		facesUtil.setObjetoSessao("horarioRefeicao", horarioRefeicao);
		facesUtil.exibirPaginaDialog("pretty:AssociarHorarioRefeicaoUnidade");
		LoggerUtil.logInfo(Acao.ALTERAR, horarioRefeicao, log);
	}
	
	private boolean validarHorarioRefeicao(HorarioRefeicao horarioRefeicao) {
		if(horarioRefeicao == null) return false;
		
		
		for(HorarioRefeicao horario : this.listHorarios) {
			if(horarioRefeicao.getTipoRefeicao().equals(horario.getTipoRefeicao()) && ((horario.getId() <= 0) || horarioRefeicao.getId() != horario.getId())) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Não é possível registrar dois horários para a mesma refeição.");
				return false; 
			}
		}
		
		return true;
	}
 
	public void incluirHorarioRefeicao(SelectEvent event) {
		HorarioRefeicao horarioRefeicao = (HorarioRefeicao) event.getObject();

		try {
			
			if(this.validarHorarioRefeicao(horarioRefeicao)) {
				
				if (this.horarioRefeicao != null) {
					this.listHorarios.remove(this.horarioRefeicao);
				} else {
					horarioRefeicao.setUnidadePrisional(unidade);
					horarioRefeicao.setDataInicio(dataInicio);
				}
				
				this.listHorarios.add(horarioRefeicao);
			}
			
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao incluir a horário de refeição no contrato: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, horarioRefeicao, e, log);
		}
	}

	private boolean buscar(long id) {
		if (id <= 0) {
			return false;
		}

		this.listHorarios = service.buscarPorUnidade(id);
		
		this.unidade = this.listHorarios.get(0).getUnidadePrisional();
		this.dataInicio = this.listHorarios.get(0).getDataInicio();

		return this.listHorarios != null && !this.listHorarios.isEmpty();
	}
	
	private void inativarHorariosRefeicao(List<HorarioRefeicao> horariosAntigos, Date dataInicio) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dataInicio);
			calendar.add(Calendar.DATE, -1);
			
			if(horariosAntigos.size() > 0) {
				for(HorarioRefeicao anterior : horariosAntigos) {
					anterior.setDataFim(calendar.getTime());
					service.salvar(anterior);
				}
			}
			
		}  catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		}
		
	}
	
	private boolean validarTemTodosHorarios() {		
		List<TipoRefeicao> listaRefeicao = this.listHorarios.stream().map(x -> x.getTipoRefeicao()).collect( Collectors.toList() );
		
		if(!tipoRefeicaoService.temTodasRefeicoes(listaRefeicao)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Por favor, informe horário para todas as refeições.");
			return false;
		}
		
		
		if(listHorarios.get(0).getId() == 0) {
			if(service.temHorarioRefeicao(this.unidade.getId())) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Já existe horário de refição para esta unidade.");
				return false;
			}
		}		
		return true; 
	}


	public FiltroHorarioRefeicao getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroHorarioRefeicao filtro) {
		this.filtro = filtro;
	}

	public LazyDataModel<HorarioRefeicaoDTO> getHorariosRefeicao() {
		return horariosRefeicao;
	}

	public void setHorariosRefeicao(LazyDataModel<HorarioRefeicaoDTO> horariosRefeicao) {
		this.horariosRefeicao = horariosRefeicao;
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}
	
	public List<HorarioRefeicao> getListHorarios(){
		return this.listHorarios;
	}
	
	public void setListHorarios(List<HorarioRefeicao> listHorarios) {
		this.listHorarios = listHorarios;
	}
	
	public UnidadePrisional getUnidade() {
		return this.unidade;
	}
	
	public void setUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

}
