package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;

public enum SgpStatus {
	I("Inativo", 1),
	A("Ativo", 2);

	private String descricao;
	private Integer id;

    SgpStatus(String descricao, Integer id) {
        this.descricao = descricao;
        this.id = id;
    }

	public String getDescricao() {
		return descricao;
	}

    public Integer getId() {
        return id;
    }

    public static SgpStatus getByInicial(final Integer id) {
        return Arrays.stream(values()).filter(value -> value.getId().equals(id)).findFirst().orElse(null);
    }
}
