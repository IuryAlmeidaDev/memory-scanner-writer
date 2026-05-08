package br.gov.mt.sejudh.sigepen.bean;

import java.util.Date;
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

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoDieta;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.DietaRecuperando;
import br.gov.mt.sejudh.sigepen.model.TipoDieta;
import br.gov.mt.sejudh.sigepen.service.DietaEspecialService;
import br.gov.mt.sejudh.sigepen.service.TipoDietaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "ListarTipoDieta", pattern = "/tipoDieta", viewId = "/WEB-INF/pages/tipoDieta/ListarTipoDieta.xhtml"),
		@URLMapping(id = "CadastrarTipoDieta", pattern = "/tipoDieta/cadastrar", viewId = "/WEB-INF/pages/tipoDieta/CadastrarTipoDieta.xhtml"),
		@URLMapping(id = "AlterarTipoDieta", pattern = "/tipoDieta/alterar/#{id}", viewId = "/WEB-INF/pages/tipoDieta/CadastrarTipoDieta.xhtml"),
		@URLMapping(id = "VisualizarTipoDieta", pattern = "/tipoDieta/visualizar/#{id}", viewId = "/WEB-INF/pages/tipoDieta/CadastrarTipoDieta.xhtml") })
public class TipoDietaBean extends MetodosDePaginaBean {
	
	private TipoDieta tipoDieta;
	
	private LazyDataModel<TipoDieta> tiposDieta;
	
	private FiltroTipoDieta filtro;
	
	@Inject
	private TipoDietaService service;
	
	@Inject
	private DietaEspecialService dietaRecuperandoService;
	
	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;

	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void carregaFiltros() throws Exception {
		this.setFiltro(new FiltroTipoDieta());
		super.carregarFiltroPaginacao(FiltroTipoDieta.class, this, getFiltro());
	}
	
	@URLAction(onPostback = false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.tipoDieta = new TipoDieta();
				break;
			case VISUALIZAR:
			case ALTERAR:
				long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));

				if (!buscarTipoDieta(id)) {
					facesUtil.exibirMensagemErro("Tipo de Dieta não encontrado.", true);
					return "pretty:ListarTipoDieta";
				}

				logDeAcesso(log, this.tipoDieta);
				break;
			case LISTAR:
				pesquisar();
				logDeAcesso(log, "Tipos Dieta");
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
	
	private boolean buscarTipoDieta(long id) {
		if (id <= 0) {
			return false;
		}

		this.tipoDieta = service.buscarPorId(id, true);

		return this.tipoDieta != null;
	}
	
	public void pesquisar() {
		this.tiposDieta = new LazyDataModel<TipoDieta>() {
			private static final long serialVersionUID = -783263180478975476L;

			@Override
			public List<TipoDieta> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));

				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					LoggerUtil.logErro(e, log);
				}

				return service.buscarPorFiltro(getFiltro());
			}
		};
	}
	
	private boolean validar() throws Exception {
		
		if(this.tipoDieta.getId() != null && this.tipoDieta.getId() > 0) {
			TipoDieta old = service.buscarPorId(this.tipoDieta.getId());
			
			if(Status.INATIVO.equals(this.tipoDieta.getStatus())) {
				if(Status.ATIVO.equals(old.getStatus())) {
					inativarRecuperandos(this.tipoDieta);
				}
			} else {
				if(!this.tipoDieta.getEspecial()) {
					if(old.getEspecial()) {
						if(service.existeDietaPadrao()) {
							throw new ValidacaoException("Já existe um Tipo de Dieta padrão cadastrado, verificar se é um cadastro de dieta especial.");
						}
					}
				}
			}
		}else {
			if(!this.tipoDieta.getEspecial()) {
				if(service.existeDietaPadrao()) {
					throw new ValidacaoException("Já existe um Tipo de Dieta padrão cadastrado, verificar se é um cadastro de dieta especial.");
				}
			}
		}
		
		return true;
	}
	
	public String salvar() {
		String retorno = null;
		try {
			if(validar())
				service.salvar(this.tipoDieta);
			
			facesUtil.exibirMensagemInfo("Tipo de Dieta " + this.tipoDieta.getDescricao() + " salva com Sucesso!", true);
			retorno = "pretty:ListarTipoDieta";
			LoggerUtil.logInfo(Acao.SALVAR, tipoDieta, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
					"Erro ao salvar Tipo de Dieta (" + this.tipoDieta.getDescricao() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, tipoDieta, e, log);
		}

		return retorno;
	}
	
	public String inativar(TipoDieta tipoDieta) {
		String retorno = null;
		
		try {
			tipoDieta.setStatus(Status.INATIVO);
			inativarRecuperandos(tipoDieta);
			service.salvar(tipoDieta);
			
			facesUtil.exibirMensagemInfo("Tipo de Dieta " + tipoDieta.getDescricao() + " inativada com sucesso!", false);
			LoggerUtil.logInfo(Acao.SALVAR, tipoDieta, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch(Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao inativar Tipo de Dieta (" + tipoDieta.getDescricao() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, tipoDieta, e, log);	
		}
		
		return retorno;
	}
	
	private void inativarRecuperandos(TipoDieta tipoDieta) throws Exception {
		try {
			List<DietaRecuperando> dietaRecuperandos = dietaRecuperandoService.buscarbuscarPorTipoDieta(tipoDieta.getId());
			
			if(dietaRecuperandos.size() > 0) {
				for(DietaRecuperando recuperando : dietaRecuperandos) {
					recuperando.setFim(new Date());
					dietaRecuperandoService.salvar(recuperando);
				}
			}
			
		} catch(Exception e) {
			throw new Exception("Ocorreu um erro ao inativar dieta especial de recuperandos.");
		}
	}
	
	public TipoDieta getTipoDieta() {
		return tipoDieta;
	}

	public void setTipoDieta(TipoDieta tipoDieta) {
		this.tipoDieta = tipoDieta;
	}

	public LazyDataModel<TipoDieta> getTiposDieta() {
		return tiposDieta;
	}

	public void setTiposDieta(LazyDataModel<TipoDieta> tiposDieta) {
		this.tiposDieta = tiposDieta;
	}

	public FiltroTipoDieta getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroTipoDieta filtro) {
		this.filtro = filtro;
	}
}
