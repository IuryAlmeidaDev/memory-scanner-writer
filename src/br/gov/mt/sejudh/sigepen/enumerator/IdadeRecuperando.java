package br.gov.mt.sejudh.sigepen.enumerator;

public enum IdadeRecuperando {
    T("Todos"),
    A("Acima de 60 anos"),
    B("Entre 46 e 60 anos"),
    C("Entre 35 e 45 anos"),
    D("Entre 30 e 34 anos"),
    E("Entre 25 e 29 anos"),
    F("Entre 18 e 24 anos");
	
    private final String descricao;

    IdadeRecuperando(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
