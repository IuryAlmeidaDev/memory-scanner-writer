package br.gov.mt.sejudh.sigepen.model.dto;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mt.sejudh.sigepen.enumerator.SituacaoPlantao;
import br.gov.mt.sejudh.sigepen.model.Atividade;
import br.gov.mt.sejudh.sigepen.model.PlantaoServidor;


public class RegistroPlantaoServidorDTO2 {

	private Long id;
	
	private String nomeServidor;

	private ServidorDTO servidorDTO;
	
	private String atividadeSelecionada;
	
	private List<Atividade> atividades = new ArrayList<Atividade>();
	
	private String situacaoSelecionada;
	
	private SituacaoPlantao situacaoPlantao;
	
	private List<ServidorDTO> equipeServidores; // Preenchido no init
	
	private LocalDate dataRegistroPlantaoServidor = LocalDate.now();

	private PlantaoServidor plantaoServidor;
	
	private Long registroPlantaoId;
	
	private Long matricula;
	
	// Novos campos, inicializados como null
    private String ocorrenciaSelecionada;    
    private String justificativa;

	public RegistroPlantaoServidorDTO2() {
	}

	public RegistroPlantaoServidorDTO2(PlantaoServidor plantaoServidor,List<Atividade> atividadesGestao) {
		this.plantaoServidor = plantaoServidor;
		this.servidorDTO = new ServidorDTO(plantaoServidor.getServidor());
		this.atividadeSelecionada = plantaoServidor.getAtividae();
		this.situacaoSelecionada = plantaoServidor.getSituacao();
		this.atividades = atividadesGestao;
	}

	public RegistroPlantaoServidorDTO2(Long id, String nomeServidor, ServidorDTO servidorDTO, String atividadeSelecionada, List<Atividade> atividades, String situacaoSelecionada, SituacaoPlantao situacaoPlantao, List<ServidorDTO> equipeServidores, LocalDate dataRegistroPlantaoServidor, Long registroPlantaoId, Long matricula, String ocorrenciaSelecionada, String justificativa) {
		this.id = id;
		this.nomeServidor = nomeServidor;
		this.servidorDTO = servidorDTO;
		this.atividadeSelecionada = atividadeSelecionada;
		this.atividades = atividades;
		this.situacaoSelecionada = situacaoSelecionada;
		this.situacaoPlantao = situacaoPlantao;
		this.equipeServidores = equipeServidores;
		this.dataRegistroPlantaoServidor = dataRegistroPlantaoServidor;
		this.registroPlantaoId = registroPlantaoId;
		this.matricula = matricula;
		this.ocorrenciaSelecionada = ocorrenciaSelecionada;
		this.justificativa = justificativa;
	}

	/* ------------------------  Getts e Setts  ------------------------ */
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public List<Atividade> getAtividades() {
		return atividades;
	}

	public void setAtividades(List<Atividade> atividades) {
		this.atividades = atividades;
	}

	public String getSituacaoSelecionada() {
		return situacaoSelecionada;
	}

	public void setSituacaoSelecionada(String situacaoSelecionada) {
		this.situacaoSelecionada = situacaoSelecionada;
	}

	public SituacaoPlantao getSituacaoPlantao() {
		return situacaoPlantao;
	}

	public void setSituacaoPlantao(SituacaoPlantao situacaoPlantao) {
		this.situacaoPlantao = situacaoPlantao;
	}

	public List<ServidorDTO> getEquipeServidores() {
		return equipeServidores;
	}

	public void setEquipeServidores(List<ServidorDTO> equipeServidores) {
		this.equipeServidores = equipeServidores;
	}

	public LocalDate getDataRegistroPlantaoServidor() {
		return dataRegistroPlantaoServidor;
	}

	public void setDataRegistroPlantaoServidor(LocalDate dataRegistroPlantaoServidor) {
		this.dataRegistroPlantaoServidor = dataRegistroPlantaoServidor;
	}

	public Long getRegistroPlantaoId() {
		return registroPlantaoId;
	}

	public void setRegistroPlantaoId(Long registroPlantaoId) {
		this.registroPlantaoId = registroPlantaoId;
	}
	
	public String getAtividadeSelecionada() {
		return atividadeSelecionada;
	}

	public void setAtividadeSelecionada(String atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public String getOcorrenciaSelecionada() {
		return ocorrenciaSelecionada;
	}

	public void setOcorrenciaSelecionada(String ocorrenciaSelecionada) {
		this.ocorrenciaSelecionada = ocorrenciaSelecionada;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}


    public ServidorDTO getServidorDTO() {
        return servidorDTO;
    }

    public void setServidorDTO(ServidorDTO servidorDTO) {
        this.servidorDTO = servidorDTO;
    }

	public PlantaoServidor getPlantaoServidor() {
		return plantaoServidor;
	}

	public void setPlantaoServidor(PlantaoServidor plantaoServidor) {
		this.plantaoServidor = plantaoServidor;
	}
}
