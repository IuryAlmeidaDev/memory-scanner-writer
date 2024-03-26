package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoVisitaObrigatoriedade {

	MENOR_IDADE("Menor de Idade"),
	MAIOR_IDADE("Maior de Idade"),
	EMANCIPADO("Emancipado"),
	TODOS("Todos");
	
	private String descricao;

	private TipoVisitaObrigatoriedade(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
