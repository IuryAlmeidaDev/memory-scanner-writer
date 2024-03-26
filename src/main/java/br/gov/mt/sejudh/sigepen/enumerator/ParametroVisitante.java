package br.gov.mt.sejudh.sigepen.enumerator;

public enum ParametroVisitante {

    RECEBIMENTO_DOCUMENTACAO(1, "Recebimentos de Documentação"),
    CHECAGEM_DOCUMENTO(2, "Checagem dos Documentos"),
    CONFERENCIA_AUTENTICACAO(3, "Conferencia e Autenticações"),
    ANALISE_CERTIDAO(4, "Analise e Autenticações de Certidões"),
    APROVACAO_CADASTRO(5, "Aprovação do Cadastro");

    private final Integer id;
    private final String nome;

    ParametroVisitante(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
