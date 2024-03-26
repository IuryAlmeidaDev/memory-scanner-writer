package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoDesligamentoRelatorio {
	TODOS("Todos"),
	ALVARA_DE_SOLTURA("Alvará de Soltura"),
	OBITO("Óbito"),
	FUGA("Fuga"),
	OUTRO_SISTEMA_PENITENCIARIO("Outro Sistema Penitenciário");
	
	private String descricao;
	
	private TipoDesligamentoRelatorio(String descricao){
		this.descricao = descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}

}
