package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;

public enum TipoEntrada {
    FAMILIA(1, "Família"),
    ATENDIMENTO(2, "Atendimento"),
    ENTRADA_SAIDA(3, "Entrada/Saída");

    private final Integer id;
    private final String descricao;

    TipoEntrada(Integer id, String descricao){
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoEntrada getValue(final Integer id) {
        return Arrays.stream(values()).filter(value -> value.getId().equals(id)).findFirst().orElse(null);
    }
}
