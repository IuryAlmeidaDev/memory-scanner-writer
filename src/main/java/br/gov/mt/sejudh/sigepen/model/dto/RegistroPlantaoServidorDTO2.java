package br.gov.mt.sejudh.sigepen.model.dto;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mt.sejudh.sigepen.enumerator.SituacaoPlantao;
import br.gov.mt.sejudh.sigepen.model.Atividade;


public class RegistroPlantaoServidorDTO2 {

	private Long id;
	
	private String nomeServidor;
	
	private String atividadeSelecionada;
	
	private List<Atividade> atividades = new ArrayList<Atividade>();
	
	private String situacaoSelecionada;
	
	private SituacaoPlantao situacaoPlantao;
	
	private List<ServidorDTO> equipeServidores; // Preenchido no init
	
	private LocalDate dataRegistroPlantaoServidor = LocalDate.now();
	
	private Long registroPlantaoId;
	
	private Long matricula;
	
	// Novos campos, inicializados como null
    private String ocorrenciaSelecionada;    
    private String justificativa;
	
	
		
	
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

	
	
	
	

}
