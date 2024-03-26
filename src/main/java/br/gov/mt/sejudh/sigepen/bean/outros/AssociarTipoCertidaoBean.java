package br.gov.mt.sejudh.sigepen.bean.outros;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.primefaces.model.LazyDataModel;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.base.dataModel.AbstractDataModel;
import br.gov.mt.sejudh.sigepen.dao.filter.FiltroTipoImpedimento;
import br.gov.mt.sejudh.sigepen.model.Certidao;
import br.gov.mt.sejudh.sigepen.service.CertidaoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarTipoCertidao", pattern = "/associar/tipoCertidao", viewId = "/WEB-INF/pages/_dialogs/AssociarTipoCertidao.xhtml")
public class AssociarTipoCertidaoBean extends MetodosDialogBean {

	private Certidao certidao;

	private FiltroTipoImpedimento filtro;

	private LazyDataModel<Certidao> lazyDataModel;

	@Inject
	private CertidaoService service;
			
	@Inject
	private FacesUtil facesUtil;
	
	@URLAction(onPostback=false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		this.filtro = new FiltroTipoImpedimento();
		this.filtro.setSelecionarCertidao(true);
		
		this.certidao = (Certidao) facesUtil.getObjetoSessao("certidao");
		
		if (this.certidao == null) {
			this.certidao = new Certidao();
			
		}
		this.lazyDataModel = new AbstractDataModel<>(this.service, this.filtro, null);
		
		return null;
	}
	
	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public void selecionarCertidao(Certidao certidao) {
		facesUtil.fecharPaginaDialog(certidao);
	}

	public Certidao getCertidao() {
		return certidao;
	}

	public void setCertidao(Certidao certidao) {
		this.certidao = certidao;
	}

	public LazyDataModel<Certidao> getLazyDataModel() {
		return lazyDataModel;
	}

	public FiltroTipoImpedimento getFiltro() {
		return filtro;
	}
}
