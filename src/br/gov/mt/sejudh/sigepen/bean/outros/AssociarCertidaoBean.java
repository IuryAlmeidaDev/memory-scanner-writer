package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.model.Certidao;
import br.gov.mt.sejudh.sigepen.model.VisitanteCertidao;
import br.gov.mt.sejudh.sigepen.service.ComboService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarCertidao", pattern = "/associar/certidao", viewId = "/WEB-INF/pages/_dialogs/AssociarCertidao.xhtml")
public class AssociarCertidaoBean extends MetodosDialogBean {

	private VisitanteCertidao certidao;

    private List<Certidao> certidoes;
	
	@Inject
	private FacesUtil facesUtil;

    @Inject
    private ComboService comboService;
	
	@URLAction(onPostback=false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		this.certidao = (VisitanteCertidao) facesUtil.getObjetoSessao("certidao");
		
		if (this.certidao == null) {
			this.certidao = new VisitanteCertidao();
		}
		
		return null;
	}	

	public void incluirContato() {
		facesUtil.fecharPaginaDialog(certidao);
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

    public List<Certidao> getCertidoes() {
	    if (this.certidoes == null) {
            certidoes = this.comboService.getCertidoes();
        }
        return this.certidoes;
    }

    public VisitanteCertidao getCertidao() {
        return certidao;
    }

    public void setCertidao(VisitanteCertidao certidao) {
        this.certidao = certidao;
    }

}
