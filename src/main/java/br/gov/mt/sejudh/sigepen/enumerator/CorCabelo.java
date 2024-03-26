package br.gov.mt.sejudh.sigepen.enumerator;

public enum CorCabelo {	
	PRETOS("Pretos"),
	CASTANHOS("Castanhos"),
	LOIROS("Loiros"),
	RUIVOS("Ruivos"),
	GRISALHOS("Grisalhos"),
	BRANCOS("Brancos");
	
	private String descricao;
	
	private CorCabelo(String descricao){
		this.descricao=descricao;
		
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
