package br.gov.mt.sejudh.sigepen.enumerator;

public enum VencimentoCredenciamento {
    VENCIDOS (0, "Vencidos"),
    VENCER_30(30, "Vencer ate 30 dias"),
    VENCER_60(60, "Vencer ate 60 dias");

    private final int dias;
    private final String descricao;

    VencimentoCredenciamento(int dias, String descricao) {
        this.dias = dias;
        this.descricao = descricao;
    }

    public int getDias() {
        return dias;
    }

    public String getDescricao() {
        return descricao;
    }
}
