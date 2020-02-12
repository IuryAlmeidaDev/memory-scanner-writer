package br.gov.mt.sejudh.sigepen.enumerator;

public enum SituacaoUnidade {	
	INATIVO("Inativa"),
	ATIVO("Ativa"),
	INTERDITADO_TOTAL("Interditada Total"),
	INTERDITADO_PARCIAL("Interditada Parcial");
	
private String descricao;

	private SituacaoUnidade(String descricao) {
		this.descricao = descricao;

	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
