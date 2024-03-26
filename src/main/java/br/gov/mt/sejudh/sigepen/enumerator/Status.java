package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;

public enum Status {
	INATIVO("Inativo", "I"),
	ATIVO("Ativo", "A");

	private String descricao;
	private final String inicial;

    Status(String descricao, String inicial) {
        this.descricao = descricao;
        this.inicial = inicial;
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
}
