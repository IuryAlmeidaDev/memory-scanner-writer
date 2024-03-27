package br.gov.mt.sejudh.sigepen.bean.outros;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import br.gov.mt.sejudh.sigepen.model.HorarioRefeicao;
import br.gov.mt.sejudh.sigepen.model.Refeicao;
import br.gov.mt.sejudh.sigepen.model.TipoRefeicao;
import br.gov.mt.sejudh.sigepen.service.TipoRefeicaoService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;

@ManagedBean
@ViewScoped
@URLMapping(id = "AssociarHorarioRefeicao", pattern = "/associar/HorarioRefeicao", viewId = "/WEB-INF/pages/_dialogs/AssociarHorarioRefeicao.xhtml")
public class AssociarHorarioRefeicaoBean extends MetodosDialogBean {
	
	private HorarioRefeicao horarioRefeicao;
	private List<TipoRefeicao> tiposRefeicao;
	
	@Inject
	private TipoRefeicaoService service;
	
	@Inject
	private FacesUtil facesUtil;
	
	@URLAction(onPostback = false)
	public String init() {
		if (this.isDialogInvalida()) {
			return "pretty:dashboard";
		}
		
		this.tiposRefeicao = service.listarTodos();

		this.horarioRefeicao = (HorarioRefeicao) facesUtil.getObjetoSessao("horarioRefeicao");

		if (this.horarioRefeicao == null) {
			this.horarioRefeicao = new HorarioRefeicao();
		}

		return null;
	}

	public void incluirHorarioRefeicao() {
		facesUtil.fecharPaginaDialog(horarioRefeicao);
	}

	public void fecharDialog(ActionEvent event) {
		facesUtil.fecharPaginaDialog(null);
	}

	public List<TipoRefeicao> getTiposRefeicao() {
		return tiposRefeicao;
	}

	public void setTiposRefeicao(List<TipoRefeicao> tiposRefeicao) {
		this.tiposRefeicao = tiposRefeicao;
	}

	public HorarioRefeicao getHorarioRefeicao() {
		return horarioRefeicao;
	}

	public void setHorarioRefeicao(HorarioRefeicao horarioRefeicao) {
		this.horarioRefeicao = horarioRefeicao;
	}
}
