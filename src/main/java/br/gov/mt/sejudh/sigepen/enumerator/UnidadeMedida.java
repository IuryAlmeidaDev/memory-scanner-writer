package br.gov.mt.sejudh.sigepen.enumerator;

public enum UnidadeMedida {
    KILOGRAMA(1, "Quilograma (kg)"),
    GRAMA(2, "Grama (g)"),
    MILIGRAMA(3, "Miligrama (mg)"),
    LITRO(4, "Litro (L)"),
    MILILITRO(5, "Mililitro (ml)"),
    UNIDADE(6, "Unidade (Un)");

    private final Integer id;
    private final String descricao;

    UnidadeMedida(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}
