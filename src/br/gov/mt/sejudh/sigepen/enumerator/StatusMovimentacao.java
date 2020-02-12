package br.gov.mt.sejudh.sigepen.enumerator;

public enum StatusMovimentacao {
	PENDENTE("Pendente"),
	EM_ANDAMENTO("Em Andamento"),
	FINALIZADO("Finalizado"),
	FINALIZADO_PARCIAL("Finalizado Parcial"),
	SUSPENSO("Suspenso"),
	CANCELADO("Cancelado"),
	AGUARDANDO_FINALIZAR("Aguardando Finalizar");
	
	private String descricao;
	
	private StatusMovimentacao(String descricao){
		this.descricao = descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}

}