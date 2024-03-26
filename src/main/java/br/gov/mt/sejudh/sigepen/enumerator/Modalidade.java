package br.gov.mt.sejudh.sigepen.enumerator;

public enum Modalidade {

	PREVENTIVA("Preventiva"),
	TEMPORARIA("Temporária");
	
	private String descricao;
	
	private Modalidade(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
