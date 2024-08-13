package br.gov.mt.sejudh.sigepen.model.relatorio;

import java.util.List;

public class EstruturaRelatorio {
    private Long idEstrutura;
    private String nomeEstrutura;
    private boolean cela;
    private int quantidade;

    private List<RecuperandoRelatorio> recuperandos;

    public Long getIdEstrutura() {
        return idEstrutura;
    }

    public void setIdEstrutura(Long idEstrutura) {
        this.idEstrutura = idEstrutura;
    }

    public String getNomeEstrutura() {
        return nomeEstrutura;
    }

    public void setNomeEstrutura(String nomeEstrutura) {
        this.nomeEstrutura = nomeEstrutura;
    }

    public boolean isCela() {
        return cela;
    }

    public void setCela(boolean cela) {
        this.cela = cela;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public List<RecuperandoRelatorio> getRecuperandos() {
        return recuperandos;
    }

    public void setRecuperandos(List<RecuperandoRelatorio> recuperandos) {
        this.recuperandos = recuperandos;
    }
}
