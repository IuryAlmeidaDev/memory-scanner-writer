package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;

public enum StatusCredenciamento {
	PENDENTE(2, "Pendente"),
	REGULAR(4, "Regular"),
	IRREGULAR(3, "Irregular"),
	APROVADO(6, "Aprovado"),
	NAO_APROVADO(5, "Não Aprovado"),
	INATIVO(1, "Inativo");

    private final String descricao;
	private final Integer id;

    StatusCredenciamento(Integer id, String descricao) {
        this.descricao = descricao;
        this.id = id;
    }

	public String getDescricao() {
		return descricao;
	}

    public Integer getId() {
        return id;
    }

    public static StatusCredenciamento getValue(final Integer id) {
        return Arrays.stream(values()).filter(value -> value.getId().equals(id)).findFirst().orElse(null);
    }
}
