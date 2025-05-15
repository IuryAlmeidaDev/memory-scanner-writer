package br.gov.mt.sejudh.sigepen.model.dto;

import java.sql.Date;
import java.util.List;
import br.gov.mt.sejudh.sigepen.model.Atividade;
import br.gov.mt.sejudh.sigepen.model.Servidor;

public class RegistroPlantaoServidorDTO {

	private Long id;
	
	private Servidor nomeServidor;
	
	private Atividade atividadeSelecionada;
	
	private String situacaoSelecionada;
	
	private List<ServidorDTO> equipeServidores; // Preenchido no init
			
	private Date dataRegistroPlantaoServidor;
	
	private Long registroPlantaoId;
	
	
	
	
	/* ------------------------  Getts e Setts  ------------------------ */
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	

	public Servidor getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(Servidor nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public Atividade getAtividadeSelecionada() {
		return atividadeSelecionada;
	}

	public void setAtividadeSelecionada(Atividade atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}

	public String getSituacaoSelecionada() {
		return situacaoSelecionada;
	}

	public void setSituacaoSelecionada(String situacaoSelecionada) {
		this.situacaoSelecionada = situacaoSelecionada;
	}
	


	public List<ServidorDTO> getEquipeServidores() {
		return equipeServidores;
	}

	public void setEquipeServidores(List<ServidorDTO> equipeServidores) {
		this.equipeServidores = equipeServidores;
	}
	

	public Date getDataRegistroPlantaoServidor() {
		return dataRegistroPlantaoServidor;
	}

	public void setDataRegistroPlantaoServidor(Date dataRegistroPlantaoServidor) {
		this.dataRegistroPlantaoServidor = dataRegistroPlantaoServidor;
	}

	public Long getRegistroPlantaoId() {
		return registroPlantaoId;
	}

	public void setRegistroPlantaoId(Long registroPlantaoId) {
		this.registroPlantaoId = registroPlantaoId;
	}	
	
}
