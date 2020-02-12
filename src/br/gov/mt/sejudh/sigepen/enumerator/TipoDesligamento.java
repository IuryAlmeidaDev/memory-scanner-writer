package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoDesligamento {
	ALVARA_DE_SOLTURA("Alvará de Soltura"),
	OBITO("Óbito"),
	FUGA("Fuga"),
	OUTRO_SISTEMA_PENITENCIARIO("Outro Sistema Penitenciário");
	
	private String descricao;
	
	private TipoDesligamento(String descricao){
		this.descricao = descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}

}
