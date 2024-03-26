package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;

public enum StatusCredenciamentosVisita {
	DISPONIVEL("DISPONIVEL", "Disponível"),
	INDISPONIVEL("INDISPONIVEL", "Indisponível"),
	FALTA_DISCIPLINAR("FALTA_DISCIPLINAR", "Falta Disciplinar"),
	IMPEDIMENTO("IMPEDIMENTO", "Impedimento");

	
	private final String descricao;
	private final String id;
	
	StatusCredenciamentosVisita(String id, String descricao) {
	        this.descricao = descricao;
	        this.id = id;
	    }
	
	public String getDescricao() {
		return descricao;
	}

    public String getId() {
        return id;
    }

    public static StatusCredenciamentosVisita getValue(final Integer id) {
        return Arrays.stream(values()).filter(value -> value.getId().equals(id)).findFirst().orElse(null);
    }

}
