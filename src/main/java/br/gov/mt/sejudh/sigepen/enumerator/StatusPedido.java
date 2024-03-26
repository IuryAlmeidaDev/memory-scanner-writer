package br.gov.mt.sejudh.sigepen.enumerator;

public enum StatusPedido {
	SOLICITADO("Solicitado"),
	AUTORIZADO("Autorizado"),	
	NAO_AUTORIZADO("Não Autorizado"),
	VINCULADO("Vinculado"),
	EM_ANDAMENTO("Em Andamento"),
	FINALIZADO("Finalizado"),
	SUSPENSO("Suspenso"),
	CANCELADO("Cancelado");
	
	private String descricao;
	
	private StatusPedido(String descricao){
		this.descricao = descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}

}
