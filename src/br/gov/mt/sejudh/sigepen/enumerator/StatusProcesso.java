package br.gov.mt.sejudh.sigepen.enumerator;

public enum StatusProcesso {
	
	EM_ANDAMENTO("Aguardando Julgamento"),
	EM_EXECUCAO("Em Execução"),
	FINALIZADO("Finalizado");
	
	private String descricao;

	private StatusProcesso(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
