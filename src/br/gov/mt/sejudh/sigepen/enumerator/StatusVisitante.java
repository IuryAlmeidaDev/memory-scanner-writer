package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;

public enum StatusVisitante {
	I("Inativo", 1),
	A("Ativo", 2),
	P("Pendente", 3),
    C("Credenciamento em Andamento", 4),
    M("Impedido", 5);

	private final String descricao;
	private final Integer id;

    StatusVisitante(String descricao, Integer id) {
        this.descricao = descricao;
        this.id = id;
    }

	public String getDescricao() {
		return descricao;
	}

    public Integer getId() {
        return id;
    }

    public static StatusVisitante getByInicial(final Integer id) {
        return Arrays.stream(values()).filter(value -> value.getId().equals(id)).findFirst().orElse(null);
    }
}
