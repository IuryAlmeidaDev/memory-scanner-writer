package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import br.gov.mt.sejudh.sigepen.model.Atividade;
import br.gov.mt.sejudh.sigepen.service.AtividadeService;



@ManagedBean
@ViewScoped
public class AtividadeBean extends MetodosDePaginaBean {

	private List<Atividade> atividades;	

	@Inject
	private AtividadeService atividadeService;

	
	@PostConstruct
	public void inicializar() {
		atividades = atividadeService.listarTodasAtividades(); 
		
	}

	
	
	
	/* --------------------------- get & setts -------------------------------------- */

	public List<Atividade> getAtividades() {
		return atividades;
	}

	public void setAtividades(List<Atividade> atividades) {
		this.atividades = atividades;
	}
	

}
