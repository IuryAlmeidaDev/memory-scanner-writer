package br.gov.mt.sejudh.sigepen.enumerator;

public enum TempoCondenacao {
    T("Todos"),
    A("Acima de 100 anos"),
    B("Condenação até 04 anos"),
    C("Condenação maior que 04 até 08 anos"),
    D("Condenação maior que 08 até 15 anos"),
    E("Condenação maior que 15 até 20 anos"),
    F("Condenação maior que 20 até 30 anos"),
    G("Condenação maior que 30 até 50 anos"),
    H("Condenação maior que 50 até 100 anos");

    private final String descricao;

    TempoCondenacao(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
