package br.gov.mt.sejudh.sigepen.enumerator;

public enum Esfera {

	ESTADUAL("Estadual"),
	FEDERAL("Federal");
	
	private String descricao;
	
	private Esfera(String descricao){
		this.descricao=descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
