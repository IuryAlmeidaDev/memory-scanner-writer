package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoDocumentoPedidoTransito {

	OFICIO("Ofício"),
	AUTORIZACAO("Autorização");
	
	private String descricao;
	
	private TipoDocumentoPedidoTransito(String descricao){
		this.descricao = descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}

}
