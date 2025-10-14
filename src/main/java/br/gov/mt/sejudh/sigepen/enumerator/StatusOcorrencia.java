package br.gov.mt.sejudh.sigepen.enumerator;

public enum StatusOcorrencia {
		
	ANDAMENTO("Em andamento"),
	ENCERRADO("Encerrado");

	
	private String descricao;

	private StatusOcorrencia(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
