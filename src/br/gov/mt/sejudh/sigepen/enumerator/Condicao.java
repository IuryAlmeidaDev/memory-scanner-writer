package br.gov.mt.sejudh.sigepen.enumerator;

public enum Condicao {
	
	PROVISORIO("Provisório"),
	CONDENADO("Condenado"),
	MEDIDA_SEGURANCA("Medida de Segurança");

	private String descricao;
	
	private Condicao(String descricao){
		this.descricao = descricao;
	}

	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
