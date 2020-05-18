package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoAtestado {
	Comportamento("Comportamento"),
	Permanencia("Permanência");
	
	private String descricao;
	
	private TipoAtestado(String descricao){
		this.descricao = descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getName() {
		return this.descricao;
	}
}
