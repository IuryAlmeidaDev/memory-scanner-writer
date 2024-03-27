package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoContato {
	CELULAR("Celular"),
	FIXO("Fixo"),
	EMAIL("Email");
	
	private String descricao;
	
	private TipoContato(String descricao){
		this.descricao = descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}

}
