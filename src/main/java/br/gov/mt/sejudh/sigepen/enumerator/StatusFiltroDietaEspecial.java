package br.gov.mt.sejudh.sigepen.enumerator;

public enum StatusFiltroDietaEspecial {
	ATIVO("Ativo"),
	SUSPENSO("Suspenso"),
	ENCERRADO("Encerrado"),
	TODOS("Todos");

	private final String descricao;

	StatusFiltroDietaEspecial(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
