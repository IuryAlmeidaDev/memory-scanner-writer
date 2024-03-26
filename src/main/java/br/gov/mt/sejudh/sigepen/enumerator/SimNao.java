package br.gov.mt.sejudh.sigepen.enumerator;

public enum SimNao {
    N("Não"),
    S("Sim");

    private final String descricao;

    SimNao(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static SimNao getByBool(final Boolean bool) {
        if (bool) return SimNao.S;
        return SimNao.N;
    }
}
