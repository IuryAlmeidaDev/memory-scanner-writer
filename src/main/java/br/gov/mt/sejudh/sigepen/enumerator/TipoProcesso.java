package br.gov.mt.sejudh.sigepen.enumerator;

public enum TipoProcesso {
	
	ACAO_PENAL("Ação Penal"),
	ALVARA_DE_SOLTURA("Alvará de Soltura"),
	GUIA_DE_EXECUCAO("Guia de Execução"),
	MANDADO_SEGURANÇA("Mandado de Prisão"),	
	NOTA_CULPA("Nota de Culpa"),
	OFICIO("Ofício"),
	TERMO_USO_TORNOZELEIRA("Termo de Uso Tornozeleira");
	
	private String descricao;

	private TipoProcesso(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
