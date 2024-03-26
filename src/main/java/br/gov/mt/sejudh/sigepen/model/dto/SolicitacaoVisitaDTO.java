package br.gov.mt.sejudh.sigepen.model.dto;

import br.gov.mt.sejudh.sigepen.enumerator.SgpStatusAlternativo;
import br.gov.mt.sejudh.sigepen.enumerator.SimNao;

public class SolicitacaoVisitaDTO {

    private Long id;

    private String numeroDecisaoJudicial;

    private SimNao possuiDecisaoJudicial;

    private SgpStatusAlternativo status;
    
    private String nomeSolicitante;

    private String nomeRecuperando;

    private String parentesco;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumeroDecisaoJudicial() {
		return numeroDecisaoJudicial;
	}

	public void setNumeroDecisaoJudicial(String numeroDecisaoJudicial) {
		this.numeroDecisaoJudicial = numeroDecisaoJudicial;
	}

	public SimNao getPossuiDecisaoJudicial() {
		return possuiDecisaoJudicial;
	}

	public void setPossuiDecisaoJudicial(SimNao possuiDecisaoJudicial) {
		this.possuiDecisaoJudicial = possuiDecisaoJudicial;
	}

	public SgpStatusAlternativo getStatus() {
		return status;
	}

	public void setStatus(SgpStatusAlternativo status) {
		this.status = status;
	}

	public String getNomeSolicitante() {
		return nomeSolicitante;
	}

	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}

	public String getNomeRecuperando() {
		return nomeRecuperando;
	}

	public void setNomeRecuperando(String nomeRecuperando) {
		this.nomeRecuperando = nomeRecuperando;
	}

	public String getParentesco() {
		return parentesco;
	}

	public void setParentesco(String parentesco) {
		this.parentesco = parentesco;
	}


}
