package br.gov.mt.sejudh.sigepen.bean;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.gov.mt.sejudh.sigepen.dao.filter.FiltroPessoa;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.TipoDocumento;
import br.gov.mt.sejudh.sigepen.model.dto.PessoaDTO;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.service.DocumentoService;
import br.gov.mt.sejudh.sigepen.service.EnderecoService;
import br.gov.mt.sejudh.sigepen.service.PessoaService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;
import br.gov.mt.sejudh.sigepen.util.Sigepen;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarPessoa", pattern = "/pessoa", viewId = "/WEB-INF/pages/pessoa/ListarPessoa.xhtml")
,@URLMapping(id = "CadastrarPessoa", pattern = "/pessoa/cadastrar", viewId = "/WEB-INF/pages/pessoa/CadastrarPessoa.xhtml")
,@URLMapping(id = "AlterarPessoa", pattern = "/pessoa/alterar/#{id}", viewId = "/WEB-INF/pages/pessoa/CadastrarPessoa.xhtml")
,@URLMapping(id = "VisualizarPessoa", pattern = "/pessoa/visualizar/#{id}", viewId = "/WEB-INF/pages/pessoa/CadastrarPessoa.xhtml")
})
public class PessoaBean extends MetodosDePaginaBean {

	private FiltroPessoa filtro;

	private Pessoa pessoa;

	private LazyDataModel<PessoaDTO> pessoas;

	private List<Cidade> cidades;

	private Estado estado;

	private Boolean renderPais = false;
	
	private Date dataMinima;
	
	private Documento doc;
	
	private Endereco endereco;

	@Inject
	private PessoaService service;
	
	@Inject
	private EnderecoService enderecoService;
	
	@Inject
	private DocumentoService documentoService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private ComboService comboService;
	
	@Inject
	private Logger log;

	@URLAction(onPostback=false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
				case CADASTRAR:
					this.pessoa = new Pessoa();
					break;
					
				case VISUALIZAR:
				case ALTERAR:
					long id = Sigepen.converteStringEmLong(facesUtil.getQueryParameter("id"));
					
					if (!buscarPessoa(id)) {
						facesUtil.exibirMensagemErro("Pessoa não encontrada.", true);
						return "pretty:ListarPessoa";
					}
					
					if (this.pessoa.getNaturalidade() != null) {
						this.estado = this.pessoa.getNaturalidade().getEstado();
						this.cidades = comboService.listarCidade(this.estado);
					}
					
					logDeAcesso(log, this.pessoa);
					break;
					
				case LISTAR:
					pesquisar();
					logDeAcesso(log, "Pessoas");
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
	
	private boolean buscarPessoa(long id) {
		if (id <= 0) {
			return false;
		}
		
		this.pessoa = service.buscarPessoaPorId(id);
		
		if (this.pessoa == null) {
			return false;
		}
		
		return true;
	}


	public void listarCidades(AjaxBehaviorEvent event) {
		try {
			cidades = comboService.listarCidade(estado);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar cidades: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public void listaPaises(AjaxBehaviorEvent event) {
		// 30 é o ID do Brasil
		if (this.pessoa.getPais().getId() != 30) {
			this.setRenderPais(true);
		} else {
			this.setRenderPais(false);
		}
	}

	public String salvar() {
		String retorno = null;
		
		try {
			service.salvar(this.pessoa);
			facesUtil.exibirMensagemInfo("Pessoa " + this.pessoa.getNome() + " salva com sucesso!", true);
			retorno = "pretty:ListarPessoa";
			LoggerUtil.logInfo(Acao.SALVAR, this.pessoa, log);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao salvar pessoa (" + this.pessoa.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.SALVAR, this.pessoa, e, log);
		}
		
		return retorno;
	}

	public void excluir(PessoaDTO pessoa) {
		try {
			service.excluir(pessoa.getId());
			facesUtil.exibirMensagemInfo("Pessoa " + pessoa.getNome() + " excluída com Sucesso!", false);
			LoggerUtil.logInfo(Acao.EXCLUIR, pessoa, log);
		} catch(ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir pessoa (" + pessoa.getNome() + "): " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, pessoa, e, log);
		}
	}

	public void pesquisar() {
		this.setFiltro(new FiltroPessoa());
		this.pessoas = new LazyDataModel<PessoaDTO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<PessoaDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
				setRowCount(service.quantidadeFiltrados(getFiltro()));
				
				if (first >= getRowCount()) {
					first = 0;                    
					DataTable dt = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("registrosForm:dataTable");
					dt.setFirst(first);
				}

				getFiltro().organizaFiltro(first, pageSize, sortField, sortOrder);

				return service.buscaPorFiltro(getFiltro());
			}
		};
	}
	
	public boolean pessoaEhEstrangeira() {
		if (this.pessoa.getNacionalidade() == null) {
			return false;
		}
		
		// 1 é o ID de Nacionalidade brasileiro nato
		return (this.pessoa.getNacionalidade().getId() != 1);
	}
	
	public void alterarNacionalidade(){
		if(!this.pessoaEhEstrangeira()){
			this.pessoa.setPais(null);
		}else {
			this.pessoa.setNaturalidade(null);
		}
	}
	
	public void abrirDialogDocumento(ActionEvent event) {
		this.doc = null;
		facesUtil.exibirPaginaDialog("AssociarDocumento");
	}
	
	public void abrirDialogAlterarDocumento(Documento documento) {
		this.doc = new Documento(documento);
		facesUtil.setObjetoSessao("documento", documento);		
		facesUtil.exibirPaginaDialog("AssociarDocumento");
		LoggerUtil.logInfo(Acao.ALTERAR, documento, log);
	}
	
	public void excluirDocumento(Documento documento) {
		try {
			this.pessoa.getDocumentos().remove(documento);
			if(this.pessoa.getId()>0){
				documentoService.excluirDocumento(documento);
				LoggerUtil.logInfo(Acao.EXCLUIR, documento, log);
			}else {
				LoggerUtil.logInfo(Acao.REMOVER, documento, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir documento: " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, documento, e, log);
		}
	}
	
	public void incluirDocumento(SelectEvent event) {
		Documento documento = (Documento) event.getObject();
		
		try {
			if (documento != null) {
				if (this.doc == null) {
					if (jaPossuiTipoDocumento(this.pessoa.getDocumentos(),documento.getTipoDocumento())) {
						throw new ValidacaoException("Esta pessoa ja possui este tipo de Documento!");					
					} else {
						documento.setPessoa(this.pessoa);
						this.pessoa.getDocumentos().add(documento);
					}
				} else {
					this.pessoa.getDocumentos().remove(documento);
					
					if (jaPossuiTipoDocumento(this.pessoa.getDocumentos(),	documento.getTipoDocumento())) {
						this.pessoa.getDocumentos().add(this.doc);
						throw new ValidacaoException("Esta pessoa ja possui este tipo de Documento!");
					} else {						
						this.pessoa.getDocumentos().add(documento);
						}
			}
				if(this.pessoa.getId()>0){
					documentoService.salvarDocumento(documento);
					LoggerUtil.logInfo(Acao.SALVAR, documento, log);
				}else {
					LoggerUtil.logInfo(Acao.ADICIONAR, documento, log);
				}
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir documento: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, documento, e, log);
		}
	}
	
	public boolean jaPossuiTipoDocumento(List<Documento> documentos, TipoDocumento tipoDoc){
		for(Documento doc: documentos){
			if(doc.getTipoDocumento().equals(tipoDoc) ){
				return true;				
			}
		}		
		return false;
	}
	
	public void abrirDialogEndereco(ActionEvent event) {
		this.endereco = null;
		facesUtil.exibirPaginaDialog("AssociarEndereco");
	}
	
	public void abrirDialogAlterarEndereco(Endereco endereco) {
		this.endereco = endereco;
		facesUtil.setObjetoSessao("endereco", endereco);		
		facesUtil.exibirPaginaDialog("AssociarEndereco");
		LoggerUtil.logInfo(Acao.ALTERAR, endereco, log);
	}
	
	public void excluirEndereco(Endereco endereco) {
		try {
			this.pessoa.getEnderecos().remove(endereco);
			if(this.pessoa.getId()>0){
				enderecoService.excluirEndereco(endereco);
				LoggerUtil.logInfo(Acao.EXCLUIR, endereco, log);
			}else {
				LoggerUtil.logInfo(Acao.REMOVER, endereco, log);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir endereço: " + e.getMessage());
			LoggerUtil.logErro(Acao.EXCLUIR, endereco, e, log);
		}
	}
	
	public void incluirEndereco(SelectEvent event) {
		Endereco endereco = (Endereco) event.getObject();
		
		try {
			if (endereco != null) {				
				if (this.endereco != null) {
					this.pessoa.getEnderecos().remove(this.endereco);
					this.pessoa.getEnderecos().add(endereco);
				} else {
					endereco.setPessoa(this.pessoa);
					this.pessoa.getEnderecos().add(endereco);
				}
				if(this.pessoa.getId()>0){
					enderecoService.salvarEndereco(endereco);
					LoggerUtil.logInfo(Acao.SALVAR, endereco, log);	
				}else {
					LoggerUtil.logInfo(Acao.ADICIONAR, endereco, log);	
				}
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir endereço: " + e.getMessage());
			LoggerUtil.logErro(Acao.ADICIONAR, endereco, e, log);
		}
	}

	public FiltroPessoa getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroPessoa filtro) {
		this.filtro = filtro;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public LazyDataModel<PessoaDTO> getPessoas() {
		return pessoas;
	}

	public void setPessoas(LazyDataModel<PessoaDTO> pessoas) {
		this.pessoas = pessoas;
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	public Boolean getRenderPais() {
		return renderPais;
	}

	public void setRenderPais(Boolean renderPais) {
		this.renderPais = renderPais;
	}
	
	public Date getDataMinima() {
		dataMinima = new Date();		
		return dataMinima;
	}
}
