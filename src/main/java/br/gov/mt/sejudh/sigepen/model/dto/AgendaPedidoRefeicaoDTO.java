package br.gov.mt.sejudh.sigepen.model.dto;

public class AgendaPedidoRefeicaoDTO {

    private Long unidadeId;
    private String nomeUnidade;
    private String contrato;
    private String refeicaoAtual;
    private String proximaRefeicao;
    private String disparo;
    private String corte;
    private String tempoRestanteCorte;
    private String status;
    private String motivo;
    private String ultimaAvaliacao;

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public String getNomeUnidade() {
        return nomeUnidade;
    }

    public void setNomeUnidade(String nomeUnidade) {
        this.nomeUnidade = nomeUnidade;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getRefeicaoAtual() {
        return refeicaoAtual;
    }

    public void setRefeicaoAtual(String refeicaoAtual) {
        this.refeicaoAtual = refeicaoAtual;
    }

    public String getProximaRefeicao() {
        return proximaRefeicao;
    }

    public void setProximaRefeicao(String proximaRefeicao) {
        this.proximaRefeicao = proximaRefeicao;
    }

    public String getDisparo() {
        return disparo;
    }

    public void setDisparo(String disparo) {
        this.disparo = disparo;
    }

    public String getCorte() {
        return corte;
    }

    public void setCorte(String corte) {
        this.corte = corte;
    }

    public String getTempoRestanteCorte() {
        return tempoRestanteCorte;
    }

    public void setTempoRestanteCorte(String tempoRestanteCorte) {
        this.tempoRestanteCorte = tempoRestanteCorte;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getUltimaAvaliacao() {
        return ultimaAvaliacao;
    }

    public void setUltimaAvaliacao(String ultimaAvaliacao) {
        this.ultimaAvaliacao = ultimaAvaliacao;
    }
}
