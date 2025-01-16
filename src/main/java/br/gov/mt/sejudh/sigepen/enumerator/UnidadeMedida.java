package br.gov.mt.sejudh.sigepen.enumerator;

public enum UnidadeMedida {
    KILOGRAMA(0, "Quilograma", "kg"),
    GRAMA(1, "Grama", "g"),
    MILIGRAMA(2, "Miligrama", "mg"),
    LITRO(3, "Litro", "L"),
    MILILITRO(4, "Mililitro", "ml"),
    UNIDADE(5, "Unidade", "und");

    private final Integer id;
    private final String descricao;
    private final String sigla;

    UnidadeMedida(Integer id, String descricao, String sigla) {
        this.id = id;
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao + String.format(" (%s)", sigla);
    }

    public String getSigla() {
        return sigla;
    }

    public static String getSiglaPorId(int id) {
        for (UnidadeMedida medida : UnidadeMedida.values()) {
            if (medida.id ==  id) {
                return medida.sigla;
            }
        }
        throw new IllegalArgumentException("Unidade de medida inexistente");
    }
}
