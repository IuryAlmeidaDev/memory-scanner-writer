package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;

public enum TipoRelatorioEntradaSaidaRec {

	 	ENTRADA(1, "Entrada"),
	    SAIDA(2, "Saída"),
	    ENTRADA_SAIDA(3, "Entrada/Saída");

	    private final Integer id;
	    private final String descricao;

	    TipoRelatorioEntradaSaidaRec(Integer id, String descricao){
	        this.id = id;
	        this.descricao = descricao;
	    }

	    public Integer getId() {
	        return id;
	    }

	    public String getDescricao() {
	        return descricao;
	    }

	    public static TipoRelatorioEntradaSaidaRec getValue(final Integer id) {
	        return Arrays.stream(values()).filter(value -> value.getId().equals(id)).findFirst().orElse(null);
	    }
}
