package br.gov.mt.sejudh.sigepen.enumerator;

public enum StatusPlantao {
	
	INICIADO("Iniciado"),	
	ANDAMENTO("Em andamento"),
	CONCLUIDO("Concluido");
	
	
	private String descricao;

	private StatusPlantao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
