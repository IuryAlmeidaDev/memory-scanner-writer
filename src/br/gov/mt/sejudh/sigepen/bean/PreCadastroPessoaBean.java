package br.gov.mt.sejudh.sigepen.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Documento;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.model.Pessoa;
import br.gov.mt.sejudh.sigepen.model.TipoDocumento;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.service.DocumentoService;
import br.gov.mt.sejudh.sigepen.service.EnderecoService;
import br.gov.mt.sejudh.sigepen.service.RecuperandoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
		@URLMapping(id = "PreCadastroPessoaCadastrar", pattern = "/pre-cadastro/cadastrar", viewId = "/WEB-INF/pages/_dialogs/PreCadastroPessoa.xhtml"),
		@URLMapping(id = "PreCadastroPessoaVisualizar", pattern = "/pre-cadastro/visualizar", viewId = "/WEB-INF/pages/_dialogs/PreCadastroPessoa.xhtml"),
		@URLMapping(id = "PreCadastroPessoaAlterar", pattern = "/pre-cadastro/alterar", viewId = "/WEB-INF/pages/_dialogs/PreCadastroPessoa.xhtml")
})
public class PreCadastroPessoaBean extends MetodosDePaginaBean {
	
	@Inject
	private ComboService comboService;
	
	@Inject
	private EnderecoService enderecoService;
	
	@Inject
	private DocumentoService documentoService;
	
	@Inject
	private RecuperandoService recuperandoService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private Logger log;
	
	private Pessoa pessoa;
	
	private Estado estado;
	
	private List<Cidade> cidades;
	
	private Documento documento;
	
	private Documento docAux;
	
	private Endereco endereco;
	
	private Endereco endAux;
	
	private Estado estadoEndereco;
	
	private List<Cidade> cidadesEndereco;
	
	private Date dataMinima;
	
	private boolean skip;
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			this.pessoa = ((Pessoa) facesUtil.getObjetoSessao("pessoa"));
			
			switch(this.getAcaoDeTela()) {
				case CADASTRAR:
					if (this.isDialogInvalida()) {
						return "pretty:dashboard";
					}
					
					if (this.pessoa.getId() <= 0 && this.pessoa.getNome() == null) {
						this.pessoa = new Pessoa();
					} else {
						inicializarVariaveis();
					}
					
					this.documento = new Documento();
					this.endereco = new Endereco();
					break;
					
				case VISUALIZAR:
				case ALTERAR:					
					if (this.pessoa == null || this.isDialogInvalida()) {
						return "pretty:dashboard";
					}
					
					inicializarVariaveis();
					break;
			
				default:
					return "pretty:dashboard";
			}
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
			e.printStackTrace();
		}
		
		return null;
	}

	private void inicializarVariaveis() {
		if(!this.pessoaEhEstrangeira()) {
			this.estado = this.pessoa.getNaturalidade().getEstado();
			this.cidades = comboService.listarCidade(this.estado);
		}
	}
	
	public void cadastrarPessoa() {
		try {
			facesUtil.fecharPaginaDialog(this.pessoa);
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "ERRO", e.getMessage());
		}
	}	
	
	private boolean validarCadastro() {
		try {
			Calendar data = Calendar.getInstance();
			
			if (this.pessoa.getDataNascimento().after(data.getTime())) {
				throw new ValidacaoException("Data de nascimento não pode ser maior que a data atual.");
			}
			
			data.add(Calendar.YEAR, -18);
			if (this.pessoa.getDataNascimento().after(data.getTime())) {
				throw new ValidacaoException("Data de nascimento é menor que 18 anos. Corrija para poder continuar o cadastro.");
			}
			
			data = Calendar.getInstance();
			data.add(Calendar.YEAR, -100);
			if (this.pessoa.getDataNascimento().before(data.getTime())) {
				throw new ValidacaoException("Data de nascimento não pode ser maior que 100 anos.");
			}
			
			Long idRecup = recuperandoJaExisteNoSistema();
			
			if (idRecup != null) {
				throw new ValidacaoException("O Recuperando " + this.pessoa.getNome() + ", matrícula " + idRecup + " já existe no sistema.");
			}
			
			return true;
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
			RequestContext.getCurrentInstance().update("formDados:tabDados");
			
			return false;
		}
	}
	
	private Long recuperandoJaExisteNoSistema() {
		return recuperandoService.recuperandoJaExisteNoSistema(this.pessoa);
	}
	
	/*
	 * Listeners
	 */

	public String onFlowProcess(FlowEvent event) {
		if (!validarCadastro()) {
			return event.getOldStep();
		}
		
		List<String> exibeBotaoVoltar = new ArrayList<String>();
		exibeBotaoVoltar.add("tabDocumentos");
		exibeBotaoVoltar.add("tabEnderecos");
		
		String ultimaTab = "tabEnderecos";
		
		CommandButton btnBack = (CommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("formBotoes:btnBack");
		CommandButton btnNext = (CommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("formBotoes:btnNext");
		CommandButton btnContinue = (CommandButton)FacesContext.getCurrentInstance().getViewRoot().findComponent("formBotoes:btnContinue");
		
		btnBack.setRendered(exibeBotaoVoltar.contains(event.getNewStep()));
		btnNext.setRendered(!event.getNewStep().equals(ultimaTab));
		btnContinue.setRendered(event.getNewStep().equals(ultimaTab) && !this.getIsVisualizacao());
		
		RequestContext.getCurrentInstance().update("formBotoes");
		
		if(skip) {
			skip = false;
			return "confirm";
		}
		else {
			return event.getNewStep();
		}
	}
	
	public void fecharDialog(ActionEvent event) {
		if (this.pessoa.getNome() == null) {
			facesUtil.fecharPaginaDialog(null);
		} else {
			facesUtil.fecharPaginaDialog(pessoa);
		}
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
			this.setEstado(null);
			this.pessoa.setNaturalidade(null);
		}
	}
	
	public void listarCidades() {
		try {
			cidades = comboService.listarCidade(estado);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar cidades: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void listarCidadesEndereco() {
		try {
			cidadesEndereco = comboService.listarCidade(estadoEndereco);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar cidades: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void novoDocumento() {
		this.documento = new Documento();
		this.docAux = null;
	}
	
	public void alterarDocumento(Documento doc) {
		this.docAux = new Documento(doc);
		this.documento = doc;
	}
	
	public void excluirDocumento(Documento documento) {
		try {
			this.pessoa.getDocumentos().remove(documento);
			if(this.pessoa.getId()>0){
				documentoService.excluirDocumento(documento);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir documento: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void incluirDocumento() {
		try {			
			if (this.documento != null) {
				if (this.docAux == null) {
					if (jaPossuiTipoDocumento(this.pessoa.getDocumentos(),documento.getTipoDocumento())) {
						throw new ValidacaoException("Esta pessoa já possui este tipo de Documento!");					
					} else {
						documento.setPessoa(this.pessoa);
						this.pessoa.getDocumentos().add(documento);
					}
				} else {
					this.pessoa.getDocumentos().remove(documento);
					
					if (jaPossuiTipoDocumento(this.pessoa.getDocumentos(),	documento.getTipoDocumento())) {
						this.pessoa.getDocumentos().add(this.docAux);
						throw new ValidacaoException("Esta pessoa já possui este tipo de Documento!");
					} else {						
						this.pessoa.getDocumentos().add(documento);
						}
			}
				if(this.pessoa.getId()>0){
					documentoService.salvarDocumento(documento);
				}
				
				RequestContext.getCurrentInstance().execute("PF('novoDocumento').hide()");	
			}
		} catch (ValidacaoException e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir documento: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public boolean jaPossuiTipoDocumento(List<Documento> documentos, TipoDocumento tipoDoc){
		for(Documento doc: documentos){
			if(doc.getTipoDocumento().equals(tipoDoc)){
				return true;				
			}
		}		
		return false;
	}
	
	public void novoEndereco() {
		this.endereco = new Endereco();
		this.endAux = null;
		this.estadoEndereco = null;
		this.cidadesEndereco = null;
	}
	
	public void alterarEndereco(Endereco end) {
		try {
			this.endAux = new Endereco(end);
			this.endereco = end;
			this.estadoEndereco = end.getCidade().getEstado();
			this.cidadesEndereco = comboService.listarCidade(this.estadoEndereco);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void incluirEndereco() {
		try {
			if (this.endereco != null) {
				
				if (this.endAux == null) {
					endereco.setPessoa(this.pessoa);
					this.pessoa.getEnderecos().add(endereco);
				} else {
					pessoa.getEnderecos().remove(this.endereco);
					pessoa.getEnderecos().add(this.endereco);
				}
				if(this.pessoa.getId()>0){
					enderecoService.salvarEndereco(endereco);
				}
				RequestContext.getCurrentInstance().execute("PF('novoEndereco').hide()");
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao incluir endereço: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	public void excluirEndereco(Endereco endereco) {
		try {
			this.pessoa.getEnderecos().remove(endereco);
			if(this.pessoa.getId()>0){
				enderecoService.excluirEndereco(endereco);
			}
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao excluir endereço: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}
	
	/*
	 * Getters e Setters
	 */

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Estado getEstadoEndereco() {
		return estadoEndereco;
	}

	public void setEstadoEndereco(Estado estadoEndereco) {
		this.estadoEndereco = estadoEndereco;
	}

	public List<Cidade> getCidadesEndereco() {
		return cidadesEndereco;
	}

	public void setCidadesEndereco(List<Cidade> cidadesEndereco) {
		this.cidadesEndereco = cidadesEndereco;
	}

	public Date getDataMinima() {
		dataMinima = new Date();		
		return dataMinima;
	}	
}
