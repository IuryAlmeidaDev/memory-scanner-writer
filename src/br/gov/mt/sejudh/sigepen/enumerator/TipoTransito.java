package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoTransito {
	ENTRADA("Entrada"),
	REMANEJAMENTO("Remanejamento"),
	INTERNA("Movimentação Interna"),
	EXTERNA("Movimentação Externa"),
	TRANSFERENCIA("Transferência");
	
	private String descricao;
	
	private TipoTransito(String descricao){
		this.descricao = descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}

}
