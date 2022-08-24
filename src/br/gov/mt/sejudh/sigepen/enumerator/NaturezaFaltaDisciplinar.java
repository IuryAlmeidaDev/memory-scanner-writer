package br.gov.mt.sejudh.sigepen.enumerator;

public enum NaturezaFaltaDisciplinar {

	L("Leve"),
	M("Média"),
	G("Grave");

	private String descricao;

	private NaturezaFaltaDisciplinar(String descricao){
		this.descricao = descricao;
	}

	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
