package br.gov.mt.sejudh.sigepen.enumerator;

public enum Status {	
	INATIVO("Inativo"),
	ATIVO("Ativo");
	
	private String descricao;
	
	private Status(String descricao){
		this.descricao=descricao;
		
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
