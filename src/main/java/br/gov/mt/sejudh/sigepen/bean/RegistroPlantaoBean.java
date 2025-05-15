package br.gov.mt.sejudh.sigepen.bean;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import br.gov.mt.sejudh.sigepen.enumerator.Acao;
import br.gov.mt.sejudh.sigepen.enumerator.SituacaoPlantao;
import br.gov.mt.sejudh.sigepen.enumerator.Status;
import br.gov.mt.sejudh.sigepen.enumerator.TipoPlantao;
import br.gov.mt.sejudh.sigepen.exception.ValidacaoException;
import br.gov.mt.sejudh.sigepen.model.Atividade;
import br.gov.mt.sejudh.sigepen.model.Plantao;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantao;
import br.gov.mt.sejudh.sigepen.model.RegistroPlantaoServidor;
import br.gov.mt.sejudh.sigepen.model.dto.RegistroPlantaoServidorDTO2;
import br.gov.mt.sejudh.sigepen.model.dto.ServidorDTO;
import br.gov.mt.sejudh.sigepen.service.AtividadeService;
import br.gov.mt.sejudh.sigepen.service.PlantaoService;
import br.gov.mt.sejudh.sigepen.service.RegistroPlantaoService;
import br.gov.mt.sejudh.sigepen.service.ServidorService;
import br.gov.mt.sejudh.sigepen.util.FacesUtil;
import br.gov.mt.sejudh.sigepen.util.LoggerUtil;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
@URLMapping(id = "ListarRegistroPlantao", pattern = "/plantao/listarRegistro", viewId = "/WEB-INF/pages/plantao/ListarRegistroPlantao.xhtml")
})
public class RegistroPlantaoBean extends MetodosDePaginaBean {

	
	@Inject
	private FacesUtil facesUtil;

	@Inject
	private Logger log;
	
	
	private List<Atividade> atividades;	
	private List<Plantao> plantoes;	
	private List<ServidorDTO> servidores;
	
	RegistroPlantaoServidor registroPlantaoServidor;
	private RegistroPlantao registroPlantao;		
	private SituacaoPlantao situacaoSelecionada;
	
	private Atividade atividade;
	private Atividade atividadeSelecionada;
		
	private RegistroPlantaoServidorDTO2 servidorSelecionado = new RegistroPlantaoServidorDTO2();
			
	
	@Inject
	private PlantaoService plantaoService;
	
	@Inject
	private RegistroPlantaoService registroPlantaoService;
	
	@Inject
	private AtividadeService atividadeService;
	
	@Inject
	private ServidorService servidorService;
	
	private List<RegistroPlantaoServidorDTO2> listaGestao = new ArrayList<RegistroPlantaoServidorDTO2>();
	private List<RegistroPlantaoServidorDTO2> listaPlantao = new ArrayList<RegistroPlantaoServidorDTO2>();
	private List<RegistroPlantaoServidorDTO2> listaAdministrativa = new ArrayList<RegistroPlantaoServidorDTO2>();
	
	
	
	@PostConstruct
	public void inicializar() {
		if (registroPlantao == null || registroPlantao.getId() == null) {
		    registroPlantao = new RegistroPlantao();		    
		}
		
	    listaEquipe();	   	    
	    plantoes = registroPlantaoService.listarPlantaoPorStatus();   
	}
	

	
	@URLAction(onPostback = false)
	public String init() {
		try {
			switch (this.getAcaoDeTela()) {
			case CADASTRAR:
				this.registroPlantao = new RegistroPlantao();
				break;

			case ALTERAR:
			case VISUALIZAR:								

				logDeAcesso(log, this.registroPlantao);
				break;

			case LISTAR:				
				logDeAcesso(log, "Registro Plantoes");
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
	
	
	private void listaPlantao() {
		Long tipo = 1L;
	    servidores = servidorService.listarServidoresComPlantaoEmLotacao();	    
	    atividades = atividadeService.listarTodasAtividadesPorTipo(tipo);
	    
	    servidores.forEach(s -> {	  
	    	
	    RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	    
	    	dto.setNomeServidor(s.getNome());	    	       	       
	    	dto.setAtividades(atividades);		    	
	    	listaPlantao.add(dto);
	    });
	}
	
	private void listaGestao() {
		Long tipo = 2L;
	    servidores = servidorService.listarServidoresComPlantaoEmLotacao();	    
	    atividades = atividadeService.listarTodasAtividadesPorTipo(tipo);
	    
	    servidores.forEach(s -> {	  
	    	
	    RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	    
	    	dto.setNomeServidor(s.getNome());	
	    	dto.setMatricula(s.getMatricula());	    
	    	dto.setAtividades(atividades);		    	
	    	listaGestao.add(dto);
	    });
	}
	
	private void listaAdministrativa() {			
		Long tipo = 3L;
	    servidores = servidorService.listarServidoresComPlantaoEmLotacao();	    
	    atividades = atividadeService.listarTodasAtividadesPorTipo(tipo);
	    
	    servidores.forEach(s -> {	  
	    	
	    RegistroPlantaoServidorDTO2 dto = new RegistroPlantaoServidorDTO2();
	    
	    	dto.setNomeServidor(s.getNome());	    	    	       
	    	dto.setAtividades(atividades);		    	
	    	listaAdministrativa.add(dto);
	    });
	}
	
	
	private void listaEquipe() {				   	       	   
	    listaGestao(); 
	    listaPlantao();
	    listaAdministrativa();
	}
		
	
	public String salvarEquipe(List<RegistroPlantaoServidorDTO2> itemLista) {
	    String retorno = null;
	    boolean algumSalvo = false;

	    RegistroPlantao ultimoRegistro = registroPlantaoService.buscarUltimoRegistro();
	    if (ultimoRegistro == null) {
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar equipe", "Nenhum registro encontrado.");
	        return null;
	    }

	    Long registroPlantaoId = ultimoRegistro.getId();

	    for (RegistroPlantaoServidorDTO2 dto : itemLista) {
	        if (dto.getNomeServidor() != null) {

	            boolean registroExistente = registroPlantaoService.existeRegistro(dto.getNomeServidor(), dto.getDataRegistroPlantaoServidor());

	            if (!registroExistente) {
	                RegistroPlantaoServidor equipe = new RegistroPlantaoServidor();

	                equipe.setNomeServidor(dto.getNomeServidor());
	                equipe.setAtividadeSelecionada(dto.getAtividadeSelecionada());
	                equipe.setSituacaoSelecionada(dto.getSituacaoSelecionada());

	                equipe.setDataRegistroPlantaoServidor(
	                    Date.from(dto.getDataRegistroPlantaoServidor()
	                        .atStartOfDay(ZoneId.systemDefault()).toInstant()));

	                equipe.setRegistroPlantaoId(registroPlantaoId);
	                equipe.setOcorrenciaSelecionada(dto.getOcorrenciaSelecionada() != null ? dto.getOcorrenciaSelecionada() : null);
	                equipe.setJustificativa(dto.getJustificativa() != null ? dto.getJustificativa() : null);

	                registroPlantaoService.salvarEquipePlantao(equipe);
	                algumSalvo = true;

	            } else {
	                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "O servidor " + dto.getNomeServidor() + " já tem um registro para esta data.");
	            }

	        } else {
	            facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "Dados incompletos para alguns servidores.");
	        }
	    }

	    if (algumSalvo) {
	        facesUtil.exibirMensagemInfo("Registro Plantão " + ultimoRegistro.getId() + " salvo com sucesso!", true);
	        retorno = "pretty:ListarRegistroPlantao";
	    } else {
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Nenhum registro foi salvo. Verifique se os dados não são duplicados ou incompletos.");
	    }

	    return retorno;
	}

	
		
	public String salvar() {
	    String retorno = null;

	    try {
	    	
	    	RegistroPlantao ultimoRegistro = registroPlantaoService.buscarUltimoRegistroPlantaoComPlantao();
	    	
	    	 if (ultimoRegistro != null && ultimoRegistro.getPlantao() != null) {
	             Plantao plantaoAnterior = ultimoRegistro.getPlantao();
	             plantaoAnterior.setStatus(Status.INATIVO);
	             plantaoService.salvar(plantaoAnterior);
	         }
	    	
	        registroPlantaoService.salvar(this.registroPlantao);
	        facesUtil.exibirMensagemInfo("Registro Plantão " + this.registroPlantao.getId() + " salvo com sucesso!", true);
	        retorno = "pretty:ListarRegistroPlantao";	       
	        LoggerUtil.logInfo(Acao.SALVAR, this.registroPlantao, log);

	    } catch (ValidacaoException e) {	        
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "ERRO", e.getMessage());

	    } catch (Exception e) {	        
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "ERRO",
	                "Erro ao salvar plantao (" + this.registroPlantao.getId() + "): " + e.getMessage());	        
	        LoggerUtil.logErro(Acao.SALVAR, this.registroPlantao, e, log);
	    }

	    return retorno;
	}

	
	
	public void prepararEdicao(RegistroPlantaoServidorDTO2 servidor) {
	    this.servidorSelecionado = servidor;
	}
	
	
	public void atualizarRegistroPlantaoServidor() {
	    try {
	        if (servidorSelecionado.getNomeServidor() != null) {
	        	RegistroPlantaoServidor registroPlantaoServidor = 
	        			registroPlantaoService.buscarPorNome(servidorSelecionado.getNomeServidor());

	            if (registroPlantaoServidor != null) {	                	            	 
	                 
	                 registroPlantaoServidor.setOcorrenciaSelecionada(servidorSelecionado.getOcorrenciaSelecionada());
	                 registroPlantaoServidor.setJustificativa(servidorSelecionado.getJustificativa());   

	                registroPlantaoService.atualizar(registroPlantaoServidor);
	                
	                facesUtil.exibirMensagemInfo("Registro atualizado com sucesso!", true);
	            } else {
	                facesUtil.exibirMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "Registro não encontrado para o servidor: " + servidorSelecionado.getNomeServidor());
	            }
	        }
	    } catch (Exception e) {
	        facesUtil.exibirMensagem(FacesMessage.SEVERITY_FATAL, "Erro ao atualizar registro", e.getMessage());
	        LoggerUtil.logErro(e, log);
	    }
	}
	

	
	/* ------------------------- Get & Setts ------------------------- */

		
	public List<SituacaoPlantao> getSituacoes() {
        return Arrays.asList(SituacaoPlantao.values());
    }

	public SituacaoPlantao getSituacaoSelecionada() {
        return situacaoSelecionada;
    }

    public void setSituacaoSelecionada(SituacaoPlantao situacaoSelecionada) {
        this.situacaoSelecionada = situacaoSelecionada;
    }
    
    public Atividade getAtividadeSelecionada() {
		return atividadeSelecionada;
	}

	public void setAtividadeSelecionada(Atividade atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}
    
	public RegistroPlantao getRegistroPlantao() {
		return registroPlantao;
	}

	public void setRegistroPlantao(RegistroPlantao registroPlantao) {
		this.registroPlantao = registroPlantao;
	}
	
	public TipoPlantao[] getTiposPlantao() {
	    return TipoPlantao.values();
	}
	
	public List<Plantao> getPlantoes() {
        return plantoes;
    }

    public void setPlantoes(List<Plantao> plantoes) {
        this.plantoes = plantoes;
    }

	public List<ServidorDTO> getServidores() {
		return servidores;
	}

	public void setServidores(List<ServidorDTO> servidores) {
		this.servidores = servidores;
	}

	public List<Atividade> getAtividades() {
		return atividades;
	}

	public void setAtividades(List<Atividade> atividades) {
		this.atividades = atividades;
	}

	public Atividade getAtividade() {
		return atividade;
	}

	public void setAtividade(Atividade atividade) {
		this.atividade = atividade;
	}

	public List<RegistroPlantaoServidorDTO2> getListaGestao() {
		return listaGestao;
	}

	public void setListaGestao(List<RegistroPlantaoServidorDTO2> listaGestao) {
		this.listaGestao = listaGestao;
	}

	public List<RegistroPlantaoServidorDTO2> getListaPlantao() {
		return listaPlantao;
	}

	public void setListaPlantao(List<RegistroPlantaoServidorDTO2> listaPlantao) {
		this.listaPlantao = listaPlantao;
	}

	public List<RegistroPlantaoServidorDTO2> getListaAdministrativa() {
		return listaAdministrativa;
	}

	public void setListaAdministrativa(List<RegistroPlantaoServidorDTO2> listaAdministrativa) {
		this.listaAdministrativa = listaAdministrativa;
	}
		
	public RegistroPlantaoServidorDTO2 getServidorSelecionado() {
	    return servidorSelecionado;
	}

	public void setServidorSelecionado(RegistroPlantaoServidorDTO2 servidorSelecionado) {
	    this.servidorSelecionado = servidorSelecionado;
	}

	public RegistroPlantaoServidor getRegistroPlantaoServidor() {
		return registroPlantaoServidor;
	}

	public void setRegistroPlantaoServidor(RegistroPlantaoServidor registroPlantaoServidor) {
		this.registroPlantaoServidor = registroPlantaoServidor;
	}	
		
}
