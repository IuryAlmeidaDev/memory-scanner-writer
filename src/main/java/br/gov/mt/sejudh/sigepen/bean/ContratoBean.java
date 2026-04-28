package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroContrato;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.AcaoDeTela;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.TipoArquivo;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Arquivo;
import br.gov.mt.sejudh.sigepen.model.ArquivoJustificativa;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.Fornecedor;
import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.Justificativa;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ContratoDTO;
import br.gov.mt.sejudh.sigepen.model.dto.UnidadePrisionalDTO;
import br.gov.mt.sejudh.sigepen.service.ContratoService;
import br.gov.mt.sejudh.sigepen.service.FornecedorService;
import br.gov.mt.sejudh.sigepen.service.HorarioRefeicaoService;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.service.AgendadorPedidoAlimentacaoDinamicoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarContrato", pattern = "/contrato", viewId = "/WEB-INF/pages/contrato/ListarContrato.xhtml"),
		@URLMapping(id = "CadastrarContrato", pattern = "/contrato/cadastrar", viewId = "/WEB-INF/pages/contrato/CadastrarContrato.xhtml"),
		@URLMapping(id = "AlterarContrato", pattern = "/contrato/alterar/#{id}", viewId = "/WEB-INF/pages/contrato/CadastrarContrato.xhtml"),
		@URLMapping(id = "VisualizarContrato", pattern = "/contrato/visualizar/#{id}", viewId = "/WEB-INF/pages/contrato/CadastrarContrato.xhtml") })
public class ContratoBean extends MetodosDePaginaBean {

	private FiltroContrato filtro;

	private Contrato contrato;

	private UnidadePrisional unidade;

	private HorarioRefeicao horarioRefeicao;

	private Status inativo = Status.INATIVO;

	private LazyDataModel<ContratoDTO> contratos;

	@Inject
	private HorarioRefeicaoService horarioRefeicaoService;

	@Inject
	private ContratoService contratoService;

	@Inject
	private FornecedorService fornecedorService;

	@Inject
	private TipoRefeicaoService tipoRefeicaoService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@Inject
	private SecurityUtil security;

	@Inject
	private FileDownloadHandlerBean fileDownload;

	private Long contratoSelecionada;

	private List<UnidadePrisional> unidadesPrisionais;

	private List<Fornecedor> fornecedores;

	@Inject
	private UnidadePrisionalService unidadeService;

	@Inject
	private AgendadorPedidoAlimentacaoDinamicoService agendadorPedidoAlimentacaoDinamicoService;

	public Contrato getcontrato() {
		return contrato;
	}

	public void setcontrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public Long getcontratoSelecionada() {
		return this.contratoSelecionada;
	}

	public void setcontratoSelecionada(Long contratoSelecionada) {
		this.contratoSelecionada = contratoSelecionada;
	}

	public LazyDataModel<ContratoDTO> getContratos() {
		return contratos;
	}

	public void setContratos(LazyDataModel<ContratoDTO> contratos) {
		this.contratos = contratos;
	}

	public FiltroContrato getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroContrato filtro) {
		this.filtro = filtro;
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}

	public List<Fornecedor> getFornecedores() {
		return fornecedores;
	}

	public void setFornecedores(List<Fornecedor> fornecedores) {
		this.fornecedores = fornecedores;
	}

	public FornecedorService getFornecedorService() {
		return fornecedorService;
	}

	public boolean getInativo() {
		return this.inativo.equals(this.contrato.getStatus());
	}

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {

		if (getAcaoDeTela().equals(AcaoDeTela.ALTERAR) || getAcaoDeTela().equals(AcaoDeTela.VISUALIZAR)) {
			if (facesUtil.getQueryParameter("id") != null) {
				this.contratoSelecionada = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
			}
		}
		
		 this.setFiltro(new FiltroContrato());
		
		if (!security.possuiPermissao("ListarCardapio")) {
			this.setFiltro(new FiltroContrato());
			// this.filtro.setId(SecurityUtil.getUnidadeSelecionada().getId());

			super.carregarFiltroPaginacao(FiltroContrato.class, this, getFiltro());
		} else {
			super.carregarFiltroPaginacao(FiltroContrato.class, this);
		}
	}

	@URLAction(onPostback = false)
	public String init() {
		try {
			this.unidadesPrisionais = unidadeService.listarUnidadesNaoInativas();
			this.fornecedores = fornecedorService.listarAtivo();
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.contrato = new Contrato();
				break;
			case VISUALIZAR:
			case ALTERAR:

				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

				if (!buscarContrato(id)) {
					facesUtil.exibirMensagemErro("Contrato não encontrada.", true);
					return "pretty:ListarContrato";
				}

				logDeAcesso(log, this.contrato);

				break;

			case LISTAR:
				pesquisar();
				logDeAcesso(log, "Contrato");
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

	private boolean buscarContrato(long id) {
		if (id <= 0) {
			return false;
		}

		this.contrato = contratoService.buscaPorId(id);

		if (this.contrato == null) {

			return false;
		}

		if (this.contrato.getJustificativa() == null) {
			this.contrato.setJustificativa(new Justificativa());
		}

		return true;
	}

	public void pesquisar() {
		this.contratos = new LazyDataModel<ContratoDTO>() {
			private static final long serialVersionUID = 1L;

			public List<ContratoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
		
				
				try {
					
					if(getFiltro () == null) {
						setFiltro(new FiltroContrato());
					}
					
					getFiltro().setListarTodasUnidades(security.possuiPermissao("ListarTodasUnidades"));
					
					configuraPaginacao(0, first, pageSize, sortField, sortOrder, getFiltro());
					
					int total = contratoService.contarFiltrados(getFiltro());
					setRowCount(total);
			
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
					return contratoService.buscarPorFiltro(getFiltro());
					
				} catch (Exception e) {
					e.printStackTrace();
					return new ArrayList<>();
					}
			}
		};
	}

	private boolean validarDataContrato() {
		if (!this.contrato.getVigenciaContratoInicio().before(this.contrato.getVigenciaContratoFim())) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO",
					"A data do fim da virgencia do contrato não pode ser menor da data do inicio do contrato!");
			return false;
		}
		return true;
	}

	private boolean validaContratoExistenteAtivo() {
		Contrato contratoExistente = contratoService.buscarContratoPorNumero(this.contrato.getNumContrato());
		if (contratoExistente != null && contratoExistente.getId() != this.contrato.getId()) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Contrato ja existente!");
			return false;
		}
		return true;
	}

	private boolean validarContratoUnidade() {
		if (this.contrato.getUnidades().size() == 0) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Contrato não pode ficar sem unidade!");
			return false;
		}
		if (!contratoService.validarContratoUnidade(this.contrato)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Existe unidades em contratos ativos!");
			return false;
		}
		return true;
	}

	private boolean validarTemTodosHorarios() {
		List<TipoRefeicao> listaRefeicao = this.contrato.getHorariosRefeicao().stream()
				.filter(x -> x.getUnidadePrisional() == null)
				.map(x -> x.getTipoRefeicao())
				.collect(Collectors.toList());

		if (!tipoRefeicaoService.temTodasRefeicoes(listaRefeicao)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO",
					"Por favor, informe horário para todas as refeições.");
			return false;
		}

		return true;
	}

	private boolean validarContrato() {
		return (this.validarDataContrato() && this.validaContratoExistenteAtivo() && this.validarContratoUnidade()
				&& this.validarTemTodosHorarios());
	}

	public String salvar() {
		String retorno = null;

		try {
			if (this.validarContrato()) {
				contratoService.salvar(this.contrato);
				rearmarDisparosUnidadesDoContrato();
				facesUtil.exibirMensagemInfo("contrato " + this.contrato.getNumContrato() + " salva com Sucesso!",
						true);
				retorno = "pretty:ListarContrato";
				LoggerUtil.logInfo(Acao.SALVAR, contrato, log);
			}

		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao salvar contrato (" + this.contrato.getNumContrato() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, contrato, e, log);
		}

		return retorno;
	}

	public void abrirDialogUnidade(ActionEvent event) {
		this.unidade = null;
		facesUtil.exibirPaginaDialog("pretty:AssociarUnidade");
	}

	public void abrirDialogAlterarUnidade(UnidadePrisional unidade) {
		this.unidade = unidade;
		facesUtil.setObjetoSessao("unidade", unidade);
		facesUtil.exibirPaginaDialog("pretty:AssociarUnidade");
		LoggerUtil.logInfo(Acao.ALTERAR, unidade, log);
	}

	public void incluirUnidade(SelectEvent event) {
		UnidadePrisionalDTO u = (UnidadePrisionalDTO) event.getObject();
		UnidadePrisional unidade = unidadeService.buscaSimplesPorId(u.getId());

		try {
			if (unidade != null) {
				if (this.contrato.getUnidades().contains(unidade)) {
					facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Unidade ja adicinada ao contrato ");
				} else {
					if (this.unidade != null) {
						this.contrato.getUnidades().remove(this.unidade);
						contrato.getUnidades().add(unidade);
					} else {
						// unidade.setContrato(contrato);
						this.contrato.getUnidades().add(unidade);
					}
				}

			}
			this.unidadesPrisionais.remove(unidade);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao incluir a unidade no contrato: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, unidade, e, log);
		}
	}

	public void excluirUnidade(UnidadePrisional unidade) {
		try {
			this.contrato.getUnidades().remove(unidade);

			if (this.contrato.getId() > 0) {
				LoggerUtil.logInfo(Acao.EXCLUIR, unidade, log);
			} else {
				LoggerUtil.logInfo(Acao.REMOVER, unidade, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao excluir unidade do contrato: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, unidade, e, log);
		}
	}

	public void abrirDialogHorario(ActionEvent event) {
		this.horarioRefeicao = null;
		facesUtil.setObjetoSessao("unidadesContrato", this.contrato.getUnidades());
		facesUtil.exibirPaginaDialog("pretty:AssociarHorarioRefeicao");
	}

	public void abrirDialogAlterarHorario(HorarioRefeicao horarioRefeicao) {
		this.horarioRefeicao = horarioRefeicao;
		facesUtil.setObjetoSessao("horarioRefeicao", horarioRefeicao);
		facesUtil.setObjetoSessao("unidadesContrato", this.contrato.getUnidades());
		facesUtil.exibirPaginaDialog("pretty:AssociarHorarioRefeicao");
		LoggerUtil.logInfo(Acao.ALTERAR, horarioRefeicao, log);
	}

	private boolean validarHorarioRefeicao(HorarioRefeicao horarioRefeicao) {
		if (horarioRefeicao == null)
			return false;
		
		if (horarioRefeicao.getHorarioInicio() == null || horarioRefeicao.getHorarioInicio().length() != 4
				|| horarioRefeicao.getHorarioFim() == null || horarioRefeicao.getHorarioFim().length() != 4) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO",
					"Horário da refeição inválido: deve informar o horário no formato HHmm.");
			return false;
		}

		Integer horaMinima = 0;
		Integer horaMaxima = 23;
		Integer minutoMaximo = 59;
		
		int hInicio = Integer.parseInt(horarioRefeicao.getHorarioInicio().substring(0, 2));
		int mInicio = Integer.parseInt(horarioRefeicao.getHorarioInicio().substring(2, 4));
		int hFim = Integer.parseInt(horarioRefeicao.getHorarioFim().substring(0, 2));
		int mFim = Integer.parseInt(horarioRefeicao.getHorarioFim().substring(2, 4));

		if (hInicio < horaMinima || hInicio > horaMaxima || mInicio > minutoMaximo ||
			hFim < horaMinima || hFim > horaMaxima || mFim > minutoMaximo) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO",
					"Horário da refeição inválido: as horas devem ser entre 00 e 23 e os minutos entre 00 e 59.");
			return false;
		}
		
		if (Integer.parseInt(horarioRefeicao.getHorarioInicio()) > Integer.parseInt(horarioRefeicao.getHorarioFim())) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO",
					"Horário da refeição inválido: o horário de início não pode ser maior que o horário de fim.");
			return false;
		}

		for (HorarioRefeicao horario : this.contrato.getHorariosRefeicao()) {
			boolean mesmoEscopo = Objects.equals(
					horarioRefeicao.getUnidadePrisional() != null ? horarioRefeicao.getUnidadePrisional().getId() : null,
					horario.getUnidadePrisional() != null ? horario.getUnidadePrisional().getId() : null);

			if (mesmoEscopo
					&& horarioRefeicao.getTipoRefeicao().equals(horario.getTipoRefeicao())
					&& ((horario.getId() <= 0) || horarioRefeicao.getId() != horario.getId())) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO",
						"Não é possível registrar dois horários para a mesma refeição no mesmo escopo.");
				return false;
			}
		}

		return true;
	}

	public void incluirHorarioRefeicao(SelectEvent event) {
		HorarioRefeicao horarioRefeicao = (HorarioRefeicao) event.getObject();
		try {
			if (validarHorarioRefeicao(horarioRefeicao)) {

				if (this.horarioRefeicao != null) {
					this.contrato.getHorariosRefeicao().remove(this.horarioRefeicao);
				} else {
					horarioRefeicao.setContrato(contrato);
				}

				this.contrato.getHorariosRefeicao().add(horarioRefeicao);

//				if (this.contrato.getId() != null && this.contrato.getId() > 0) {
//					horarioRefeicaoService.salvar(horarioRefeicao);
//					LoggerUtil.logInfo(Acao.SALVAR, horarioRefeicao, log);
//				} else {
//					LoggerUtil.logInfo(Acao.ADICIONAR, horarioRefeicao, log);
//				}
			}

		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao incluir a horário de refeição no contrato: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, horarioRefeicao, e, log);
		}
	}

	public void excluirHorarioRefeicao(HorarioRefeicao horarioRefeicao) {
		try {
			this.contrato.getHorariosRefeicao().remove(horarioRefeicao);

			if (this.contrato.getId() > 0) {
				horarioRefeicaoService.excluir(horarioRefeicao);
				rearmarDisparosUnidadesDoContrato();
				LoggerUtil.logInfo(Acao.EXCLUIR, horarioRefeicao, log);
			} else {
				LoggerUtil.logInfo(Acao.REMOVER, horarioRefeicao, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao excluir horário de refeição do contrato: " + e.getMessage());
			LoggerUtil.logErro(Acao.ASSOCIAR, horarioRefeicao, e, log);
		}
	}

	public void associarArquivoJustificativa(FileUploadEvent event) {

		if (event == null) {
			return;
		}

		ArquivoJustificativa arquivoJustificativa = new ArquivoJustificativa();
		arquivoJustificativa.setJustificativa(this.contrato.getJustificativa());
		arquivoJustificativa.setDataInclusao(Sigepen.getDataAtual());
		arquivoJustificativa.setNome(event.getFile().getFileName());
		arquivoJustificativa.setArquivo(event.getFile().getContents());
		arquivoJustificativa.setContentType(event.getFile().getContentType());

		this.contrato.getJustificativa().setArquivoJustificativa(arquivoJustificativa);

		LoggerUtil.logInfo(Acao.ADICIONAR, arquivoJustificativa + " " + this.contrato, log);
	}

	public void downloadArquivoJustificativa() {
		Arquivo arquivo = new Arquivo();
		ArquivoJustificativa arquivoJustificativa = this.contrato.getJustificativa().getArquivoJustificativa();
		byte[] pdf = arquivoJustificativa.getArquivo();
		arquivo.setBytes(pdf);
		arquivo.setContentType("application/pdf");
		arquivo.setNome(arquivoJustificativa.getNome());
		arquivo.setTipo(TipoArquivo.PDF);
		fileDownload.prepararArquivo(arquivo);
		RequestContext.getCurrentInstance().execute("download()");
	}

	private void rearmarDisparosUnidadesDoContrato() {
		if (this.contrato == null || this.contrato.getUnidades() == null) {
			return;
		}

		for (UnidadePrisional unidadeContrato : this.contrato.getUnidades()) {
			if (unidadeContrato != null && unidadeContrato.getId() > 0) {
				try {
					agendadorPedidoAlimentacaoDinamicoService.rearmarUnidade(unidadeContrato.getId());
				} catch (Exception e) {
					log.warn("Falha ao reprogramar disparo automatico da unidade " + unidadeContrato.getId(), e);
				}
			}
		}
	}

}
