package br.gov.mt.sejudh.sigepen.enumerator;

public enum Lgbt {
    T("Todos"),
    A("Mulheres Trans"),
    B("Homens Trans"),
    C("Travesti"),
    D("Bissexual"),
    E("Homossexual"),
    F("Heterossexual");

    private final String descricao;

    Lgbt(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
