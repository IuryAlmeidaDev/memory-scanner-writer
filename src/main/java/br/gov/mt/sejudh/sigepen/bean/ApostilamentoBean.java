package br.gov.mt.sejudh.sigepen.bean;

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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroApostilamento;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Apostilamento;
import br.gov.mt.sejudh.sigepen.model.Contrato;
import br.gov.mt.sejudh.sigepen.model.Refeicao;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.model.UnidadePrisional;
import br.gov.mt.sejudh.sigepen.model.dto.ApostilamentoDTO;
import br.gov.mt.sejudh.sigepen.service.ApostilamentoService;
import br.gov.mt.sejudh.sigepen.service.ContratoService;
import br.gov.mt.sejudh.sigepen.service.RefeicaoService;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.service.UnidadePrisionalService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.SecurityUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarApostilamento", pattern = "/apostilamento", viewId = "/WEB-INF/pages/apostilamento/ListarApostilamento.xhtml"),
		@URLMapping(id = "CadastrarApostilamento", pattern = "/apostilamento/cadastrar", viewId = "/WEB-INF/pages/apostilamento/CadastrarApostilamento.xhtml"),
		@URLMapping(id = "AlterarApostilamento", pattern = "/apostilamento/alterar/#{id}", viewId = "/WEB-INF/pages/apostilamento/CadastrarApostilamento.xhtml"),
		@URLMapping(id = "VisualizarApostilamento", pattern = "/apostilamento/visualizar/#{id}", viewId = "/WEB-INF/pages/apostilamento/CadastrarApostilamento.xhtml") })
public class ApostilamentoBean extends MetodosDePaginaBean {

	private List<Contrato> contratos;
	private LazyDataModel<ApostilamentoDTO> apostilamentos;
	private List<UnidadePrisional> unidadesPrisionais;

	private Refeicao refeicao;
	private Apostilamento apostilamento;
	private FiltroApostilamento filtro;

	@Inject
	private ApostilamentoService apostilamentoService;

	@Inject
	private ContratoService contratoService;

	@Inject
	private UnidadePrisionalService unidadeService;

	@Inject
	private RefeicaoService refeicaoService;

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
		this.setFiltro(new FiltroApostilamento());
		super.carregarFiltroPaginacao(FiltroApostilamento.class, this, getFiltro());
	}

	@URLAction(onPostback = false)
	public String init() {
		try {
			this.setContratos(contratoService.listarAtivos());
			this.setUnidadesPrisionais(unidadeService.listarUnidadesNaoInativas());

			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.apostilamento = new Apostilamento();
				break;
			case VISUALIZAR:
			case ALTERAR:
				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

				if (!buscarApostilamento(id)) {
					facesUtil.exibirMensagemErro("Apostilamento não encontrado.", true);
					return "pretty:ListarApostilamento";
				}

				logDeAcesso(log, this.apostilamento);
				break;
			case LISTAR:
				pesquisar();
				logDeAcesso(log, "Apostilamentos");
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
		Apostilamento apostilamentoAntigo = null;
		String retorno = null;

		try {
			if ((this.apostilamento.getDataInicio()
					.compareTo(this.apostilamento.getContrato().getVigenciaContratoInicio()) < 0)
					|| (this.apostilamento.getDataInicio()
							.compareTo(this.apostilamento.getContrato().getVigenciaContratoFim()) > 0)) {
				throw new ValidacaoException(
						"Data do apostilamento fora do período de vigência do contrato selecionado");
			}

			if (this.validarTemTodosHorarios()) {
				if (this.apostilamento.getId() <= 0) {
					apostilamentoAntigo = apostilamentoService.buscarApostilamentoAnterior(apostilamento.getContrato());
				}

				apostilamentoService.salvar(this.apostilamento);
				inativarApostilamentoAnterior(apostilamentoAntigo, this.apostilamento.getDataInicio());

				facesUtil.exibirMensagemInfo("apostilamento cadastrado com Sucesso!",
						true);
				retorno = "pretty:ListarApostilamento";
				LoggerUtil.logInfo(Acao.SALVAR, apostilamento, log);

			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao salvar apostilamento (" + this.apostilamento.getId() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, apostilamento, e, log);
		}

		return retorno;
	}

	public void pesquisar() {
		this.apostilamentos = new LazyDataModel<ApostilamentoDTO>() {
			private static final long serialVersionUID = -783263180478975476L;

			@Override
			public List<ApostilamentoDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				getFiltro().setListarTodasUnidades(securityUtil.possuiPermissao("ListarTodasUnidades"));
				setRowCount(apostilamentoService.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return apostilamentoService.buscaPorFiltro(getFiltro());
			}
		};
	}

	private void inativarApostilamentoAnterior(Apostilamento anterior, Date dataInicio) {

		try {
			if (anterior != null && anterior.getId() != null && anterior.getId() > 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dataInicio);
				calendar.add(Calendar.DATE, -1);

				anterior.setDataFim(calendar.getTime());
				apostilamentoService.salvar(anterior);
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		}
	}

	private boolean buscarApostilamento(long id) {
		if (id <= 0) {
			return false;
		}

		this.apostilamento = apostilamentoService.buscarPorId(id);

		return this.apostilamento != null;
	}

	public void abrirDialogRefeicao(ActionEvent event) {
		this.refeicao = null;
		facesUtil.exibirPaginaDialog("pretty:AssociarRefeicao");
	}

	public void abrirDialogAlterarRefeicao(Refeicao refeicao) {
		this.refeicao = refeicao;
		facesUtil.setObjetoSessao("refeicao", refeicao);
		facesUtil.exibirPaginaDialog("pretty:AssociarRefeicao");
		LoggerUtil.logInfo(Acao.ALTERAR, refeicao, log);
	}

	private boolean validarRefeicao(Refeicao refeicao) {

		if (refeicao == null)
			return false;

		if (refeicao.getValor() < 0) {
			facesUtil.exibirMensagemErro("Valor da refeição não pode ser negativo.", true);
			return false;
		}

		for (Refeicao tipoRefeicao : this.apostilamento.getRefeicoes()) {
			if (refeicao.getTipoRefeicao().equals(tipoRefeicao.getTipoRefeicao())
					&& (refeicao.getId() <= 0 || refeicao.getId() != tipoRefeicao.getId())) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO",
						"Não é possível registrar duas vezes a mesma refeição.");
				return false;
			}
		}

		return true;
	}

	public void incluirRefeicao(SelectEvent event) {
		Refeicao refeicao = (Refeicao) event.getObject();

		try {
			if (validarRefeicao(refeicao)) {
				if (this.refeicao != null) {
					apostilamento.getRefeicoes().remove(this.refeicao);
				} else {
					refeicao.setApostilamento(this.apostilamento);
				}

				apostilamento.getRefeicoes().add(refeicao);

				if (this.apostilamento.getId() != null && this.apostilamento.getId() > 0) {
					refeicaoService.salvar(refeicao);
					LoggerUtil.logInfo(Acao.SALVAR, refeicao, log);
				} else {
					LoggerUtil.logInfo(Acao.ADICIONAR, refeicao, log);
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao incluir refeicao no apostilamento: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, refeicao, e, log);
		}
	}

	public void excluirRefeicao(Refeicao refeicao) {
		try {
			this.apostilamento.getRefeicoes().remove(refeicao);

			if (this.apostilamento.getId() > 0) {
				refeicaoService.excluir(refeicao);
				LoggerUtil.logInfo(Acao.EXCLUIR, refeicao, log);
			} else {
				LoggerUtil.logInfo(Acao.REMOVER, refeicao, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao excluir refeicao do Apostilamento: " + e.getMessage());
			LoggerUtil.logErro(Acao.REMOVER, refeicao, e, log);
		}
	}

	public Date getMinDate() {

		if (this.apostilamento != null && this.apostilamento.getContrato() != null) {
			return this.apostilamento.getContrato().getVigenciaContratoInicio();
		}

		return null;
	}

	private boolean validarTemTodosHorarios() {
		List<TipoRefeicao> listaRefeicao = this.apostilamento.getRefeicoes().stream().map(x -> x.getTipoRefeicao())
				.collect(Collectors.toList());

		if (!tipoRefeicaoService.temTodasRefeicoes(listaRefeicao)) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO",
					"Por favor, informe horário para todas as refeições.");
			return false;
		}

		return true;
	}

	public Apostilamento getApostilamento() {
		return apostilamento;
	}

	public void setApostilamento(Apostilamento apostilamento) {
		this.apostilamento = apostilamento;
	}

	public LazyDataModel<ApostilamentoDTO> getApostilamentos() {
		return apostilamentos;
	}

	public void setApostilamentos(LazyDataModel<ApostilamentoDTO> apostilamentos) {
		this.apostilamentos = apostilamentos;
	}

	public FiltroApostilamento getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroApostilamento filtro) {
		this.filtro = filtro;
	}

	public List<Contrato> getContratos() {
		return contratos;
	}

	public void setContratos(List<Contrato> contratos) {
		this.contratos = contratos;
	}

	public Refeicao getRefeicao() {
		return refeicao;
	}

	public void setRefeicao(Refeicao refeicao) {
		this.refeicao = refeicao;
	}

	public List<UnidadePrisional> getUnidadesPrisionais() {
		return unidadesPrisionais;
	}

	public void setUnidadesPrisionais(List<UnidadePrisional> unidadesPrisionais) {
		this.unidadesPrisionais = unidadesPrisionais;
	}

}