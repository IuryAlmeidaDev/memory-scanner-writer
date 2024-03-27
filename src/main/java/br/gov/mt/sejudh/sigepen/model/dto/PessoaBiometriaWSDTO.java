package br.gov.mt.sejudh.sigepen.model.dto;

public class PessoaBiometriaWSDTO {
	
	String nomePessoa;
	Long idPessoa;
	Long idRecuperando;
	Long idVisitante;
	
	public PessoaBiometriaWSDTO() {

	}
	
	public PessoaBiometriaWSDTO(String nomePessoa, Long idPessoa, Long idRecuperando, Long idVisitante) {
		super();
		this.nomePessoa = nomePessoa;
		this.idPessoa = idPessoa;
		this.idRecuperando = idRecuperando;
		this.idVisitante = idVisitante;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}
	
	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}
	
	public Long getIdPessoa() {
		return idPessoa;
	}
	
	public void setIdPessoa(Long idPessoa) {
		this.idPessoa = idPessoa;
	}
	
	public Long getIdRecuperando() {
		return idRecuperando;
	}
	
	public void setIdRecuperando(Long idRecuperando) {
		this.idRecuperando = idRecuperando;
	}
	
	public Long getIdVisitante() {
		return idVisitante;
	}
	
	public void setIdVisitante(Long idVisitante) {
		this.idVisitante = idVisitante;
	}
}
