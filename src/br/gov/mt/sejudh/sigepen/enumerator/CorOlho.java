package br.gov.mt.sejudh.sigepen.enumerator;

public enum CorOlho {	
	PRETOS("Pretos"),
	CASTANHOS("Castanhos"),
	AZUIS("Azuis"),
	VERDES("Verdes");
	
	private String descricao;
	
	private CorOlho(String descricao){
		this.descricao=descricao;
		
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
