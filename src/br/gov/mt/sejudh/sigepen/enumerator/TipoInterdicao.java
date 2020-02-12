package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoInterdicao {
	ADMINISTRATIVA("Administrativa"),
	JUDICIARIA("Judicial");

	private String descricao;

	private TipoInterdicao(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
}
