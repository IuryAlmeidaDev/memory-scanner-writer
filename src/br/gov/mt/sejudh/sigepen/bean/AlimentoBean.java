package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroAlimento;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Alimento;
import br.gov.mt.sejudh.sigepen.service.AlimentoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarAlimento", pattern = "/alimento", viewId = "/WEB-INF/pages/alimento/ListarAlimento.xhtml"),
		@URLMapping(id = "CadastrarAlimento", pattern = "/alimento/cadastrar", viewId = "/WEB-INF/pages/alimento/CadastrarAlimento.xhtml"),
		@URLMapping(id = "AlterarAlimento", pattern = "/alimento/alterar/#{id}", viewId = "/WEB-INF/pages/alimento/CadastrarAlimento.xhtml"),
		@URLMapping(id = "VisualizarAlimento", pattern = "/alimento/visualizar/#{id}", viewId = "/WEB-INF/pages/alimento/CadastrarAlimento.xhtml") })
public class AlimentoBean extends MetodosDePaginaBean {

	private Alimento alimento;

	private LazyDataModel<Alimento> alimentos;

	private FiltroAlimento filtro;
	
	@Inject
	private AlimentoService service;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		this.setFiltro(new FiltroAlimento());
		super.carregarFiltroPaginacao(FiltroAlimento.class, this, getFiltro());
	}

	@URLAction(onPostback = false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.alimento = new Alimento();
				break;
			case VISUALIZAR:
			case ALTERAR:
				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

				if (!buscarAlimento(id)) {
					facesUtil.exibirMensagemErro("Alimento não encontrado.", true);
					return "pretty:ListarAlimento";
				}

				logDeAcesso(log, this.alimento);
				break;
			case LISTAR:
				pesquisar();
				logDeAcesso(log, "Alimentos");
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

	private boolean buscarAlimento(long id) {
		if (id <= 0) {
			return false;
		}

		this.alimento = service.buscarPorId(id, true);

		return this.alimento != null;
	}

	public void pesquisar() {
		this.alimentos = new LazyDataModel<Alimento>() {

			private static final long serialVersionUID = -2430049618383025058L;

			@Override
			public List<Alimento> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}

				return service.buscarPeloFiltro(getFiltro());
			}
		};
	}
	
	private boolean validarAlimentoUnico(Alimento alimento) {
		Alimento retornoAlimento = service.buscarAlimentoPorNome(this.alimento);
		if(retornoAlimento != null && retornoAlimento.getId() != alimento.getId()) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", "Alimento já existe");
			return false;
		}
		return true;
	}

	public String salvar() {
		String retorno = null;
		if(this.validarAlimentoUnico(alimento)) {			
			try {
				service.salvar(this.alimento);
				
				facesUtil.exibirMensagemInfo("Alimento " + this.alimento.getDescricao() + " salva com Sucesso!", true);
				retorno = "pretty:ListarAlimento";
				LoggerUtil.logInfo(Acao.SALVAR, alimento, log);
			} catch (ValidacaoException e) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
			} catch (Exception e) {
				facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
						"Erro ao salvar Alimento (" + this.alimento.getDescricao() + "): " + e.getMessage());
				LoggerUtil.logErro(Acao.SALVAR, alimento, e, log);
			}
		}

		return retorno;
	}

	public void excluir(Alimento alimento) {
		try {
			service.excluir(alimento);
			facesUtil.exibirMensagemInfo("Alimento " + alimento.getDescricao() + " excluída com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, alimento, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "A remoção do alimento não é possível, ("
					+ alimento.getDescricao() + "), pois está atualmente vinculado a um cardápio. ");
			LoggerUtil.logErro(Acao.EXCLUIR, alimento, e, log);
		}
	}

	public Alimento getAlimento() {
		return alimento;
	}

	public void setAlimento(Alimento alimento) {
		this.alimento = alimento;
	}

	public LazyDataModel<Alimento> getAlimentos() {
		return alimentos;
	}

	public void setAlimentos(LazyDataModel<Alimento> alimentos) {
		this.alimentos = alimentos;
	}

	public FiltroAlimento getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroAlimento filtro) {
		this.filtro = filtro;
	}
}
