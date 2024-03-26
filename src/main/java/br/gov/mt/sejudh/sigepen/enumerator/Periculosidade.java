package br.gov.mt.sejudh.sigepen.enumerator;

public enum Periculosidade {	
	BAIXA("Baixa"),
	MÉDIA("Média"),
	ALTA("Alta"),
	ALTÍSSIMA("Altíssima");
	
	private String descricao;
	
	private Periculosidade(String descricao){
		this.descricao=descricao;
		
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
