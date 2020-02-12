package br.gov.mt.sejudh.sigepen.enumerator;

public enum Sexo {	
	M("Masculino"),
	F("Feminino");
	
	private String descricao;
	
	private Sexo(String descricao){
		this.descricao=descricao;
		
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
