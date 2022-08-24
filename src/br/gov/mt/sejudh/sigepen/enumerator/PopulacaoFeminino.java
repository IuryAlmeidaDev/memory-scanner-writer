package br.gov.mt.sejudh.sigepen.enumerator;

public enum PopulacaoFeminino {
    T("Todas"),
    A("Gestante"),
    B("Lactante"),
    C("Mãe de menor de 12 anos"),
    D("Mãe de PCD");
	
    private final String descricao;

    PopulacaoFeminino(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
