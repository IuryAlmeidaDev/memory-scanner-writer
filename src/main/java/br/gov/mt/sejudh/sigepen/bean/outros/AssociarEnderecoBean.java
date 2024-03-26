package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;

import br.gov.mt.sejudh.sigepen.model.Cidade;
import br.gov.mt.sejudh.sigepen.model.Endereco;
import br.gov.mt.sejudh.sigepen.model.Estado;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarEndereco", pattern = "/associar/endereco", viewId = "/WEB-INF/pages/_dialogs/AssociarEndereco.xhtml")
public class AssociarEnderecoBean extends MetodosDialogBean {

	private Endereco endereco;
	
	private Estado estado;
	
	private List<Cidade> cidades;
	
	@Inject
	private ComboService comboService;
	
	@Inject
	private Logger log;
			
	@Inject
	private FacesUtil facesUtil;
	
	@URLAction(onPostback=false)
	public String init() {
		try {
			if (this.isDialogInvalida()) {
				return "pretty:dashboard";
			}
			
			this.endereco = (Endereco) facesUtil.getObjetoSessao("endereco");
			
			if (this.endereco == null) {
				this.endereco = new Endereco();
			} else {
				this.estado = this.endereco.getCidade().getEstado();
				this.cidades = comboService.listarCidade(estado);
			}
		} catch (Exception e){
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", e.getMessage());
			LoggerUtil.logErro(e, log);
		}
		
		return null;
	}
	
	public void listaCidades(AjaxBehaviorEvent event) {
		try {
			this.cidades = comboService.listarCidade(estado);
		} catch (Exception e) {
			facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO", "Erro ao listar cidades: " + e.getMessage());
			LoggerUtil.logErro(e, log);
		}
	}

	public void incluirEndereco() {	
		facesUtil.fecharPaginaDialog(endereco);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
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
	
}
