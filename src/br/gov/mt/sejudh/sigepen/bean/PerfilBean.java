package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPerfil;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Funcionalidade;
import br.gov.mt.sejudh.sigepen.model.Perfil;
import br.gov.mt.sejudh.sigepen.model.Permissao;
import br.gov.mt.sejudh.sigepen.service.PerfilService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarPerfil", pattern = "/perfil", viewId = "/WEB-INF/pages/perfil/ListarPerfil.xhtml")
,@URLMapping(id = "CadastrarPerfil", pattern = "/perfil/cadastrar", viewId = "/WEB-INF/pages/perfil/CadastrarPerfil.xhtml")
,@URLMapping(id = "AlterarPerfil", pattern = "/perfil/alterar/#{id}", viewId = "/WEB-INF/pages/perfil/CadastrarPerfil.xhtml")
,@URLMapping(id = "VisualizarPerfil", pattern = "/perfil/visualizar/#{id}", viewId = "/WEB-INF/pages/perfil/CadastrarPerfil.xhtml")
})
public class PerfilBean extends MetodosDePaginaBean {

	private LazyDataModel<Perfil> perfis;
	private Perfil perfil;
	private FiltroPerfil filtro;
	private List<Permissao> permissoesDoPerfil;	
	private DualListModel<Permissao> permissoesModel;	
	private Funcionalidade funcionalidadeSelecionada;	
	private Map<Funcionalidade, DualListModel<Permissao>> mapModel;
	
	@Inject
	private PerfilService service;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	@URLAction(onPostback = false, phaseId = PhaseId.RESTORE_VIEW)
	public void configurarFiltroPaginacao() throws Exception {
		super.carregarFiltroPaginacao(FiltroPerfil.class, this);
	}
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.perfil = new Perfil();
					buscarPermissoesDoPerfil();
					break;
			
				case VISUALIZAR:
				case ALTERAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarPerfil(id)) {
						facesUtil.exibirMensagemErro("Perfil não encontrado.", true);
						return "pretty:ListarPerfil";
					}

					buscarPermissoesDoPerfil();
					logDeAcesso(log, this.perfil);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Perfis");
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
	
	private boolean buscarPerfil(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.perfil = service.buscarPorId(id);
		
		if (this.perfil == null) {
			return false;
		}
		
		return true;
	}


	private void buscarPermissoesDoPerfil() {		
		if (this.perfil.getId() > 0) {
			this.permissoesDoPerfil = service.buscarPermissoesDoPerfil(this.perfil);
		} else {
			this.permissoesDoPerfil = new ArrayList<Permissao>();
		}
	}

	public void excluir(Perfil perfil) {
		try {
			service.excluir(perfil);
			facesUtil.exibirMensagemInfo("Perfil " + perfil.getNome() + " excluído com sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, perfil, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Erro", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir perfil (" + perfil.getNome() + "): "  + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, perfil, e, log);
		}
	}
	
	public String salvar() {
		String retorno = null;
		
		try {
			this.perfil.setPermissoes(this.permissoesDoPerfil);
			service.salvar(this.getPerfil());
			retorno = "pretty:ListarPerfil";
			facesUtil.exibirMensagemInfo("Perfil " + this.perfil.getNome() + " salvo com sucesso!", true);
			LoggerUtil.logInfo(Acao.SALVAR, this.perfil, log);
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Erro", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar perfil (" + this.perfil.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.perfil, e, log);
		}
		
		return retorno;	
	}
	
	public void pesquisar() {
		this.perfis = new LazyDataModel<Perfil>() {
			private static final long serialVersionUID = 1L;
			
			public List<Perfil> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				setRowCount(service.buscarQuantidadeFiltrada(getFiltro()));
				
				try {
					configuraPaginacao(getRowCount(), first, pageSize, sortField, sortOrder, getFiltro());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return service.buscarPerfisPorFiltro(getFiltro());
			}
		};
	}

	
	public void configurarFuncionalidade(ActionEvent event) {
		long id = (long) event.getComponent().getAttributes().get("idFuncionalidade");
		String nome = (String) event.getComponent().getAttributes().get("nomeFuncionalidade");
		
		this.funcionalidadeSelecionada = new Funcionalidade();
		this.funcionalidadeSelecionada.setId(id);
		this.funcionalidadeSelecionada.setNome(nome);
	}
	
	public void atualizarPermissoes(ActionEvent event) {
		this.mapModel.remove(this.funcionalidadeSelecionada);
		this.mapModel.put(this.funcionalidadeSelecionada, this.permissoesModel);
		
		List<Permissao> listaTeste = new ArrayList<Permissao>();
		
		for (Permissao permissao : this.permissoesDoPerfil) {
			if (permissao.getFuncionalidade().getId() == this.funcionalidadeSelecionada.getId()) {
				listaTeste.add(permissao);
			}
		}
		
		this.permissoesDoPerfil.removeAll(listaTeste);
		this.permissoesDoPerfil.addAll(this.permissoesModel.getTarget());
		
		this.permissoesModel = null;
	}
	
	public List<Permissao> permissoesPorFuncionalidade(long idFuncionalidade) {
		return configurarPermissoesPorFuncionalidade(idFuncionalidade);
	}
	
	private List<Permissao> configurarPermissoesPorFuncionalidade(long idFuncionalidade) {
		List<Permissao> lista = new ArrayList<Permissao>();
		
		for (Permissao permissao : this.permissoesDoPerfil) {
			if (permissao.getFuncionalidade().getId() == idFuncionalidade) {
				lista.add(permissao);
			}
		}
		
		return lista;
	}

	public LazyDataModel<Perfil> getPerfis() {
		return perfis;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
	public DualListModel<Permissao> getPermissoesModel() {		
		if (this.mapModel == null) {
			this.mapModel = new HashMap<Funcionalidade, DualListModel<Permissao>>();
		}
		
		DualListModel<Permissao> listaMapModel = this.mapModel.get(this.funcionalidadeSelecionada);
		boolean isMapModelNulo = (listaMapModel == null);
		
		if (!(isMapModelNulo)) {
			this.permissoesModel = listaMapModel;
			return this.permissoesModel;
		}
		
		try {
			this.permissoesModel = service.listarPermissoesModel(this.getPerfil(), this.funcionalidadeSelecionada.getId());
			this.mapModel.put(this.funcionalidadeSelecionada, this.permissoesModel);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar permissões: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return this.permissoesModel;
	}
	
	public void setPermissoesModel(DualListModel<Permissao> permissoes) {
		this.permissoesModel = permissoes;
	}

	public Funcionalidade getFuncionalidadeSelecionada() {
		return funcionalidadeSelecionada;
	}
	
 	public FiltroPerfil getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroPerfil filtro) {
		this.filtro = filtro;
	}
}
