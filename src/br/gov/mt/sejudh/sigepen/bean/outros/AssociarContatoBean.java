package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import br.gov.mt.sejudh.sigepen.enumerator.TipoContato;
import br.gov.mt.sejudh.sigepen.model.Contato;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarContato", pattern = "/associar/contato", viewId = "/WEB-INF/pages/_dialogs/AssociarContato.xhtml")
public class AssociarContatoBean extends MetodosDialogBean {

	private Contato contato;
	
	private String email;
	
	private String telefone;
			
	@Inject
	private FacesUtil facesUtil;
	
	@URLAction(onPostback=false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		this.contato = (Contato) facesUtil.getObjetoSessao("contato");
		
		if (this.contato == null) {
			this.contato = new Contato();
			
		} else if (this.contato.getTipoContato() == TipoContato.CELULAR || this.contato.getTipoContato() == TipoContato.FIXO) {
			setTelefone(this.contato.getValor());				
		} else {
			setEmail(this.contato.getValor());
		}
		
		return null;
	}	

	public void incluirContato() {
		if(this.contato.getTipoContato() == TipoContato.EMAIL){
			contato.setValor(email);
		}else {
			contato.setValor(telefone);
		}
		facesUtil.fecharPaginaDialog(contato);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public Contato getContato() {
		return contato;
	}


	public void setContato(Contato contato) {
		this.contato = contato;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
}
