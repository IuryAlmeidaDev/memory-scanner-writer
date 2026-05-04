package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;

public enum Status {
    INATIVO("Inativo", "I", 1),
    ATIVO("Ativo", "A", 2),
    PENDENTE("Pendente", "P", 3),

    private String descricao;
    private final String inicial;
    private Integer id;

    Status(String descricao, String inicial) {
        this.descricao = descricao;
        this.inicial = inicial;
    }

    Status(String descricao, String inicial, Integer id) {
        this.descricao = descricao;
        this.inicial = inicial;
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getInicial() {
        return inicial;
    }

    public static Status getByInicial(final String id) {
        return Arrays.stream(values()).filter(value -> value.getInicial().equals(id)).findFirst().orElse(null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
