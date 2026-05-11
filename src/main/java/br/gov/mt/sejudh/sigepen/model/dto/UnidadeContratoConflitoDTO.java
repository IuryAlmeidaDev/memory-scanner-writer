package br.gov.mt.sejudh.sigepen.model.dto;

public class UnidadeContratoConflitoDTO {

    private Long idUnidade;
    private String nomeUnidade;
    private String numeroContrato;

    public UnidadeContratoConflitoDTO() {
    }

    public UnidadeContratoConflitoDTO(String nomeUnidade, String numeroContrato) {
        this.nomeUnidade = nomeUnidade;
        this.numeroContrato = numeroContrato;
    }

    public UnidadeContratoConflitoDTO(Long idUnidade, String nomeUnidade, String numeroContrato) {
        this.idUnidade = idUnidade;
        this.nomeUnidade = nomeUnidade;
        this.numeroContrato = numeroContrato;
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public String getNomeUnidade() {
        return nomeUnidade;
    }

    public void setNomeUnidade(String nomeUnidade) {
        this.nomeUnidade = nomeUnidade;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }
}
