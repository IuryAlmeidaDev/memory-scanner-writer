package br.gov.mt.sejudh.sigepen.enumerator;

public enum PronomeTratamento {
	DOUTOR("Doutor(a)"),
	EXCELENTISSIMO_SENHOR("Excelentíssimo(a) Senhor(a)"),
	MERITISSIMO_JUIZ("Meritíssimo(a) Juiz(a)"),
	SENHOR("Senhor(a)"),
	SENHOR_DIRETOR("Senhor(a) Diretor(a)"),
	VOSSA_SENHORIA("Vossa Senhoria"),
	VOSSA_EXCELENCIA("Vossa Excelência");
	
	private String descricao;
	
	private PronomeTratamento(String descricao){
		this.descricao = descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}

}
