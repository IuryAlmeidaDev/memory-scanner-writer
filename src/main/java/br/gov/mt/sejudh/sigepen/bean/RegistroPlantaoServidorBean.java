package br.gov.mt.sejudh.sigepen.bean;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import org.apache.logging.log4j.Logger;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;
import br.gov.mt.sejudh.sigepen.model.Atividade;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantao;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantaoServidor;
import br.gov.mt.sejudh.sigepen.model.dto.RegistroPlantaoServidorDTO;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.service.RegistroPlantaoService;
import br.gov.mt.sejudh.sigepen.service.RegistroPlantaoServidorService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "CadastrarRegistroPlantao", pattern = "/plantao/listarRegistro", viewId = "/WEB-INF/pages/plantao/CadastrarRegistroPlantao.xhtml")
})
public class RegistroPlantaoServidorBean extends MetodosDePaginaBean {

	
	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;
	
	private List<ServidorDTO> servidores;	
	private Atividade atividade;	
	
	
	private RegistroPlantaoServidor registroPlantaoServidor;	
	
	
	@Inject
	private RegistroPlantaoServidorService registroPlantaoServidorService;
	
	@Inject
	RegistroPlantaoService registroPlantaoService; 
	
	
    private RegistroPlantaoServidorDTO registroPlantaoServidorDTO = new RegistroPlantaoServidorDTO();

		
	
	@URLAction(onPostback = false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.registroPlantaoServidor = new RegistroPlantaoServidor();
				break;

			case ALTERAR:
			case VISUALIZAR:								

				logDeAcesso(log, this.registroPlantaoServidor);
				break;

			case LISTAR:				
				logDeAcesso(log, "Registro Plantão Servidor");
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
	
	
	
	
	/* ------------------------- Métodos ------------------------- */
	
	
	public void salvar() {
	    try {
	    	
	    	RegistroPlantao ultimoRegistro = registroPlantaoService.buscarUltimoRegistro();
	    	if (ultimoRegistro == null) {
	    	    facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar equipe", "Nenhum registro encontrado.");
	    	    return;
	    	}
	    	
	    	Long registroPlantaoId = ultimoRegistro.getId();
	    	
	         
	        for (ServidorDTO dto : registroPlantaoServidorDTO.getEquipeServidores()) {
	        	
	            RegistroPlantaoServidor equipe = new RegistroPlantaoServidor();
	            
	            //equipe.setNomeServidor(registroPlantaoServidorDTO.getNomeServidor());
	            //equipe.setAtividadeSelecionada(registroPlantaoServidorDTO.getAtividadeSelecionada());	           
	            equipe.setSituacaoSelecionada(registroPlantaoServidorDTO.getSituacaoSelecionada());	            
	            equipe.setRegistroPlantaoId(registroPlantaoId);
	            	           
	            registroPlantaoServidorService.salvar(equipe);
	            
	          
	        }
	    } catch (Exception e) {
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar equipe", e.getMessage());
	        LoggerUtil.logErro(e, log);
	    }
	}


		
	
	
	/* ------------------------- Get & Setts ------------------------- */
	
	public List<ServidorDTO> getServidores() {
	    return servidores;
	}

	public void setServidores(List<ServidorDTO> servidores) {
	    this.servidores = servidores;
	}

	public Atividade getAtividade() {
	    return atividade;
	}

	public void setAtividade(Atividade atividade) {
	    this.atividade = atividade;
	}

	public RegistroPlantaoServidor getRegistroPlantaoServidor() {
	    return registroPlantaoServidor;
	}

	public void setRegistroPlantaoServidor(RegistroPlantaoServidor registroPlantaoServidor) {
	    this.registroPlantaoServidor = registroPlantaoServidor;
	}

	public RegistroPlantaoServidorDTO getRegistroPlantaoServidorDTO() {
	    return registroPlantaoServidorDTO;
	}

	public void setRegistroPlantaoServidorDTO(RegistroPlantaoServidorDTO registroPlantaoServidorDTO) {
	    this.registroPlantaoServidorDTO = registroPlantaoServidorDTO;
	}

	
	
	

}
