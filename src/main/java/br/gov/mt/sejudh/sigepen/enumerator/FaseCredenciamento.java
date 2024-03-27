package br.gov.mt.sejudh.sigepen.enumerator;

import java.util.Arrays;

public enum FaseCredenciamento {

	RECEBIMENTO(1, "Recebimento de Documentação", "CredenciamentoRecebimentoDocumentacao1"),
	CHECAGEM(2, "Checagem de Documentação", "CredenciamentoChecagemDocumentos2"),
	CONFERENCIA(3, "Conferencia e Autenticação", "CredenciamentoConferenciaAutenticacao3"),
	ANALISE(4, "Análise e Autenticação de Certidões", "CredenciamentoAnaliseCertidoes4"),
	APROVACAO(5, "Aprovação do Cadastro", "CredenciamentoAprovacaoCadastro5"),
    ;

    private final String descricao;
	private final Integer id;
	private final String permissao;

    FaseCredenciamento(Integer id, String descricao, String permissao) {
        this.descricao = descricao;
        this.id = id;
        this.permissao = permissao;
    }

	public String getDescricao() {
		return descricao;
	}

    public Integer getId() {
        return id;
    }

    public String getPermissao() {
        return permissao;
    }

    public static FaseCredenciamento getValue(final Integer id) {
        if (id == null) return null;
        return Arrays.stream(values()).filter(value -> value.getId().equals(id)).findFirst().orElse(null);
    }
}
